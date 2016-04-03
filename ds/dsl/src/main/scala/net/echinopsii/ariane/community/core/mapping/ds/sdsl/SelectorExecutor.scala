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

import java.util

import com.tinkerpop.blueprints.{Vertex, GraphQuery, Graph}
import net.echinopsii.ariane.community.core.mapping.ds.sdsl.internal.{BlueprintsQueryOperations, Expression}
import org.slf4j.{LoggerFactory, Logger}

class SelectorExecutor(val graph: Object) {
  private final val log: Logger = LoggerFactory.getLogger(classOf[SelectorExecutor])
  var resultSet: Set[Object] = Set()

  def executeQuery(predicates: (Expression, Expression, String), initialQuery: Object, returnQuery: Boolean): Object = {
    val left = predicates._1
    val right = predicates._2
    val ops = predicates._3
    var updatedQuery: Object = None

    ops match {
      case "and" =>
        updatedQuery = executeQuery(left.query, initialQuery, returnQuery = true)
        updatedQuery = executeQuery(right.query, updatedQuery, returnQuery = true)

      case _ =>
        initialQuery match {
          case blueprint_query: GraphQuery =>
            updatedQuery = blueprint_query.has(left.toString, BlueprintsQueryOperations.toBlueprintsPredicate(ops.toString), right.toString)
          case _ => throw new SelectorParserException("The query type is not supported !")
        }
    }

    if (!returnQuery) {
      updatedQuery match {
        case blueprint_query: GraphQuery =>
          var iter : util.Iterator[Vertex] = blueprint_query.vertices().iterator()
          while (iter.hasNext)
            resultSet = resultSet + iter.next()
          None
        case _ => throw new SelectorParserException("The query type is not supported !")
      }
    } else updatedQuery
  }

  def execute(query: String): Set[Object] = {
    log.debug("selector query : \n\n" + query)
    graph match {
      case blueprints_graph: Graph =>
        val predicates = new SelectorParser().parse(query)
        executeQuery(predicates, blueprints_graph.query(), returnQuery = false)
        resultSet
      case _ => throw new SelectorExecutorException("Unsupported Graph API !")
    }
  }
}
