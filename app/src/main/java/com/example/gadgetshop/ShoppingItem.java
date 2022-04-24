package com.example.gadgetshop;

public class ShoppingItem {

    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private final int imageResource;

    public ShoppingItem(String name, String info, String price, float ratedInfo, int imageResource) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
    }

    String getName() {
        return name;
    }

    String getInfo() {
        return info;
    }

    String getPrice() {
        return price;
    }

    float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRatedInfo(float ratedInfo) {
        this.ratedInfo = ratedInfo;
    }

}
