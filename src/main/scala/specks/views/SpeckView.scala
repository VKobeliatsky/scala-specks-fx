package specks.views

import specks.models._
import scalafx.Includes._
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, FlowPane, Pane, StackPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.animation._
import scalafx.util.Duration

object SpeckView {
  val WIDTH = 100
  val HEIGHT = 100
  val TRANSITION_DURATION = Duration(1000)

  val INITIAL_DATA = Data(Empty(), 0, 0)

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

  def getXPos(data: SpeckView.Data): Double =
    SpecksView.PADDING + (SpeckView.WIDTH + SpecksView.SPECK_MARGIN) * data.col

  def getYPos(data: SpeckView.Data): Double =
    SpecksView.PADDING + (SpeckView.HEIGHT + SpecksView.SPECK_MARGIN) * data.row

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
  private var clickHandler: Option[Speck => Unit] = None

  private var data = SpeckView.INITIAL_DATA

  private var currentTransition: Option[TranslateTransition] = None

  val root: Pane = pane

  render(initial)

  def render(next: SpeckView.Data): Unit = if (data != next) {
    translate(next)

    labelTextProp() = SpeckView.getLabel(next)
    backgroundProp() = SpeckView.getBackground(next)

    data = next
  }

  def onClick(handler: Speck => Unit): Unit =
    clickHandler = Option(handler)

  override def unmount(): Unit = {
    currentTransition foreach {transition => transition.stop()}
    pane.onMouseClicked = null
  }

  private def ensureTransition: TranslateTransition = currentTransition getOrElse {
    val nextTransition = new TranslateTransition(
      SpeckView.TRANSITION_DURATION, pane
    ) {
      onFinished = _ => currentTransition = None
    }

    nextTransition.fromX = SpeckView.getXPos(data)
    nextTransition.fromY = SpeckView.getYPos(data)

    currentTransition = Some(nextTransition)

    nextTransition
  }

  private def translate(next: SpeckView.Data): Unit = {
    val nextTransition = ensureTransition

    nextTransition.stop()

    nextTransition.toX = SpeckView.getXPos(next)
    nextTransition.toY = SpeckView.getYPos(next)

    nextTransition.play()

    currentTransition = Some(nextTransition)
  }

  private def handleClick(e: MouseEvent): Unit =
    if (currentTransition.isEmpty) for {
      handler <- clickHandler
    } handler(data.speck)
}
