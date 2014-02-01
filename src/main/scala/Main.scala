package com.github.razvanpanda.RealityQuest

import java.io.File
import scala.concurrent.duration._
import scala.slick.driver.H2Driver.simple._
import com.github.nscala_time.time.Imports._
import com.sun.jna._
import com.sun.jna.platform.win32.User32

class Main
{
    val connection = "jdbc:h2:db/database;AUTO_SERVER=TRUE"
    val driver = "org.h2.Driver"

    LockStatus.start()

    def CollectData() =
    {
        val hwnd = User32.INSTANCE.GetForegroundWindow
        val windowTextLength = User32.INSTANCE.GetWindowTextLength(hwnd) + 1
        val windowText = new Array[Char](windowTextLength)
        User32.INSTANCE.GetWindowText(hwnd, windowText, windowTextLength)
        val windowClass = new Array[Char](256)
        User32.INSTANCE.GetClassName(hwnd, windowClass, 256)
        val dateTimeNow = DateTime.now
        LogItem(None, dateTimeNow, dateTimeNow, Native.toString(windowText),Native.toString(windowClass))
    }

    val logItems = TableQuery[LogItems]

    var previousLogItem: Option[LogItem] = None
    Database.forURL(connection, driver = driver) withSession
    {
        implicit session =>
            if (!new File("db/database.h2.db").exists()) logItems.ddl.create
            previousLogItem = logItems.sortBy(_.BeginDateTime.desc).firstOption
    }

    val system = akka.actor.ActorSystem("system")
    import system.dispatcher
    system.scheduler.schedule(0 milliseconds, 1000 milliseconds, new Runnable()
    {
        def run(): Unit = Database.forURL(connection, driver = driver) withSession
        {
            implicit session =>
            var logItem: LogItem = null
            if (LockStatus.isLocked)
            {
                val dateTimeNow = DateTime.now
                logItem = LogItem(None, dateTimeNow, dateTimeNow, "Lock Screen", "")
            }
            else
                logItem = CollectData()

            if (logItem.WindowTitle.isEmpty) logItem = logItem.copy(WindowTitle = "Empty Window Text")

            val windowTitle = logItem.WindowTitle
            val windowClass = logItem.WindowClass

            def saveNewLogItem()
            {
                val newLogItemId = logItems returning logItems.map(_.Id) += logItem
                previousLogItem = Option(logItem.copy(Id = newLogItemId))
            }

            previousLogItem match
            {
                case Some(logItem: LogItem) =>
                    val sameApplication = logItem.WindowTitle == windowTitle && logItem.WindowClass == windowClass
                    if (sameApplication)
                    {
                        val updatedLogItem = logItem.copy(EndDateTime = DateTime.now)
                        logItems.where(_.Id === logItem.Id).update(updatedLogItem)
                        previousLogItem = Option(updatedLogItem)
                    }
                    else saveNewLogItem()

                case None =>  saveNewLogItem()
            }
        }
    })
}