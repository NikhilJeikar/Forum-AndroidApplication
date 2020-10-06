package com.haggle.forum.Holder;

public class PrivateListHolder {
    private String  Text;
    private String Time;
    private String Type;
    private Boolean belong;


    public PrivateListHolder(String text, String time, String type, Boolean belong) {
        Text = text;
        Time = time;
        Type = type;
        this.belong = belong;
    }

    public String getType() {
        return Type;
    }

    public String getText() {
        return Text;
    }

    public String getTime() {
        return Time;
    }

    public Boolean getBelong() {
        return belong;
    }
}
