package com.clay;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.server.JsonRpcServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

@JsonRpcService
public class Wallet {
    private Integer balance = 0;

    private String address;
    private String privateKey;
    private Blockchain blockchain;
    private JsonRpcServer rpcServer;

    public Wallet(Blockchain blockchain){
        this.privateKey = randomAlphaNumeric(32);
        this.address = randomAlphaNumeric(32);
	    this.blockchain = blockchain;
        this.rpcServer = new JsonRpcServer();

        startServer();

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
                    String response = rpcServer.handle(request, teamService);
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
