package com.clay;

import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

import java.io.IOException;

public class NodeService extends Thread {
    private NodeHandler nodeHandler;

    public NodeService(Wallet wallet) {
        this.nodeHandler = new NodeHandler(new Node(wallet));
        run();
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

    @Override
    public void run() {
        startServer();
    }
}
