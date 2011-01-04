/*
 *  Copyright (C) 2010 mathieu
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ui.ide.workflow.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openmole.core.model.mole.IMole;
import org.openmole.ui.ide.control.MoleScenesManager;
import org.openmole.ui.ide.exception.MoleExceptionManagement;
import org.openmole.ui.ide.serializer.MoleMaker;
import org.openmole.ui.ide.serializer.GUISerializer;

/**
 *
 * @author mathieu
 */
public class OpenXMLAction  implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            IMole mole = (IMole) GUISerializer.getInstance().unserialize("/tmp/mole.xml");
         //   MoleScene moleS = new MoleScene();
           // moleS.build(mole);
            MoleScenesManager.getInstance().addTab(MoleMaker.processToMoleScene(mole));
        } catch (Throwable ex) {
            MoleExceptionManagement.showException(ex);
        }
    }
    
}