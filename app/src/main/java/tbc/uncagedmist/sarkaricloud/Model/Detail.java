package tbc.uncagedmist.sarkaricloud.Model;

import com.google.firebase.firestore.Exclude;

public class Detail {

    private String name, image,id, web;

    public Detail() {
    }

    public Detail(String name, String web) {
        this.name = name;
        this.web = web;
    }

    public Detail(String name, String image, String id, String web) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.web = web;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }
}
