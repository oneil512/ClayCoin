package com.clay;

import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WalletService extends Thread  {
    private ArrayList<Transaction> pendingTransactions = new ArrayList<>();
    private Wallet wallet;
    private WalletHandler walletHandler;

    public WalletService(Blockchain blockchain) {
        this.wallet = new Wallet(blockchain);
        this.walletHandler = new WalletHandler(wallet);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> this.run());
    }
    public void startServer(){
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8331)
                .setServerInfo("Test/1.1")
                .registerHandler("*", this.walletHandler)
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

    public Wallet getWallet() {
        return wallet;
    }

    public WalletHandler getWalletHandler() {
        return walletHandler;
    }

    public ArrayList<Transaction> getPendingTransactions() {
        return pendingTransactions;
    }
}
