# xy-inc
Zup programming test
## _POIsFinder_ Project

This project is NOT recommended to be used in production, however it's a great project to play 
around with implementing, learning and discussing about a better RESTful architecture. 
It solves a simplification of the problem of finding points of interest.

## Dependencies

* [Scala] - Programming language.
* [SBT] (Simple Build Tool) - Build tool for Scala and Java projects.
* [Akka] - Asynchronous event-driven middleware framework implemented in Scala, for building high performance and reliable distributed applications. Akka decouples business logic from low-level mechanisms such as threads, locks and non-blocking IO.
* [Spray] - Scala framework for building RESTful web services on top of Akka: lightweight, asynchronous, non-blocking, actor-based, modular, testable.
* [Slick] - Database query and access library for Scala. It provides a toolkit to work with stored data almost as using Scala collections. Features an extensible query compiler which can generate code for different backends.
* [H2] - A in-memory database._This will be replaced soon, and replaced by MongoDB_.
* [Logback] - Fast and stable logging utility. Natively implements the SLF4J API.

## Download SBT

If you don't have a SBT in you system you can get it from a repository or run the following script to download and install it locally (version 0.13.11).

```
sh sbt_install.sh
```

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
sbt "test-only *PoiFinderSpec"
```

[Scala]: http://www.scala-lang.org
[SBT]: http://www.scala-sbt.org
[Akka]: http://akka.io 
[Spray]: http://spray.io 
[Slick]: http://slick.typesafe.com
[H2]: http://h2database.com/html/main.html
[Logback]: http://logback.qos.ch
