package com.clay;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.codec.digest.DigestUtils;

public class Block {
    private String previousHash;
    private ArrayList<String> transactions;
    private Integer nonce;
    private Instant ts;
    private Integer merkleRoot;
    private Integer reward = 10;

    private String blockHash;

    public Block(String previousHash, ArrayList<String> transactions,  String address) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.ts = Instant.now();
        this.merkleRoot = generateMerkleRoot();
        transactions.add(0, new Transaction(reward, "0", address).toString());

        this.blockHash = DigestUtils.sha256Hex(getBlockHead());
    }

    private int generateMerkleRoot(){
        if(transactions.size() % 2 != 0){
                transactions.set(transactions.size(), transactions.get(transactions.size() - 1));
        }
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < transactions.size(); i++){
            list.set(i, transactions.get(i).hashCode());
        }

        while(list.size() > 1){

            Integer h1 = list.get(list.size() - 1);
            list.remove(list.size() - 1);

            Integer h2 = list.get(list.size() - 1);
            list.remove(list.size() - 1);

            Integer h3 = (h1.toString() + h2.toString()).hashCode();
            list.set(list.size(), h3);
        }
        return list.get(0);
    }

    public ArrayList<String> getTransactions() {
        return transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Instant getTs() {
        return ts;
    }

    public void setTs(Instant ts) {
        this.ts = ts;
    }

    public Integer getMerkleRoot() {
        return merkleRoot;
    }

    public void setBlockHash(String hash){
        this.blockHash = hash;
    }

    public String getBlockHead() {
        return previousHash +
                transactions.toString() +
                nonce.toString() +
                ts.toString() +
                merkleRoot.toString();
    }

}
