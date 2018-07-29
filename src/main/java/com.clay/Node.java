package com.clay;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

@JsonRpcService

public class Node {

    public void mine(){

    }

    @JsonRpcMethod
    public void getTransactions(@JsonRpcParam("transaction") Transaction transaction){

    }
}
