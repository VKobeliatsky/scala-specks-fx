package specks.views

import scalafx.scene.layout.Pane
import specks.models._

import scala.collection._

object SpecksView {
  val PADDING = 15
  val SPECK_MARGIN = 15
  def getWidth(model: SpecksModel): Double =
    SpecksView.PADDING + model.width * (SpeckView.WIDTH + SpecksView.SPECK_MARGIN)
  def getHeight(model: SpecksModel): Double =
    SpecksView.PADDING + model.height * (SpeckView.HEIGHT + SpecksView.SPECK_MARGIN)
}

class SpecksView(initial: SpecksModel)
  extends BaseView [SpecksModel, Pane] {

  private var clickHandler: Option[Speck => Unit] = None

  val root: Pane = new Pane
  val speckViews: mutable.Map[Speck, SpeckView] = mutable.Map()

  render(initial)

  def render(model: SpecksModel): Unit = {
    setSize(model)
    updateSpeckViews(model)
  }

  def onClick(handler: Speck => Unit): Unit =
    clickHandler = Option(handler)

  override def unmount(): Unit =
    for (view <- speckViews.values) view.unmount()

  private def setSize(model: SpecksModel): Unit =
    root.setPrefSize(SpecksView.getWidth(model), SpecksView.getHeight(model))

  private def updateSpeckViews(model: SpecksModel): Unit = {
    val updated: mutable.Set[Speck] = mutable.Set()
    val created: mutable.Set[Speck] = mutable.Set()

    for (
      (speck, col, row) <- model.specksWithPositions
    ) if (
      speckViews contains speck
    ) {
      val view = speckViews(speck)
      view render SpeckView.Data(speck, col, row)
      updated add speck
    } else {
      val view = SpeckView.create(speck, col, row)
      view.onClick(handleSpeckClick)
      root.children add view.root
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