package utilities.file

// todo: package name is ridiculous, change to utils

object File{
	import java.io.File

	def name(id: Long, table: String) : String = {
		utilities.Constants.path+"/"+table+"."+id.toString
	}

	/*
		http://www.tutorialspoint.com/java/java_filewriter_class.htm
	*/
	def write(path: String, content: String):Boolean = {
		import java.io.FileWriter

    	try{
	    	val file = new File(path)
			file.createNewFile()

			val writer:FileWriter = new FileWriter(file)

			// Writes the content to the file
			writer.write(content)
			writer.flush()
			writer.close()

			true
		}
		catch{
			case _:Throwable => false
		}
	}

  def writeBuffer(path: String, content: String):Boolean = {
    import java.io.{FileOutputStream, OutputStreamWriter, BufferedWriter}
    try{
      val bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))
      bw.write(content)
      bw.close()
      true
    }
    catch{
      case _:Throwable => false
    }
  }

	def read(path: String):String = try{
		scala.io.Source.fromFile(path).mkString
	}
	catch{
		case _: Throwable => {
			println("File does not exist")
			""
		}
	}

  // todo: test
  /**
   * creates directory
   */
  def createDir(path: String):Boolean = if((new java.io.File(path)).mkdir()){
      true
    }
    else{
      false
    }

  // todo: test
  /**
   * append content to exsiting file
   */
  def append(path: String, s: String):Boolean = try {
    import java.io.FileWriter
    import java.io.BufferedWriter

    val fileWritter = new FileWriter(path,true)
    val bufferWritter:BufferedWriter = new BufferedWriter(fileWritter)
    bufferWritter.write(s)
    bufferWritter.close()
    true
  }
  catch{
    case _:Throwable => false
  }

  /**
   * read files by line and applies function
   * @param path: filepath
   * @param fx: function to be applied to line (by default, just returns it)
   * @param dropLines: number of lines to be ignored at beginning of files (e.g. header)
   * @return iterator of A
   */
  def readByLine[A](path: String, fx: String => A = (x:String)=> x, dropLines:Int = 0):Iterator[A] = {
    scala.io.Source.fromFile(path)
      .getLines
      .drop(dropLines)
      .map{line =>
        fx(line)
      }
  }

  /**
   * parse a CSV line
   * @param s: input string
   * @param r: regexp
   */
  def parseCSVLine(s: String, r: String = """,(?=([^\"]*\"[^\"]*\")*[^\"]*$)"""):Array[String] = s.split(r)

	def delete(path: String):Boolean={
		try{
			(new File(path)).delete
		}
		catch{
			case _:Throwable => false
		}
	}

	/**
   *check it file path exists
   * @see http://stackoverflow.com/questions/21177107/how-to-check-if-path-or-file-exist-in-scala
   */
	def exists(path: String):Boolean = new File(path).exists

	def move(origin: String, dest: String):Boolean = {
		val f = new File(origin)
		if(f.exists){
			f.renameTo(new File(dest))
			true
		}
		else{
			false
		}
	}

  /**
   * inserts text between two regular expressions foud in file
   * @param  {[type]} path: String        [description]
   * @param textPatterns: tuple with firt and last regexp
   * @param text to be inserted
   * @return true if success
   * @todo write test
   */
  def insertBetweenRegexps(path: String, text: String, textPatterns: (String, String)):Boolean = try{
    import scala.util.matching.Regex
    import scala.collection.mutable.ArrayBuffer

    val myAppCode = new ArrayBuffer[String]()

    var beginRoutes = false
    val pattern1 = new Regex(textPatterns._1)
    val pattern2 = new Regex(textPatterns._2)
    
    for(line <- readByLine(path)){
        
      if(beginRoutes && (pattern2 findAllIn line).size>0){
        myAppCode.append(text)
        beginRoutes = false
      }
        
      if(!beginRoutes && (pattern1 findAllIn line).size>0){
        beginRoutes = true
      }

      myAppCode.append(line)
    }

    delete(path)
    write(path, myAppCode.mkString("\n"))
    true
  }catch{
    case _:Throwable => false
  }
}