package user_management.play.flow

case class FlowInfo(
   chunkSize: Int,
   totalSize: Long = 0L,
   identifier: String = null,
   filename: String = null,
   relativePath: String = null,
   filePath: String = null,
   totalChunks:Int =0,
   mimeType:Option[String] = None
                   
){

  type ResumableChunkNumber = Int

  var uploadedChunks: Set[Int] = Set[ResumableChunkNumber]()

  def valid: Boolean = {
    if (chunkSize < 0 || totalSize < 0 || identifier.isEmpty || filename.isEmpty || relativePath.isEmpty) false
    else true
  }

  def uploadFinished(currentFlowChunk:Int): Boolean = currentFlowChunk == totalChunks

}
