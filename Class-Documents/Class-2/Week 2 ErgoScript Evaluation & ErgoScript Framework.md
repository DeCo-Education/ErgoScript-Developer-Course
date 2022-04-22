# Week 2: ErgoScript Evaluation & ErgoScript Framework

# Auction House

1. Issue Artwork - [https://github.com/anon-real/ErgoAuctionHouse/blob/master/src/auction/issueArtworkAssm.js](https://github.com/anon-real/ErgoAuctionHouse/blob/master/src/auction/issueArtworkAssm.js)
2. New Auction - [https://github.com/anon-real/ErgoAuctionHouse/blob/master/src/auction/newAuctionAssm.js](https://github.com/anon-real/ErgoAuctionHouse/blob/master/src/auction/newAuctionAssm.js)
3. New Bid - [https://github.com/anon-real/ErgoAuctionHouse/blob/master/src/auction/newBidAssm.js](https://github.com/anon-real/ErgoAuctionHouse/blob/master/src/auction/newBidAssm.js)

# Box Focused Guard Script Framework

Guard scripts determines whether a box is spendable in a transaction. This design focuses on the component within a box and allow the components written down to guide the ErgoScript implementation of the Guard Script.

1. List out
    1. Value
    2. Tokens
    3. Registers
    4. Actions

## Framework + Example

**Value**: 0.1 Ergs

**Token List:**

1. Sample NFT Name
    1. Identification NFT - 1
    

**Registers**:

**R4** → Personal Details

1. DataType - Coll[Coll[Byte]]
2. Data List
    1. First Name
    2. Last Name
    3. Address

**R5** → Age, HOB and Grades

1. DataType - Coll[Long]
2. Data List
    1. Age
    2. HOB (Height of Birth)
    3. Math Score

**R6** → Joined DeCo

1. DataType - Boolean
    1. Took DeCo Layman Course

**R7** → Records

1. Last Recorded Height

**Spending Path:**

1. Grows Older Every Year
    
    INPUTS Position: 0
    
    OUTPUTS Position: 0
    
    Trigger Condition:
    
    1. When all other conditions fail (an else condition)
    
    Contract Conditions:
    
    1. Condition 1: Everything stays the same other than the age
    2. Condition 2: Can only increment by *n*,  where n = ((Current Height - HOB)/(Height in a year)) - Age
2. Math Score changed after tests
    
    INPUTS Position: 0
    
    OUTPUTS Position: 0
    
    Trigger Condition:
    
    1. Test Score Identification Token in INPUTS (1)
    
    Contract Conditions:
    
    1. Condition 1: Everything stays the same other than the Math Score
    2. Condition 2: Math score is from Test Score Box R4[Long]
3. Decided to take DeCo Layman Course
    
    INPUTS Position: 0
    
    OUTPUTS Position: 0
    
    Trigger Condition:
    
    1. If DeCo award NFT is in INPUTS(1)
    
    Contract Conditions:
    
    1. Condition 1: Math Score >= 98

**Box Conditions:**

Persisted data

1. Identification Token
2. Personal details

Modified data:

Other Conditions:

1. Records updated for every Tx

## Building the ErgoScript

After forming the framework. Take the actions and refer to its components to design the ErgoScript for the Guard Script.

**1st**, Fulfill the Box Conditions

```scala
{
	val persistIdentificationToken = allOf(Coll(
		// ._1 == Id, ._2 == quantity
		SELF._tokens(0)._1 == OUTPUTS(0)._tokens(0)._1,
		SELF._tokens(0)._2 == OUTPUTS(0)._tokens(0)._2
	))
	
	val persistPersonalDetails = 
		SELF.R4[Coll[Coll[Byte]] == OUTPUTS(0).R4[Coll[Coll[Byte]]

	val recordsUpdated = 
		OUTPUTS(0).R7 == HEIGHT

	val boxConditionsCheck = allOf(Coll(
		persistIdentificationToken,
		persistPersonalDetails,
		recordsUpdated))

	val ageNHOBCheck = allOf(Coll(
		SELF.R5[Coll[Long]].get(0) == OUTPUTS(0).R5[Coll[Long]].get(0),
		SELF.R5[Coll[Long]].get(1) == OUTPUTS(0).R5[Coll[Long]].get(1)
	))

	val mathScoreCheck = 
		SELF.R5[Coll[Long]].get(2) == OUTPUTS(0).R5[Coll[Long]].get(2)

	val deCoAwardCheck =
		SELF.R6[Boolean].get == OUTPUTS(0).R5[Boolean].get

	if (INPUTS(1)._tokens(0)._1 == deCoNFTAwardToken) {
		sigmaProp(allOf(Coll(
			boxConditionsCheck,
			ageNHOBCheck,
			SELF.R5[Coll[Long]].get(2) >= 98
		)))
	} else if (INPUTS(1)._tokens(0)._1 ==  testScoreIdentification) {
		sigmaProp(allOf(Coll(
			boxConditionCheck,
			ageNHOBCheck,
			OUTPUTS(0).R5[Coll[Long]].get(2) == INPUTS(1).R4[Long].get
		))
	} else {
		val maxAge = (HEIGHT - SELF.R5[Coll[Long]].get(1))/(heightInAYear)
		val ageNotExceedMaxAge = OUTPUTS(0).R5[Coll[Long]].get(0) <= maxAge

		sigmaProp(allOf(Coll(
			boxConditionsCheck,
			deCoAwardCheck,
			mathScoreCheck,
			ageNotExceedMaxAge
		))
	}
}
```

## Blank Guard Script Framework

**Value**: {insert} Ergs

**Token List:**

1. Sample NFT Name
    1. Identification NFT - 1
    

**Registers**:

**R4** → 

1. DataType - 
2. Data List
    1. 

**R5** → 

1. DataType - 
2. Data List
    1. 

**R6** → 

1. DataType - 
    1. 

**R7** → 

1. DataType -

**R8** →

1. DataType -

**R9** →

1. DataType -

**Spending Paths:**

1. {Path}
    
    *INPUTS Position*: 
    
    *OUTPUTS Position*: 
    
    *Trigger Condition*:
    
    1. 
    
    *Contract Conditions*:
    
    1. Condition 1: 
2. {Path}
    
    *INPUTS Position*: 
    
    *OUTPUTS Position*: 
    
    *Trigger Condition*:
    
    1. 
    
    *Contract Conditions*:
    
    1. Condition 1: 
3. {Path}
    
    *INPUTS Position*: 
    
    *OUTPUTS Position*: 
    
    *Trigger Condition*:
    
    1. 
    
    *Contract Conditions*:
    
    1. Condition 1:

**Box Conditions:**

*Persisted data*

1. 
2. 

*Modified data*

1. 

*Other Conditions*

1.