/*
 * Copyright (C) 2011 <mathieu.Mathieu Leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.model.data

import org.openmole.ide.core.model.commons.Constants._
import org.openmole.core.model.sampling._
import org.openmole.ide.core.model.panel.ISamplingPanelUI
import org.openmole.ide.core.model.sampling._

trait ISamplingDataUI extends IDataUI with ISamplingCompositionElementDataUI {

  def id: String

  def name: String = "Sampling"

  def coreObject(factors: List[IFactorDataUI],
                 samplings: List[Sampling]): Sampling

  def buildPanelUI: ISamplingPanelUI

  def imagePath: String

  def fatImagePath: String

  def isAcceptable(factor: IFactorDataUI): Boolean

  def isAcceptable(sampling: ISamplingDataUI): Boolean

  def preview: String
}
