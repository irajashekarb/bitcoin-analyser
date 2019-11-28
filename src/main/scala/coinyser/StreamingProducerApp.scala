package coinyser

import cats.effect.IO
import com.pusher.client.Client
import com.pusher.client.channel.SubscriptionEventListener
import com.typesafe.scalalogging.StrictLogging

object StreamingProducerApp extends StrictLogging{
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
}
