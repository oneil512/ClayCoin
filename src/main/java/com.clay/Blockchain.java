package com.clay;

public class Blockchain {

    public Blockchain(){
        int[] genesisTransactions = {};
        Block genesisBlock = new Block(0, genesisTransactions);

        System.out.print(genesisBlock.getBlockHash());
    }
}
