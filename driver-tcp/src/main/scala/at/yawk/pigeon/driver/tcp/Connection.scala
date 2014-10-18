package at.yawk.pigeon.driver.tcp

import java.net.SocketAddress

import at.yawk.pigeon.Datagram

/**
 * @author yawkat
 */
abstract class Connection {
  val remoteAddress: SocketAddress

  def send(message: Datagram)
}
