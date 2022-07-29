const axios = require("axios");
const dotEnv = require("dotenv");
dotEnv.config()
const request = require('supertest')
const app = require('../server')


const initializeUsers = async () => { 
    var mentorAccount = await axios.post(`https://oauth2.googleapis.com/token?client_id=${process.env.GOOGLE_CLIENT_ID}&client_secret=${process.env.GOOGLE_CLIENT_SECRET}&grant_type=refresh_token&refresh_token=${process.env.REFRESH_TOKEN_KUSH}`)
    var menteeAccount = await axios.post(`https://oauth2.googleapis.com/token?client_id=${process.env.GOOGLE_CLIENT_ID}&client_secret=${process.env.GOOGLE_CLIENT_SECRET}&grant_type=refresh_token&refresh_token=${process.env.REFRESH_TOKEN_MANEK}`)

    var mentorIdToken = mentorAccount.data.id_token
    var menteeIdToken = menteeAccount.data.id_token

    return [mentorIdToken, menteeIdToken]
}

describe("when the user is not already in the database", () => {

    test("(for mentorIdToken) should return a json response with status code 200", async () => {
        const [mentorIdToken, ] = await initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": mentorIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("signedUp")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

    test("(for menteeIdToken) should return a json response with status code 200", async () => {
        const [, menteeIdToken] = await initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": menteeIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("signedUp")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

    test("(for mentorIdToken) should return a json response with status code 200", async () => {
        const [mentorIdToken, ] = await initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": mentorIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("loggedIn")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

    test("(for menteeIdToken) should return a json response with status code 200", async () => {
        const [, menteeIdToken] = await initializeUsers()
        const res = await request(app).post("/users").send({
            "Token": menteeIdToken, 
            "firebase_token": "fb_token"
        })
        expect(res.statusCode).toBe(200)
        expect(JSON.parse(res.text).status).toBe("loggedIn")
        expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
    })

})

