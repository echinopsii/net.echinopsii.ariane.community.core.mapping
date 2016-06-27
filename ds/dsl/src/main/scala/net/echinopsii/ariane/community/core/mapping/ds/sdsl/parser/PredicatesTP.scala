/**
 * MDSL
 * Mapping Domain Specific Language
 * Copyright (C) 29/03/14 echinopsii
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

import net.echinopsii.ariane.community.core.mapping.ds.sdsl.internal.{And, Ops, Predicate}

import scala.util.parsing.combinator.JavaTokenParsers

trait PredicatesTP extends UtilsTP with ExpressionsTP with JavaTokenParsers {
  def predicate(): Parser[Predicate] = predicateAnd()

  def predicateAnd(): Parser[Predicate] = {
    predicateOps() ~ rep(ignoreCase("and") ~> predicateOps()) ^^ {
      case head ~ rest => rest.foldLeft(head)((a, b) => new And(a,b))
    }
  }

  def predicateOps(): Parser[Predicate] = operators()

  def operators(): Parser[Predicate] =
      propertyKeyExp ~ "=" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,o) } |
      propertyKeyExp ~ "!=" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,"<>") }|
      propertyKeyExp ~ "<>" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,o) }|
      propertyKeyExp ~ ">" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,o) } |
      propertyKeyExp ~ "<" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,o) } |
      propertyKeyExp ~ ">=" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,o) }|
      propertyKeyExp ~ "<=" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,o) }|
      propertyKeyExp ~ "=~" ~ propertyValueExp ^^ { case l~o~r => new Ops(l,r,o) }|
      propertyKeyExp ~ ignoreCase("like") ~ expression ^^ { case l~o~r => new Ops(l,r,"=~") }|
      failure("Unsupported operation")
}
