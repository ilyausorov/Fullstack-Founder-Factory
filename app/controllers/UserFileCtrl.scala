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

import models.UserFile
import java.io.FileNotFoundException

class UserFileCtrl @Inject()(userService: user_management.user.UserManagementService, helpers: user_management.play.utils.controller.FormHelper, actions: Actions) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._

  implicit val myJsonFormat = Json.format[database.UserFileRow]

  val table = UserFile.tableName

  def listing(user_id: Long) = IsAuthenticated.async{implicit request =>
    UserFile.list(user_id).map{x => Ok(Json.toJson(x))}
  }

  def insert(user_id: Long, type_id: Long) = (IsAuthenticated andThen IsAdmin)(parse.multipartFormData){implicit request =>
    if(insertBody(user_id, type_id, request.body.file("picture"))){
      Redirect(routes.UserCtrl.editM(user_id))
    }
    else{
      BadRequest("not an image")
    }
  }
	
  def insertBody(user_id: Long, type_id: Long, file: Option[play.api.mvc.MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]]) = file.map{f =>

      val key = utilities.encryption.Random.alphanumeric(12)
      UserFile.update(
        database.UserFileRow(None, user_id, key, f.filename, f.contentType, type_id),
        user_id
      ).map{c =>
        
        FileCtrl.upload(file, utilities.file.File.name(c, table))
      }
      true
    }.getOrElse{
      false
    }

  def delete(id: Long, user_id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    UserFile.detail(id, user_id).map{f =>
      f.map{a=>
        UserFile.delete(id, user_id)
        Redirect(routes.UserCtrl.editM(a.user_id))
      }.getOrElse(BadRequest("error"))
    }
  }

  def deleteLogin(id: Long) = IsAuthenticated.async{implicit request =>
    val user_id = request.user.id.get
    UserFile.detail(id).map{f => 
      f.filter(_.user_id == user_id).map{a=>
        UserFile.delete(id, user_id)
        Redirect(routes.PublicCtrl.index())
      }.getOrElse(BadRequest("error"))
    }
  }
	
  def serve(user_id: Long, inline: Boolean = false) = IsAuthenticated.async{implicit request =>
    helpers.single.bindFromRequest.fold(
      e => helpers.error[Long](e),
      v => UserFile.detail(v, user_id).map{f=>
        f.map{a =>
          serveFile(a, inline)
        }.getOrElse{
          BadRequest("file does not exist")
        }
      }
    )
  }

  def serveLogin(id: Long, inline: Boolean = true) = IsAuthenticated.async{implicit request =>
    val user_id = request.user.id.get
    UserFile.detail(id, user_id).map{f=>
      f.filter(_.user_id == user_id).map{a=>
        serveFile(a, inline)
      }.getOrElse{
        BadRequest("access denied")
	  }
    }
  }
	
	
  def serveFile(a: database.UserFileRow, inline: Boolean = true) = try{
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