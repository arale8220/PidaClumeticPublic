package cluemetic.dev.arale.pidaclumetic;

public class ISetupNoticeItem {
    String titleStr, contentStr;
    Boolean clicked;

    public ISetupNoticeItem(String titleStr, String contentStr, Boolean clicked) {
        this.titleStr = titleStr;
        this.contentStr = contentStr;
        this.clicked = clicked;
    }

    public String getTitleStr() {
        return titleStr;
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public Boolean getClicked() {
        return clicked;
    }

    public void setClicked(Boolean clicked) {
        this.clicked = clicked;
    }
}
