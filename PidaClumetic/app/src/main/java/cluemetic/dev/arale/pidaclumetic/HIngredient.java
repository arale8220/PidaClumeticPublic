package cluemetic.dev.arale.pidaclumetic;

import android.graphics.drawable.Drawable;

public class HIngredient {
    String name;
    Drawable ewg_grade;

    public HIngredient(Drawable ewg_grade, String name) {
        this.name = name;
        this.ewg_grade = ewg_grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return ewg_grade;
    }

    public void setIcon(Drawable ewg_grade) {
        this.ewg_grade = ewg_grade;
    }
}
