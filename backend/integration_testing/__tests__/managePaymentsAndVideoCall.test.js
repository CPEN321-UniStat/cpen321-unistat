const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client

afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

const meetingID = 12345;

// // // Tests for createZoomMeeting
// describe("POST /createZoomMeeting", () => {
//     test("Creates a Zoom meeting", async () => {
//         await process.nextTick(() => { });
//         const res = await request(app).post("/createZoomMeeting").send({
//             "meetingTopic": "Test Meeting",
//             "meetingStartTime": "2022-08-11'T'11:05:00",
//             "meetingEndTime": "2022-08-11'T'12:05:00",
//             "mId": meetingID
//         })
//         await process.nextTick(() => { });
//         expect(res.statusCode).toBe(200)
//     })
// })

// Tests for updating meeting logs
describe("PUT /updateMeetingLog", () => {
    test("Update meeting logs with valid mId", async () => {
        const res = await request(app).put("/updateMeetingLog").send({
            "mId": meetingID,
            "meetingLog": {
                "timestamp": "2022-07-09T11:00:00",
                "userEmail": "manekgujral11@gmail.com",
                "isMentor": true,
                "action": "JOINED"
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