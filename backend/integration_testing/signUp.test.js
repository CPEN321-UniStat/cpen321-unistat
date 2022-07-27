const request = require('supertest')
const app = require('../server')
// need to generate this token for a test account
const token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjA3NGI5MjhlZGY2NWE2ZjQ3MGM3MWIwYTI0N2JkMGY3YTRjOWNjYmMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1NzI0NzcwNjQzNzAta2lqODQzYnJ0cTk0MGxvMGpqdm8zbjJrZjkya2Q0c24uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI1NzI0NzcwNjQzNzAtODg1YnMzMzR1djE3Zmh1YmltdG9mNnN1MjRtZjBwcDguYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTA5NzgzODk5NjI4ODkyOTIyODciLCJlbWFpbCI6Im1hbmVrZ3VqcmFsMTFAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJNYW5layBHdWpyYWwiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUl0YnZtbHI0amZjeUdVQjgxNVVWMEFrUjNCMllhaVRSdnBzZlZYQ2RjSE49czk2LWMiLCJnaXZlbl9uYW1lIjoiTWFuZWsiLCJmYW1pbHlfbmFtZSI6Ikd1anJhbCIsImxvY2FsZSI6ImVuIiwiaWF0IjoxNjU4ODU3NDE5LCJleHAiOjE2NTg4NjEwMTl9.Q1B51hZFT5wyA20KrPi3m9KUVErwKhokkjUugvHqjW4c7m6OMA6QXEfZrt09IZhBhCMKnjAKlngG_2al7uS_Zu2dlLsluWCT1SUJeAJWGN828qL2vexxx-ZOBy7h5XA8uGfyuhpgS8ERUK0UvYIjsk_djOoZl471-RKvqqQVVF5lYD4RJ9EovkoNpM2_oSrnJf9TJ3EfTtfh2QdGMog5BcYFBxz-bOb2U7i9afPutYjDLLe0sUqRe5p468mldYFc4SJjDIKWAwIHSKCsIxezCUPfjXzxIAlmYC2ZDyCgeOr_zmLG7UlMxAsqq7m_91-PhEEZTPyDTCUoHAkkO1ga4Q"

describe("Sign Up Use Case End to End Test", () => {

    describe("mentee test case success", () => {
        test("POST users api called", async () => {
            const res = await request(app).post("/users").send({
                "Token": token, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })
    })

    describe("mentee test case incorrect email", () => {
        test("POST users api called", async () => {
            const res = await request(app).post("/users").send({
                "Token": "test token", 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe(error)
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })
    })

    describe("mentee/mentor sign up fail due to missing email body", () => {
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


    describe("mentor test case success", () => {

        test("POST users api called", async () => {
            const res = await request(app).post("/users").send({
                "Token": token, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })


        test("POST create mentor stat", async () => {
            const res = await request(app).post("/stats").send({
                "userEmail": "manekgujral11@gmail.com",
                "userPhoto": "link",
                "userName": "Manek Gujral",
                "univName": "UBC",
                "univMajor": "Computer Science",
                "univGpa": "4.33",
                "univEntranceScore": "1600",
                "univBio": "Test bio",
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("Stat stored for manekgujral11@gmail.com")
        })
    })

    describe("mentor test case incorrect email", () => {
        test("POST users api called", async () => {
            const res = await request(app).post("/users").send({
                "Token": token, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })
    })

    describe("mentor test case invalid stats", () => {

        test("POST users api called", async () => {
            const res = await request(app).post("/users").send({
                "Token": token, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })


        test("POST create mentor stat", async () => {
            const res = await request(app).post("/stats").send({
                "userEmail": "manekgujral11@gmail.com",
                "userPhoto": "link",
                "userName": "Manek Gujral",
                "univName": "UBC",
                "univMajor": "Science",
                "univGpa": "4.0",
                "univEntranceScore": "1100",
                "univBio": "Test bio",
            })
            expect(res.statusCode).toBe(400)
            expect(JSON.parse(res.text).status).toBe("Stat already exists")
        })
    })

    describe("mentor sign up fail due to missing stat body", () => {

        test("POST users api called", async () => {
            const res = await request(app).post("/users").send({
                "Token": token, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("signedUp")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })
        
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
                const res = await request(app).post("/users").send(body)
                expect(res.statusCode).toBe(400)
                expect(JSON.parse(res.text).status).toBe("Cannot create user with undefined body")
            })
        })
    })

    describe("mentee/mentor test case already signed up", () => {

        test("POST users api called", async () => {
            const res = await request(app).post("/users").send({
                "Token": token, 
                "firebase_token": "testFirebaseToken"
            })
            expect(res.statusCode).toBe(200)
            expect(JSON.parse(res.text).status).toBe("loggedIn")
            expect(res.headers['content-type']).toBe('text/html; charset=utf-8')
        })
    })

})