package com.clay;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

import java.util.ArrayList;

@JsonRpcService
public class Node {

    private ArrayList<Transaction> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;

    public Node(Blockchain blockchain){
        this.blockchain = blockchain;
    }

    public void mine(int difficulty){
        while(pendingTransactions.size() > 0){

            updatePendingTransactions();
        }
    }

    @JsonRpcMethod
    public void getTransactions(@JsonRpcParam("transaction") Transaction transaction){
        pendingTransactions.add(transaction);
    }

    private void updatePendingTransactions(){

    }
}
