/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package run
package akkaReceive

import com.qifun.statelessFuture.akka.FutureFactory
import _root_.akka.actor._
import scala.concurrent.duration._

class ConcatenationActor extends Actor with FutureFactory {
  
  def nextInt = Future {
    nextMessage.await.toString.toInt
  }
  
  override def receive = Future {
    while (true) {
      val numberOfSubstrings = nextInt.await
      var i = 0
      val sb = new StringBuilder
      while (i < numberOfSubstrings) {
        sb ++= nextMessage.await.toString
        i += 1
      }
      val result = sb.toString
      println(result)
      sender ! result
    }
    throw new IllegalStateException("Unreachable code!")
  }
}

object ConcatenationActor {
  def main(arguments: Array[String]) {
    val system = ActorSystem("helloworld")
    val concatenationActor = system.actorOf(Props[ConcatenationActor], "concatenationActor")
    val inbox = Inbox.create(system)
    inbox.send(concatenationActor, "4")
    inbox.send(concatenationActor, "Hello")
    inbox.send(concatenationActor, ", ")
    inbox.send(concatenationActor, "world")
    inbox.send(concatenationActor, "!")
    assert(inbox.receive(5.seconds) == "Hello, world!")
    inbox.send(concatenationActor, "2")
    inbox.send(concatenationActor, "Hello, ")
    inbox.send(concatenationActor, "world, again!")
    assert(inbox.receive(5.seconds) == "Hello, world, again!")
  }
}

import scala.language.postfixOps
import scala.concurrent._
import com.qifun.statelessFuture.Future
import scala.concurrent.duration._
import scala.concurrent.duration.Duration.Inf
import scala.collection._
import scala.runtime.NonLocalReturnControl
import scala.util.{ Try, Success, Failure }
import AutoStart._
import com.qifun.statelessFuture.test.Async.{ async, await, future }
import org.junit.Test

class DoubleReceiveActor extends Actor with FutureFactory {

  def receive = Future {
    while (true) {
      val message1 = nextMessage.await
      val message2 = nextMessage.await
      sender ! raw"message1=$message1 message2=$message2"
    }
    throw new IllegalStateException("Unreachable code!")
  }
}

class AkkaReceiveSpec {

  @Test
  def helloword() {
    ConcatenationActor.main(Array.empty[String])
  }

  @Test
  def doubleReceive() {
    val system = ActorSystem("helloakka")
    val doubleReceiveActor = system.actorOf(Props[DoubleReceiveActor], "greeter")
    val inbox = Inbox.create(system)
    inbox.send(doubleReceiveActor, "hello")
    inbox.send(doubleReceiveActor, "world")
    inbox.receive(5.seconds) mustBe "message1=hello message2=world"
  }

}


