package cluemetic.dev.arale.pidaclumetic;

public class Product {
    private String title;
    private String price;
    private String company;
    private String id;
    private String info_url;
    private String img_url;
    private String info_seller;
    private String info_manyfacturer;
    private String info_country;

    public String getSubCount() {
        return subCount;
    }

    public void setSubCount(String subCount) {
        this.subCount = subCount;
    }

    private String subCount ;

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo_url() {
        return info_url;
    }

    public void setInfo_url(String info_url) {
        this.info_url = info_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getInfo_seller() {
        return info_seller;
    }

    public void setInfo_seller(String info_seller) {
        this.info_seller = info_seller;
    }

    public String getInfo_manyfacturer() {
        return info_manyfacturer;
    }

    public void setInfo_manyfacturer(String info_manyfacturer) {
        this.info_manyfacturer = info_manyfacturer;
    }

    public String getInfo_country() {
        return info_country;
    }

    public void setInfo_country(String info_country) {
        this.info_country = info_country;
    }

    public Product(String title, String price, String company, String id, String info_url, String img_url, String info_seller, String info_manyfacturer, String info_country, String subCount) {
        this.title = title;
        this.price = price;
        this.company = company;
        this.id = id;
        this.info_url = info_url;
        this.img_url = img_url;
        this.info_seller = info_seller;
        this.info_manyfacturer = info_manyfacturer;
        this.info_country = info_country;
        this.subCount = subCount;
    }
}
