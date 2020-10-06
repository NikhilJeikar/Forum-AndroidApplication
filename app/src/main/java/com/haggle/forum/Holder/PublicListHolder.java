package com.haggle.forum.Holder;

public class PublicListHolder {
    private String DataType, Text , PrivateID , Time;
    private Boolean belong;

    public Boolean getBelong() {
        return belong;
    }

    public PublicListHolder(String DataType, String text, String privateID, String time, Boolean belong) {
        this.DataType = DataType;
        Text = text;
        PrivateID = privateID;
        Time = time;
        this.belong = belong;
    }


    public String getTime() {
        return Time;
    }

    public String getDataType() {
        return DataType;
    }

    public String getText() {
        return Text;
    }

    public String getPrivateID() {
        return PrivateID;
    }
}
