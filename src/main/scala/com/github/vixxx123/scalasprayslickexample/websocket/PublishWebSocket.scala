/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */


package com.github.vixxx123.scalasprayslickexample.websocket

import akka.actor.ActorContext
import com.github.vixxx123.scalasprayslickexample.rest.auth.RestApiUser
import com.github.vixxx123.scalasprayslickexample.util.JsonUtil

/**
 * Allows to send push messages via websocket
 */
trait PublishWebSocket {

  /**
   * Pushes message to all open sockets
   * @param messageToPush - message to be pushed
   * @param context - akka actor context
   */
  def publishAll(messageToPush: PublishMessage)(implicit context: ActorContext) {
    val websocket = context.actorSelection(s"/user/websocket")
    websocket ! Push(JsonUtil.serialize(messageToPush))
  }

  /**
   * Pushes message to specific user
   *
   * @param user - user to which message should be pushed
   *
   * @param messageToPush - message to be pushed
   * @param context - akka actor context
   */
  def publishToUser(user: RestApiUser, messageToPush: PublishMessage)(implicit context: ActorContext) {
    val websocket = context.actorSelection(s"/user/websocket")
    websocket ! PushToUser(user, JsonUtil.serialize(messageToPush))
  }
}

/**
 * Base Push message
 * @param messageType - type of message
 */
sealed abstract class PublishMessage(val messageType: String)

case class RawTextPublishMessage(text: String) extends PublishMessage("raw")
case class CreatePublishMessage(entityType: String, uri: String, createdEntity: Any) extends PublishMessage("create")
case class UpdatePublishMessage(entityType: String, uri: String, updatedEntity: Any) extends PublishMessage("update")
case class DeletePublishMessage(entityType: String, id: Int) extends PublishMessage("delete")
