const { MongoClient } = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)

async function connect() {
    try {
        await client.connect();
        console.log("successfully connected to database!");
    } catch (error) {
        console.log(error);
        await client.close();
    }
}

module.exports = {
    connect,
    client
}