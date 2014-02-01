package com.github.razvanpanda.realityquest

import com.github.nscala_time.time.Imports._

case class LogItem
(
    Id: Option[Int] = None,
    BeginDateTime: DateTime,
    EndDateTime: DateTime,
    WindowTitle: String,
    WindowClass: String
)