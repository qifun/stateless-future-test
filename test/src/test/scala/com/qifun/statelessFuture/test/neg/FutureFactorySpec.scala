/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package neg

import org.junit.Test

class FutureFactorySpec {
  @Test
  def `A future in an actor cannot await another future from another actor`() {
    expectError("type mismatch") {
      """
        | import com.qifun.statelessFuture.akka.FutureFactory
        | import akka.actor.Actor
        | class MyActor extends Actor with FutureFactory {
        |   def another = Future { 0 }
        |   def main = Future { (new MyActor).another.await; ??? }
        |   def receive = main
        | }
        | ()
      """.stripMargin
    }
  }
}

