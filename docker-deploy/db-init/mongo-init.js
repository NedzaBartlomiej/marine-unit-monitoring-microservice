print("#### MONGO-INIT.JS ####"); // todo separated files for every service and execute here as main init file

print("Creating new database: apiServiceDB.");
db = new Mongo().getDB("apiServiceDB");


// USERS COLLECTION
print("-- Executing init operations for: users collection. --");

print("Creating users collection.");
db.createCollection('users');

print("Inserting init-temp document.");
db.users.insertOne({temp: "temp"});

print("Creating unique index for email field.");
db.users.createIndex({email: 1}, {unique: true})

print("Deleting init-temp document.");
db.users.deleteOne({temp: "temp"});


// SHIP TRACKS COLLECTION
print("-- Executing init operations for: shipTracks collection. --");

print("Creating shipTracks collection.");
db.createCollection('shipTracks');


// VERIFICATION TOKEN COLLECTION
print("-- Executing init operations for: verificationTokens collection. --");

print("Creating verificationTokens collection.");
db.createCollection('verificationTokens');


// JWT TOKENS COLLECTION
print("-- Executing init operations for: jwtTokens collection. --");

print("Creating jwtTokens collection.");
db.createCollection('jwtTokens');


// ACTIVE POINTS COLLECTION
print("-- Executing init operations for: activePoints collection. --");

print("Creating activePoints collection.");
db.createCollection('activePoints');