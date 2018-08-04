package com.clay;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

import java.io.*;
import java.util.ArrayList;

//TODO have a thread check for new blocks

public class Node extends Thread{

    private ArrayList<String> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;
    private Wallet wallet;
    private NodeService nodeService;

    public Node(Wallet wallet) {
        this.blockchain = wallet.getBlockchain();
        this.wallet = wallet;
        this.nodeService = new NodeService();
    }

    public void mine(int difficulty){
        String check = new String(new char[difficulty]).replace("\0", "0");
        while(pendingTransactions.size() > 0){
            boolean minedBlock = false;
            Block block = new Block(blockchain.getLastBlock().getBlockHash(), pendingTransactions, wallet.getAddress());
            while(!minedBlock){
                String sha256hex = DigestUtils.sha256Hex(block.getBlockHead());
                if (sha256hex.startsWith(check)){
                    block.setBlockHash(sha256hex);
                    minedBlock = true;
                    System.out.print(block.getBlockHash());
                }
                block.incrementNonce();
            }
            broadcastBlock(block);
        }
    }

    public void listenForTransactions(Transaction transaction){
        pendingTransactions.add(transaction.toString());
    }

    private void listenForBlock(Block block){
        validateNewBlock(block);

    }

    public void startServer(){
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8332)
                .setServerInfo("Test/1.1")
                .registerHandler("*", this.nodeService)
                .create();

        try {
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void broadcastBlock(Block block){
    }

    private boolean validateNewBlock(Block block){
        return true;
    }

    @Override
    public void run() {
        startServer();
    }
}
