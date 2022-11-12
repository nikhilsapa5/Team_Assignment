package edu.northeastern.team_assignment;

public class Message {

    public String sendName;
    public String toName;
    public String imgId;
    public Long timestamp;

    public String getSenderName() {
        return sendName;
    }

    public String getReceiverName() {
        return toName;
    }

    public String getImgId() {
        return imgId;
    }

    public Long getTime(){return timestamp;}
}
