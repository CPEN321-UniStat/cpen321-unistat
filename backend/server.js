var express = require("express");
var app = express()

const db = require("./database/connect")

const users = require("./users/userHandlers")

const payment = require("./payments/paymentHandlers")

const meeting = require("./meetings/meetingHandlers")

app.use(express.json());

app.get("/", (req, res) => {
    res.status(200).send("Server running...");
})


// User Enpoints

app.post("/users", users.handleUserEntry);

app.post("/userByEmail", users.getUserByEmail);

app.post("/stats", users.createUserStat);

app.get("/stats", users.getAllUserStats);

app.post("/statsByFilter", users.getStatsByFilter);

app.post("/statsBySorting", users.getStatsBySorting);  

app.post("/statsByConfiguration", users.getStatsByConfiguration);  

app.put("/stats", users.updateStat);

app.delete("/stats", users.deleteStat);


// Payment Enpoints

app.post("/schedulePayment", payment.schedulePayment)

app.post("/coinsByUser", payment.getCoinsByUser)


// Meeting Endpoints

app.post("/meetings", meeting.createMeetingRequest)

app.get("/meetings/:email", meeting.getMeetingByEmail)

app.post("/meetingsById/", meeting.getMeetingById)

app.put("/meetings", meeting.respondToMeeting)

app.put("/updateMeetingLog", meeting.updateMeetingLog)

app.post("/createZoomMeeting", meeting.createZoomMeeting)

app.put("/firebaseToken", meeting.updateFirbaseToken)

app.post("/sendMeetingRequest", meeting.sendMeetingRequest)

app.post("/sendMeetingResponse", meeting.sendMeetingResponse)

var server = app.listen(8081, (req, res) => {
    var host = server.address().address;
    var port = server.address().port;
    console.log(`server successfully running at http://${host}:${port}`);
})

db.connect().catch(err => {
    console.error(err)
    db.client.close()
})

module.exports = app;