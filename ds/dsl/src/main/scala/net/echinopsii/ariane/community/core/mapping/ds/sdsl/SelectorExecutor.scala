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

import com.tinkerpop.blueprints.{GraphQuery, Graph}
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames
import net.echinopsii.ariane.community.core.mapping.ds.sdsl.internal.{BlueprintsQueryOperations, Expression}
import org.slf4j.{LoggerFactory, Logger}

class SelectorExecutor(val graph: Object) {
  private final val log: Logger = LoggerFactory.getLogger(classOf[SelectorExecutor])

  def executeQuery(predicates: (Expression, Expression, String), initialQuery: Object): Object = {
    val left = predicates._1
    val right = predicates._2
    val ops = predicates._3
    var updatedQuery: Object = None

    ops match {
      case "and" =>
        updatedQuery = executeQuery(left.query, initialQuery)
        updatedQuery = executeQuery(right.query, updatedQuery)

      case _ =>
        initialQuery match {
          case blueprint_query: GraphQuery =>
            updatedQuery = blueprint_query.has(left.toString, BlueprintsQueryOperations.toBlueprintsPredicate(ops.toString), right.getValue)
          case _ => throw new SelectorParserException("The query type is not supported !")
        }
    }
    updatedQuery
  }

  def execute(query: String, mo_type: String): Object = {
    log.debug("selector query : \n\n" + query)
    graph match {
      case blueprints_graph: Graph =>
        val predicates = new SelectorParser().parse(query)
        executeQuery(predicates, blueprints_graph.query().has(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, mo_type))
      case _ => throw new SelectorExecutorException("Unsupported Graph API !")
    }
  }
}
