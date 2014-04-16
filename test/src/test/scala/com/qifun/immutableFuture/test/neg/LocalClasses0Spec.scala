/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.immutableFuture
package test
package neg

import org.junit.Test

class LocalClasses0Spec {
  @Test
  def localClassCrashIssue16() {
    expectError("reflective compilation has failed") {
      """
        | import _root_.com.qifun.immutableFuture.test.internal.AsyncId.{ async, await }
        | async {
        |   class B { def f = 1 }
        |   await(new B()).f
        | }
      """.stripMargin
    }
  }

  @Test
  def `https://issues.scala-lang.org/browse/SI-8505`() {
    expectError("Person is already defined as (compiler-generated) case class companion object Person") {
      """
        | import _root_.com.qifun.immutableFuture.test.internal.AsyncId.{ await, async }
        | async {
        |   trait Base { def base = 0 }
        |   await(0)
        |   case class Person(name: String) extends Base
        |   val fut = async { "bob" }
        |   val x = Person(await(fut))
        |   x.base
        |   x.name
        | }
      """.stripMargin
    }
  }
}
