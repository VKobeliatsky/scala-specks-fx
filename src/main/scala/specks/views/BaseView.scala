package specks.views

import scalafx.scene.Parent

abstract class BaseView[Data, Root <: Parent] {
  val root: Root
  def render(data: Data): Unit
  def unmount(): Unit = {}
}
