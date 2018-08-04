package com.clay;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class NodeService implements HttpRequestHandler {
    private ArrayList<String> pendingTransactions = new ArrayList<>();


    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        byte[] data;

        /*

        System.out.println(""); // empty line before each request
        System.out.println(httpRequest.getRequestLine());
        System.out.println("-------- HEADERS --------");
        for(Header header: httpRequest.getAllHeaders()) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
        System.out.println("--------");
*/

        HttpEntity entity = null;
        if (httpRequest instanceof HttpEntityEnclosingRequest)
            entity = ((HttpEntityEnclosingRequest)httpRequest).getEntity();

        // For some reason, just putting the incoming entity into
        // the response will not work. We have to buffer the message.
        if (entity == null) {
            data = new byte [0];
        } else {
            data = EntityUtils.toByteArray(entity);
        }

        JSONObject jsonObj = new JSONObject(new String(data));

        System.out.println(jsonObj);

        if(jsonObj.get("method").toString().equals("listenForTransactions")){
            JSONObject payload = jsonObj.getJSONObject("data");
            double amount = Double.parseDouble(payload.get("amount").toString());
            String toAddress = payload.get("toAddress").toString();
            String fromAddress = payload.get("fromAddress").toString();
            Transaction transaction = new Transaction(amount, fromAddress, toAddress);
            listenForTransactions(transaction);
        }


        httpResponse.setEntity(new StringEntity("dummy response"));
    }
    public void listenForTransactions(Transaction transaction){
        pendingTransactions.add(transaction.toString());
    }

    public ArrayList<String> getPendingTransactions() {
        return pendingTransactions;
    }
}
