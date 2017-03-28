package com.trade.book.booktrade.objects;

/**
 * Created by Nikhil on 28-Mar-17.
 */

public class RequestObject {

    String name,publisher;

    public RequestObject(String nm,String pb){
        name = nm;
        publisher =pb;
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }
}
