package specks.views

import specks.models._
import javafx.scene.{layout => jfxsl}
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.{Background, BackgroundFill, Border, CornerRadii, Pane, StackPane}
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

  def getBackground(data: Data): jfxsl.Background = Background
    .sfxBackground2jfx(data match {
      case Data(Value(_), _, _) => new Background(
        Array(new BackgroundFill(
          Color.Green, CornerRadii.Empty, Insets.Empty
        ))
      )
      case _ => Background.Empty
    })

  def getLabel(data: SpeckView.Data): String = data match {
    case SpeckView.Data(Value(value), _, _) => value.toString
    case _ => "  "
  }

  def getXPos(data: SpeckView.Data): Double =
    SpecksView.PADDING + (SpeckView.WIDTH + SpecksView.SPECK_MARGIN) * data.col

  def getYPos(data: SpeckView.Data): Double =
    SpecksView.PADDING + (SpeckView.HEIGHT + SpecksView.SPECK_MARGIN) * data.row
}

final class SpeckView
  extends BaseView[SpeckView.Data, Pane](SpeckView.INITIAL_DATA) {

  private val label = new Label() {
    font = new Font(48)
    textFill = Color.White
    text <== mapData(SpeckView.getLabel)
  }

  private val pane = new StackPane() {
    prefWidth = SpeckView.WIDTH
    prefHeight = SpeckView.HEIGHT
    background <== mapData(SpeckView.getBackground)
    children = label
    onMouseClicked = handleClick
  }

  private var clickHandler: Option[Speck => Unit] = None

  private var currentTransition: Option[TranslateTransition] = None

  val root: Pane = pane

  listenData(translate)

  def onClick(handler: Speck => Unit): Unit =
    clickHandler = Option(handler)

  override def unmount(): Unit = {
    currentTransition foreach { transition => transition.stop() }
    clickHandler = None
  }

  private def translate(
    from: SpeckView.Data,
    to: SpeckView.Data
  ): Unit = if (from != to) {
    val transition = ensureTransition

    transition.stop()

    transition.fromX = SpeckView.getXPos(from)
    transition.fromY = SpeckView.getYPos(from)

    transition.toX = SpeckView.getXPos(to)
    transition.toY = SpeckView.getYPos(to)

    transition.play()

    currentTransition = Some(transition)
  }

  private def handleClick(e: MouseEvent): Unit =
    if (currentTransition.isEmpty) for {
      handler <- clickHandler
    } handler(data().speck)

  private def ensureTransition: TranslateTransition =
    currentTransition getOrElse newTransition

  private def newTransition: TranslateTransition = {
    val transition = new TranslateTransition(
      SpeckView.TRANSITION_DURATION,
      pane
    )

    currentTransition = Option(transition)
    transition.onFinished = _ => currentTransition = None

    transition
  }

}
