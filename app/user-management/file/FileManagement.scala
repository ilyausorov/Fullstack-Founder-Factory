package user_management.user.file

import java.io.File

import user_management.play.flow.FlowInfo

import scala.concurrent.Future

trait FileManagement[A] {

  val path:String


  def save(type_id:Long,userOrCompanyId:Long,info:FlowInfo):Future[A]

  def listByType(type_id:Long):Future[Seq[A]]

  def detail(id:Long):Future[Option[(File,A)]]

  def delete(id:Long):Future[Long]

  def delete(id:Long,userOrCompanyId:Long):Future[Long]

  def delete(ids:Seq[Long]):Future[Seq[Long]]

  def deleteAll(userOrCompanyId:Long):Future[Seq[Long]]

}
