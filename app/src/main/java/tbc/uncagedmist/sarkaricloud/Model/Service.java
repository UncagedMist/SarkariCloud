package tbc.uncagedmist.sarkaricloud.Model;

import com.google.firebase.firestore.Exclude;

public class Service {

    private String id,name, image;

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
