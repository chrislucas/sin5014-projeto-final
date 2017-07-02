package com.br.umlrecognizer.impl.views;


import com.br.umlrecognizer.utils.BufferedImageUtils;
import com.br.umlrecognizer.utils.MatUtils;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by C_Luc on 24/06/2017.
 */
public class UIContours {

    private static final String onFillString        = "On";
    private static final String offFillString       = "Off";
    private static final String boundingBoxString   = "Pintar o contorno";
    private static final String circleString        = "Envolucro circular";
    private static final String convexHullString    = "Envolucro Poligonar";
    private JLabel imageView;
    private JLabel binaryView;
    private String windowName;
    private Mat originalImage;
    private Mat image;
    private Mat binary = new Mat();


    private String fillFlag         = offFillString;
    private String enclosingType    = boundingBoxString;
    private int imageThreshold      = 125;
    private int areaThreshold       = 500;


    public UIContours(String title, Mat image) {
        this.windowName     = title;
        this.image          = image;
        this.originalImage  = image.clone();
        this.binary         = new Mat(new Size(image.width(), image.height()), CvType.CV_8UC1);
    }

    public void show() {
        setSystenLookAndFeel();
        JFrame frame = buildFrame(this.windowName);
        updateView();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.pack();
                frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                frame.setLocationRelativeTo(null);
                frame.setResizable(true);
                frame.setVisible(true);
                // carregando a imagem binarizada
                processOperation();
            }
        });
    }

    /**
     * Criando a tela
     *
     * */
    private JFrame buildFrame(String windowName) {
        JFrame frame = new JFrame(windowName);
        frame.setLayout(new GridBagLayout());
        setupFillRadio(frame);
        setupShapeRadio(frame);
        setupThresholdSlider(frame);
        setupBoundingBox(frame);
        setupResetButton(frame);
        setupImage(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private void resetImage() {
        image = originalImage.clone();
        Mat temp = new Mat();
        // conversao em nivel de cinza
        Imgproc.cvtColor(image, temp,Imgproc.COLOR_BGR2GRAY);
        // kimiarizacao
        Imgproc.threshold(temp, binary, imageThreshold, 255.0, Imgproc.THRESH_BINARY_INV);
        // atualizacao das JLabels
        updateView();
    }

    private void applyBlur() {
        // atualizacao das JLabels
        updateView();
    }

    private void setupResetButton(JFrame frame) {
        JButton resetButton = new JButton("Desenhar o contorno");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                drawContours();
            }
        });
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        frame.add(resetButton,c);
    }

    protected void drawContours() {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat copyBinImage = binary.clone();
        /**
         * Para desenhar um poligono com preenchimento -1, senao 2
         * */
        int thickness = ( fillFlag.equals(onFillString) )? -1 : 2;
        Mat h = new Mat();
        Imgproc.findContours(copyBinImage, contours, h
                , Imgproc.CHAIN_APPROX_NONE, Imgproc.CHAIN_APPROX_SIMPLE);

        /*
        System.out.printf("Q. Contornos %d\n", contours.size());
        System.out.printf("%s\n", h.dump());
        */
        for(int i=0; i<contours.size(); i++){
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);

            /*
            System.out.println(currentContour.dump());
            System.out.printf("Pontos: %f\n Area: %f"
                    ,currentContour.size().height, currentArea);
            */

            if(currentArea > areaThreshold){
                Scalar rgb = new Scalar(0,0,255);
                Imgproc.drawContours(image, contours, i,  rgb, thickness);
                if(boundingBoxString.equals(enclosingType)){
                    drawBoundingBox(currentContour, thickness);
                }
                /*
                else if (circleString.equals(enclosingType)){
                    drawEnclosingCircle(currentContour);
                }
                else if (convexHullString.equals(enclosingType)){
                    drawConvexHull(currentContour);
                }
                */
            }
            else{
                Scalar rgb = new Scalar(0,0,255);
                // na funcao drawContours o parametro scalar e um vetor de tres elementos que representa a cor
                // em rgb
                Imgproc.drawContours(image, contours, i, rgb, thickness);
            }
        }
        updateView();
    }

    private void setupThresholdSlider(JFrame frame) {
        JLabel sliderLabel = new JLabel("Threshold:", JLabel.RIGHT);
        int minimum = 0;
        int maximum = 255;
        int initial =imageThreshold;
        JSlider thresholdSlider = new JSlider(JSlider.HORIZONTAL,
                minimum, maximum, initial);

        thresholdSlider.setMajorTickSpacing(25);
        thresholdSlider.setMinorTickSpacing(5);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setPaintLabels(true);
        thresholdSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                imageThreshold = (int)source.getValue();
                // TODO
                // Atualizar imagens
                processOperation();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        frame.add(sliderLabel,c);
        c.gridx = 1;
        c.gridy = 2;
        frame.add(thresholdSlider,c);
    }

    private void setupBoundingBox(JFrame frame) {
        JLabel sliderLabel = new JLabel("Área mínima:", JLabel.RIGHT);
        int minimum = 0;
        int maximum = 10000;
        int initial = 500;

        JSlider areaSlider = new JSlider(JSlider.HORIZONTAL,
                minimum, maximum, initial);
        areaSlider.setMajorTickSpacing(1000);
        areaSlider.setMinorTickSpacing(250);
        areaSlider.setPaintTicks(true);
        areaSlider.setPaintLabels(true);
        areaSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source  = (JSlider)e.getSource();
                areaThreshold   = (int)source.getValue();
                // TODO
                // Atualizar imagens
                processOperation();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 3;
        frame.add(sliderLabel,c);

        c.gridx = 1;
        c.gridy = 3;
        frame.add(areaSlider,c);
    }

    private void setupShapeRadio(JFrame frame) {
        JRadioButton boundingBoxButton = new JRadioButton(boundingBoxString);
        boundingBoxButton.setMnemonic(KeyEvent.VK_B);
        boundingBoxButton.setActionCommand(boundingBoxString);
        boundingBoxButton.setSelected(true);
/*
        JRadioButton circleButton = new JRadioButton(circleString);
        circleButton.setMnemonic(KeyEvent.VK_C);
        circleButton.setActionCommand(circleString);
        circleButton.setSelected(false);

        JRadioButton convexHullButton = new JRadioButton(convexHullString);
        convexHullButton.setMnemonic(KeyEvent.VK_H);
        convexHullButton.setActionCommand(convexHullString);
        convexHullButton.setSelected(false);
*/
        ButtonGroup group = new ButtonGroup();
        group.add(boundingBoxButton);
        //group.add(circleButton);
        //group.add(convexHullButton);

        ActionListener operationChangeListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                enclosingType  = event.getActionCommand();
                processOperation();
            }
        };

        boundingBoxButton.addActionListener(operationChangeListener);
        //circleButton.addActionListener(operationChangeListener);
        //convexHullButton.addActionListener(operationChangeListener);

        GridLayout gridRowLayout    = new GridLayout(1,0);
        JPanel radioOperationPanel  = new JPanel(gridRowLayout);
        JLabel rangeLabel           = new JLabel("Contorno:", JLabel.RIGHT);

        radioOperationPanel.add(boundingBoxButton);
        //radioOperationPanel.add(circleButton);
        //radioOperationPanel.add(convexHullButton);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.add(rangeLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        frame.add(radioOperationPanel,c);
    }

    private void setupFillRadio(JFrame frame){
        JRadioButton onButton = new JRadioButton(onFillString);
        onButton.setMnemonic(KeyEvent.VK_O);
        onButton.setActionCommand(onFillString);
        onButton.setSelected(false);

        JRadioButton offButton = new JRadioButton(offFillString);
        offButton.setMnemonic(KeyEvent.VK_F);
        offButton.setActionCommand(offFillString);
        offButton.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(onButton);
        group.add(offButton);

        ActionListener operationChangeListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fillFlag = event.getActionCommand();
                processOperation();
            }
        };

        onButton.addActionListener(operationChangeListener);
        offButton.addActionListener(operationChangeListener);

        GridLayout gridRowLayout    = new GridLayout(1,2);
        JPanel radioOperationPanel  = new JPanel(gridRowLayout);
        JLabel fillLabel = new JLabel("Contorno:", JLabel.RIGHT);
        radioOperationPanel.add(onButton);
        radioOperationPanel.add(offButton);
        GridBagConstraints c = new GridBagConstraints();
        c.fill  = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        frame.add(fillLabel,c);
        c.gridx = 1;
        c.gridy = 0;
        frame.add(radioOperationPanel,c);
    }

    private void setupImage(JFrame frame) {
        imageView   = new JLabel();
        binaryView  = new JLabel();
        GridBagConstraints c = new GridBagConstraints();
        c.fill  = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        frame.add(new JLabel("Original", JLabel.CENTER),c);
        c.gridx = 1;
        frame.add(new JLabel("Binarizada", JLabel.CENTER),c);
        c.gridy = 6;
        c.gridx = 0;
        c.gridwidth = 1;
        frame.add(imageView,c);
        c.gridx = 1;
        frame.add(binaryView,c);
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

    private void updateView() {
        Mat bw = MatUtils.toGrayScale(image);
        Image outputImage = BufferedImageUtils.toBufferedImage(bw);
        Image binaryImage = BufferedImageUtils.toBufferedImage(binary);
        imageView.setIcon(new ImageIcon(outputImage));
        binaryView.setIcon(new ImageIcon(binaryImage));
    }

    /**
     * Ao mudar o threeshold a imagem eh resetada
     * Ao mudar o threeshold da area eh resetada
     * ao clicar num dos radios
     * */
    private void processOperation() {
        resetImage();
    }

    private void drawBoundingBox(MatOfPoint currentContour, int thickness) {
        Rect rectangle = Imgproc.boundingRect(currentContour);
        /**
         * Na funcao rectangle o vetor Scalar com 3 elementos representa as colores no padrao
         * bgr
         * */
        Scalar bgr = new Scalar(255,0,0);
        Mat bw = MatUtils.toGrayScale(image);
        Imgproc.rectangle(image, rectangle.tl(), rectangle.br(), bgr , thickness);
    }


    private void drawEnclosingCircle(MatOfPoint currentContour) {
        float[] radius = new float[1];
        Point center = new Point();
        MatOfPoint2f currentContour2f = new MatOfPoint2f();
        currentContour.convertTo(currentContour2f, CvType.CV_32FC2);
        Imgproc.minEnclosingCircle(currentContour2f, center, radius);
        Imgproc.circle(image, center, (int) radius[0], new Scalar(255,0,0));
    }

    private void drawConvexHull(MatOfPoint currentContour) {
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(currentContour, hull);
        List<MatOfPoint> hullContours = new ArrayList<MatOfPoint>();
        MatOfPoint hullMat = new MatOfPoint();
        hullMat.create((int)hull.size().height,1,CvType.CV_32SC2);
        for(int j = 0; j < hull.size().height ; j++)
        {
            int index = (int)hull.get(j, 0)[0];
            double[] point = new double[] {
                    currentContour.get(index, 0)[0], currentContour.get(index, 0)[1]
            };
            hullMat.put(j, 0, point);
        }
        hullContours.add(hullMat);
        Imgproc.drawContours(image, hullContours, 0, new Scalar(128,0,0), 2);
    }

}
