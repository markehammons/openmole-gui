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

package org.openmole.ide.core.implementation.workflow

import java.awt.Point
import org.openmole.ide.core.model.workflow._

class InputSlotWidget(scene: IMoleScene,val capsule: ICapsuleUI, val index: Int,var startingSlot: Boolean) extends SlotWidget(scene.graphScene) with IInputSlotWidget{
  
  setStartingSlot(startingSlot)
  setPreferredLocation(new Point(2, 24 + index * 20))

  def widget = this
  
  def setStartingSlot(b: Boolean) = {
    startingSlot = b
    b match {
      case true=>
        scene match {
          case x:ExecutionMoleScene=> setImage(Images.IMAGE_START_EXE_SLOT)
          case _=> setImage(Images.IMAGE_START_SLOT)
        }
      case false=> scene match {
          case x:ExecutionMoleScene=> setImage(Images.IMAGE_INPUT_EXE_SLOT) 
          case _=> setImage(Images.IMAGE_INPUT_SLOT)
        }
    }
  }
}