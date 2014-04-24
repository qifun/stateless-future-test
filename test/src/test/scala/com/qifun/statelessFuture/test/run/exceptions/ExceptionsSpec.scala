/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package run
package exceptions

import com.qifun.statelessFuture.test.Async.{ async, await, future }

import scala.concurrent.{ ExecutionContext, Await }
import ExecutionContext.Implicits._
import scala.concurrent.duration._
import scala.reflect.ClassTag
import AutoStart._
import org.junit.Test

class ExceptionsSpec {

  @Test
  def `uncaught exception within async`() {
    val fut = async { throw new Exception("problem") }
    intercept[Exception] { Await.result(fut, 2.seconds) }
  }

  @Test
  def `uncaught exception within async after await`() {
    val base = future { "five!".length }
    val fut = async {
      val len = await(base)
      throw new Exception(s"illegal length: $len")
    }
    intercept[Exception] { Await.result(fut, 2.seconds) }
  }

  @Test
  def `await failing future within async`() {
    val base = future[Int] { throw new Exception("problem") }
    val fut = future {
      val x = base.await
      x * 2
    }
    intercept[Exception] { Await.result(fut, 2.seconds) }
  }

  @Test
  def `await failing future within async after await`() {
    val base = future[Any] { "five!".length }
    val fut = async {
      val a = await(base.mapTo[Int]) // result: 5
      val b = await((future { (a * 2).toString }).mapTo[Int]) // result: ClassCastException
      val c = await(future { (7 * 2).toString }) // result: "14"
      b + "-" + c
    }
    intercept[ClassCastException] { Await.result(fut, 2.seconds) }
  }

  def one = Future(1)

  @Test
  def `catch an exception and then recover`() {
    var finallyLog = 0
    var unreachableLog = 0
    val fut = Future {
      try {
        one.await
        one.await
        throw new Exception
        unreachableLog += one.await
      } catch {
        case e: Exception =>
          "recover"
      } finally {
        finallyLog += 1
      }
    }
    Await.result(fut, 2.seconds) mustBe "recover"
    unreachableLog mustBe 0
    finallyLog mustBe 1
  }

  @Test
  def `catch a custom exception and then recover`() {
    var finallyLog = 0
    var unreachableLog = 0
    val fut = Future {
      class MyException extends Exception
      try {
        one.await
        one.await
        throw new MyException
        unreachableLog += one.await
      } catch {
        case e: MyException =>
          "recover"
      } finally {
        finallyLog += 1
      }
    }
    Await.result(fut, 2.seconds) mustBe "recover"
    unreachableLog mustBe 0
    finallyLog mustBe 1
  }

  @Test
  def `catch an exception and then recover without a no finally block`() {
    var unreachableLog = 0
    val fut = Future {
      try {
        one.await
        throw new Exception
        unreachableLog += one.await
      } catch {
        case e: Exception =>
          "recover"
      }
    }
    Await.result(fut, 2.seconds) mustBe "recover"
    unreachableLog mustBe 0
  }

  @Test
  def `should not catch the exception because the exception's type is mismatched`() {
    var unreachableLog = 0
    class MyException1 extends Exception
    val fut = Future {
      class MyException2 extends Exception
      try {
        one.await
        throw new MyException1
        unreachableLog += one.await
      } catch {
        case e: MyException2 =>
          "recover"
      }
      unreachableLog += 1
    }
    intercept[MyException1] { Await.result(fut, 2.seconds) }
    unreachableLog mustBe 0
  }

  @Test
  def `nested try`() {
    var unreachableLog = 0
    var i = 10

    val fut = Future {
      class MyException1 extends Exception
      class MyException2 extends Exception
      try {
        try {
          one.await
          one.await
          throw new MyException1
          one.await
          unreachableLog += one.await
        } catch {
          case e: MyException2 =>
            one.await
            "recover2"
        } finally {
          i += 1
        }
        unreachableLog += one.await
      } catch {
        case e: MyException1 =>
          "recover1"
      } finally {
        i *= 2
      }
    }
    Await.result(fut, 2.seconds) mustBe "recover1"
    unreachableLog mustBe 0
    i mustBe 22
  }

  @Test
  def `throw an exception before the inner try`() {
    var unreachableLog = 0
    var i = 10
    val fut = Future {
      class MyException1 extends Exception
      class MyException2 extends Exception
      try {
        throw new MyException2
        val noException = try {
          "noException"
        } catch {
          case e: MyException2 => {
            one.await
            unreachableLog += one.await
          }
        } finally {
          i += 1
        }
        unreachableLog += one.await
      } catch {
        case e: MyException1 =>
          "recover1"
        case e: MyException2 =>
          "recover2"
      } finally {
        i *= 2
      }
    }
    Await.result(fut, 2.seconds) mustBe "recover2"
    unreachableLog mustBe 0
    i mustBe 20
  }

  @Test
  def `throw an exception after the inner try`() {
    var unreachableLog = 0
    var i = 10
    val fut = Future {
      class MyException1 extends Exception
      class MyException2 extends Exception
      try {
        val noException = try {
          one.await
          Future("noException").await
        } catch {
          case e: MyException2 => {
            unreachableLog += one.await
          }
        } finally {
          i += 1
        }
        if (noException == "noException") {
          throw new MyException2
        }
        unreachableLog += one.await
      } catch {
        case e: MyException1 =>
          "recover1"
        case e: MyException2 =>
          "recover2"
      } finally {
        i *= 2
      }
    }
    Await.result(fut, 2.seconds) mustBe "recover2"
    unreachableLog mustBe 0
    i mustBe 22
  }
}
