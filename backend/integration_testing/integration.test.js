const request = require('supertest')
const app = require('../server')
const init = require("./initUsers")

describe("when the user is not already in the database", () => {

    test("(for mentorIdToken) should return a json response with status code 200", async () => {
        const [mentorIdToken, ] = await init.initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": mentorIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("signedUp")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

    test("(for menteeIdToken) should return a json response with status code 200", async () => {
        const [, menteeIdToken] = await init.initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": menteeIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("signedUp")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

    test("(for mentorIdToken) should return a json response with status code 200", async () => {
        const [mentorIdToken, ] = await init.initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": mentorIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("loggedIn")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

    test("(for menteeIdToken) should return a json response with status code 200", async () => {
        const [, menteeIdToken] = await init.initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": menteeIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("loggedIn")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

})


