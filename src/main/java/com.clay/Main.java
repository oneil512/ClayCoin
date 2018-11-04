package com.clay;

public class Main {

    public static void main(String[] args){

    	// conflict resolution when node gets a soft fork
		// convert to bigendian
		// two reward transactions
		// command line interface to send transactions
		// more validation
        // send loops of async requests to everything in the node/wallet connection address
        // have different endpoints that you can hit, instead of determining everything from the body of the request
    	Blockchain blockchain = new Blockchain();

		WalletService walletService1 = new WalletService(blockchain);
		NodeService nodeService = new NodeService(walletService1);

        while(true) {
            try{
            Thread.sleep(2000);
            walletService1.getWallet().sendTransaction(0, walletService1.getWallet().getAddress());
            System.out.println(walletService1.getWallet().getBalance());
            } catch (Exception e){
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
}
