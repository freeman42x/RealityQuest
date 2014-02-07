package com.github.razvanpanda.realityquest.sensors

import com.sun.jna.platform.win32.User32
import com.github.nscala_time.time.Imports._
import com.github.razvanpanda.realityquest.LogItem
import com.sun.jna.Native
import akka.actor.Actor
import com.github.razvanpanda.realityquest.sensors.WindowStatus.GetData

object WindowStatus
{
    case class GetData()
}

class WindowStatus extends Actor
{
    def receive: Actor.Receive =
    {
        case GetData =>
            val hwnd = User32.INSTANCE.GetForegroundWindow
            val windowTextLength = User32.INSTANCE.GetWindowTextLength(hwnd) + 1
            val windowText = new Array[Char](windowTextLength)
            User32.INSTANCE.GetWindowText(hwnd, windowText, windowTextLength)
            val windowClass = new Array[Char](256)
            User32.INSTANCE.GetClassName(hwnd, windowClass, 256)
            val dateTimeNow = DateTime.now
            sender ! LogItem(None, dateTimeNow, dateTimeNow, Native.toString(windowText),Native.toString(windowClass))
    }
}