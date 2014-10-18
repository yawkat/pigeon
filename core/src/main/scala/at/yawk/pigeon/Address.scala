package at.yawk.pigeon

import io.netty.buffer.{Unpooled, ByteBuf}

/**
 * @author yawkat
 */
class Address private(val address: ByteBuf) extends AnyVal

object Address {
  def apply(address: ByteBuf): Address = {
    new Address(Unpooled.unmodifiableBuffer(address))
  }
}
