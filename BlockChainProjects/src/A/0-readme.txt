ProjectA:
This a basic blockchain. Each block has data (a string saying block and its number), uuid, the
previous block's hash, nonce, and its own hash. Class StringUtil uses SHA256 to create
the hash, and createHash method calls it. MineBlock method creates the block with
its hash. AddBlock is exactly what is sounds like, as well as isChainValid. In main, the actual
block chain is made using everything above and places it in an array. Some code was from
Prof Elliot's BlockInputG.java. A link provided by Prof Elliot was used heavily, and
the link is in the file. Some of these features are not required at this point, see comments
in the project for more.