package com.clay;

import org.apache.commons.codec.digest.DigestUtils;
import java.util.ArrayList;

public class Node {

    private ArrayList<String> pendingTransactions = new ArrayList<>();
    private Blockchain blockchain;
    private Wallet wallet;

    public Node(Wallet wallet){
        this.blockchain = wallet.getblockchain();
	this.wallet = wallet;
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

            checkForNewBlock();
        }
    }

    public void listenForTransactions(Transaction transaction){
        pendingTransactions.add(transaction.toString());
    }

    private void checkForNewBlock(){

    }
    
    private void broadcastBlock(Block block){
    }
}
