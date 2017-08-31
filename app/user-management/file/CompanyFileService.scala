package user_management.user.file

import java.nio.file.{StandardCopyOption, Paths}

import utilities.encryption.Random
import user_management.play.flow.FlowInfo
import user_management.user.UserManagementService
import user_management.user.models.CompanyFile
import com.google.inject.Inject
import play.api.Configuration
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.MySQLDriver.api._

class CompanyFileService @Inject()(userManagementService: UserManagementService,configuration:Configuration) extends FileManagement[CompanyFile]{

  override val path = configuration.getString("utils.files.storage").getOrElse("tmp/test/") + CompanyFile.tableName + "."
  val db = CompanyFile.db

  override def save(type_id:Long,company_id:Long,info:FlowInfo) = {

    val file = CompanyFile(None,company_id,Random.alphanumeric(24),info.filename,info.mimeType,type_id)

    CompanyFile.update(file,company_id).map{id =>

      java.nio.file.Files.move(Paths.get(info.filePath),Paths.get(path + id),StandardCopyOption.REPLACE_EXISTING)
      file.copy(id = Some(id))
    }
  }

  def listByType(type_id:Long) = db.run{

    CompanyFile.query.filter(_.type_id === type_id).result

  }

  def detail(id:Long) = db.run{

    CompanyFile.query.filter(_.id === id).result.headOption.map{_.map(f =>

      (new java.io.File(path + f.id.get),f))

    }

  }

  def delete(id:Long) = db.run{

    CompanyFile.query.filter(_.id === id).delete.map{_ =>

      java.nio.file.Files.delete(Paths.get(path + id))
      id

    }

  }

  def delete(id:Long,company_id:Long) = {

    CompanyFile.detail(id,company_id).flatMap(file =>

      CompanyFile.delete(id,company_id).map{_ =>

        file.foreach(f => java.nio.file.Files.delete(Paths.get(path + id)))
        id

      }
    )

  }

  def delete(ids:Seq[Long]) = db.run{

    CompanyFile.query.filter(f => ids.map(f.id === _).reduceLeft(_ || _)).delete.map{_ =>
      ids.foreach(id => java.nio.file.Files.delete(Paths.get(path + id)))
      ids
    }
  }

  def deleteAll(company_id:Long) = {

    CompanyFile.list(company_id).flatMap(files =>
      CompanyFile.deleteAll(company_id).map { _ =>
        files.foreach(f => java.nio.file.Files.delete(Paths.get(path + f.id.get)))
        files.map(_.id.get)
      }
    )
  }

}
