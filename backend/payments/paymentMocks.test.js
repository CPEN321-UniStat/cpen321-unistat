jest.mock("./paymentHandlers"); 

const payment = require("./paymentHandlers")

/* Mock implementaions */
payment.schedulePayment.mockImplementation((req, res) => {
  const jsonResp = {
      "status": `Payment scheduled at ${req.body.mEndTime}`
  }
  return JSON.stringify(jsonResp)
})

payment.getCoinsByUser.mockImplementation((req, res) => {
  const jsonResp = {
      "status": `Retrieved coins of user ${req.body.userEmail}`
  }
  return JSON.stringify(jsonResp)
})