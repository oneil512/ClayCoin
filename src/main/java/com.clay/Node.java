package com.clay;

import java.util.ArrayList;

public class Node {

    private volatile ArrayList<Transaction> pendingTransactions = new ArrayList<>();
    private volatile Wallet wallet;

    public Node(Wallet wallet) {
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public ArrayList<Transaction> getPendingTransactions() {
        return pendingTransactions;
    }

    public void setPendingTransactions(ArrayList<Transaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public void addPendingTransaction(Transaction transaction) {
        pendingTransactions.add(transaction);
    }
}
