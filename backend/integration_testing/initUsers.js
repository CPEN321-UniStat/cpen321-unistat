const axios = require("axios");
const dotEnv = require("dotenv");
dotEnv.config()

const initializeUsers = async () => { 
    var mentorAccount = await axios.post(`https://oauth2.googleapis.com/token?client_id=${process.env.GOOGLE_CLIENT_ID}&client_secret=${process.env.GOOGLE_CLIENT_SECRET}&grant_type=refresh_token&refresh_token=${process.env.REFRESH_TOKEN_KUSH}`)
    var menteeAccount = await axios.post(`https://oauth2.googleapis.com/token?client_id=${process.env.GOOGLE_CLIENT_ID}&client_secret=${process.env.GOOGLE_CLIENT_SECRET}&grant_type=refresh_token&refresh_token=${process.env.REFRESH_TOKEN_MANEK}`)

    var mentorIdToken = mentorAccount.data.id_token
    var menteeIdToken = menteeAccount.data.id_token

    return [mentorIdToken, menteeIdToken]
}

module.exports = {
    initializeUsers
}