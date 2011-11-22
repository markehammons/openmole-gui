/*
 * Copyright (C) 2011 leclaire
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

package org.openmole.ide.misc.widget.multirow

  
import javax.swing.ImageIcon
import org.openmole.ide.misc.image.ImageTool
import org.openmole.ide.misc.widget.ChooseFileTextField
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import org.openmole.ide.misc.widget.multirow.RowWidget.Plus
import org.openmole.ide.misc.widget.multirow.RowWidget._
import scala.swing.Button
import scala.swing.ComboBox
import scala.swing.Label
import scala.swing.Panel

object MultiTwoCombosChooseFileTextField
{
  class Factory[A, B] extends IRowWidgetFactory[TwoCombosChooseFileTextFieldRowWidget[A,B]]{
    def apply(row: TwoCombosChooseFileTextFieldRowWidget[A,B], panel: Panel) = {
      import row._
      new TwoCombosChooseFileTextFieldRowWidget(name,
                                                comboContentA,
                                                selectedA,
                                                inBetweenString1,
                                                comboContentB,
                                                selectedB,
                                                inBetweenString2,
                                                filePath,
                                                plus)
      
    }
  }
  
  class TwoCombosChooseFileTextFieldRowWidget[A,B](override val name: String,
                                                   val comboContentA: List[A], 
                                                   val selectedA: A,
                                                   val inBetweenString1: String, 
                                                   val comboContentB: List[B], 
                                                   val selectedB: B,
                                                   val inBetweenString2: String,
                                                   val filePath: String,
                                                   val plus: Plus) extends IRowWidget3[A,B,String]{
    
    val combo1 = new ComboBox[A](comboContentA) { selection.item = selectedA }
    val combo2 = new ComboBox[B](comboContentB) { selection.item = selectedB }
    val chooseFileText = new ChooseFileTextField(filePath)
    val refreshButton = new Button{icon = new ImageIcon(ImageTool.loadImage("img/refresh.png",10,10))}
    
    override val panel = new RowPanel(name,
                                      List(combo1,new Label(inBetweenString1),
                                           combo2,new Label(inBetweenString2),
                                           chooseFileText,
                                           refreshButton),
                                      plus)
    
    override def content: (A,B,String) = (combo1.selection.item,combo2.selection.item,chooseFileText.text)
    
  }
}

import MultiTwoCombosChooseFileTextField._
class MultiTwoCombosChooseFileTextField[A,B](rWidgets: List[TwoCombosChooseFileTextFieldRowWidget[A,B]], 
                                             factory: IRowWidgetFactory[TwoCombosChooseFileTextFieldRowWidget[A,B]],
                                             minus: Minus= NO_EMPTY,
                                             plus: Plus= ADD) 
extends MultiWidget(rWidgets,factory,5,minus){ 
  def this(
    rowName: String,
    initValues: (List[A],List[B]), 
    selected: List[(A,B)],
    inbetweenString1: String,
    inbetweenString2: String,
    filePath: String,
    factory: IRowWidgetFactory[TwoCombosChooseFileTextFieldRowWidget[A,B]],
    minus: Minus,
    plus: Plus) = this (if (selected.isEmpty) { List(new TwoCombosChooseFileTextFieldRowWidget(rowName,
                                                                                               initValues._1,
                                                                                               initValues._1(0),
                                                                                               inbetweenString1,
                                                                                               initValues._2, 
                                                                                               initValues._2(0),
                                                                                               inbetweenString2,
                                                                                               filePath,
                                                                                               plus))}
                        else
                          selected.map{case(s1,s2)=> new TwoCombosChooseFileTextFieldRowWidget(rowName,
                                                                                               initValues._1, 
                                                                                               s1,
                                                                                               inbetweenString1,
                                                                                               initValues._2,
                                                                                               s2,
                                                                                               inbetweenString2,
                                                                                               filePath,
                                                                                               plus)}, 
                        factory,minus,plus)

  def this(rName: String , 
           iValues: (List[A],List[B]), 
           selected: List[(A,B)],
           ibString: String,
           ibString2: String,
           fp: String) = this(rName,
                              iValues,
                              selected, 
                              ibString,
                              ibString2,
                              fp,
                              new Factory[A,B],
                              NO_EMPTY,
                              ADD)
  
  def this(rName: String , 
           iValues: (List[A],List[B]), 
           selected: List[(A,B)],
           ibString: String,
           ibString2: String,
           fp: String,
           minus: Minus, 
           plus: Plus) = this(rName,
                              iValues,
                              selected, 
                              ibString,
                              ibString2,
                              fp,
                              new Factory[A,B],
                              minus,
                              plus)
  
  def content = rowWidgets.map(_.content).toList 
}