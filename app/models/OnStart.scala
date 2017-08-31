package models

import database._
import user_management.user._
import play.Logger
import slick.driver.MySQLDriver.api._
class OnStart {
  def evolutions{
    List(
        TableQuery[NewsletterDB]   
    ) 
  }
  com.typesafe.config.ConfigFactory.invalidateCaches()
  play.Logger.info("Application has started")
  evolutions
}