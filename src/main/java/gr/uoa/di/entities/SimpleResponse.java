package gr.uoa.di.entities;

/**
 * Created by Sevle on 10/16/2016.
 */
public class SimpleResponse {


    private String response ="";

    private Boolean isOk;

    public SimpleResponse(String s) {
        this.response = s;
    }

    public SimpleResponse(String response, Boolean isOk) {
        this.response = response;
        this.isOk = isOk;
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
