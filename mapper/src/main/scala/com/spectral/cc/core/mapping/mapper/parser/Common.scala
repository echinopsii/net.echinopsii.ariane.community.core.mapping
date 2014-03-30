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

import scala.util.parsing.combinator.JavaTokenParsers
import com.spectral.cc.core.mapping.mapper.MapperParserException

class Common extends JavaTokenParsers {
  def ignoreCase1(str: String): Parser[String] = ("""(?i)\b""" + str + """\b""").r ^^ (x => x.toLowerCase)
  def ignoreCase2(str: String): Parser[String] = (str + """\b""").r ^^ (x => x.toLowerCase)
  def ignoreCase(str: String): Parser[String] = ignoreCase1(str) | ignoreCase2(str) | failure(str + " expected")

  def ignoreCases(strings: String*): Parser[String] = ignoreCases(strings.toList)

  def ignoreCases(strings: List[String]): Parser[String] = strings match {
    case List(x) => ignoreCase(x)
    case first :: rest => ignoreCase(first) | ignoreCases(rest)
    case _ => throw new MapperParserException("List expected here !")
  }

  def stripQuotesEscape(s: String) = s.replaceAll("\\\\\"" , "\"");
  def stripQuotes(s: String) = s.substring(1, s.length - 1)

  def string: Parser[String] = (quoteString | apostropheString) ^^ (str => {stripQuotes(str)})
  def apostropheString: Parser[String] = ("\'" + """([^'\p{Cntrl}\\]|\\[\\/bfnrt]|\\u[a-fA-F0-9]{4})*""" + "\'").r
  def quoteString: Parser[String] = stringLiteral ^^ (str => stripQuotesEscape(str))
}
