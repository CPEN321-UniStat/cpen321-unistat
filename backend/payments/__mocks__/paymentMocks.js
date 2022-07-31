jest.mock("../paymentHandlers"); 

const payment = require("../paymentHandlers")

/* Mock implementaions */
payment.schedulePayment.mockImplementation((req, res) => {
  const jsonResp = {
      "status": `Payment scheduled at ${req.body.mEndTime}`
  }
  return JSON.stringify(jsonResp)
})

payment.getUserCoins.mockImplementation(async (userEmail) => {
  return 100
})