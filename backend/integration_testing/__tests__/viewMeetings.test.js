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

    var query2 = {email : "kusharora339@gmail.com"}
    client.db("UniStatDB").collection("Users").deleteOne(query2);
    client.db("UniStatDB").collection("Users").insertOne(mentorUser);  
})

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})


// Tests for Viewing Meetings
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
})