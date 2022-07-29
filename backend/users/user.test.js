const request = require('supertest')
const app = require('../server')
const db = require("../database/connect")
const { JsonWebTokenError } = require('jsonwebtoken')
const client = db.client

jest.mock("./userVerification.js")

const idMenteeToken = "validMenteeToken"
const idMentorToken = "validMentorToken"
const invalidIdToken = "invalid"
const verify = require("./userVerification")

const menteeSampleUser = {
    iss: 'iss',
    azp: 'azp',
    aud: 'aud',
    sub: 'sub',
    email: 'menteeuser@sample.com',
    email_verified: true,
    name: 'Mentee User',
    picture: 'Test pic',
    given_name: 'Mentee',
    family_name: 'User',
    locale: 'en',
    iat: 0,
    exp: 0
  }

  const mentorSampleUser = {
    iss: 'iss',
    azp: 'azp',
    aud: 'aud',
    sub: 'sub',
    email: 'mentoruser@sample.com',
    email_verified: true,
    name: 'Mentee User',
    picture: 'Test pic',
    given_name: 'Mentor',
    family_name: 'User',
    locale: 'en',
    iat: 0,
    exp: 0
  }

  const mentorSampleStat = {
    "userEmail": "mentoruser@sample.com",
    "userPhoto": "https://lh3.googleusercontent.com/a/AItbvmnZ_qSBbayg--2ZH-kFFsfVZC6v57Rv1x4Ugtg=s96-c",
    "userName": "Mentor User",
    "univName": "Mentor Univ",
    "univMajor": "Mentor major",
    "univGpa": 1.0,
    "univEntranceScore": 1255,
    "univBio": "😀🥰😄😋😚😄"
  }

verify.userVerifier.mockImplementation( async (idToken) => {
    if (idToken === idMenteeToken) {
        return menteeSampleUser
    } else if (idToken === idMentorToken) {
        return mentorSampleUser
    } else {
        throw "invalid token"
    }
})

