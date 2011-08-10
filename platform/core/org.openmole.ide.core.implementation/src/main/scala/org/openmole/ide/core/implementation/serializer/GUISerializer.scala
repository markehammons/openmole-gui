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

package org.openmole.ide.core.implementation.serializer

import com.thoughtworks.xstream.XStream
import java.io.EOFException
import com.thoughtworks.xstream.io.xml.DomDriver
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import org.openmole.ide.core.model.workflow.IMoleScene
import org.openmole.ide.misc.exception.GUIUserBadDataError
import org.openmole.ide.core.implementation.control.MoleScenesManager
import org.openmole.ide.core.model.dataproxy._
import org.openmole.ide.core.implementation.dataproxy._
import org.openmole.ide.core.implementation.control.TabManager
import org.openmole.ide.core.implementation.data._
import org.openide.windows.WindowManager
import org.openmole.ide.core.implementation.MoleSceneTopComponent
import org.openmole.ide.core.implementation.palette.PaletteSupport
import org.openmole.ide.core.implementation.workflow.MoleScene

object GUISerializer {
    
  val xstream = new XStream(new DomDriver)
  xstream.registerConverter(new MoleSceneConverter)
  
  xstream.alias("molescene", classOf[IMoleScene])
//  xstream.alias("entity", classOf[EntityUI])
  xstream.alias("data_proxy", classOf[IDataProxyUI])
  
  def serialize(toFile: String) = {
    val writer = new FileWriter(new File(toFile))
    
    //root node
    val out = xstream.createObjectOutputStream(writer, "openmole")

    out.writeObject(new SerializedProxys(Proxys.task,
                                         Proxys.prototype,
                                         Proxys.sampling,
                                         Proxys.environment))
    //molescenes
    MoleScenesManager.moleScenes.keys.foreach(out.writeObject(_))
    
    out.close
  }
  
  def unserialize(fromFile: String) = {
    val reader = new FileReader(new File(fromFile))
    val in = xstream.createObjectInputStream(reader)
   
    Proxys.clearAll
    MoleScenesManager.removeMoleScenes
    
    try {
      while(true) {
        val readObject = in.readObject
        readObject match{
          case x: SerializedProxys=> x.loadProxys
          case x: MoleScene=> {TabManager.displayBuildMoleScene(MoleScenesManager.addBuildMoleScene(x))}
          case _=> throw new GUIUserBadDataError("Failed to unserialize object " + readObject.toString)
        }
      }
    } catch {
      case eof: EOFException => println("Ugly stop condition of Xstream reader !")
    } finally {
      PaletteSupport.refreshPalette
      in.close
    }
  }
}