package com.vixxx123.websocket

import akka.actor.ActorContext
import com.vixxx123.util.JsonUtil

trait PublishWebSocket {

  def publishAll(pubSubMsg: PublishMessage)(implicit context: ActorContext) = {
    val websocket = context.actorSelection(s"/user/websocket")
    websocket ! Push(JsonUtil.serialize(pubSubMsg))
  }
}

sealed class PublishMessage(val operation: String)
case class RawTextPublishMessage(text: String) extends PublishMessage("raw")
case class CreatePublishMessage(entityType: String, uri: String, createdEntity: Any) extends PublishMessage("create")
case class UpdatePublishMessage(entityType: String, uri: String, updatedEntity: Any) extends PublishMessage("update")
case class DeletePublishMessage(entityType: String, id: Int) extends PublishMessage("delete")
