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
        res.send(result); // send back all stats
    }
    )
})

app.get("/statsByFilter", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({ [Object.keys(req.body)[0]] : Object.values(req.body)[0] }).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        res.send(result); // send back all stats with filter applied
    }
    )
})

app.get("/statsBySorting", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({}).sort([Object.keys(req.body)[0]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        res.send(result.reverse()); // send back all stats sorted applied
    }
    )
})  

app.get("/statsByConfiguration", async (req, res) => {
    client.db("UniStatDB").collection("Stats").find({[Object.keys(req.body)[0]] : Object.values(req.body)[0]}).sort([Object.keys(req.body)[1]]).toArray(function(err, result) {
        if (err){
            console.log(error)
            res.status(400).send(JSON.stringify(error))
        }
        res.send(result.reverse()); // send back all stats sorted by filter applied
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

async function storeGoogleUserData(idToken) {
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
