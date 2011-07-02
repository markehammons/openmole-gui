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

package org.openmole.ide.core.action

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import org.openmole.ide.core.workflow.CapsuleUI
import org.openmole.ide.core.workflow.MoleScene

class RemoveCapsuleAction(scene: MoleScene,capsule: CapsuleUI) extends ActionListener{

  override def actionPerformed(ae: ActionEvent)= {
    val id = scene.manager.capsuleID(capsule)
    scene.manager.removeCapsuleUI(id)
    scene.removeNodeWithEdges(id)
  }
}
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import org.openmole.ide.core.workflow.CapsuleUI;
//import org.openmole.ide.core.workflow.MoleScene;
//
///**
// *
// * @author Mathieu Leclaire <mathieu.leclaire@openmole.fr>
// */
//public class RemoveCapsuleAction implements ActionListener {
//
//    CapsuleUI capsule;
//    MoleScene scene;
//
//    public RemoveCapsuleAction(MoleScene scene,
//            CapsuleUI capsule) {
//        this.capsule = capsule;
//        this.scene = scene;
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent ae) {
//        String id = scene.getManager().getCapsuleUIID(capsule);
//        scene.getManager().removeCapsuleUI(id);
//        scene.removeNodeWithEdges(id);
//    }
//}