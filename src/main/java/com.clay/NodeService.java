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

        System.out.println(""); // empty line before each request
        System.out.println(httpRequest.getRequestLine());
        System.out.println("-------- HEADERS --------");
        for(Header header: httpRequest.getAllHeaders()) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
        System.out.println("--------");

        HttpEntity entity = null;
        if (httpRequest instanceof HttpEntityEnclosingRequest)
            entity = ((HttpEntityEnclosingRequest)httpRequest).getEntity();

        // For some reason, just putting the incoming entity into
        // the response will not work. We have to buffer the message.
        byte[] data;
        if (entity == null) {
            data = new byte [0];
        } else {
            data = EntityUtils.toByteArray(entity);
        }

        System.out.println(new String(data));
        String stringData = new String(data);
        JSONObject jsonObj = new JSONObject(stringData);
        System.out.println(jsonObj);


        httpResponse.setEntity(new StringEntity("dummy response"));
    }
    public void listenForTransactions(Transaction transaction){
        pendingTransactions.add(transaction.toString());
    }

    public ArrayList<String> getPendingTransactions() {
        return pendingTransactions;
    }
}
