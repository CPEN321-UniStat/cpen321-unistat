jest.mock("../paymentHandlers"); 

const payment = require("../paymentHandlers")

/* Mock implementaions */
payment.schedulePayment.mockImplementation((req, res) => {
  const jsonResp = {
      "status": `Payment scheduled at ${req.body.mEndTime}`
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