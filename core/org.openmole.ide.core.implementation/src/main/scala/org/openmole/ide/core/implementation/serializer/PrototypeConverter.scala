/*
 * Copyright (C) 2012 mathieu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.implementation.serializer

import org.openmole.ide.core.implementation.serializer.SerializerState._
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import com.thoughtworks.xstream.mapper.Mapper
import org.openmole.ide.misc.tools.util.ClassLoader
import org.openmole.ide.core.implementation.registry._
import org.openmole.ide.core.implementation.prototype.GenericPrototypeDataUI
import org.openmole.ide.core.implementation.dataproxy.PrototypeDataProxyUI
import org.openmole.ide.core.implementation.dataproxy.Proxys
import org.openmole.ide.core.implementation.panel.ConceptMenu
import org.openmole.ide.core.model.dataproxy.IPrototypeDataProxyUI
import scala.collection.mutable.HashSet

class PrototypeConverter(mapper: Mapper,
                         provider: ReflectionProvider,
                         val serializer: GUISerializer,
                         var state: SerializerState) extends ReflectionConverter(mapper, provider) {

  override def marshal(o: Object,
                       writer: HierarchicalStreamWriter,
                       mc: MarshallingContext) = {
    val proto = o.asInstanceOf[IPrototypeDataProxyUI]
    state.content.get(proto) match {
      case Some(Serializing(id)) ⇒
      case Some(Serialized(id)) ⇒
        writer.addAttribute("id", id.toString)
      case None ⇒
        state.content += proto -> new Serialized(proto.id)
        writer.addAttribute("id", proto.id.toString)
        writer.addAttribute("name", proto.dataUI.name)
        writer.addAttribute("type", proto.dataUI.typeClassString)
        writer.addAttribute("dim", proto.dataUI.dim.toString)
    }
  }

  override def unmarshal(reader: HierarchicalStreamReader,
                         uc: UnmarshallingContext) = {

    addPrototype(new PrototypeDataProxyUI(GenericPrototypeDataUI(reader.getAttribute("name"),
      reader.getAttribute("dim").toInt)(ClassLoader.toManifest(reader.getAttribute("type"))),
      false, reader.getAttribute("id").toInt))
  }

  override def canConvert(t: Class[_]) = t.isAssignableFrom(classOf[PrototypeDataProxyUI])

  def addPrototype(p: IPrototypeDataProxyUI): IPrototypeDataProxyUI = {
    val key = KeyPrototypeGenerator(p)
    if (!KeyRegistry.protoProxyKeyMap.contains(key)) {
      Proxys.prototypes += p
      ConceptMenu.prototypeMenu.popup.contents += ConceptMenu.addItem(p)
      if (!(GenericPrototypeDataUI.baseType ::: GenericPrototypeDataUI.extraType contains p.dataUI.typeClassString)) {
        GenericPrototypeDataUI.extraType = GenericPrototypeDataUI.extraType :+ p.dataUI.typeClassString
      }
    }
    KeyRegistry.protoProxyKeyMap(key)
  }
}