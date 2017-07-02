package com.br.umlrecognizer;

import org.opencv.core.Core;

/**
 * Created by C_Luc on 02/07/2017.
 */
public class Main {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        System.out.println(Core.VERSION);
    }

}
