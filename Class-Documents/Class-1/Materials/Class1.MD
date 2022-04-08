# Week 1: Syntax

# CLASS 1: Syntax

# Basic Syntax In ErgoScript

```scala
val bool: Boolean = true

```

What’s going on here?

`val` - A keyword used to create a basic, immutable, value of any type

`bool` - The name used for the value created

`: Boolean` - Specifying the type of the value, this is not always necessary, but does make code more understandable

`= true` - We are setting our Boolean value bool equal to true

ErgoScript is strongly typed, you should always know what types you are dealing with

### Learn to think in true and false statements, booleans (More specifically, Sigma Propositions) are the core of every ErgoScript contract

## More ErgoScript Syntax Examples:

```scala
  if(bool == true){
    val x = 0
    val y = 1
    val z = ((x * y) + 5) - (3 / 2)
  }else{
    val x = 2L
    val y: Coll[Long]  = Coll(0L, 1L, x) // You can build collections of elements
    val z: (Long, Long) = (3, 4)
    val a: (Long, Coll[Long]) = (x, y) // Build complex types by layering together pairs and colls
    val b: Coll[((Long, Long), Boolean)] = Coll(((2L, 4L), true), ((7L, 2L), false))
  }

```

ErgoScript is based off of Scala, which brings means we have some standard functional programming methods and syntax

```scala
  val myMap: Coll[(Int, Long)] = {      // Wrap this val statement into a function that returns a collection of integers paired with longs
    val intCollection = Coll(0, 1, 2)
    intCollection.map{                  // Use the map function, a standard FP method that iterates through the entire
      (myInt: Int) =>                   // collection and inputs each element through a function to return a collection of outputs.
        (myInt, myInt.toLong)           // We represent our mapping function using a Lambda expression, we define the
                                        // parameter to be the element of our collection (an Int), then use the
                                        // arrow operator (=>) in order to show how our parameter maps to an output.
    }                                   // We do not need to specify the return value with a keyword
  }

```

### Def vs Val?

```scala

  def computeAsDef(myInt: Int): Int = {
    myInt + 1
  }

  val computeAsVal: Int = {
    (myInt: Int) =>
      myInt + 1
  }

```

The above statements **do the exact same thing**. The difference is that `val` statements are calculated when the script initializes itself to be run. `def` statements are instead calculated whenever the call is made.
In most instances, you will likely use `val` statements

## Sigma Propositions

Sigma Propositions are the core of every single ErgoScript contract
SigmaProps represent some conditions about the transaction that must be met in order to spend a certain box.
They are quite similar to booleans, in that they may be reduced into two values, `true` or `false`.
SigmaProps enable the usage of Zero-Knowledge Proofs, an important part of modern-day cryptography and one of the defining features of Ergo in terms of its privacy

**All contracts in ErgoScript return a Sigma Proposition at the very end**. This SigmaProp represents the conditions needed
to spend the box protected by your contract. For this reason, all the code you make within an ErgoScript contract should affect the outcome of your Sigma Proposition in some way.

SigmaProps come in a few different forms, but there are two main ways you will see them in ErgoScript contracts.

### SigmaProps From Booleans

You may create SigmaProps from booleans using the `sigmaProp` function. This
allows you to define arbitrary spending conditions for any contract.

```scala
{
  val mathIsHard: Boolean = (1 + 1) != 2
  sigmaProp(mathIsHard) // SigmaProp created from a boolean using the sigmaProp function
                        // What would this contract evaluate to?
}

```

### SigmaProps From Public Keys

