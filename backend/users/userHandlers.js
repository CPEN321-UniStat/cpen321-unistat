// This file contains all functions that handle and process user information

const axios = require("axios");
const e = require("express");

// set up firebase authentication for notifications
var admin = require("firebase-admin");

var serviceAccount = require("../serviceAccountKey.json");

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
const handleUserEntry = async (req, res) => {

    if (req.body.Token == undefined || req.body.firebase_token == undefined) {
        var jsonResp = {
            "status": "Cannot create user with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    }
    else{  
        try {
            var alreadyExists = await storeGoogleUserData(req.body.Token, req.body.firebase_token);
            console.log("exists: " + alreadyExists);
            var jsonResp = {
                "status": alreadyExists ? "loggedIn" : "signedUp"
            }
            res.status(200).send(JSON.stringify(jsonResp));
        } catch (error) {
            console.log(error)
            res.status(400).send(JSON.stringify(error));
        }
    }
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getUserByEmail = async (req, res) => {

    if (req.body.userEmail == undefined) {
        var jsonResp = {
            "status": "Cannot get user without valid email"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    }
    else{
        var query = {"email": req.body.userEmail}
        client.db("UniStatDB").collection("Users").findOne(query, function(err, result) {
            if (err){
                console.log(error)
                res.status(400).send(JSON.stringify(error))
            } else {
                if (result == undefined || result.name == undefined) {
                    var jsonResp = {
                        "status": "Cannot get user without valid email"
                    }
                    res.status(400).send(JSON.stringify(jsonResp))
                }
                else{
                    var jsonResp = {"userName" : result.name}
                    res.status(200).send(JSON.stringify(jsonResp));
                }
            }
        })
    }
}

// Functions for managing user stats

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const createUserStat = async (req, res) => {
    
    if (req.body.userEmail == undefined || req.body.userPhoto == undefined || req.body.userName == undefined || req.body.univName == undefined || req.body.univMajor == undefined || req.body.univGpa == undefined || req.body.univEntranceScore == undefined || req.body.univBio == undefined) {
        var jsonResp = {
            "status": "Cannot create user stat with undefined body"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    }
    else{
        try {
            var existingUsers = client.db("UniStatDB").collection("Stats").find({userEmail: req.body.userEmail}, {$exists: true})
            var lenUsers = (await existingUsers.toArray()).length
            if (lenUsers > 0) {
                var jsonResp = {
                    "status": "Stat already exists"
                }
                res.status(400).send(JSON.stringify(jsonResp))
            }
            else{
                await client.db("UniStatDB").collection("Stats").insertOne(req.body)
                var jsonResp = {
                    "status": `Stat stored for ${req.body.userEmail}`
                }
                res.status(200).send(JSON.stringify(jsonResp))
            }
        } catch (error) {
            console.log(error)
            res.status(400).send(JSON.stringify(error))
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
    client.db("UniStatDB").collection("Stats").find({ [Object.keys(req.body)[0]] : Object.values(req.body)[0] }).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getStatsBySorting = async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({}).sort([Object.keys(req.body)[0]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats sorted applied
    }
    )
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getStatsByConfiguration = async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({[Object.keys(req.body)[0]] : Object.values(req.body)[0]}).sort([Object.keys(req.body)[1]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats sorted by filter applied
    }
    )
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const updateStat = async (req, res) => {
    // Update stat data
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

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const deleteStat = async (req, res) => {
    // Delete stat data
    try {
        await client.db("UniStatDB").collection("Stats").deleteOne({userEmail : req.body.userEmail})
        var jsonResp = {
            "status": `Stat deleted for ${req.body.userEmail}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
}

/**
 * 
 * @param {*} idToken 
 * @param {*} fb_token 
 * @returns 
 */
async function storeGoogleUserData(idToken, fb_token) {
    var response = await axios.get(`https://oauth2.googleapis.com/tokeninfo?id_token=${idToken}`)
    response.data.firebase_token = fb_token

    var existingUsers = client.db("UniStatDB").collection("Users").find({email: response.data.email}, {$exists: true})
    var lenUsers = (await existingUsers.toArray()).length

    if (lenUsers > 0) { // User already exists
        console.log("already exists")
        await client.db("UniStatDB").collection("Users").updateOne({email : response.data.email}, {$set: {"firebase_token": fb_token}})
    } else { // New user, so insert
        console.log("new user, signing up...")
        response.data.currency = 100
        await client.db("UniStatDB").collection("Users").insertOne(response.data)
    }

    console.log("num existing users: ", lenUsers)
    return lenUsers
}

const sendMeetingRequest = async (userEmail) => {

    //email of person you are sending request to
    try {
        const curUser = await client.db("UniStatDB").collection("Users").find({ email : userEmail }) //mentor email
        var curToken = (await curUser.toArray())[0].firebase_token
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
        admin.messaging().sendToDevice(curToken, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
        })
        .catch(function(error) {
            console.log("Error sending message:", error);
        })
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
        admin.messaging().sendToDevice(curToken, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
        })
        .catch(function(error) {
            console.log("Error sending message:", error);
        })
    }
}

module.exports = {
    handleUserEntry,
    storeGoogleUserData,
    getUserByEmail,
    createUserStat,
    getAllUserStats,
    getStatsByFilter,
    getStatsBySorting,
    getStatsByConfiguration,
    updateStat,
    deleteStat,
    sendMeetingRequest,
    sendMeetingResponse
}