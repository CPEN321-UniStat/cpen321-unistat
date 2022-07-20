const { MongoClient } = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)

const connect = async() => {
    await client.connect()
    console.log("successfully connected to database!")
}

module.exports = {
    connect,
    client
}