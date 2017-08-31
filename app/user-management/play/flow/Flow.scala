package user_management.play.flow

import java.io.{File, FileInputStream, InputStream, RandomAccessFile}

import play.api.cache.CacheApi
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile
import play.api.mvc.BodyParsers.parse
import play.api.mvc._

import scala.concurrent.{Future, duration}
import scala.language.postfixOps

/**
 * Trait to manage flow-js server-sided. A working example/controller can be found under "test"
 * https://github.com/Kayrnt/NGFlowPlaySample
 */
trait Flow {

  val cache:CacheApi
  val baseDirectory: String
  val multipartMaxLengthParser: BodyParser[scala.Either[MaxSizeExceeded, MultipartFormData[Files.TemporaryFile]]] = parse.maxLength(1024 * 5000, parse.multipartFormData)

  protected val cacheKey = "flow_"

  /**
   * Retrieve the chunk number. Set to -1 if not present
   * @param request
   * @return
   */
  protected def getFlowChunkNumber(implicit request: RequestHeader): Int = request.getQueryString("flowChunkNumber").map(_.toInt).getOrElse(-1)

  protected def getFlowChunkNumber(multipart: MultipartFormData[TemporaryFile]): Int = multipart.asFormUrlEncoded.getOrElse("flowChunkNumber", Nil).headOption.map(_.toInt).getOrElse(-1)


  protected def checkFlowInfo(info: FlowInfo): FlowInfo = {
    if (!info.valid) {
      remove(info)
      throw new IllegalArgumentException("Invalid request params.")
    }
    else info
  } 

  protected def getFlowInfo(request: RequestHeader): FlowInfo = {

    val flowChunkSizeOpt = request.getQueryString("flowCurrentChunkSize")
    val flowTotalSizeOpt = request.getQueryString("flowTotalSize")
    val flowIdentifierOpt = request.getQueryString("flowIdentifier")
    val flowFilenameOpt = request.getQueryString("flowFilename")
    val flowRelativePathOpt = request.getQueryString("flowRelativePath")
    val flowTotalChunks = request.getQueryString("flowTotalChunks")

    val flowInfo = getInfo(flowChunkSizeOpt,flowTotalSizeOpt,flowIdentifierOpt,flowFilenameOpt,flowRelativePathOpt,flowTotalChunks,request.contentType)
    checkFlowInfo(flowInfo)
  }

  protected def getFlowInfo(file: MultipartFormData.FilePart[Files.TemporaryFile], multipart: MultipartFormData[TemporaryFile]): FlowInfo = {

    def getField(key: String) = multipart.asFormUrlEncoded.getOrElse(key, Nil).headOption

    val flowChunkSizeOpt = getField("flowCurrentChunkSize")
    val flowTotalSizeOpt = getField("flowTotalSize")
    val flowIdentifierOpt = getField("flowIdentifier")
    val flowFilenameOpt = getField("flowFilename")
    val flowRelativePathOpt = getField("flowRelativePath")
    val flowTotalChunks = getField("flowTotalChunks")

    val flowInfo = getInfo(flowChunkSizeOpt,flowTotalSizeOpt,flowIdentifierOpt,flowFilenameOpt,flowRelativePathOpt,flowTotalChunks,file.contentType)
    checkFlowInfo(flowInfo)

  }

  private def getInfo(chunkSize:Option[String], totalSize:Option[String], identifier:Option[String], filename:Option[String], relativePath:Option[String],totalChunks:Option[String],mimeTypeOpt:Option[String]) = {

    (chunkSize, totalSize, identifier, filename, relativePath,totalChunks) match{

      case (Some(cSize), Some(tSize), Some(id), Some(name), Some(relPath),Some(totChunks)) =>

        lazy val flowFilePath = new File(baseDirectory, name).getAbsolutePath

        cache.getOrElse(cacheKey + id,duration.DurationInt(1800) seconds){

          FlowInfo(cSize.toInt,tSize.toInt,id,name,relPath,flowFilePath,totChunks.toInt,mimeTypeOpt)

        }

      case _ => throw new IllegalArgumentException("Invalid request params.")

    }
  }

  protected def writeToTempFile(flowChunkNumber: Int, info: FlowInfo, contentLength: Long, input: InputStream) = {

    val raf: RandomAccessFile = new RandomAccessFile(info.filePath, "rw")
    raf.seek((flowChunkNumber - 1) * info.chunkSize)

    var readData: Long = 0
    val buffer: Array[Byte] = new Array[Byte](1024 * 100)
    var r: Int = 0
    do {
      r = input.read(buffer)
      if (r > 0) {
        raf.write(buffer, 0, r)
        readData += r
      }
    }
    while (readData < contentLength && r > 0)
    raf.close()
    input.close()

  }

  protected def processChunk(file: MultipartFormData.FilePart[Files.TemporaryFile], multipart: MultipartFormData[TemporaryFile],onUploadCompleted: FlowInfo => Future[Result],onUploading:FlowInfo => Future[Result])(implicit request: RequestHeader) = {

    val flowChunkNumber: Int = getFlowChunkNumber(multipart)
    val info: FlowInfo = getFlowInfo(file,multipart) //Get flow info from map or request
    val contentLength: Long = request.headers("Content-Length").toLong

    writeToTempFile(flowChunkNumber, info, contentLength, new FileInputStream(file.ref.file))
    info.uploadedChunks += flowChunkNumber

    if (info.uploadFinished(flowChunkNumber)) {
      remove(info)
      onUploadCompleted(info)
    }
    else onUploading(info)

  }

  private def remove(info:FlowInfo) = cache.remove(cacheKey + info.identifier)

}
