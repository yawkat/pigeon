package at.yawk.pigeon.driver.tcp

import at.yawk.pigeon.Driver

/**
 * @author yawkat
 */
abstract class Connector extends Driver {
  def start()

  def stop()
}
