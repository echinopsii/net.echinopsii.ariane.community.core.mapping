/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE]
 * Copyright (C) 29/03/14 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.dsl.internal

import net.echinopsii.ariane.community.core.mapping.ds.domain.{Gate, Endpoint, Node, Container}
import net.echinopsii.ariane.community.core.mapping.ds.dsl.MapperParserException
import com.typesafe.scalalogging.slf4j.Logging
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames

abstract class Expression() {
  var eType: String

  def toCypherMatch(objectType: String) : (String, String)
  def toCypherWhere : String
  def calcType : String
}

case class IdentifierExp(var eType: String = "", iName: String, var iRoot: Option[IdentifierExp] = None, var iProp: Option[IdentifierExp] = None) extends Expression with Logging {
  override def toString() = if (iRoot!=None) {iRoot.get.toString+"."+iName} else {iName}

  def toCypherMatch(objectType: String) : (String,String) = {
    var cypherMatch:String = ""
    if (iRoot!=None && (iRoot.get.propertiesDepth == 1 && iRoot.get.iRoot != None)) {
      cypherMatch="("+globalRoot.iName+":"+objectType+")-[:"+MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY+"]->"+iRoot.get.toCypherWhere
    }
    logger.debug("[IDT ("+iName+") matcher] : "+cypherMatch)
    (cypherMatch,iRoot.get.toCypherWhere+"."+MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID+" = "+iRoot.get.toString)
  }
  def toCypherWhere : String = {
    var cypherWhere:String = ""
    if (iRoot!=None) {
      if (iRoot.get.propertiesDepth == 1) {
        cypherWhere=iRoot.get.toCypherWhere+"."+iName
      } else {
        cypherWhere=iRoot.get.toCypherWhere+iName.substring(0,1).toUpperCase()+iName.substring(1)
      }
    } else {
      cypherWhere = iName
    }
    cypherWhere
  }
  def calcType: String = {
    if (iRoot!=None && eType == "") {
      var clazz: Class[_ <: Any] = null
      iRoot.get.calcType match {
        case MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE => clazz = classOf[Container]
        case MappingDSGraphPropertyNames.DD_TYPE_NODE_VALUE => clazz = classOf[Node]
        case MappingDSGraphPropertyNames.DD_TYPE_GATE_VALUE => clazz = classOf[Gate]
        case MappingDSGraphPropertyNames.DD_TYPE_ENDPOINT_VALUE => clazz = classOf[Endpoint]
        case _ => throw new MapperParserException("Unexpected root identifier")
      }
      eType = clazz.getDeclaredMethod("get"+iName.substring(0,1).toUpperCase()+iName.substring(1)).getReturnType.toString
    }
    eType
  }

  def propertiesDepth: Int = if (iProp!=None) iProp.get.propertiesDepth+1 else 0
  def globalRoot: IdentifierExp = if (iRoot!=None) iRoot.get.globalRoot else this
}

case class StringExp(value: String) extends Expression {
  override var eType: String = "String"
  override def toString() = value

  def toCypherMatch(objectType: String) : (String,String) = ("","")
  def toCypherWhere : String = value
  def calcType : String = eType
}