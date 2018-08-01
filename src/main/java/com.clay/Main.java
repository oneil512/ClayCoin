package com.clay;

public class Main {

    public static void main(String[] args){

	Blockchain blockchain = new Blockchain();
	Wallet wallet = new Wallet(blockChain);

	Node node = new Node(wallet);

	wallet.sendTransaction(0, wallet.getAddress());

	node.mine(4);


    }
}
