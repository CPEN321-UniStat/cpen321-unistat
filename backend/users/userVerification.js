const {OAuth2Client} = require('google-auth-library');
const authClient = new OAuth2Client("572477064370-885bs334uv17fhubimtof6su24mf0pp8.apps.googleusercontent.com");

const userVerifier = async (idToken) => {
    const ticket = await authClient.verifyIdToken({
        idToken: idToken,
        audience: ["572477064370-885bs334uv17fhubimtof6su24mf0pp8.apps.googleusercontent.com"]  // Specify the CLIENT_ID of the app that accesses the backend
        // Or, if multiple clients access the backend:
        //[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
    });
    const response = ticket.getPayload();
    return response
}

module.exports = {
    userVerifier
}