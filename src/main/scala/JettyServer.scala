package com.github.razvanpanda.realityquest

import com.github.razvanpanda.realityquest.servlets.ApiServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import scala.concurrent.{Future, ExecutionContext}

object JettyServer
{
    def start() =
    {
        import ExecutionContext.Implicits.global
        Future
        {
            val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080
            val server = new Server(port)
            val context = new WebAppContext()
            context setContextPath "/"

            if (getClass.getClassLoader.getResource("ScalatraBootstrap.class").getPath.startsWith("jar:"))
            {
                val webappPath = getClass.getClassLoader.getResource("webapp").toExternalForm
                context.setResourceBase(webappPath)
            }
            else
            {
                context.setResourceBase("src/main/resources_web/webapp")
                context.setInitParameter("org.eclipse.jetty.servlet.Default.maxCachedFiles", "0")
            }

            context.addEventListener(new ScalatraListener)
            context.addServlet(classOf[ApiServlet], "/api/*")

            server.setHandler(context)

            server.start()
            server.join()
        }
    }
}