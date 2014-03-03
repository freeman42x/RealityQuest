package com.github.razvanpanda.realityquest

import org.scalatra._
import javax.servlet.ServletContext
import com.github.razvanpanda.realityquest.servlets.ApiServlet

class ScalatraBootstrap extends LifeCycle
{
    override def init(context: ServletContext)
    {
        context.mount(new ApiServlet, "/api/*")
    }
}