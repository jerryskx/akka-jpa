package utils.orm

import scala.collection.{mutable => mutable}
import org.hibernate.{Session, SessionFactory}
import org.hibernate.cfg.Configuration

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/8/13
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
object HB extends utils.logging.Logger {
  val sessionFactories = mutable.Map[String, SessionFactory]()    // SessionFactory Map

  val DEFAULT = "hibernate"

  def apply():SessionFactory = apply(DEFAULT)
  def apply(cfg:String):SessionFactory = sessionFactory(cfg)

  def sessionFactory(cfg:String):SessionFactory = {
    sessionFactories.get(cfg).getOrElse({
      val cfgFile = "/" + cfg + ".cfg.xml"
      val sessionFactory = new Configuration()
        .configure(cfgFile) // configures settings from hibernate.cfg.xml
        .buildSessionFactory()
      sessionFactories += cfg -> sessionFactory
      sessionFactory
    })
  }
  def session():Session = sessionFactory(DEFAULT).getCurrentSession
  def session(cfg:String):Session = sessionFactory(cfg).getCurrentSession


  def shutdown() = {
    sessionFactories.values.foreach(sf => {
      try{sf.close()}
      catch{case e:Throwable => error("Failed to close SessionFactory " + sf, e)}
    })
    sessionFactories.clear()
  }
}
