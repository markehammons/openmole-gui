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

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.BeanInfo;
import java.io.IOException;
import javax.swing.Action;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.netbeans.spi.palette.PaletteFilter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;
import org.openmole.ui.ide.workflow.implementation.Preferences;


/**
 *
 * @author Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 */
public class PaletteSupport {
    private static PaletteController controller;
    
    public static PaletteController createPalette(){
        AbstractNode paletteRoot = new AbstractNode(new CategoryBuilder());
        paletteRoot.setName("Palette Root");
        Preferences.getInstance().clearProperties();
        Preferences.getInstance().clearModels();
        controller = PaletteFactory.createPalette(paletteRoot, new MyActions(), new MyPaletteFilter(), new MyDnDHandler());
        return controller;
    }

    private static class MyActions extends PaletteActions {

        @Override
        public Action[] getImportActions() {
            return null;
        }

        @Override
        public Action[] getCustomPaletteActions() {
            return null;
        }

        @Override
        public Action[] getCustomCategoryActions(Lookup lookup) {
            return null;
        }

        @Override
        public Action[] getCustomItemActions(Lookup lookup) {
            return null;
        }

        @Override
        public Action getPreferredAction(Lookup lookup) {
            return null;
        }
    }

    public static class MyPaletteFilter extends PaletteFilter {

        public static void refresh(){
            System.out.println("MyPaletteFilter.refresh() " +  controller);
            controller.refresh();
        }
        
        @Override
        public boolean isValidCategory(Lookup lkp) {
            System.out.println("MyPaletteFilter.isValidCategory() " + lkp);
            return true;
        }

        @Override
        public boolean isValidItem(Lookup lkp) {
            System.out.println("MyPaletteFilter.isValidItem() " + lkp);
            return true;
        }
        
    }
    
    private static class MyDnDHandler extends DragAndDropHandler {

        @Override
        public void customize(ExTransferable exTransferable, Lookup lookup) {
            Node node = lookup.lookup(Node.class);
            final Image image = (Image) node.getIcon(BeanInfo.ICON_COLOR_16x16);
           /* exTransferable.put(new ExTransferable.Single(DataFlavor.imageFlavor) {

                @Override
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    return image;
                }
            });*/
            exTransferable.put(new ExTransferable.Single(DataFlavor.imageFlavor) {

                @Override
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    return image;
                }
            });
        }
    }
}