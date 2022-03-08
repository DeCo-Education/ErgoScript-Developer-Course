package DeCo.Playground

import org.ergoplatform.appkit.{ErgoId, ErgoType, Parameters}
import sigmastate.eval.CostingSigmaDslBuilder.Colls

object Helpers {
  def ergToNanoErg(erg: Double): Long = (BigDecimal(erg) * Parameters.OneErg).longValue()
  def nanoErgToErg(nanoErg: Long): Double = (BigDecimal(nanoErg) / Parameters.OneErg).doubleValue()

  def toId(hex: String): ErgoId = ErgoId.create(hex)

  def trunc(str: String): String = {
    str.take(6)+"..."+str.takeRight(6)
  }

}
