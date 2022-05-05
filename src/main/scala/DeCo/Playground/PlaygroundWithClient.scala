package DeCo.Playground

import org.ergoplatform.appkit.{NetworkType, RestApiErgoClient}

object PlaygroundWithClient extends App {
  val client = RestApiErgoClient.create(
    "http://213.239.193.208:9053/",
    NetworkType.MAINNET,
    "",
    RestApiErgoClient.getDefaultExplorerUrl(NetworkType.MAINNET))

  val boxId = ""

  val pinLockScript = s"""
    sigmaProp(INPUTS(0).R4[Coll[Byte]].get == blake2b256(OUTPUTS(0).R4[Coll[Byte]].get))
  """.stripMargin

  val pinLockCode = ""

  client.execute(ctx => {
    val inputBox1 = ctx.getBoxesById(boxId).head
    println(inputBox1)
  })
}
