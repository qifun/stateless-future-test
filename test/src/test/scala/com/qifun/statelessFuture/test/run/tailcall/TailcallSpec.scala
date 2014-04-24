/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package run
package tailcall

import org.junit.Test
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import AutoStart._

class TailcallSpec {
  @Test
  def `Tailcalls should not waste too many memory` {
    System.gc()
    val startMemory = Runtime.getRuntime.freeMemory
    def ping(count: Int): Future[Long] = Future {
      if (count > 0) {
        pong(count - 1).await
      } else {
        System.gc()
        Runtime.getRuntime.freeMemory - startMemory
      }
    }

    def pong(count: Int): Future[Long] = Future {
      ping(count - 1).await
    }

    import ExecutionContext.Implicits.global
    val memoryUsage = Await.result(ping(500000000), 30.seconds)
    assert(memoryUsage < 500000000, memoryUsage + " is not less than " + 500000000)
  }

}
