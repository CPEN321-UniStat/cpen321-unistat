const request = require('supertest')
const {app, server} = require('../../server')
const init = require("../initUsers")
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

beforeAll(() => {
    console.log("DROPPING")
    var query1 = {email : "manekgujral11@gmail.com"}
    var query2 = {email : "kusharora339@gmail.com"}
    var query3 = {userEmail : "kusharora339@gmail.com"}
    client.db("UniStatDB").collection("Users").deleteOne(query1);
    client.db("UniStatDB").collection("Users").deleteOne(query2);
    client.db("UniStatDB").collection("Stats").deleteOne(query3);
    client.db("UniStatDB").collection("Users").insertOne(menteeUser);
    client.db("UniStatDB").collection("Users").insertOne(mentorUser);
})

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

describe("login use case", () => {

    describe("when the user is already in the database", () => {

        test("(for mentee) should return a json response with status code 200", async () => {
            const [,idMenteeToken] = await init.initializeUsers()
            const fb_token = await init.initUserFbToken();
            const res = await request(app).post("/users").send({
                "Token": idMenteeToken, 
                "firebase_token": fb_token
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("loggedIn")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("(for mentor) should return a json response with status code 200", async () => {
            [idMentorToken,] = await init.initializeUsers();
            const fb_token = await init.initUserFbToken()
            const res = await request(app).post("/users").send({
                "Token": idMentorToken, 
                "firebase_token": fb_token
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("loggedIn")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when body does not contain valid user token", () => {
        // send a json response with status code 400
        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/users").send({
                "Token": "invalid token", 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(400)
        })
    })

    describe("when body does not contain user token or firebase_token or both", () => {
        // send a json response with status code 400
        const body = [
            {  "Token": "token" },
            {  "firebase_token": "testFirebaseToken" },
            {}
        ]

        body.forEach(async (body) => {
            test("should return a json response with status code 400", async () => {
                const res = await request(app).post("/users").send(body)
                expect(res.statusCode).toBe(400)
                expect(JSON.parse(res.text).status).toBe("Cannot create user with undefined body")
            })
        })
    })

})