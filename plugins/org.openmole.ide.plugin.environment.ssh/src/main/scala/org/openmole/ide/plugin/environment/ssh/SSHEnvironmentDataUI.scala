/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.plugin.environment.ssh

import org.openmole.plugin.environment.ssh.SSHEnvironment
import org.openmole.core.batch.environment.BatchEnvironment
import org.openmole.ide.core.model.data.IEnvironmentDataUI

class SSHEnvironmentDataUI(val name: String = "",
                           val login: String = "",
                           val host: String = "",
                           val nbSlots: Int = 1,
                           val dir: String = "/tmp/",
                           val runtimeMemory: Int = BatchEnvironment.defaultRuntimeMemory) extends IEnvironmentDataUI {

  def coreObject = new SSHEnvironment(login, host, nbSlots, runtimeMemory, path = dir)

  def coreClass = classOf[SSHEnvironment]

  def imagePath = "img/ssh.png"

  override def fatImagePath = "img/ssh_fat.png"

  def buildPanelUI = new SSHEnvironmentPanelUI(this)
}
