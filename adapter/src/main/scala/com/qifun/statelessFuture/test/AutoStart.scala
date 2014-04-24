package com.qifun.statelessFuture
package test

import scala.concurrent.ExecutionContext
import scala.util.Success
import scala.util.Failure
object AutoStart {

  import scala.language.implicitConversions

  implicit def statelessFutureToConcurrentFuture[A](underlying: Future.Stateless[A])(implicit intialExecutionContext: ExecutionContext): scala.concurrent.Future[A] = {
    val p = Promise[A]()
    intialExecutionContext.execute(new Runnable {
      override final def run(): Unit = {
        p.completeWith(underlying).result
      }
    })
    Awaitable.ToConcurrentFuture(p)
  }

  implicit def toConcurrentFuture[A](underlying: Future[A])(implicit intialExecutionContext: ExecutionContext): scala.concurrent.Future[A] = {
    underlying match {
      case statefulFuture: Future.Stateful[A] => new Awaitable.ToConcurrentFuture(statefulFuture)
      case statelessFuture: Future.Stateless[A] => statelessFutureToConcurrentFuture(statelessFuture)
    }
  }
}