package coinyser

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.TimeZone

import cats.effect.IO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.pusher.client.Client
import com.pusher.client.channel.SubscriptionEventListener
import com.typesafe.scalalogging.StrictLogging

object StreamingProducer extends StrictLogging{
  val mapper: ObjectMapper = {

    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    m.setDateFormat(sdf)
  }

  // Method for deserializing websocket transaction
  def deserializeWebsocketTransaction(s: String): WebsocketTransaction = {
    mapper.readValue(s, classOf[WebsocketTransaction])
  }

  def subscribe(pusher: Client) (onTradeReceived: String => Unit): IO[Unit] = {
    for {
      _ <- IO(pusher.connect())
      channel <- IO(pusher.subscribe("live_trades"))
      _ <- IO(channel.bind("trade", new SubscriptionEventListener {
        override def onEvent(channelName: String, event: String, data: String): Unit = {
          logger.info(s"Received event $event with data: $data from $channelName")
        }
      }))
    } yield ()
  }

  def convertWsTransaction(wsTx: WebsocketTransaction): Transaction = {
    Transaction(
      timestamp = new Timestamp(wsTx.timestamp.toLong * 1000), tid = wsTx.id, price = wsTx.price, sell = wsTx.`type` == 1, amount = wsTx.amount)
  }

  def serializeTransaction(transaction: Transaction): String = {
    mapper.writeValueAsString(transaction)
  }
}
