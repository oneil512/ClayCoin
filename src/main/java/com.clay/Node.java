package com.clay;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.server.JsonRpcServer;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

//TODO have a thread check for new blocks

@JsonRpcService
public class Node {

    private ArrayList<String> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;
    private Wallet wallet;
    private JsonRpcServer rpcServer;

    public Node(Wallet wallet){
        this.blockchain = wallet.getBlockchain();
	    this.wallet = wallet;
        this.rpcServer = new JsonRpcServer();

	    startServer();
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
    
    private void broadcastBlock(Block block){
    }

    private boolean validateNewBlock(Block block){
        return true;
    }
}
