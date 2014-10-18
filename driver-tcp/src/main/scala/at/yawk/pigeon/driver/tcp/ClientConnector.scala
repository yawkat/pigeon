package at.yawk.pigeon.driver.tcp

import java.net.SocketAddress

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}

/**
 * @author yawkat
 */
class ClientConnector private[tcp](driver: TcpDriver, val serverAddress: SocketAddress)
  extends NettyConnector(driver) {

  override protected def doStart() = {
    val workGroup = new NioEventLoopGroup()

    val b = new Bootstrap()
    b.group(workGroup)
      .channel(classOf[NioSocketChannel])
      .option(ChannelOption.SO_KEEPALIVE, Boolean.box(true))
      .handler(new ChannelInitializer[SocketChannel] {
      override def initChannel(ch: SocketChannel) = useForCommunication(ch)
    })

    val future = b.connect(serverAddress).sync()

    channel = Some(future.channel())

    closeOn(future.channel())
  }
}
