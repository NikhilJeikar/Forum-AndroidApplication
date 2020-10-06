package com.haggle.forum.Holder;

public class InListHolder {
    private String Name;
    private String RandomId;
    private String Stream;
    private String DpID;

    public InListHolder(String name, String randomId, String stream, String dpID) {
        Name = name;
        RandomId = randomId;
        Stream = stream;
        DpID = dpID;
    }

    public String getStream() {
        return Stream;
    }

    public String getName() {
        return Name;
    }

    public String getDpID() {
        return DpID;
    }

    public String getRandomId() {
        return RandomId;
    }

}
