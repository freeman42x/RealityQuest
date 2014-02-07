package com.github.razvanpanda.realityquest

import java.io.File
import scala.concurrent.duration._
import scala.slick.driver.H2Driver.simple._
import com.github.nscala_time.time.Imports._
import com.github.razvanpanda.realityquest.sensors.{IdleStatus, WindowStatus, LockStatus}
import akka.actor.{Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Future
import com.github.razvanpanda.realityquest.sensors.LockStatus.GetLockStatus
import com.github.razvanpanda.realityquest.sensors.WindowStatus.GetData
import com.github.razvanpanda.realityquest.sensors.IdleStatus.GetIdleStatus

class ActivityLogger extends Actor
{
    import context.dispatcher
    val connection = "jdbc:h2:db/database;AUTO_SERVER=TRUE"
    val driver = "org.h2.Driver"

    val logItems = TableQuery[LogItemTable]

    var previousLogItem: Option[LogItem] = None

    Database.forURL(connection, driver = driver) withSession
    {
        implicit session =>
            if (!new File("db/database.h2.db").exists()) logItems.ddl.create
            previousLogItem = logItems.sortBy(_.BeginDateTime.desc).firstOption
    }

    var collectData: LogItem = null

    val windowStatusActor = context.actorOf(Props[WindowStatus])
    val lockStatusActor = context.actorOf(Props[LockStatus])
    val idleStatusActor = context.actorOf(Props[IdleStatus])
    def receive =
    {
        case "log" =>
            implicit val timeout = Timeout(2000, MILLISECONDS)
            val f1 = windowStatusActor ? GetData
            val f2 = lockStatusActor ? GetLockStatus
            val f3 = idleStatusActor ? GetIdleStatus
            val future: Future[(LogItem, Boolean, Boolean)] = for
            {
                logItemData <- f1.mapTo[LogItem]
                isLocked <- f2.mapTo[Boolean]
                isIdle <- f3.mapTo[Boolean]
            } yield (logItemData, isLocked, isIdle)

            future.onSuccess
            {
                case (logItemData: LogItem, isLocked: Boolean, isIdle: Boolean) =>
                    Database.forURL(connection, driver = driver) withSession
                    {
                        implicit session =>
                            var logItem: LogItem = null
                            if (isLocked)
                            {
                                val dateTimeNow = DateTime.now
                                logItem = LogItem(None, dateTimeNow, dateTimeNow, "Lock Screen", "")
                            }
                            else
                                logItem = logItemData

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

                                case None => saveNewLogItem()
                            }
                    }
                case _ =>
                    println("success")
            }
    }
}