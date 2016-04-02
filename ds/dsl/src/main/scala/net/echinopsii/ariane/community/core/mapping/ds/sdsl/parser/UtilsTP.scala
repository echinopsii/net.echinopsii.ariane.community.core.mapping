/**
 * Mapping Selector
 * Mapping Selector
 * Copyright (C) 01/04/16 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.sdsl.parser

import scala.util.parsing.combinator.JavaTokenParsers
import net.echinopsii.ariane.community.core.mapping.ds.tools.ParserUtils

class UtilsTP extends ParserUtils with JavaTokenParsers {

  val selectorkeywords = List(
    "and","or","like","=","!=","<>",">","<",">=","<=","=~"
  )
  def notAKeyword: Parser[String] =
    not(ignoreCases(selectorkeywords: _*)) ~> ident | ignoreCases(selectorkeywords: _*) ~> failure("invalid keyword usage.")
}
