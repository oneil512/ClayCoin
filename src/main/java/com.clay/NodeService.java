package com.clay;

import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NodeService extends Thread {
    private NodeHandler nodeHandler;
    private Miner miner;

    private volatile Node node;
    private Wallet wallet;

    public NodeService(WalletService walletService) {
        this.wallet = walletService.getWallet();
        this.node = new Node(wallet);

        this.miner = new Miner(node);
        this.nodeHandler = new NodeHandler(this);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> this.run());
        executor.execute(() -> miner.run());
    }

    public void startServer(){
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8332)
                .setServerInfo("Test/1.1")
                .registerHandler("*", this.nodeHandler)
                .create();

        try {
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void run() {
        startServer();
    }
}
