package com.haggle.forum.Holder;

public class FeedListHolder {
    private String Name;
    private String Count;
    private String Type;
    private String RandomId;
    private String DpID;

    public FeedListHolder(String name, String count, String type, String randomId, String dpID) {
        Name = name;
        Count = count;
        Type = type;
        RandomId = randomId;
        DpID = dpID;
    }

    public String getDpID() {
        return DpID;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getName() {
        return Name;
    }

    public String getCount() {
        return Count;
    }

    public String getType() {
        return Type;
    }

    public String getRandomId() {
        return RandomId;
    }
}
