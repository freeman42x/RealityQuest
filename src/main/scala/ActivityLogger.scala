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
import com.github.razvanpanda.realityquest.ActivityLogger.Log
import org.scalatra.atmosphere.{JsonMessage, AtmosphereClient}
import org.json4s._
import org.json4s.ext.JodaTimeSerializers

object ActivityLogger
{
    case class Log()
}

class ActivityLogger extends Actor
{
    import context.dispatcher
    val connection = "jdbc:h2:db/database;AUTO_SERVER=TRUE"
    val driver = "org.h2.Driver"

    val logItems = TableQuery[LogItemTable]
    val userSettings = TableQuery[UserSettingTable]

    var previousLogItemOption: Option[LogItem] = None

    Database.forURL(connection, driver = driver) withSession
    {
        implicit session =>
            if (!new File("db/database.h2.db").exists())
            {
                logItems.ddl.create
                userSettings.ddl.create
            }

            previousLogItemOption = logItems.sortBy(_.BeginDateTime.desc).firstOption
    }

    var collectData: LogItem = null

    val windowStatusActor = context.actorOf(Props[WindowStatus])
    val lockStatusActor = context.actorOf(Props[LockStatus])
    // val idleStatusActor = context.actorOf(Props[IdleStatus]) TODO activate from settings
    def receive =
    {
        case Log =>
            implicit val timeout = Timeout(2000, MILLISECONDS)
            val f1 = windowStatusActor ? GetData
            val f2 = lockStatusActor ? GetLockStatus
            // val f3 = idleStatusActor ? GetIdleStatus
            val future: Future[(LogItem, Boolean)] = for // , Boolean
            {
                logItemData <- f1.mapTo[LogItem]
                isLocked <- f2.mapTo[Boolean]
            //    isIdle <- f3.mapTo[Boolean]
            } yield (logItemData, isLocked)//, isIdle)

            future.onSuccess
            {
                case (logItemData: LogItem, isLocked: Boolean) => //, isIdle: Boolean) =>
                    Database.forURL(connection, driver = driver) withSession
                    {
                        implicit session =>
                            var currentLogItem: LogItem = null
                            if (isLocked)
                            {
                                val dateTimeNow = DateTime.now
                                currentLogItem = LogItem(None, dateTimeNow, dateTimeNow, "Lock Screen", "")
                            }
                            else
                                currentLogItem = logItemData

                            if (currentLogItem.WindowTitle.isEmpty) currentLogItem = currentLogItem.copy(WindowTitle = "Empty Window Text")

                            def saveNewLogItem()
                            {
                                val newLogItemId = logItems returning logItems.map(_.Id) += currentLogItem
                                previousLogItemOption = Option(currentLogItem.copy(Id = newLogItemId))
                            }

                            previousLogItemOption match
                            {
                                case Some(previousLogItem: LogItem) =>
                                    val isSameActiveWindow = previousLogItem.WindowTitle == currentLogItem.WindowTitle && previousLogItem.WindowClass == currentLogItem.WindowClass
                                    if (isSameActiveWindow)
                                    {
                                        val updatedLogItem = previousLogItem.copy(EndDateTime = DateTime.now)
                                        logItems.where(_.Id === previousLogItem.Id).update(updatedLogItem)
                                        previousLogItemOption = Option(updatedLogItem)
                                    }
                                    else
                                    {
                                        saveNewLogItem()
                                        implicit val formats = DefaultFormats ++ JodaTimeSerializers.all
                                        AtmosphereClient.broadcastAll(JsonMessage(Extraction.decompose(currentLogItem)))
                                    }

                                case None => saveNewLogItem()
                            }
                    }
                case _ =>
                    println("success")
            }
    }
}