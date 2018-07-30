package com.clay;

import java.util.ArrayList;

public class Blockchain {

    private ArrayList<Block> chain = new ArrayList<>();
    private int reward = 10;


    public Blockchain(){
        ArrayList<String> genesisTransactions = new ArrayList<>();
        Block genesisBlock = new Block(0, genesisTransactions);

    }

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public int getReward() {
        return reward;
    }
}
