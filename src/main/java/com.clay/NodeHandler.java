package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpRequestHandler;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.Signature;

public class NodeHandler extends Handler implements HttpRequestHandler {
    private volatile Node node;

    public NodeHandler(NodeService nodeService){
        this.node = nodeService.getNode();
    }

    public Node getNode() {
        return node;
    }

    public void broadcastVerifiedTransaction(Transaction transaction) {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8332");
        StringEntity requestEntity = new StringEntity(
                "{\"method\" : \"listenForTransactions\", \"data\" : " + transaction.toJson() + " }",
                ContentType.APPLICATION_JSON);
        try {
            httpPost.setEntity(requestEntity);
            httpclient.execute(httpPost);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void listenForTransactions(Transaction transaction){
        if(validateTransaction(transaction)) {
            broadcastVerified(transaction);
            if(node.getPendingTransactions().contains(transaction)){
                Transaction t = node.getPendingTransactions().get(node.getPendingTransactions().indexOf(transaction));
                t.addNodeSignatures(transaction.getNodeVerifications());
            } else {
                node.addPendingTransaction(transaction);
            }
        }
    }

    public void listenForBlock(Block block){
        if (validateBlock(block)){
            node.getWallet().getBlockchain().addBlock(block);
        }
    }

    private void broadcastVerified(Transaction transaction) {
        Transaction verifiedTransaction = signTransaction(transaction);
        //broadcastVerifiedTransaction(verifiedTransaction); Can't do this while I am broadcasting to myself
    }

    public Transaction signTransaction(Transaction transaction){
        try {
            byte[] data = transaction.getHash().getBytes();

            Signature sig = Signature.getInstance("SHA1WithECDSA");
            sig.initSign(node.getWallet().getPrivateKey());
            sig.update(data);
            byte[] signatureBytes = sig.sign();
            transaction.addNodeSignature(new BASE64Encoder().encode(signatureBytes), node.getWallet().getAddress());

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return transaction;
    }

    private boolean validateBlock(Block block) {
        return DigestUtils.sha256Hex(block.getBlockHead()).equals(block.gethash());
    }
}
