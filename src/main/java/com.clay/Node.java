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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

//TODO have a thread check for new blocks

@JsonRpcService
public class Node extends Thread {

    private ArrayList<String> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;
    private Wallet wallet;
    private JsonRpcServer rpcServer;
    private JsonRpcClient client;

    public Node(Wallet wallet){
        this.blockchain = wallet.getBlockchain();
	    this.wallet = wallet;
        this.rpcServer = new JsonRpcServer();

        this.client = new JsonRpcClient(new Transport() {

            CloseableHttpClient httpClient = HttpClients.createDefault();

            @NotNull
            @Override
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

    @JsonRpcMethod
    public void listenForTransactions(@JsonRpcParam("transaction") Transaction transaction){
        pendingTransactions.add(transaction.toString());
    }

    @JsonRpcMethod
    private void listenForBlock(@JsonRpcParam("block") Block block){
        validateNewBlock(block);

    }

    public void startServer(){
        try {
            ServerSocket server = new ServerSocket(8332);
            System.out.println("Listening for connection on port 8332 ....");
            while (true) {
                try (Socket socket = server.accept()) {
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String request = "";
                    String line;
                    while ((line = buffer.readLine()) != null) {
                        if (line.isEmpty()) {
                            break;
                        }
                        System.out.println(line);
                        request += line;
                    }

                    System.out.print("port 8332 reading: " + request);

                    String response = rpcServer.handle(request, this);
                } catch (IOException e) {
                    System.out.print(e.getMessage());
                }
            }
        } catch (IOException e){
            System.out.print(e.getMessage());
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
