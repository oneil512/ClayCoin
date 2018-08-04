package com.clay;

import java.util.Arrays;

public class Transaction {

    private String fromAddress;
    private String toAddress;

    private int hash;

    private double amount;

    public Transaction(double amount, String fromAddress, String toAddress){
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;

        Object[] arr = {fromAddress, toAddress, amount};
        this.hash = Arrays.hashCode(arr);
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String toJson() {
        return "{ \"fromAddress\" : \"" + fromAddress + "\"," +
         "\"toAddress\" : \"" + toAddress + "\"," +
         "\"hash\" : \"" + hash + "\"," +
         "\"amount\" : \"" + amount + "\"," + "}";
    }
}
