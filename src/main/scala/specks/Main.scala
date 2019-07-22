package specks

import scalafx.Includes._
import scalafx.beans.{property => Properties}
import scalafx.{application => Application}
import scalafx.{geometry => Geometry}
import scalafx.{scene => Scene}
import scalafx.scene.{control => Control}
import scalafx.scene.{layout => Layout}
import specks.models.{SpecksModel, Value}
import specks.views.SpeckView

object Main extends Application.JFXApp {
  val model = new SpecksModel(8, 8)
  stage = new Application.JFXApp.PrimaryStage {
    title = "Hello"
    scene = new Scene.Scene {
      private val view = new SpeckView()
      root = view.root
      view.render(Value(42))
//      root = new Layout.FlowPane(10, 0) {
//        padding = Geometry.Insets(25)
//        val button = new Control.Button("Hello")
//        val label = new Control.Label
//        val text = Properties.StringProperty("World!")
//        label.text <== text
//        children = Seq(
//          button,
//          label
//        )
//        button.onAction = () => {
//          text() += "!"
//        }
//      }
    }
  }
}