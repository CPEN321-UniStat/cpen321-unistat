const db = require("../database/connect")
const client = db.client;

const schedule = require('node-schedule');

const schedulePayment = async (req, res) => {
    // Update stat data
    console.log("-------------------schedulePayment-------------------")
    try {
        console.log(req.body.mEndTime)
        var endTime = req.body.mEndTime
        var id = req.body.mId
        //           new Date(Year, M, d, h,  m, s)
        console.log(new Date())
        const date = new Date(endTime.year, endTime.month, endTime.dayOfMonth, endTime.hourOfDay, endTime.minute, endTime.second);
        // const serverDate = new Date(date.getTime() + 7 * 60 *60000)
        schedule.scheduleJob(date, function(){
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
}

const getCoinsByUser = async (req, res) => {
    client.db("UniStatDB").collection("Users").findOne({"email": req.body.userEmail}, function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"coins" : result.currency}
        res.status(200).send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
}

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

module.exports = {
    schedulePayment,
    getCoinsByUser
}