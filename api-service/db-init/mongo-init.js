print("#### MONGO-INIT.JS ####");

print("Creating new database: apiServiceDB.");
db = new Mongo().getDB("apiServiceDB");


// USERS COLLECTION
print("-- Executing init operations for: users collection. --");

print("Creating users collection.");
db.createCollection('users');

print("Inserting init-temp document.");
db.users.insertOne({temp: "temp"});

print("Creating unique index for an email field.");
db.users.createIndex({email: 1}, {unique: true})

print("Creating unique index for an username field.");
db.users.createIndex({username: 1}, {unique: true})

print("Deleting init-temp document.");
db.users.deleteOne({temp: "temp"});


// SHIP TRACKS COLLECTION
print("-- Executing init operations for: shipTracks collection. --");

print("Creating shipTracks collection.");
db.createCollection('shipTracks');