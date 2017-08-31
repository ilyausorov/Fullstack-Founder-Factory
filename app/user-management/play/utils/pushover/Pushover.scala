package user_management.play.utils.pushover

import javax.inject.Inject
import scala.concurrent.Future

import play.api.libs.ws._
import utilities._

class Pushover @Inject() (ws: WSClient) {

  val token	= Constants.getConf("pushover.token", "ap8SonXM4fp2jH1Yxr1BQBW8j3SiSS")
  val group	= Constants.getConf("pushover.group", "gktgw2pDsHX1dLYbbowjELiphEuCsQ")
  val url	= Constants.getConf("pushover.url", "https://api.pushover.net/1/messages.json")

  def push(title:String,message:String):Future[WSResponse] = {

    val body = Map(
      "token" -> Seq(token)
     , "user" -> Seq(group)
     , "title" -> Seq(title)
     , "message" -> Seq(message))

    ws.url(url).post(body)

  }

}
