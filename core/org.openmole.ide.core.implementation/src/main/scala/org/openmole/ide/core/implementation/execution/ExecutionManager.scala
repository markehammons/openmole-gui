/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.core.implementation.execution

import java.awt.Color
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.Timer
import org.openmole.core.batch.environment.BatchEnvironment
import org.openmole.core.implementation.execution.local._
import org.openmole.core.model.execution.Environment
import org.openmole.core.model.mole.ICapsule
import org.openmole.ide.misc.visualization._
import org.openmole.ide.misc.widget._
import org.openmole.core.model.mole._
import org.openmole.ide.core.implementation.dialog.StatusBar
import org.openmole.ide.core.implementation.serializer.MoleMaker
import org.openmole.ide.core.model.panel._
import org.openmole.ide.core.model.dataproxy.IPrototypeDataProxyUI
import org.openmole.ide.core.model.factory._
import org.openmole.ide.core.model.control.IExecutionManager
import org.openmole.ide.core.model.workflow.IMoleSceneManager
import scala.collection.mutable.HashMap
import scala.swing._
import org.openmole.misc.eventdispatcher.EventDispatcher
import org.openmole.misc.exception.UserBadDataError
import scala.collection.JavaConversions._
import org.openmole.core.model.job.State
import org.openmole.core.model.data._
import org.openmole.core.model.execution.ExecutionState
import org.openmole.ide.core.model.workflow.ICapsuleUI
import TextAreaOutputStream._

object ExecutionManager {
  implicit def executionStatesDecorator(s: scala.collection.mutable.Map[ExecutionState.ExecutionState, AtomicInteger]) = new {
    def states = new States(s(ExecutionState.READY).get, s(ExecutionState.SUBMITTED).get, s(ExecutionState.RUNNING).get)
  }
}

