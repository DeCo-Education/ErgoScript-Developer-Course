# Week 3: AppKit

# Recall the Alcohol Sale Proxy Contract from Week 1

We will use this proxy contract and Ergo AppKit to write the off-chain code for the transaction.

```scala
{
	// ====== Alcohol Sale Proxy Contract Example ====== // 

	// Hard-coded constants expected at compile time are written in UpperCamelCase.
	
	// INPUTS:
	// license          = INPUTS(0)
	// buyerProxyInputs = INPUTS - INPUTS(0)
	//
	// OUTPUTS:
	// storeBox              = OUTPUTS(0)
	// provincialSalesTaxBox = OUTPUTS(1)
	// federalSalesTaxBox    = OUTPUTS(2)
	// buyerWalletBox        = OUTPUTS(3)
	// minerFeeBox           = OUTPUTS(4)
	//
	// (*) Note: 
	//           1. Mining fee box is always the last box in the set of OUTPUTS of a transaction,
	//              I am just showing this for clarity, but it will not be accessed in this contract.
  //           2. If there is any that change remains in the proxy, 
	//							it is sent back to the buyer wallet.

	// Contract variables
  val buyerPK: SigmaProp          = PK(buyerPKString)
	val buyerProxyInputs: Coll[Box] = INPUTS.filter({ (input: Box) => input.propositionBytes == SELF.propositionBytes })
	val buyerAmount: Long           = buyerProxyInputs.fold(0L)({ (input: Box, acc: Long) => acc + input.value })
	val provincialSalesTax: Long    = (AlcoholSaleAmount * ProvincialSalesTaxNum) / ProvincialSalesTaxDenom
	val federalSalesTax: Long       = (AlcoholSaleAmount * FederalSalesTaxNum) / FederalSalesTaxDenom
	val totalCost: Long             = AlcoholSaleAmount + provincialSalesTax + federalSalesTax + MinerFee
	
	// Variables associated with the buyer's license
	val license = INPUTS(0)
	val id      = license.R4[Coll[Byte]].get
	val name    = license.R5[Coll[Byte]].get
	val bDay    = license.R6[Coll[Byte]].get
	val address = license.R7[Coll[Byte]].get
	val expDate = license.R8[Coll[Byte]].get

	// Context variables needed for the proxy contract, assuming they are provided correctly
	val licenseTemplateContractBytes = getVar[Coll[Byte]](0).get

	// Substitute the constants of the license template contract bytes
	// and create the new contract bytes for the buyer's license
	val newLicenseContractBytes = {
		
		// New positions
		val newPositions_SigmaProp: Coll[Int] = Coll(0)
		val newPositions_Coll_Byte: Coll[Int] = Coll(1, 2, 3, 4, 5)
	
		// New constants
		val newConstants_SigmaProp: Coll[SigmaProp] = Coll(buyerPK)
		val newConstants_Coll_Byte: Coll[Byte] = Coll(id, name, bDay, address, expDate)

		// New contract bytes with substituted buyer PK
		val newContractBytes_SigmaProp = substConstants(licenseTemplateContractBytes, newPositions_SigmaProp, newConstants_SigmaProp)
		
		// New contract bytes with substituted buyer license information
		val newContractBytes_Coll_Byte = substConstants(newContractBytes_SigmaProp, newPositions_Coll_Byte, newConstants_Coll_Byte)
		val newContractBytes = newContractBytes_Coll_Byte
		
		newContractBytes
	}

	// Check for a valid sale
	val validSale = {

		// Check for a valid license 
		val validLicense = {
			allOf(Coll(
				BuyerLicenseContractBytes == newLicenseContractBytes,
				license.propositionBytes == newLicenseContractBytes
			))
		}

		// Check for a valid proxy amount
    val validProxyAmount = {
	    buyerAmount >= totalAmount
		}

		// Check for a valid store
		val validStore = {
			val storeBox = OUTPUTS(0)
			storeBox.propBytes == StoreBoxPropositionBytes
		}

		// Check for valid sales taxes
		val validSalesTaxes = {
			
			// Check for a valid provincial tax
			val validProvincialSalesTax = {
				val provincialSalesTaxBox = OUTPUTS(1)
				allOf(Coll(
					(provincialSalesTaxBox.propositionBytes == ProvincialSalesTaxPK),
					(provincialSalesTaxBox.value >= provincialSalesTax)
				))
			}

			// Check for a valid federal tax
			val validFederalSalesTax = {
				val federalSalesTaxBox = OUTPUTS(2)
				allOf(Coll(
					(federalSalesTaxBox.propositionBytes == FederalSalesTaxPK),
					(federalSalesTaxBox.value >= federalSalesTax)
				))
			}
      
      // Demand that both sales taxes are valid
      allOf(Coll(
        validProvincialSalesTax,
        validFederalSalesTax
      ))

		}

		// Check for a valid buyer wallet to return any change
		val validBuyerWallet = {
			if (buyerAmount > totalCost) {
				val buyerWalletBox = OUTPUTS(3)
				buyerWalletBox.propositionBytes == buyerPK.propBytes
			} else {
				true
			}
		}		
	
		// Demand that all the conditions are valid
		allOf(Coll(
			validLicense,
			validProxyAmount,
			validStore,
			validSalesTaxes,
			validBuyerWallet
		))

	}

	// Check for a valid refund
	val validRefund = {
		val refundWalletBox = OUTPUTS(0)
		allOf(Coll(
			(refundWalletBox.propositionBytes == buyerPK.propBytes),
			(refundWalletBox.value >= buyerAmount - MinerFee)
		))
	}

	// Obtain the appropriate sigma proposition
	sigmaProp(anyOf(Coll(
		validSale,
		validRefund
	)))
	
}
```

