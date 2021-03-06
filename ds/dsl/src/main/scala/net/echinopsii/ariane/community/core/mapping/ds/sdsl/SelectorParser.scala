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
package net.echinopsii.ariane.community.core.mapping.ds.sdsl

import net.echinopsii.ariane.community.core.mapping.ds.SelectorParserException
import net.echinopsii.ariane.community.core.mapping.ds.sdsl.parser.PredicatesTP
import net.echinopsii.ariane.community.core.mapping.ds.sdsl.internal.Expression

class SelectorParser extends PredicatesTP {
  def parse(query: String): (Expression, Expression, String) = {
    parseAll(predicate(), query) match {
      case Success(result, _) => result.query
      case failure : NoSuccess => throw new SelectorParserException(failure.msg)
    }
  }
}