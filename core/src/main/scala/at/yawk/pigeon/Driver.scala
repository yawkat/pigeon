package at.yawk.pigeon

/**
 * @author yawkat
 */
abstract class Driver {
  val router: DatagramRouter

  def publish(datagram: Datagram) = {}

  def children: Iterable[Driver] = List.empty
}
