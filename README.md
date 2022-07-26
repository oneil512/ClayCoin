# ClayCoin

## Update
I attempted to do this whole project without looking at any reference implementations or designs so I could learn as much as possible. as such, this code is a terribly designed complexity trap that is probably riddled with bugs and should never be run by anyone, ever. but it works, i guess.

Proof of Work blockchain written in Java.  Uses SHA256.  I did this project to learn more about programming blockchains and to hopefully get a job in the field.  Right now when you run main a node and wallet are generated for you, a miner starts mining coins to your address, and a test script sends a transaction from you to yourself with 0 coins every two seconds.  In order to get multiple nodes in the network, uncomment line 62 in nodeHandler, and change all the ports/addresses to broadcast to the other nodes in your network.

# TODO

More validation on the nodes end.

Set up the project so that it is easier for multiple nodes to exist in the network and new nodes to join.  To do this just keep a file with ip addresses of your base network of nodes and create an endpoint to contact new nodes with.

