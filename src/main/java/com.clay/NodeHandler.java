package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;
import sun.security.ec.ECPublicKeyImpl;

import java.io.IOException;
import java.security.PublicKey;
import java.security.Signature;

public class NodeHandler implements HttpRequestHandler {
    private volatile Node node;
    private NodeService nodeService;

    public NodeHandler(NodeService nodeService){
        this.node = nodeService.getNode();
        this.nodeService = nodeService;
    }

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws IOException {
        byte[] data;
        HttpEntity entity = null;

        if (httpRequest instanceof HttpEntityEnclosingRequest)
            entity = ((HttpEntityEnclosingRequest)httpRequest).getEntity();

        if (entity == null) {
            data = new byte [0];
        } else {
            data = EntityUtils.toByteArray(entity);
        }
        JSONObject jsonObj = new JSONObject(new String(data));

        //TODO write output to log file
        System.out.println(jsonObj);

        if(jsonObj.get("method").toString().equals("listenForTransactions")){
            JSONObject payload = jsonObj.getJSONObject("data");
            ObjectMapper mapper = new ObjectMapper();

            Transaction transaction = mapper.readValue(payload.toString(), Transaction.class);
            listenForTransactions(transaction);
        }

        if(jsonObj.get("method").toString().equals("listenForBlocks")){
            JSONObject payload = jsonObj.getJSONObject("data");
            ObjectMapper mapper = new ObjectMapper();

            Block block = mapper.readValue(payload.toString(), Block.class);
            listenForBlock(block);
        }
    }
    public void listenForTransactions(Transaction transaction){
        if(validateTransaction(transaction)) {
            node.addPendingTransaction(transaction.toJson());
        }
    }

    private void listenForBlock(Block block){
        if (validateBlock(block)){
            node.getWallet().getBlockchain().addBlock(block);
        }
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

    private boolean validateBlock(Block block){
        return DigestUtils.sha256Hex(block.getBlockHead()).equals(block.getBlockHash());
    }

    private boolean validateTransaction(Transaction transaction){

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

    private void broadcastVerified(Transaction transaction) {
        Transaction verifiedTransaction = signTransaction(transaction);
        broadcastVerifiedTransaction(verifiedTransaction);
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
            System.out.println(e.getMessage());
        }
        return transaction;
    }

    public Node getNode() {
        return node;
    }
}
