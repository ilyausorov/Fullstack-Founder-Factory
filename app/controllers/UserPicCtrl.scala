package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._

import play.api.Play.current

import play.api.libs.json.Json
import play.api.libs.json.JsValue

import javax.inject.Inject

import user_management.user.authentication.AuthenticationService
import user_management.user.models.{Login, Permission}
import play.api.i18n.{Lang, MessagesApi, Messages, I18nSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.UserPic

class UserPicCtrl @Inject()(userService: user_management.user.UserManagementService, helpers: user_management.play.utils.controller.FormHelper, actions: Actions) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._

  val table = UserPic.tableName

  /*
    insert image when user is logged in (any user_id can be spceified)
    for admin
  */
  def insert(user_id: Long, type_id: Long) = (IsAuthenticated andThen IsAdmin)(parse.multipartFormData){implicit request =>
    if(insertBody(user_id, type_id, request.body.file("picture"))){
      Redirect(routes.UserCtrl.editM(user_id))
    }
    else{
      BadRequest("not an image")
    }
  }

  /*
    insert image when user is logged in (no user_id)
    @param type_id: type_id defines
  */
  def insertLogin(type_id: Long) = IsAuthenticated(parse.multipartFormData){implicit request =>
    val user_id = request.user.id.get
    if(insertBody(user_id, type_id, request.body.file("picture"))){

      Redirect(routes.PublicCtrl.index())
    }
    else{
      BadRequest("not an image")
    }
  }

  /*
    do the insert independently if comes from admin or user
  */
  def insertBody(user_id: Long, type_id: Long, file: Option[play.api.mvc.MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]]) = {
    if(file.get.contentType.get.split("/")(0)=="image"){
      val key = utilities.encryption.Random.alphanumeric(12)
      val f = UserPic.update(
        database.UserPicRow(None, user_id, key, "profile", Some("image/png"), type_id),
        user_id
      ).map{c =>
        
        FileCtrl.upload(file, utilities.file.File.name(c, table))
      }
      true
    }
    else{
      false
    }
  }

  def delete(id: Long, user_id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    UserPic.detail(id, user_id).map{f =>
      f.map{a=>
        UserPic.delete(id, user_id)
        Redirect(routes.UserCtrl.editM(a.user_id))
      }.getOrElse(BadRequest("error"))
    }
  }

  def deleteLogin(id: Long) = IsAuthenticated.async{implicit request =>
    val user_id = request.user.id.get
    UserPic.detail(id).map{f => 
      f.filter(_.user_id == user_id).map{a=>
        UserPic.delete(id, user_id)
        Redirect(routes.LoginCtrl.profile())
      }.getOrElse(BadRequest("error"))
    }
  }

  def serve(id: Long, user_id: Long, inline: Boolean = true) = IsAuthenticated.async{implicit request =>
    UserPic.detail(id, user_id).map{f=>
      f.map{a =>
        serveFile(a, inline)
      }.getOrElse{
        BadRequest("file does not exist")
      }
    }
  }

  def serveLogin(id: Long, inline: Boolean = true) = IsAuthenticated.async{implicit request =>
    val user_id = request.user.id.get
    UserPic.detail(id, user_id).map{f=>
      f.filter(_.user_id == user_id).map{a=>
        serveFile(a, inline)
      }.getOrElse{
        BadRequest("access denied")
      }
    }
  }

  def serveFile(a: database.UserPicRow, inline: Boolean = true) = {
    import java.io.FileNotFoundException
    try{
      Ok.sendFile(
        // todo :: here tsble name
          content = new java.io.File(utilities.file.File.name(a.id.get, table))
        , fileName = _ => a.name
        , inline = inline
      ).as(a.contentType.getOrElse("image"))
    }
    catch{  
      case ex: FileNotFoundException=> {
        BadRequest("file does not exist")
      }
    }
  }
}