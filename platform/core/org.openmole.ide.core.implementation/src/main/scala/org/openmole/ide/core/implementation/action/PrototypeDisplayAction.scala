/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.core.implementation.action

import org.openmole.ide.core.implementation.dataproxy.PrototypeDataProxyFactory
import org.openmole.ide.core.implementation.display.Displays
import org.openmole.ide.core.implementation.display.PrototypeDisplay
import scala.swing.Action

class PrototypeDisplayAction(dpf:PrototypeDataProxyFactory,tytype : String) extends Action(dpf.factory.displayName){
  override def apply = {
    Displays.currentType = tytype
    PrototypeDisplay.dataProxy = Some(dpf.buildDataProxyUI(Displays.name))
    Displays.propertyPanel.initNewEntity
  }
}