// This file contains all functions that handle and process user information

const axios = require("axios");

const db = require("../database/connect")
const client = db.client;

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const handleUserEntry = async (req, res) => {
    var alreadyExists;
    try {
        alreadyExists = await storeGoogleUserData(req.body.Token, req.body.firebase_token);
    } catch (error) {
        console.log(error)
        res.status(400).send(error)
    }
    console.log("exists: " + alreadyExists);
    var jsonResp = {
        "status": alreadyExists ? "loggedIn" : "signedUp"
    }
    res.status(200).send(JSON.stringify(jsonResp));
}

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const getUserByEmail = async (req, res) => {

    var query = {"email": req.body.userEmail}
    
    client.db("UniStatDB").collection("Users").findOne(query, function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        } else {
            var jsonResp = {"userName" : result.name}
            res.status(200).send(JSON.stringify(jsonResp));
        }
    })
}

// Functions for managing user stats

/**
 * 
 * @param {*} req 
 * @param {*} res 
 */
const createUserStat = async (req, res) => {
    // Store stat data
    try {
        await client.db("UniStatDB").collection("Stats").insertOne(req.body)
        var jsonResp = {
            "status": `Stat stored for ${req.body.userEmail}`
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
}