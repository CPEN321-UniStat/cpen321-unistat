var express = require("express");
var app = express()

const { MongoClient } = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)

const axios = require("axios")

app.use(express.json());

app.get("/", (req, res) => {
    res.status(200).send("Server running...");
})

app.post("/users", async (req, res) => {
    try {
        var alreadyExists = await storeGoogleUserData(req.body.Token);
        console.log("exists: " + alreadyExists);
        var jsonResp = {
            "status": alreadyExists ? "loggedIn" : "signedUp"
        }
        res.status(200).send(JSON.stringify(jsonResp));
    } catch (error) {
        console.log(error)
        res.status(400).send(error)
    }
})

var server = app.listen(8081, (req, res) => {
    var host = server.address().address;
    var port = server.address().port;
    console.log(`server successfully running at http://${host}:${port}`);
})

async function run() {
    try {
        await client.connect();
        console.log("successfully connected to database!");
    } catch (error) {
        console.log(error);
        await client.close();
    }
}

async function storeGoogleUserData(idToken) {
    var res = false
    const response = await axios.get(`https://oauth2.googleapis.com/tokeninfo?id_token=${idToken}`)
    console.log(response.data)

    var existingUsers = client.db("UniStatDB").collection("Users").find({email: response.data.email}, {$exists: true})
    var lenUsers = (await existingUsers.toArray()).length

    if (lenUsers > 0) { // User already exists
        console.log("already exists")
    } else { // New user, so insert
        console.log("new user, signing up...")
        await client.db("UniStatDB").collection("Users").insertOne(response.data)
    }

    console.log("num existing users: ", lenUsers)
    return lenUsers > 0 ? true : false
}

run()
