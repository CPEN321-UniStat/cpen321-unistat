const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client
const { changeTesting } = require('../../meetings/meetingHandlers')


const meetingID = 12345;
const { handlePayment } = require('../../payments/paymentHandlers')
jest.mock('node-schedule');
const schedule = require('node-schedule');

schedule.scheduleJob.mockImplementation(() => {
    handlePayment(meetingID)
})

beforeAll( () => {
    changeTesting();
})

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

// Tests for updating meeting logs
describe("PUT /updateMeetingLog", () => {
    test("Update meeting logs with valid mId", async () => {
        await process.nextTick(() => { });
        var res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:00:00",
                "userEmail": "kusharora339@gmail.com",
                "isMentor": true,
                "action": "JOINED"
            }
        })
        expect(res.statusCode).toBe(200)

        res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:01:00",
                "userEmail": "manekgujral11@gmail.com",
                "isMentor": false,
                "action": "JOINED"
            }
        })
        expect(res.statusCode).toBe(200)

        res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:55:00",
                "userEmail": "manekgujral11@gmail.com",
                "isMentor": false,
                "action": "LEFT"
            }
        })
        expect(res.statusCode).toBe(200)

        res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:58:00",
                "userEmail": "kusharora339@gmail.com",
                "isMentor": true,
                "action": "LEFT"
            }
        })
        expect(res.statusCode).toBe(200)
    })

    test("Update meeting logs with invalid mId", async () => {
        const res = await request(app).put("/updateMeetingLog").send({
            "mId": "invalid",
            "meetingLog": {
                "timestamp": "2022-07-09T11:00:00",
                "userEmail": "manekgujral11@gmail.com",
                "isMentor": true,
                "action": "JOINED"
            }
        })
        expect(res.statusCode).toBe(400)
    })
})

// // // Tests for createZoomMeeting
describe("POST /createZoomMeeting", () => {
    test("Creates a Zoom meeting", async () => {
        await process.nextTick(() => { });
        const res = await request(app).post("/createZoomMeeting").send({
            "meetingTopic": "Test Meeting",
            "meetingStartTime": "2022-08-11'T'11:05:00",
            "meetingEndTime": "2022-08-11'T'12:05:00",
            "mId": meetingID
        })
        await process.nextTick(() => { });
        expect(res.statusCode).toBe(200)
    })
})
