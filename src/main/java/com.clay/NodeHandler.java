package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpRequestHandler;
import sun.misc.BASE64Encoder;
import sun.security.ec.ECPublicKeyImpl;

import java.io.IOException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static sun.tools.jstat.Alignment.keySet;

public class NodeHandler extends Handler implements HttpRequestHandler {
    private volatile Node node;
    private NodeService nodeService;

    public NodeHandler(NodeService nodeService){
        this.node = nodeService.getNode();
        this.nodeService = nodeService;
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
            node.addPendingTransaction(dedupeVerifications(transaction).toJson());
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

    @Override
    public boolean validateTransaction(Transaction transaction){

        Boolean verifySig = false;
        try {
            Signature sig = Signature.getInstance("SHA1WithECDSA");
            PublicKey pk = new ECPublicKeyImpl(Base64.decodeBase64(transaction.getFromAddress()));
            sig.initVerify(pk);
            sig.update(transaction.getHash().getBytes());
            verifySig = sig.verify(Base64.decodeBase64(transaction.getSignature()));

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        if(transaction.getAmount() >= 0 && verifySig) {
            broadcastVerified(transaction);
            return true;
        }
        return false;
    }

    private boolean validateBlock(Block block) {
        return DigestUtils.sha256Hex(block.getBlockHead()).equals(block.gethash());
    }

    public Transaction dedupeVerifications(Transaction transaction) {
        ArrayList<String> pendingTransactions = node.getPendingTransactions();
        for(int i = 0; i < node.getPendingTransactions().size(); i++){
            if(transaction.getHash() == pendingTransactions.get(i)){
                ObjectMapper mapper = new ObjectMapper();

                try {
                    Transaction transaction1 = mapper.readValue(pendingTransactions.get(i), Transaction.class);
                    Map<String, String> all = transaction.getNodeVerifications();
                    all.putAll(transaction1.getNodeVerifications());
                    pendingTransactions.remove(i);
                    transaction1.setNodeVerifications(all);
                    return transaction1;

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return transaction;
    }
}
