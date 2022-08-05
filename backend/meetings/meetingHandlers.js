const db = require("../database/connect")
const client = db.client;

const users = require("../users/userHandlers.js");
const payment = require("../payments/paymentHandlers");

// creating zoom backend


const dotEnv = require("dotenv");
dotEnv.config()
const requestPromise = require("request-promise");
const jwt = require("jsonwebtoken");

const zoomPayload = {
    iss: process.env.ZOOM_APP_API_KEY, // CHANGE TO ZOOM_APP_API_KEY BEFORE FINAL SUBMISSION
    exp: new Date().getTime() + 5000,
}
var jwtToken = jwt.sign(zoomPayload, process.env.ZOOM_APP_SECRET) // CHANGE TO ZOOM_APP_SECRET BEFORE FINAL SUBMISSION
var zoomEmail = "cpen321.unistat@gmail.com";

// CRUD Functions for Meetings collection
const createMeetingRequest = async (req, res) => {
    // Post a new meeting
    try {
        const isMenteeValid = await isValidUser(req.body.menteeEmail);
        const isMentorValid = await isValidUser(req.body.mentorEmail);
        const isMentorMentor = await isMentor(req.body.mentorEmail)
        const validPayment = (req.body.paymentAmount && !isNaN(req.body.paymentAmount))
        const validTimes = areValidTimes(req.body.mStartTime, req.body.mEndTime)
        const isMeetingIdValid = await isValidMid(req.body.mId)
        if ( isMenteeValid && isMentorValid && isMentorMentor && validPayment && validTimes && isMeetingIdValid) {
            await client.db("UniStatDB").collection("Meetings").insertOne(req.body)
            const jsonResp = {
                "status": `Meeting request inputted by ${req.body.menteeEmail}`
            }

            await users.sendMeetingRequest(req.body.mentorEmail)
            res.status(200).send(JSON.stringify(jsonResp))
        } else {
            const jsonResp = {
                "status": "Invalid user error"
            }
            res.status(400).send(JSON.stringify(jsonResp))
        }
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
}

const getMeetingByEmail = async (req, res) => {
    const isEmailValid = await isValidUser(req.params.email)
    if (isEmailValid) {
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
    } else {
        var jsonResp = {
            "status": "Invalid user error"
        }
        res.status(400).send(JSON.stringify(jsonResp))
    }
}

const optimalMeetings = async (req, res) => {
    var email = req.params.email;

    var startDay = parseInt(req.headers['startday'], 10)
    var startMonth = parseInt(req.headers['startmonth'], 10)
    var endDay = parseInt(req.headers['endday'], 10)
    var endMonth = parseInt(req.headers['endmonth'], 10)
    var weekLoaderMonth = parseInt(req.headers['weekloadermonth'], 10)
    var year = parseInt(req.headers['year'], 10)

    console.log("weekLoaderMonth: ", weekLoaderMonth)

    var findQuery = {
        "$and": [
          {"status": "PENDING"},
          {"$or": [{"menteeEmail": email}, {"mentorEmail": email}]},
          {"$or": [{ "mStartTime.month": {"$ne": startMonth}}, {"mStartTime.dayOfMonth": {"$gte": startDay}}]},
          {"$or": [{ "mStartTime.month": startMonth}, {"mStartTime.month": {"$gt": startMonth}}]},
          {"$or": [{ "mEndTime.month": {"$ne": endMonth}}, {"mEndTime.dayOfMonth": {"$lte": endDay}}]},
          {"$or": [{ "mEndTime.month": endMonth}, {"mEndTime.month": {"$lt": endMonth}}]},
          {"mStartTime.year": year}
        ]
      }
    
    var sortQuery = {
        "mEndTime.year": 1,
        "mEndTime.month": 1,
        "mEndTime.dayOfMonth": 1,
        "mEndTime.hourOfDay": 1,
        "mEndTime.minute": 1,
        "mEndTime.second": 1
    }

    
    client.db("UniStatDB").collection("Meetings").find(findQuery).sort(sortQuery).toArray(function(err, result) {
        if (err){
            console.log(err)
            res.status(400).send(JSON.stringify(err))
        }
        const P = []
        for (let i = 0; i < result.length; i++) {
            P[i+1] = getLargestIndexCompatibleInterval(i, result)
        }
        const v = []
        for (let i = 0; i < result.length; i++) {
            var payment = result[i].paymentAmount
            v[i+1] = payment
        }
        const M = []
        M[0] = 0
        for (let i = 1; i <= result.length; i++) {
            M[i] = Math.max(v[i] + M[P[i]], M[i-1])
        }

        var optimalIndices = findOptimalMeetings(result.length, M, P, v)
        var optimalMeetings = optimalIndices.map(x => result[x-1])
        var optimalMeetingsForMonth = optimalMeetings.filter(meeting => meeting.mStartTime.month === weekLoaderMonth)

        var jsonResp = {"meetings" : optimalMeetingsForMonth}
        console.log("meeting length:", optimalMeetingsForMonth.length)
        res.status(200).send(JSON.stringify(jsonResp)); 
    }) 
}


function getLargestIndexCompatibleInterval (j, meetings) {
    // console.log("---------------")
    var p = 0
    var meetingStartTime = meetings[j].mStartTime
    const givenStart = new Date(meetingStartTime.year, meetingStartTime.month, meetingStartTime.dayOfMonth, meetingStartTime.hourOfDay, meetingStartTime.minute, meetingStartTime.second)
    // console.log(givenStart)
    // console.log("end-times:")
    for (let i=0; i < meetings.length; i++) {
        var meetingEndTime = meetings[i].mEndTime
        const meetingEnd = new Date(meetingEndTime.year, meetingEndTime.month, meetingEndTime.dayOfMonth, meetingEndTime.hourOfDay, meetingEndTime.minute, meetingEndTime.second)
        // console.log(meetingEnd)
        if (meetingEnd <= givenStart) {
            p = i+1
        }
    }
    // console.log(p)
    return p
}

function findOptimalMeetings(j, M, P, v) {

    if ( j === 0)
        return []
    if (v[j] + M[P[j]] > M[j-1]) {
        // console.log(j)
        const arr = []
        arr[0] = j
        return arr.concat(findOptimalMeetings(P[j], M, P, v))
    }
    else {
        return findOptimalMeetings(j-1, M, P, v)
    }

}

const getMeetingById = async (req, res) => {
    var query = {"mId": req.body.mId}

    client.db("UniStatDB").collection("Meetings").find(query).toArray(function(err, result) {
        if (err){
            console.log(err)
            res.status(400).send(JSON.stringify(err))
        }
        if (result.length > 0) {
            const jsonResp = {"meeting" : result}
            res.status(200).send(JSON.stringify(jsonResp))
        } else {
            const jsonResp = {
                "status": `Invalid mId error`
            }
            res.status(400).send(JSON.stringify(jsonResp))
        }
    })
}

const respondToMeeting = async (req, res) => {
    // Update stat data
    try {
        console.log(req.body.mId + " " + req.body.status + " " + req.body.mColor)
        console.log(req.body)
        console.log("status", req.body.status)
        find_query = {"mId" : req.body.mId}
        update_query = {"$set": {
            "status": req.body.status,
            "mColor": req.body.mColor,
            "zoomId": req.body.zoomId,
            "zoomPassword": req.body.zoomPassword
        }}

        if (req.body.status != "ACCEPTED" && req.body.status != "DECLINED" &&req.body.status != "PENDING") {
            const jsonResp = {
                "status": `Invalid status error`
            }
            res.status(400).send(JSON.stringify(jsonResp))
            return
        }
        
        var meeting = client.db("UniStatDB").collection("Meetings").find(find_query, {$exists: true})
        var meetingArray = await meeting.toArray()
        var menteeEmail = meetingArray[0].menteeEmail;
        var mentorEmail = meetingArray[0].mentorEmail;

        if(req.body.email != mentorEmail) {
            const jsonResp = {
                "status": `Invalid user error`
            }
            res.status(400).send(JSON.stringify(jsonResp))
        } else {
            await client.db("UniStatDB").collection("Meetings").updateOne(find_query, update_query)
            await users.sendMeetingResponse(menteeEmail)
            const jsonResp = {
                "status": `Meeting status updated`
            }
            res.status(200).send(JSON.stringify(jsonResp))
        }
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
        var ret = await client.db("UniStatDB").collection("Meetings").updateOne(find_query, update_query)
        if (ret.matchedCount === 0) {
            const jsonResp = {
                "status": `Meeting with provided meeting ID "${req.body.mId}" does not exist`
            }
            res.status(400).send(JSON.stringify(jsonResp))
            return
        }
        const jsonResp = {
            "status": `Meeting logs updated for meeting ID: ${req.body.mId}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
}

const createZoomMeeting = async (req, res) => {
    if (req.body.mId == null || (req.body.meetingStartTime >= req.body.meetingEndTime) || req.body.meetingTopic == null) {
        console.log("Invalid input error")
        var jsonResp = {"status" : "Invalid inputs error"}
        res.status(400).send(JSON.stringify(jsonResp))
        return
    }
 
    console.log("EMAIL----------------", zoomEmail);

    //email = "cpen321.unistat@gmail.com"; // your zoom developer email account
    var options = {
        method: "POST",
        uri: "https://api.zoom.us/v2/users/" + zoomEmail + "/meetings",
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

    // Schedule Payment 
    try {
        await payment.schedulePayment(req.body.meetingEndTime, req.body.mId);
    } catch (error) {
        console.log("Payment failed. Error:", error)
        const jsonResp = {"status" : "Schedule payment failed"}
        res.status(400).send(JSON.stringify(jsonResp))
        return
    }

    await requestPromise(options)
    .then(function (response) {
        console.log("response is: ", response)
        const jsonResp = {"status" : response}
        res.status(200).send(JSON.stringify(jsonResp))
    })
    .catch(function (err) {
        // API call failed...
        console.log("API call failed, reason ", err)
        const jsonResp = {"status" : `Create Zoom meeting failed ${err}`}
        res.status(400).send(JSON.stringify(jsonResp))
    })
}


const updateFirbaseToken = async (req, res) => {
    // Update firebase_token data
    const validUser = await isValidUser(req.body.email)
    
    if (!validUser) {
        var jsonResp = {
            "status": `Invalid user error: ${req.body.email}`
        }
        console.log("INVALIDUSER")
        res.status(400).send(JSON.stringify(jsonResp))
        return
    }
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

const isValidUser = async (email) => {
    var query = {email}
    var existingUsers = client.db("UniStatDB").collection("Users").find(query, {$exists: true})
    var lenUsers = (await existingUsers.toArray()).length
    return (lenUsers > 0) ? 1 : 0;
}

const isMentor = async (email) => {
    var query = {"userEmail": email}
    var existingUsers = client.db("UniStatDB").collection("Stats").find(query, {$exists: true})
    var lenUsers = (await existingUsers.toArray()).length
    return (lenUsers > 0) ? 1 : 0;
}

// returns true if time1 <= time2 ad false otherwise
const areValidTimes = (time1, time2) => {
    const start = new Date(time1.year, time1.month, time1.dayOfMonth, time1.hourOfDay, time1.minute, time1.second)
    const end = new Date(time2.year, time2.month, time2.dayOfMonth, time2.hourOfDay, time2.minute, time2.second)
    return start <= end
}

const isValidMid = async (mId) => {
    var query = {mId}
    try {
        const existingMeeting = client.db("UniStatDB").collection("Meetings").find(query, {$exists: true})
        const lenMeeting = (await existingMeeting.toArray()).length
        return (lenMeeting > 0) ? 0 : 1;
    } catch (err) {
        return 1
    }
}

module.exports = {
    getMeetingByEmail,
    getMeetingById,
    optimalMeetings,
    respondToMeeting,
    updateMeetingLog,
    createMeetingRequest,
    updateFirbaseToken,
    createZoomMeeting,
    changeTesting: () => {
        zoomEmail = "manekgujral11@gmail.com"
        zoomPayload.iss = process.env.API_KEY
        secretKey = process.env.API_SECRET
        jwtToken = jwt.sign(zoomPayload, secretKey)
    }
}
