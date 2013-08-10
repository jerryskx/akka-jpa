package utils.orm

import collection.{mutable => mutable}
import javax.persistence.{Persistence, EntityManagerFactory}

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/8/13
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
object JPA extends utils.logging.Logger {
  val emfMap = mutable.Map[String, EntityManagerFactory]()

  def emf(persistenceUnit:String) =  {
    emfMap.get(persistenceUnit).getOrElse({
      debug("register emf for  " + persistenceUnit)
      val emf = Persistence.createEntityManagerFactory(persistenceUnit)
      emfMap += persistenceUnit -> emf
      emf
    })
  }
  def em(persistenceUnit:String) = {
    emf(persistenceUnit).createEntityManager()
  }

  def shutdown() = {
    debug("shutdown all emf.")
    emfMap.values.foreach(emf => {
      try{emf.close()}
      catch{case e:Throwable => error("Failed to close EntityManagerFactory " + emf, e)}
    })
  }
}
