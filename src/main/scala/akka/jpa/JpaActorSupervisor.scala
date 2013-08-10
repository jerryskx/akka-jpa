package akka.jpa

import scala.concurrent.duration._
import akka.actor._
import akka.routing.RoundRobinRouter
import akka.actor.SupervisorStrategy.{Restart, Stop}
import utils.logging.Logger
import concurrent.ExecutionContext.Implicits.global

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/2/12
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */

object JpaActorSupervisor {
  val DISPATCHER = "akkajpa.dispatcher"

  lazy val conf = com.typesafe.config.ConfigFactory.load()
  lazy val maxRestartPerMin = try { conf.getInt("akkajpa.maxRestartPerMin") } catch { case e: Throwable => 3}
  lazy val numberOfInstances = try { conf.getInt("akkajpa.numberOfInstances") } catch { case e: Throwable => 5}
}
class JpaActorSupervisor(persistenceUnit:String) extends Actor with Logger {
  var jpaActor: Option[ActorRef] = None

  // Try to restart the actor 'n' times within a minute, otherwise stop it.
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = JpaActorSupervisor.maxRestartPerMin,
    withinTimeRange = 1 minute) {

    case aie: ActorInitializationException => {
      error("Exception starting JpaActor: " + aie.toString)
      Stop
    }
    case e => {
      error("Exception in JpaActor: " + e.toString)
      Restart
    }
  }


  override def preStart() {
    debug("Starting JpaActorSupervisor")
    initActor()
  }

  override def postStop() {
    warn("Stopping JpaActorSupervisor")
    broadcast(JpaActorSystem.STOP_JPA_ACTOR)
  }

  def initActor() {
    debug("Initializing JpaActor")
    // Chapter 5.10 Routing Scala (Akka Documentation PDF Release 2.1 Page 232)

    // create routees with "supervisorStrategy" and use RR default dispatcher
    val router = RoundRobinRouter(nrOfInstances = JpaActorSupervisor.numberOfInstances, supervisorStrategy = supervisorStrategy)

    // create JpaActor with RoundRobinRouter and akkajpa.dispatcher
    val children = context.actorOf(
      Props(classOf[JpaActor], persistenceUnit).withRouter(router).withDispatcher(JpaActorSupervisor.DISPATCHER),
      name = "JpaActor")

    context.watch(children)  // watch over my children
    jpaActor = Some(children)
  }

  def broadcast(msg:Any) = for (i <- 1 to JpaActorSupervisor.numberOfInstances) jpaActor.map(_ ! msg)

  def receive = {
    case Terminated(actorRef) if Some(actorRef) == jpaActor => {    // children are gone (JPA Actors are all dead); revive them in a min
      debug("JpaActor ended")
      jpaActor = None
      context.system.scheduler.scheduleOnce(1 minute, self, JpaActorSystem.RESTART_JPA_ACTOR)
      debug("Scheduled restart of JpaActor")
    }

    case JpaActorSystem.RESTART_JPA_ACTOR => {   // receive restart request, that's revive my actors
      debug("Restarting JpaActor")
      initActor()
    }

    case JpaActorSystem.REFRESH_EM => {   // broadcast to all children to refresh their entity manager
      broadcast(JpaActorSystem.REFRESH_EM)
    }

    case it => {
      jpaActor match {
        case Some(child) => child forward it      // "it" will be the "message"
        case None => context.system.scheduler.scheduleOnce(1 minute, self, it)   // no child (actor) yet; wait for a min; hopefully, child will be back in a min
      }
    }
  }

}
