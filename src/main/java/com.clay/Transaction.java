package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Transaction {

    private String fromAddress;
    private String toAddress;
    private String hash;

    private String signature;
    private double amount;
    private HashMap<String, String> nodeVerifications = new HashMap<>();

    static Semaphore semaphore = new Semaphore(1);

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

    public void addNodeSignature(String signature, String address) {
        try {
            semaphore.acquire();
            nodeVerifications.put(signature, address);
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }
    }

    public void addNodeSignatures(HashMap<String, String> sigs) {
        try {
            semaphore.acquire();
            nodeVerifications.putAll(sigs);
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }
    }

    public HashMap<String, String> getNodeVerifications(){
        return nodeVerifications;
    }

    public void setNodeVerifications(HashMap<String, String> nodeVerifications) {
        this.nodeVerifications = nodeVerifications;
    }

    public String toJson() {
        try {
            semaphore.acquire();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{}";
        } finally {
            semaphore.release();
        }
    }

    @Override
    public String toString() {
        try {
            semaphore.acquire();
            return "Transaction{" +
                    "fromAddress='" + fromAddress + '\'' +
                    ", toAddress='" + toAddress + '\'' +
                    ", hash='" + hash + '\'' +
                    ", signature='" + signature + '\'' +
                    ", amount=" + amount +
                    '}';
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error in concurrency during toString";
        } finally {
            semaphore.release();
        }

    }
}
