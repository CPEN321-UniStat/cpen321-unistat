const request = require('supertest')
const {app, server} = require('../../server')
const db = require("../../database/connect")
const client = db.client


afterAll( () => {
    // Close the server instance after each test
    server.close()
    client.close()
})

describe("View Statistics use case", () => {
    
    describe("test for getting all stats", () => {

        test("should return a json response with status code 200", async () => {
            const res = await request(app).get("/stats")
            expect(res.statusCode).toBe(200)
        })

    })

    describe("tests for filtering stats", () => {

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
    
    describe('tests for sorting stats', () => { 

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

    describe('tests for filtering and sorting stats', () => {

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
})