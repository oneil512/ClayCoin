package com.clay;

import java.util.ArrayList;

public class Blockchain {

    private ArrayList<Block> chain = new ArrayList<>();


    public Blockchain(){
        ArrayList<String> genesisTransactions = new ArrayList<>();
        Block genesisBlock = new Block("0", genesisTransactions, "address");

    }

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }
}
