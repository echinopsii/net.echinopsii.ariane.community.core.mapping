package net.echinopsii.ariane.community.core.mapping.ds.mdsl

import net.echinopsii.ariane.community.core.mapping.ds.MapperParserException
import org.junit.runner.RunWith
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.junit.JUnitRunner
import scala.io.Source
import Matchers._

@RunWith(classOf[JUnitRunner])
class MapperParserTests extends FunSuite {

  private def traceMapperQuery(mapperQuery: MapperQueryGen) = {
    if (mapperQuery.startBlock!=null) {
      mapperQuery.startBlock.mapPointsPredicate foreach { case (startObjID, (startObjType, startObjPredicate)) => println("Start OBJ ID: " + startObjID + "\n" +
        "Start OBJ Type: " + startObjType + "\n" +
        "Start OBJ Predicate: " + startObjPredicate)
      }
    }

    if (mapperQuery.linkBlock!=null) {
      mapperQuery.linkBlock.mapPointsPredicate foreach {case (startObjID, (startObjType, startObjPredicate)) => println("PassThrough OBJ ID: " + startObjID + "\n" +
        "PassThrough OBJ Type: " + startObjType + "\n" +
        "PassThrough OBJ Predicate: " + startObjPredicate)}
      if (mapperQuery.linkBlock.path!=null && mapperQuery.linkBlock.path!="")
        println("custom path : " + mapperQuery.linkBlock.path + "\n")
    }

    if (mapperQuery.endBlock!=null) {
      mapperQuery.endBlock.mapPointsPredicate foreach { case (endObjID, (endObjType, endObjPredicate)) => println("End OBJ ID: " + endObjID + "\n" +
        "End OBJ Type: " + endObjType + "\n" +
        "End OBJ Predicate: " + endObjPredicate)
      }
    }
  }

