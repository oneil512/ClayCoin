package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import sun.security.ec.ECPublicKeyImpl;

import java.io.IOException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;

public class WalletHandler  implements HttpRequestHandler {

    private Wallet wallet;
    private HashMap transactionPool;
    private Integer MIN_VERIFICATION = 0;

    public WalletHandler(Wallet wallet){
        this.wallet = wallet;
        this.transactionPool = new HashMap<String, Integer>();
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
            return true;
        }
        return false;
    }


    public void listenForTransactions(Transaction transaction){
        if(validateTransaction(transaction)) {
            Integer verifications = (Boolean) transactionPool.get(transaction.toJson()) ?
                    (Integer) transactionPool.get(transaction.toJson()) + 1 : 0;
            transactionPool.put(transaction.toJson(), verifications);
            if(transaction.getToAddress() == wallet.getAddress() && verifications > MIN_VERIFICATION){
                wallet.setBalance(wallet.getBalance() + transaction.getAmount());
            }
        }
    }

    private void listenForBlock(Block block){
        if (validateBlock(block)){
            wallet.getBlockchain().addBlock(block);
            this.updateBalance(block);
        }
    }

    private void updateBalance(Block block){
        ArrayList<String> transactions = block.getTransactions();

        for(int i = 0; i < transactions.size(); i++){

        }

    }

    private boolean validateBlock(Block block){
        return DigestUtils.sha256Hex(block.getBlockHead()).equals(block.getBlockHash());
    }

}
