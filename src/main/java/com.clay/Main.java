package com.clay;

public class Main {

    public static void main(String[] args){

        //TODO allow for all nodes to try and mine block and select their policy for doing so
        // send loops of async requests to everything in the node/wallet connection address
    	Blockchain blockchain = new Blockchain();

		WalletService walletService1 = new WalletService(blockchain);
		NodeService nodeService = new NodeService(walletService1);


		walletService1.getWallet().sendTransaction(0, walletService1.getWallet().getAddress());
		walletService1.getWallet().sendTransaction(0, walletService1.getWallet().getAddress());
		walletService1.getWallet().sendTransaction(0, walletService1.getWallet().getAddress());
    }
}
