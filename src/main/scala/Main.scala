//import org.joda.time.DateTime
import scala.sys.process._
import scala.concurrent.duration._
import scala.slick.driver.H2Driver.simple._
//import com.github.tototoshi.slick.JodaSupport._

object Main extends App
{
    val connection = "jdbc:h2:db/database;AUTO_SERVER=TRUE"
    case class LogItem(Id: Option[Int] = None, WindowTitle: String, WindowClass: String)

    class LogItems(tag: Tag) extends Table[LogItem](tag, "LogItems")
    {
        def Id = column[Option[Int]]("Id", O.PrimaryKey, O.AutoInc)
        //def LogDateTime = column[DateTime]("LogDateTime")
        def WindowTitle = column[String]("WindowTitle")
        def WindowClass = column[String]("WindowClass")
        def * = (Id, WindowTitle, WindowClass) <> (LogItem.tupled, LogItem.unapply)
    }

    val logItems = TableQuery[LogItems]

//    Database.forURL(connection, driver = "org.h2.Driver") withSession
//    {
//        implicit session =>
//            logItems.ddl.create
//    }

    val system = akka.actor.ActorSystem("system")
    import system.dispatcher
    system.scheduler.schedule(0 milliseconds, 2000 milliseconds, new Runnable()
    {
        def run(): Unit =
        {
            Database.forURL(connection, driver = "org.h2.Driver") withSession
            {
                implicit session =>
                    val rawData: String = "ActiveWindowTitle.exe".!!
                    val data = rawData.split("[\\r\\n]+")
                    if (data.length == 0) return
                    val windowTitle = if (data.length >= 1) data(0).trim else ""
                    val windowClass = if (data.length >= 2) data(1).trim else ""
                    logItems += LogItem(None, windowTitle, windowClass)
            }
        }
    })
}