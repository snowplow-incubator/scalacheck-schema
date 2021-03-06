/*
 * Copyright (c) 2018-2021 Snowplow Analytics Ltd. All rights reserved.
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

import cats.implicits._
import org.scalacheck.Gen
import org.scalacheck.cats.implicits._

object Utils {
  def traverseMap[K, S, O](
    hashMap: Map[K, S]
  )(f: S => Gen[O]): Gen[List[(K, O)]] =
    hashMap
      .map { case (k, v) => f(v).map { (k, _) } }
      .toList
      .sequence[Gen, (K, O)]
}
