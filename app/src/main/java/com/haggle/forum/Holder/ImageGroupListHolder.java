package com.haggle.forum.Holder;

import java.util.ArrayList;

public class ImageGroupListHolder {
    private String Name;
    private ArrayList<String> Source;

    public ImageGroupListHolder(String name, ArrayList<String> source) {
        Name = name;
        Source = source;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<String> getSource() {
        return Source;
    }
}
