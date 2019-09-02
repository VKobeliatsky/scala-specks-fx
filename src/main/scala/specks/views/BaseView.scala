package specks.views

import scalafx.beans.property.ObjectProperty
import scalafx.beans.value.ObservableValue
import scalafx.scene.Parent
import scalafx.event.subscriptions.Subscription

import scala.collection.mutable


abstract class BaseView[Data, Root <: Parent](default: Data) {


  private type Action[T] = (T, T) => Unit


  val data: ObjectProperty[Data] = ObjectProperty(default)


  protected val subscriptions: mutable.Set[Subscription] = mutable.Set[Subscription]()


  val root: Root


  def mapData[T](map: Data => T): ObjectProperty[T] = {
    val property = ObjectProperty(map(data()))
    listenData({ (_, next) => property() = map(next) })
    property
  }


  def listenData(action: Action[Data]): Unit = listen(data, action)


  def render(next: Data): Unit = {
    data() = next
  }

  def unmount(): Unit = {
    subscriptions foreach {
      _.cancel()
    }
  }


  private def listen[T](
    value: ObservableValue[T, T],
    action: Action[T]
  ): Unit =
    subscriptions add value.onChange((_, current, next) => action(current, next))
}
