import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.language.postfixOps

case class Job(wordToSearch: String, neighbours: Int)
case class Request(wordToSearch: String, neighbours: Int, urls: List[String])

class ConcondancerActor(id: Int, bookUrl: String) extends Actor {
  @tailrec
  val readBookFromUrl : (String, List[String]) => String = (url, encodings) => {
    try {
      Source.fromURL(url, encodings.head).mkString
    } catch {
      case e: Exception =>
        println(bookUrl + ": not encoding " + encodings.head)
        readBookFromUrl(url, encodings.tail)
    }
  }

  val encodings = List("windows-1252", "utf-8", "ISO-8859-1", "iso-8859-2")
  val bookHtml = readBookFromUrl(bookUrl, encodings)
  val preparedBook: String = Concordancer.prepareBook(bookHtml)

  // put prepared book to file
  /*val dest = "outputTest" + id + ".txt"
  val writer = new PrintWriter(new File(dest))
  writer.write(preparedBook)
  writer.close()*/

  def receive = {
    case Job(wordToSearch, neighbours) => {
      println(id + " [" + bookUrl + "] Finding conconcanders for word: " + wordToSearch)
      val result = Concordancer.findConcordances(wordToSearch, preparedBook, neighbours)
      println(id + " [" + bookUrl + "] Found word '" + wordToSearch + "' " + result.size + " times")
      sender ! result
    }
  }
}

class ServerActor(serverId: Int) extends Actor {
  val system = ActorSystem("ConcondancerSystem")
  implicit val timeout = Timeout(100 seconds)
  var urlsActorsMap = Map[String, ActorRef]()
  var id = 0

  def receive = {
    case Request(wordToSearch, neighbours, urls) => {
      urls.foreach(url => {
        if (!(urlsActorsMap contains url)) {
          val tempId = id
          id += 1
          val tempActor = system.actorOf(Props(new ConcondancerActor(tempId, url)), "Actor" + tempId)
          urlsActorsMap += (url -> tempActor)
        }
      })

      val actors = urls map { urlsActorsMap(_) }

      val futures = actors map { _ ? Job(wordToSearch, neighbours) }

      val combined = futures.foldLeft(List[String]())((acc: List[String], elem: Future[Any]) =>
        acc ::: Await.result(elem, Duration.Inf).asInstanceOf[List[String]])

      sender ! combined
    }
  }
}

object Server extends App {
  val system = ActorSystem("RemoteActorSystem", ConfigFactory.load("server"))
  val remoteActor = system.actorOf(Props(new ServerActor(1)), "RemoteActor")
}