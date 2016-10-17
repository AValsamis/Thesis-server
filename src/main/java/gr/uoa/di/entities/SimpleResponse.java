package gr.uoa.di.entities;

/**
 * Created by Sevle on 10/16/2016.
 */
public class SimpleResponse {


    private String response;

    public SimpleResponse(String s) {
        this.response = s;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
