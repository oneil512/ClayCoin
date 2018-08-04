package com.clay;

import java.util.ArrayList;

public class Node {

    private ArrayList<String> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;
    private Wallet wallet;

    public Node(Wallet wallet) {
        this.blockchain = wallet.getBlockchain();
        this.wallet = wallet;
    }

    public Blockchain getBlockchain() {
        return blockchain;
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
}
