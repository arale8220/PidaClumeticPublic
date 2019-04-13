package cluemetic.dev.arale.pidaclumetic;

public class OPidaProduct {
    Integer type, state; //type 0 tester 1 normal 2 group // state 0 준비중 1 배송중 2 완료
    String orderUri, imgUri, time, number;

    public OPidaProduct(Integer type, Integer state, String orderUri, String imgUri, String time, String number) {
        this.type = type;
        this.state = state;
        this.orderUri = orderUri;
        this.imgUri = imgUri;
        this.time = time;
        this.number = number;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getOrderUri() {
        return orderUri;
    }

    public void setOrderUri(String orderUri) {
        this.orderUri = orderUri;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
