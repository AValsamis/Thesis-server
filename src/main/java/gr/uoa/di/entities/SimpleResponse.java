package gr.uoa.di.entities;

/**
 * Created by Sevle on 10/16/2016.
 */
public class SimpleResponse {


    private String response ="";

    private Boolean isOk;

    private Boolean isElderly;

    public SimpleResponse(String s) {
        this.response = s;
    }

    public SimpleResponse(String response, Boolean isOk, Boolean isElderly) {
        this.response = response;
        this.isOk = isOk;
        this.isElderly = isElderly;
    }

    public SimpleResponse(String response, Boolean isOk) {
        this.response = response;
        this.isOk = isOk;
    }

    public Boolean getOk() {
        return isOk;
    }

    public void setOk(Boolean ok) {
        isOk = ok;
    }

    public Boolean getElderly() {
        return isElderly;
    }

    public void setElderly(Boolean elderly) {
        isElderly = elderly;
    }

    public Boolean getIsOk() {
        return isOk;
    }

    public void setIsOk(Boolean ok) {
        isOk = ok;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
