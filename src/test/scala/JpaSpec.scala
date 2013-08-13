import javax.persistence.{Persistence, EntityManagerFactory}
import models.entity.{OrderItem, OrderLog}
import org.specs2.mutable.Specification
import utils.db.DB
import services.JPA


/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/8/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
class JpaSpec  extends Specification {
  sequential // to run test sequentially

  val jpaService = JPA("playjpaPersistenceUnit")
  val TEST_USER_FIRST_NAME = "Tom"
  val TEST_UPD_ADDR = "TEST UPDATE addr1 2"

  var ordId:Int = _

  step {
//    println("startup in jpa test")
    DB.registerDs("playjpa")  // creates datasource and register it
//    DB.startup
  }

  "JPA" should {

    "creat EntityManager" in {
      val em = utils.orm.JPA.em("playjpaPersistenceUnit")
      em.find(classOf[OrderLog], 1000000).asInstanceOf[OrderLog]

      val ord =  em.find(classOf[OrderLog], 1000000).asInstanceOf[OrderLog]
      em.clear()
      em.close()
      Option(ord) must beSome[OrderLog]
    }
    "find Entity by ID" in {
      jpaService.find[OrderLog](1000000) must beSome[OrderLog]
    }
    "run DB transaction" in {
      val order:OrderLog = jpaService.transaction{em =>
        val entity = em.find(classOf[OrderLog], 1000000)
        org.hibernate.Hibernate.initialize(entity.orderItems)
        entity
      }.asInstanceOf[OrderLog]
      order.orderItems.size() must be_>=(1)
    }
    "be able to run query" in {
      // query with single result
      jpaService.querySingleResult[OrderLog]("from OrderLog order by order_id desc ") must beSome[OrderLog]

      // query with result list
      jpaService.query[OrderLog]("from OrderLog order by order_id asc ").size must be_>=(1)

      // test OptionString
      jpaService.query[OrderLog]("from OrderLog as o where o.shiptoAddress2 is not null").foreach(ord => {
        ord.shiptoAddress2 must beSome[String]
      })

      // test query with param
      jpaService.query[OrderLog]("from OrderLog as o where o.shiptoFirstName =:firstname and o.shiptoLastName = :lastname",
        ("firstname" -> "Jerry"),("lastname" -> "Wang"))
        .size must be_>=(1)
    }

    "be able to persist entity" in {
      println("insert")
      val ord1 = OrderLog(TEST_USER_FIRST_NAME, "Hanks", "Hollywood 1", None, "Hollywood", "CA", "91234")
      ord1.addItem(OrderItem("123123123", 20.99f))
      jpaService.persist(ord1)

//      sleep()
      ordId = ord1.id
//      println("ordId is " + ordId)
//      jpaService.find[OrderLog](ordId) must beSome[OrderLog]
      findCustomer(ord1.id) must beTrue
    }

    "be able to update entity" in {
      println("update")
      import scala.collection.JavaConversions._
//      val ord = jpaService.querySingleResult[OrderLog]("from OrderLog as o where o.shiptoFirstName = :firstname", ("firstname" -> TEST_USER_FIRST_NAME))
      val ord = jpaService.find[OrderLog](ordId)
      ord must beSome[OrderLog]
      ord.map(ord => {
        ord.shiptoAddress1 = "TEST UPDATE addr1 2"
        ord.orderItems.foreach (oi => oi.price = oi.price + 1)
        jpaService.merge(ord)
      })

//      sleep()
      jpaService.find[OrderLog](ordId) must beSome[OrderLog].which(newOrd => {
        newOrd.shiptoAddress1 must be_==(TEST_UPD_ADDR)
        newOrd.orderItems.forall(_.price == 21.99f) must beTrue
      })
    }

    "be able to delete entity" in {
      println("delete")
//      var ord2 =  jpaService.querySingleResult[OrderLog]("from OrderLog as o where o.shiptoFirstName = :firstname", ("firstname" -> TEST_USER_FIRST_NAME))
      var ord2 = jpaService.find[OrderLog](ordId)
      println(ord2)
      sleep()

      ord2 must beSome[OrderLog]
      jpaService.remove(ord2.get)

//      sleep()
      findCustomer(ord2.get.id) must beFalse
    }
  }

  step {

    println("Clean up test data")
    // clean up test data
    jpaService.query[OrderLog]("from OrderLog as o where o.shiptoFirstName =:firstname ", ("firstname" -> TEST_USER_FIRST_NAME))
      .foreach(order=>jpaService.remove(order))

    println("release all resources")
    akka.jpa.JpaActorSystem.shutdown()
    utils.orm.JPA.shutdown()
    DB.shutdown()
  }

  def sleep() = {
//    Thread.sleep(5000)
  }
  def findCustomer(id:Int) = {

    var hasCustomer = false
    DB.withConnection("playjpa") { conn =>
      val s = conn.createStatement()
      val rs = s.executeQuery("select order_id from order_log where order_id = " + id)
      hasCustomer = rs.next
      rs.close
    }
    hasCustomer
  }
}
