/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package run
package ifelse3

import language.{reflectiveCalls, postfixOps}
import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration._
import com.qifun.statelessFuture.test.Async.{async, await, future}
import org.junit.Test
import ExecutionContext.Implicits.global
import AutoStart._

class TestIfElse3Class {

  import ExecutionContext.Implicits.global

  def base(x: Int): Future[Int] = future {
    x + 2
  }

  def m(y: Int): Future[Int] = async {
    val f = base(y)
    var z = 0
    if (y > 0) {
      val x1 = await(f)
      var w = x1 + 2
      z = w + 2
    } else {
      val x2 = await(f)
      var w = x2 + 2
      z = w - 2
    }
    z
  }
}


class IfElse3Spec {

  @Test
  def `variables of the same name in different blocks`() {
    val o = new TestIfElse3Class
    val fut = o.m(10)
    val res = Await.result(fut, 2 seconds)
    res mustBe (16)
  }
}
