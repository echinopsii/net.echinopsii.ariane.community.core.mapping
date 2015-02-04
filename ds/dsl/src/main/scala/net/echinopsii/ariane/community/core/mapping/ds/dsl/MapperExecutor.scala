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

object MapperExecutorUtil {
  def mapperQueryToCypherQuery(query: String): (String, Boolean) = {
    var cypherQuery : String = ""
    var isMapperQuery : Boolean = false
    if (!query.startsWith("CYPHER")) {
      cypherQuery = new MapperParser("cypher").parse(query).genQuery()
      isMapperQuery = true
    } else {
      if (query.startsWith("CYPHER 1.9"))
        cypherQuery = query
      else
        cypherQuery = query.replace("CYPHER", "")
    }
    (cypherQuery, isMapperQuery)
  }
}

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
        val queryTuple: (String, Boolean) = MapperExecutorUtil.mapperQueryToCypherQuery(query)
        val cypherQuery: String = queryTuple._1
        val isMapperQuery: Boolean = queryTuple._2

        log.debug("cypher query : \n\n" + cypherQuery)

        //var result: ExecutionResult = cypherEngine.prepare(mapperQuery).execute(null)
        log.debug("Cypher execution begins : " + new Date().toString)
        val result: ExecutionResult = cypherEngine.execute(cypherQuery)
        //log.error(result.dumpToString())
        log.debug("Cypher execution ends : " + new Date().toString)

        if (isMapperQuery) {
          result foreach (row => {
            row foreach (column => {
              log.debug(column._1 + ":" + column._2)
              column._1 match {
                case "CID" => {
                  val cidl: List[Long] = column._2.asInstanceOf[List[Long]]
                  log.debug("ADD CIDs TO RESULT MAP " + cidl)
                  cidl foreach (cid => resultMap += ("V" + cid.toString -> MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE))
                }
                case "NID" => {
                  val nidl: List[Long] = column._2.asInstanceOf[List[Long]]
                  log.debug("ADD NIDs TO RESULT MAP " + nidl)
                  nidl foreach (nid => resultMap += ("V" + nid.toString -> MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE))
                }
                case "EID" => {
                  val eidl: List[Long] = column._2.asInstanceOf[List[Long]]
                  log.debug("ADD EIDs TO RESULT MAP " + eidl)
                  eidl foreach (eid => resultMap += ("V" + eid.toString -> MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE))
                }
                case "TID" => {
                  val tidl: List[Long] = column._2.asInstanceOf[List[Long]]
                  log.debug("ADD TIDs TO RESULT MAP " + tidl)
                  tidl foreach (tid => resultMap += ("V" + tid.toString -> MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE))
                }
                case "LID" => {
                  val lidl: List[Long] = column._2.asInstanceOf[List[Long]]
                  log.debug("ADD LIDs TO RESULT MAP " + lidl)
                  lidl foreach (lid => resultMap += ("E" + lid.toString -> MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY))
                }
                case _ => throw new MapperExecutorException("Unknown Column Identifier !")
              }
            })
          })
          log.debug("resultMap is built : " + new Date().toString)
        } else {
          resultMap += ("cypherResult:" -> result.dumpToString())
        }
      }
      case _ => throw new MapperExecutorException("Unsupported execution engine !")
    }

    resultMap.asJava
  }
}
