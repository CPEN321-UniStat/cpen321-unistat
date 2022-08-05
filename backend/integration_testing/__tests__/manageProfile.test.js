const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client


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

describe("Manage Profile use case", () => {

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

    describe("Tests for getting user profile", () => {

        describe("(for mentee) Filter by user email", () => {

            test("should return a json response with a json array of length 1 with status code 200", async () => {
                const res = await request(app).post("/statsByFilter").send({
                    "userEmail": "manekgujral11@gmail.com",
                })
                expect(res.statusCode).toBe(200)
                var dataLen = JSON.parse(res.text).statData.length
                for (let i = 0; i < dataLen; i++) {
                    expect(JSON.parse(res.text).statData[0].isMentor).toBe(false)
                }
                expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
            })
    
        })

        describe("(for mentor) Filter by user email", () => {

            test("should return a json response with a json array of length 1 with status code 200", async () => {
                const res = await request(app).post("/statsByFilter").send({
                    "userEmail": "kusharora339@gmail.com",
                })
                expect(res.statusCode).toBe(200)
                var dataLen = JSON.parse(res.text).statData.length
                for (let i = 0; i < dataLen; i++) {
                    expect(JSON.parse(res.text).statData[i].userEmail).toBe("kusharora339@gmail.com")
                }
                expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
            })
    
        })
    })

    describe("Tests for updating user profile", () => {

        describe("updating user stat when all fields of body defined", () => {

            test("should return a json response with status code 200", async () => {
                mentorSampleStat.univGpa = 4.0
                const res = await request(app).put("/stats").send(mentorSampleStat)
                expect(res.statusCode).toBe(200)
                expect(JSON.parse(res.text).status).toBe(`Stat updated for ${mentorSampleStat.userEmail}`)
                expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
            })
        })
    
        describe("when body is missing or undefined", () => {
            
            const body = [
                {  "userEmail": "testmail" },
                {  "univName": "Harvard" },
                {  "univMajor": "CPEN" },
                {  "univGpa": "3.5" },
                {  "univEntranceScore": "1100" },
                {  "univBio": "test bio" },
                {  "userEmail": "email", "userPhoto": "link", "userName": undefined },
                {
                    "userEmail": "manekgujral11@gmail.com",
                    "univName": "UBC",
                    "univMajor": "Computer Science",
                    "univGpa": "4.33",
                    "univEntranceScore": "1600",
                },
                {}
            ]
    
            body.forEach(async (body) => {
                test("should return a json response with status code 400", async () => {
                    const res = await request(app).put("/stats").send(body)
                    expect(res.statusCode).toBe(400)
                    expect(JSON.parse(res.text).status).toBe("Cannot update user stat with undefined body")
                })
            })
        })
    })
})