package com.clay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class WalletService implements HttpRequestHandler {
    private ArrayList<String> pendingTransactions = new ArrayList<>();


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

        if(jsonObj.get("method").toString().equals("listenForBlocks")){
            JSONObject payload = jsonObj.getJSONObject("data");
            ObjectMapper mapper = new ObjectMapper();

            Block block = mapper.readValue(payload.toString(), Block.class);
            listenForBlocks(block);
        }
    }
    public void listenForBlocks(Block block){
        System.out.print(1);
    }

    public ArrayList<String> getPendingTransactions() {
        return pendingTransactions;
    }
}
