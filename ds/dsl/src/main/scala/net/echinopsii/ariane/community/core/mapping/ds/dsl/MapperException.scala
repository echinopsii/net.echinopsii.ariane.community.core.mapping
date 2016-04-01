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
package net.echinopsii.ariane.community.core.mapping.ds.dsl

abstract class MapperException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(message:String) = this(message, null)
}

class MapperParserException(message: String, cause: Throwable=null) extends MapperException(message,cause) {
  def this(message:String) = this(message, null)
}

class MapperExecutorException(message: String, cause: Throwable=null) extends MapperException(message,cause) {
  def this(message:String) = this(message, null)
}

class MapperEmptyResultException(message: String, cause: Throwable=null) extends MapperException(message,cause) {
  def this(message:String) = this(message, null)
}
