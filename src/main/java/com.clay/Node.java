package com.clay;

import java.util.ArrayList;

public class Node {

    private volatile ArrayList<String> pendingTransactions = new ArrayList<>();
    private volatile Wallet wallet;

    public Node(Wallet wallet) {
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public ArrayList<String> getPendingTransactions() {
        return pendingTransactions;
    }

    public void setPendingTransactions(ArrayList<String> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public void addPendingTransaction(String transaction) {
        pendingTransactions.add(transaction);
    }
}
