import scala.sys.process._
import scala.concurrent.duration._
import scala.slick.driver.H2Driver.simple._
import com.github.nscala_time.time.Imports._
import com.github.tototoshi.slick.JodaSupport._

object Main extends App
{
    val connection = "jdbc:h2:db/database;AUTO_SERVER=TRUE"
    val driver = "org.h2.Driver"
    case class LogItem(Id: Option[Int] = None, BeginDateTime: DateTime, EndDateTime: DateTime, WindowTitle: String, WindowClass: String)

    class LogItems(tag: Tag) extends Table[LogItem](tag, "LogItems")
    {
        def Id = column[Option[Int]]("Id", O.PrimaryKey, O.AutoInc)
        def BeginDateTime = column[DateTime]("BeginDateTime")
        def EndDateTime = column[DateTime]("EndDateTime")
        def WindowTitle = column[String]("WindowTitle")
        def WindowClass = column[String]("WindowClass")
        def * = (Id, BeginDateTime, EndDateTime, WindowTitle, WindowClass) <> (LogItem.tupled, LogItem.unapply)
    }

    val logItems = TableQuery[LogItems]

//    Database.forURL(connection, driver = driver) withSession
//    {
//        implicit session =>
//            logItems.ddl.create
//    }

    var previousLogItem: Option[LogItem] = None
    Database.forURL(connection, driver = driver) withSession
    {
        implicit session =>
            previousLogItem = logItems.sortBy(_.BeginDateTime.desc).firstOption
    }

    val system = akka.actor.ActorSystem("system")
    import system.dispatcher
    system.scheduler.schedule(0 milliseconds, 1000 milliseconds, new Runnable()
    {
        def run(): Unit =
            Database.forURL(connection, driver = driver) withSession
            {
                implicit session =>
                    val rawData: String = "ActiveWindowTitle.exe".!!
                    val data = rawData.split("[\\r\\n]+")
                    if (data.length == 0) return
                    val windowTitle = if (data.length >= 1) data(0).trim else ""
                    val windowClass = if (data.length >= 2) data(1).trim else ""

                    def saveNewLogItem()
                    {
                        val currentDateTime = DateTime.now
                        val newLogItem = LogItem(None, currentDateTime, currentDateTime, windowTitle, windowClass)
                        val newLogItemId = (logItems returning logItems.map(_.Id)) += newLogItem
                        previousLogItem = Option(newLogItem.copy(Id = newLogItemId))
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