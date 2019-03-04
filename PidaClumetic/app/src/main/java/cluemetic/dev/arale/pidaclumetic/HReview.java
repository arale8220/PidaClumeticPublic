package cluemetic.dev.arale.pidaclumetic;

public class HReview {
    String owner, content;

    public HReview(String owner, String content) {
        this.owner = owner;
        this.content = content;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
