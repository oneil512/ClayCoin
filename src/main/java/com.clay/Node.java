package com.clay;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import org.apache.commons.codec.digest.DigestUtils;


import java.security.MessageDigest;
import java.util.ArrayList;

@JsonRpcService
public class Node {

    private ArrayList<String> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;
    private MessageDigest digest;

    public Node(Blockchain blockchain){
        this.blockchain = blockchain;
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            System.out.print(e.getLocalizedMessage());
        }
    }

    public void mine(int difficulty){
        while(pendingTransactions.size() > 0){
            boolean minedBlock = false;
            Block block = new Block(blockchain.getLastBlock().getBlockHash(), pendingTransactions);
            while(!minedBlock){
                String sha256hex = DigestUtils.sha256Hex(block.getBlockHead());
            }

            checkForNewBlock();
        }
    }

    @JsonRpcMethod
    public void getTransactions(@JsonRpcParam("transaction") Transaction transaction){
        pendingTransactions.add(transaction.toString());
    }

    private void checkForNewBlock(){

    }
}
