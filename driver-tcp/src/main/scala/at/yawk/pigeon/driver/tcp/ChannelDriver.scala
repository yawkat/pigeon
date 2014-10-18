package at.yawk.pigeon.driver.tcp

import at.yawk.pigeon.{DatagramRouter, Datagram, Driver}
import io.netty.channel.Channel

/**
 * @author yawkat
 */
private[tcp] class ChannelDriver(val router: DatagramRouter, channel: Channel) extends Driver {
  override def publish(datagram: Datagram) = {
    channel.write(datagram)
  }
}
