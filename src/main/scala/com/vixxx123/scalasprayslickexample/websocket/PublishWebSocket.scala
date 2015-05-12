/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */


package com.vixxx123.scalasprayslickexample.websocket

import akka.actor.ActorContext
import com.vixxx123.scalasprayslickexample.util.JsonUtil

trait PublishWebSocket {

  def publishAll(pubSubMsg: PublishMessage)(implicit context: ActorContext) = {
    val websocket = context.actorSelection(s"/user/websocket")
    websocket ! Push(JsonUtil.serialize(pubSubMsg))
  }

  def publishToUser(userId: Int, pubSubMsg: PublishMessage)(implicit context: ActorContext) = {
    val websocket = context.actorSelection(s"/user/websocket")
    websocket ! PushToUser(userId, JsonUtil.serialize(pubSubMsg))
  }
}

sealed class PublishMessage(val operation: String)
case class RawTextPublishMessage(text: String) extends PublishMessage("raw")
case class CreatePublishMessage(entityType: String, uri: String, createdEntity: Any) extends PublishMessage("create")
case class UpdatePublishMessage(entityType: String, uri: String, updatedEntity: Any) extends PublishMessage("update")
case class DeletePublishMessage(entityType: String, id: Int) extends PublishMessage("delete")
