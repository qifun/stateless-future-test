/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture
package test
package run
package helloAkka

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

case object Greet
case class WhoToGreet(who: String)
case class Greeting(message: String)

class Greeter extends Actor with FutureFactory {
  var greeting = ""

  def receive = Future {
    while (true) {
      nextMessage.await match {
        case WhoToGreet(who) => greeting = s"hello, $who"
        case Greet => sender ! Greeting(greeting) // Send the current greeting back to the sender
      }
    }
    throw new IllegalStateException("Unreachable code!")
  }
}
// prints a greeting
class GreetPrinter extends Actor with FutureFactory {
  def receive = Future {
    while (true) {
      nextMessage.await match {
        case Greeting(message) => message mustBe "hello, typesafe"
      }
    }
    throw new IllegalStateException("Unreachable code!")
  }
}

class HelloAkkaSpec {

  @Test
  def helloAkka() {

    // Create the 'helloakka' actor system
    val system = ActorSystem("helloakka")

    // Create the 'greeter' actor
    val greeter = system.actorOf(Props[Greeter], "greeter")

    // Create an "actor-in-a-box"
    val inbox = Inbox.create(system)

    // Tell the 'greeter' to change its 'greeting' message
    greeter.tell(WhoToGreet("akka"), ActorRef.noSender)

    // Ask the 'greeter for the latest 'greeting'
    // Reply should go to the "actor-in-a-box"
    inbox.send(greeter, Greet)

    // Wait 5 seconds for the reply with the 'greeting' message
    val Greeting(message1) = inbox.receive(5.seconds)
    message1 mustBe "hello, akka"

    // Change the greeting and ask for it again
    greeter.tell(WhoToGreet("typesafe"), ActorRef.noSender)
    inbox.send(greeter, Greet)
    val Greeting(message2) = inbox.receive(5.seconds)
    message2 mustBe "hello, typesafe"

    val greetPrinter = system.actorOf(Props[GreetPrinter])
    // after zero seconds, send a Greet message every second to the greeter with a sender of the greetPrinter
    system.scheduler.schedule(0.seconds, 1.second, greeter, Greet)(system.dispatcher, greetPrinter)
    Thread.sleep(2100)
  }

}


