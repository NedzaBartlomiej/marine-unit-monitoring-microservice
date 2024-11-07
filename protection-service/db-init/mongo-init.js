print("#### MONGO-INIT.JS ####");

print("Creating new database: protectionServiceDB.");
db = new Mongo().getDB("protectionServiceDB");

// SUSPECT_LOGINS COLLECTION
print("-- Executing init operations for: suspectLogins collection. --");

print("Creating suspectLogins collection.");
db.createCollection('suspectLogins');