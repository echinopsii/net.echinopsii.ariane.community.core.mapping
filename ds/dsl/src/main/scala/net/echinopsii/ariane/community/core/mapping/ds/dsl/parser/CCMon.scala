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
package net.echinopsii.ariane.community.core.mapping.ds.dsl.parser

import scala.util.parsing.combinator._

trait CCMon extends Common with JavaTokenParsers {
  def value : Parser[String] = string
  def member: Parser[(String, String)] = (string<~":")~value ^^ { case name~value => (name, value) }
  def obj: Parser[Map[String, String]] = "{" ~> repsep(member, ",") <~ "}" ^^ (Map() ++ _)
  def map_a: Parser[(Map[String, String], Map[String, String], Map[String, String])] = obj ^^ { case block => (block, null, null) }
  def map_through_c: Parser[(Map[String, String], Map[String, String], Map[String, String])] = "-"~> obj <~"-" ^^ { case block => (null, block, null)}
  def map_a_to_b: Parser[(Map[String, String], Map[String, String], Map[String, String])] = (obj<~"--")~obj ^^ { case startBlock~endBlock => (startBlock, null,  endBlock) }
  def map_a_to_b_through_c: Parser[(Map[String, String], Map[String, String], Map[String, String])] = (obj<~"-")~(obj<~"-")~obj ^^ {case startBlock~linkBlock~endBlock => (startBlock, linkBlock, endBlock)}
  //def arr: Parser[List[Any]]        = "["~> repsep(value, ",") <~"]"

  def map (queryText: java.lang.CharSequence) : (Boolean, Object) =
    parseAll(map_a, queryText) match {
      case Success(result, _) =>
        (true, result)
      case failure1: NoSuccess =>
        var errors : Set[String] = Set()
        errors += MapperParserUtils.parseFailureToString(failure1)
        parseAll(map_a_to_b, queryText) match {
          case Success(result, _) =>
            (true, result)
          case failure2: NoSuccess =>
            errors += MapperParserUtils.parseFailureToString(failure2)
            parseAll(map_a_to_b_through_c, queryText) match {
              case Success(result, _) =>
                (true, result)
              case failure3: NoSuccess =>
                errors+=MapperParserUtils.parseFailureToString(failure3)
                parseAll(map_through_c, queryText) match {
                  case Success(result, _) =>
                    (true, result)
                  case failure4: NoSuccess =>
                    var errorMsg : String = ""
                    errors+=MapperParserUtils.parseFailureToString(failure4)
                    for (err <- errors)
                      if (errors.last == err)
                        errorMsg += err + "\n\n"
                      else
                        errorMsg += err + "\n\n OR \n\n"
                    (false, errorMsg)
                }
            }
        }
    }
}
