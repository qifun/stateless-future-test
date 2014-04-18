/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package neg

import org.junit.Test

class NakedAwait {
  @Test
  def `await only allowed in async neg`() {
    import _root_.com.qifun.statelessFuture.test.Async._
    expectError("`await` must be enclosed in a `Future` block") {
      """
        | import _root_.com.qifun.statelessFuture.test.Async._
        | (null: _root_.com.qifun.statelessFuture.Future[Any]).await
      """.stripMargin
    }
  }

  @Test
  def `await not allowed in by-name argument`() {
    expectError("await must not be used under a by-name argument.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | def foo(a: Int)(b: => Int) = 0
        | async { foo(0)(await(0)) }
      """.stripMargin
    }
  }

  @Test
  def `await not allowed in boolean short circuit argument 1`() {
    expectError("await must not be used under a by-name argument.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { true && await(false) }
      """.stripMargin
    }
  }

  @Test
  def `await not allowed in boolean short circuit argument 2`() {
    expectError("await must not be used under a by-name argument.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { true || await(false) }
      """.stripMargin
    }
  }

  @Test
  def nestedObject() {
    expectError("await must not be used under a nested object.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { object Nested { await(false) } }
      """.stripMargin
    }
  }

  @Test
  def nestedTrait() {
    expectError("await must not be used under a nested trait.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { trait Nested { await(false) } }
      """.stripMargin
    }
  }

  @Test
  def nestedClass() {
    expectError("await must not be used under a nested class.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { class Nested { await(false) } }
      """.stripMargin
    }
  }

  @Test
  def nestedFunction() {
    expectError("await must not be used under a nested function.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { () => { await(false) } }
      """.stripMargin
    }
  }

  @Test
  def nestedPatMatFunction() {
    expectError("await must not be used under a nested class.") { // TODO more specific error message
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { { case x => { await(false) } } : PartialFunction[Any, Any] }
      """.stripMargin
    }
  }

  @Test
  def finallyBody() {
    expectError("await must not be used under a finally.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { try { () } finally { await(false) } }
      """.stripMargin
    }
  }

  @Test
  def guard() {
    expectError("await must not be used under a pattern guard.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { 1 match { case _ if await(true) => } }
      """.stripMargin
    }
  }

  @Test
  def nestedMethod() {
    expectError("await must not be used under a nested method.") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | async { def foo = await(false) }
      """.stripMargin
    }
  }

  @Test
  def returnIllegal() {
    expectError("return is illegal") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | def foo(): Any = async { return false }
        | ()
        |
        |""".stripMargin
    }
  }

  @Test
  def lazyValIllegal() {
    expectError("await must not be used under a lazy val initializer") {
      """
        | import _root_.com.qifun.statelessFuture.test.internal.AsyncId._
        | def foo(): Any = async { val x = { lazy val y = await(0); y } }
        | ()
        |
        |""".stripMargin
    }
  }
}
