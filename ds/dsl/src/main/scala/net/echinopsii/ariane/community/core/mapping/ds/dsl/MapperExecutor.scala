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
package net.echinopsii.ariane.community.core.mapping.ds.dsl

import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.cypher.{ExecutionResult, ExecutionEngine}
import scala.collection.JavaConverters._
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames

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

  def execute(query: String): java.util.Map[String, String] = {
    var resultMap: Map[String, String] = Map()

    val engine: Object = engineOption getOrElse { throw new MapperExecutorException("Execution engine has not been initialized correctly !") }
    engine match {
      case cypherEngine: ExecutionEngine => {
        var mapperQuery : String = new MapperParser("cypher").parse(query).genQuery()
        var result: ExecutionResult = cypherEngine.prepare(mapperQuery).execute(null)
        //println(result.dumpToString())
        result.columnAs[List[Long]]("CID").toList foreach(cidl => cidl.toList foreach (cid => resultMap+=("V" + cid.toString -> MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE)))
        // TODO : check why first columnAs seems to erase entire result and so why we need to replay the query exec !
        result = cypherEngine.prepare(mapperQuery).execute(null)
        result.columnAs[List[Long]]("NID").toList foreach(nidl => nidl.toList foreach (nid => resultMap+=("V" + nid.toString -> MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE)))
        result = cypherEngine.prepare(mapperQuery).execute(null)
        result.columnAs[List[Long]]("EID").toList foreach(eidl => eidl.toList foreach (eid => resultMap+=("V" + eid.toString -> MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)))
        result = cypherEngine.prepare(mapperQuery).execute(null)
        result.columnAs[List[Long]]("TID").toList foreach(tidl => tidl.toList foreach (tid => resultMap+=("V" + tid.toString -> MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)))
        result = cypherEngine.prepare(mapperQuery).execute(null)
        result.columnAs[List[Long]]("LID").toList foreach(lidl => lidl.toList foreach (lid => resultMap+=("E" + lid.toString -> MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)))
      }
      case _ => throw new MapperExecutorException("Unsupported execution engine !")
    }

    resultMap.asJava
  }
}
