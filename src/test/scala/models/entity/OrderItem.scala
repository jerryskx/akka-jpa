package models.entity

import javax.persistence._
import models.DatabaseConstants._


/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/2/12
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */

object OrderItem {
  def apply(sku:String, price: Float) = {
    val item = new OrderItem
    item.sku = sku
    item.price = price
    item.status = "NEW"
    item
  }
}

@Entity
@Cacheable(false)
@Table(name = TABLE_ORDER_ITEMS)
class OrderItem extends Serializable  {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = COL_ID)
  var id: Int = _

  @Column(name = COL_SKU)
  var sku:String = _

  @Column(name = COL_PRICE)
  var price: Float= _

  @Column(name = COL_STATUS)
  var status:String = _

  @ManyToOne
  @JoinColumn(name = COL_ORDER_ID)
  var orderLog: OrderLog =  _
}
