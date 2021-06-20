package com.example;

public class Rect {
	private String id;
    private String name;
    private int width;
    private int height;
    private String color;

    public String getID() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }

    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public String getColor() {
        return this.color;
    }
    
    public void setID(String f) {
        this.id = f;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setWidth(int w) {
        this.width = w;
    }
    
    public void setHeight(int w) {
        this.height = w;
    }
    
    public void setColor(String c) {
        this.color = c;
    }
}