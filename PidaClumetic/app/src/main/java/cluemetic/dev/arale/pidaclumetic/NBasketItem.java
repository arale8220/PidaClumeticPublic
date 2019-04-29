package cluemetic.dev.arale.pidaclumetic;

public class NBasketItem {
    String img, title;
    Integer quantity, price;

    public NBasketItem(String img, String title, Integer price, Integer quantity) {
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
