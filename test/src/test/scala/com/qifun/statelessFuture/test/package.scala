/*
 * Copyright (C) 2012-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package com.qifun.statelessFuture

import reflect._
import tools.reflect.{ ToolBox, ToolBoxError }
import java.net.URLClassLoader
import java.io.File
import com.qifun.statelessFuture.test.Async

package object test {

  implicit class objectops(obj: Any) {
    def mustBe(other: Any) = assert(obj == other, obj + " is not " + other)

    def mustEqual(other: Any) = mustBe(other)
  }

  implicit class stringops(text: String) {
    def mustContain(substring: String) = assert(text contains substring, text)
  }

  def intercept[T <: Throwable: ClassTag](body: => Any): T = {
    try {
      body
      throw new Exception(s"Exception of type ${classTag[T]} was not thrown")
    } catch {
      case t: Throwable =>
        if (classTag[T].runtimeClass != t.getClass) throw t
        else t.asInstanceOf[T]
    }
  }

  def eval(code: String, compileOptions: String = ""): Any = {
    val tb = mkToolbox(compileOptions)
    tb.eval(tb.parse(code))
  }

  def mkToolbox(compileOptions: String = ""): ToolBox[_ <: scala.reflect.api.Universe] = {
    val m = scala.reflect.runtime.currentMirror
    import scala.tools.reflect.ToolBox
    m.mkToolBox(options = compileOptions)
  }

  def scalaBinaryVersion: String = {
    val PreReleasePattern = """.*-(M|RC).*""".r
    val Pattern = """(\d+\.\d+)\..*""".r
    scala.util.Properties.versionNumberString match {
      case s @ PreReleasePattern(_) => s
      case Pattern(v) => v
      case _ => ""
    }
  }

  val toolboxClasspath = {
    val currentClassLoader = Async.getClass.getClassLoader.asInstanceOf[URLClassLoader]
    (for (url <- currentClassLoader.getURLs) yield {
      new File(url.toURI).getPath
    }).mkString(System.getProperty("path.separator"))
  }

  def expectError(errorSnippet: String, compileOptions: String = "",
    baseCompileOptions: String = s"-cp ${toolboxClasspath}")(code: String) {
    intercept[ToolBoxError] {
      eval(code, compileOptions + " " + baseCompileOptions)
    }.getMessage mustContain errorSnippet
  }
}