# Assumptions

1. We know the PK and mnemonic of the buyer
2. We know the proxy address
3. We know the license address
4. We know the PK of the store
5. We know the provincial and federal government PKs

**Real-world solutions to the assumptions:**

1. Using ErgoPay you could create an unsigned transaction with a QR code that requests as input the user’s PK and outputs a proxy box, guarded at the above contract, and with the funds needed for the sale.
2. Again using ErgoPay, another unsigned transaction could be created to request the user for the proxy address and the pay-2-script address of the license box. Internally, you would search for both and verify the information within the license box.
3. Alternatively, you could avoid using a proxy contract with ErgoPay and just request the user for their PK and P2S address of the license box, and place the proxy contract logic directly within your ErgoPay backend.
4. With a digitial identity solution, instead of the P2S address, it could be some sort of ZK-Proof that the application uses to ensure the validity of the user’s identity. As wallets enable dApp integration in the future, requiring payment with some sort of identity verification may become possible.
5. The store and government PKs would in reality be a P2S address proxy contract such that conditions could be placed in order to enforce who was able to spend the box. For example, one could imagine using a threshold signature scheme in these contracts.

# Steps

1. Create the ErgoClient
2. Create the BlockchainContext
3. Retrieve the transaction Input Boxes
    1. Retrieve the license box
    2. Retrieve the proxy boxes and calculate the sale amount
4. Calculate the amount for the two sales taxes
5. Create the Context Variables and Extended Inputs
6. Create the Transaction Builder
7. Create the transaction Output Boxes
8. Create the Prover
9. Create the Unsigned Transaction
10. Sign the transaction and retrieve the transaction id

# Alcohol Sale Transaction AppKit Code

