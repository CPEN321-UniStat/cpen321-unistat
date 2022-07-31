// Mock the whole meetings module

// The following line automatically assigns jest.fn() to each module export, mocking all functions.
jest.mock("../meetingHandlers"); 

const meetings = require("../meetingHandlers")

const sampleMeetings = [
        {
            "meetingLogs": [],
            "menteeEmail": "vijeethvp@gmail.com",
            "mentorEmail": "mentor@gmail.com",
            "paymentAmount": 5,
            "status": "PENDING",
            "mColor": 0,
            "mEndTime": {
              "year": 2022,
              "month": 6,
              "dayOfMonth": 27,
              "hourOfDay": 12,
              "minute": 27,
              "second": 56
            },
            "mId": 12345,
            "mName": "Integration Meeting Test",
            "mStartTime": {
              "year": 2022,
              "month": 6,
              "dayOfMonth": 27,
              "hourOfDay": 11,
              "minute": 27,
              "second": 56
            }
          }
    ]

/********* Mock implementaions ****************/
// createMeetingRequest()
meetings.createMeetingRequest.mockImplementation((req, res) => {
    const jsonResp = {
        "status": `Meeting request inputted by ${req.body.menteeEmail}`
    }
    return JSON.stringify(jsonResp)
})

// getMeetingByEmail()
meetings.getMeetingByEmail.mockImplementation((req, res) => {
    const jsonResp = {"meetings" : sampleMeetings}
    return JSON.stringify(jsonResp)
})

// getMeetingById()
meetings.getMeetingById.mockImplementation((req, res) => {
    const jsonResp = {"meeting" : sampleMeetings}
    return JSON.stringify(jsonResp)
})

// respondToMeeting()
meetings.respondToMeeting.mockImplementation((req, res) => {
    const jsonResp = {
        "status": `Meeting status updated`
    }
    return JSON.stringify(jsonResp)
})

// updateMeetingLog()
meetings.updateMeetingLog.mockImplementation((req, res) => {
    var jsonResp = {
        "status": `Meeting logs updated for meeting ID: ${req.body.mId}`
    }
    return JSON.stringify(jsonResp)
})

// createZoomMeeting()
meetings.createZoomMeeting.mockImplementation((req, res) => {
    const jsonResp = {
        "status": {
            "id": "123",
            "password": "456"
        }
    }
    return JSON.stringify(jsonResp)
})

// updateFirbaseToken()
meetings.updateFirbaseToken.mockImplementation((req, res) => {
    var jsonResp = {
        "status": `Firebase Token updated for ${req.body.email}`
    }
    return JSON.stringify(jsonResp)
})


/********* Test Suite ************************/
// test('createMeetingRequest test', () => {
//     const req = {
//         "body": {
//             "menteeEmail": "email"
//         }
//     }
//     const res = {}
//     const expected = {
//         "status": `Meeting request inputted by ${req.body.menteeEmail}`
//     }
//     const jsonExpected = JSON.stringify(expected)
//     expect(meetings.createMeetingRequest(req, res)).toBe(jsonExpected);
// })

// test('getMeetingByEmail test', () => {
//     const req = {}
//     const res = {}
//     const expected = {
//         "meetings" : sampleMeetings
//     }
//     const jsonExpected = JSON.stringify(expected)
//     expect(meetings.getMeetingByEmail(req, res)).toBe(jsonExpected);
// })

// test('getMeetingById test', () => {
//     const req = {}
//     const res = {}
//     const expected = {
//         "meeting" : sampleMeetings
//     }
//     const jsonExpected = JSON.stringify(expected)
//     expect(meetings.getMeetingById(req, res)).toBe(jsonExpected);
// })

// test('respondToMeeting test', () => {
//     const req = {}
//     const res = {}
//     const expected = {
//         "status": `Meeting status updated`
//     }
//     const jsonExpected = JSON.stringify(expected)
//     expect(meetings.respondToMeeting(req, res)).toBe(jsonExpected);
// })

// test('updateMeetingLog test', () => {
//     const req = {        
//         "body": {
//             "mId": "123"
//         }
//     }
//     const res = {}
//     const expected = {
//         "status": `Meeting logs updated for meeting ID: ${req.body.mId}`
//     }
//     const jsonExpected = JSON.stringify(expected)
//     expect(meetings.updateMeetingLog(req, res)).toBe(jsonExpected);
// })

// test('updateFirbaseToken test', () => {
//     const req = {
//         "body": {
//             "email": "email"
//         }
//     }
//     const res = {}
//     const expected = {
//         "status": `Firebase Token updated for ${req.body.email}`
//     }
//     const jsonExpected = JSON.stringify(expected)
//     expect(meetings.updateFirbaseToken(req, res)).toBe(jsonExpected);
// })

// test('createZoomMeeting test', () => {
//     const req = {}
//     const res = {}
//     const expected = {
//         "status": {
//             "id": "123",
//             "password": "456"
//         }
//     }
//     const jsonExpected = JSON.stringify(expected)
//     expect(meetings.createZoomMeeting(req, res)).toBe(jsonExpected);
// })