describe("POST /users", () => {

    describe("when the user is not already in the database", () => {

        test("(for mentee) should return a json response with status code 200", async () => {
            const res = await request(app).post("/users").send({
                "Token": idMenteeToken, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            // expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("(for mentor) should return a json response with status code 200", async () => {
            const res = await request(app).post("/users").send({
                "Token": idMentorToken, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            // expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when the user is already in the database", () => {

        test("(for mentee) should return a json response with status code 200", async () => {
            const res = await request(app).post("/users").send({
                "Token": idMenteeToken, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("loggedIn")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("(for mentor) should return a json response with status code 200", async () => {
            const res = await request(app).post("/users").send({
                "Token": idMentorToken, 
                "firebase_token": "testFirebaseToken"
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
                "Token": invalidIdToken, 
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

describe("POST /userByEmail", () => {

    describe("when the user is in the database", () => {

        test("should return a json response with status code 200", async () => {
            const res = await request(app).post("/userByEmail").send({
                "userEmail": "manekgujral11@gmail.com"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).userName).toBe("Manek Gujral")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when the user is not in the database", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/userByEmail").send({
                "userEmail": "dummmyEmail"
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Cannot get user without valid email")
        })

    })

    describe("when body is missing or undefined", () => {
        const body = [
            {  "userEmail": undefined },
            {}
        ]

        body.forEach(async (body) => {
            test("should return a json response with status code 400", async () => {
                const res = await request(app).post("/userByEmail").send(body)
                expect(res.statusCode).toBe(400)
                expect(JSON.parse(res.text).status).toBe("Cannot get user without valid email")
            })
        })
    })
})

describe("POST /stats", () => {

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

describe("GET /stats", () => {

    describe("get all Stats in db", () => {
        test("should return a json response with status code 400", async () => {
            const res = await request(app).get("/stats")
            expect(res.statusCode).toBe(200)
        })
    })

})

describe("POST /statsByFilter", () => {

    describe("when the filter string is undefined", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByFilter").send({
                "univName": undefined
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot filter user stat with undefined body")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when there is more than one filter", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByFilter").send({
                "univName": "UBC",
                "univMajor": "cpen"
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot filter more than one string")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when the filter criteria is incorrect", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByFilter").send({
                "xxx": "",
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Please make sure the filter criteria is either univName or univMajor")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("Filter by university name", () => {

        test("should return a json response with a json array of length 1 with status code 200", async () => {
            const res = await request(app).post("/statsByFilter").send({
                "univName": "UBC",
            })
            expect(res.statusCode).toBe(200)
            var dataLen = JSON.parse(res.text).statData.length
            for (let i = 0; i < dataLen; i++) {
                expect(JSON.parse(res.text).statData[i].univName).toBe("UBC")
            }
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("Filter by university major", () => {

        test("should return a json response with a json array of length 1 with status code 200", async () => {
            const res = await request(app).post("/statsByFilter").send({
                "univMajor": "MBA",
            })
            expect(res.statusCode).toBe(200)
            var dataLen = JSON.parse(res.text).statData.length
            for (let i = 0; i < dataLen; i++) {
                expect(JSON.parse(res.text).statData[i].univMajor).toBe("MBA")
            }
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })
})

describe("POST /statsBySorting", () => {

    describe("when the sort string is undefined", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsBySorting").send(undefined)
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot sort user stat with undefined body")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when there is more than one sort criteria", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsBySorting").send({
                "univGpa": "",
                "univEntranceScore": ""
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot sort by more than one criteria")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when the sort criteria is incorrect", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsBySorting").send({
                "xxx": "",
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Please make sure the sort criteria is either univGpa or univEntranceScore")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("Sort by university GPA", () => {

        test("should return a json array with with univGpas in non-increasing order, and status code 200", async () => {
            const res = await request(app).post("/statsBySorting").send({
                "univGpa": "",
            })

            const resStats = await request(app).get("/stats")
            var statLength = JSON.parse(resStats.text).statData.length
            var expectedList = []
            var actualList = []

            for (let i=0; i<statLength; i++){
                actualList.push(JSON.parse(res.text).statData[i].univGpa)
                expectedList.push(JSON.parse(resStats.text).statData[i].univGpa)
            }

            expectedList.sort(function(a, b) {
                return a - b;
              }).reverse()


            expect(res.statusCode).toBe(200)
            expect(actualList).toStrictEqual(expectedList)
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("Sort by university Entrance Score", () => {

        test("should return a json array with with univEntranceScores in non-increasing order, and status code 200", async () => {
            const res = await request(app).post("/statsBySorting").send({
                "univEntranceScore": "",
            })

            const resStats = await request(app).get("/stats")
            var statLength = JSON.parse(resStats.text).statData.length
            var expectedList = []
            var actualList = []

            for (let i=0; i<statLength; i++){
                actualList.push(JSON.parse(res.text).statData[i].univEntranceScore)
                expectedList.push(JSON.parse(resStats.text).statData[i].univEntranceScore)
            }

            expectedList.sort(function(a, b) {
                return a - b;
              }).reverse()


            expect(res.statusCode).toBe(200)
            expect(actualList).toStrictEqual(expectedList)
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })
})

describe("POST /statsByConfiguration", () => {

    describe("when the filter string is undefined", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send(undefined)
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot sort/filter user stats with undefined body")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univName": null,
                "univGpa": ""
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot sort/filter user stats with undefined body")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univName": "",
                "univGpa": null
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot sort/filter user stats with undefined body")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univName": null,
                "univGpa": null
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot sort/filter user stats with undefined body")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when there is more or less than two sort/filter criteria", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univName": "UBC",
                "univEntranceScore": "",
                "xxx": ""
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Cannot sort/filter by more or less than one criteria for each")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("when the sort/filter criteria is incorrect", () => {

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "xxx": "ubc",
                "xxxx": ""
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Please make sure that the sort and filter configurations are correct with sort placed before the filter configuration")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univGpa": "",
                "univName": "UBC"
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Please make sure that the sort and filter configurations are correct with sort placed before the filter configuration")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univEntranceScore": "",
                "univMajor": "CPEN"
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Please make sure that the sort and filter configurations are correct with sort placed before the filter configuration")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a json response with status code 400", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univGpa": "",
                "univMajor": "CPEN"
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Invalid request: Please make sure that the sort and filter configurations are correct with sort placed before the filter configuration")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

    })

    describe("Filter and sort with correct configuration", () => {

        test("should return a sorted and filtered json data with status code 200", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univName": "UBC",
                "univGpa": ""
            })
            expect(res.statusCode).toBe(200)

            var dataLen = JSON.parse(res.text).statData.length

            for (let i = 0; i < dataLen; i++) {
                expect(JSON.parse(res.text).statData[i].univName).toBe("UBC")
            }

            for (let i = 0; i < dataLen-1; i++) {
                var curr = JSON.parse(res.text).statData[i].univGpa
                var next = JSON.parse(res.text).statData[i+1].univGpa
                var expectedCondition = (curr >= next)
                expect(expectedCondition).toBe(true)
            }

            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a sorted and filtered json data with status code 200", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univName": "UBC",
                "univEntranceScore": ""
            })
            expect(res.statusCode).toBe(200)

            var dataLen = JSON.parse(res.text).statData.length

            for (let i = 0; i < dataLen; i++) {
                expect(JSON.parse(res.text).statData[i].univName).toBe("UBC")
            }

            for (let i = 0; i < dataLen-1; i++) {
                var curr = JSON.parse(res.text).statData[i].univEntranceScore
                var next = JSON.parse(res.text).statData[i+1].univEntranceScore
                var expectedCondition = (curr >= next)
                expect(expectedCondition).toBe(true)
            }

            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })

        test("should return a sorted and filtered json data with status code 200", async () => {
            const res = await request(app).post("/statsByConfiguration").send({
                "univMajor": "cpen",
                "univGpa": ""
            })
            expect(res.statusCode).toBe(200)

            var dataLen = JSON.parse(res.text).statData.length

            for (let i = 0; i < dataLen; i++) {
                expect(JSON.parse(res.text).statData[i].univMajor).toBe("cpen")
            }

            for (let i = 0; i < dataLen-1; i++) {
                var curr = JSON.parse(res.text).statData[i].univGpa
                var next = JSON.parse(res.text).statData[i+1].univGpa
                var expectedCondition = (curr >= next)
                expect(expectedCondition).toBe(true)
            }

            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })
    })
})

describe("PUT /stats", () => {

    describe("updating user stat when all fields of body defined", () => {

        test("should return a json response with status code 200", async () => {
            const res = await request(app).put("/stats").send({
                "userEmail": "manekgujral11@gmail.com",
                "userPhoto": "link",
                "userName": "Manek Gujral",
                "univName": "UBC",
                "univMajor": "Computer Science",
                "univGpa": 4.20,
                "univEntranceScore": 1600,
                "univBio": "Test bio",
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("Stat updated for manekgujral11@gmail.com")
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