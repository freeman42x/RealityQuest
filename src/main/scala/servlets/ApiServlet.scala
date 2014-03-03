package com.github.razvanpanda.realityquest.servlets

import org.scalatra._
import org.json4s._
import org.scalatra.json._
import org.json4s.ext.JodaTimeSerializers
import scala.slick.driver.H2Driver.simple._
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import com.github.tototoshi.slick.H2JodaSupport._
import org.scalatra.atmosphere._
import com.github.razvanpanda.realityquest.LogItemTable

class ApiServlet extends ScalatraServlet with JacksonJsonSupport with AtmosphereSupport
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

        val logItems = TableQuery[LogItemTable]

        Database.forURL(connection, driver = driver) withSession
        {
            implicit session =>
            val beginDateTime = if (DateTime.now.withHourOfDay(6) > DateTime.now) DateTime.now.withHourOfDay(6).minusDays(1) else DateTime.now.withHourOfDay(6)
            val endDateTime = DateTime.now

            Map("LogItems" -> logItems.filter(logItem => logItem.BeginDateTime >= beginDateTime && logItem.BeginDateTime < endDateTime).list)
        }
    }

    atmosphere("/log")
    {
        new AtmosphereClient
        {
            def receive =
            {
                case Disconnected(disconnector, Some(error)) => println("Atmosphere disconnected: " + error)
                case Error(Some(error)) => println(error)
            }
        }
    }

    error
    {
        case t: Throwable => t.printStackTrace()
    }
}