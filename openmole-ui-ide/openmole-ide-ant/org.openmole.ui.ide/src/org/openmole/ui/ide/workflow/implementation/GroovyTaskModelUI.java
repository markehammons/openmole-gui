/*
 *  Copyright (C) 2010 Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Affero GNU General Public License as published by
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

package org.openmole.ui.ide.workflow.implementation;

import org.openmole.core.model.task.IGenericTask;

/**
 *
 * @author Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 */
public class GroovyTaskModelUI <T extends IGenericTask> extends GenericTaskModelUI{

    public GroovyTaskModelUI(String name) {
        super(name);
    }

    @Override
    public void proceed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void eventOccured(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}