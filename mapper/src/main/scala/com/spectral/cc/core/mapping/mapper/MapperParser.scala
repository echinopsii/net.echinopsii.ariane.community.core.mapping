/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE]
 * Copyright (C) 27/03/14 echinopsii
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
package com.spectral.cc.core.mapping.mapper

import com.spectral.cc.core.mapping.mapper.parser.{SqlLike, CCMon, Common}
import com.spectral.cc.core.mapping.mapper.internal.IdentifierExp
import com.spectral.cc.core.mapping.mapper.internal.Block

class MapperParser extends Common with CCMon with SqlLike {

  var identifierRegistry:Map[String, IdentifierExp] = Map()

  def parse(queryText: String): MapperQuery = {
    /*
     * Lexer
     */
    var lexedStartBlock: Map[String, String] = null
    var lexedEndBlock: Map[String, String] =  null
    parseAll(map, queryText) match {
      case Success(result, _) =>
        lexedStartBlock = result._1
        lexedEndBlock = result._2
      case failure : NoSuccess => throw new MapperParserException(failure.msg)
    }

    /*
     * Parser
     */
    val parsedStartBlock = parseBlock(lexedStartBlock)
    val parsedEndBlock = parseBlock(lexedEndBlock)

    MapperQuery(parsedStartBlock,parsedEndBlock)
  }

  private def parseBlock(lexedBlock: Map[String, String]): Block = {
    val block:Block = new Block()
    lexedBlock foreach {
      case (objID, objValue) =>
        parseAll(notAKeyword, objID) match {
          case Success(result, _) => {
            identifierRegistry+=(objID -> new IdentifierExp(iName = objID))
            objValue match {
              case sqlLikeT:String => {
                parseAll(sqlLike(objID, this), sqlLikeT) match {
                  case Success(result, _) => {
                    block.mapPointsPredicate += (objID -> result)
                  }
                  case failure : NoSuccess => throw new MapperParserException(failure.msg)
                }
              }
              case _ => throw new MapperParserException("Unexpected objValue type")
            }
          }
          case failure : NoSuccess => throw new MapperParserException(failure.msg)
        }
    }
    block
  }

  /*
  private def mapPointIdentifier: Parser[String] = {
    notAKeyword ~> checkMapEndpointUnicity(notAKeyword.toString)
    //^^ { case str => if (identifierRegistry.get(str)==None) { str } else { failure("Map endpoint "+str+" is defined more than once !") }}
  }

  private def checkMapEndpointUnicity(iName: String): Parser[String] = {
    if (identifierRegistry.get(iName)==None) {
      iName.r
    } else {
      failure("Map endpoint "+iName+" is defined more than once !")
    }
  }
  */
}