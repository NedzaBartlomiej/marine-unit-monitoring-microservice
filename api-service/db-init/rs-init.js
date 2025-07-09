print("#### RS.INIT.SH - apiServiceDBRS ####")

print("-- Initializing replica set. --");
rs.initiate({
    "_id": "apiServiceDBRS",
    "members": [
        {
          "_id": 0,
          "host": "api-service-mongodb-primary:27017"
        }
    ]
});