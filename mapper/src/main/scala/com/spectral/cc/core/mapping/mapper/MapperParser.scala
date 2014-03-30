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

import com.spectral.cc.core.mapping.mapper.parser.{SQLlikeValue, CCMon, Common}

class MapperParser extends Common with CCMon with SQLlikeValue {

  def parse(queryText: String): Unit /*MapperQuery*/ = {
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
    parsedStartBlock foreach {case (startObjID, (startObjType, startObjPredicate)) => println("Start OBJ ID: " + startObjID + "\n" +
                                                                                              "Start OBJ Type: " + startObjType + "\n" +
                                                                                              "Start OBJ Predicate: " + startObjPredicate)}
    val parsedEndBlock = parseBlock(lexedEndBlock)
    parsedEndBlock foreach {case (endObjID, (endObjType, endObjPredicate)) => println("End OBJ ID: " + endObjID + "\n" +
                                                                                      "End OBJ Type: " + endObjType + "\n" +
                                                                                      "End OBJ Predicate: " + endObjPredicate)}
  }

  private def parseBlock(lexedBlock: Map[String, String]): Map[String, (String,String)] = {
    var parsedBlock: Map[String, (String, String)] = Map()
    lexedBlock foreach {
      case (startObjID, startObjIDValue) =>
        parseAll(notAKeyword, startObjID) match {
          case Success(result, _) => {
            startObjIDValue match {
              case startObjIDSQLLike:String => {
                parseAll(ccMonSQLLike, startObjIDSQLLike) match {
                  case Success(result, _) => {
                    parsedBlock += (startObjID -> result)
                  }
                  case failure : NoSuccess => throw new MapperParserException(failure.msg)
                }
              }
              case _ => throw new MapperParserException("Unexpected startObjIDValue type")
            }
          }
          case failure : NoSuccess => throw new MapperParserException(failure.msg)
        }
    }
    parsedBlock
  }
}