package xasync

import scala.util.chaining._

import scala.concurrent._
import scala.concurrent.duration._

import scala.async.Async.{async, await}

object CompareFutureWithAsync extends hutil.App {

  implicit val ec: ExecutionContext = ExecutionContext.global

  val rand = scala.util.Random

  type Response = String

  val futureDOY: Future[Response] = Future {
    // ws.url("https://api.day-of-year/today").get()
    val month = rand.nextInt(12).toString
    val day   = rand.nextInt(30).toString
    s"$month/$day"
  }

  val futureDaysLeft: Future[Response] = Future {
    // ws.url("https://api.days-left/today").get()
    rand.nextInt(365).toString
  }

  def nameOfMonth(num: Int): Future[Response] =
    Future {
      num match {
        case 0  => "Jan"
        case 1  => "Feb"
        case 2  => "Mar"
        case 3  => "Apr"
        case 4  => "May"
        case 5  => "Jun"
        case 6  => "Jul"
        case 7  => "Aug"
        case 8  => "Sep"
        case 9  => "Oct"
        case 10 => "Nov"
        case 11 => "Dec"
        case _  => throw new IllegalArgumentException(s"illegal month: $num")
      }
    }

  val date = """(\d+)/(\d+)""".r

  val future1 = for {
    dayOfYear <- futureDOY
    response  <- dayOfYear match {
                   case date(month, _) =>
                     for (name <- nameOfMonth(month.toInt))
                       yield s"Status: 200 - It’s $name!"
                   case _              =>
                     Future.successful("Status: 404: - Not a date, mate")
                 }
  } yield response

  Await
    .result(future1, Duration.Inf)
    .pipe(println)

  @annotation.nowarn("cat=other-nullary-override")
  val future2 = async {
    await(futureDOY) match {
      case date(month, _) =>
        val monthName = await(nameOfMonth(month.toInt))
        s"Status: 200 - It’s $monthName!"
      case _              =>
        "Status: 404: - Not a date, mate!"
    }
  }

  Await.result(future2, Duration.Inf) pipe println
}
