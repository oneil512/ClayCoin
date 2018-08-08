package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.codec.digest.DigestUtils;

public class Transaction {

    private String fromAddress;
    private String toAddress;
    private String hash;
    private String publicKey;

    private String signature;
    private double amount;

    public Transaction(){}

    public Transaction(double amount, String fromAddress, String toAddress){
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;

        Object[] tx = {fromAddress, toAddress, amount};
        this.hash = DigestUtils.sha256Hex(tx.toString());
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", hash='" + hash + '\'' +
                ", publicKey=" + publicKey +
                ", signature='" + signature + '\'' +
                ", amount=" + amount +
                '}';
    }
}
