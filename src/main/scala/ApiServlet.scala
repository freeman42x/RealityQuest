package com.github.razvanpanda.RealityQuest

import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.json4s.ext.JodaTimeSerializers
import scala.slick.driver.H2Driver.simple._
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import com.github.tototoshi.slick.H2JodaSupport._

class ApiServlet extends ScalatraServlet with JacksonJsonSupport
{
    protected implicit val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all

    before()
    {
        contentType = formats("json")
    }

    get("/reports/home")
    {
        val connection = "jdbc:h2:db/database;AUTO_SERVER=TRUE"
        val driver = "org.h2.Driver"

        val logItems = TableQuery[LogItems]

        Database.forURL(connection, driver = driver) withSession
        {
            implicit session =>
//            val now = DateTime.now
//            val today = now.toLocalDate
//            val tomorrow = today.plusDays(1)
//
//            val startOfToday = today.toDateTimeAtStartOfDay(now.getZone)
//            val startOfTomorrow = tomorrow.toDateTimeAtStartOfDay(now.getZone)

            val beginDateTime = if (DateTime.now.withHourOfDay(6) > DateTime.now) DateTime.now.withHourOfDay(6).minusDays(1) else DateTime.now.withHourOfDay(6)
            val endDateTime = DateTime.now

            Map("LogItems" -> logItems.filter(logItem => logItem.BeginDateTime >= beginDateTime && logItem.BeginDateTime < endDateTime).list)
        }
    }
}