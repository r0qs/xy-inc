# xy-inc
Zup programming test
## _POIsFinder_ Project

This project is NOT recommended to be used in production, however it's a great project to play 
around with implementing, learning and discussing about a better RESTFul architecture. 
It solves a simplification of the problem of finding points of interest.

## Run!

1. Start the application:

```
sbt run
```
2. Open a browser in: http://127.0.0.1:8080

3. Open a terminal and try:

### Create some Point of Interest (poi)

The id field is auto-incremented

```
curl -v -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/pois -d '{"id": -1, "name": "Pastelaria", "x": 32, "y": 34}'
```

### Delete a poi
```
curl -v -X DELETE http://127.0.0.1:8080/pois/ID_NUMBER
```

### Get all points

```
curl -v http://127.0.0.1:8080/pois/all
```

### Get a poi by id

```
curl -v http://127.0.0.1:8080/pois/ID_NUMBER
```

### Get a list of nearest POIs from given coordinates (x, y)

```
curl -G -v http://127.0.0.1:8080/pois/nearest -d x=20 -d y=10 -d dmax=10
```
## Run Test Suite
```
sbt test
```
or run tests individually:

### DAO Tests
```
sbt "test-only *PoiDAOSuite"
```

### Service Tests
```
sbt "test-only *PoiServiceSpec"
```
