package cluemetic.dev.arale.pidaclumetic;

public class NBasketItem {
    String img, title, price;
    Integer quantity;

    public NBasketItem(String img, String title, String price, Integer quantity) {
        this.img = img;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
