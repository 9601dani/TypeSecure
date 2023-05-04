package com.danimo;

import com.danimo.frames.EditorVIew;

public class Main {
    public static int CONTADOR=0;
    public static void main(String[] args) {
        System.out.println("Hello world!");
        EditorVIew editor= new EditorVIew();
        editor.setVisible(true);
    }
}