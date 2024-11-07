print("#### RS.INIT.SH - protectionServiceDBRS ####");

print("-- Initializing replica set. --");
rs.initiate({
    "_id": "protectionServiceDBRS",
    "members": [
        {
          "_id": 0,
          "host": "protection-service-mongodb-primary:27017"
        },
    ]
});