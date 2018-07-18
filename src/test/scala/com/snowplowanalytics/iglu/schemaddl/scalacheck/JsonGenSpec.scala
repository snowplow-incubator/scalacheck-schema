/*
 * Copyright (c) 2018 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.iglu.schemaddl.scalacheck

import org.json4s.JsonAST._
import org.json4s.jackson.{ parseJson, compactJson }

import com.snowplowanalytics.iglu.schemaddl.jsonschema.Schema
import com.snowplowanalytics.iglu.schemaddl.jsonschema.json4s.Json4sToSchema._

import org.scalacheck.Arbitrary
import org.specs2.{ScalaCheck, Specification}

class JsonGenSpec extends Specification with ScalaCheck { def is = s2"""
  int builds valid integer with minimum $e1
  int builds valid integer with minimum and maximum $e2
  jsonObject builds valid with required keys $e3
  json builds valid JSON from enum $e4
  json builds valid JSON for oneOf even without properties $e5
  """

  def e1 = {
    val json = """{"type": "integer", "minimum": 10 }""".stripMargin
    val input = Schema.parse(parseJson(json)).getOrElse(throw new RuntimeException("Invalid schema"))

    implicit val arb: Arbitrary[JValue] = Arbitrary(JsonGenSchema.int(input))

    val check: JValue => Boolean = {
      case JInt(i) => i >= 10
      case _ => false
    }

    prop(check)
  }

  def e2 = {
    val json = """{"type": "integer", "minimum": 10, "maximum": 100 }""".stripMargin
    val input = Schema.parse(parseJson(json)).getOrElse(throw new RuntimeException("Invalid schema"))

    implicit val arb: Arbitrary[JValue] = Arbitrary(JsonGenSchema.int(input))

    val check: JValue => Boolean = {
      case JInt(i) => i >= 10 && i <= 100
      case _ => false
    }

    prop(check)
  }

  def e3 = {
    val json =
      """
        |{
        |  "type": "object",
        |  "properties": {
        |    "foo": {
        |      "type": "integer",
        |      "maximum": 5
        |    },
        |    "bar": {
        |      "type": "integer",
        |      "maximum": 5
        |    }
        |  },
        |  "required": ["foo"],
        |  "additionalProperties": true
        |}""".stripMargin

    val input = Schema.parse(parseJson(json)).getOrElse(throw new RuntimeException("Invalid schema"))

    implicit val arb: Arbitrary[JValue] =
      Arbitrary(JsonGenSchema.jsonObject(input))

    val check: JValue => Boolean = {
      case JObject(fields) if fields.map(_._1).contains("foo") => true
      case _ => false
    }

    prop(check)
  }

  def e4 = {
    val json =
      """{ "enum": ["one", 2, [], false] }""".stripMargin
    val input = Schema.parse(parseJson(json)).getOrElse(throw new RuntimeException("Invalid schema"))

    implicit val arb: Arbitrary[JValue] =
      Arbitrary(JsonGenSchema.json(input))

    val check: JValue => Boolean = {
      case JString("one") => true
      case JInt(x) => x == 2
      case JArray(List()) => true
      case JBool(false) => true
      case _ => false
    }

    prop(check)
  }

  def e5 = {
    val json =
      """{ "oneOf": [
        |  {
        |    "type": "object",
        |    "required": [ "pojo" ]
        |  },
        |  {
        |    "type": "object",
        |    "required": [ "json" ]
        |  }
        |]}""".stripMargin
    val input = Schema.parse(parseJson(json)).getOrElse(throw new RuntimeException("Invalid schema"))

    val check: JValue => Boolean = {
      case JObject(List(("pojo", _))) => true
      case JObject(List(("json", _))) => true
      case j =>
        println(compactJson(j))
        false
    }

    implicit val arb: Arbitrary[JValue] =
      Arbitrary(JsonGenSchema.json(input))

    prop(check)
  }
}
