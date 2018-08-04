package com.clay;

public class Main {

    public static void main(String[] args){

    	Blockchain blockchain = new Blockchain();

		Wallet wallet = new Wallet(blockchain);
		NodeService node = new NodeService(wallet);

		Wallet wallet2 = new Wallet(blockchain);
		wallet2.run();

		wallet2.sendTransaction(0, wallet.getAddress());
    }
}
