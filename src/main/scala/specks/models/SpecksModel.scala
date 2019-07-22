package specks.models

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class SpecksModel(val width: Int, val height: Int) {

  private val specks: Array[Speck] = mkOrdered().toArray

  def isDefinedAt(i: Int, j: Int): Boolean = i * width + j < width * height

  def apply(i: Int, j: Int): Speck = specks(i * width + j)

  def shuffle(): this.type = {
    val ordered = mkOrdered()
    specks.zipWithIndex.foreach({case (_, index) =>
      val orderedIndex = Random.nextInt(ordered.length)
      val speck = ordered(orderedIndex)
      specks(index) = speck
      ordered.remove(orderedIndex)
    })
    this
  }

  def move(target: Value): this.type = {
    val targetAt = specks.indexWhere({
      case Value(value) if value == target.value => true
      case _ => false
    })
    val cursorAt = specks.indexWhere({
      case Empty() => true
      case _ => false
    })

    if (
      targetAt >= 0 && (
        targetAt + 1 == cursorAt ||
          targetAt - 1 == cursorAt ||
          targetAt + width == cursorAt ||
          targetAt - width == cursorAt
        )
    ) {
      specks(cursorAt) = specks(targetAt)
      specks(targetAt) = Empty()
    }

    this
  }

  private def mkOrdered(): ArrayBuffer[Speck] = {
    val ordered = new ArrayBuffer[Speck]()

    for(i <- 0 until width)
      for(j <- 0 until height) {
        val pos = i * width + j
        ordered += Value(pos + 1)
      }

    ordered(ordered.length - 1) = Empty()

    ordered
  }

}