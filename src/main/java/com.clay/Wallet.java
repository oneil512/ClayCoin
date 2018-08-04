package com.clay;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class Wallet extends Thread {
    private Integer balance = 0;

    private String address;
    private String privateKey;
    private Blockchain blockchain;
    private WalletService walletService;

    public Wallet(Blockchain blockchain){
        this.privateKey = randomAlphaNumeric(32);
        this.address = randomAlphaNumeric(32);
	    this.blockchain = blockchain;
        this.walletService = new WalletService();

    }

    public Integer getBalance() {
        return balance;
    }

    public String getAddress() {
        return address;
    }

    public Boolean sendTransaction(double amount, String toAddress){
        if (balance >= amount) {
            Transaction transaction = new Transaction(amount, address, toAddress);
            broadcastTransaction(transaction);
            return true;
        }
        return false;
    }

    public static String randomAlphaNumeric(int count) {
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public void broadcastTransaction(Transaction transaction) {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8332");
        StringEntity requestEntity = new StringEntity(
                "{\"method\" : \"listenForTransactions\", \"data\" : " + transaction.toJson() + " }",
                ContentType.APPLICATION_JSON);
        try {
            httpPost.setEntity(requestEntity);
            httpclient.execute(httpPost);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void startServer(){
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8332)
                .setServerInfo("Test/1.1")
                .registerHandler("*", this.walletService)
                .create();

        try {
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Blockchain getBlockchain(){
	    return this.blockchain;
    }

    private Transaction receiveTransaction(Transaction transaction){
        return transaction;
    }

    @Override
    public void run() {
        startServer();
    }
}
