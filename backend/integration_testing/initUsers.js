const axios = require("axios");
const dotEnv = require("dotenv");
var admin = require("firebase-admin");

dotEnv.config()

const initializeUsers = async () => { 
    await process.nextTick(() => { });
    var mentorAccount = await axios.post(`https://oauth2.googleapis.com/token?client_id=${process.env.GOOGLE_CLIENT_ID}&client_secret=${process.env.GOOGLE_CLIENT_SECRET}&grant_type=refresh_token&refresh_token=${process.env.REFRESH_TOKEN_KUSH}`)
    await process.nextTick(() => { });
    var menteeAccount = await axios.post(`https://oauth2.googleapis.com/token?client_id=${process.env.GOOGLE_CLIENT_ID}&client_secret=${process.env.GOOGLE_CLIENT_SECRET}&grant_type=refresh_token&refresh_token=${process.env.REFRESH_TOKEN_MANEK}`)
   await process.nextTick(() => { });
    var testAccount = await axios.post(`https://oauth2.googleapis.com/token?client_id=${process.env.GOOGLE_CLIENT_ID}&client_secret=${process.env.GOOGLE_CLIENT_SECRET}&grant_type=refresh_token&refresh_token=${process.env.TEST_ACC_REFRESH_TOKEN}`)

    var mentorIdToken = mentorAccount.data.id_token
    var menteeIdToken = menteeAccount.data.id_token
    var testAccIdToken = testAccount.data.id_token
    // var fbToken = initUserFbToken()
    return [mentorIdToken, menteeIdToken, testAccIdToken]
}

const initUserFbToken = async () => {
    const uid = '106089786222161579479';

    var token = await admin.auth().createCustomToken(uid)

    return token

    // admin.auth()
    // .createCustomToken(uid)
    // .then((customToken) => {
    //     return customToken
    // })
    // .catch((error) => {
    //     console.log('Error creating custom token:', error);
    // });
}

module.exports = {
    initializeUsers,
    initUserFbToken
}
