package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._ 

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models._

class NewsletterCtrl extends Controller{
  def subscribe = Action{implicit request =>
    myForm.bindFromRequest.fold(
      e => Redirect(routes.PublicCtrl.index.toString+"#newsletter").flashing("error"  -> "newsletter"),
      v => {
        Future{
          Newsletter.update(Newsletter(None, v)).map{nid =>
            Newsletter.detail(nid).map{fd =>
              fd.map{n => 
                val content = views.html.email_templates.newsletter(n).toString
                val a = new utilities.Email(Seq(v), "Thanks for signing up to the "+utilities.Constants.name+" newsletter", content, true)
                a.setFrom(utilities.Constants.name+" "+"<"+utilities.Constants.email+">")
				a.send
              }
            } 
          }          
        }

        Redirect(routes.PublicCtrl.index.toString+"#newsletter").flashing("success"  -> "newsletter")
      }
    )
  }

  val myForm = Form(
    single("email" -> email)
  )

  def changeStatus(id: Long, key: String) = Action{implicit request=>
    Newsletter.changeStatus(id, key)
    Ok(views.html.other_pages.newsletter_confirm())
  }

  def export = Action.async{implicit request=>
    Newsletter.list.map{fa => 
      Ok("email,date,status\n"+{fa.map{a =>
        a.email+","+utilities.Date.str(utilities.Date.datetimeToDate(a.date_added), utilities.Date.Pattern.datetime)+","+{a.status_id match{
          case 1 => "confirmed"
          case _ => ""
        }}
      }
      .mkString("\n")
      })
    .withHeaders(CONTENT_DISPOSITION -> {"attachment; filename=export.csv"})
    }
  }
}