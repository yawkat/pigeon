package at.yawk.pigeon.driver.tcp

import java.util

import at.yawk.pigeon.{Message, MessageId, Datagram, Address}
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

/**
 * @author yawkat
 */
private[tcp] class DatagramCodec extends ByteToMessageCodec[Datagram] {
  private def writeVarInt(to: ByteBuf, int: Long) = {
    var more = true
    while (more) {
      more = (int & ~0x7F) != 0
      var encodedByte = int & ~0x7F
      if (more) {
        encodedByte |= 0x80
      }
      to.writeByte(encodedByte.toInt)
    }
  }

  private def readVarInt(from: ByteBuf): Long = {
    var result = 0
    var offset = 0
    var next = 0x80
    while ((next & 0x80) != 0) {
      next = from.readByte() & 0xFF
      result |= (next & 0x7F) << offset
      offset += 7
    }
    result
  }

  private def writeSizedBuffer(to: ByteBuf, from: ByteBuf) = {
    val len = from.readableBytes()
    writeVarInt(to, len)
    to.writeBytes(from, len)
  }

  private def readSizedBuffer(from: ByteBuf): ByteBuf = {
    val len = readVarInt(from)
    val buf = from.readSlice(len.toInt)
    buf
  }

  override def encode(ctx: ChannelHandlerContext, msg: Datagram, out: ByteBuf) = {
    writeSizedBuffer(out, msg.id.id)
    writeSizedBuffer(out, msg.target.address)
    writeSizedBuffer(out, msg.body.message)
  }

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]) = {
    val id = readSizedBuffer(in)
    val address = readSizedBuffer(in)
    val body = readSizedBuffer(in)

    out.add(new Datagram(MessageId(id), Address(address), Message(body)))
  }
}
