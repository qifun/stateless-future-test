package com.qifun.statelessFuture
package test.internal

import scala.reflect.macros.Context
import scala.util.control.Exception.Catcher

object AsyncId {
  def awaitMacro(c: Context)(a: c.Expr[Any]): c.Expr[Nothing] = {

    import c.universe.Flag._
    import c.universe._
    import c.mirror._
    import compat._
    val future = ANormalForm.applyMacro(c)(a)
    reify {
      future.splice.await
    }
  }

  def blockingWait[A](future: Future[A]): A = {
    implicit val catcher: Catcher[Unit] = {
      case throwable: Throwable => throw throwable
    }
    val lock = new AnyRef
    lock.synchronized {
      var r: Option[A] = None
      for (a <- future) {
        lock.notify()
        r = Some(a)
      }
      while (r == None) {
        lock.wait()
      }
      r.get
    }
  }

  def asyncMacro(c: Context)(a: c.Expr[Any]): c.Expr[Nothing] = {

    import c.universe.Flag._
    import c.universe._
    import c.mirror._
    import compat._

    val future = ANormalForm.applyMacro(c)(a)

    reify {
      _root_.com.qifun.statelessFuture.test.internal.AsyncId.blockingWait(future.splice)
    }

  }

  import scala.language.experimental.macros

  def async[A](a: A): A = macro asyncMacro

  def await[A](a: A): A = macro awaitMacro
}