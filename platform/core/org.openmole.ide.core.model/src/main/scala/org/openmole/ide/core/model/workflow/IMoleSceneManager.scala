/*
 * Copyright (C) 2011 <mathieu.leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.model.workflow

import org.apache.commons.collections15.bidimap.DualHashBidiMap
import org.openmole.ide.core.model.commons.TransitionType
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet

trait IMoleSceneManager {
  def name: Option[String]
  
  def name_=(n:Option[String])
  
  def startingCapsule_=(n:Option[ICapsuleUI])
  
  def getNodeID: String
  
  def getEdgeID: String
  
  def capsuleID(cv: ICapsuleUI): String
  
  def capsules:DualHashBidiMap[String, ICapsuleUI]
  
  def startingCapsule: Option[ICapsuleUI]
  
  def capsuleConnections:  HashMap[ICapsuleUI, HashSet[ITransitionUI]]
  
  def removeCapsuleUI(nodeID: String):Unit
  
  def registerCapsuleUI(cv: ICapsuleUI):Unit
  
  def setStartingCapsule(capsule: ICapsuleUI): Iterable[String]
  
  def transitions: Iterable[ITransitionUI] 
  
  def transition(edgeID: String): ITransitionUI
  
  def removeTransition(edgeID: String)
  
  def registerTransition(s: ICapsuleUI, t:IInputSlotWidget,transitionType: TransitionType.Value,cond: Option[String]): Boolean
  
  def registerTransition(edgeID: String,s: ICapsuleUI, t:IInputSlotWidget,transitionType: TransitionType.Value,cond: Option[String]): Boolean
 }