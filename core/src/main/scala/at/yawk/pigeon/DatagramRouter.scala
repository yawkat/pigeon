package at.yawk.pigeon

/**
 * @author yawkat
 */
class DatagramRouter(override val children: Iterable[Driver]) extends Driver {
  private val history = new ItemHistory[MessageId]()
  override val router = this // override Driver.router

  def received(datagram: Datagram, through: Driver) = {
    if (history.push(datagram.id)) {
      this.transmit(datagram, through)
    }
  }

  override def publish(datagram: Datagram) = {
    this.transmit(datagram)
  }

  private def transmit(datagram: Datagram, except: Driver = null, on: Driver = this) {
    if (on != except) {
      on.publish(datagram)
      on.children.foreach { d =>
        transmit(datagram, except, d)
      }
    }
  }
}
