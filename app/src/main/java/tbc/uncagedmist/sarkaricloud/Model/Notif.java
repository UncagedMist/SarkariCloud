package tbc.uncagedmist.sarkaricloud.Model;

public class Notif {

    private String app_id,included_segments,data,contents;


    public Notif(String app_id, String included_segments, String data, String contents) {
        this.app_id = app_id;
        this.included_segments = included_segments;
        this.data = data;
        this.contents = contents;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getIncluded_segments() {
        return included_segments;
    }

    public void setIncluded_segments(String included_segments) {
        this.included_segments = included_segments;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
