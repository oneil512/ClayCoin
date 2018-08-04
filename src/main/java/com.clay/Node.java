package com.clay;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

import java.io.*;


public class Node extends Thread {

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
        while(nodeService.getPendingTransactions().size() > 0){
            boolean minedBlock = false;
            Block block = new Block(blockchain.getLastBlock().getBlockHash(), nodeService.getPendingTransactions(), wallet.getAddress());
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
