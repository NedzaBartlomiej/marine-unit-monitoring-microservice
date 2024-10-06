print("#### MONGO-INIT.JS ####");

print("Creating new database: adminServiceDB.");
db = new Mongo().getDB("adminServiceDB");

// ADMINS COLLECTION
print("-- Executing init operations for: admins collection. --");

print("Creating users collection.");
db.createCollection('admins');

print("Inserting init-temp document.");
db.users.insertOne({temp: "temp"});

print("Creating unique index for an email field.");
db.users.createIndex({email: 1}, {unique: true})

print("Creating unique index for a login field.");
db.users.createIndex({login: 1}, {unique: true})

print("Deleting init-temp document.");
db.users.deleteOne({temp: "temp"});