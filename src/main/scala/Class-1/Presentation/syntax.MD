
  #Basic Syntax In ErgoScript
  ```scala
  val bool: Boolean = true
  ```
  Whats going on here?

  `val` - A keyword used to create a basic, immutable, value of any type

  `bool` - The name used for the value created

  `: Boolean` - Specifying the type of the value, this is not always necessary, but does make code more understandable
  
   `= true` - We are setting our Boolean value bool equal to true

  ErgoScript is strongly typed, you should always know what types you are dealing with

  ####Learn to think in true and false statements, booleans (More specifically, Sigma Propositions) are the core of every ErgoScript contract

  ##More ErgoScript Syntax Examples:
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
  ErgoScript is based off of scala, which brings means we have some standard functional programming methods and syntax
```scala
  val myMap: Coll[(Int, Long)] = {      // Wrap this val statement into a function that returns a collection of integers that map to longs
    val intCollection = Coll(0, 1, 2)
    intCollection.map{                  // Use the map function, a standard FP method that iterates through the entire
      (myInt: Int) =>                   // collection and inputs each element through a function to return a collection of outputs.
        (myInt, myInt.toLong)           // We represent our mapping function using a Lambda expression, we define the
                                        // parameter to be the element of our collection (an Int), then use the
                                        // arrow operator (=>) in order to show how our parameter maps to an output.
    }                                   // We do not need to specify the return value with a keyword
  }
```
  ###Def vs Val?

```scala

  def computeAsDef(myInt: Int): Int = {
    myInt + 1
  }

  val computeAsVal: Int = {
    (myInt: Int) =>
      myInt + 1
  }
  ```

  The above statements **do the exact same thing**. The difference is that `val` statements are calculated when the script
  initializes itself to be run. `def` statements are instead calculated whenever the call is made.
  In most instances, you will likely use val statements

  ##Sigma Propositions
  Sigma Propositions are the core of every single ErgoScript contract
  SigmaProps represent some conditions about the transaction that must be met in order to spend a certain box.
  They are quite similar to booleans, in that they may be reduced into two values, `true` or `false`.
  SigmaProps enable the usage of Zero-Knowledge Proofs, an important part of modern day cryptography and one of the
  defining features of Ergo in terms of its privacy
    
**All contracts in ErgoScript return a Sigma Proposition at the very end**. This SigmaProp represents the conditions needed
to spend the box protected by your contract. For this reason, all the code you make within an ErgoScript
contract should affect the outcome of your Sigma Proposition in some way.

  SigmaProps come in a few different forms, but there are two main ways you will see them in ErgoScript contracts.

###SigmaProps From Booleans
You may create SigmaProps from booleans using the `sigmaProp` function. This
allows you to define arbitrary spending conditions for any contract.
```scala
{
  val mathIsHard: Boolean = (1 + 1) != 2
  sigmaProp(mathIsHard) // SigmaProp created from a boolean using the sigmaProp function
                        // What would this contract evaluate to?
}
```
###SigmaProps From Public Keys
Public Keys (Essentially, the part of your address that makes it different from everyone else's)
are also SigmaProps. When a public key is passed as a SigmaProp, your contract checks whether or
not the given PK is the one that signed the transaction
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

Now you've seen the basics, for the end of class, lets look at a simple ErgoScript contract, the pin-lock
we mentioned earlier

###Pinlock Contract
```scala
{
  sigmaProp( INPUTS(0).R4[Coll[Byte]].get == blake2b256(OUTPUTS(0).R4[Coll[Byte]].get) )
}
```
Don't worry if you don't understand the functions used here, these are global functions that we will
get into in the next section.
What's happening here is this:

We may spend Input 0 of this transaction if and only if there exists an output whose R4 (register 4)
contains the hash of the collection of bytes found in R4 of the Input.
This contract refers to itself as INPUTS(0),
for a more clear example, look at the following, where the box being spent refers to itself within its own contract:

###Pinlock Contract (with SELF)
```scala
{
  sigmaProp( SELF.R4[Coll[Byte]].get == blake2b256(OUTPUTS(0).R4[Coll[Byte]].get) )
}
```

Are these two contracts equivalent?
That is, are there any spending conditions that exist in which one contract could evaluate to true, and one could evaluate to false?