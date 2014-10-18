package at.yawk.pigeon

/**
 * @author yawkat
 */
abstract class DriverFactory {
  def getDriver(router: DatagramRouter): Driver

  final def apply(router: DatagramRouter): Driver = getDriver(router)
}
