package com.clay;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args){

	Wallet wallet = new Wallet();
	Blockchain blockchain = new Blockchain();

	Node node = new Node(blockchain, wallet);

	wallet.sendTransaction(0, wallet.getAddress());

	node.mine(4);


    }
}
