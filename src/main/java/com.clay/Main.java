package com.clay;

public class Main {

    public static void main(String[] args){

        //TODO allow for all nodes to try and mine block and select their policy for doing so
        // Note, this will not work because they will be using the same port. need two computers.
        // send loops of async requests to everything in the node/wallet connection address
    	Blockchain blockchain = new Blockchain();

		Wallet wallet1 = new Wallet(blockchain);
		Wallet wallet2 = new Wallet(blockchain);
		NodeService node1 = new NodeService(wallet1);


		wallet1.run();

		wallet1.sendTransaction(0, wallet2.getAddress());
    }
}
