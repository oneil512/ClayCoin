package com.clay;


import com.sun.tools.javac.util.List;
import org.apache.commons.lang3.ArrayUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class Block {
    private int previousHash;
    private ArrayList<String> transactions;
    private int nonce;
    private Instant ts;
    private int merkleRoot;

    private int blockHash;

    public Block(int previousHash, ArrayList<String> transactions) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.ts = Instant.now();
        this.merkleRoot = generateMerkleRoot();

        Object[] contents = {transactions, previousHash};

        blockHash = Arrays.hashCode(contents);
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

    public int getPreviousHash() {
        return previousHash;
    }

    public int getBlockHash() {
        return blockHash;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public Instant getTs() {
        return ts;
    }

    public void setTs(Instant ts) {
        this.ts = ts;
    }
}
