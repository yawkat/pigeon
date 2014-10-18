package at.yawk.pigeon

import java.util
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * @author yawkat
 */
class Loft private(private val router: DatagramRouter) {
  private val listeners = new ConcurrentHashMap[Address, util.Set[(Message) => Unit]]()

  def getChannel(address: Address): Channel = {
    new Channel {
      override def publish(message: Message) = router.publish(new Datagram(address, message))

      override def subscribe(listener: (Message) => Unit) = {
        var section = listeners.get(address)
        if (section == null) {
          section = Collections.newSetFromMap(new ConcurrentHashMap[(Message) => Unit, java.lang.Boolean]())
          listeners.putIfAbsent(address, section)
        }
        section.add(listener)
      }
    }
  }

  def apply(address: Address): Channel = getChannel(address)
}

object Loft {

  class Builder {
    private var driverFactories = Set[DriverFactory]()

    def append(factory: DriverFactory): Builder = {
      driverFactories += factory
      this
    }

    def build(): Loft = {
      val drivers = collection.mutable.Buffer[Driver]()
      val _router = new DatagramRouter(drivers)
      val loft = new Loft(_router)

      val subscribeDriver = new Driver() {
        val router = _router

        override def publish(datagram: Datagram) = {
          val section = loft.listeners.get(datagram.target)
          if (section != null) {
            collection.JavaConversions.asScalaSet(section).foreach { listener =>
              listener(datagram.body)
            }
          }
        }
      }
      drivers.append(subscribeDriver)
      drivers.appendAll(driverFactories.map(f => f(_router)))

      loft
    }
  }

}
