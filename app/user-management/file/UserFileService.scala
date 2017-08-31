package user_management.user.file

import user_management.play.flow.FlowInfo
import user_management.user.models.CompanyFile
import com.google.inject.Inject
import play.api.Configuration

import scala.concurrent.Future

class UserFileService @Inject()(configuration:Configuration) extends FileManagement[CompanyFile]{

  override val path: String = configuration.getString("something").getOrElse("somethingElse")

  override def deleteAll(user_id: Long)  = ???

  override def listByType(type_id: Long) = ???

  override def delete(id: Long): Future[Long] = ???

  override def delete(id: Long, user_id: Long) = ???

  override def delete(ids: Seq[Long]): Future[Seq[Long]] = ???

  override def detail(id: Long) = ???

  override def save(type_id: Long, user_id: Long, info: FlowInfo) = ???
}
