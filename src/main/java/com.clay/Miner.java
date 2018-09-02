package com.clay;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;

public class Miner extends Thread {

    private volatile Node node;

    public Miner(Node node){
        this.node = node;
    }

    public void mine(int difficulty){
        String check = new String(new char[difficulty]).replace("\0", "0");
        while (true) {
            System.out.println("pending trans num");
            System.out.println(node.getPendingTransactions().size());
            boolean minedBlock = false;

            Block block = new Block(
                    node.getWallet().getBlockchain().getLastBlock().getBlockHash(),
                    node.getPendingTransactions(),
                    node.getWallet().getAddress()
            );

            while (!minedBlock) {
                String sha256hex = DigestUtils.sha256Hex(block.getBlockHead());
                //System.out.println("hash " + sha256hex);

                if (sha256hex.startsWith(check)) {
                    block.setBlockHash(sha256hex);
                    minedBlock = true;
                    System.out.print("hash " + block.getBlockHash());
                }
                block.incrementNonce();
            }
            broadcastBlock(block);
            System.out.println("nonce " + block.getNonce().toString());
            node.setPendingTransactions(new ArrayList<>());
        }
    }

    public void broadcastBlock(Block block){
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

    @Override
    public void run() {
        mine(4);
    }

}
