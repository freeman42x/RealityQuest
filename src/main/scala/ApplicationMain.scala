package com.github.razvanpanda.realityquest

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene
import scene.effect._
import scene.paint.{Stops, LinearGradient}
import scene.text.Text
import javafx.application.Platform
import java.awt._
import javafx.stage.{WindowEvent, Stage}
import javax.imageio.ImageIO
import javafx.event.EventHandler
import java.awt.event.{ActionEvent, ActionListener}

object ApplicationMain extends JFXApp
{
    private var firstTime = false
    private var trayIcon: TrayIcon = null

    def start() =
    {
        stage = new PrimaryStage
        {
            title = "RealityQuest"
            width = 600
            height = 170
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

        createTrayIcon(stage)
        firstTime = true
        Platform.setImplicitExit(false)
        hide(stage)
    }

    private def createTrayIcon(stage: Stage)
    {
        if (SystemTray.isSupported)
        {
            val tray: SystemTray = SystemTray.getSystemTray
            var image: Image = null
            image = ImageIO.read(getClass.getResource("/TrayIcon.png").openStream())

            stage.setOnCloseRequest(new EventHandler[WindowEvent]
            {
                def handle(t: WindowEvent)
                {
                    hide(stage)
                }
            })

            val closeListener: ActionListener = new ActionListener
            {
                def actionPerformed(e: ActionEvent)
                {
                    System.exit(0)
                }
            }

            val showListener: ActionListener = new ActionListener
            {
                def actionPerformed(e: ActionEvent)
                {
                    Platform.runLater(new Runnable
                    {
                        def run()
                        {
                            stage.show()
                        }
                    })
                }
            }

            val popup: PopupMenu = new PopupMenu
            val showItem: MenuItem = new MenuItem("Show")
            showItem.addActionListener(showListener)
            popup.add(showItem)
            val closeItem: MenuItem = new MenuItem("Close")
            closeItem.addActionListener(closeListener)
            popup.add(closeItem)
            trayIcon = new TrayIcon(image, "Title", popup)
            trayIcon.setImageAutoSize(true)
            trayIcon.addActionListener(showListener)
            try
            {
                tray.add(trayIcon)
            }
            catch
            {
                case e: AWTException => System.err.println(e)
            }
        }
    }

    private def hide(stage: Stage)
    {
        Platform.runLater(new Runnable
        {
            def run()
            {
                if (SystemTray.isSupported)
                {
                    stage.hide()
                    if (firstTime)
                    {
                        trayIcon.displayMessage(
                            "Minimized to tray.",
                            """Double click to open.
                              |Right click for context menu.""".stripMargin,
                            TrayIcon.MessageType.INFO)
                        firstTime = false
                    }
                }
                else
                {
                    System.exit(0)
                }
            }
        })
    }

    start()
    ActivityLogger.start()
    JettyServer.start()
}