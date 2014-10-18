package at.yawk.pigeon.driver.tcp

import java.net.SocketAddress

import at.yawk.pigeon.DatagramRouter
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}

/**
 * @author yawkat
 */
class ServerConnector private[tcp](driver: TcpDriver, val listenAddress: SocketAddress)
  extends NettyConnector(driver) {

  override protected def doStart() = {
    val bossGroup = new NioEventLoopGroup()
    val workGroup = new NioEventLoopGroup()

    val b = new ServerBootstrap()
    b.group(bossGroup, workGroup)
      .channel(classOf[NioServerSocketChannel])
      .childHandler(new ChannelInitializer[SocketChannel] {
      override def initChannel(ch: SocketChannel) = useForCommunication(ch)
    })
      .option(ChannelOption.SO_BACKLOG, Integer.valueOf(128))
      .childOption(ChannelOption.SO_KEEPALIVE, Boolean.box(true))

    val future = b.bind(listenAddress).sync()

    channel = Some(future.channel())

    closeOn(future.channel())
  }
}
