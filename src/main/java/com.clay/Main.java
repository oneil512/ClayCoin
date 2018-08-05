package com.clay;

public class Main {

    public static void main(String[] args){

        //TODO allow for all nodes to try and mine block and select their policy for doing so
        // Note, this will not work because they will be using the same port. need two computers.
    	Blockchain blockchain = new Blockchain();

		Wallet wallet1 = new Wallet(blockchain);
		Wallet wallet2 = new Wallet(blockchain);
		Wallet wallet3 = new Wallet(blockchain);
		NodeService node1 = new NodeService(wallet1);
		NodeService node2 = new NodeService(wallet2);
		NodeService node3 = new NodeService(wallet3);


		wallet1.run();
		wallet2.run();
		wallet3.run();

		wallet1.sendTransaction(0, wallet2.getAddress());
		wallet2.sendTransaction(0, wallet3.getAddress());
		wallet3.sendTransaction(0, wallet1.getAddress());
		wallet2.sendTransaction(0, wallet1.getAddress());
    }
}
