package coinyser

import org.apache.spark.sql.test.SharedSparkSession
import org.scalatest.{Matchers, WordSpec}

class BatchProducerSpec extends WordSpec with Matchers with SharedSparkSession {
  val httpTransaction1 =
    HttpTransaction("1532365695", "70683282", "7740.00", "0", "0.10041719")
  val httpTransaction2 =
    HttpTransaction("1532365693", "70683281", "7739.99", "0", "0.00148564")
}
