package specks.views

import specks.models._
import scalafx.Includes._
import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, FlowPane, Pane, StackPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object SpeckView {
  val WIDTH = 100
  val HEIGHT = 100

  case class Data(
                   speck: Speck,
                   col: Int,
                   row: Int
                 )

  def getBackground(data: Data): Background = data match {
    case Data(Value(_), _, _) => new Background(
      Array(new BackgroundFill(
        Color.Green, CornerRadii.Empty, Insets.Empty
      ))
    )
    case _ => Background.Empty
  }

  def getLabel(data: SpeckView.Data): String = data match {
    case SpeckView.Data(Value(value), _, _) => value.toString
    case _ => "  "
  }

  def create(speck: Speck, col: Int, row: Int): SpeckView = new SpeckView(Data(speck, col, row))
}

class SpeckView(initial: SpeckView.Data)
  extends BaseView[SpeckView.Data, Pane] {

  private val labelTextProp = StringProperty(SpeckView.getLabel(initial))
  private val backgroundProp = ObjectProperty(SpeckView.getBackground(initial))
  private val label = new Label() {
    font = new Font(48)
    textFill = Color.White
    text <== labelTextProp
  }
  private val pane = new StackPane() {
    prefWidth = SpeckView.WIDTH
    prefHeight = SpeckView.HEIGHT
    background <== backgroundProp
    children = label
    onMouseClicked = handleClick
  }
  private var rendered = initial
  private var clickHandler: Option[Speck => Unit] = None

  val root: Pane = pane

  render(initial)

  def render(data: SpeckView.Data): Unit = {
      rendered = data
      labelTextProp() = SpeckView.getLabel(data)
      backgroundProp() = SpeckView.getBackground(data)
      pane.relocate(
        SpecksView.PADDING + (SpeckView.WIDTH + SpecksView.SPECK_MARGIN) * data.col,
        SpecksView.PADDING + (SpeckView.HEIGHT + SpecksView.SPECK_MARGIN) * data.row
      )
  }

  def onClick(handler: Speck => Unit): Unit =
    clickHandler = Option(handler)

  override def unmount(): Unit = {
    pane.onMouseClicked = null
  }

  private def handleClick(e: MouseEvent): Unit =
    clickHandler foreach {f => f(rendered.speck)}

}
