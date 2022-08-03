const {OAuth2Client} = require('google-auth-library')
const authClient = new OAuth2Client(process.env.GOOGLE_CLIENT_ID)

const userVerifier = async (idToken) => {
    const ticket = await authClient.verifyIdToken({
        idToken,
        audience: [process.env.GOOGLE_CLIENT_ID]  // Specify the CLIENT_ID of the app that accesses the backend
        // Or, if multiple clients access the backend:
        //[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
    });
    const response = ticket.getPayload();
    return response
}

module.exports = {
    userVerifier
}