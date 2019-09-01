package specks.views

import scalafx.scene.layout.Pane
import specks.models._

import scala.collection._
import scala.language.implicitConversions

object SpecksView {

  case class Data(
                   specks: Seq[SpeckWithPosition],
                   width: Int,
                   height: Int
                 )

  object Data {
    implicit def fromModel(model: SpecksModel): Data =
      Data(
        model.specksWithPositions,
        model.width,
        model.height
      )
  }

  type SpeckWithPosition = (Speck, Int, Int)

  val INITIAL_DATA: Data = Data(Seq(), 0, 0)
  val PADDING = 15
  val SPECK_MARGIN = 15

  def getWidth(data: Data): Double =
    SpecksView.PADDING + data.width * (SpeckView.WIDTH + SpecksView.SPECK_MARGIN)

  def getHeight(data: Data): Double =
    SpecksView.PADDING + data.height * (SpeckView.HEIGHT + SpecksView.SPECK_MARGIN)

}

final class SpecksView
  extends BaseView [SpecksView.Data, Pane](SpecksView.INITIAL_DATA) {

  private var clickHandler: Option[Speck => Unit] = None

  val root: Pane = new Pane
  val speckViews: mutable.Map[Speck, SpeckView] = mutable.Map()

  override def render(next: SpecksView.Data): Unit = {
    setSize(next)
    updateSpeckViews(next)
    super.render(next)
  }

  def onClick(handler: Speck => Unit): Unit =
    clickHandler = Option(handler)

  override def unmount(): Unit =
    for (view <- speckViews.values) view.unmount()

  private def setSize(next: SpecksView.Data): Unit =
    root.setPrefSize(
      SpecksView.getWidth(next),
      SpecksView.getHeight(next)
    )

  private def updateSpeckViews(next: SpecksView.Data): Unit = {
    val updated: mutable.Set[Speck] = mutable.Set()
    val created: mutable.Set[Speck] = mutable.Set()

    for (
      (speck, col, row) <- next.specks
    ) if (
      speckViews contains speck
    ) {
      val view = speckViews(speck)
      view render SpeckView.Data(speck, col, row)
      updated add speck
    } else {
      val view = new SpeckView
      view.onClick(handleSpeckClick)
      root.children add view.root
      view render SpeckView.Data(speck, col, row)
      speckViews put (speck, view)
      created add speck
    }

    for (
      view <- (speckViews -- updated -- created).values
    ) {
      view.unmount()
      root.children.remove(view.root)
    }
  }

  private def handleSpeckClick(speck: Speck): Unit = {
    clickHandler foreach {f => f(speck)}
  }
}

case class SpeckPosition(col: Int, row: Int)