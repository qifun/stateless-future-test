package com.qifun.statelessFuture
package test

import scala.reflect.macros.Context

object Async {

  def asyncMacro(c: Context)(a: c.Expr[Any]): c.Expr[StatelessFuture[Nothing]] = {
    ANormalForm.applyMacro(c)(a)
  }

  def futureMacro(c: Context)(a: c.Expr[Any]): c.Expr[StatelessFuture[Nothing]] = {
    ANormalForm.applyMacro(c)(a)
  }

  def awaitMacro(c: Context)(a: c.Expr[Future[Any]]): c.Expr[Nothing] = {
    c.universe.reify(a.asInstanceOf[c.Expr[Future[Nothing]]].splice.await)
  }

  import scala.language.experimental.macros
  def async[A](a: A): StatelessFuture[A] = macro asyncMacro
  def future[A](a: A): StatelessFuture[A] = macro futureMacro
  def await[A](a: Future[A]): A = macro awaitMacro
}