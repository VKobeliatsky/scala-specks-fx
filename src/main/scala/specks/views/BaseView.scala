package specks.views

import scalafx.scene.Parent

abstract class BaseView[Data, Root <: Parent](default: Data) {
  private var _data: Option[Data] = None
  def data: Data = _data getOrElse default
  protected def data_=(next: Data): Unit = _data = Option(next)

  val root: Root

  def render(next: Data): Unit = {
    data = next
  }
  def unmount(): Unit = {}
}
