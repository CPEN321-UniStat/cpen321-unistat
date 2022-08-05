const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

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


// Tests for creating meeting requests
describe("POST /meetings", () => {
    test("From an existing user to a non-existing user", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mentorEmail = "invalid@gmail.com";
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From a non-existing user to an existing mentor", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.menteeEmail = "invalid@gmail.com";
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From a non-existing user to an non-existing mentor", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mentorEmail = "invalid@gmail.com";
        body.menteeEmail = "alsoinvalid@gmail.com";
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    // created additional test for when mentorid specified is valid but is not a mentor
    test("From an existing mentee to another mentee", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mentorEmail = "manekgujral11@gmail.com";
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("Meeting with a non-numeric input for paymentAmount", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.paymentAmount = "invalid";
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("Meeting with no input for paymentAmount", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        delete body.paymentAmount;
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("Meeting with startTime after endTime", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mStartTime = {
            "year": 2022,
            "month": 6,
            "dayOfMonth": 27,
            "hourOfDay": 14,
            "minute": 27,
            "second": 56
          }
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From an existing mentee to themselves", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mentorEmail = "manekgujral11@gmail.com";
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From an existing user to a mentor who is an existing user", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mId = meetingID;
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(200)
    })
    
})

// Tests for getting all meetings for a user
describe("GET /meetings/email", () => {
    test("Get meetings for a valid user", async () => {
        const res = await request(app).get("/meetings/kusharora339@gmail.com").set("year", 2022).set("month", 6).send()
        expect(res.statusCode).toBe(200)
        // expect to get the meeting that was inputted above
        expect(JSON.parse(res.text).meetings.some(meeting => {
            if (meeting.mId == meetingID) {
                return true;
            }
            return false;
        }))
    })

    test("Get meetings for an invalid user", async () => {
        const res = await request(app).get("/meetings/johnwick@gmail.com").send({
            "email": "johnwick@gmail.com",
            "month": 6,
            "year": 2022
        })
        expect(res.statusCode).toBe(400)
    })
})

// Tests for getting all meetingsById
describe("GET /meetingsById", () => {
    test("Get meetingsById for a valid user", async () => {
        const res = await request(app).post("/meetingsById").send({
            "mId": meetingID
        })
        expect(res.statusCode).toBe(200)
    })

    test("Get meetingsById for an invalid user", async () => {
        const res = await request(app).post("/meetingsById").send({
            "mId": "invalid"
        })
        expect(res.statusCode).toBe(400)
    })
})

// Tests for updating meeting status
describe("PUT /meetings", () => {
    test("Update meeting status with an invalid status", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "invalid",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with an invalid mId", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": "invalid",
            "status": "declined",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with a user that is not the mentor in associated meeting", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "kusharora339@gmail.com@sample.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with an invalid user", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "invalid"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with a valid status and meeting ID", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(200)
    })
})

// Tests for updating a firebase token
describe("PUT /firebaseToken", () => {
    test("Update firebase token for a valid user", async () => {
        const res = await request(app).put("/firebaseToken").send({
            "email": "manekgujral11@gmail.com",
            "firebase_token": ""
        })
        expect(res.statusCode).toBe(200)
    })

    test("Update firebase token for an invalid user", async () => {
        const res = await request(app).put("/firebaseToken").send({
            "email": "johnwick@gmail.com",
            "firebase_token": ""
        })
        expect(res.statusCode).toBe(400)
    })
})