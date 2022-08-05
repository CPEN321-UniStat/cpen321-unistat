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

    client.db("UniStatDB").listCollections({name: "Meetings"}).next(
        function (err, collectionInfo) {
            if (collectionInfo) { // Only if collection exists
                client.db("UniStatDB").collection("Meetings").drop();
            }
            if (err) console.log("Error dropping:", err)
        }
    )
})

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

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

})