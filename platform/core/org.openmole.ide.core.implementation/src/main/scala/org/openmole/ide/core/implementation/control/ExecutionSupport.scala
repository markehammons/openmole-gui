/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.core.implementation.control

import scala.swing.ScrollPane

object ExecutionSupport extends ScrollPane{
  def changeView(etp: ExecutionTabbedPane) = contents= etp
}