package com.github.razvanpanda.realityquest

import scala.slick.driver.H2Driver.simple._

class UserSettingTable(tag: Tag) extends Table[UserSetting](tag, "UserSettings")
{
    def Id = column[Option[Int]]("Id", O.PrimaryKey, O.AutoInc)
    def IdleStatusModuleIsActive = column[Boolean]("IdleStatusModuleIsActive")
    def * = (Id, IdleStatusModuleIsActive) <> (UserSetting.tupled, UserSetting.unapply)
}