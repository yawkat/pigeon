package at.yawk.pigeon

import java.util.Random

import io.netty.buffer.{Unpooled, ByteBuf}

/**
 * @author yawkat
 */
class MessageId private (val id: ByteBuf) extends AnyVal

object MessageId {
  private val RNG = new Random()
  private val DefaultLength = 16

  def random(length: Int = DefaultLength): MessageId = {
    val bytes = new Array[Byte](length)
    RNG.nextBytes(bytes)
    create(Unpooled.wrappedBuffer(bytes))
  }

  def create(id: ByteBuf): MessageId = {
    new MessageId(Unpooled.unmodifiableBuffer(id))
  }
}
