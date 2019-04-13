package cluemetic.dev.arale.pidaclumetic;

public class ROnePurchaseItem {
    Integer type;
    String img, title, descr, id;

    public ROnePurchaseItem(Integer type, String img, String title, String descr, String id) {
        this.type = type;
        this.img = img;
        this.title = title;
        this.descr = descr;
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
