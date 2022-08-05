const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client

const meetingID = 12345;

const sampleIntegrationTestMeeting = {
    "meetingLogs": [],
    "menteeEmail": "manekgujral11@gmail.com",
    "mentorEmail": "kusharora339@gmail.com",
    "mentorName": "Mentor User",
    "paymentAmount": 5,
    "status": "PENDING",
    "mColor": 0,
    "mEndTime": {
      "year": 2022,
      "month": 6,
      "dayOfMonth": 27,
      "hourOfDay": 12,
      "minute": 27,
      "second": 56
    },
    "mId": meetingID,
    "mName": "Integration Meeting Test",
    "mStartTime": {
      "year": 2022,
      "month": 6,
      "dayOfMonth": 27,
      "hourOfDay": 11,
      "minute": 27,
      "second": 56
    }
}

beforeAll(() => {
    console.log("DROPPING")
    var query1 = {mId : meetingID}
    client.db("UniStatDB").collection("Meetings").deleteOne(query1);
    client.db("UniStatDB").collection("Meetings").insertOne(sampleIntegrationTestMeeting);
})

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

// Tests for updating meeting logs
describe("PUT /updateMeetingLog", () => {
    test("Update meeting logs with valid mId", async () => {
        await process.nextTick(() => { });
        var res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:00:00",
                "userEmail": "kusharora339@gmail.com",
                "isMentor": true,
                "action": "JOINED"
            }
        })
        expect(res.statusCode).toBe(200)

        res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:01:00",
                "userEmail": "manekgujral11@gmail.com",
                "isMentor": false,
                "action": "JOINED"
            }
        })
        expect(res.statusCode).toBe(200)

        res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:55:00",
                "userEmail": "manekgujral11@gmail.com",
                "isMentor": false,
                "action": "LEFT"
            }
        })
        expect(res.statusCode).toBe(200)

        res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:58:00",
                "userEmail": "kusharora339@gmail.com",
                "isMentor": true,
                "action": "LEFT"
            }
        })
        expect(res.statusCode).toBe(200)
    })

    test("Update meeting logs with invalid mId", async () => {
        const res = await request(app).put("/updateMeetingLog").send({
            "mId": "invalid",
            "meetingLog": {
                "timestamp": "2022-07-09T11:00:00",
                "userEmail": "manekgujral11@gmail.com",
                "isMentor": true,
                "action": "JOINED"
            }
        })
        expect(res.statusCode).toBe(400)
    })
})