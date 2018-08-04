package com.clay;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8331");
        StringEntity requestEntity = new StringEntity(
                "{\"method\" : \"listenForTransactions\", \"data\" : " + block.toJson() + " }",
                ContentType.APPLICATION_JSON);
        try {
            httpPost.setEntity(requestEntity);
            httpclient.execute(httpPost);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean validateNewBlock(Block block){
        return true;
    }

    @Override
    public void run() {
        startServer();
    }
}
