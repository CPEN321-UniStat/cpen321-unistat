var express = require("express");
var app = express()

const { MongoClient, MongoNetworkError } = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)

const axios = require("axios")

// set up firebase authentication for notifications
var admin = require("firebase-admin");

var serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});


app.use(express.json());


app.put("/firebaseToken", async (req, res) => {
    // Update firebase_token data
    try {
        await client.db("UniStatDB").collection("Users").updateOne({email : req.body.email}, {$set: req.body})
        var jsonResp = {
            "status": `Firebase Token updated for ${req.body.email}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
})

app.post("/sendMeetingRequest", async (req, res) => {

    //email of person you are sending request to
    try {
        const curUser = await client.db("UniStatDB").collection("Users").find({ email : req.body.email }) //mentor email
        var curToken = (await curUser.toArray())[0].firebase_token
    } catch (error) {
        console.log(error)
    }

    var payload = {
        notification: {
            title: "UniStat",
            body: "Someone requested a meeting with you!",
        },
    }
    
    var options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    }

    admin.messaging().sendToDevice(curToken, payload, options)
    .then(function(response) {
        console.log("Successfully sent message:", response);
        var jsonResp = {"res" : "Successfully sent notification"}
        res.send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    })
    .catch(function(error) {
        console.log("Error sending message:", error);
    })

})

app.post("/sendMeetingResponse", async (req, res) => {

    //email of person you are responding to
    try {
        const curUser = await client.db("UniStatDB").collection("Users").find({ email : req.body.email })
        var curToken = (await curUser.toArray())[0].firebase_token
    } catch (error) {
        console.log(error)
    }

    var payload = {
        notification: {
            title: "UniStat",
            body: "Someone responded to your meeting request!",
        },
    }
    
    var options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    }

    admin.messaging().sendToDevice(curToken, payload, options)
    .then(function(response) {
        console.log("Successfully sent message:", response);
        var jsonResp = {"res" : "Successfully sent notification"}
        res.send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    })
    .catch(function(error) {
        console.log("Error sending message:", error);
    })

})


app.get("/", (req, res) => {
    res.status(200).send("Server running...");
})

app.post("/users", async (req, res) => {
    try {
        var alreadyExists = await storeGoogleUserData(req.body.Token, req.body.firebase_token);
        console.log("exists: " + alreadyExists);
        var jsonResp = {
            "status": alreadyExists ? "loggedIn" : "signedUp"
        }
        res.send(JSON.stringify(jsonResp));
    } catch (error) {
        console.log(error)
        res.status(400).send(error)
    }
})

//CRUD function for Stats collection
app.post("/stats", async (req, res) => {
    // Store stat data
    try {
        await client.db("UniStatDB").collection("Stats").insertOne(req.body)
        var jsonResp = {
            "status": `Stat stored for ${req.body.userEmail}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
})

app.get("/stats", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({}).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
})

app.post("/statsByFilter", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({ [Object.keys(req.body)[0]] : Object.values(req.body)[0] }).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result}
        res.send(JSON.stringify(jsonResp)); // send back all stats with filter applied
    }
    )
})

app.post("/statsBySorting", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({}).sort([Object.keys(req.body)[0]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.send(JSON.stringify(jsonResp)); // send back all stats sorted applied
    }
    )
})  

app.post("/statsByConfiguration", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({[Object.keys(req.body)[0]] : Object.values(req.body)[0]}).sort([Object.keys(req.body)[1]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        var jsonResp = {"statData" : result.reverse()}
        res.send(JSON.stringify(jsonResp)); // send back all stats sorted by filter applied
    }
    )
})  

app.put("/stats", async (req, res) => {
    // Update stat data
    try {
        await client.db("UniStatDB").collection("Stats").updateOne({userEmail : req.body.userEmail}, {$set: req.body})
        var jsonResp = {
            "status": `Stat updated for ${req.body.userEmail}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
    }
})

app.delete("/stats", async (req, res) => {
    // Delete stat data
    try {
        await client.db("UniStatDB").collection("Stats").deleteOne({userEmail : req.body.userEmail})
        var jsonResp = {
            "status": `Stat deleted for ${req.body.userEmail}`
        }
        res.status(200).send(JSON.stringify(jsonResp))
    } catch (error) {
        console.log(error)
        res.status(400).send(JSON.stringify(error))
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

async function storeGoogleUserData(idToken, fb_token) {
    var response = await axios.get(`https://oauth2.googleapis.com/tokeninfo?id_token=${idToken}`)
    response.data.firebase_token = fb_token
    console.log(fb_token)

    var existingUsers = client.db("UniStatDB").collection("Users").find({email: response.data.email}, {$exists: true})
    var lenUsers = (await existingUsers.toArray()).length

    if (lenUsers > 0) { // User already exists
        console.log("already exists")
        await client.db("UniStatDB").collection("Users").updateOne({email : response.data.email}, {$set: {"firebase_token": fb_token}})
    } else { // New user, so insert
        console.log("new user, signing up...")
        await client.db("UniStatDB").collection("Users").insertOne(response.data)
    }

    console.log("num existing users: ", lenUsers)
    return lenUsers > 0 ? true : false
}


run()
