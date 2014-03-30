/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE]
 * Copyright (C) 28/03/14 echinopsii
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.spectral.cc.core.mapping.mapper.parser

import scala.util.parsing.combinator._

trait SQLlikeValue extends Common with JavaTokenParsers {

  val ccmonobjtypes = List("container", "node", "endpoint")
  val ccmonkeywords = List("from","where","and","or","container","node","endpoint")

  def ccMonSQLLike: Parser[(String,String)] =
    (ignoreCase("from") ~> ccobjtype) ~ (ignoreCase("where") ~> predicate) ^^ {case ccmblktype ~ predicate => (ccmblktype,predicate)}

  def predicate: Parser[String] = """.*""".r

  def notAKeyword: Parser[String] =
    not(ignoreCases(ccmonkeywords: _*)) ~> """.*""".r | ignoreCases(ccmonkeywords: _*) ~> failure("Invalid keyword usage !")

  def ccobjtype: Parser[String] = ignoreCases(ccmonobjtypes: _*)
}
