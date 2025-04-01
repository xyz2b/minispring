package com.test.service;

public class ConstructorArgService {
    private int i;
    private String s;

    public ConstructorArgService() {

    }

    public ConstructorArgService(int i, String s) {
        this.i = i;
        this.s = s;
        System.out.println(this.i + "," + this.s);
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public void sayHello() {
        System.out.println("Constructor Arg Service says hello");
    }
}
