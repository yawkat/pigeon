package at.yawk.pigeon

/**
 * @author yawkat
 */
trait Channel {
  def publish(message: Message)

  def subscribe(listener: Message => Unit)
}
