package com.clay;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.server.JsonRpcServer;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

@JsonRpcService
public class Wallet extends Thread {
    private Integer balance = 0;

    private String address;
    private String privateKey;
    private Blockchain blockchain;
    private JsonRpcServer rpcServer;
    private JsonRpcClient client;

    public Wallet(Blockchain blockchain){
        this.privateKey = randomAlphaNumeric(32);
        this.address = randomAlphaNumeric(32);
	    this.blockchain = blockchain;
        this.rpcServer = new JsonRpcServer();

        this.client = new JsonRpcClient(new Transport() {

            CloseableHttpClient httpClient = HttpClients.createDefault();

            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                // Used Apache HttpClient 4.3.1 as an example
                HttpPost post = new HttpPost("http://localhost:8332");
                post.setEntity(new StringEntity(request, Charsets.UTF_8));
                post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
                try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                    return EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
                }
            }
        });

        //startServer();

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
                .method("listenForTransactions")
                .param("transaction", transaction.toString())
                .execute();
    }

    public void startServer(){
        try {
            JsonRpcServer rpcServer = new JsonRpcServer();
            ServerSocket server = new ServerSocket(8331);
            System.out.println("Listening for connection on port 8331 ....");
            while (true) {
                try (Socket socket = server.accept()) {
                    Date today = new Date();
                    String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + today;
                    socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                    //String response = rpcServer.handle(request, teamService);
                } catch (IOException e) {
                    System.out.print(e.getMessage());
                }
            }
        } catch (IOException e){
            System.out.print(e.getMessage());
        }
    }

    public Blockchain getBlockchain(){
	    return this.blockchain;
    }

    @JsonRpcMethod
    private Transaction receiveTransaction(@JsonRpcParam("transaction") Transaction transaction){
        return transaction;
    }
}
