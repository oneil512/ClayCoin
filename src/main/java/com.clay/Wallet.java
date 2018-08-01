package com.clay;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Wallet {
    private Integer balance = 0;

    private String address;
    private String privateKey;
    private Blockchain blockchain;

    public Wallet(Blockchain blockchain){
        this.privateKey = randomAlphaNumeric(32);
        this.address = randomAlphaNumeric(32);
	this.blockchain = blockchain;
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
    }

    public Blockchain getBlockchain(){
	return this.blockchain;
    }
}
