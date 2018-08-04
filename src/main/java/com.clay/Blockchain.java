package com.clay;

import java.util.ArrayList;

public class Blockchain {

    private ArrayList<Block> chain = new ArrayList<>();


    public Blockchain(){
        ArrayList<String> genesisTransactions = new ArrayList<>();
        Block genesisBlock = new Block("0", genesisTransactions, "address");
        chain.add(genesisBlock);

    }

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block block){
        chain.add(block);
    }
}
