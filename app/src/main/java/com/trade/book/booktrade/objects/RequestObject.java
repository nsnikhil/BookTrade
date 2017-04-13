package com.trade.book.booktrade.objects;


public class RequestObject {

    private String name, publisher, userId;
    private int requestId;

    public RequestObject(String nm, String pb, String uid, int rid) {
        name = nm;
        publisher = pb;
        userId = uid;
        requestId = rid;
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getUserId() {
        return userId;
    }

    public int getRequestId() {
        return requestId;
    }


}
