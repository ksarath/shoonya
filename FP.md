## Functions
Properties of Functions:
1. Total
2. Deterministic
3. No side effect


1. Not total: throw an exception or return null
2. Non deterministic: random, clock / time
3. Side effect: readLine, logging


1. Functions        (only use functions, if it's not a function, don't use it)
2. No poly methods  (don't use methods from AnyRef / Java "Object" etc, don't use methods on polymorphic objects)
3. No null          (never use null)
4. No RTTI          (no Runtime Type information/identification, TypeTags etc...)


http://www.lihaoyi.com/post/StrategicScalaStylePracticalTypeSafety.html#scalazzi-scala

Benefits of pure functions
1. immutability
2. concurrency
3. no state


## Typeclasses
A set of 3 things :
1. Types
2. Operations on values of those types
3. Laws governing the operations

In scala : encoded using traits
1. Types are the Type parameters of the trait
2. Operations are the methods of the trait
3. Laws are... comments in the Scaladoc... (at best some Scalacheck testing...)


