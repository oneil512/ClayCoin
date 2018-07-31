package com.clay;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.sun.istack.internal.NotNull;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Wallet {
    private Integer balance;

    private String address;
    private String privateKey;

    public Wallet(){
        this.privateKey = randomAlphaNumeric(32);
        this.address = randomAlphaNumeric(32);
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
        client.createNotification()
                .method("getTransactions")
                .param("transaction", transaction.toString())
                .execute();
    }

    JsonRpcClient client = new JsonRpcClient(new Transport() {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        @NotNull
        public String pass(@NotNull String request) throws IOException {
            // Used Apache HttpClient 4.3.1 as an example
            HttpPost post = new HttpPost("http://json-rpc-server/team");
            post.setEntity(new StringEntity(request, Charsets.UTF_8));
            post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                return EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
            }
        }
    });
}
