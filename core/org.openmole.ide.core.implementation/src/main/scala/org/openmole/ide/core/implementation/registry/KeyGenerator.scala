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

package org.openmole.ide.core.implementation.registry

import org.openmole.core.model.data.IPrototype
import org.openmole.misc.tools.obj.ClassUtils._


object KeyGenerator {
  def stripArrays(m : Manifest[_]) : Manifest[_] = {
    if (m.erasure.isArray) stripArrays(m.erasure.fromArray.toManifest)
    else m
  }
  
  def apply(proto : IPrototype[_]) : PrototypeKey = 
    new PrototypeKey(proto.getClass,stripArrays(proto.`type`))
}