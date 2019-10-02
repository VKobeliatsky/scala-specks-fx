package specks.views

import scalafx.beans.property.ObjectProperty
import scalafx.beans.value.ObservableValue
import scalafx.scene.Parent
import scalafx.event.subscriptions.Subscription

import scala.collection.mutable


abstract class BaseView[Data, Root <: Parent](default: Data) {


  type ChangeHandler = (Data, Data) => Unit


  private val _data: ObjectProperty[Data] = ObjectProperty(default)
  def data: Data = _data()


  protected val subscriptions: mutable.Set[Subscription] = mutable.Set[Subscription]()


  val root: Root


  def mapData[T](map: Data => T): ObjectProperty[T] = {
    val property = ObjectProperty(map(data))
    onDataChange({ (_, next) => property() = map(next) })
    property
  }


  def onDataChange(handler: ChangeHandler): Unit = listen(_data, handler)


  def render(next: Data): Unit = {
    _data() = next
  }


  def unmount(): Unit = {
    subscriptions foreach {
      _.cancel()
    }
  }


  private def listen(
    value: ObservableValue[Data, Data],
    action: ChangeHandler
  ): Unit =
    subscriptions add value.onChange((_, from, to) => action(from, to))

}
