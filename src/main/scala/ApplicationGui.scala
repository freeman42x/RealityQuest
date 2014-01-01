package org.github.razvanpanda

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene
import scene.effect._
import scene.paint.{Stops, LinearGradient}
import scene.text.Text

object ApplicationGui extends JFXApp
{
    stage = new PrimaryStage
    {
        title = "RealityQuest"
        width = 600
        height = 450
        resizable = false
        scene = new Scene
        {
            fill = WHITESMOKE
            content = new HBox
            {
                content = Seq(new Text
                {
                    text = "Reality"
                    style = "-fx-font-size: 100pt"
                    fill = new LinearGradient(
                        endX = 0,
                        stops = Stops(PALEGREEN, SEAGREEN))
                }, new Text
                {
                    text = "Quest"
                    style = "-fx-font-size: 100pt"
                    fill = new LinearGradient(
                        endX = 0,
                        stops = Stops(CYAN, DODGERBLUE))
                    effect = new DropShadow
                    {
                        color = DODGERBLUE
                        radius = 25
                        spread = 0.25
                    }
                })
            }
        }
    }

    new Main
}