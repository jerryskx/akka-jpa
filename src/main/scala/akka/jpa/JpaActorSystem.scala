package akka.jpa

import akka.actor.{ActorRef, Props, ActorSystem}
//import play.api.Logger
import javax.persistence.EntityManager
import collection.{mutable => mutable}

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/2/12
 * Time: 4:56 PM
 *
 *
 * References:
 * http://doc.akka.io/docs/akka/snapshot/scala/actors.html
 */


object JpaActorSystem extends utils.logging.Logger {

  val STOP_JPA_ACTOR = "Attack ships on fire off the shoulder of Orion."
  val RESTART_JPA_ACTOR = "Don't you die on me!"
  val REFRESH_EM = "Reload EM"

  val DISPATCHER = "akkajpa.dispatcher"
  val system = ActorSystem("JpaActorSystem")

  debug("In JpaActorSystem")

  val supervisors = mutable.Map[String, ActorRef]()

  def apply(persistenceUnit:String) = {
    supervisors.get(persistenceUnit).getOrElse({
      // create JpaActorSupervisor Actor at system (top) level, using "akkajpa.dispatcher" config
      val supervisor = JpaActorSystem.system.actorOf(Props(classOf[JpaActorSupervisor], persistenceUnit)
//      val supervisor = JpaActorSystem.system.actorOf(Props(new JpaActorSupervisor(persistenceUnit))
        .withDispatcher(DISPATCHER), name = "jpaActorSupervisor")
      supervisors += persistenceUnit -> supervisor
      supervisor
    })
  }
  def shutdown() = {
    system.shutdown()
    supervisors.clear()
  }
}

case class Create(obj: AnyRef)
case class Read[T](clazz: Class[T], id: Any)
case class Update(obj: AnyRef)
case class Delete(obj: AnyRef)
case class Query(query: String, hints:QueryConfig, params: (String, Any)*)
case class Transaction(f:EntityManager=>Any)

case class QueryConfig(cacheMode:Option[String]=None, readOnly:Option[Boolean] = None, fetchSize:Option[Int] = None, maxResults:Option[Int]=None)
