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


const menteeUser = {
    "iss": "https://accounts.google.com",
    "azp": "572477064370-fo6khhqfp4g0bp8k8s9fqsjn2msoqimq.apps.googleusercontent.com",
    "aud": "572477064370-fo6khhqfp4g0bp8k8s9fqsjn2msoqimq.apps.googleusercontent.com",
    "sub": "110978389962889292287",
    "email": "manekgujral11@gmail.com",
    "email_verified": true,
    "at_hash": "B8XZ0KZ81_4blNoVFCeJJw",
    "iat": 1659631906,
    "exp": 1659635506,
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
    "iat": 1659631907,
    "exp": 1659635507,
    "firebase_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTY1OTYzMTkwOSwiZXhwIjoxNjU5NjM1NTA5LCJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay1vNWVhZkB1bmlzdGF0LTE2NTYxMjg1OTU5MzkuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJmaXJlYmFzZS1hZG1pbnNkay1vNWVhZkB1bmlzdGF0LTE2NTYxMjg1OTU5MzkuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ1aWQiOiIxMDYwODk3ODYyMjIxNjE1Nzk0NzkifQ.i8lPQeYeTanT-B-XXtUa0DvPdoswUkY0RnarebenheSUzkkAR6JhT-aUfU5W6z5IxzMPujb4jZZCGqBNWUIXZFZD1G2VrjjgqQxY56_euEtl8OBaj5Wxuhu1bYShWXGIsqQCOgxlmtzIKvWKat0bxRz6ASjm3D6zqZ55uy67CYZUC0735MlUBYtzWw50Ai01FXSuhhyDwbLWCMt6J6M0PgNomwe5Z2IllAu86_zyOCIRysxqAObSG5n8679xiFT49doTkA3xYftorOFuG0fSRxZB-ulKJN7kxU-NRdvVvMIGhie5JDpF0KxnpCNSVhYyM0iu70rzDUgnds1e2pCUIw",
    "currency": 100
}


beforeAll(() => {
    console.log("DROPPING")
    var query1 = {mId : meetingID}
    client.db("UniStatDB").collection("Meetings").deleteOne(query1);
    client.db("UniStatDB").collection("Meetings").insertOne(sampleIntegrationTestMeeting);
    var query2 = {email : "manekgujral11@gmail.com"}
    var query3 = {email : "kusharora339@gmail.com"}
    client.db("UniStatDB").collection("Users").deleteOne(query2);
    client.db("UniStatDB").collection("Users").deleteOne(query3);
    client.db("UniStatDB").collection("Users").insertOne(menteeUser);
    client.db("UniStatDB").collection("Users").insertOne(mentorUser);

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
