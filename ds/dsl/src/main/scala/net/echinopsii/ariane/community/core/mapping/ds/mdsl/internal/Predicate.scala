/**
 * MDSL
 * Mapping Domain Specific Language
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
package net.echinopsii.ariane.community.core.mapping.ds.mdsl.internal

import com.typesafe.scalalogging.slf4j.Logging

abstract class Predicate extends Expression {
  override var eType: String = "Predicate"
}

case class And(left: Predicate, right: Predicate) extends Predicate with Logging {
  override def toString = left.toString + " and " + right.toString

  def toCypherMatch(objectType: String) : (String,String) = {
    val lmatcher : (String,String) = left.toCypherMatch(objectType)
    val rmatcher : (String,String) = right.toCypherMatch(objectType)
    logger.debug("[AND matcher] left : "+lmatcher+" ; right : " + rmatcher)

    var matcher : String = lmatcher._1
    var mjwhere : String = ""

    if (lmatcher._1 != "") {
      matcher = lmatcher._1
      mjwhere = lmatcher._2
    }
    if (rmatcher._1 != "" && rmatcher._1 != lmatcher._1) {
      if (matcher != "") {
        matcher += "," + rmatcher._1
        mjwhere += " AND " + rmatcher._2
      } else {
        matcher = rmatcher._1
        mjwhere = rmatcher._2
      }
    }

    (matcher,mjwhere)
  }
  def toCypherWhere : String = left.toCypherWhere + " and " + right.toCypherWhere
  def calcType : String = eType
}

case class Or(left: Predicate, right: Predicate) extends Predicate with Logging {
  override def toString = left.toString + " or " + right.toString

  def toCypherMatch(objectType: String) : (String,String) = {
    val lmatcher : (String,String) = left.toCypherMatch(objectType)
    val rmatcher : (String,String) = right.toCypherMatch(objectType)
    logger.debug("[OR matcher] left : "+lmatcher+" ; right : "+ rmatcher)

    var matcher : String = lmatcher._1
    var mjwhere : String = ""

    if (lmatcher._1 != "") {
      matcher = lmatcher._1
      mjwhere = lmatcher._2
    }
    if (rmatcher._1 != "" && rmatcher._1 != lmatcher._1) {
      if (matcher != "") {
        matcher += "," + rmatcher._1
        mjwhere += " AND " + rmatcher._2
      } else {
        matcher = rmatcher._1
        mjwhere = rmatcher._2
      }
    }

    (matcher,mjwhere)
  }
  def toCypherWhere : String = left.toCypherWhere + " or " + right.toCypherWhere
  def calcType : String = eType
}

case class Ops(left: Expression, right: Expression, ops: String) extends Predicate with Logging {
  override def toString = left.toString + " " + ops + " " + right.toString

  def toCypherMatch(objectType: String) : (String,String) = {
    val lmatcher : (String,String) = left.toCypherMatch(objectType)
    val rmatcher : (String,String) = right.toCypherMatch(objectType)
    logger.debug("[OPS ("+ops+")  matcher] left : "+lmatcher+" ; right : "+rmatcher)

    var matcher : String = ""
    var mjwhere : String = ""

    if (lmatcher._1 != "") {
      matcher = lmatcher._1
      mjwhere = lmatcher._2
    }
    if (rmatcher._1 != "" && rmatcher._1 != lmatcher._1) {
      if (matcher != "") {
        matcher += "," + rmatcher._1
        mjwhere += " AND " + rmatcher._2
      } else {
        matcher = rmatcher._1
        mjwhere = rmatcher._2
      }
    }

    (matcher,mjwhere)
  }

  def toCypherWhere : String = left.toCypherWhere + " " + ops + " " + right.toCypherWhere
  def calcType : String = eType
}
