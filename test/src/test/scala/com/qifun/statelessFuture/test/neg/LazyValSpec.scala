/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package neg

import org.junit.Test

class LazyValSpec {
  @Test
  def `https://issues.scala-lang.org/browse/SI-8499`() {
    expectError("reflective compilation has failed") {
      """
        | import com.qifun.statelessFuture.test.internal.AsyncId._
        | async {
        |   var x = 0
        |   lazy val y = { x += 1; 42 }
        |   assert(x == 0, x)
        |   val z = await(1)
        |   val result = y + x
        |   assert(x == 1, x)
        |   identity(y)
        |   assert(x == 1, x)
        |   result
        | }
      """.stripMargin
    }
  }
}

