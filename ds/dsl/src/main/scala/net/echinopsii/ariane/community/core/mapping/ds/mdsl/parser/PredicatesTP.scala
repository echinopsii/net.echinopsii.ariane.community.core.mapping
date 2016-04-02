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
package net.echinopsii.ariane.community.core.mapping.ds.mdsl.parser

import scala.util.parsing.combinator.JavaTokenParsers
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.MapperParser
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.internal.{And,Ops,Or,Predicate}

trait PredicatesTP extends UtilsTP with ExpressionsTP with JavaTokenParsers {
  def predicate(blockEntityName: String, blockEntityType: String, mapperParser: MapperParser): Parser[Predicate] = predicateOr(blockEntityName,blockEntityType, mapperParser)

  def predicateOr(blockEntityName: String, blockEntityType: String, mapperParser: MapperParser): Parser[Predicate] = {
    predicateAnd(blockEntityName,blockEntityType,mapperParser) ~ rep(ignoreCase("or") ~> predicateAnd(blockEntityName,blockEntityType,mapperParser)) ^^ {
      case head ~ rest => rest.foldLeft(head)((a,b) => new Or(a,b))
    }
  }

  def predicateAnd(blockEntityName: String, blockEntityType: String, mapperParser: MapperParser): Parser[Predicate] = {
    predicateOps(blockEntityName,blockEntityType,mapperParser) ~ rep(ignoreCase("and") ~> predicateOps(blockEntityName,blockEntityType,mapperParser)) ^^ {
      case head ~ rest => rest.foldLeft(head)((a, b) => new And(a,b))
    }
  }

  def predicateOps(blockEntityName: String, blockEntityType: String, mapperParser: MapperParser): Parser[Predicate] =
    operators(blockEntityName,blockEntityType,mapperParser)

  def operators(blockEntityName: String, blockEntityType: String, mapperParser: MapperParser): Parser[Predicate] =
      expression(blockEntityName,blockEntityType,mapperParser) ~ "=" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,o) } |
      expression(blockEntityName,blockEntityType,mapperParser) ~ "!=" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,"<>") }|
      expression(blockEntityName,blockEntityType,mapperParser) ~ "<>" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,o) }|
      expression(blockEntityName,blockEntityType,mapperParser) ~ ">" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,o) } |
      expression(blockEntityName,blockEntityType,mapperParser) ~ "<" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,o) } |
      expression(blockEntityName,blockEntityType,mapperParser) ~ ">=" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,o) }|
      expression(blockEntityName,blockEntityType,mapperParser) ~ "<=" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,o) }|
      expression(blockEntityName,blockEntityType,mapperParser) ~ "=~" ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,o) }|
      expression(blockEntityName,blockEntityType,mapperParser) ~ ignoreCase("like") ~ expression(blockEntityName,blockEntityType,mapperParser) ^^ { case l~o~r => new Ops(l,r,"=~") }|
      failure("Unsupported operation")
}
