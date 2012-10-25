/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.plugin.method.sensitivity

import org.openmole.core.model.data._
import org.openmole.core.model.domain._
import org.openmole.core.model.sampling._
import org.openmole.core.model.task._
import org.openmole.ide.core.implementation.data.EmptyDataUIs
import org.openmole.ide.core.implementation.sampling._
import org.openmole.ide.core.model.data._
import org.openmole.ide.core.model.dataproxy._
import org.openmole.misc.exception.UserBadDataError
import org.openmole.plugin.method.sensitivity.SaltelliSampling
import org.openmole.ide.misc.tools.Counter

class SaltelliSamplingDataUI(val samples: String = "1",
                             val id: String = "sampling" + Counter.id.getAndIncrement) extends ISamplingDataUI {

  implicit def string2Int(s: String): Int = augmentString(s).toInt

  def coreObject(factors: List[IFactorDataUI],
                 samplings: List[Sampling]) =
    new SaltelliSampling(
      try samples
      catch {
        case e: NumberFormatException ⇒ throw new UserBadDataError("An integer is exepected as number of samples")
      },
      factors.flatMap { f ⇒
        f.prototype match {
          case Some(p: IPrototypeDataProxyUI) ⇒ f.domain match {
            case Some(d: IDomainDataUI) ⇒
              val proto = p.dataUI.coreObject.asInstanceOf[Prototype[Double]]
              List(Factor(proto,
                d.coreObject(proto).asInstanceOf[Domain[Double] with Bounds[Double]]))
            case _ ⇒ Nil
          }
          case _ ⇒ Nil
        }
      }.toSeq: _*)

  def coreClass = classOf[SaltelliSampling]

  def imagePath = "img/saltelliSampling.png"

  override def fatImagePath = "img/saltelliSampling_fat.png"

  def buildPanelUI = new SaltelliSamplingPanelUI(this)

  //FIXME 2.10
  def isAcceptable(factor: IFactorDataUI) = false

  def isAcceptable(sampling: ISamplingDataUI) = true

  def preview = "Saltelli with " + samples + "samples"
}
