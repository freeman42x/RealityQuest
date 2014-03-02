package com.github.razvanpanda.realityquest.sensors

import java.awt._
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import scala.concurrent.duration._
import akka.actor.{ActorLogging, Actor}
import com.github.razvanpanda.realityquest.sensors.IdleStatus.GetIdleStatus

object IdleStatus
{
    case class GetIdleStatus()
}

class IdleStatus extends Actor with ActorLogging
{
    object IdleDetection
    {
        var isIdle = false
        private val robot = new Robot
        private var screenDimensions: Rectangle = null

        var screenWidth = 0
        var screenHeight = 0
        val graphicsEnv: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment
        val graphicsDevices: Array[GraphicsDevice] = graphicsEnv.getScreenDevices

        for (screens <- graphicsDevices)
        {
            val mode: DisplayMode = screens.getDisplayMode
            screenWidth += mode.getWidth
            if (mode.getHeight > screenHeight)
            {
                screenHeight = mode.getHeight
            }
        }
        screenDimensions = new Rectangle(0, 0, screenWidth, screenHeight)

        val threshHold = 0.05
        var screenShot: BufferedImage = null
        var screenShotPrev = robot.createScreenCapture(screenDimensions)

        val system = akka.actor.ActorSystem("system")
        import system.dispatcher
        system.scheduler.schedule(0.milliseconds, 1000.milliseconds, new Runnable()
        {
            def run() =
            {
                screenShot = robot.createScreenCapture(screenDimensions)
                isIdle = compareScreens(screenShotPrev, screenShot) < threshHold
                screenShotPrev = screenShot
                log.info("idle = " + isIdle)
            }
        })

        private def compareScreens(screen1: BufferedImage, screen2: BufferedImage) =
        {
            var pixelDifferenceCount: Int = 0
            val s1: Array[Int] = screen1.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData
            val s2: Array[Int] = screen2.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData
            var i: Int = 0
            while (i < s1.length)
            {
                if (s1(i) != s2(i))
                {
                    pixelDifferenceCount += 1
                }

                i += 1
            }

            log.info("difference count = " + pixelDifferenceCount)
            pixelDifferenceCount.asInstanceOf[Double] / (screen1.getHeight * screen1.getWidth).asInstanceOf[Double] * 100
        }
    }

    def receive: Actor.Receive =
    {
        case GetIdleStatus =>
            sender ! IdleDetection.isIdle
    }
}