package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.ArrayList;

public class NodeHandler implements HttpRequestHandler {
    private Node node;

    public NodeHandler(Node node){
        this.node = node;
    }


    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
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
        node.getPendingTransactions().add(transaction.toString());
        if(validateTransaction(transaction)) {
            mine(4);
        }
    }

    private void listenForBlock(Block block){
        validateBlock(block);
    }

    private boolean validateBlock(Block block){
        return DigestUtils.sha256Hex(block.getBlockHead()).equals(block.getBlockHash());
    }

    private boolean validateTransaction(Transaction transaction){
        if(transaction.getAmount() < 0) {
            return false;
        }
        return true;
    }


    public void broadcastBlock(Block block){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8331");
        StringEntity requestEntity = new StringEntity(
                "{\"method\" : \"listenForTransactions\", \"data\" : " + block.toJson() + " }",
                ContentType.APPLICATION_JSON);
        try {
            httpPost.setEntity(requestEntity);
            httpclient.execute(httpPost);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void mine(int difficulty){
        String check = new String(new char[difficulty]).replace("\0", "0");
        while (node.getPendingTransactions().size() > 0) {
            System.out.println("pending trans > 0");
            boolean minedBlock = false;

            Block block = new Block(
                    node.getBlockchain().getLastBlock().getBlockHash(),
                    node.getPendingTransactions(),
                    node.getWallet().getAddress()
            );

            while (!minedBlock) {
                String sha256hex = DigestUtils.sha256Hex(block.getBlockHead());
                //System.out.println("hash " + sha256hex);

                if (sha256hex.startsWith(check)) {
                    block.setBlockHash(sha256hex);
                    minedBlock = true;
                    System.out.print(block.getBlockHash());
                }
                block.incrementNonce();
            }
            broadcastBlock(block);
            System.out.println(block.getNonce());
            node.setPendingTransactions(new ArrayList<>());
        }
    }

    public Node getNode() {
        return node;
    }
}
