package net.echinopsii.ariane.community.core.mapping.ds.dsl

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.io.Source

@RunWith(classOf[JUnitRunner])
class MapperParserTests extends FunSuite {

  private def traceMapperQuery(mapperQuery: MapperQueryGen) = {
    mapperQuery.startBlock.mapPointsPredicate foreach {case (startObjID, (startObjType, startObjPredicate)) => println("Start OBJ ID: " + startObjID + "\n" +
      "Start OBJ Type: " + startObjType + "\n" +
      "Start OBJ Predicate: " + startObjPredicate)}

    if (mapperQuery.linkBlock!=null) {
      mapperQuery.linkBlock.mapPointsPredicate foreach {case (startObjID, (startObjType, startObjPredicate)) => println("PassThrough OBJ ID: " + startObjID + "\n" +
        "PassThrough OBJ Type: " + startObjType + "\n" +
        "PassThrough OBJ Predicate: " + startObjPredicate)}
    }

    mapperQuery.endBlock.mapPointsPredicate foreach {case (endObjID, (endObjType, endObjPredicate)) => println("End OBJ ID: " + endObjID + "\n" +
      "End OBJ Type: " + endObjType + "\n" +
      "End OBJ Predicate: " + endObjPredicate)}
  }

  test("mapperQuery10.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery10.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery1Result.cypher")).mkString
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

  test("mapperQuery11.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery11.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery1Result.cypher")).mkString
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

  test("mapperQuery20.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery20.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery2Result.cypher")).mkString
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

  test("mapperQuery21.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery21.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery2Result.cypher")).mkString
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

  test("mapperQuery30.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery30.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery3Result.cypher")).mkString
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

  test("mapperQuery31.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery31.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery3Result.cypher")).mkString
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

  test("mapperQuery40.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery40.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery4Result.cypher")).mkString
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

  test("mapperQuery41.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery41.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery4Result.cypher")).mkString
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

  test("mapperQuery50.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery50.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery5Result.cypher")).mkString
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

  test("mapperQuery51.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery51.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery5Result.cypher")).mkString
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

  test("mapperQuery60.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery60.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery6Result.cypher")).mkString
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

  test("mapperQuery61.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery61.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery6Result.cypher")).mkString
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

  test("mapperQuery70.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery70.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery7Result.cypher")).mkString
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

  test("mapperQuery71.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery71.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery7Result.cypher")).mkString
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

  test("mapperQuery80.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery80.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery8Result.cypher")).mkString
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
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName = \"multicast-udp-tibrv://;239.69.69.69\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mapperQuery81.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery81.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery8Result.cypher")).mkString
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
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName = \"multicast-udp-tibrv://;239.69.69.69\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mapperQuery90.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery90.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery9Result.cypher")).mkString
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
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName = \"multicast-udp-tibrv://;239.69.69.69\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._2.toString==="ptEP.endpointURL =~ \".*tibrvrdmprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mapperQuery91.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery91.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery9Result.cypher")).mkString
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
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName = \"multicast-udp-tibrv://;239.69.69.69\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._1.toString==="endpoint")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptEP").get._2.toString==="ptEP.endpointURL =~ \".*tibrvrdmprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mapperQuery100.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery100.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery10Result.cypher")).mkString
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
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName = \"multicast-udp-tibrv://;239.69.69.69\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptContainer")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptContainer").get._1.toString==="container")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptContainer").get._2.toString==="ptContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdmprd01.*\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }

  test("mapperQuery110.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery110.ccmon")).mkString
    val res = Source.fromURL(getClass.getResource("/mapperQuery11Result.cypher")).mkString
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
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("moulticast").get._2.toString==="moulticast.transportName = \"multicast-udp-tibrv://;239.69.69.69\"")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptNode")!=None)
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptNode").get._1.toString==="node")
    assert(mapperQuery.linkBlock.mapPointsPredicate.get("ptNode").get._2.toString==="ptNode.nodeName = \"APP6969.tibrvrdmprd01\"")

    assert(mapperQuery.endBlock.mapPointsPredicate.size===1)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer")!=None)
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._1.toString==="container")
    assert(mapperQuery.endBlock.mapPointsPredicate.get("endContainer").get._2.toString==="endContainer.containerPrimaryAdminGate.nodeName =~ \".*tibrvrdl05prd01.*\"")

    assert(mapperQuery.genQuery===res)
  }
}
