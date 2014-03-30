package com.spectral.cc.core.mapping.mapper

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.io.Source

@RunWith(classOf[JUnitRunner])
class MapperParserTests extends FunSuite {

  test("mapperQuery10.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery10.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery11.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery11.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery20.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery20.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery21.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery21.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery30.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery30.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery31.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery31.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery40.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery40.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery41.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery41.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery50.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery50.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery51.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery51.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery60.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery60.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery61.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery61.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery70.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery70.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

  test("mapperQuery71.ccmon") {
    val req = Source.fromURL(getClass.getResource("/mapperQuery71.ccmon")).mkString
    def mapper = new MapperParser()
    mapper.parse(req)
  }

}
