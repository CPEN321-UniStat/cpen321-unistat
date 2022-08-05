// This file contains all functions that handle and process user information

// set up firebase authentication for notifications
var admin = require("firebase-admin");

var serviceAccount = require("../serviceAccountKey.json");

const payment = require("../payments/paymentHandlers.js");

const verify = require("./userVerification");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = require("../database/connect")
const client = db.client;

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
 const removeUserfromDB = async (req, res) => {
    if (req.body.userEmail == undefined || req.body.userEmail == "" || req.body == undefined) {
        var jsonRespError = {
            "status": "Cannot remove user with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonRespError))
    } else {
        await client.db("UniStatDB").collection("Stats").deleteOne({userEmail : req.body.userEmail})
        await client.db("UniStatDB").collection("Users").deleteOne({email : req.body.userEmail})
        await client.db("UniStatDB").collection("Meetings").deleteOne({menteeEmail : req.body.userEmail})
        await client.db("UniStatDB").collection("Meetings").deleteOne({mentorEmail : req.body.userEmail})
        var jsonResp = {
            "status": `User removed : ${req.body.userEmail}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    }

}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const handleUserEntry = async (req, res) => {

    if (req.body.Token == undefined || req.body.firebase_token == undefined) {
        const jsonResp = {
            "status": "Cannot create user with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    } else {
        var alreadyExists = await storeGoogleUserData(req.body.Token, req.body.firebase_token);
        console.log("exists: " + alreadyExists);
        const jsonResp = {
            "status": alreadyExists ? "loggedIn" : "signedUp"
        }
        res.status(200).send(JSON.stringify(jsonResp));
    }
}

// Functions for managing user stats

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const createUserStat = async (req, res) => {
    
    if (req.body.userEmail == undefined 
        || req.body.userPhoto == undefined 
        || req.body.userName == undefined 
        || req.body.univName == undefined 
        || req.body.univMajor == undefined 
        || req.body.univGpa == undefined 
        || req.body.univEntranceScore == undefined 
        || req.body.univBio == undefined) {
        const jsonResp = {
            "status": "Cannot create user stat with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    } else{
        var existingUsers = client.db("UniStatDB").collection("Stats").find({userEmail: req.body.userEmail}, {$exists: true})
        var lenUsers = (await existingUsers.toArray()).length
        if (lenUsers == 0) {
            await client.db("UniStatDB").collection("Stats").insertOne(req.body)
            var jsonResp = {
                "status": `Stat stored for ${req.body.userEmail}`
            }
            res.status(200).send(JSON.stringify(jsonResp))
        }
        else {
            var jsonResp2 = {
                "status": "Stat already exists"
            }
            res.status(400).send(JSON.stringify(jsonResp2))
        }
    }
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getAllUserStats = async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({}).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getStatsByFilter = async (req, res) => {
    if ( req.body == undefined || Object.values(req.body)[0] == undefined) {
        var jsonResp = {
            "status": "Invalid request: Cannot filter user stat with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    } else if (Object.keys(req.body).length > 1) {
        var jsonResp2 = {
            "status": "Invalid request: Cannot filter more than one string"
        }
        res.status(400).send(JSON.stringify(jsonResp2))
    } else if (!(Object.keys(req.body)[0] == "univName" || Object.keys(req.body)[0] == "univMajor" || Object.keys(req.body)[0] == "userEmail")) {
        var jsonResp3 = {
            "status": "Invalid request: Please make sure the filter criteria is either univName or univMajor"
        }
        res.status(400).send(JSON.stringify(jsonResp3))
    } else {
        client.db("UniStatDB").collection("Stats").find({ [Object.keys(req.body)[0]] : Object.values(req.body)[0] }).toArray(function(err, result) {
            if (err){
                console.log(error)
                res.status(400).send(JSON.stringify(error))
            }
            
            if(result[0] != undefined && Object.keys(req.body)[0] == "userEmail"){
                // result[0].isMentor = true
                const currency = payment.getUserCoins(Object.values(req.body)[0])
                currency.then(function(r){
                    result[0].coins = r
                    result[0].isMentor = true
                    var jsonResp = {
                        "statData": result,
                    }
                    res.status(200).send(JSON.stringify(jsonResp));
                }
                ).catch(function(err){
                    console.log(err)
                    res.status(400).send(JSON.stringify(err))
                })
            }
            else if(Object.keys(req.body)[0] == "userEmail"){
                const currency = payment.getUserCoins(Object.values(req.body)[0])
                currency.then(function(r){
                    var jsonResp = {
                        "statData": [{ "coins": r, "isMentor": false }]
                    }
                    res.status(200).send(JSON.stringify(jsonResp));
                }
                ).catch(function(err){
                    console.log(err)
                    res.status(400).send(JSON.stringify(err))
                })
            }
            else{
                var jsonResp = {
                    "statData": result,
                }
                res.status(200).send(JSON.stringify(jsonResp));
            }
        })
    }
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getStatsBySorting = async (req, res) => {
    if ( req.body == undefined || Object.keys(req.body)[0] == undefined) {
        var jsonResp = {
            "status": "Invalid request: Cannot sort user stat with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    } else if (Object.keys(req.body).length > 1) {
        var jsonResp2 = {
            "status": "Invalid request: Cannot sort by more than one criteria"
        }
        res.status(400).send(JSON.stringify(jsonResp2))
    } else if (!(Object.keys(req.body)[0] == "univGpa" || Object.keys(req.body)[0] == "univEntranceScore")) {
        var jsonResp3 = {
            "status": "Invalid request: Please make sure the sort criteria is either univGpa or univEntranceScore"
        }
        res.status(400).send(JSON.stringify(jsonResp3))
    } else {
        client.db("UniStatDB").collection("Stats").find({}).sort([Object.keys(req.body)[0]]).toArray(function(err, result) {
            if (err){
                console.log(error)
                res.status(400).send(JSON.stringify(error))
            }
            var jsonResp4 = {"statData" : result.reverse()}
            res.status(200).send(JSON.stringify(jsonResp4)); // send back all stats sorted applied
        })
    }
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getStatsByConfiguration = async (req, res) => {
    if (req.body == undefined || Object.values(req.body)[0] == undefined ||  Object.values(req.body)[1] == undefined) {
        var jsonResp = {
            "status": "Invalid request: Cannot sort/filter user stats with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    } else if (Object.keys(req.body).length != 2) {
        var jsonResp2 = {
            "status": "Invalid request: Cannot sort/filter by more or less than one criteria for each"
        }
        res.status(400).send(JSON.stringify(jsonResp2))
    } else if (!(Object.keys(req.body)[1] == "univGpa" || Object.keys(req.body)[1] == "univEntranceScore") 
                    && !(Object.keys(req.body)[0] == "univName" || Object.keys(req.body)[0] == "univMajor")) {
        var jsonResp3 = {
            "status": "Invalid request: Please make sure that the sort and filter configurations are correct with sort placed before the filter configuration"
        }
        res.status(400).send(JSON.stringify(jsonResp3))
    } else {
        client.db("UniStatDB").collection("Stats").find({[Object.keys(req.body)[0]] : Object.values(req.body)[0]}).sort([Object.keys(req.body)[1]]).toArray(function(err, result) {
            if (err){
                console.log(error)
                res.status(400).send(JSON.stringify(error))
            }
            var jsonResp4 = {"statData" : result.reverse()}
            res.status(200).send(JSON.stringify(jsonResp4)); // send back all stats sorted by filter applied
        })
    }
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const updateStat = async (req, res) => {
    // Update stat data
    if (req.body.userEmail == undefined  
    || req.body.univName == undefined 
    || req.body.univMajor == undefined 
    || req.body.univGpa == undefined 
    || req.body.univEntranceScore == undefined 
    || req.body.univBio == undefined) {
        var jsonResp = {
            "status": "Cannot update user stat with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    } else {
        try {
            await client.db("UniStatDB").collection("Stats").updateOne({userEmail : req.body.userEmail}, {$set: req.body})
            var jsonResp = {
                "status": `Stat updated for ${req.body.userEmail}`
            }
            res.status(200).send(JSON.stringify(jsonResp))
        } catch (error) {
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
    }
}


/**
 * 
 * @param {*} idToken 
 * @param {*} fb_token 
 * @returns 
 */
 async function storeGoogleUserData(idToken, fb_token) {

    var response = null;
    try {
        response = await verify.userVerifier(idToken);
    } catch (error) {
        console.log(error)
        throw error
    }

    console.log(response)
    // var response = await axios.get(`https://oauth2.googleapis.com/tokeninfo?id_token=${idToken}`)
    response.firebase_token = fb_token

    var existingUsers = client.db("UniStatDB").collection("Users").find({email: response.email}, {$exists: true})
    var lenUsers = (await existingUsers.toArray()).length

    if (lenUsers > 0) { // User already exists
        console.log("already exists")
        await client.db("UniStatDB").collection("Users").updateOne({email : response.email}, {$set: {"firebase_token": fb_token}})
    } else { // New user, so insert
        console.log("new user, signing up...")
        response.currency = 100
        client.db("UniStatDB").collection("Users").insertOne(response)
    }
    
    console.log("num existing users: ", lenUsers)
    return lenUsers
}

const sendMeetingRequest = async (userEmail) => {
    //email of person you are sending request to
    try {
        const curUser = client.db("UniStatDB").collection("Users").find({ email: userEmail }) //mentor email
 //mentor email
        var curToken = (await curUser.toArray())[0].firebase_token
        console.log("CURRTOKEN--------------------")
    } catch (error) {
        console.log(error)
    }
    var payload = {
        notification: {
            title: "UniStat",
            body: "Someone requested a meeting with you!",
        },
    }
    
    var options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    }
    if (curToken != "" && curToken != undefined) {
        try {
            admin.messaging().sendToDevice(curToken, payload, options)
            return "Successfully sent notification"
        } catch (error) {
            return error
        }
    
    }
}
const sendMeetingResponse = async (userEmail) => {
    //email of person you are responding to
    try {
        const curUser = await client.db("UniStatDB").collection("Users").find({ email : userEmail })
        var curToken = (await curUser.toArray())[0].firebase_token
    } catch (error) {
        console.log(error)
    }
    var payload = {
        notification: {
            title: "UniStat",
            body: "Someone responded to your meeting request!",
        },
    }
    
    var options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    }
    if (curToken != "" && curToken != undefined) {
        try {
            admin.messaging().sendToDevice(curToken, payload, options)
            return "Successfully sent notification"
        } catch (error) {
            return error
        }
    
    }
}

module.exports = {
    removeUserfromDB,
    handleUserEntry,
    storeGoogleUserData,
    createUserStat,
    getAllUserStats,
    getStatsByFilter,
    getStatsBySorting,
    getStatsByConfiguration,
    updateStat,
    sendMeetingRequest,
    sendMeetingResponse
}