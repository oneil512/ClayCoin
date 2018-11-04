package com.clay;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.codec.digest.DigestUtils;

public class Block {
    private String previousHash;
    private ArrayList<Transaction> transactions;

    private Integer nonce = 0;
    private Instant ts;

    private Integer merkleRoot;
    private Integer reward = 10;
    private String hash;

    static Semaphore semaphore = new Semaphore(1);

    public Block(){}

    public Block(String previousHash, ArrayList<Transaction> transactions, String address) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.ts = Instant.now();
        this.transactions.add(0, new Transaction(reward, "0", address));
        this.merkleRoot = generateMerkleRoot();

        this.hash = DigestUtils.sha256Hex(getBlockHead());
    }

    private int generateMerkleRoot(){
        ArrayList<Integer> list = new ArrayList<>();

        if(transactions.size() % 2 != 0){
                list.add(transactions.get(transactions.size() - 1).hashCode());
        }

        for(int i = 0; i < transactions.size(); i++){
            list.add(transactions.get(i).hashCode());
        }

        while(list.size() > 1){

            Integer h1 = list.get(list.size() - 1);
            list.remove(list.size() - 1);

            Integer h2 = list.get(list.size() - 1);
            list.remove(list.size() - 1);

            Integer h3 = (h1.toString() + h2.toString()).hashCode();
            list.add(h3);
        }
        return list.size() > 0 ? list.get(0) : 0;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void incrementNonce(){
        this.nonce += 1;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        try {
            semaphore.acquire();
            this.transactions = transactions;
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }
        generateMerkleRoot();
    }

    public String gethash() {
        return hash;
    }

    public Integer getNonce() {
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

    public void sethash(String hash){
        this.hash = hash;
    }

    public String toJson() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(this);
        } catch (Exception e) {
            return "{}";
        }
    }

    public String getBlockHead() {
        try {
            semaphore.acquire();
            return previousHash +
                    transactions.toString() +
                    nonce.toString() +
                    ts.toString() +
                    merkleRoot.toString();
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            semaphore.release();
        }
        return "";
    }

}
