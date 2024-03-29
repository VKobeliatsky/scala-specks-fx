package specks

import specks.models._
import specks.views._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application._
import scalafx.scene._

object Main extends JFXApp {
  private val model = new SpecksModel(4, 4)
  private val view = new SpecksView

  view.render(model)

  view.onClick {
    case speck@Value(_) =>
      model.move(speck)
      view.render(model)
    case Empty() =>
      model.shuffle()
      view.render(model)
  }

  stage = new PrimaryStage {
    title = "Hello"
    scene = new Scene {
      root = view.root
      resizable = false
    }
  }
}