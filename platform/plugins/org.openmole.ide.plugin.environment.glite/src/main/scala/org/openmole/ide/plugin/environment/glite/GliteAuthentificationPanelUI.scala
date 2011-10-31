/*
 * Copyright (C) 2011 <mathieu.leclaire at openmole.org>
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

package org.openmole.ide.plugin.environment.glite

import java.awt.Color
import org.openmole.ide.core.model.panel.IAuthentificationPanelUI
import org.openmole.ide.misc.widget.ChooseFileTextField
import org.openmole.ide.misc.widget.MigPanel
import org.openmole.misc.workspace.Workspace
import org.openmole.plugin.environment.glite.authentication.GliteAuthenticationMethod
import org.openmole.plugin.environment.glite.authentication.GlobusProxyFile
import org.openmole.plugin.environment.glite.authentication.P12Certificate
import org.openmole.plugin.environment.glite.authentication.PEMCertificate
import scala.swing.ButtonGroup
import scala.swing.FileChooser.SelectionMode._
import scala.swing.Label
import scala.swing.PasswordField
import scala.swing.RadioButton
import scala.swing.Separator
import scala.swing.event.ButtonClicked
import org.openmole.misc.workspace.Workspace
import scala.swing.event.Key._

object GliteAuthentificationPanelUI {
  val RED = new Color(212,0,0)
  val GREEN = new Color(136,170,0)
}

class GliteAuthentificationPanelUI extends MigPanel("","[left][right]","") with IAuthentificationPanelUI{
  var passString = ""
  var initButton: Option[RadioButton] = None 
  val pemButton = new RadioButton("pem")
  val p12Button = new RadioButton("p12")
  val proxyButton = new RadioButton("proxy")
  val groupButton = new ButtonGroup{
    buttons+= pemButton
    buttons+= p12Button
    buttons+= proxyButton}
  
  val pem1TextField= new ChooseFileTextField("Certification path", "Select afile", Some("pem files"), FilesOnly,Some("pem"))
  val pem2TextField= new ChooseFileTextField("Key Path", "Select a file", Some("pem files"), FilesOnly,Some("pem"))
  val p12TextField= new ChooseFileTextField("Certification path", "Select a file", Some("p12 file"), FilesOnly,Some("p12"))
  val proxyTextField = new ChooseFileTextField("nada", "Select a file", Some("proxy file"), FilesOnly,Some("proxy"))
  
  contents+= new MigPanel("wrap","","[]15[]15[]"){
    contents+= pemButton
    contents+= p12Button
    contents+= proxyButton}
  contents+= new Separator
  
  val pemPanel = buildPemPanel
  val p12Panel = buildP12Panel
  val proxyPanel = buildProxyPanel
  
  val pem = (pemButton,pemPanel,{pem1TextField.text="";pem2TextField.text=""})
  val p12 = (p12Button,p12Panel,{p12TextField.text=""})
  val proxy = (proxyButton,proxyPanel,{proxyTextField.text=""})
  
  Workspace.persistentList(classOf[GliteAuthenticationMethod]).headOption match {
    case Some((i:Int,x:P12Certificate))=> 
      initButton = Some(p12Button)
      passString = Workspace.decrypt(x.cypheredPassword)
      p12TextField.text = x.p12CertPath
      addP12
    case Some((i:Int,x:PEMCertificate))=> 
      initButton = Some(pemButton)
      pem1TextField.text = x.certPath
      pem2TextField.text = x.keyPath
      passString = Workspace.decrypt(x.cypheredPassword)
      addPem
    case Some((i:Int,x: GlobusProxyFile))=> 
      initButton = Some(proxyButton)
      proxyTextField.text = x.proxyFile
      addProxy
    case _=> 
      initButton = Some(p12Button)
      addP12
  }
  
  var pemPassField= new PasswordField(passString,20)
  pemPanel.contents += pemPassField
  var p12PassField= new PasswordField(passString,20)
  p12Panel.contents+= p12PassField
  
  listenTo(`pemButton`,`p12Button`,`proxyButton`)
  reactions += {
    case ButtonClicked(`pemButton`) => addPem
    case ButtonClicked(`p12Button`) =>  addP12
    case ButtonClicked(`proxyButton`) => addProxy
  }

  groupButton.select(initButton.get)
 
  
  def addPem = {clean; contents+= pem._2; refresh}
  def addP12 = {clean; contents+= p12._2; refresh}
  def addProxy = {clean; contents+= proxy._2; refresh}
  
  def clean = if(contents.size == 3) contents.remove(2)
  
  def refresh = {repaint
                 revalidate}
  
  override def saveContent = {
    if (pemButton.selected) Workspace.persistentList(classOf[GliteAuthenticationMethod])(0)= new PEMCertificate(Workspace.encrypt(new String(pemPassField.password)),
                                                                                                                pem1TextField.text,
                                                                                                                pem2TextField.text)
    else if(p12Button.selected) Workspace.persistentList(classOf[GliteAuthenticationMethod])(0)= new P12Certificate(Workspace.encrypt(new String(p12PassField.password)),
                                                                                                                    p12TextField.text)
    else if(proxyButton.selected) {
      Workspace.persistentList(classOf[GliteAuthenticationMethod])(0)= new GlobusProxyFile(proxyTextField.text)
    }
    
    (pem :: p12 :: proxy :: Nil).filterNot(_._1.selected).foreach(_._3)
  }
  
  def buildPemPanel =
    new MigPanel("fillx,wrap 2","[left][grow,fill]","") {
      contents+= (new Label("Certification"),"gap para")
      contents+= pem1TextField
      contents+= (new Label("Key"),"gap para")
      contents+= pem2TextField
      contents+= (new Label("Password"),"gap para")}
      
  
  def buildP12Panel = 
    new MigPanel("fillx,wrap 2","[left][grow,fill]","") {
      contents+= (new Label("Certification"),"gap para")
      contents+= p12TextField
      contents+= (new Label("password"),"gap para")}
  
  def buildProxyPanel = 
    new MigPanel("fillx,wrap 2","[left][grow,fill]","") {
      contents+= (new Label("Proxy"),"gap para")
      contents+= proxyTextField}
}