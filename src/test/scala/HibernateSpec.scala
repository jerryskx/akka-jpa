import anorm._
import models.entity.OrderLog
import org.hibernate.cfg.Configuration
import org.hibernate.SessionFactory
import org.specs2.mutable.Specification
import services.JPA
import utils.db.DB

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/6/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
class HibernateSpec  extends Specification {
//  sequential // to run test sequentially

  step {
    DB.registerDs("playjpa")
//    DB.startup
  }

  "HB" should {
    "find by ID" in {
      val session = utils.orm.HB.session()
      session.beginTransaction()

      val ord = session.get(classOf[OrderLog], new java.lang.Integer(1000000)).asInstanceOf[OrderLog]

      session.close()
      Option(ord) must beSome[OrderLog]
    }
  }

  step {
    utils.orm.HB.shutdown()
    DB.shutdown()
  }

}
