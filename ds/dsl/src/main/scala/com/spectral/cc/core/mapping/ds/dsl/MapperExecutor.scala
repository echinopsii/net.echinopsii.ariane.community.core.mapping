/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE]
 * Copyright (C) 08/04/14 echinopsii
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
package com.spectral.cc.core.mapping.ds.dsl

import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.cypher.{ExecutionResult, ExecutionEngine}
import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames

class MapperExecutor(val graph: Object) {
  var engineOption: Option[Object] = None
  graph match {
    case neodb: GraphDatabaseService => {
      engineOption = Some(new ExecutionEngine(neodb))
    }
    case _ => {
      throw new MapperExecutorException("Unsupported graph !")
    }
  }

  def execute(query: String): Map[String, Long] = {
    var resultMap: Map[String, Long] = Map()

    val engine: Object = engineOption getOrElse { throw new MapperExecutorException("Execution engine has not been initialized correctly !") }
    engine match {
      case cypherEngine: ExecutionEngine => {
        val result: ExecutionResult = cypherEngine.prepare(new MapperParser("cypher").parse(query).genQuery()).execute(null)
        result.columnAs[Int]("CID").toList foreach(cid => resultMap+=(MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE -> cid))
        result.columnAs[Int]("NID").toList foreach(nid => resultMap+=(MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE -> nid))
        result.columnAs[Int]("EID").toList foreach(eid => resultMap+=(MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE -> eid))
        result.columnAs[Int]("TID").toList foreach(tid => resultMap+=(MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE -> tid))
        result.columnAs[Int]("LID").toList foreach(lid => resultMap+=(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY -> lid))
      }
      case _ => throw new MapperExecutorException("Unsupported execution engine !")
    }

    resultMap
  }
}
