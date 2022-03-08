package DeCo.Playground

import DeCo.Playground.Models.{AddressBalance, FullBalance, Output, TransactionData}
import org.ergoplatform.appkit.{Address, ErgoId, NetworkType, RestApiErgoClient}
import org.ergoplatform.explorer.client.{DefaultApi, ExplorerApiClient}
import org.ergoplatform.explorer.client.model.{Balance, Items, ItemsA, OutputInfo, TotalBalance, TransactionInfo}
import retrofit2.Response
import sigmastate.Values.ErgoTree

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

class ExplorerHandler(networkType: NetworkType) {
  private val url: String = RestApiErgoClient.getDefaultExplorerUrl(networkType)
  private val apiClient = new ExplorerApiClient(url)
  private val apiService: DefaultApi = apiClient.createService(classOf[DefaultApi])

  private def asOption[T](resp: Response[T]): Option[T] = {
    if (resp.isSuccessful)
      Some(resp.body())
    else
      None
  }

  private def itemSeq[T](opt: Option[Items[T]]) = {
    if (opt.isDefined)
      Some(opt.get.getItems.asScala.toSeq)
    else
      None
  }

  private def outputSeq(opt: Option[ItemsA]) = {
    if (opt.isDefined)
      Some(opt.get.getItems.asScala.toSeq)
    else
      None
  }

  def getTransaction(id: ErgoId): Option[TransactionData] = {
    TransactionData.fromOption(asOption[TransactionInfo](apiService.getApiV1TransactionsP1(id.toString).execute()))
  }

  def txsForAddress(addr: Address, offset: Int = 0, limit: Int = 10): Option[Seq[TransactionData]] = {
    val opt = asOption[Items[TransactionInfo]](apiService.getApiV1AddressesP1Transactions(addr.toString, offset, limit).execute())
    TransactionData.fromOptionSeq(itemSeq[TransactionInfo](opt))
  }

  def boxesByTemplateHash(hash: String, offset: Int = 0, limit: Int = 10): Option[Seq[Output]] = {
    Output.fromOptionSeq(
      outputSeq(asOption[ItemsA](apiService.getApiV1BoxesByergotreetemplatehashP1(hash, offset, limit).execute()))
    )
  }

  def boxesByErgoTree(ergoTree: ErgoTree, offset: Int = 0, limit: Int = 10): Option[Seq[Output]] = {
    Output.fromOptionSeq(
      outputSeq(asOption[ItemsA](apiService.getApiV1BoxesByergotreetemplatehashP1(ergoTree.bytesHex, offset, limit).execute()))
    )
  }
  def boxesByErgoTreeHex(ergoTreeHex: String, offset: Int = 0, limit: Int = 10): Option[Seq[Output]] = {
    Output.fromOptionSeq(
      outputSeq(asOption[ItemsA](apiService.getApiV1BoxesByergotreetemplatehashP1(ergoTreeHex, offset, limit).execute()))
    )
  }

  def boxesByAddress(address: Address, offset: Int = 0, limit: Int = 10): Option[Seq[Output]] = {
    Output.fromOptionSeq(
      outputSeq(asOption[ItemsA](apiService.getApiV1BoxesByaddressP1(address.toString, offset, limit).execute()))
    )
  }

  def boxesByTokenId(tokenId: ErgoId, offset: Int = 0, limit: Int = 10): Option[Seq[Output]] = {
    Output.fromOptionSeq(
      outputSeq(asOption[ItemsA](apiService.getApiV1BoxesUnspentBytokenidP1(tokenId.toString, offset, limit).execute()))
    )
  }

  def getTotalBalance(address: Address): Option[FullBalance] = {
    FullBalance.fromOption(asOption[TotalBalance](apiService.getApiV1AddressesP1BalanceTotal(address.toString).execute()))
  }

  def getConfirmedBalance(address: Address, minConfirmations: Int = 20): Option[AddressBalance] = {
    AddressBalance.fromOption(asOption[Balance](apiService.getApiV1AddressesP1BalanceConfirmed(address.toString, minConfirmations).execute()))
  }





}