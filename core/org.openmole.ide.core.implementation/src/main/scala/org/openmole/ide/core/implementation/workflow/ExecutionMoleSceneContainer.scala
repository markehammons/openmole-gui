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
import java.awt.Color
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants._
import org.openide.DialogDescriptor
import org.openide.DialogDisplayer
import org.openmole.core.model.mole._
import org.openmole.core.implementation.mole.MoleExecution
import org.openmole.ide.core.implementation.execution.MultiGenericGroupingStrategyPanel
import org.openmole.ide.core.implementation.execution.ExecutionManager
import org.openmole.ide.core.implementation.execution.MoleFinishedEvent
import org.openmole.ide.core.implementation.serializer.MoleMaker
import org.openmole.ide.core.model.dataproxy.ITaskDataProxyUI
import org.openmole.ide.core.model.panel.IHookPanelUI
import org.openmole.ide.core.model.workflow.ICapsuleUI
import org.openmole.ide.core.model.workflow.ISceneContainer
import org.openmole.ide.misc.widget._
import scala.collection.mutable.HashMap
import scala.swing._
import org.openmole.ide.misc.tools.image.Images._
import java.io.File
import scala.swing.FileChooser.SelectionMode._
import org.openmole.ide.core.implementation.dialog.DialogFactory
import org.openmole.ide.core.implementation.serializer.GUISerializer
import scala.swing.FileChooser.Result._
import org.openmole.core.serializer.SerializerService
import org.openmole.ide.core.implementation.dialog.StatusBar

class ExecutionMoleSceneContainer(val scene: ExecutionMoleScene,
                                  val page: TabbedPane.Page,
                                  bmsc: BuildMoleSceneContainer) extends Panel with ISceneContainer {
  peer.setLayout(new BorderLayout)

  val executionManager =
    MoleMaker.buildMole(bmsc.scene.manager) match {
      case Right((mole, prototypeMapping, capsuleMapping, errors)) ⇒
        Some(new ExecutionManager(bmsc.scene.manager,
          mole,
          prototypeMapping,
          capsuleMapping))
      case Left(l) ⇒
    }

  val startStopButton = new Button(start) {
    background = new Color(125, 160, 0)
  }

  val exportButton = new Button(export) {
    background = new Color(55, 170, 200)
  }

  var groupingPanel: Option[MultiGenericGroupingStrategyPanel] = None

  var panelHooks = new HashMap[IHookPanelUI, (ICapsuleUI, Class[_ <: Hook])]
  executionManager match {
    case Some(eManager: ExecutionManager) ⇒

      //peer.add(
      val centerPanel = new ScrollPane(new ExecutionPanel {
        peer.setLayout(new BorderLayout)
        peer.add(new PluginPanel("wrap") {
          val hookTabbedPane = new TabbedPane

          //Hooks
          contents += new Label { text = "<html><b><font \"size=\"5\" >Hooks</font></b></html>" }
          contents += hookTabbedPane
          eManager.capsuleMapping.keys.foreach { c ⇒
            c.dataUI.task match {
              case Some(t: ITaskDataProxyUI) ⇒
                val activated = c.dataUI.hooks.filter { _._2.activated }
                if (!activated.isEmpty) {
                  hookTabbedPane.pages += new TabbedPane.Page("<html><b>" + t.dataUI.name + "</b></html>",
                    new PluginPanel("", "", "[top]") {
                      activated.foreach {
                        case (hClass, hDataUI) ⇒
                          val p = hDataUI.buildPanelUI(t)
                          panelHooks += p -> (c, hClass)
                          contents += p.peer
                          contents += new Separator(Orientation.Vertical) { foreground = Color.WHITE }
                      }
                    })
                }
              case _ ⇒
            }
          }
          contents += new TitleLabel("Grouping strategy")
          groupingPanel = Some(new MultiGenericGroupingStrategyPanel(eManager))
          contents += groupingPanel.get.panel
        }.peer, BorderLayout.CENTER)

        peer.add(new PluginPanel("wrap") {
          contents += new TitleLabel("Execution control")
          contents += new PluginPanel("wrap 2", "[]-20[]") {
            //Start / Stop
            contents += startStopButton
            contents += exportButton

            // View Mole execution
            contents += new MainLinkLabel("Mole execution", new Action("") {
              def apply =
                DialogDisplayer.getDefault.notify(new DialogDescriptor(new JScrollPane(scene.graphScene.createView) {
                  setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
                }, "Mole execution"))
            })
          }
        }.peer, BorderLayout.SOUTH)
      })

      peer.add(new SplitPane(Orientation.Horizontal) {
        leftComponent = centerPanel
        rightComponent = eManager
        resizeWeight = 1
      }.peer)
    case None ⇒
  }

  def start: Action = new Action("Start") {
    override def apply = executionManager match {
      case Some(x: ExecutionManager) ⇒
        listenTo(x)
        reactions += {
          case MoleFinishedEvent ⇒
            startLook
        }
        startStopButton.background = new Color(170, 0, 0)
        startStopButton.action = stop
        exportButton.enabled = false
        x.start(panelHooks.map { ph ⇒ ph._1 -> ph._2._1 }.toMap,
          groupingPanel.get.coreObjects)
      case _ ⇒
    }
  }

  def startLook = {
    startStopButton.background = new Color(125, 160, 0)
    startStopButton.action = start
    exportButton.enabled = true
  }

  def stop: Action = new Action("Stop") {
    override def apply = executionManager match {
      case Some(x: ExecutionManager) ⇒
        startStopButton.background = new Color(125, 160, 0)
        startStopButton.action = start
        x.cancel
      case _ ⇒
    }
  }

  def export = new Action("Export") {
    override def apply = {
      val fc = DialogFactory.fileChooser(" Export a Mole execution",
        "*.xml",
        "xml")
      if (fc.showDialog(new Label, "OK") == Approve) {
        val text = fc.selectedFile.getPath
        if (new File(text).getParentFile.isDirectory) Some(text.split('.')(0) + ".xml")
        else None
      } match {
        case Some(t: String) ⇒ executionManager match {
          case Some(x: ExecutionManager) ⇒ x.buildMoleExecution(panelHooks.map { ph ⇒ ph._1 -> ph._2._1 }.toMap,
            groupingPanel.get.coreObjects) match {
              case Right((mExecution, environments)) ⇒ SerializerService.serialize(mExecution, new File(t))
              case Left(e) ⇒ StatusBar.blockException(e)
            }
          case _ ⇒
        }
        case _ ⇒
      }
    }
  }

  def moleExecution = executionManager match {
    case Some(x: ExecutionManager) ⇒ x.moleExecution
    case _ ⇒ None
  }

  def started = moleExecution match {
    case Some(me: MoleExecution) ⇒ me.started
    case _ ⇒ false
  }

  def finished = moleExecution match {
    case Some(me: MoleExecution) ⇒ me.finished
    case _ ⇒ false
  }

  class ExecutionPanel extends Panel {
    background = new Color(77, 77, 77)
  }
}