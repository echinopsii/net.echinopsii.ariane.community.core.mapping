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
package net.echinopsii.ariane.community.core.mapping.ds.sdsl.parser

import scala.util.parsing.combinator.JavaTokenParsers
import net.echinopsii.ariane.community.core.mapping.ds.sdsl.internal.{ByteExp, ShortExp, IntegerExp, LongExp, PropertyKeyExp, StringExp, Expression}

trait ExpressionsTP extends UtilsTP with JavaTokenParsers {
  def expression: Parser[Expression] = {
    propertyKeyExp | stringExp
  }

  def propertyKeyExp: Parser[Expression] = {
    propertyKey ^^ {
      case str => new PropertyKeyExp(value=str)
    }
  }

  def propertyValueExp: Parser[Expression] = {
    stringExp | numberExp
  }

  def numberExp: Parser[Expression] = {
    wholeNumber ^^ {
      case signed_number =>
        /*
        val test_long = signed_number.toLong
        if ( -128 <= test_long &&  test_long <= 127) {
          new ByteExp(value=signed_number.toByte)
        } else if (-32768 <= test_long && test_long <= 32767) {
          new ShortExp(value=signed_number.toShort)
        } else if (-2147483648 <= test_long && test_long <= 2147483647) {
          new IntegerExp(value=signed_number.toInt)
        } else {
          new LongExp(value=signed_number.toLong)
        }
        */
        new LongExp(value=signed_number.toLong)
    }
  }

  def stringExp: Parser[Expression] = {
    string ^^ {
      case str => new StringExp(value=str)
    }
  }


}