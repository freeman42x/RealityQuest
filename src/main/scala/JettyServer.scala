package com.github.razvanpanda.realityquest

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
            context.getSessionHandler.getSessionManager.setMaxInactiveInterval(-1) // TODO remove
            context.setContextPath("/")
            context.setInitParameter("org.scalatra.LifeCycle", "com.github.razvanpanda.realityquest.ScalatraBootstrap")

            if (getClass.getClassLoader.getResource("com/github/razvanpanda/realityquest/ScalatraBootstrap.class").getPath.startsWith("jar:"))
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

            server.setHandler(context)

            try
            {
                server.start()
                server.dump(System.err)
                server.join()
            }
            catch
            {
                case t: Throwable => t.printStackTrace(System.err)
            }
        }
    }
}