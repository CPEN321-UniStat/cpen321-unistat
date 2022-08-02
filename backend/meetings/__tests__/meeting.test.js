const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const { sendMeetingRequest } = require('../../users/userHandlers')
const { schedulePayment } = require('../../payments/paymentHandlers')
const client = db.client


require("../../users/__mocks__/userMocks")
jest.mock("../../users/userHandlers.js")

require("../../payments/__mocks__/paymentMocks")
jest.mock("../../payments/paymentHandlers.js")

// UNIT TESTS (Point 6 of M6)
const meetingID = Math.random().toString(16).substr(2, 16);
const sampleIntegrationTestMeeting = {
    "meetingLogs": [
        {
          "timestamp": "2022-07-09T11:00:00",
          "userEmail": "manekgujral11@gmail.com",
          "isMentor": false,
          "action": "JOINED"
        },
        {
            "timestamp": "2022-07-09T11:00:00",
            "userEmail": "kusharora339@gmail.com",
            "isMentor": true,
            "action": "JOINED"
        },
        {
            "timestamp": "2022-07-09T11:00:00",
            "userEmail": "manekgujral11@gmail.com",
            "isMentor": false,
            "action": "LEFT"
        },
        {
            "timestamp": "2022-07-09T11:00:00",
            "userEmail": "kusharora339@gmail.com",
            "isMentor": true,
            "action": "LEFT"
        }
    ],
    "menteeEmail": "manekgujral11@gmail.com",
    "mentorEmail": "kusharora339@gmail.com",
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
    client.db("UniStatDB").listCollections({name: "Meetings"}).next(
        function (err, collectionInfo) {
            if (collectionInfo) { // Only if collection exists
                client.db("UniStatDB").collection("Meetings").drop();
            }
        }
    )
})



afterAll( async() => {
    // Close the server instance after each test
    server.close()
    client.close()
})

// Tests for creating meeting requests
describe("POST /meetings", () => {
    describe("When meeting isn't in the DB", () => {
        test("From an existing user to a non-existing user", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.mentorEmail = "invalid@gmail.com";
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })
    
        test("From a non-existing user to an existing mentor", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.menteeEmail = "invalid@gmail.com";
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })
    
        test("From a non-existing user to an non-existing mentor", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.mentorEmail = "invalid@gmail.com";
            body.menteeEmail = "alsoinvalid@gmail.com";
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })
    
        // created additional test for when mentorid specified is valid but is not a mentor
        test("From an existing mentee to another mentee", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.mentorEmail = "kusharora339@gmail.com";
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })
    
        test("Meeting with a non-numeric input for paymentAmount", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.paymentAmount = "invalid";
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })
    
        test("Meeting with no input for paymentAmount", async () => {
            var body = {...sampleIntegrationTestMeeting}
            delete body.paymentAmount;
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })
    
        test("Meeting with startTime after endTime", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.mStartTime = {
                "year": 2022,
                "month": 6,
                "dayOfMonth": 27,
                "hourOfDay": 14,
                "minute": 27,
                "second": 56
            }
            await process.nextTick(() => { });
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })
    
        test("From an existing mentee to themselves", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.mentorEmail = "manekgujral11@gmail.com";
            await process.nextTick(() => { });
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(400)
        })

        test("From an existing user to a mentor who is an existing user", async () => {
            var body = {...sampleIntegrationTestMeeting}
            body.mId = meetingID;
            await process.nextTick(() => { });
            const res = await request(app).post("/meetings").send(body)
            expect(res.statusCode).toBe(200)
        })
    })

    test("For a meeting that already exists", async () => {
        var body = {...sampleIntegrationTestMeeting}
        body.mId = meetingID;
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })
    
})

// Tests for getting all meetings for a user
describe("GET /meetings/email", () => {
    test("Get meetings for a valid user", async () => {
        const res = await request(app).get("/meetings/kusharora339@gmail.com").send({
            "email": "kusharora339@gmail.com",
            "month": 6,
            "year": 2022
        })
        expect(res.statusCode).toBe(200)
        // expect to get the meeting that was inputted above
        expect(res.meetings.some(meeting => {
            if (meeting.mId = meetingID) {
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

    test("Invalid month/year value", async () => {
        const res = await request(app).get("/meetings/johnwick@gmail.com").send({
            "email": "johnwick@gmail.com",
            "month": 6,
            "year": -2022
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
            "status": "DECLINED",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with a user that is not the mentor in associated meeting", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "kusharora339@gmail.com"
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

    test("Update meeting status with a valid status, meeting ID, and zoom credentials", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "DECLINED",
            "email": "kusharora339@gmail.com",
            "zoomId": "12345",
            "zoomPassword": "password"
        })
        expect(res.statusCode).toBe(200)
    })
})


// Tests for createZoomMeeting
// describe("POST /createZoomMeeting", () => {
//     test("Meeting Name is null", async () => {
//         const res = await request(app).post("/createZoomMeeting").send({
//             "meetingTopic": null,
//             "meetingStartTime": "2022-08-11'T'11:05:00",
//             "meetingEndTime": "2022-08-11'T'12:05:00"
//         })
//         expect(res.statusCode).toBe(400)
//     })

//     test("Creates a Zoom meeting", async () => {
//         const res = await request(app).post("/createZoomMeeting").send({
//             "meetingTopic": "Test Meeting",
//             "meetingStartTime": "2022-08-11'T'11:05:00",
//             "meetingEndTime": "2022-08-11'T'12:05:00"
//         })
//         expect(res.statusCode).toBe(200)
//     })

//     test("Start date is after end date", async () => {
//         await process.nextTick(() => { });
//         const res = await request(app).post("/createZoomMeeting").send({
//             "meetingTopic": "Test Meeting",
//             "meetingStartTime": "2022-08-11'T'14:05:00",
//             "meetingEndTime": "2022-08-11'T'12:05:00"
//         })
//         expect(res.statusCode).toBe(400)
//     })
// })

// Tests for updating meeting logs
describe("PUT /updateMeetingLog", () => {
    test("Update meeting logs with valid mId", async () => {
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
                "userEmail": "kusharora339@gmail.com",
                "isMentor": true,
                "action": "JOINED"
            }
        })
        expect(res.statusCode).toBe(400)
    })
})

// Tests for updating a firebase token
describe("PUT /firebaseToken", () => {
    test("Update firebase token for a valid user", async () => {
        const res = await request(app).put("/firebaseToken").send({
            "email": "kusharora339@gmail.com",
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