package at.yawk.pigeon

/**
 * @author yawkat
 */
class Datagram(val id: MessageId, val target: Address, val body: Message) {
  def this(target: Address, message: Message) = {
    this(MessageId.random(), target, message)
  }
}
