/*
 * Copyright (C) 2011 leclaire
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.implementation.workflow

import java.awt.Point
import org.netbeans.api.visual.anchor.PointShape
import org.netbeans.api.visual.widget.Widget
import org.openmole.ide.core.model.workflow.ICapsuleUI
import org.openmole.ide.core.model.workflow.IInputSlotWidget
import org.netbeans.api.visual.action.ActionFactory
import org.openmole.ide.core.implementation.provider.TransitionMenuProvider
import org.openmole.ide.core.model.commons.Constants._
import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap

class BuildMoleScene(n: String) extends MoleScene {

  def this() = this("")
  manager.name = Some(n)
  override val isBuildScene = true
  
  def copy =  {
    var capsuleMapping = new HashMap[ICapsuleUI,ICapsuleUI]
    var islots = new HashMap[IInputSlotWidget, IInputSlotWidget]
    val ms = new ExecutionMoleScene
    manager.capsules.foreach(n=> {
        val (caps,islotMapping) = n._2.copy(ms)
        if (n._2.startingCapsule) ms.manager.setStartingCapsule(caps)
        SceneItemFactory.createCapsule(caps,ms, new Point(n._2.connectableWidget.x.toInt,n._2.connectableWidget.y.toInt))
        capsuleMapping+= n._2-> caps
        islots++= islotMapping})
    manager.transitions.foreach(t=> {SceneItemFactory.createEdge(ms,capsuleMapping(t.source), islots(t.target), t.transitionType, t.condition)
      })
    ms
  }
  
  def initCapsuleAdd(w: ICapsuleUI)= {
    obUI= Some(w.asInstanceOf[Widget])
    obUI.get.createActions(SELECT).addAction(selectAction)
    obUI.get.createActions(CONNECT).addAction(connectAction)
    obUI.get.createActions(CONNECT).addAction(moveAction)
  }
  
  def attachEdgeWidget(e: String)= {
    val connectionWidget = new LabeledConnectionWidget(graphScene,manager.transition(e))
    connectLayer.addChild(connectionWidget);
    connectionWidget.setEndPointShape(PointShape.SQUARE_FILLED_BIG)
    connectionWidget.getActions.addAction(createSelectAction)
    connectionWidget.getActions.addAction(createObjectHoverAction)
    connectionWidget.getActions.addAction(reconnectAction)
    connectionWidget.getActions.addAction(ActionFactory.createPopupMenuAction(new TransitionMenuProvider(this,connectionWidget)));
    connectionWidget
  }
}