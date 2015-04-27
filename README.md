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

```scala
class CompanyApi(actorContext: ActorContext) extends BaseResourceApi with Logging {

  /**
   * Handler val names must be unique in the system - all
   */

  private val companyDao = new CompanyDb

  private val companyCreateHandler = actorContext.actorOf(RoundRobinPool(2).props(CreateActor.props(companyDao)), CreateActor.Name)
  private val companyPutHandler = actorContext.actorOf(RoundRobinPool(5).props(UpdateActor.props(companyDao)), UpdateActor.Name)
  private val companyGetHandler = actorContext.actorOf(RoundRobinPool(20).props(GetActor.props(companyDao)), GetActor.Name)
  private val companyDeleteHandler = actorContext.actorOf(RoundRobinPool(20).props(DeleteActor.props(companyDao)), DeleteActor.Name)

  override val logTag: String = getClass.getName

  override def init() = {
    companyDao.initTable()
    super.init()
  }

  override def route() =
    pathPrefix(ResourceName) {
      pathEnd {
        get {
          ctx => companyGetHandler ! GetMessage(ctx, None)
        } ~
        post {
          entity(as[Company]) {
            user =>
              ctx => companyCreateHandler ! CreateMessage(ctx, user)
          }
        }
      } ~
      pathPrefix (IntNumber){
        entityId => {
          pathEnd {
            get {
              ctx => companyGetHandler ! GetMessage(ctx, Some(entityId))
            } ~ put {
              entity(as[Company]) { entity =>
                ctx => companyPutHandler ! PutMessage(ctx, entity.copy(id = Some(entityId)))
              }
            } ~ delete {
              ctx => companyDeleteHandler ! DeleteMessage(ctx, entityId)
            } ~ patch {
              ctx => companyPutHandler ! PatchMessage(ctx, entityId)
            }
          }
        }
      }
    }
}

object CompanyApi extends Api{
  override def create(actorContext: ActorContext): BaseResourceApi = new CompanyApi(actorContext)
}

```

* create new routing class - which inherits from BaseResourceApi
    - method init should be used to initialize resource. It is run once on server start up. I use it to create db tables if they don't exists yet
    - method route - should define REST route for new API
* create new object/class which inherits from
    - create method should return new routing class (created in previous step)
* in RestExampleApi object add your Api class to list.

```scala
object RestExampleApp extends App{
  new Rest(ActorSystem("on-spray-can"), List(PersonApi, CompanyApi), List(new ConsoleLogger))
}
```

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
