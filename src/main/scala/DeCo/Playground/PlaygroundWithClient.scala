package DeCo.Playground

import org.ergoplatform.appkit.{NetworkType, RestApiErgoClient}

object PlaygroundWithClient extends App {
  val client = RestApiErgoClient.create(
    "http://213.239.193.208:9053/",
    NetworkType.MAINNET,
    "",
    RestApiErgoClient.getDefaultExplorerUrl(NetworkType.MAINNET))

  val boxId = ""
  client.execute(ctx => {
    val inputBox1 = ctx.getBoxesById(boxId).head
    println(inputBox1)
  })
}
