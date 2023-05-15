package com.danimo;

import com.danimo.frames.EditorVIew;
import com.danimo.manageError.ObjectErr;

import java.util.ArrayList;

public class Main {
    public static int CONTADOR=0;
    public static ArrayList<String> view_console= new ArrayList<>();
    public static void main(String[] args) {
        System.out.println("Hello world!");
        EditorVIew editor= new EditorVIew();
        editor.setVisible(true);
        /*String greeting="Hello"+ " world";
        String message2 = "El resultado es: " + greeting.equals( "Hello world");
        System.out.println(message2);*/
        int variable=3;
        while(variable>0){
            if(variable==3){
                break;
            }
            variable--;
        }
        System.out.println(variable);
    }
}