Public Keys (Essentially, the part of your address that makes it different from everyone else's)
are also SigmaProps. When a public key is passed as a SigmaProp, your contract checks whether or not the given PK is the one that signed the transaction
You may think of signing a transaction, as quite literally signing it with your signature to prove
that the transaction was authorized by you.

```scala
{
  // You can use the PK function to
  // hardcode an address's public key into your contract
  val myPK: SigmaProp = PK("9etXmP7D3ZkWssDopWcWkCPpjn22RVuEyXoFSbVPWAvvzDbcDXE")

  myPK
}

```

### SigmaProp Operations

Much like booleans, you may use logical operations on SigmaProps in order to build more complex spending logic for your contract

```scala
{
  val enoughERG = INPUTS(0).value > 1000000
  val myPK = PK("9etXmP7D3ZkWssDopWcWkCPpjn22RVuEyXoFSbVPWAvvzDbcDXE")

  sigmaProp(enoughERG) || myPK // What does this contract do? Under what conditions could such a contract be spent?
}

```

You can see in the above contract that usage of `||` actually creates two spending paths for the given contract.

Now you've seen the basics, for the end of this section, lets look at a simple ErgoScript contract, the pin-lock we mentioned earlier

### Pin-lock Contract

```scala
{
  sigmaProp( INPUTS(0).R4[Coll[Byte]].get == blake2b256(OUTPUTS(0).R4[Coll[Byte]].get) )
}

```

Don't worry if you don't understand the functions used here, these are global functions that we will get into the next section.
What's happening here is this:

We may spend Input 0 of this transaction if and only if there exists an output whose `R4` (register 4)
contains the hash of the collection of bytes found in R4 of the Input.
This contract refers to itself as `INPUTS(0)`, for a more clear example, look at the following, where the box being spent refers to itself within its own contract:

### Pin-lock Contract (with SELF)

```scala
{
  sigmaProp( SELF.R4[Coll[Byte]].get == blake2b256(OUTPUTS(0).R4[Coll[Byte]].get) )
}

```

Are these two contracts equivalent?
That is, are there any spending conditions that exist in which one contract could evaluate to true, and one could evaluate to false?

# CLASS 1: Technical Knowledge

# The Blockchain Context

The Blockchain Context represents data taken from the transaction and the state of the blockchain. The data for the blockchain context is stored in the global object `CONTEXT`. `CONTEXT`, therefore, contains the main entities that you will interact with to manipulate your contract’s spending conditions.

### HEIGHT

An integer representing the height of the current block is to be validated by miners. The number can be accessed using `HEIGHT`.

### SELF

`SELF` represents the current eUTxO input box which holds the ErgoScript contract. The SELF box is of type `Box`, which contains information relevant to the specified eUTXO such as:

the value in nanoERGs; the box id; the proposition bytes of the guarding script for this eUTXO, the tokens stored in the box; and the box registers.

### INPUTS

The eUTxOs are used as input boxes to be spent in the transaction. The boxes are stored in a collection and can be accessed using the object called `INPUTS`. The `SELF` box is part of this input box collection.

### Data Inputs

Data inputs are input eUTXOs that are not spent in the transaction. They serve as ‘read-only’ boxes, usually holding information necessary for the spending conditions of the contract. Data inputs can be accessed using `CONTEXT.dataInputs`.

### OUTPUTS

The output box eUTXOs will be created from the transaction. The collection can be accessed using the `OUTPUTS` object.

### Headers

Block headers (Parts of each block that hold information representing the block) are available through the `CONTEXT.headers` function. The `headers` function returns a collection holding the last 10 block headers present before the current `HEIGHT`. Using this function allows your contract to gain insight into the most recent blocks that occurred before the transaction that your contract is being executed in. Each `Header` object in the collection returned by `headers` holds a variety of different information, much of this information pertains to the block miner, such as the miner’s PK, the nonce used to find the block, and the votes the miner submitted when the block was mined.

### Pre-Headers

The `CONTEXT.preHeader` function gives your contract access to the `PreHeader`. The `PreHeader` object represents all of the information available to miners who are working to find the next block. Because each block miner must insert their own information to properly mine a block, the data contained within each `PreHeader` object is completely different depending on who mines the block. This means that your contract could execute differently depending on who ends up mining the block that spends your contract’s box.

## Example

```scala
{
	// Don't worry about the fields accessed in our CONTEXT data, we will go more in depth
	// in the next section. Instead focus on how we are accessing this data and then using
	// using it in our contract.
	val selfAtZero = SELF.id == INPUTS(0).id
	val boxAmountToAdd: Long = CONTEXT.dataInputs(0).R4[Long].get // Get's a long from R4
	val amountAddedInOutputs = OUTPUTS(0).value == SELF.value + boxAmountToAdd
	val heightIsValid: Boolean = HEIGHT > 700000
	val randomValueIsEven: Boolean = CONTEXT.headers(0).powNonce(0) % 2 == 0

	sigmaProp(amountAddedInOutputs && selfAtZero) 
			|| sigmaProp(heightIsValid && randomValueIsEven)
}
```

# Boxes and Registers

Boxes are the main entity used to hold ERG according to the eUTXO model. Registers are pieces of data that can be attached to any box. They allow your contract to store data and incorporate it into transactions at a later point. There are 4 mandatory registers present within each box in the Ergo blockchain, these registers describe information that is necessary for a box to exist and be valid. Besides the mandatory registers, there are 5 additional registers that may store arbitrary data. This data could be obtained both off-chain and on-chain and allows you to incorporate more data when creating the spending conditions for your contract.

### R0

Contains the monetary value of the box in nanoERGs. This register is accessed using `Box.value`, where `Box` could represent either `SELF` or one of the boxes in the `INPUTS` or `OUTPUTS` collection.

### R1

Contains the collection of proposition bytes of the guarding ErgoScript contract for the box. It is accessed using `Box.propositionBytes`.

### R2

Contains a collection of the tokens stored in the box. A token contains is represented by two pieces of information: the unique token id; and the amount of the specified token. The collection is accessed using `Box.tokens`.

### R3

Contains information about the box’s creation such as: the transaction id from which the box was created as an output; the outputs index of the box (i.e. the index used in `OUTPUTS`); and the creation height of the block of the transaction from which the box came from. This collection is accessed using `Box.creationInfo`. The height referenced here is used as part of Ergo’s unique storage rent feature, where boxes may be spent after 4 years so that miners may take some fee and recycle ERG back into the blockchain.

### R4 - R9

Contains arbitrary data that is set whenever the box is first outputted from a transaction. This data may essentially be any type commonly found in ErgoScript, along with more complex types built from Pairs and Collections. Registers may also store more complex types such as `Box`, `SigmaProp`, `GroupElement`, and `AVLTree`.

### Other Box Functions

Outside of the registers themselves, each box has a specific identification hash that may be referenced using the `id` function. Box ids are formed by taking the `blake2b256` hash of the boxes content as a `Coll[Byte]` . You may directly reference the un-hashed collection of bytes representing a box using the `bytes` function. Keep in mind that each box’s content and id are cryptographically unique, meaning that no two boxes within the blockchain may have the same id or content bytes. This is achieved due to the inclusion of `creationInfo` within each box, as transaction ids and associated output indexes are all values that must be unique to a given UTXO.  You may use the `bytesWithoutRef` function to return a `Coll[Byte]` that does not contain such information.

## Example

```scala
{
	val valueMultiplier = SELF.R4[Int].get
	val tokenMultiplier = INPUTS(1).R4[Int].get

	if(SELF.id == INPUTS(0).id){
		val outputValue = OUTPUTS(0).value == SELF.value * valueMultiplier 
		val outputTokens = OUTPUTS(0).tokens(0)._2 == SELF.value * tokenMultiplier 
		sigmaProp(outputValue && outputTokens)
	}else{
		val outputGoesToCheese = {
			PK("9etXmP7D3ZkWssDopWcWkCPpjn22RVuEyXoFSbVPWAvvzDbcDXE").propBytes
				== OUTPUTS(0).propositionBytes
		}
		sigmaProp(outputGoesToCheese)
	}
}
```

# Global Functions

There are a variety of global functions available to use in ErgoScript. Here we will go through some of the most commonly used functions, (besides `sigmaProp`). A complete reference for all global functions and types may be found in the [ErgoScript LangSpec](https://github.com/ScorexFoundation/sigmastate-interpreter/blob/develop/docs/LangSpec.md) .

## Logical Functions

Logical functions describe global functions used on booleans. These functions of course include operators such as `&&` and `||`, but the following commonly used functions also exist.

### AllOf

The `allOf` function takes a collection booleans and returns a boolean indicating whether or not all of the given booleans within the collection evaluate to true. It is equivalent to inserting `&&` between each boolean in the collection.

### AnyOf

The `anyOf` function takes a collection of booleans and returns a boolean indicating whether or not at least one boolean within the entire collection evaluates to true. It is equivalent to inserting `||` between each boolean in the collection.

### XorOf

The `xorOf` function takes a collection of booleans and applies the XOR operation between each boolean within the collection. Therefore, the `xorOf` function returns true if an odd number of booleans within the collection evaluate to true. It returns false if an even number of booleans within the collection evaluate to true.

## Zero-Knowledge Functions

Zero Knowledge functions are functions that may be used to evaluate SigmaProps with Zero-Knowledge needed to verify the truth of the SigmaProps given.

### AtLeast

The `atLeast` function takes an integer `k` and a collection of SigmaProps and returns whether `k` SigmaProps within the collection evaluate to true. All SigmaProps are evaluated with zero knowledge.

### ZKProof

The `ZKProof` function takes a block of code and evaluates all operations within the code with zero-knowledge scoping. This may be useful if some operations within your contract must be kept private. The code block used must not use any boolean operations to ensure zero-knowledge, and the entire block must evaluate into a single root SigmaProp.

## Cryptographic Functions

Cryptographic functions are functions related to cryptography. Such functions include hashing, and calculation over `GroupElement` values.

### Blake2b256

`blake2b256` takes a `Coll[Byte]` and returns a new `Coll[Byte]` hashed according to the Blake2b256 algorithm. The Blake2b256 algorithm is the main hashing algorithm used within Ergo. It is also incorporated as a part of the Autolykos PoW algorithm used to mine Ergo.

### Sha256

`sha256` takes a `Coll[Byte]` and returns a new `Coll[Byte]` hashed according to the SHA256 hashing function.

### DecodePoint

`decodePoint` takes a `Coll[Byte]` representing a Group Element and converts it into the `GroupElement` type.

### ProveDHTuple

`proveDHTuple` takes four `GroupElement` values and constructs them into a public key represented by a SigmaProp according to the Diffie-Hellman signature protocol. This is useful for creating shared public keys in MultiSig and Ring Signature settings.

### ProveDLog

`proveDlog` takes a `GroupElement` and creates a public key represented by a SigmaProp.

## Compile-Time Functions

Compile-Time functions are not evaluated during the spending of the script, instead these functions are used when compiling an ErgoScript contract into the native ErgoTree language. These functions all take Strings (which is not an actual ErgoScript type) and then convert these strings into some ErgoScript type during contract compilation. These values are stored directly within the contract and cannot change once the contract has been compiled.

### FromBase

Two functions, `fromBase64` and `fromBase58` take Strings of their respective base and convert them into a `Coll[Byte]` at compile time.

### PK

The `PK` function takes an address string (which is actually just a base58 encoded `GroupElement` with a network identifier prefix) and converts it into a SigmaProp public key at compile time. This is actually done using many of the functions listed above.

### Deserialize

The `deserialize[T]` function takes a type parameter `T` along with a base58 encoded String of binary data. The String value is converted into some value with ErgoScript type `T` at compile time.

## Other Functions

### GetVar
`getVar[T]` takes type parameter `T` along with some integer `tag` and returns some Context Variable with the given type that is associated with the given `tag`. Context Variables are specific off-chain variables that may be attached to any box at the time of transaction creation. Context variables allow for robust changes for certain parameters within your contract. It is especially useful for providing generic contracts that rely on off-chain information which may change between different spending transactions.

### SubstConstants

The `substConstants` function has the following signature:

```scala
def substConstants[T](scriptBytes: Coll[Byte],
 positions: Coll[Int], newValues: Coll[T]): Coll[Byte]
```


It allows a contract to construct another contract’s propositional bytes using the given parameters. Because a contract’s address is created using its contents, inputting different constants within a contract can change it’s address(and therefore, the contract itself). This function allows for a contract of the same “template” to be created using a new set of constants. In order to be used properly, one must provide a sample of the contract’s propositional bytes(parameter `scriptBytes`), along with the `positions` at which certain constants of type `T` must be replaced with the values in the `newValues` parameter.

## Example: Alcohol Sale Proxy Contract

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
	//				it is sent back to the buyer wallet.

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