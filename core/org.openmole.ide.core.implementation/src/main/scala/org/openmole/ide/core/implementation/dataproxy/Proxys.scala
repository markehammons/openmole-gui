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

package org.openmole.ide.core.implementation.dataproxy

import java.util.concurrent.atomic.AtomicInteger
import org.openmole.ide.core.model.dataproxy._
import org.openmole.ide.core.model.factory._
import org.openmole.ide.core.implementation.panel.ConceptMenu
import org.openmole.ide.core.model.data._
import scala.collection.JavaConversions._
import scala.collection.mutable.HashSet
import java.io.File

object Proxys {
    
  val incr = new AtomicInteger
  
  var tasks = new HashSet[ITaskDataProxyUI]
  var prototypes = new HashSet[IPrototypeDataProxyUI]
  var samplings = new HashSet[ISamplingDataProxyUI]
  var environments = new HashSet[IEnvironmentDataProxyUI]
  
  def filePrototypes: List[IPrototypeDataProxyUI] = prototypes.filter(_.dataUI.coreObject.`type`.erasure == classOf[File])
  .toList
  
  def clearAll: Unit = {
    ConceptMenu.clearAllItems
    List(tasks,prototypes,environments,samplings).foreach{_.clear}
  }
} 
  