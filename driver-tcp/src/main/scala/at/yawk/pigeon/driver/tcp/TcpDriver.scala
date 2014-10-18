package at.yawk.pigeon.driver.tcp

import java.net.SocketAddress

import at.yawk.pigeon.{DatagramRouter, Datagram, Driver, Loft}

/**
 * @author yawkat
 */
class TcpDriver(val router: DatagramRouter, val listener: (Datagram) => Unit) extends Driver {
  private var connectors: Set[Connector] = collection.immutable.Set()

  override def children: Iterable[Driver] = {
    connectors
  }

  def createListener(on: SocketAddress): ServerConnector = {
    val connector = new ServerConnector(this, on)
    add(connector)
    connector
  }

  def createClient(to: SocketAddress): ClientConnector = {
    val connector = new ClientConnector(this, to)
    add(connector)
    connector
  }

  def remove(connector: Connector) = {
    this.synchronized {
      this.connectors -= connector
    }
  }

  private def add(connector: Connector) = {
    this.synchronized {
      this.connectors += connector
    }
  }
}
