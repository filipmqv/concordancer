import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class Request(wordToSearch: String, neighbours: Int, urls: List[String])

object Client extends App {
  implicit val timeout = Timeout(500 seconds)
  val system = ActorSystem("ClientSystem", ConfigFactory.load("client"))
  val remoteActor = system.actorSelection("akka.tcp://RemoteActorSystem@127.0.0.1:9001/user/RemoteActor")

  /*val urls = List(
    "http://www.gutenberg.org/files/2554/2554-h/2554-h.htm",    //CRIME AND PUNISHMENT
    "http://www.gutenberg.org/files/706/706-h/706-h.htm",       //THE AMATEUR CRACKSMAN
    "http://www.gutenberg.org/cache/epub/9806/pg9806.html",     //MR. JUSTICE RAFFLES
    "http://www.gutenberg.org/files/707/707-h/707-h.htm"        //RAFFLES FURTHER ADVENTURES OF THE AMATEUR CRACKSMAN
  )*/

  val urls = List(
    "http://tomasz.fabisiak.pl/concordancer/1.html",
    "http://tomasz.fabisiak.pl/concordancer/2.html",
    "http://tomasz.fabisiak.pl/concordancer/3.html",
    //"http://tomasz.fabisiak.pl/concordancer/4.html",
    //"http://tomasz.fabisiak.pl/concordancer/5.html",
    //"http://tomasz.fabisiak.pl/concordancer/6.html",
    //"http://tomasz.fabisiak.pl/concordancer/7.html",
    "http://tomasz.fabisiak.pl/concordancer/8.html",
    //"http://tomasz.fabisiak.pl/concordancer/9.html",
    //"http://tomasz.fabisiak.pl/concordancer/10.html",
    "http://tomasz.fabisiak.pl/concordancer/11.html",
    //"http://tomasz.fabisiak.pl/concordancer/12.html",
    //"http://tomasz.fabisiak.pl/concordancer/13.html",
    "http://tomasz.fabisiak.pl/concordancer/14.html",
    "http://tomasz.fabisiak.pl/concordancer/15.html"
  )

  println("That 's remote:" + remoteActor)
  val future = remoteActor ? Request("home", 6, urls)
  println(" ask sent")
  val result = Await.result(future, Duration.Inf)
  println(result)
  println("Number of results: " + result.asInstanceOf[List[String]].size)
}
