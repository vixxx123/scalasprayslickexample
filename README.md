# Welcome to rest scala/spray/akka/slick/mysql example #

I have created this project during my research on scala/spray/akka/slick/mysql REST service.

It may be used to learn a bit on how to use those technologies to create REST service.
It's just a prove of concept though and it's not production ready. It definitely can be used as a good start.

Additionally websocket (<a href="https://github.com/wandoulabs/spray-websocket">https://github.com/wandoulabs/spray-websocket</a>) was added to project.
    All events in the REST system like create/update/delete of resource are publishing information to all connected clients via websocket.

The database underneath is mysql, but it can be easily switch to any other which is supported by slick.

### System requirements ###

* Mysql server - up and running
* Java with maven is installed
* That's it

### Configuration ###

* Set up user and database on mysql server
* Edit db.conf in resources to configure db connection

### Adding new REST API ###
* take a look at com.vixxx123.scalasprayslickexample.exampleapi.person.PersonApi as example
* create new routing class - which inherits from BaseResourceApi
    - method init should be used to initialize resource. It is run once on server start up. I use it to create db tables if they don't exists yet
    - method route - should define REST route for new API
* create new object/class which inherits from
    - create method should return new routing class (created in previous step)
* in RestExampleApi object add your Api class to list.
* that's it - you are good to go

PersonApi class is just an example you don't have to implement handling incoming request same way,
but I think it's quite good design. It give a possibility to configure number of workers per request type etc.

### Features ###
* Each type of resource and method can have different numbers of actors - easy to optimise performance
* Fully based on Akka
* Uses Slick for persistence - easy to switch between databases (at least it should be easy :) )
* Push messaging via websocket

### Road map ###
* Adding OAuth 2
* Publishing message via websocket to specific user only (based on open session and user id)

### How to run ###
* mvn clean install
* mvn scala:run -DmainClass=com.vixxx123.scalasprayslickexample.RestExampleApp


### Have fun ###
