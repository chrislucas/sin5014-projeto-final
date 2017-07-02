package com.br.umlrecognizer.impl.views;

import com.br.umlrecognizer.utils.BufferedImageUtils;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

/**
 * Created by C.Lucas on 22/05/2017.
 */
public class ImageViewer {

    private JLabel imageView;
    private Mat originalImage, modifiedImage;
    private Stack<Mat> states;
    private Image image;

    /**/
    private void defineImages(Mat matrixImage) {
        this.originalImage = matrixImage.clone();
        this.modifiedImage = matrixImage.clone();
    }

    public void show(Mat matrixImage, String title) {
        setSystenLookAndFeel();
        JFrame frame = build(title);
        defineImages(matrixImage);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                image = BufferedImageUtils.toBufferedImage(matrixImage);
                imageView.setIcon(new ImageIcon(image));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private JFrame build(String title) {
        JFrame frame = new JFrame(title);
        imageView = new JLabel();
        final JScrollPane imageScrollPane = new JScrollPane(imageView);
        imageScrollPane.setPreferredSize(new Dimension(640, 480));
        frame.add(imageScrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        return frame;
    }


    public void updateImage(Mat newMatImage) {
        image = BufferedImageUtils.toBufferedImage(newMatImage);
        imageView.setIcon(new ImageIcon(image));
    }

    private void setSystenLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            printExcpMessage(e);
        } catch (InstantiationException e) {
            printExcpMessage(e);
        } catch (IllegalAccessException e) {
            printExcpMessage(e);
        } catch (UnsupportedLookAndFeelException e) {
            printExcpMessage(e);
        }
    }

    private void printExcpMessage(Exception e) {
        String message = String.format("Error: %s\n%s\n%s", e.getCause()
                , e.getMessage(), e.getLocalizedMessage());
        System.out.println(message);
    }

}
