package models

/**
 * Created with IntelliJ IDEA.
 * User: jerrywang
 * Date: 8/2/12
 * Time: 3:25 PM

-- -----------------------------------------------------
-- Table `playjpa`.`order_log`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `playjpa`.`order_log` (
`order_id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
`shipto_first_name` VARCHAR(255) NOT NULL ,
`shipto_last_name` VARCHAR(255) NOT NULL ,
`shipto_address1` VARCHAR(255) NOT NULL ,
`shipto_address2` VARCHAR(255) NULL ,
`shipto_city` VARCHAR(50) NOT NULL ,
`shipto_state` VARCHAR(10) NOT NULL ,
`shipto_zip` VARCHAR(25) NOT NULL ,
`order_timestamp` DATETIME NOT NULL ,
PRIMARY KEY (`order_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playjpa`.`order_items`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `playjpa`.`order_items` (
`id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
`order_id` INT UNSIGNED NOT NULL ,
`sku` VARCHAR(45) NOT NULL ,
`price` DECIMAL(6,2) NOT NULL ,
`status` VARCHAR(25) NOT NULL ,
PRIMARY KEY (`id`) ,
INDEX `fk_order_items_order_idx` (`order_id` ASC) ,
CONSTRAINT `fk_order_items_order`
 FOREIGN KEY (`order_id` )
 REFERENCES `playjpa`.`order_log` (`order_id` )
 ON DELETE NO ACTION
 ON UPDATE NO ACTION)
ENGINE = InnoDB;

alter table order_log auto_increment = 1000000;

alter table order_items auto_increment = 1000;


-- -----------------------------------------------------
Default data
-- -----------------------------------------------------
select * from order_log
go
select * from order_items
go

insert into order_log (shipto_first_name, shipto_last_name, shipto_address1, shipto_city, shipto_state, shipto_zip, order_timestamp) values ('Jerry', 'Wang', '225 S. Sepulveda Blvd', 'Manhattan Beach', 'CA', '90266', now())
go


--insert into order_items (order_id, sku, price, status) values (1000000, '123abc', 14.99,'NEW')
insert into order_items (order_id, sku, price, status) values (1000000, '345def', 18.99,'NEW')
go
*/
object DatabaseConstants {
  final val TABLE_ORDER_LOG = "order_log"
  final val TABLE_ORDER_ITEMS = "order_items"

  final val COL_ORDER_ID = "order_id"
  final val COL_SHIPTO_FIRST_NAME = "shipto_first_name"
  final val COL_SHIPTO_LAST_NAME = "shipto_last_name"
  final val COL_SHIPTO_ADDRESS1 = "shipto_address1"
  final val COL_SHIPTO_ADDRESS2 = "shipto_address2"
  final val COL_SHIPTO_CITY = "shipto_city"
  final val COL_SHIPTO_STATE = "shipto_state"
  final val COL_SHIPTO_ZIP = "shipto_zip"
  final val COL_ORDERTIMESTAMP = "order_timestamp"

  final val COL_ID = "id"
  final val COL_SKU = "sku"
  final val COL_PRICE = "price"
  final val COL_STATUS = "status"

}