  test("mdsl/mapperQuery10.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery10.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery1Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery11.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery11.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery1Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery20.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery20.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery2Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._1.toString==="node")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._2.toString==="startNode.nodeName = \"APP6969.tibrvrdl03prd01\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._1.toString==="node")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._2.toString==="endNode.nodeName = \"APP6969.tibrvrdl05prd01\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery21.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery21.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery2Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._1.toString==="node")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._2.toString==="startNode.nodeName = \"APP6969.tibrvrdl03prd01\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._1.toString==="node")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._2.toString==="endNode.nodeName = \"APP6969.tibrvrdl05prd01\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery30.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery30.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery3Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startEP")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startEP").get._1.toString==="endpoint")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startEP").get._2.toString==="startEP.endpointURL = \"multicast-udp-tibrv://tibrvrdl03prd01.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._1.toString==="endpoint")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._2.toString==="endEP.endpointURL = \"multicast-udp-tibrv://tibrvrdl05prd01.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery31.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery31.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery3Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startEP")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startEP").get._1.toString==="endpoint")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startEP").get._2.toString==="startEP.endpointURL = \"multicast-udp-tibrv://tibrvrdl03prd01.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._1.toString==="endpoint")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._2.toString==="endEP.endpointURL = \"multicast-udp-tibrv://tibrvrdl05prd01.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery40.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery40.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery4Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._1.toString==="endpoint")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._2.toString==="endEP.endpointURL = \"multicast-udp-tibrv://tibrvrdl05prd01.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery41.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery41.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery4Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._1.toString==="endpoint")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endEP").get._2.toString==="endEP.endpointURL = \"multicast-udp-tibrv://tibrvrdl05prd01.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery50.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery50.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery5Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainers")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainers").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainers").get._2.toString==="startContainers.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\" or startContainers.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdwprd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery51.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery51.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery5Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainers")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainers").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainers").get._2.toString==="startContainers.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\" or startContainers.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdwprd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery60.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery60.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery6Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._1.toString==="node")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._2.toString==="startNode.nodeName = \"APP6969.tibrvrdl03prd01\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdwprd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery61.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery61.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery6Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._1.toString==="node")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._2.toString==="startNode.nodeName = \"APP6969.tibrvrdl03prd01\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdwprd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery70.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery70.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery7Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdwprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._1.toString==="node")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._2.toString==="endNode.nodeName = \"APP6969.tibrvrdl03prd01\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery71.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery71.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery7Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdwprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._1.toString==="node")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endNode").get._2.toString==="endNode.nodeName = \"APP6969.tibrvrdl03prd01\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery80.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery80.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery8Result.cypher")).mkString
    val mapperQuery: MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._1.toString==="transport")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName =~ \".*239.69.69.69.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery81.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery81.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery8Result.cypher")).mkString
    val mapperQuery: MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._1.toString==="transport")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName =~ \".*239.69.69.69.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery90.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery90.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery9Result.cypher")).mkString
    val mapperQuery: MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._1.toString==="transport")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName =~ \".*239.69.69.69.*\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._2.toString==="ptEP.endpointURL =~ \".*tibrvrdmprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery91.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery91.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery9Result.cypher")).mkString
    val mapperQuery: MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._1.toString==="transport")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName =~ \".*239.69.69.69.*\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._2.toString==="ptEP.endpointURL =~ \".*tibrvrdmprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery100.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery100.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery10Result.cypher")).mkString
    val mapperQuery: MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._1.toString==="transport")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName =~ \".*239.69.69.69.*\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptContainer")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptContainer").get._1.toString==="container")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptContainer").get._2.toString==="ptContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdmprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery110.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery110.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery11Result.cypher")).mkString
    val mapperQuery: MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._1.toString==="transport")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName =~ \".*239.69.69.69.*\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptNode")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptNode").get._1.toString==="node")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptNode").get._2.toString==="ptNode.nodeName = \"APP6969.tibrvrdmprd01\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery120.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery120.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery12Result.cypher")).mkString
    val mapperQuery: MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("middleOfficeService")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("middleOfficeService").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("middleOfficeService").get._2.toString==="middleOfficeService.containerPrimaryAdminGate.nodeName =~ \"rbqcliadmingate.mo01\"")

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("rbqNode1EP2")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("rbqNode1EP2").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("rbqNode1EP2").get._2.toString==="rbqNode1EP2.endpointURL =~ \".*MiddleOfficeService.*\" or rbqNode1EP2.endpointURL =~ \".*RPC/BOQ.*\" or rbqNode1EP2.endpointURL =~ \".*RPC/RIQ.*\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("rbqNode2EP1")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("rbqNode2EP1").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("rbqNode2EP1").get._2.toString==="rbqNode2EP1.endpointURL =~ \".*MiddleOfficeService.*\" or rbqNode2EP1.endpointURL =~ \".*BOQ/BOQ.*\" or rbqNode2EP1.endpointURL =~ \".*RIQ/RIQ.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("riskService")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("riskService").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("riskService").get._2.toString==="riskService.containerPrimaryAdminGate.nodeName =~ \"rbqcliadmingate.risk01\"")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("backOfficeService")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("backOfficeService").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("backOfficeService").get._2.toString==="backOfficeService.containerPrimaryAdminGate.nodeName =~ \"rbqcliadmingate.bo01\"")

    assert(mapperQuery.genQuery===res)

  }

  test("mdsl/mapperQuery140.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery140.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery14Result.cypher")).mkString
    val parsedQ : String = MapperExecutorUtil.mapperQueryToCypherQuery(req)._1
    assert(parsedQ===res)
  }

  test("mdsl/mapperQuery150.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery150.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery15Result.cypher")).mkString
    val parsedQ : String = MapperExecutorUtil.mapperQueryToCypherQuery(req)._1
    assert(parsedQ===res)
  }

  test("mdsl/mapperQuery160.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery160.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery16Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerName = \"dekatonmac\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery170.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery170.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery17Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.startBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._1.toString==="container")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startContainer").get._2.toString==="startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode")!=None)
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._1.toString==="node")
    assert(mapperQuery.startBlock.mapPointsPredicate.get("startNode").get._2.toString==="startNode.nodeName = \"APP6969.tibrvrdl03prd01\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery180.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery180.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery18Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("dekatonmac")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("dekatonmac").get._1.toString==="container")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("dekatonmac").get._2.toString==="dekatonmac.containerName = \"dekatonmac\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery190.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery190.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery19Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("firefox_dekatonmac")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("firefox_dekatonmac").get._1.toString==="node")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("firefox_dekatonmac").get._2.toString==="firefox_dekatonmac.nodeName = \"[9626] firefox\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery200.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery200.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery20Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("unkown_remote_endpoint")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("unkown_remote_endpoint").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("unkown_remote_endpoint").get._2.toString==="unkown_remote_endpoint.endpointURL = \"tcp://178.236.6.191:443\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery210.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery210.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery21Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._1.toString==="transport")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName =~ \".*239.69.69.69.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQuery220.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQuery220.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/cypher/mapperQuery22Result.cypher")).mkString
    val mapperQuery:MapperQueryGen = new MapperParser("cypher").parse(req)

    //traceMapperQuery(mapperQuery)
    //println(mapperQuery.genQuery)

    assert(mapperQuery.linkBlock.mapPointsPredicate.size===2)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("firefox_dekatonmac")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("firefox_dekatonmac").get._1.toString==="node")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("firefox_dekatonmac").get._2.toString==="firefox_dekatonmac.nodeName = \"[9626] firefox\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("unkown_remote_endpoint")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("unkown_remote_endpoint").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("unkown_remote_endpoint").get._2.toString==="unkown_remote_endpoint.endpointURL = \"tcp://178.236.6.191:443\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mdsl/mapperQueryError01.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError01.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("[from] : invalid keyword usage.")
  }

  test("mdsl/mapperQueryError02.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError02.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("[where] : invalid keyword usage.")
  }

  test("mdsl/mapperQueryError03.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError03.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("acontainer expected but not found : \nFROM container WHERE >endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")
  }

  test("mdsl/mapperQueryError04.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError04.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("container | node | endpoint | transport expected but not found : \nFROM >contNainer WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"")
  }

  test("mdsl/mapperQueryError05.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError05.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("{ expected but not found : \n >['startContainer': 'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"'}\n--\n{'endContainer': 'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"'}\n\n OR \n\n- expected but not found : \n >['startContainer': 'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"'}\n--\n{'endContainer': 'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"'}\n\n")
  }

  test("mdsl/mapperQueryError06.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError06.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("} expected but not found : \n{'startContainer': 'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\" >]\n--\n{'endContainer': 'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"'}\n\n OR \n\n- expected but not found : \n >{'startContainer': 'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl03prd01.*\"']\n--\n{'endContainer': 'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"'}\n\n")
  }

  test("mdsl/mapperQueryError07.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError07.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("\\z expected but not found : \n{\n\t'dekatonmac': 'FROM container WHERE dekatonmac.containerName = \"dekatonmac\"'\n} >-\n\n OR \n\n-- expected but not found : \n{\n\t'dekatonmac': 'FROM container WHERE dekatonmac.containerName = \"dekatonmac\"'\n} >-\n\n OR \n\n{ expected but end of source found : \n{\n\t'dekatonmac': 'FROM container WHERE dekatonmac.containerName = \"dekatonmac\"'\n}\n-< \n\n OR \n\n- expected but not found : \n >{\n\t'dekatonmac': 'FROM container WHERE dekatonmac.containerName = \"dekatonmac\"'\n}\n-\n\n")
  }

  test("mdsl/mapperQueryError08.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mdsl/mapperQueryError08.ccmon")).mkString
    val thrown = the [MapperParserException] thrownBy new MapperParser("cypher").parse(req)
    thrown.getMessage should equal ("{ expected but not found : \n >-\n{\n\t'dekatonmac': 'FROM container WHERE dekatonmac.containerName = \"dekatonmac\"'\n}\n\n OR \n\n- expected but end of source found : \n-\n{\n\t'dekatonmac': 'FROM container WHERE dekatonmac.containerName = \"dekatonmac\"'\n}< \n\n")
  }
}