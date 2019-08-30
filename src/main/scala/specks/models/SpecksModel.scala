package specks.models

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class SpecksModel(val width: Int, val height: Int) {

  private val _specks: Array[Speck] = mkOrdered().toArray

  def specks: Array[Speck] = _specks.clone
  def specksWithPositions: Seq[(Speck, Int, Int)] =
    _specks.zipWithIndex.map {case (speck, index) =>
      (speck, toCol(index), toRow(index))
  }

  def isDefinedAt(col: Int, row: Int): Boolean = col < width && row < height

  def apply(col: Int, row: Int): Option[Speck] =
    if (isDefinedAt(col, row)) Option(_specks(toIndex(col, row)))
    else None

  def apply(speck: Speck): Option[(Int, Int)] = {
    val index = _specks indexOf speck
    if (index >= 0)
      Option((toCol(index), toRow(index)))
    else
      None
  }

  def shuffle(): this.type = {
    val ordered = mkOrdered()
    _specks.indices.foreach(index => {
      val orderedIndex = Random.nextInt(ordered.length)
      val speck = ordered(orderedIndex)
      _specks(index) = speck
      ordered.remove(orderedIndex)
    })
    this
  }

  def move(speck: Speck): this.type = speck match {
    case Empty() => this
    case Value(target) =>
      val targetAt = _specks.indexWhere({
        case Value(value) if value == target => true
        case _ => false
      })
      val cursorAt = _specks.indexWhere({
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
        _specks(cursorAt) = _specks(targetAt)
        _specks(targetAt) = Empty()
      }

      this
  }

  private def mkOrdered(): ArrayBuffer[Speck] = {
    val ordered = new ArrayBuffer[Speck]()

    for(
      row <- 0 until height;
      col <- 0 until width
    ) {
      val pos = toIndex(col, row)
      ordered += Value(pos + 1)
    }

    ordered(ordered.length - 1) = Empty()

    ordered
  }

  private def toIndex(col: Int, row: Int): Int =
    row * width + col

  private def toCol(index: Int): Int =
    index % width

  private def toRow(index: Int): Int =
    index / width
}