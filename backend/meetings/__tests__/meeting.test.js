const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const { changeTesting } = require('../meetingHandlers')
const client = db.client

require("../../users/__mocks__/userMocks")
jest.mock("../../users/userHandlers.js")

require("../../payments/__mocks__/paymentMocks")
jest.mock("../../payments/paymentHandlers.js")

const menteeUser = {
    "iss": "https://accounts.google.com",
    "azp": "572477064370-fo6khhqfp4g0bp8k8s9fqsjn2msoqimq.apps.googleusercontent.com",
    "aud": "572477064370-fo6khhqfp4g0bp8k8s9fqsjn2msoqimq.apps.googleusercontent.com",
    "sub": "110978389962889292287",
    "email": "manekgujral11@gmail.com",
    "email_verified": true,
    "at_hash": "B8XZ0KZ81_4blNoVFCeJJw",
    "iat": 1,
    "exp": 1,
    "firebase_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTY1OTYzMTkwOCwiZXhwIjoxNjU5NjM1NTA4LCJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay1vNWVhZkB1bmlzdGF0LTE2NTYxMjg1OTU5MzkuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJmaXJlYmFzZS1hZG1pbnNkay1vNWVhZkB1bmlzdGF0LTE2NTYxMjg1OTU5MzkuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ1aWQiOiIxMDYwODk3ODYyMjIxNjE1Nzk0NzkifQ.HU5CGo3U1iQyBoR9wttLO6bYfBcbG4oag_R2S18mAh6wjoQdTkVq8eEPYvft0fIaRsaP_5wNoN_mnmebrNT9t7U1iiGcUHcVzf8As6l3YZv46KyqHTXyRcRO-fiEVhWkWWkjKWQT1OJ5iDQUvM4lq5muuNb-YuWPMmpGq3mYb-hj7aAm-SjKNuVYmbfX843gRU8iCvW9oKxhvo3dE8-rvYC_0y4KTGkEOpJPdGDOTLJ96cjNb7yRU14Ko7_B9BRATaA_NW0hakJlardM9rl7huKpR7Gs-NktwRxbdpx-Ab_MZS_4ZDB_Byqe9TJ92XCRuKuZNBIAGEh0_XBH71b1lw",
    "currency": 100
}

const mentorUser = {
    "iss": "https://accounts.google.com",
    "azp": "572477064370-fo6khhqfp4g0bp8k8s9fqsjn2msoqimq.apps.googleusercontent.com",
    "aud": "572477064370-fo6khhqfp4g0bp8k8s9fqsjn2msoqimq.apps.googleusercontent.com",
    "sub": "109260748916358625017",
    "email": "kusharora339@gmail.com",
    "email_verified": true,
    "at_hash": "VOaaDwawAqKvc80o8CHxxQ",
    "iat": 2,
    "exp": 2,
    "firebase_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTY1OTYzMTkwOSwiZXhwIjoxNjU5NjM1NTA5LCJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay1vNWVhZkB1bmlzdGF0LTE2NTYxMjg1OTU5MzkuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJmaXJlYmFzZS1hZG1pbnNkay1vNWVhZkB1bmlzdGF0LTE2NTYxMjg1OTU5MzkuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ1aWQiOiIxMDYwODk3ODYyMjIxNjE1Nzk0NzkifQ.i8lPQeYeTanT-B-XXtUa0DvPdoswUkY0RnarebenheSUzkkAR6JhT-aUfU5W6z5IxzMPujb4jZZCGqBNWUIXZFZD1G2VrjjgqQxY56_euEtl8OBaj5Wxuhu1bYShWXGIsqQCOgxlmtzIKvWKat0bxRz6ASjm3D6zqZ55uy67CYZUC0735MlUBYtzWw50Ai01FXSuhhyDwbLWCMt6J6M0PgNomwe5Z2IllAu86_zyOCIRysxqAObSG5n8679xiFT49doTkA3xYftorOFuG0fSRxZB-ulKJN7kxU-NRdvVvMIGhie5JDpF0KxnpCNSVhYyM0iu70rzDUgnds1e2pCUIw",
    "currency": 100
}

const mentorStat = {
    "userEmail": "kusharora339@gmail.com",
    "userPhoto": "https://lh3.googleusercontent.com/a/AItbvmnZ_qSBbayg--2ZH-kFFsfVZC6v57Rv1x4Ugtg=s96-c",
    "userName": "Mentor User",
    "univName": "Mentor Univ",
    "univMajor": "Mentor major",
    "univGpa": 1,
    "univEntranceScore": 1255,
    "univBio": "😀🥰😄😋😚😄"
}

// UNIT TESTS (Point 6 of M6)
const meetingID = Math.random().toString(16).substr(2, 16);
const sampleIntegrationTestMeeting = {
    "meetingLogs": [],
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
            if (err) console.log("Error dropping db:", err)
        }
    )
    var query1 = {email : "manekgujral11@gmail.com"}
    var query2 = {email : "kusharora339@gmail.com"}
    var query3 = {userEmail : "kusharora339@gmail.com"}
    client.db("UniStatDB").collection("Users").deleteOne(query1);
    client.db("UniStatDB").collection("Users").deleteOne(query2);
    client.db("UniStatDB").collection("Stats").deleteOne(query3);
    client.db("UniStatDB").collection("Users").insertOne(menteeUser);
    client.db("UniStatDB").collection("Users").insertOne(mentorUser);
    client.db("UniStatDB").collection("Stats").insertOne(mentorStat);
    changeTesting()
})

