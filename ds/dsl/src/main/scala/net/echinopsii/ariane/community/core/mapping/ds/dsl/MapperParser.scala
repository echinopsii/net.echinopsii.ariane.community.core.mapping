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
package net.echinopsii.ariane.community.core.mapping.ds.dsl

import net.echinopsii.ariane.community.core.mapping.ds.dsl.parser.{SqlLike, CCMon, Common}
import net.echinopsii.ariane.community.core.mapping.ds.dsl.internal.IdentifierExp
import net.echinopsii.ariane.community.core.mapping.ds.dsl.internal.Block

class MapperParser(val queryType: String) extends Common with CCMon with SqlLike {

  var identifierRegistry:Map[String, IdentifierExp] = Map()

  def parse(queryText: String): MapperQueryGen = {
    /*
     * Lexer
     */
    var lexedStartBlock: Map[String, String] = null
    var lexedLinkBlock: Map[String, String] = null
    var lexedEndBlock: Map[String, String] =  null
    parseAll(map, queryText) match {
      case Success(result, _) =>
        lexedStartBlock = result._1
        lexedLinkBlock = result._2
        lexedEndBlock = result._3
      case failure : NoSuccess => throw new MapperParserException(failure.msg)
    }

    /*
     * Parser
     */
    val parsedStartBlock = parseBlock(lexedStartBlock)
    val parsedLinkBlock = parseBlock(lexedLinkBlock)
    val parsedEndBlock = parseBlock(lexedEndBlock)

    queryType match {
      case "cypher" => MapperToCypherQueryGen(parsedStartBlock,parsedLinkBlock,parsedEndBlock)
      case "gremlin" => throw new MapperParserException("not implemented yet !")
      case _ => throw new MapperParserException("invalid query type !")
    }
  }

  private def parseBlock(lexedBlock: Map[String, String]): Block = {
    if (lexedBlock==null)
      return null
    val block:Block = new Block()
    lexedBlock foreach {
      case (objID, objValue) =>
        parseAll(notAKeyword, objID) match {
          case Success(result, _) =>
            if (objID!="path") {
              identifierRegistry+=(objID -> new IdentifierExp(iName = objID))
              objValue match {
                case sqlLikeT:String =>
                  parseAll(sqlLike(objID, this), sqlLikeT) match {
                    case Success(resultT, _) => block.mapPointsPredicate += (objID -> resultT)
                    case failure : NoSuccess =>
                      var errorMsg = failure.msg.replaceFirst("expected but `.*'", "expected but not")
                      errorMsg = errorMsg.replaceAll("'", "")
                      errorMsg = errorMsg.replaceAll("`", "")
                      errorMsg = errorMsg.replaceAll("""\\b""", "")
                      errorMsg = errorMsg.replaceAll("string matching regex ", "")
                      errorMsg += " : \n" + failure.next.source.subSequence(0, failure.next.offset-1) +
                        " >" + failure.next.source.charAt(failure.next.offset) +
                        failure.next.source.subSequence(failure.next.offset+1, failure.next.source.length())
                      throw new MapperParserException(errorMsg)
                  }
                case _ => throw new MapperParserException("Unexpected objValue type")
              }
            } else
              block.path = objValue
          case failure : NoSuccess => throw new MapperParserException("[" + objID + "] : " + failure.msg)
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