package DeCo.Playground

import org.ergoplatform.appkit.impl.ErgoTreeContract
import org.ergoplatform.appkit.{Address, ConstantsBuilder, ErgoType, ErgoValue, NetworkType, Parameters, RestApiErgoClient}
import scorex.crypto.hash.Blake2b256

object PlaygroundWithChainedTransactions extends App{
  val client = RestApiErgoClient.create(
    "http://213.239.193.208:9053/",
    NetworkType.MAINNET,
    "",
    RestApiErgoClient.getDefaultExplorerUrl(NetworkType.MAINNET))

  val dummyTxId = "1234"
  val dummyAddress: Address = Address.create("4MQyML64GnzMxZgm")

  val boxId = ""

  val pinLockScript = s"""
    sigmaProp(INPUTS(0).R4[Coll[Byte]].get == blake2b256(OUTPUTS(0).R4[Coll[Byte]].get))
  """.stripMargin


  val pinLockCode = "abcd"

  client.execute(ctx => {
    val pinLockContract = ctx.compileContract(ConstantsBuilder.create()
      .build(), pinLockScript)

    val txB = ctx.newTxBuilder()

    val pinLockToByte: Array[Byte] = Blake2b256.hash(pinLockCode)

    val pinlockInputBox = txB.outBoxBuilder()
      .value(Parameters.MinFee * 10)
      .contract(pinLockContract)
      .registers(
        ErgoValue.of(pinLockToByte, ErgoType.byteType())
      )
      .build()
      .convertToInputWith(dummyTxId, 0)
  })
}
