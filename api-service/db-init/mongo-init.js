print("#### MONGO-INIT.JS ####");

print("Creating new database: apiServiceDB.");
db = new Mongo().getDB("apiServiceDB");


// USERS COLLECTION
print("-- Executing init operations for: users collection. --");

print("Creating users collection.");
db.createCollection('users');

// SHIP TRACKS COLLECTION
print("-- Executing init operations for: shipTracks collection. --");

print("Creating shipTracks collection.");
db.createCollection('shipTracks');