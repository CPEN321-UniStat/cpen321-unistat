const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client

var mentorSampleStat = {
    "userEmail": "kusharora339@gmail.com",
    "userPhoto": "link",
    "userName": "Mentor User",
    "univName": "Mentor Univ",
    "univMajor": "Mentor major",
    "univGpa": 1.0,
    "univEntranceScore": 1255,
    "univBio": "😀🥰😄😋😚😄"
}

beforeAll(() => {
    console.log("DROPPING")
    var query1 = {email : "manekgujral11@gmail.com"}
    var query2 = {email : "kusharora339@gmail.com"}
    var query3 = {userEmail : "kusharora339@gmail.com"}
    client.db("UniStatDB").collection("Users").deleteOne(query1);
    client.db("UniStatDB").collection("Users").deleteOne(query2);
    client.db("UniStatDB").collection("Stats").deleteOne(query3);
    client.db("UniStatDB").collection("Stats").insertOne(mentorSampleStat);
})

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

describe("Manage Profile use case", () => {

    describe("Tests for getting user profile", () => {

        describe("(for mentee) Filter by user email", () => {

            test("should return a json response with a json array of length 1 with status code 200", async () => {
                await process.nextTick(() => { });
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
                const res = await request(app).put("/stats").send({
                    "userEmail": "kusharora339@gmail.com",
                    "userPhoto": "link",
                    "userName": "Mentor User",
                    "univName": "Mentor Univ",
                    "univMajor": "Mentor major",
                    "univGpa": 1.0,
                    "univEntranceScore": 1255,
                    "univBio": "😀🥰😄😋😚😄"
                })
                expect(res.statusCode).toBe(200)
                expect(JSON.parse(res.text).status).toBe("Stat updated for kusharora339@gmail.com")
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
                    await process.nextTick(() => { });
                    const res = await request(app).put("/stats").send(body)
                    expect(res.statusCode).toBe(400)
                    expect(JSON.parse(res.text).status).toBe("Cannot update user stat with undefined body")
                })
            })
        })
    })
})