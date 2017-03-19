package com.trade.book.booktrade;


import java.io.Serializable;

public class BookObject implements Serializable{

    String name,publisher,description,cateogory,condition;
    int costPrice,sellingPrice,edition,userId,itemId;

    BookObject(String nm,String pb,int cp,int sp, int ed,String des,String condt,String cat,int usrd,int itmid){
        name = nm;
        publisher = pb;
        costPrice = cp;
        sellingPrice = sp;
        edition = ed;
        description = des;
        condition = condt;
        cateogory = cat;
        userId = usrd;
        itemId = itmid;
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public int getSellingPrice() {
        return sellingPrice;
    }

    public int getEdition() {
        return edition;
    }

    public String getDescription() {
        return description;
    }

    public String getCondition() {
        return condition;
    }

    public String getCateogory() {
        return cateogory;
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }
}
