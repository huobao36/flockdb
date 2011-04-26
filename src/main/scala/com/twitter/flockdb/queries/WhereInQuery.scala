/*
 * Copyright 2010 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twitter.flockdb
package queries

import com.twitter.gizzard.thrift.conversions.Sequences._
import shards.Shard

class WhereInQuery(shard: Shard, sourceId: Long, states: Seq[State], destinationIds: Seq[Long]) extends Query {
  def sizeEstimate() = destinationIds.size

  def selectWhereIn(page: Seq[Long]) = {
    shard.intersect(sourceId, states, (Set(destinationIds: _*) intersect Set(page: _*)).toSeq)
  }

  def selectPageByDestinationId(count: Int, cursor: Cursor) = {
    val results = shard.intersect(sourceId, states, destinationIds)
    new ResultWindow(results.map(result => (result, Cursor(result))), count, cursor)
  }

  def selectPage(count: Int, cursor: Cursor) = selectPageByDestinationId(count, cursor)

  override def toString =
    "<WhereInQuery sourceId="+sourceId+" states=("+states.map(_.name).mkString(",")+") shard="+shard+" destIds=("+destinationIds.mkString(",")+")>"
}
