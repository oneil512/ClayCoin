@JsonRpcService

public class Node {

    public void mine(){

    }

    @JsonRpcMethod
    public void getTransactions(@JsonRpcParam("transaction") Transaction transaction){

    }
}
