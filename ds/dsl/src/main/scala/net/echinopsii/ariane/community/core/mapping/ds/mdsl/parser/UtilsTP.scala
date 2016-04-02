/**
 * MDSL
 * Mapping Domain Specific Language
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
package net.echinopsii.ariane.community.core.mapping.ds.mdsl.parser

import net.echinopsii.ariane.community.core.mapping.ds.tools.ParserUtils

import scala.util.parsing.combinator.JavaTokenParsers
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames

class UtilsTP extends ParserUtils with JavaTokenParsers {

  object MapperParserUtils {
    def parseFailureToString(failure: NoSuccess): String = {
      var errorMsg = failure.msg.replaceFirst("expected but `.*'", "expected but not")
      errorMsg = errorMsg.replaceAll("'", "")
      errorMsg = errorMsg.replaceAll("`", "")
      errorMsg = errorMsg.replaceAll( """\\b""", "")
      errorMsg = errorMsg.replaceAll("string matching regex ", "")
      if (failure.next.offset > 1 && failure.next.offset < failure.next.source.length()) {
        errorMsg += " : \n" + failure.next.source.subSequence(0, failure.next.offset - 1) +
          " >" + failure.next.source.charAt(failure.next.offset) +
          failure.next.source.subSequence(failure.next.offset + 1, failure.next.source.length())
      } else if (failure.next.offset == 0) {
        errorMsg += " : \n >" + failure.next.source
      } else if (failure.next.offset == failure.next.source.length()) {
        errorMsg += " : \n" + failure.next.source.subSequence(0, failure.next.offset) +
          "< "
      }
      errorMsg
    }
  }

  val ccmonobjtypes = List(
    MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE, MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE,
    MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE, MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE,
    MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE + " | " + MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE + " | " +
    MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE + " | " + MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE
  )
  val ccmonkeywords = List("from","where","and","or","like","=","!=","<>",">","<",">=","<=","=~",
    MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE, MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE,
    MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE, MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)
  def notAKeyword: Parser[String] =
    not(ignoreCases(ccmonkeywords: _*)) ~> ident | ignoreCases(ccmonkeywords: _*) ~> failure("invalid keyword usage.")
  def ccobjtype: Parser[String] =
    ignoreCases(ccmonobjtypes: _*)
}
