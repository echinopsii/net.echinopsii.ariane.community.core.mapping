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

abstract class Expression() {
  var eType: String

  def query : (Expression, Expression, String)
  def getValue : Any
  def calcType : String
}

case class PropertyKeyExp(value: String) extends  Expression {
  override var eType: String = "PropertyKey"
  override def toString = value

  override def calcType: String = eType
  override def getValue: String = value
  override def query: (Expression, Expression, String) = null
}

case class ByteExp(value: Byte) extends Expression {
  override var eType: String = "Byte"
  override def toString = value.toString

  override def calcType: String = eType
  override def getValue: Byte = value
  override def query: (Expression, Expression, String) = null
}

case class ShortExp(value: Short) extends Expression {
  override var eType: String = "Short"
  override def toString = value.toString

  override def calcType: String = eType
  override def getValue: Short = value
  override def query: (Expression, Expression, String) = null
}

case class IntegerExp(value: Int) extends Expression {
  override var eType: String = "Integer"
  override def toString = value.toString

  override def calcType: String = eType
  override def getValue: Int = value
  override def query: (Expression, Expression, String) = null
}

case class LongExp(value: Long) extends Expression {
  override var eType: String = "Long"
  override def toString = value.toString

  override def calcType: String = eType
  override def getValue: Long = value
  override def query: (Expression, Expression, String) = null
}

case class StringExp(value: String) extends Expression {
  override var eType: String = "String"
  override def toString = value

  override def calcType: String = eType
  override def getValue: String = value
  override def query: (Expression, Expression, String) = null
}