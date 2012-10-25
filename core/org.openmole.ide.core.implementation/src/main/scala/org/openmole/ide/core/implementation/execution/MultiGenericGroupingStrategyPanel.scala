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

package org.openmole.ide.core.implementation.execution

import org.openmole.core.model.mole._
import org.openmole.ide.core.implementation.registry.KeyRegistry
import org.openmole.ide.core.model.control.IExecutionManager
import org.openmole.ide.misc.widget.PluginPanel
import org.openmole.ide.misc.widget.multirow.IData
import org.openmole.ide.misc.widget.multirow.IFactory
import org.openmole.ide.misc.widget.multirow.IPanel
import org.openmole.ide.misc.widget.multirow.MultiPanel
import scala.swing.MyComboBox
import scala.swing.event.SelectionChanged
import org.openmole.ide.misc.widget.multirow.RowWidget._
import org.openmole.ide.misc.widget.multirow.MultiWidget._

object MultiGenericGroupingStrategyPanel {

  class GroupingStrategyPanel(executionManager: IExecutionManager)
      extends PluginPanel("wrap 3") with IPanel[GroupingStrategyData] {

    val hookFactoryComboBox = new MyComboBox(KeyRegistry.groupingStrategies.values.toList)

    hookFactoryComboBox.selection.reactions += {
      case SelectionChanged(`hookFactoryComboBox`) ⇒
        panelUI = buildPanelUI
        if (contents.size > 2) contents.remove(2)
        contents += panelUI.peer
      case _ ⇒
    }

    val capsuleComboBox = new MyComboBox(executionManager.capsuleMapping.values.toList)

    var panelUI = buildPanelUI
    contents += hookFactoryComboBox
    contents += capsuleComboBox
    contents += panelUI.peer

    def content = new GroupingStrategyData(panelUI.coreObject,
      capsuleComboBox.selection.item)

    def buildPanelUI = hookFactoryComboBox.selection.item.buildPanelUI(executionManager)
  }

  class GroupingStrategyData(val coreObject: Grouping,
                             val capsule: ICapsule) extends IData

  class GroupingStrategyFactory(executionManager: IExecutionManager) extends IFactory[GroupingStrategyData] {
    def apply = new GroupingStrategyPanel(executionManager: IExecutionManager)
  }
}

import MultiGenericGroupingStrategyPanel._

class MultiGenericGroupingStrategyPanel(executionManager: IExecutionManager) extends MultiPanel("",
  new GroupingStrategyFactory(executionManager: IExecutionManager),
  List(),
  CLOSE_IF_EMPTY,
  ADD) {
  def coreObjects = content.map { c ⇒ c.coreObject -> c.capsule }
}