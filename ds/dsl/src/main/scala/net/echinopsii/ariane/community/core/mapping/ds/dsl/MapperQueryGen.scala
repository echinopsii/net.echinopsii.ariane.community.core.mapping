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
package net.echinopsii.ariane.community.core.mapping.ds.dsl

import net.echinopsii.ariane.community.core.mapping.ds.dsl.internal.{Block,Predicate}
import scala.collection.mutable
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames

abstract class MapperQueryGen(val startBlock: Block, val linkBlock: Block, val endBlock : Block) {
  def genQuery(): String
}

case class MapperToCypherQueryGen(override val startBlock: Block, override val linkBlock: Block, override val endBlock: Block) extends MapperQueryGen(startBlock, linkBlock, endBlock) {
  private def cypherBlockBorder(blockLine:(String,(String,Predicate))):String = {
    val matcher = blockLine._2._2.toCypherMatch(blockLine._2._1)
    var cypher = ""
    if (matcher._1 != "")
      cypher = "\nMATCH " + matcher._1 + "\n"
    else
      cypher = "\nMATCH (" + blockLine._1 + ":"+ blockLine._2._1 +")\n"
    cypher += "WHERE\n"
    //cypher += blockLine._1 + "." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+blockLine._2._1+"\" AND\n"
    if (matcher._2!="")
      cypher += matcher._2 + " AND\n"
    cypher += "(" + blockLine._2._2.toCypherWhere + ")\n"

    cypher
  }

  private def cypherBlockLink(blockLine:(String,(String,Predicate))):String = {
    var cypher = ""
    blockLine._2._1 match {
      case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE | MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE => cypher = cypherBlockBorder(blockLine)
      case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE | MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE => {
        val matcher = blockLine._2._2.toCypherMatch(blockLine._2._1)
        cypher += "\nMATCH (" + blockLine._1 + ":" + blockLine._2._1 +") -[:"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY +"*]-> (" + blockLine._1 + "EPs:endpoint)"
        if (matcher._1 != "") {
          cypher += ", " + matcher._1 + "\n"
        } else {
          cypher += "\n"
        }
        cypher += "WHERE\n"
        if (matcher._2!="")
          cypher += matcher._2 + " AND\n"

        cypher += "(" + blockLine._2._2.toCypherWhere + ")\n"
      }
    }

    cypher
  }

  private def clean(withList: mutable.MutableList[String], idxBegin : Int, unionCount: Int): mutable.MutableList[String] = {
    var ret : mutable.MutableList[String] = mutable.MutableList()
    withList foreach ( withValue => if (withList.indexOf(withValue)<idxBegin || withList.indexOf(withValue)>=idxBegin+unionCount) ret+=withValue)
    ret
  }

  private def cypherBlockUnion(withList: mutable.MutableList[String], idxBegin: Int, unionCount: Int, unionName: String): String = {
    var cypher : String = "\nMATCH "+ unionName +"\n"
    cypher += "WHERE\n"
    withList foreach ( withVal => if (withList.indexOf(withVal) >= idxBegin && withList.indexOf(withVal) < (idxBegin+unionCount-1)) {
      cypher += unionName + "."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + " = " + withVal + "." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + " OR\n"
    } else if (withList.indexOf(withVal)==idxBegin+unionCount-1) {
      cypher += unionName + "."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + " = " + withVal + "." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + "\n"
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

  private def cypherLinkMatch(startLink: String, passThroughLink: String, endLink: String):String = {
    var cypher : String = "\nMATCH path = " + startLink + " -[:"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY+"|"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"*]- "
    if (passThroughLink!=null && passThroughLink!="")
      cypher += passThroughLink + " -[:"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY+"|"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"*]- "
    cypher += endLink + "\n"
    cypher += "WHERE\n"
    cypher += "ALL(n in nodes(path) where 1=length(filter(m in nodes(path) WHERE m=n)))\n"
    cypher += "RETURN DISTINCT\n"
    cypher += "EXTRACT(co in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\")| co." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\")| no." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\")| e." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\")| t." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as TID,\n"
    cypher += "EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = \""+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"\")| l." + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID + ") as LID;"

    cypher
  }

  private def cypherCustomLinkMatch(startLink: String, customPath: String, endLink: String):String = {
    var cypher : String = "\nMATCH path = " + startLink + customPath + endLink + "\n"
    cypher += "RETURN DISTINCT\n"
    cypher += "EXTRACT(co in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\")| co." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\")| no." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\")| e." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\")| t." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as TID,\n"
    cypher += "EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = \""+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"\")| l." + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID + ") as LID;"

    cypher
  }

  private def cypherStandaloneBlockReturn(union: String): String = {
    var cypher : String = "\nRETURN DISTINCT\n"
    cypher += "EXTRACT(co in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\")| co." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\")| no." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\")| e." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\")| t." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as TID;"

    cypher
  }

  def genQuery(): String = {
    var cypher : String = ""
    var withList : mutable.MutableList[String] = mutable.MutableList()
    var returnList : mutable.MutableList[String] = mutable.MutableList()
    var startLinkPointsCount : Int = 0
    var startLinkPoint : String = ""
    var endLinkPointsCount : Int = 0
    var endLinkPoint : String = ""
    var passThroughPointsCount : Int = 0
    var passThroughLinkPoint : String = ""
    var customPath : String = ""

    var standAloneBlockPointsCount : Int = 0
    var standAloneBlockPoint : String = ""

    if (startBlock!=null && endBlock!=null) {
      for (lineVal: String <- startBlock.mapPointsPredicate.keySet) {
        cypher += cypherBlockBorder((lineVal, startBlock.mapPointsPredicate.get(lineVal).get))
        withList += lineVal
        cypher += "WITH "
        withList foreach (withVal => if (withList.last == withVal) {
          cypher += withVal + "\n"
        } else {
          cypher += withVal + ", "
        })
      }

      startLinkPointsCount = withList.length
      if (startLinkPointsCount == 1) startLinkPoint = withList.get(0).get
      else {
        startLinkPoint = "startUnion"
        cypher += cypherBlockUnion(withList, 0, startLinkPointsCount, startLinkPoint)
        startLinkPointsCount = 1
      }

      for (lineVal: String <- endBlock.mapPointsPredicate.keySet) {
        cypher += cypherBlockBorder((lineVal, endBlock.mapPointsPredicate.get(lineVal).get))
        withList += lineVal
        cypher += "WITH "
        withList foreach (withVal => if (withList.last == withVal) {
          cypher += withVal + "\n"
        } else {
          cypher += withVal + ", "
        })
      }

      endLinkPointsCount = withList.length - startLinkPointsCount
      if (endLinkPointsCount == 1) endLinkPoint = withList.last
      else {
        endLinkPoint = "endUnion"
        cypher += cypherBlockUnion(withList, startLinkPointsCount, endLinkPointsCount, endLinkPoint)
        endLinkPointsCount = 1
      }

      if (linkBlock != null) {
        for (lineVal: String <- linkBlock.mapPointsPredicate.keySet) {
          cypher += cypherBlockLink((lineVal, linkBlock.mapPointsPredicate.get(lineVal).get))
          linkBlock.mapPointsPredicate.get(lineVal).get._1 match {
            case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE | MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE => withList += lineVal
            case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE | MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE => withList += lineVal + "EPs"
          }

          cypher += "WITH "
          withList foreach (withVal => if (withList.last == withVal) {
            cypher += withVal + "\n"
          } else {
            cypher += withVal + ", "
          })
        }

        if (linkBlock.path != null && linkBlock.path != "") customPath = linkBlock.path
        else {
          passThroughPointsCount = withList.length - (startLinkPointsCount + endLinkPointsCount)
          if (passThroughPointsCount == 1) passThroughLinkPoint = withList.last
          else {
            passThroughLinkPoint = "ptUnion"
            cypher += cypherBlockUnion(withList, startLinkPointsCount + endLinkPointsCount, passThroughPointsCount, passThroughLinkPoint)
          }
        }
      }

      if (customPath!=null && customPath!="")
        cypher+=cypherCustomLinkMatch(startLinkPoint, customPath, endLinkPoint)
      else
        cypher+=cypherLinkMatch(startLinkPoint, passThroughLinkPoint, endLinkPoint)

    } else if (startBlock!=null && linkBlock==null && endBlock==null) {
      for (lineVal: String <- startBlock.mapPointsPredicate.keySet) {
        cypher += cypherBlockBorder((lineVal, startBlock.mapPointsPredicate.get(lineVal).get))
        withList += lineVal
        cypher += "WITH "
        withList foreach (withVal => if (withList.last == withVal) {
          cypher += withVal + "\n"
        } else {
          cypher += withVal + ", "
        })
      }

      standAloneBlockPointsCount = withList.length
      if (standAloneBlockPointsCount == 1) standAloneBlockPoint = withList.get(0).get
      else {
        standAloneBlockPoint = "standaloneBlockUnion"
        cypher += cypherBlockUnion(withList, 0, standAloneBlockPointsCount, standAloneBlockPoint)
      }

      cypher += cypherStandaloneBlockReturn(standAloneBlockPoint)

    } else if (startBlock==null && linkBlock!=null && endBlock==null) {


    }

    cypher
  }
}