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


// Tests for getting optimal meetingsById
describe("GET /optimalMeetings", () => {
    test("Get meetings for a valid user", async () => {
        await process.nextTick(() => { });

        const res = await request(app).get("/optimalMeetings/kusharora339@gmail.com")
                                .set("startDay", 26).set("startMonth", 6)
                                .set("endDay", 29).set("endMonth", 6)
                                .set("year", 2022).set("weekloadermonth", 6)
        expect(res.statusCode).toBe(200)
        // expect to get the meeting that was inputted above
        expect(JSON.parse(res.text).meetings.some(meeting => {
            if (meeting.mId == meetingID) {
                return true;
            }
            return false;
        }))
    })
})