package com.example.gadgetshop;

public class ShoppingItem {

    private String id;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int cartedCount;

    public ShoppingItem(String name, String info, String price, float ratedInfo, int imageResource, int cartedCount) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.cartedCount = cartedCount;
    }

    public ShoppingItem() {
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCartedCount() {
        return cartedCount;
    }

    public void setCartedCount(int cartedCount) {
        this.cartedCount = cartedCount;
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

    public String _getId() {
        return id;
    }

    public void setId(String id){ this.id = id; }
}
