package com.clay;

public class Main {

    public static void main(String[] args){

    	Blockchain blockchain = new Blockchain();

		Wallet wallet = new Wallet(blockchain);
		Node node = new Node(wallet);
		node.run();

		Wallet wallet2 = new Wallet(blockchain);

		wallet2.sendTransaction(0, wallet.getAddress());

	//	node.mine(4);


    }
}
