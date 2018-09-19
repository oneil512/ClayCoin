package com.clay;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.protocol.HttpRequestHandler;

import java.util.ArrayList;

public class WalletHandler extends Handler implements HttpRequestHandler {

    private Wallet wallet;
    private ArrayList<Transaction> transactionPool;
    private Integer MIN_VERIFICATION = 0;

    public WalletHandler(Wallet wallet){
        this.wallet = wallet;
        this.transactionPool = new ArrayList<Transaction>();
    }

    public void listenForTransactions(Transaction transaction){
        if(validateTransaction(transaction)) {
            if(transactionPool.contains(transaction)) {
                Transaction t = transactionPool.get(transactionPool.indexOf(transaction));
                t.addNodeSignatures(transaction.getNodeVerifications());
            }

            if(transaction.getToAddress() == wallet.getAddress() && transaction.getNodeVerifications().size() > MIN_VERIFICATION){
                wallet.setBalance(wallet.getBalance() + transaction.getAmount());
            }
        }
    }

    public void listenForBlock(Block block){
        if (validateBlock(block)){
            wallet.getBlockchain().addBlock(block);
            this.updateBalance(block);
        }
    }

    private void updateBalance(Block block){
        ArrayList<Transaction> transactions = block.getTransactions();

        for(int i = 0; i < transactions.size(); i++){

        }

    }

    private boolean validateBlock(Block block){
        return DigestUtils.sha256Hex(block.getBlockHead()).equals(block.gethash());
    }
}
