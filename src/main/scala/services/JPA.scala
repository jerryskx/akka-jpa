package services

import scala.collection.{mutable => mutable}
import akka.util.Timeout
import akka.pattern.ask
import akka.jpa._
import javax.persistence.EntityManager

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/2/12
 * Time: 3:54 PM
 *
 */

object JPA {
  val jpaMap = mutable.Map[String, JPA]()    // JPA Service Map
  def apply():JPA = apply("default")
  def apply(persistenceUnit:String):JPA = {
    jpaMap.get(persistenceUnit).getOrElse({
      val jpaService = new JPA(persistenceUnit)
      jpaMap += persistenceUnit -> jpaService
      jpaService
    })
  }
}
class JPA(persistenceUnit:String) {
  implicit lazy val dur = 30 seconds
  implicit lazy val timeout = Timeout(dur)
  val jpaSupervisor: ActorRef = JpaActorSystem(persistenceUnit)

  def find[T <: AnyRef](id:Any)(implicit manifest: Manifest[T]): Option[T] =
    Await.result(jpaSupervisor ? Read(manifest.runtimeClass,id), dur).asInstanceOf[Option[T]]

  def persist(entity:AnyRef):Unit = Await.result(jpaSupervisor ? Create(entity), dur)

  def merge[T <: AnyRef](entity:T):T = Await.result(jpaSupervisor ? Update(entity), dur).asInstanceOf[T]

  def remove[T <: AnyRef](entity:T):Unit = Await.result(jpaSupervisor ? Delete(entity), dur)

  def query[T](hql:String, params: (String, Any)*): List[T] =
    query(hql, QueryConfig(), params: _*)

  def query[T](hql:String, config: QueryConfig, params: (String, Any)*): List[T] =
    Await.result(jpaSupervisor ? Query(hql, config, params: _*), dur).asInstanceOf[List[T]]

  def querySingleResult[T](hql:String, params: (String, Any)*): Option[T] =
    query[T](hql, QueryConfig(maxResults=Some(1)), params: _*).headOption

  def transaction(f:EntityManager => Any): AnyRef =
    Await.result(jpaSupervisor ? Transaction(f), dur).asInstanceOf[AnyRef]

}