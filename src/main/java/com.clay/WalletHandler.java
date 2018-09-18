package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.protocol.HttpRequestHandler;
import sun.security.ec.ECPublicKeyImpl;

import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WalletHandler extends Handler implements HttpRequestHandler {

    private Wallet wallet;
    private ArrayList<String> transactionPool;
    private Integer MIN_VERIFICATION = 0;

    public WalletHandler(Wallet wallet){
        this.wallet = wallet;
        this.transactionPool = new ArrayList<String>();
    }

    public boolean validateTransaction(Transaction transaction){

        Boolean verifySig = false;
        try {
            Signature sig = Signature.getInstance("SHA1WithECDSA");
            PublicKey pk = new ECPublicKeyImpl(Base64.decodeBase64(transaction.getFromAddress()));
            sig.initVerify(pk);
            sig.update(transaction.getHash().getBytes());
            verifySig = sig.verify(Base64.decodeBase64(transaction.getSignature()));

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        if(transaction.getAmount() >= 0 && verifySig) {
            return true;
        }
        return false;
    }


    public void listenForTransactions(Transaction transaction){
        if(validateTransaction(transaction)) {
            if(transactionPool.contains(transaction.toJson())) {
                String t = transactionPool.get(transactionPool.indexOf(transaction.toJson()));

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Transaction transaction1 = mapper.readValue(t, Transaction.class);
                    transaction1.addNodeSignatures(transaction.getNodeVerifications());
                    String newTransaction = transaction1.toJson();
                    transactionPool.remove(t);
                   transactionPool.add(newTransaction);
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
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
        ArrayList<String> transactions = block.getTransactions();

        for(int i = 0; i < transactions.size(); i++){

        }

    }

    private boolean validateBlock(Block block){
        return DigestUtils.sha256Hex(block.getBlockHead()).equals(block.gethash());
    }

    public Transaction dedupeVerifications(Transaction transaction) {
        ArrayList<String> pendingTransactions = transactionPool;
        for(int i = 0; i < transactionPool.size(); i++){
            if(transaction.getHash() == pendingTransactions.get(i)){
                ObjectMapper mapper = new ObjectMapper();

                try {
                    Transaction transaction1 = mapper.readValue(pendingTransactions.get(i), Transaction.class);
                    Map<String, String> all = transaction.getNodeVerifications();
                    all.putAll(transaction1.getNodeVerifications());
                    pendingTransactions.remove(i);
                    transaction1.setNodeVerifications(all);
                    return transaction1;

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return transaction;
    }

}
