const request = require('supertest')
const {app, server} = require('../../server')
const init = require("../initUsers")
const db = require("../../database/connect")
const client = db.client

beforeAll(() => {
    console.log("DROPPING")
    var query1 = {email : "manekgujral11@gmail.com"}
    var query2 = {email : "kusharora339@gmail.com"}
    var query3 = {userEmail : "kusharora339@gmail.com"}
    client.db("UniStatDB").collection("Users").deleteOne(query1);
    client.db("UniStatDB").collection("Users").deleteOne(query2);
    client.db("UniStatDB").collection("Stats").deleteOne(query3);
})

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

var mentorSampleStat = {
    "userEmail": "kusharora339@gmail.com",
    "userPhoto": "https://lh3.googleusercontent.com/a/AItbvmnZ_qSBbayg--2ZH-kFFsfVZC6v57Rv1x4Ugtg=s96-c",
    "userName": "Mentor User",
    "univName": "Mentor Univ",
    "univMajor": "Mentor major",
    "univGpa": 1.0,
    "univEntranceScore": 1255,
    "univBio": "😀🥰😄😋😚😄"
}

describe("Sign up and login use case", () => {

    describe("when the user is not already in the database", () => {

        test("(for mentee) should return a json response with status code 200", async () => {
            const [,idMenteeToken] = await init.initializeUsers()
            const fb_token = await init.initUserFbToken()
            const res = await request(app).post("/users").send({
                "Token": idMenteeToken, 
                "firebase_token": fb_token
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("(for mentor) should return a json response with status code 200", async () => {
            const [idMentorToken,] = await init.initializeUsers()
            const fb_token = await init.initUserFbToken()
            const res = await request(app).post("/users").send({
                "Token": idMentorToken, 
                "firebase_token": fb_token
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

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

    describe("Tests for creating a user stat", () => {

        describe("creating user stat when all fields of body defined and user is not in db", () => {

            test("should return a json response with status code 200", async () => {
                const res = await request(app).post("/stats").send(mentorSampleStat)
                expect(res.statusCode).toBe(200)
                expect(JSON.parse(res.text).status).toBe(`Stat stored for ${mentorSampleStat.userEmail}`)
            })
        })
    
        describe("creating user stat when all fields of body defined and user is in db", () => {
    
            test("should return a json response with status code 400", async () => {
                const res = await request(app).post("/stats").send(mentorSampleStat)
                expect(res.statusCode).toBe(400)
                expect(JSON.parse(res.text).status).toBe("Stat already exists")
            })
        })
    
    
        describe("when body is missing or undefined", () => {
            
            const body = [
                {  "userEmail": "testmail" },
                {  "userPhoto": "link" },
                {  "userName": "unistat" },
                {  "univName": "Harvard" },
                {  "univMajor": "CPEN" },
                {  "univGpa": "3.5" },
                {  "univEntranceScore": "1100" },
                {  "univBio": "test bio" },
                {  "userEmail": "email", "userPhoto": "link", "userName": undefined },
                {
                    "userEmail": "manekgujral11@gmail.com",
                    "userPhoto": "link",
                    "userName": "Manek Gujral",
                    "univName": "UBC",
                    "univMajor": "Computer Science",
                    "univGpa": "4.33",
                    "univEntranceScore": "1600",
                },
                {}
            ]
    
            body.forEach(async (body) => {
                test("should return a json response with status code 400", async () => {
                    const res = await request(app).post("/stats").send(body)
                    expect(res.statusCode).toBe(400)
                    expect(JSON.parse(res.text).status).toBe("Cannot create user stat with undefined body")
                })
            })
        })
    })

})