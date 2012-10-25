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

package org.openmole.ide.core.model.data

import java.awt.Point
import org.openmole.ide.core.model.sampling.ISamplingWidget
import org.openmole.ide.core.model.panel.ISamplingCompositionPanelUI
import org.openmole.core.model.sampling._

trait ISamplingCompositionDataUI extends IDataUI {
  def name: String

  override def toString: String = name

  def coreObject: Sampling

  def imagePath: String

  def fatImagePath: String

  def factors: Iterable[(IFactorDataUI, Point)]

  def samplings: Iterable[(ISamplingDataUI, Point)]

  def finalSampling: Option[String]

  def connections: Iterable[(String, String)]

  def buildPanelUI: ISamplingCompositionPanelUI
}
