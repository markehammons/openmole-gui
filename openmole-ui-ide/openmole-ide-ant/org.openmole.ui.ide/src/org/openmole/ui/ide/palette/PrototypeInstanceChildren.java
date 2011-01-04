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
package org.openmole.ui.ide.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.Node;
import org.openmole.ui.ide.commons.ApplicationCustomize;
import org.openmole.ui.ide.workflow.implementation.PrototypeUI;
import org.openmole.ui.ide.workflow.implementation.PrototypesUI;

/**
 *
 * @author Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 */
public class PrototypeInstanceChildren extends GenericChildren {

    @Override
    protected List<Node> initCollection() {
        Collection<PrototypeUI> prototypes = PrototypesUI.getInstance().getPrototypes();

        ArrayList childrenNodes = new ArrayList(prototypes.size());
        for (PrototypeUI proto : prototypes) {
            childrenNodes.add(new PrototypeInstanceNode(ApplicationCustomize.PROTOTYPE_DATA_INSTANCE_FLAVOR, proto.getName()));
        }
        return childrenNodes;
    }

    @Override
    public void refreshNodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}