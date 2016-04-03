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
package net.echinopsii.ariane.community.core.mapping.ds.sdsl.internal

import com.typesafe.scalalogging.slf4j.Logging
import net.echinopsii.ariane.community.core.mapping.ds.sdsl.SelectorParserException
import net.echinopsii.ariane.community.core.mapping.ds.tools.Text

abstract class Predicate extends Expression {
  override var eType: String = "Predicate"
}

case class And(left: Predicate, right: Predicate) extends Predicate with Logging {
  override def toString = left.toString + " and " + right.toString

  override def query: (Predicate, Predicate, String) =  (left, right, "and")

  override def calcType: String = eType

  override def getValue: AnyRef = toString
}

case class Ops(left: Expression, right: Expression, ops: String) extends Predicate with Logging {
  override def toString = left.toString + " " + ops + " " + right.toString

  override def query: (Expression, Expression, String) =  (left, right, ops)

  override def calcType: String = eType

  override def getValue: AnyRef = ops
}

object BlueprintsQueryOperations {
  def toBlueprintsPredicate(ops: String): com.tinkerpop.blueprints.Predicate = ops match {
    case "=" => com.tinkerpop.blueprints.Compare.EQUAL
    case ">" => com.tinkerpop.blueprints.Compare.GREATER_THAN
    case ">=" => com.tinkerpop.blueprints.Compare.GREATER_THAN_EQUAL
    case "<" => com.tinkerpop.blueprints.Compare.LESS_THAN
    case "<=" => com.tinkerpop.blueprints.Compare.LESS_THAN_EQUAL
    case "!=" => com.tinkerpop.blueprints.Compare.NOT_EQUAL
    case "<>" => com.tinkerpop.blueprints.Compare.NOT_EQUAL
    case "=~" => Text.REGEX
    case "like" => Text.REGEX
    case _ => throw new SelectorParserException("Operation " + ops + " not recognized")
  }
}