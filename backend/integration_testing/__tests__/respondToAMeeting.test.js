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


// Tests for Responding to Meetings
describe("PUT /meetings", () => {
    test("Update meeting status with an invalid status", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "invalid",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with an invalid mId", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": "invalid",
            "status": "declined",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with a user that is not the mentor in associated meeting", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "kusharora339@gmail.com@sample.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with an invalid user", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "invalid"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with a valid status and meeting ID", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(200)
    })
})
