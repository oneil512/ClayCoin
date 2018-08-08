package com.clay;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class Wallet extends Thread {
    private double balance = 0;

    private Blockchain blockchain;
    private WalletService walletService;
    private KeyPair keyPair;

    public Wallet(Blockchain blockchain){
	    this.blockchain = blockchain;
        this.walletService = new WalletService();
        try {
            this.keyPair = getKeyPair();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public double getBalance() {
        return balance;
    }

    public String getAddress() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }

    public Boolean sendTransaction(double amount, String toAddress){
        if (balance >= amount) {
            Transaction transaction = new Transaction(amount, getAddress(), toAddress);
            transaction = signTransaction(transaction);
            broadcastTransaction(transaction);
            balance -= amount;
            return true;
        }
        return false;
    }

    private Transaction signTransaction(Transaction transaction){
        try {
            byte[] data = transaction.getHash().getBytes();

            Signature sig = Signature.getInstance("SHA1WithECDSA");
            sig.initSign(keyPair.getPrivate());
            sig.update(data);
            byte[] signatureBytes = sig.sign();
            transaction.setSignature(new BASE64Encoder().encode(signatureBytes));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return transaction;
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
                .setListenerPort(8331)
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

    private static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        keyGen.initialize(ecSpec);
        KeyPair kp = keyGen.generateKeyPair();
        return kp;
    }

    @Override
    public void run() {
        startServer();
    }
}
