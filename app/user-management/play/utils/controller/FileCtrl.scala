package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._ 


import java.util.Date._

import models._
import views._

import play.api.Play.current


object FileCtrl extends Controller {

	def upload(request: play.api.mvc.Request[play.api.libs.Files.TemporaryFile], id: Long, table: String) : Boolean = {

		import utilities.file.File._
		import java.io.File
		try{
			val file = new File(utilities.file.File.name(id, table))
			request.body.moveTo(file, true)
			true
		}
		catch{
			case e: Exception => false
			case _: Throwable => false
		}
	}

	def upload(f: Option[play.api.mvc.MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]], filepath: String){
			
			f.map { picture =>
			import java.io.File		
			val file = new File(filepath)
			try {
				picture.ref.moveTo(file, true)
				true
			}
			catch{
				case e: Exception => false
			}
		}
	}
}