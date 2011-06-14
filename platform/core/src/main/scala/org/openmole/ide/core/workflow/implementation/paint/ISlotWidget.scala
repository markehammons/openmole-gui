/*
 * Copyright (C) 2011 Mathieu leclaire <mathieu.leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.workflow.implementation.paint

import java.awt.Point
import org.netbeans.api.visual.widget.Scene
import org.openmole.ide.core.workflow.implementation.CapsuleUI

class ISlotWidget(scene: Scene,val capsule: CapsuleUI, val index: Int,val startingSlot: Boolean) extends SlotWidget(scene){
  if (startingSlot) setImage(Images.IMAGE_START_SLOT)
  else setImage(Images.IMAGE_INPUT_SLOT)     
  setPreferredLocation(new Point(-12, 14 + index * 20))
}