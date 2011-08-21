/*
 * Copyright (C) 2011 <mathieu.leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.misc.widget

import javax.swing.JPopupMenu
import scala.swing._

class PopupMenu extends Component with SequentialContainer.Wrapper {
  override lazy val peer: JPopupMenu = new JPopupMenu
  def show( owner: Component, x: Int, y: Int) { peer.show( owner.peer, x, y )}
  def add(item: MenuItem) = contents += item
}