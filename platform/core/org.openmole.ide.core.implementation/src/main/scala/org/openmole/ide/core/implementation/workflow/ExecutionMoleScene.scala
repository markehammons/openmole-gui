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

import org.netbeans.api.visual.anchor.PointShape
import org.netbeans.api.visual.widget.Widget
import org.openmole.ide.core.model.workflow.ICapsuleUI
import org.openmole.ide.core.model.commons.Constants._
import scala.collection.JavaConversions._

class ExecutionMoleScene extends MoleScene{

  override val isBuildScene = false
  
  def initCapsuleAdd(w: ICapsuleUI)= {
    obUI= Some(w.asInstanceOf[Widget])
    obUI.get.createActions(SELECT).addAction(selectAction)
    obUI.get.createActions(CONNECT).addAction(moveAction)
  }
  
  def attachEdgeWidget(e: String)= {
    val connectionWidget = new LabeledConnectionWidget(graphScene,manager.transition(e))
    connectLayer.addChild(connectionWidget);
    connectionWidget.setEndPointShape(PointShape.SQUARE_FILLED_BIG)
    connectionWidget
  }
}