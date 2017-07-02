package com.br.umlrecognizer.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by C_Luc on 26/06/2017.
 */
public class Tess4JUtils {

    private static ITesseract instance = null;

    static {
        instance = new Tesseract();
    }

    public static String toString(BufferedImage image) {
        String response = "";
        //instance.setLanguage("eng");
        try {
            File folder = LoadLibs.extractTessResources("tessdata");
            //System.out.println(LoadLibs.getTesseractLibName());
            //File folder = new File("tessdata");
            instance.setDatapath(folder.getPath());
            System.out.println(folder.getPath());
            //TessAPI api = TessAPI.INSTANCE;
            //System.out.println(api.TessVersion());
            //String result = instance.doOCR(file);
            //System.out.println(result);
            response = instance.doOCR(image);
            System.out.println(response);
        }
        catch (TesseractException e) {
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return response;
    }
}
