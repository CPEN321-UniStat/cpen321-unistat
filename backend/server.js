var express = require("express");
var app = express()

const { MongoClient } = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)

const axios = require("axios");

// set up firebase authentication for notifications
var admin = require("firebase-admin");

var serviceAccount = require("./serviceAccountKey.json");

const schedule = require('node-schedule');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});


// creating zoom backend

const dotEnv = require("dotenv");
const requestPromise = require("request-promise");
const jwt = require("jsonwebtoken");
dotEnv.config()

const zoomPayload = {
    iss: process.env.API_KEY,
    exp: new Date().getTime() + 5000,
  }

const jwtToken = jwt.sign(zoomPayload, process.env.API_SECRET)

app.use(express.json());

app.post("/createZoomMeeting", async (req, res) => {
    email = "manekgujral11@gmail.com"; // your zoom developer email account
    var options = {
        method: "POST",
        uri: "https://api.zoom.us/v2/users/" + email + "/meetings",
        body: {
        topic: req.body.meetingTopic, //db
        type: 1,
        timezone: "UTC",
        start_time: req.body.meetingStartTime, //db
        end_time: req.body.meetingEndTime,
        "settings": {
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
})


app.put("/firebaseToken", async (req, res) => {
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
})

app.post("/sendMeetingRequest", async (req, res) => {

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

})

app.post("/sendMeetingResponse", async (req, res) => {

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

})


app.get("/", (req, res) => {
    res.status(200).send("Server running...");
})

app.post("/users", async (req, res) => {
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
})

app.post("/userByEmail", async (req, res) => {

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
})

//CRUD function for Stats collection
app.post("/stats", async (req, res) => {
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
})

app.get("/stats", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({}).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
})

app.post("/statsByFilter", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({ [Object.keys(req.body)[0]] : Object.values(req.body)[0] }).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
})

app.post("/statsBySorting", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({}).sort([Object.keys(req.body)[0]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats sorted applied
    }
    )
})  

app.post("/statsByConfiguration", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({[Object.keys(req.body)[0]] : Object.values(req.body)[0]}).sort([Object.keys(req.body)[1]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats sorted by filter applied
    }
    )
})  

app.put("/stats", async (req, res) => {
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
})

// app.delete("/stats", async (req, res) => {
//     // Delete stat data
//     try {
//         await client.db("UniStatDB").collection("Stats").deleteOne({userEmail : req.body.userEmail})
//         var jsonResp = {
//             "status": `Stat deleted for ${req.body.userEmail}`
//         }
//         res.status(200).send(JSON.stringify(jsonResp))
//     } catch (error) {
//         console.log(error)
//         res.status(400).send(JSON.stringify(error))
//     }
// })

// CRUD Functions for Meetings collection
app.post("/meetings", async (req, res) => {
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
})

// app.get("/meetings", async (req, res) => {
//     client.db("UniStatDB").collection("Meetings").find({}).toArray(function(err, result) {
//         if (err){
//             console.log(error)
//             res.status(400).send(JSON.stringify(error))
//         }
//         var jsonResp = {"meetings" : result}
//         res.send(JSON.stringify(jsonResp)); 
//     }
//     )
// })

app.get("/meetings/:email", async (req, res) => {
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
})

app.post("/meetingsById/", async (req, res) => {

    var query = {"mId": req.body.mId}

    client.db("UniStatDB").collection("Meetings").find(query).toArray(function(err, result) {
        if (err){
            console.log(err)
            res.status(400).send(JSON.stringify(err))
        }
        var jsonResp = {"meeting" : result}
        res.status(200).send(JSON.stringify(jsonResp))
    })
})

app.put("/meetings", async (req, res) => {
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
})

