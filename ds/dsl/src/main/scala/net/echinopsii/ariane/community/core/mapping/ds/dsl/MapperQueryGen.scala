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

  private def cypherWithConcat(withList: mutable.MutableList[String]): String = {
    var cypher : String = ""
    cypher += "WITH "
    withList foreach (withVal => if (withList.last == withVal) {
      cypher += withVal + "\n"
    } else {
      cypher += withVal + ", "
    })
    cypher
  }

  private def cypherBlockBorderMatch(blockLine:(String,(String,Predicate))):String = {
    val matcher = blockLine._2._2.toCypherMatch(blockLine._2._1)
    var cypher : String = ""
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

  private def cypherBlockBorkerMatchLoop(block: Block, withList: mutable.MutableList[String]): String = {
    var cypher : String = ""
    for (lineVal: String <- block.mapPointsPredicate.keySet) {
      cypher += cypherBlockBorderMatch((lineVal, block.mapPointsPredicate.get(lineVal).get))
      withList += lineVal
      cypher += cypherWithConcat(withList)
    }
    cypher
  }

  private def cypherBlockLinkMatch(blockLine:(String,(String,Predicate))):String = {
    var cypher : String = ""
    blockLine._2._1 match {
      case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE | MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE => cypher = cypherBlockBorderMatch(blockLine)
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

  private def cypherBlockLinkMatchLoop(block: Block, withList: mutable.MutableList[String]):String = {
    var cypher : String = ""
    for (lineVal: String <- block.mapPointsPredicate.keySet) {
      cypher += cypherBlockLinkMatch((lineVal, block.mapPointsPredicate.get(lineVal).get))
      linkBlock.mapPointsPredicate.get(lineVal).get._1 match {
        case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE | MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE => withList += lineVal
        case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE | MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE => withList += lineVal + "EPs"
      }
      cypher += cypherWithConcat(withList)
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
    dropList += unionName; withList.clear() ; withList ++= dropList

    cypher += cypherWithConcat(withList)

    cypher
  }

  private def cypherMakeBlockUnion(block: Block, withList: mutable.MutableList[String], unionName: String, unionStart: Int): (String, String, Int) = {
    var cypher : String = ""
    var linkPoint = ""
    var withListLength = withList.length - unionStart
    if (withListLength == 1) linkPoint = withList.get(unionStart).get
    else {
      linkPoint = unionName
      cypher += cypherBlockUnion(withList, unionStart, withListLength, linkPoint)
      withListLength = 1
    }
    (cypher, linkPoint, withListLength)
  }

  private def cypherLinkReturn(startLink: String, passThroughLink: String, endLink: String):String = {
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

  private def cypherCustomLinkReturn(startLink: String, customPath: String, endLink: String):String = {
    var cypher : String = "\nMATCH path = " + startLink + customPath + endLink + "\n"
    cypher += "RETURN DISTINCT\n"
    cypher += "EXTRACT(co in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\")| co." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\")| no." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\")| e." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\")| t." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as TID,\n"
    cypher += "EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = \""+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"\")| l." + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID + ") as LID;"

    cypher
  }

  private def cypherBlockWholeStackReturn(union: String): String = {
    var cypher : String = "\nMATCH path = " + union + " -[:" + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY + "*]-> internalObjects\n"
    cypher += "WHERE\n"
    cypher += "ALL(n in nodes(path) where 1=length(filter(m in nodes(path) WHERE m=n)))\n"
    cypher += "RETURN DISTINCT\n"
    cypher += "EXTRACT(co in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\")| co." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\")| no." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\")| e." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\")| t." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as TID,\n"
    cypher += "EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = \""+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"\")| l." + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID + ") as LID"

    cypher
  }

  private def cypherBlockWholeLinksReturn(union: String): String = {
    var cypher : String = "\nMATCH path = " + union +
      " -[:" + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY + "*]-> (internalEndpoint:endpoint)" +
      " -[:" + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY + "]- (externalEndpoint:endpoint)\n"
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


  private def cypherStandaloneBorderBlockReturn(union: String): String = {
    var cypher : String = "\nRETURN DISTINCT\n"
    cypher += "EXTRACT(co in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\")| co." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\")| no." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\")| e." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes("+union+") WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\")| t." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as TID,\n"
    cypher += "EXTRACT(l in FILTER( r in relationships("+union+") WHERE type(r) = \""+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"\")| l." + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID + ") as LID;"

    cypher
  }

  private def cypherStandaloneLinkBlockReturn(union: String): String = {
    var cypher : String = "\nMATCH path = " + union + " -[:"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"]- remoteEPs\n"
    cypher += "RETURN DISTINCT\n"
    cypher += "EXTRACT(co in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE+"\")| co." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as CID,\n"
    cypher += "EXTRACT(no in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE+"\")| no." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as NID,\n"
    cypher += "EXTRACT(e in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE+"\")| e." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as EID,\n"
    cypher += "EXTRACT(t in FILTER( n in nodes(path) WHERE n." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY + " = \""+MappingDSGraphPropertyNames.DD_TYPE_TRANSPORT_VALUE+"\")| t." + MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID + ") as TID,\n"
    cypher += "EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = \""+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY+"\")| l." + MappingDSGraphPropertyNames.DD_GRAPH_EDGE_ID + ") as LID;"

    cypher
  }

  def genQuery(): String = {
    var cypher : String = ""
    val withList : mutable.MutableList[String] = mutable.MutableList()

    if (startBlock!=null && endBlock!=null) {
      var startLinkGroupCount : Int = 0
      var startLinkGroup : String = ""
      var endLinkGroupCount : Int = 0
      var endLinkGroup : String = ""
      var passThroughLinkGroup : String = ""
      var customPath : String = ""

      cypher += cypherBlockBorkerMatchLoop(startBlock, withList)
      cypherMakeBlockUnion(startBlock, withList, "startUnion", 0) match {
        case(a,b,c) => cypher += a; startLinkGroup = b; startLinkGroupCount = c;
      }

      cypher += cypherBlockBorkerMatchLoop(endBlock, withList)
      cypherMakeBlockUnion(startBlock, withList, "endUnion", startLinkGroupCount) match {
        case(a,b,c) => cypher += a; endLinkGroup = b; endLinkGroupCount = c;
      }

      if (linkBlock != null) {
        cypher += cypherBlockLinkMatchLoop(linkBlock, withList)
        if (linkBlock.path != null && linkBlock.path != "") customPath = linkBlock.path
        else {
          cypherMakeBlockUnion(linkBlock, withList, "ptUnion", startLinkGroupCount + endLinkGroupCount) match {
            case(a,b,c) => cypher += a; passThroughLinkGroup = b;
          }
        }
      }

      if (customPath!=null && customPath!="")
        cypher+=cypherCustomLinkReturn(startLinkGroup, customPath, endLinkGroup)
      else
        cypher+=cypherLinkReturn(startLinkGroup, passThroughLinkGroup, endLinkGroup)

    } else if (startBlock!=null && linkBlock==null && endBlock==null) {
      var standAloneBlockGroup : String = ""
      var cypherStack : String = ""
      var cypherLinks : String = ""

      cypherStack += cypherBlockBorkerMatchLoop(startBlock, withList)
      cypherLinks += cypherStack

      cypherMakeBlockUnion(startBlock, withList, "standaloneBlockUnion", 0) match {
        case(a,b,c) => cypherStack += a ; cypherLinks += a; standAloneBlockGroup = b;
      }

      cypherStack += cypherBlockWholeStackReturn(standAloneBlockGroup)
      cypherLinks += cypherBlockWholeLinksReturn(standAloneBlockGroup)

      cypher += cypherStack + "\n\nUNION\n" + cypherLinks

      //cypher += cypherStandaloneBorderBlockReturn(standAloneBlockGroup)

    } else if (startBlock==null && linkBlock!=null && endBlock==null) {
      var standAloneLinkGroup : String = ""
      cypher += cypherBlockLinkMatchLoop(linkBlock, withList)
      cypherMakeBlockUnion(startBlock, withList, "standaloneLinkUnion", 0) match {
        case(a,b,c) => cypher += a; standAloneLinkGroup = b;
      }
      cypher += cypherStandaloneLinkBlockReturn(standAloneLinkGroup)
    }

    cypher
  }
}