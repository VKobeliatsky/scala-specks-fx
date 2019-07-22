package specks.views

import specks.models._
import scalafx.Includes._
import scalafx.beans.property.StringProperty
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, FlowPane, StackPane}
import scalafx.scene.Parent
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

class SpeckView {

  private val labelText = StringProperty("  ")
  private val label = new Label() {
    font = new Font(48)
    textFill = Color.White
    text <== labelText
  }
  private val pane = new StackPane() {
    padding = Insets(15)
    maxWidth = 100
    minWidth = 100
    maxHeight = 100
    minHeight = 100
    children = label
  }
  val root: Parent = pane

  def render(speck: Speck): Unit = {
    updatePane(speck)
    updateLabel(speck)
  }

  private def updateLabel(speck: Speck): Unit = speck match {
    case Value(value) => labelText() = value.toString
    case _ => labelText() = "  "
  }

  private def updatePane(speck: Speck): Unit = speck match {
    case Value(_) => pane.setBackground(
      new Background(
        Array(new BackgroundFill(
          Color.Green, CornerRadii.Empty, Insets.Empty
        ))
      )
    )
    case _ => pane.setBackground(Background.Empty)
  }

}