```scala
// ====== Alcohol Sale TransactioAppKit Example ====== //

// Constants that we assume to know beforehand are written in UpperCamelCase.

// Imports
import org.ergoplatform.appkit._
import special.collection.Coll
import java.{util => ju}
import scala.collection.JavaConverters._

// Step 1: Create the ErgoClient instance
val ergoClient: ErgoClient = RestApiErgoClient.create(nodeApiUrl, networkType, nodeApiKey, explorerURL)

// Step 2: Create the BlockchainContext instance
val alcoholSalteTxId: String = ergoClient.execute( (ctx: BlockchainContext) => {

	// Convert the base58 address strings to an Address type
	val buyerPKAddress: Address             = Address.create(BuyerPKAddressString)
	val saleProxyContractAddress: Address   = Address.create(SaleProxyContractAddressString)
	val licenseAddress: Address             = Address.create(LicenseAddressString)
	val storePKAddres: Addres               = Address.create(StorePKAddress)
	val provincialSalesTaxPKAddres: Address = Address.create(ProvincialSalesTaxPKString)
	val federalSalesTaxPKAddress: Address   = Address.create(FederalSalesTaxPKString)	

	// Step 3: Retrieve the license box
	val licenseBox: InputBox = ctx.getUnspentBoxesFor(licenseAddress, 0, 1)(0)
	
	// Step 4: Retrieve the proxy boxes - max of 5 at a time, could be set to more
	val proxyBoxes: List[InputBox] = ctx.getUnspentBoxesFor(saleProxyContractAddress, 0, 5).asScala.toList
	val buyerAmount: Long = proxyBoxes.foldLeft(OL)((acc: Long, proxyBox: Long) => acc + proxyBox.getValue())

	// Step 5: Calculate the amount for the two sales taxes
	val provincialSalesTaxAmount: Long = ProvincialSalesTaxNum * AlocholSaleAmount / ProvincialSaesTaxDenom
	val federalSalesTaxAmount: Long = FederalSalesTaxNum * AlcoholSaleAmount / FederalSalesTaxDenom
	
	// Step 6: Create the Context Variables and Extended Inputs
	val licenseErgoTreeBytes: ErgoValue[Coll[Byte]] = ErgoValue.of(licenseBox.getErgoTree().bytes)
	val cVar0_licensePropBytes: ContextVar = ContextVar.of(0.toByte, licenseErgoTreeBytes)
	val extendedProxyInputBoxes: List[InputBox] = proxyBoxes.map(proxyBox => proxyBox.withContextVars(cVar0_licensePropBytes))
	val extendedInputs: ju.List[InputBox] = seqAsJavaList(extendedProxyInputBoxes)

	// Step 7: Create the tx builder
	val txBuilder: UnsignedTransactionBuilder = ctx.newTxBuilder()
	
	// Step 8: Create the tx output boxes
	val storeBox: OutBox = txBuilder.outBoxBuilder()
		.value(AlcoholSaleAmount)
		.contract(ctx.newContract(storePKAddress.getErgoAddress().script
		.build();
	
	val provincialSalesTaxBox: OutBox = txBuilder.outBoxBuilder()
		.value(provincialSalesTaxAmount)
		.contract(ctx.newContract(provincialSalesTaxPKAddress.getErgoAddress().script)
		.build();

	val federalSalesTaxBox: OutBox = txBuilder.outBoxBuilder()
		.value(federalSalesTaxAmount)
		.contract(ctx.newContract(federalSalesTaxPKAddress.getErgoAddress))
		.build();

	// Step 9: Create the prover
	val prover: ErgoProver = ctx.newProverBuilder()
		.withMnemonic(
			SecretString.create(BuyerMnemonic),
			SecretString.empty()
		)
		.build();
	
	// Step 10: Create the unsigned transaction
	val unsignedAlcoholSaleTx: UnsignedTransaction = txBuilder.boxesToSpend(extendedInputs)
		.outputs(storeBox, provincialSalesTaxBox, federalSalesTaxBox)
		.fee(MinerFee)
		.sendChangeTo(buyerPKAddress.getErgoAddress)
		.build();
	
	// Step 11: Sign the transaction and retrieve the tx id
	val signedAlcoholSaleTx: SignedTransaction = prover.sign(unsignedAlcoholSaleTx)
	val alcoholSaleTxId: String = ctx.sendTransaction(signedAlcoholSaleTx)
	alcoholSaleTxId

})
```

# Refund Transaction

The refund transaction follows the similar steps from the sale transaction, except a refund box is created as an output box, with the buyer’s PK as the contract script. 

A change address must also be provided, but this will just be the buyer’s PK as well.