/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE]
 * Copyright (C) 02/04/14 echinopsii
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
package com.spectral.cc.core.mapping.ds.dsl.parser

import scala.util.parsing.combinator.JavaTokenParsers
import com.spectral.cc.core.mapping.ds.dsl.internal.{Expression, StringExp, IdentifierExp}
import com.spectral.cc.core.mapping.ds.dsl.MapperParser

trait Expressions extends Common with JavaTokenParsers {
  def expression(blockEntityName: String, blockEntityType: String, mapperParser: MapperParser): Parser[Expression] = {
    //println("expression " + blockEntityName)
    stringExp | (identifierExp(blockEntityName, blockEntityType, mapperParser) ^^ { case str => mapperParser.identifierRegistry.get(str).get })
  }

  def identifierExp(blockEntityName: String, blockEntityType: String, mapperParser: MapperParser): Parser[String] = {
    //println("entity "+blockEntityName)
    blockEntityName.r ~ rep("." ~> notAKeyword) ^^ {
      case head ~ rest => rest.foldLeft(head)((a, b) => defineIdentifiers(blockEntityType, blockEntityName, a, b, mapperParser))
    } | not(blockEntityName.r) ~> failure("Predicate expression identifier is not starting with block entity identifier name (" + blockEntityName + ")")
  }

  def stringExp: Parser[Expression] = {
    (stringLiteral | apostropheString) ^^ {
      case str => new StringExp(value=str)
    }
  }

  private def defineIdentifiers(blockEntityType: String, blockEntityName: String, a: String, b: String, mapperParser: MapperParser): String = {
    var identA = mapperParser.identifierRegistry.get(a)
    if (identA==None) {
      val iA = new IdentifierExp(iName = a)
      if (blockEntityName == a) {
        iA.eType = blockEntityName
      }
      mapperParser.identifierRegistry+=(a -> iA)
      identA = Some(iA)
    }

    var identB = mapperParser.identifierRegistry.get(a+"."+b)
    if (identB==None) {
      val iB = new IdentifierExp(iName = b, iRoot = identA)
      identB = Some(iB)
      mapperParser.identifierRegistry+=(a+"."+b -> iB)
      identA.get.iProp = identB
    }
    identB.get.toString
  }
}