class ExecutionManager(manager: IMoleSceneManager,
                       val mole: IMole,
                       val capsuleMapping: Map[ICapsuleUI, ICapsule],
                       val prototypeMapping: Map[IPrototypeDataProxyUI, Prototype[_]]) extends PluginPanel("", "[grow,fill]", "")
    with IExecutionManager
    with Publisher { executionManager ⇒
  val logTextArea = new TextArea {
    columns = 20
    rows = 10
    editable = false
  }
  val executionJobExceptionTextArea = new TextArea { editable = false }

  val moleExecutionExceptionTextArea = new TextArea { editable = false }

  override val printStream = new PrintStream(new TextAreaOutputStream(logTextArea), true)
  var moleExecution: Option[IMoleExecution] = None
  var status = HashMap(State.READY -> new AtomicInteger,
    State.RUNNING -> new AtomicInteger,
    State.COMPLETED -> new AtomicInteger,
    State.FAILED -> new AtomicInteger,
    State.CANCELED -> new AtomicInteger)

  //var hooksInExecution = List.empty[IHook]
  val wfPiePlotter = new PiePlotter
  val envBarPlotter = new XYPlotter(5000, 120)

  val envBarPanel = new PluginPanel("", "[][grow,fill]", "[top]") {
    contents += new PluginPanel("wrap", "[center]", "") {
      contents += new TitleLabel("Workflow")
      peer.add(wfPiePlotter.panel)
    }
  }

  var states = new States(0, 0, 0)
  val timer = new Timer(5000, new ActionListener {
    def actionPerformed(e: ActionEvent) = {
      envBarPlotter.update(states)
    }
  })
  var environments = new HashMap[Environment, (String, HashMap[ExecutionState.ExecutionState, AtomicInteger])]

  val hookMenu = new Menu("Hooks")

  val splitPane = new SplitPane(Orientation.Vertical) {
    leftComponent = new PluginPanel("wrap", "[center]", "") {
      contents += envBarPanel
    }

    rightComponent = new ScrollPane(logTextArea)
    resizeWeight = 0.65
  }

  var downloads = (0, 0)
  var uploads = (0, 0)

  System.setOut(new PrintStream(logTextArea.toStream))
  System.setErr(new PrintStream(logTextArea.toStream))

  val tabbedPane = new TabbedPane {
    opaque = true
    background = new Color(77, 77, 77)
  }
  tabbedPane.pages += new TabbedPane.Page("Progress", splitPane)
  tabbedPane.pages += new TabbedPane.Page("Errors", new ScrollPane(executionJobExceptionTextArea))
  tabbedPane.pages += new TabbedPane.Page("Environments errors", new ScrollPane(moleExecutionExceptionTextArea))

  contents += tabbedPane

  def start(hooks: Map[IHookPanelUI, ICapsuleUI],
            groupings: List[(Grouping, ICapsule)]) = synchronized {
    tabbedPane.selection.index = 0
    cancel
    initBarPlotter

    moleExecution = buildMoleExecution(hooks, groupings) match {
      case Right((mExecution, environments)) ⇒
        EventDispatcher.listen(mExecution, new JobSatusListener(this), classOf[IMoleExecution.OneJobStatusChanged])
        EventDispatcher.listen(mExecution, new JobSatusListener(this), classOf[IMoleExecution.Finished])
        EventDispatcher.listen(mExecution, new JobCreatedListener(this), classOf[IMoleExecution.OneJobSubmitted])
        EventDispatcher.listen(mExecution, new ExecutionExceptionListener(this), classOf[IMoleExecution.ExceptionRaised])
        environments.foreach { e ⇒
          e._1 match {
            case be: BatchEnvironment ⇒
              EventDispatcher.listen(be, new UploadFileListener(this), classOf[BatchEnvironment.BeginUpload])
              EventDispatcher.listen(be, new UploadFileListener(this), classOf[BatchEnvironment.EndUpload])
              EventDispatcher.listen(be, new UploadFileListener(this), classOf[BatchEnvironment.BeginDownload])
              EventDispatcher.listen(be, new UploadFileListener(this), classOf[BatchEnvironment.EndDownload])
            case _ ⇒
          }
        }
        environments.foreach {
          case (env, _) ⇒ EventDispatcher.listen(env, new EnvironmentExceptionListener(this), classOf[Environment.ExceptionRaised])
        }

        environments.foreach(buildEmptyEnvPlotter)
        if (envBarPanel.peer.getComponentCount == 2) envBarPanel.peer.remove(1)

        //FIXME Displays several environments
        if (environments.size > 0) {
          envBarPanel.peer.add(new PluginPanel("wrap", "[center]", "") {
            contents += new TitleLabel("Environment: " + environments.toList(0)._2)
            contents += envBarPlotter.panel
          }.peer)
          // splitPane.dividerLocation = (preferredSize.width * 0.4).toInt
          splitPane.revalidate
          splitPane.repaint
        }
        initPieChart
        repaint
        revalidate
        timer.start
        mExecution.start
        println("MEXECUTION STARTED ... ")
        Some(mExecution)
      case Left(e) ⇒
        StatusBar.blockException(e)
        None
    }
  }

  def buildMoleExecution(hooks: Map[IHookPanelUI, ICapsuleUI],
                         groupings: List[(Grouping, ICapsule)]) = MoleMaker.buildMoleExecution(mole,
    manager,
    hooks.flatMap { case (panel, caps) ⇒ List(capsuleMapping(caps)).zip(panel.saveContent.coreObject(this)) }.toList,
    capsuleMapping,
    groupings)

  def incrementEnvironmentState(environment: Environment,
                                state: ExecutionState.ExecutionState) = synchronized {
    states = States.factory(states, state, environments(environment)._2(state).incrementAndGet)
  }

  def decrementEnvironmentState(environment: Environment,
                                state: ExecutionState.ExecutionState) = synchronized {
    states = States.factory(states, state, environments(environment)._2(state).decrementAndGet)
  }

  def cancel = synchronized {
    timer.stop
    moleExecution match {
      case Some(me: IMoleExecution) ⇒ me.cancel
      case _ ⇒
    }
  }

  def initBarPlotter = synchronized {
    environments.clear
    buildEmptyEnvPlotter((LocalEnvironment.asInstanceOf[Environment], "Local"))
  }

  def buildEmptyEnvPlotter(e: (Environment, String)) = {
    val m = HashMap(ExecutionState.SUBMITTED -> new AtomicInteger,
      ExecutionState.READY -> new AtomicInteger,
      ExecutionState.RUNNING -> new AtomicInteger,
      ExecutionState.DONE -> new AtomicInteger,
      ExecutionState.FAILED -> new AtomicInteger,
      ExecutionState.KILLED -> new AtomicInteger)
    environments += e._1 -> (e._2, m)

    moleExecution match {
      case Some(me: IMoleExecution) ⇒
        EventDispatcher.listen(e._1, new JobStateChangedOnEnvironmentListener(this, me, e._1), classOf[Environment.JobStateChanged])
      case _ ⇒
    }
  }

  def initPieChart = synchronized {
    status.keys.foreach(k ⇒ status(k) = new AtomicInteger)
    environments.values.foreach(env ⇒ env._2.keys.foreach(k ⇒ env._2(k) = new AtomicInteger))
  }

  def displayFileTransfer = {
    StatusBar.clear
    StatusBar.inform("Downloads : " + downloads._1 + " / " + downloads._2 + "  Uploads : " + uploads._1 + " / " + uploads._2)
  }
}
