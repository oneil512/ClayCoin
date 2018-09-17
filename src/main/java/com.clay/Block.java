package com.clay;

import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.codec.digest.DigestUtils;

public class Block {
    private String previousHash;
    private ArrayList<String> transactions;

    private Integer nonce = 0;
    private Instant ts;

    private Integer merkleRoot;
    private Integer reward = 10;
    private String hash;

    public Block(){}

    public Block(String previousHash, ArrayList<String> transactions, String address) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.ts = Instant.now();
        this.transactions.add(0, new Transaction(reward, "0", address).toString());
        this.merkleRoot = generateMerkleRoot();

        this.hash = DigestUtils.sha256Hex(getBlockHead());
    }

    private int generateMerkleRoot(){
        if(transactions.size() % 2 != 0){
                transactions.add(transactions.get(transactions.size() - 1));
        }
        ArrayList<Integer> list = new ArrayList<>();
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
        return list.get(0);
    }

    public ArrayList<String> getTransactions() {
        return transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void incrementNonce(){
        this.nonce += 1;
    }

    public void setTransactions(ArrayList<String> transactions) {
        this.transactions = transactions;
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
        return previousHash +
                transactions.toString() +
                nonce.toString() +
                ts.toString() +
                merkleRoot.toString();
    }

}
