print("#### MONGO-INIT.JS ####");

print("Creating new database: devServiceDB.");
db = new Mongo().getDB("devServiceDB");

// DEVELOPERS COLLECTIONS
print("-- Executing init operations for: developers collection. --");

print("Creating users collection.");
db.createCollection('developers');

print("Inserting init-temp document.");
db.users.insertOne({temp: "temp"});

print("Creating unique index for an email field.");
db.users.createIndex({email: 1}, {unique: true})

print("Creating unique index for an username field.");
db.users.createIndex({username: 1}, {unique: true})

print("Deleting init-temp document.");
db.users.deleteOne({temp: "temp"});

// APPLICATIONS COLLECTION
print("-- Executing init operations for: applications collection. --");

print("Creating users collection.");
db.createCollection('applications');

print("Inserting init-temp document.");
db.users.insertOne({temp: "temp"});

print("Creating unique index for an name field.");
db.users.createIndex({name: 1}, {unique: true})

print("Creating unique index for an opaqueToken field.");
db.users.createIndex({opaqueToken: 1}, {unique: true})

print("Deleting init-temp document.");
db.users.deleteOne({temp: "temp"});