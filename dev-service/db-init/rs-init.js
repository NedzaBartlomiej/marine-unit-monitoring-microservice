print("#### RS.INIT.SH - devServiceDBRS ####");

print("-- Initializing replica set. --");
rs.initiate({
    "_id": "devServiceDBRS",
    "members": [
        {
          "_id": 0,
          "host": "dev-service-mongodb-primary:27017"
        },
    ]
});