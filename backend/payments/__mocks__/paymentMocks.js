jest.mock("../paymentHandlers"); 

const payment = require("../paymentHandlers")

/* Mock implementaions */
payment.schedulePayment.mockImplementation((mEndTime, mId) => {
  console.log("MOCKING")
  const jsonResp = {
      "status": `Payment scheduled at ${mEndTime}`
  }
  return JSON.stringify(jsonResp)
})

payment.getUserCoins.mockImplementationOnce(async (userEmail) => {
  console.log("MOCKING")
  throw "not found in db error"
})
payment.getUserCoins.mockImplementationOnce(async (userEmail) => {
  console.log("MOCKING")
  return 101
})
payment.getUserCoins.mockImplementationOnce(async (userEmail) => {
  console.log("MOCKING")
  throw "not found in db error"
})
payment.getUserCoins.mockImplementation(async (userEmail) => {
  console.log("MOCKING")
  return 101
})