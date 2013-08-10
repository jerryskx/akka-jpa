package akka.jpa.types

import scala.{Array => A}
import org.hibernate._
import engine.spi.SessionImplementor
import org.hibernate.usertype._
import org.hibernate.`type`._
import java.sql._
import java.io._

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/3/12
 * Time: 4:05 PM
 *
 * http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch06.html#types-custom
 * http://docs.jboss.org/hibernate/orm/4.1/javadocs/
 */

abstract class OptionType extends UserType {

  def mytype: AbstractSingleColumnStandardBasicType[_]

  def sqlTypes() = A(mytype.sqlType())

  def returnedClass() = classOf[Option[_]]

  def equals(p1: Object, p2: Object) = p1 == p2

  def hashCode(p1: Object) = p1.hashCode()

  def nullSafeGet(rs: ResultSet, names: A[String], session: SessionImplementor, owner: Object) = {
    val x = mytype.nullSafeGet(rs, names(0), session, owner)
    if (x == null) None else Some(x)
  }

  def nullSafeSet(st: PreparedStatement, value: Object, index: Int, session: SessionImplementor) = {
    mytype.nullSafeSet(st, value.asInstanceOf[Option[_]].getOrElse(null), index, session)
  }

  def deepCopy(value: Object) = value

  def isMutable = false

  def disassemble(value: Object) = value.asInstanceOf[Serializable]

  def assemble(cached: Serializable, owner: Object) = cached.asInstanceOf[java.lang.Object]

  def replace(original: Object, target: Object, owner: Object) = original
}

class OptionString extends OptionType {
  def mytype = StringType.INSTANCE
}

class OptionBool extends OptionType {
  def mytype = TrueFalseType.INSTANCE
}

class OptionInt extends OptionType {
  def mytype = IntegerType.INSTANCE
}

class OptionFloat extends OptionType {
  def mytype = FloatType.INSTANCE
}

class OptionDouble extends OptionType {
  def mytype = DoubleType.INSTANCE
}

class OptionCalendar extends OptionType {
  def mytype = CalendarType.INSTANCE
}

