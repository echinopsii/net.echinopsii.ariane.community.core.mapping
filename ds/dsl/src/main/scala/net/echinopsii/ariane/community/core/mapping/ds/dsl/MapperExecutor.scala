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
import org.slf4j.{LoggerFactory, Logger}
import java.util.Date

class MapperExecutor(val graph: Object) {
  private final val log: Logger = LoggerFactory.getLogger(classOf[MapperExecutor])
  private var runTimeEngine: Option[Object] = None

  var engineOption: Option[Object] = None
  if (runTimeEngine != None)
    engineOption = runTimeEngine
  else {
    graph match {
      case neodb: GraphDatabaseService => {
          engineOption = Some(new ExecutionEngine(neodb))
          runTimeEngine = engineOption
      }
      case _ => throw new MapperExecutorException("Unsupported graph !")
    }
  }

  def execute(query: String): java.util.Map[String, String] = {
    log.debug("mapper query : \n\n" + query)
    var resultMap: Map[String, String] = Map()

    val engine: Object = engineOption getOrElse { throw new MapperExecutorException("Execution engine has not been initialized correctly !") }
    engine match {
      case cypherEngine: ExecutionEngine => {
        val mapperQuery : String = new MapperParser("cypher").parse(query).genQuery()

        log.debug("cypher query : \n\n" + mapperQuery)

        //var result: ExecutionResult = cypherEngine.prepare(mapperQuery).execute(null)
        log.debug(new Date().toString)
        var result: ExecutionResult = cypherEngine.execute(mapperQuery)
        log.debug(result.dumpToString())

        result.columnAs[List[Long]]("CID").toList foreach(cidl => cidl.toList foreach (cid => resultMap+=("V" + cid.toString -> MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE)))
        //log.debug(result.dumpToString())
        // TODO : check why first columnAs seems to erase entire result and so why we need to replay the query exec !
        result = cypherEngine.execute(mapperQuery)
        result.columnAs[List[Long]]("NID").toList foreach(nidl => nidl.toList foreach (nid => resultMap+=("V" + nid.toString -> MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE)))
        result = cypherEngine.execute(mapperQuery)
        result.columnAs[List[Long]]("EID").toList foreach(eidl => eidl.toList foreach (eid => resultMap+=("V" + eid.toString -> MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE)))
        result = cypherEngine.execute(mapperQuery)
        result.columnAs[List[Long]]("TID").toList foreach(tidl => tidl.toList foreach (tid => resultMap+=("V" + tid.toString -> MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE)))
        result = cypherEngine.execute(mapperQuery)
        result.columnAs[List[Long]]("LID").toList foreach(lidl => lidl.toList foreach (lid => resultMap+=("E" + lid.toString -> MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY)))
        log.debug(new Date().toString)

      }
      case _ => throw new MapperExecutorException("Unsupported execution engine !")
    }

    resultMap.asJava
  }
}
