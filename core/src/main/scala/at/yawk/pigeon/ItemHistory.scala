package at.yawk.pigeon

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author yawkat
 */
class ItemHistory[T](private val chunkCount: Int = 2, private val chunkSize: Int = 10000) {
  private val chunks = new Array[Chunk](chunkCount)
  private val currentChunk = new AtomicInteger(0)

  def push(id: T): Boolean = {
    val start = currentChunk.get()
    var i = start
    do {
      if (chunks(i).has(id)) {
        return false
      }
      i = (i + 1) % chunkCount
    } while (i != start)

    val front = chunks(i)
    val added = front.push(id)
    if (added) {
      if (front.full && currentChunk.get() == i) {
        shift()
      }
    }
    added
  }

  private def shift() = {
    val next = (currentChunk.get() + 1) % chunkCount
    chunks(next).clear()
    currentChunk.set(next)
  }

  private class Chunk {
    val content = Collections.newSetFromMap[T](new ConcurrentHashMap[T, java.lang.Boolean](chunkSize))

    def full = content.size >= chunkSize

    def has(id: T): Boolean = content.contains(id)

    def push(id: T): Boolean = content.add(id)

    def clear() = content.clear()
  }

}
