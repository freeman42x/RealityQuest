package com.github.razvanpanda.RealityQuest

import scala.slick.driver.H2Driver.simple._
import com.github.nscala_time.time.Imports._
import com.github.tototoshi.slick.H2JodaSupport._

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