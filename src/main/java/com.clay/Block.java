package com.clay;

import java.time.Instant;
import java.util.Arrays;

public class Block {
    private int previousHash;
    private String[] transactions;
    private int nonce;
    private Instant ts;

    private int blockHash;

    public Block(int previousHash, String[] transactions) {
        this.previousHash = previousHash;
        this.transactions = transactions;

        Object[] contents = {transactions, previousHash};

        blockHash = Arrays.hashCode(contents);
    }

    public String[] getTransactions() {
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
