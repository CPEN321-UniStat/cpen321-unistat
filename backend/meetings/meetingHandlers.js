const db = require("../database/connect")
const client = db.client;

// set up firebase authentication for notifications
var admin = require("firebase-admin");

var serviceAccount = require("../serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// creating zoom backend


const dotEnv = require("dotenv");
dotEnv.config()
const requestPromise = require("request-promise");
const jwt = require("jsonwebtoken");
const zoomPayload = {
    iss: process.env.API_KEY,
    exp: new Date().getTime() + 5000,
}
const jwtToken = jwt.sign(zoomPayload, process.env.API_SECRET)


// CRUD Functions for Meetings collection
const createMeetingRequest = async (req, res) => {
    // Post a new meeting
    try {
        await client.db("UniStatDB").collection("Meetings").insertOne(req.body)
        var jsonResp = {
            "status": `Meeting request inputted by ${req.body.menteeEmail}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
}

const getMeetingByEmail = async (req, res) => {
    var email = req.params.email;
    var month = parseInt(req.headers['month'], 10)
    var year = parseInt(req.headers['year'], 10)
    var query = {"mStartTime.month": month, "mStartTime.year": year,  "$or": [{"menteeEmail": email}, {"mentorEmail": email}] }
    
    console.log(month)
    client.db("UniStatDB").collection("Meetings").find(query).toArray(function(err, result) {
        if (err){
            console.log(err)
            res.status(400).send(JSON.stringify(err))
        }
        var jsonResp = {"meetings" : result}
        res.status(200).send(JSON.stringify(jsonResp)); 
    })
}

const getMeetingById = async (req, res) => {

    var query = {"mId": req.body.mId}

    client.db("UniStatDB").collection("Meetings").find(query).toArray(function(err, result) {
        if (err){
            console.log(err)
            res.status(400).send(JSON.stringify(err))
        }
        var jsonResp = {"meeting" : result}
        res.status(200).send(JSON.stringify(jsonResp))
    })
}

const respondToMeeting = async (req, res) => {
    // Update stat data
    try {
        console.log(req.body.mId + " " + req.body.status + " " + req.body.mColor)
        console.log(req.body)
        find_query = {"mId" : req.body.mId}
        update_query = {"$set": {
            "status": req.body.status,
            "mColor": req.body.mColor,
            "zoomId": req.body.zoomId,
            "zoomPassword": req.body.zoomPassword
        }}
        await client.db("UniStatDB").collection("Meetings").updateOne(find_query, update_query)
        var jsonResp = {
            "status": `Meeting status updated`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
}

const updateMeetingLog = async (req, res) => {

    var find_query = {"mId": req.body.mId}
    var update_query = {"$push" : {
        "meetingLogs": req.body.meetingLog
    }}

    try {
        await client.db("UniStatDB").collection("Meetings").updateOne(find_query, update_query)
        var jsonResp = {
            "status": `Meeting logs updated for meeting ID: ${req.body.mId}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
}

const createZoomMeeting = async (req, res) => {
    email = "manekgujral11@gmail.com"; // your zoom developer email account
    var options = {
        method: "POST",
        uri: "https://api.zoom.us/v2/users/" + email + "/meetings",
        body: {
        topic: req.body.meetingTopic, //db
        timezone: "America/Vancouver",
        start_time: req.body.meetingStartTime, //db
        end_time: req.body.meetingEndTime,
        type: 2,
        settings: {
            join_before_host:1,
            waiting_room:false,
            alternative_host_update_polls:true,
        },
        },
        auth: {
        bearer: jwtToken,
        },
        headers: {
        "User-Agent": "Zoom-api-Jwt-Request",
        "content-type": "application/json",
        },
        json: true, //Parse the JSON string in the response
    }

    requestPromise(options)
    .then(function (response) {
    console.log("response is: ", response)
    var jsonResp = {"status" : response}
    res.status(200).send(JSON.stringify(jsonResp))
    })
    .catch(function (err) {
    // API call failed...
    console.log("API call failed, reason ", err)
    })
}


const updateFirbaseToken = async (req, res) => {
    // Update firebase_token data
    try {
        await client.db("UniStatDB").collection("Users").updateOne({email : req.body.email}, {$set: req.body})
        var jsonResp = {
            "status": `Firebase Token updated for ${req.body.email}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
}

const sendMeetingRequest = async (req, res) => {

    //email of person you are sending request to
    try {
        const curUser = await client.db("UniStatDB").collection("Users").find({ email : req.body.email }) //mentor email
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

    admin.messaging().sendToDevice(curToken, payload, options)
    .then(function(response) {
        console.log("Successfully sent message:", response);
        var jsonResp = {"res" : "Successfully sent notification"}
        res.status(200).send(JSON.stringify(jsonResp));
    })
    .catch(function(error) {
        console.log("Error sending message:", error);
        res.status(400).send(JSON.stringify(error));
    })

}

const sendMeetingResponse = async (req, res) => {

    //email of person you are responding to
    try {
        const curUser = await client.db("UniStatDB").collection("Users").find({ email : req.body.email })
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

    admin.messaging().sendToDevice(curToken, payload, options)
    .then(function(response) {
        console.log("Successfully sent message:", response);
        var jsonResp = {"res" : "Successfully sent notification"}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    })
    .catch(function(error) {
        console.log("Error sending message:", error);
        res.status(400).send(JSON.stringify(error));
    })

}

module.exports = {
    getMeetingByEmail,
    getMeetingById,
    respondToMeeting,
    updateMeetingLog,
    createMeetingRequest,
    sendMeetingRequest,
    sendMeetingResponse,
    updateFirbaseToken,
    createZoomMeeting
}
