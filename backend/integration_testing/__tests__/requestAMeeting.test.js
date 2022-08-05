const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client

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


beforeAll(() => {
    console.log("DROPPING")

    var query1 = {mId : meetingID}
    client.db("UniStatDB").collection("Meetings").deleteOne(query1);

    var query1 = {email : "manekgujral11@gmail.com"}
    var query2 = {email : "kusharora339@gmail.com"}
    var query3 = {userEmail : "kusharora339@gmail.com"}
    client.db("UniStatDB").collection("Users").deleteOne(query1);
    client.db("UniStatDB").collection("Users").deleteOne(query2);
    client.db("UniStatDB").collection("Stats").deleteOne(query3);
    client.db("UniStatDB").collection("Users").insertOne(menteeUser);
    client.db("UniStatDB").collection("Users").insertOne(mentorUser);
    client.db("UniStatDB").collection("Stats").insertOne(mentorStat);
})

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


// Tests for Requesting a Meeting
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