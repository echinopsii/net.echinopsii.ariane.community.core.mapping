/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE]
 * Copyright (C) 03/04/14 echinopsii
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

import com.spectral.cc.core.mapping.ds.dsl.internal.{Block,Predicate}
import scala.collection.mutable
import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames

abstract class MapperQueryGen(val startBlock: Block, val endBlock : Block) {
  def genQuery(): String
}

case class MapperToCypherQueryGen(override val startBlock: Block, override val endBlock: Block) extends MapperQueryGen(startBlock, endBlock) {
  private def cypherBlockStart(blockLine:(String,(String,Predicate))):String = {
    val matcher = blockLine._2._2.toCypherMatch
    var cypher = "\nSTART " + blockLine._1 + " = node(*)\n"
    if (matcher._1 != "") {
      cypher += "MATCH " + matcher._1 + "\n"
    }
    cypher += "WHERE\n"
    cypher += blockLine._1 + "."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY+"! = \""+blockLine._2._1+"\" AND\n"
    if (matcher._2!="")
      cypher += matcher._2 + " AND\n"
    cypher += "(" + blockLine._2._2.toCypherWhere + ")\n"

    cypher
  }

  private def clean(withList: mutable.MutableList[String], idxBegin : Int, unionCount: Int): mutable.MutableList[String] = {
    var ret : mutable.MutableList[String] = mutable.MutableList()
    withList foreach ( withValue => if (withList.indexOf(withValue)<idxBegin || withList.indexOf(withValue)>=idxBegin+unionCount) ret+=withValue)
    ret
  }

  private def cypherBlockUnion(withList: mutable.MutableList[String], idxBegin: Int, unionCount: Int, unionName: String): String = {
    var cypher : String = "\nSTART "+ unionName +" = node(*)\n"
    cypher += "WHERE\n"
    withList foreach ( withVal => if (withList.indexOf(withVal) >= idxBegin && withList.indexOf(withVal) < (idxBegin+unionCount-1)) {
      cypher += unionName + "."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+"! = " + withVal + "."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+" OR\n"
    } else if (withList.indexOf(withVal)==idxBegin+unionCount-1) {
      cypher += unionName + "."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+"! = " + withVal + "."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+"\n"
    })

    val dropList: mutable.MutableList[String] = clean(withList, idxBegin, unionCount)
    dropList += unionName; withList.clear ; withList ++= dropList

    cypher += "WITH "
    withList foreach (withVal => if (withList.last==withVal) {
      cypher += withVal + "\n"
    } else {
      cypher += withVal + ", "
    })

    cypher
  }

  private def cypherLinkMatch(startLink: String, endLink: String):String = {
    var cypher : String = "\nMATCH path = " + startLink + " -[:"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY+"|"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"*]- " + endLink + "\n"
    cypher += "WHERE\n"
    cypher += "ALL(n in nodes(path) where 1=length(filter(m in nodes(path) : m=n))) AND\n"
    cypher += "ALL(n in nodes(path) where n.MappingGraphVertexType <> \""+MappingDSGraphPropertyNames.DD_TYPE_CLUSTER_VALUE+"\")\n"
    cypher += "RETURN\n"
    cypher += "EXTRACT(co in FILTER( n in nodes(path): n.MappingGraphVertexType! = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\"): co.MappingGraphVertexID) as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes(path): n.MappingGraphVertexType! = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\"): no.MappingGraphVertexID) as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes(path): n.MappingGraphVertexType! = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\"): e.MappingGraphVertexID) as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes(path): n.MappingGraphVertexType! = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\"): t.MappingGraphVertexID) as TID,\n"
    cypher += "EXTRACT(l in FILTER( r in relationships(path) : type(r) = \""+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"\"): l.MappingGraphEdgeID) as LID;"

    cypher
  }

  def genQuery(): String = {
    var cypher : String = ""
    var withList : mutable.MutableList[String] = mutable.MutableList()
    var startLinkPointsCount : Int = 0
    var startLinkPoint : String = ""
    var endLinkPointsCount : Int = 0
    var endLinkPoint : String = ""

    for (lineVal:String <- startBlock.mapPointsPredicate.keySet) {
      cypher += cypherBlockStart((lineVal, startBlock.mapPointsPredicate.get(lineVal).get))
      withList += lineVal
      cypher += "WITH "
      withList foreach (withVal => if (withList.last==withVal) {
        cypher += withVal + "\n"
      } else {
        cypher += withVal + ", "
      })
    }

    startLinkPointsCount = withList.length
    if (startLinkPointsCount==1) startLinkPoint = withList.get(0).get
    else {
      startLinkPoint = "startUnion"
      cypher += cypherBlockUnion(withList, 0, startLinkPointsCount, startLinkPoint)
      startLinkPointsCount = 1
    }

    for (lineVal:String <- endBlock.mapPointsPredicate.keySet) {
      cypher += cypherBlockStart((lineVal, endBlock.mapPointsPredicate.get(lineVal).get))
      withList += lineVal
      cypher += "WITH "
      withList foreach ( withVal => if (withList.last==withVal) {
        cypher += withVal + "\n"
      } else {
        cypher += withVal + ", "
      })
    }

    endLinkPointsCount = withList.length-startLinkPointsCount
    if (endLinkPointsCount==1) endLinkPoint = withList.last
    else {
      endLinkPoint = "endUnion"
      cypher += cypherBlockUnion(withList, startLinkPointsCount, endLinkPointsCount, endLinkPoint)
    }

    cypher+=cypherLinkMatch(startLinkPoint, endLinkPoint)

    cypher
  }
}
