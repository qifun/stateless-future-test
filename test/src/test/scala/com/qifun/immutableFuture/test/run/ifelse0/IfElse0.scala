/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.immutableFuture
package test
package run
package ifelse0

import language.{reflectiveCalls, postfixOps}
import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration._
import com.qifun.immutableFuture.test.Async.{async, await, future}
import org.junit.Test
import com.qifun.immutableFuture.test.internal.AsyncId
import ExecutionContext.Implicits.global

class TestIfElseClass {

  import ExecutionContext.Implicits.global

  def m1(x: Int): Future[Int] = future {
    x + 2
  }

  def m2(y: Int): Future[Int] = async {
    val f = m1(y)
    var z = 0
    if (y > 0) {
      val x1 = await(f)
      z = x1 + 2
    } else {
      val x2 = await(f)
      z = x2 - 2
    }
    z
  }
}


class IfElseSpec {

  @Test def `support await in a simple if-else expression`() {
    val o = new TestIfElseClass
    val fut = o.m2(10)
    val res = Await.result(fut, 2 seconds)
    res mustBe (14)
  }

  @Test def `await in condition`() {
    import AsyncId.{async, await}
    val result = async {
      if ({await(true); await(true)}) await(1) else ???
    }
    result mustBe (1)
  }
}
