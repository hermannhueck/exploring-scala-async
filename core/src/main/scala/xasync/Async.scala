package xasync

import scala.util.chaining._

import scala.concurrent._
import scala.concurrent.duration._

import scala.async.Async.{async, await}

import hutil.stringformat._

object Async extends hutil.App {

  implicit val ec: ExecutionContext = ExecutionContext.global

  s"$dash05 async / await".yellow.pipe(println)

  @annotation.nowarn("cat=other-nullary-override")
  val future = async {
    val x = Future(2)
    val y = Future(3)
    await(x) + await(y)
  }

  future tap println
  Await.ready(future, Duration.Inf)
  future tap println

  Await.result(future, Duration.Inf) pipe println
}
