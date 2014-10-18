package at.yawk.pigeon

import io.netty.buffer.{ByteBuf, Unpooled}

/**
 * @author yawkat
 */
class Message private(val message: ByteBuf) extends AnyVal

object Message {
  def apply(message: ByteBuf): Message = {
    new Message(Unpooled.unmodifiableBuffer(message))
  }
}
