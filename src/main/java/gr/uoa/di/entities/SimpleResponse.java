package gr.uoa.di.entities;

/**
 * Created by Sevle on 10/16/2016.
 */
public class SimpleResponse {


    private String response ="";

    private Boolean ok;

    private Boolean elderly;

    public SimpleResponse() {
    }

    public SimpleResponse(String s) {
        this.response = s;
    }

    public SimpleResponse(String response, Boolean ok, Boolean elderly) {
        this.response = response;
        this.ok = ok;
        this.elderly = elderly;
    }

    public SimpleResponse(String response, Boolean ok) {
        this.response = response;
        this.ok = ok;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public Boolean getElderly() {
        return elderly;
    }

    public void setElderly(Boolean elderly) {
        this.elderly = elderly;
    }

    @Override
    public String toString() {
        return "SimpleResponse{" +
                "response='" + response + '\'' +
                ", ok=" + ok +
                ", elderly=" + elderly +
                '}';
    }
}
