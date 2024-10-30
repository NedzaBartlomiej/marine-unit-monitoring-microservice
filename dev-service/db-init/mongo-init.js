print("#### MONGO-INIT.JS ####");

print("Creating new database: devServiceDB.");
db = new Mongo().getDB("devServiceDB");

// DEVELOPERS COLLECTIONS
print("-- Executing init operations for: developers collection. --");

print("Creating users collection.");
db.createCollection('developers');

print("Inserting init-temp document.");
db.developers.insertOne({temp: "temp"});

print("Creating unique index for an email field.");
db.developers.createIndex({email: 1}, {unique: true})

print("Deleting init-temp document.");
db.developers.deleteOne({temp: "temp"});

// APPLICATIONS COLLECTION
print("-- Executing init operations for: applications collection. --");

print("Creating users collection.");
db.createCollection('applications');

print("Inserting init-temp document.");
db.applications.insertOne({temp: "temp"});

print("Creating unique index for an name field.");
db.applications.createIndex({name: 1}, {unique: true})

print("Creating unique index for an opaqueToken field.");
db.applications.createIndex({opaqueToken: 1}, {unique: true})

print("Deleting init-temp document.");
db.applications.deleteOne({temp: "temp"});