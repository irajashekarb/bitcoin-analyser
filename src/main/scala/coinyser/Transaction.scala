package coinyser

import java.sql.{Date, Timestamp}
import java.time.ZoneOffset

// Case class for Dataset[Transaction]
case class Transaction(timestamp: Timestamp,
                       date: Date,
                       tid: Int,
                       price: Double,
                       sell: Boolean,
                       amount: Double)

//In order to avoid having to pass the date, we can create a new apply method
object Transaction {
  def apply(timestamp: Timestamp,
            tid: Int,
            price: Double,
            sell: Boolean,
            amount: Double) =
    new Transaction(
      timestamp = timestamp,
      date = Date.valueOf(
        timestamp.toInstant.atOffset(ZoneOffset.UTC).toLocalDate),
      tid = tid,
      price = price,
      sell = sell,
      amount = amount)
}
