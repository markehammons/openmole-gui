/*
 * Copyright (C) 2012 mathieu
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

import java.awt.BorderLayout
import javax.swing.JScrollPane
import org.openmole.ide.core.implementation.data.CheckData
import org.openmole.ide.core.implementation.execution.ScenesManager
import org.openmole.ide.core.model.workflow.ISceneContainer
import org.openmole.ide.misc.widget.MigPanel
import org.openmole.ide.misc.widget.ToolBarButton
import org.openmole.ide.misc.tools.image.Images._
import scala.collection.mutable.HashSet
import scala.swing.Action
import scala.swing.Panel
import org.openmole.ide.misc.tools.image.Images._
import scala.swing.ToggleButton
import scala.swing.event.ButtonClicked

class BuildMoleSceneContainer(val scene : BuildMoleScene) extends Panel with ISceneContainer{ buildContainer => 

  val executionMoleSceneContainers = new HashSet[ExecutionMoleSceneContainer]
  
  peer.setLayout(new BorderLayout)
  
  val toolBar = new MigPanel("") {
    
    
    val connectionButton = new ToggleButton {
      icon = CONNECT_TRANSITION_MODE
      selected = true
    }
    
    listenTo(connectionButton)
    reactions += {
      case x : ButtonClicked => 
        ScenesManager.connectMode = connectionButton.selected
        ScenesManager.connectMode match {
          case true=> connectionButton.icon = CONNECT_TRANSITION_MODE
          case false=> connectionButton.icon = DATA_CHANNEL_TRANSITION_MODE
        }
    }
    
    contents += connectionButton
    
    contents += new ToolBarButton(BUILD_EXECUTION,
                                  "Build the workflow",
                                  buildExecutionAction)
  
    contents += new ToolBarButton(CLEAN_BUILD_EXECUTION,
                                  "Clean and build the workflow",
                                  cleanAndBuildExecutionAction)
  }
   
  peer.add(toolBar.peer,BorderLayout.NORTH)
  peer.add(new JScrollPane(scene.graphScene.createView),BorderLayout.CENTER)
  
  scene.refresh
  CheckData.checkMole(scene.manager)
  
                
  def stopAndCloseExecutions = {
    executionMoleSceneContainers.foreach{emc=>
      emc.executionManager.cancel
      ScenesManager.tabPane.pages.remove(emc.page.index)
    }
    executionMoleSceneContainers.clear
  }
  
  def buildExecutionAction = new Action("") {
    override def apply = ScenesManager.addExecutionSceneContainer(buildContainer)
  }
  
  def cleanAndBuildExecutionAction = new Action("") {
    override def apply = {
      stopAndCloseExecutions
      ScenesManager.addExecutionSceneContainer(buildContainer)
    }  
  }                                     
}