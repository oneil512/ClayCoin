package com.clay;

public class Main {

    public static void main(String[] args){

    	Blockchain blockchain = new Blockchain();

		Wallet wallet = new Wallet(blockchain);
		Wallet wallet2 = new Wallet(blockchain);

		Node node = new Node(wallet);
		node.start();

		wallet.sendTransaction(0, wallet2.getAddress());

	//	node.mine(4);


    }
}