app.post("/schedulePayment", async (req, res) => {
    // Update stat data
    console.log("-------------------schedulePayment-------------------")
    try {
        console.log(req.body.mEndTime)
        var endTime = req.body.mEndTime
        var id = req.body.mId
        //           new Date(Year, M, d, h,  m, s)
        console.log(new Date())
        const date = new Date(endTime.year, endTime.month, endTime.dayOfMonth, endTime.hourOfDay, endTime.minute, endTime.second);
        const serverDate = new Date(date.getTime() + 7 * 60 *60000)
        schedule.scheduleJob(serverDate, function(){
            // console.log(`Make payment of amount ${payment} from ${mentee} to ${mentor}`);
            handlePayment(id)
        });

        var jsonResp = {
            "status": `Scheduled payment`,
            "mid": req.body.mId
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
})

app.post("/coinsByUser", async (req, res) => {
    client.db("UniStatDB").collection("Users").findOne({"email": req.body.userEmail}, function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"coins" : result.currency}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
})
async function handlePayment(id) {
    console.log("-------------------handlePayment-------------------")
    await client.db("UniStatDB").collection("Meetings").findOne({"mId": id}, function (err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var meeting = result
        var meetingLogs = meeting.meetingLogs
        // console.log(meetingLogs)
        if (shouldMentorBePaid(meeting.mentorEmail, meeting.menteeEmail, meetingLogs))
            makePayment(meeting.menteeEmail, meeting.mentorEmail, meeting.paymentAmount)
    })

}

function shouldMentorBePaid(mentor, mentee, meetingLogs) {
    console.log("-------------------shouldMentorBePaid-------------------")
    var menteeStartTime = findStartTime(mentee, meetingLogs)
    var mentorStartTime = findStartTime(mentor, meetingLogs)
    var menteeEndTime = findEndTime(mentee, meetingLogs)
    var mentorEndTime = findEndTime(mentor, meetingLogs)
    console.log(menteeStartTime)
    console.log(mentorStartTime)
    console.log(menteeEndTime)
    console.log(mentorEndTime)


    if (menteeStartTime == null) {

        /* If mentee never joined and mentor never joined, then mentor should not be paid*/
        if (mentorStartTime == null)
            return false

        /* If mentee never joined and mentor joined, then mentor should be paid*/
        else
            return true
    }
    if (menteeStartTime != null) {

        /* If mentee joined and mentor never joined, then mentor should not be paid*/
        if (mentorStartTime == null)
            return false

        /* If mentee joined and mentor joined, check futher*/
        else {

            /* If mentor never leaves, then mentor should be paid */
            if (mentorEndTime == null)
                return true

            /* If mentor leaves but mentee never leaves, then mentor should not be paid */
            else if (menteeEndTime == null)
                return false

            /* If both mentor and mentee join, and both leave, then check: */
            return mentorEndTime > menteeEndTime && mentorStartTime < menteeEndTime

        }

    }



}

function findStartTime(user, meetingLogs) {
    for (i = 0; i <meetingLogs.length; i++) {
        var meetingLog = meetingLogs[i]
        if (meetingLog.userEmail === user && meetingLog.action === 'JOINED') {
            return Date.parse(meetingLog.timestamp)
        }
    }
    return null
}

function findEndTime(user, meetingLogs) {
    for (i = 0; i <meetingLogs.length; i++) {
        var meetingLog = meetingLogs[i]
        if (meetingLog.userEmail === user && meetingLog.action === 'LEFT') {
            return Date.parse(meetingLog.timestamp)
        }
    }
    return null
}

async function makePayment(menteeEmail, mentorEmail, payment) {
    console.log("-------------------makePayment-------------------")
    await client.db("UniStatDB").collection("Users").updateOne(
        {"email": menteeEmail},
        {"$inc": {"currency": (-1 *payment)}}
    )

    await client.db("UniStatDB").collection("Users").updateOne(
        {"email": mentorEmail},
        {"$inc": {"currency": payment}}
    )
}
app.put("/updateMeetingLog", async (req, res) => {

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
})


var server = app.listen(8081, (req, res) => {
    var host = server.address().address;
    var port = server.address().port;
    console.log(`server successfully running at http://${host}:${port}`);
})

async function run() {
    try {
        await client.connect()
    } 
    catch (error) {
        console.log(error)
        await client.close()
    }
    console.log("successfully connected to database!")
}

async function storeGoogleUserData(idToken, fb_token) {
    var response = await axios.get(`https://oauth2.googleapis.com/tokeninfo?id_token=${idToken}`)
    response.data.firebase_token = fb_token
    console.log(fb_token)

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


run()
