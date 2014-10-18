package at.yawk.pigeon.driver.tcp

import java.net.SocketAddress

import at.yawk.pigeon.Datagram
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{Channel, ChannelHandlerAdapter, ChannelHandlerContext}
import io.netty.handler.codec.protobuf.{ProtobufVarint32FrameDecoder, ProtobufVarint32LengthFieldPrepender}
import io.netty.util.concurrent.GenericFutureListener

/**
 * @author yawkat
 */
private[tcp] abstract class NettyConnector(val driver: TcpDriver) extends Connector {
  protected var channel: Option[Channel] = Option.empty
  private var state = State.Init

  override val router = driver.router

  private var _children = Set[ChannelDriver]()

  override def children = _children

  private object State extends Enumeration {
    val Init = Value
    val Running = Value
    val Exited = Value
  }

  protected def closeOn(channel: Channel) = {
    channel.closeFuture().addListener(new GenericFutureListener[Nothing] {
      override def operationComplete(f: Nothing) = markClosed()
    })
  }

  protected def useForCommunication(channel: SocketChannel) = {
    channel.pipeline()
      .addLast(new ProtobufVarint32FrameDecoder())
      .addLast(new ProtobufVarint32LengthFieldPrepender())
      .addLast(new DatagramCodec())

    val driver = new ChannelDriver(router, channel)

    val con = new Connection {
      override def send(message: Datagram) = {
        channel.write(message)
      }

      override val remoteAddress: SocketAddress = channel.remoteAddress()
    }

    channel.pipeline().addLast(new ChannelHandlerAdapter() {
      override def channelRead(ctx: ChannelHandlerContext, msg: Any) = {
        val datagram = msg.asInstanceOf[Datagram]
        NettyConnector.this.synchronized {
          _children += driver
        }
      }
    })

    channel.closeFuture().addListener(new GenericFutureListener[Nothing] {
      override def operationComplete(p1: Nothing) = {
        NettyConnector.this.synchronized {
          _children -= driver
        }
      }
    })
  }

  override def start() = {
    this.synchronized {
      if (this.state != State.Init) {
        throw new IllegalStateException("Already started!")
      }

      state = State.Running
      doStart()
    }
  }

  protected def doStart()

  private def markClosed() = {
    this.synchronized {
      state = State.Exited
      channel = Option.empty
    }
  }

  def stop() = {
    this.synchronized {
      if (state != State.Running) {
        throw new IllegalStateException("Not running!")
      }

      channel.get.close()
    }
  }
}
