const request = require('supertest')
const app = require('../server')

// Tests for createMeetingRequest
describe("POST /createZoomMeeting", () => {
    test("Creates a Zoom meeting", async () => {
        const res = await request(app).post("/createZoomMeeting").send({
            "meetingTopic": "Test Meeting",
            "meetingStartTime": "2022-08-11'T'11:05:00",
            "meetingEndTime": "2022-08-11'T'12:05:00"
        })
        expect(res.statusCode).toBe(200)
    })
})

// Test for sending meeting request notification
describe("POST /sendMeetingRequest", () => {
    test("Sending a meeting request for a valid user", async () => {
        const res = await request(app).post("/sendMeetingRequest").send({
            "email": "quinncarroll810@gmail.com"
        })
        expect(res.statusCode).toBe(200)
    })

    test("Sending a meeting request for an invalid user", async () => {
        const res = await request(app).post("/sendMeetingRequest").send({
            "email": "johnwick@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })


})

// Test for sending meeting response notification
describe("POST /sendMeetingResponse", () => {
    test("Sending a meeting response for a valid user", async () => {
        const res = await request(app).post("/sendMeetingResponse").send({
            "email": "quinncarroll810@gmail.com"
        })
        expect(res.statusCode).toBe(200)
    })

    test("Sending a meeting response for an invalid user", async () => {
        const res = await request(app).post("/sendMeetingResponse").send({
            "email": "johnwick@gmail.com"
        })
        expect(res.statusCode).toBe(400)
    })
})

// Tests for creating meeting requests
describe("POST /meetings", () => {
    test("From an existing user to a mentor who is an existing user", async () => {
        const res = await request(app).post("/sendMeetingResponse").send({
            "meetingLogs": [],
            "menteeEmail": "vijeethvp@gmail.com",
            "mentorEmail": "mentor@gmail.com",
            "paymentAmount": 5,
            "status": "PENDING",
            "mId": "12345"
        })
        expect(res.statusCode).toBe(200)
    })

 
})

// Tests for getting all meetings for a user
describe("GET /meetings/email", () => {
    test("Get meetings for a valid user", async () => {
        const res = await request(app).post("/meetings/quinncarroll810@gmail.com").send({})
        expect(res.statusCode).toBe(200)
    })

    test("Get meetings for an invalid user", async () => {
        const res = await request(app).post("/meetings/johnwick@gmail.com").send({})
        expect(res.statusCode).toBe(400)
    })
})

// Tests for getting all meetings for a user
describe("GET /meetings/email", () => {
    test("Get meetings for a valid user", async () => {
        const res = await request(app).post("/meetings/quinncarroll810@gmail.com").send({})
        expect(res.statusCode).toBe(200)
    })

    test("Get meetings for an invalid user", async () => {
        const res = await request(app).post("/meetings/johnwick@gmail.com").send({})
        expect(res.statusCode).toBe(400)
    })
})

// Tests for getting all meetingsById
describe("GET /meetingsById", () => {
    test("Get meetingsById for a valid user", async () => {
        const res = await request(app).post("/meetingsById").send({
            "mId": "12345"
        })
        expect(res.statusCode).toBe(200)
    })

    test("Get meetingsById for an invalid user", async () => {
        const res = await request(app).post("/meetingsById").send({
            "mId": "invalid"
        })
        expect(res.statusCode).toBe(400)
    })
})

// Tests for updating meeting status
describe("PUT /meetings", () => {
    test("Update meeting status with a valid status and meeting ID", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": "12345",
            "status": "declined"
        })
        expect(res.statusCode).toBe(200)
    })

    test("Update meeting status with an invalid status", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": "12345",
            "status": "invalid"
        })
        expect(res.statusCode).toBe(200)
    })

    test("Update meeting status with an invalid mId", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": "invalid",
            "status": "declined"
        })
        expect(res.statusCode).toBe(200)
    })

    test("Update meeting status with a mId", async () => {
        const res = await request(app).put("/meetings").send({
            "mId": "invalid",
            "status": "declined"
        })
        expect(res.statusCode).toBe(200)
    })
})

// Tests for updating meeting logs