afterAll( async() => {
    // Close the server instance after each test
    server.close()
    client.close()
})

// Tests for creating meeting requests
describe("POST /meetings", () => {
    test("From an existing user to a non-existing user", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mentorEmail = "invalid@gmail.com";
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From a non-existing user to an existing mentor", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);        
        body.menteeEmail = "invalid@gmail.com";
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From a non-existing user to an non-existing mentor", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);        
        body.mentorEmail = "invalid@gmail.com";
        body.menteeEmail = "alsoinvalid@gmail.com";
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    // created additional test for when mentorid specified is valid but is not a mentor
    test("From an existing mentee to another mentee", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mentorEmail = "manekgujral11@gmail.com";
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("Meeting with a non-numeric input for paymentAmount", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.paymentAmount = "invalid";
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("Meeting with no input for paymentAmount", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        delete body.paymentAmount;
        await process.nextTick(() => { });
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
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From an existing mentee to themselves", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        body.mentorEmail = "manekgujral11@gmail.com";
        await process.nextTick(() => { });
        const res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(400)
    })

    test("From an existing user to a mentor who is an existing user", async () => {
        let body = Object.assign({}, sampleIntegrationTestMeeting);
        await process.nextTick(() => { });
        var res = await request(app).post("/meetings").send(body)
        expect(res.statusCode).toBe(200)
    })

     test("For a meeting that already exists", async () => {
         let body = Object.assign({}, sampleIntegrationTestMeeting);
         body.mId = meetingID;
         await process.nextTick(() => { });
         const res = await request(app).post("/meetings").send(body)
         expect(res.statusCode).toBe(400)
     })
})

// Tests for getting all meetings for a user
describe("GET /meetings/email", () => {
    test("Get meetings for a valid user", async () => {
        await process.nextTick(() => { });
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
        await process.nextTick(() => { });
        const res = await request(app).get("/meetings/johnwick@gmail.com").send({
            "email": "johnwick@gmail.com",
            "month": 6,
            "year": 2022
        })
        expect(res.statusCode).toBe(400)
    })

    test("Invalid month/year value", async () => {
        await process.nextTick(() => { });
        const res = await request(app).get("/meetings/johnwick@gmail.com").send({
            "email": "johnwick@gmail.com",
            "month": 6,
            "year": -2022
        })
        expect(res.statusCode).toBe(400)
    })
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

// Tests for getting all meetingsById
describe("GET /meetingsById", () => {
    test("Get meetingsById for a valid user", async () => {
        await process.nextTick(() => { });
        const res = await request(app).post("/meetingsById").send({
            "mId": meetingID
        })
        expect(res.statusCode).toBe(200)
    })

    test("Get meetingsById for an invalid user", async () => {
        await process.nextTick(() => { });
        const res = await request(app).post("/meetingsById").send({
            "mId": "invalid"
        })
        expect(res.statusCode).toBe(400)
    })
})

// Tests for updating meeting status
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
            "status": "REJECTED",
            "email": "kusharora339@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with a user that is not the mentor in associated meeting", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "REJECTED",
            "email": "manekgujral11@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with an invalid user", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "REJECTED",
            "email": "invalid"
        })
        expect(res.statusCode).toBe(400)
    })

    test("Update meeting status with a valid status, meeting ID, and zoom credentials", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/meetings").send({
            "mId": meetingID,
            "status": "REJECTED",
            "email": "kusharora339@gmail.com",
            "zoomId": "12345",
            "zoomPassword": "password"
        })
        expect(res.statusCode).toBe(200)
    })
})


//Tests for createZoomMeeting
describe("POST /createZoomMeeting", () => {
    test("Meeting Name is null", async () => {
        await process.nextTick(() => { });
        const res = await request(app).post("/createZoomMeeting").send({
            "meetingTopic": null,
            "meetingStartTime": "2022-08-11'T'11:05:00",
            "meetingEndTime": "2022-08-11'T'12:05:00",
            "mId": meetingID
        })
        expect(res.statusCode).toBe(400)
    })

    test("Creates a Zoom meeting", async () => {
        await process.nextTick(() => { });
        const res = await request(app).post("/createZoomMeeting").send({
            "meetingTopic": "Test Meeting",
            "meetingStartTime": "2022-08-11'T'11:05:00",
            "meetingEndTime": "2022-08-11'T'12:05:00",
            "mId": meetingID
        })
        expect(res.statusCode).toBe(200)
    })

    test("Start date is after end date", async () => {
        await process.nextTick(() => { });
        const res = await request(app).post("/createZoomMeeting").send({
            "meetingTopic": "Test Meeting",
            "meetingStartTime": "2022-08-11'T'14:05:00",
            "meetingEndTime": "2022-08-11'T'12:05:00",
            "mId": meetingID
        })
        expect(res.statusCode).toBe(400)
    })
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
        await process.nextTick(() => { });
        const res = await request(app).put("/firebaseToken").send({
            "email": "kusharora339@gmail.com",
            "firebase_token": ""
        })
        expect(res.statusCode).toBe(200)
    })

    test("Update firebase token for an invalid user", async () => {
        await process.nextTick(() => { });
        const res = await request(app).put("/firebaseToken").send({
            "email": "johnwick@gmail.com",
            "firebase_token": ""
        })
        expect(res.statusCode).toBe(400)
    })
})
