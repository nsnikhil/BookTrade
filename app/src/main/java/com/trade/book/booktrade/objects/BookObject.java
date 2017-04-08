package com.trade.book.booktrade.objects;


import java.io.Serializable;

public class BookObject implements Serializable{

    private String name,publisher,description,cateogory,condition,photo0,photo1,photo2,photo3,photo4,photo5,photo6,photo7,userId;
    private int bid,costPrice,sellingPrice,edition,itemId,status;

    public BookObject(int id,String nm,String pb,int cp,int sp, int ed,String des,String condt,String cat,String usrd,int itmid,
                      String pic0,String pic1,String pic2,String pic3,String pic4,String pic5,String pic6,String pic7,int bookstatus){
        bid = id;
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
        photo0 = pic0;
        photo1 = pic1;
        photo2 = pic2;
        photo3 = pic3;
        photo4 = pic4;
        photo5 = pic5;
        photo6 = pic6;
        photo7 = pic7;
        status = bookstatus;
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

    public String getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getPhoto0() {
        return photo0;
    }

    public String getPhoto1() {
        return photo1;
    }

    public String getPhoto2() {
        return photo2;
    }

    public String getPhoto3() {
        return photo3;
    }

    public String getPhoto4() {
        return photo4;
    }

    public String getPhoto5() {
        return photo5;
    }

    public String getPhoto6() {
        return photo6;
    }

    public String getPhoto7() {
        return photo7;
    }

    public int getBid() {
        return bid;
    }

    public int getStatus() {
        return status;
    }
}
