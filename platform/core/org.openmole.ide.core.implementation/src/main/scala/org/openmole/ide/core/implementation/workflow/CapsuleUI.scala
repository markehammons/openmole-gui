/*
 * Copyright (C) 2011 Mathieu leclaire <mathieu.leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.implementation.workflow

import org.netbeans.api.visual.action.ActionFactory
import org.netbeans.api.visual.widget.Widget
import org.openmole.ide.core.implementation.provider.DnDTaskIntoCapsuleProvider
import org.openmole.ide.core.implementation.provider.CapsuleMenuProvider
import org.openmole.ide.core.model.commons.Constants._
import org.openmole.ide.core.model.commons.CapsuleType._
import org.openmole.ide.core.model.commons.MoleSceneType._
import org.openmole.ide.core.model.workflow.IInputSlotWidget
import org.openmole.ide.core.model.workflow.ICapsuleUI
import org.openmole.ide.core.model.dataproxy.ITaskDataProxyUI
import org.openmole.ide.core.implementation.dataproxy.Proxys
import org.openmole.ide.core.model.workflow.IMoleScene
import scala.collection.mutable.HashMap

class CapsuleUI(val scene: IMoleScene, var dataProxy: Option[ITaskDataProxyUI],var capsuleType:CapsuleType,var startingCapsule: Boolean) extends Widget(scene.graphScene) with ICapsuleUI{
  def this(sc: IMoleScene) = this (sc,None,CAPSULE,false)
    
  
  createActions(MOVE).addAction (ActionFactory.createMoveAction)
  
  var nbInputSlots = 0
  val connectableWidget= new ConnectableWidget(scene,this)
  val dndTaskIntoCapsuleProvider = new DnDTaskIntoCapsuleProvider(scene, this)
  val capsuleMenuProvider= new CapsuleMenuProvider(scene, this)
  
  addChild(connectableWidget)
        
  getActions.addAction(ActionFactory.createPopupMenuAction(capsuleMenuProvider))
  getActions.addAction(ActionFactory.createAcceptAction(dndTaskIntoCapsuleProvider))
    
  override def widget = this
  
  override def copy(sc: IMoleScene) = {
    var slotMapping = new HashMap[IInputSlotWidget,IInputSlotWidget]
    val c = new CapsuleUI(sc,dataProxy,capsuleType,startingCapsule)
    connectableWidget.islots.foreach(i=>slotMapping+=i->c.addInputSlot(false))
    (c,slotMapping)
  }
  
  override def encapsule(dpu: ITaskDataProxyUI)= {
    setDataProxy(dpu)
    capsuleMenuProvider.addTaskMenus
  }
  
  def addInputSlot(on: Boolean): IInputSlotWidget =  {
    if (on || startingCapsule) clearInputSlots(on)
    
    nbInputSlots+= 1
    val im = new InputSlotWidget(scene.graphScene,this,nbInputSlots,startingCapsule,scene.moleSceneType == EXECUTION)
    connectableWidget.addInputSlot(im)
    scene.validate
    scene.refresh
    im
  }

  private def clearInputSlots(on: Boolean) = {
    startingCapsule = on
    nbInputSlots= 0
    connectableWidget.clearInputSlots
  }
  
  def removeInputSlot= nbInputSlots-= 1
  
  def setDataProxy(dpu: ITaskDataProxyUI)={
    dataProxy= Some(dpu)
    if (Proxys.isExplorationTaskData(dpu.dataUI)) {
      capsuleType = EXPLORATION_TASK 
      connectableWidget.addSampling
    } else capsuleType = BASIC_TASK
  }
}