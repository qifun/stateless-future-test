/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package run
package become

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
import com.qifun.statelessFuture.akka.FutureFactory

import _root_.akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import scala.concurrent.duration._

class BecomeActor extends Actor with FutureFactory {

  def receive = {
    case "become1" => context.become(FutureFactory.receiveUntilReturn(become1).get, false)
    case "become2" => context.become(become2)
    case "isSameSender" => context.become(FutureFactory.receiveUntilReturn(isSameSender).get, false)
  }

  private val isSameSender = Future {
    nextMessage.await
    val sender1 = sender
    nextMessage.await
    val sender2 = sender
    sender ! (sender1 == sender2)

  }

  private val become1 = Future {
    val message1 = nextMessage.await
    val message2 = nextMessage.await
    sender ! raw"message1=$message1 message2=$message2"
  }

  private val become2 = Future {
    while (true) {
      val message1 = nextMessage.await.asInstanceOf[String].toInt
      val message2 = nextMessage.await.asInstanceOf[String].toInt
      sender ! message1 + message2
    }
    throw new IllegalStateException("Unreachable code!")

  }
}

class BecomeSpec {

  @Test
  def doubleReceive() {
    val system = ActorSystem("helloakka")
    val doubleReceiveActor = system.actorOf(Props[BecomeActor], "greeter")
    val inbox = Inbox.create(system)
    inbox.send(doubleReceiveActor, "become1")
    inbox.send(doubleReceiveActor, "hello")
    inbox.send(doubleReceiveActor, "world")
    inbox.receive(5.seconds) mustBe "message1=hello message2=world"
    inbox.send(doubleReceiveActor, "isSameSender")
    inbox.send(doubleReceiveActor, ())
    inbox.send(doubleReceiveActor, ())
    inbox.receive(5.seconds) mustBe true
    inbox.send(doubleReceiveActor, "isSameSender")
    inbox.send(doubleReceiveActor, ())
    val inbox2 = Inbox.create(system)
    inbox2.send(doubleReceiveActor, ())
    inbox2.receive(5.seconds) mustBe false
    inbox.send(doubleReceiveActor, "become2")
    inbox.send(doubleReceiveActor, "17")
    inbox.send(doubleReceiveActor, "33")
    inbox.receive(5.seconds) mustBe 50
  }
}


