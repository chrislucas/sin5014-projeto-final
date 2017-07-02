package com.br.umlrecognizer.utils; /**
 * Created by C.Lucas on 21/05/2017.
 */

import com.br.umlrecognizer.impl.views.ImageViewer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Classe que une as funcoes de {@link BufferedImage}
 * com as classe do OpenCV
 *
 * Ainda estou estudando para descobrir se essa eh a melhor
 * forma de trabalhar com Opencv
 *
 * */
public class BufferedImageUtils {

    /**
     * RGB
     * */
    public static BufferedImage readImage3Channels(File path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(path);
        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        return image;
    }

    /**
     * TODO
     *
     * */
    public static BufferedImage toBinary(BufferedImage image) {
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage result = new BufferedImage(h, w, BufferedImage.TYPE_BYTE_BINARY);
        return result;
    }

    public static BufferedImage toGraysScale(BufferedImage image) {
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage result = new BufferedImage(h, w, BufferedImage.TYPE_BYTE_GRAY);
        return result;
    }

    public static Mat toMat3Channels(BufferedImage bufferedImage) {
        Mat mat = null;
        DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
        byte buffer [] = dataBufferByte.getData();
        mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, buffer);
        return mat;
    }

    public static Mat toMat(BufferedImage bufferedImage, int cvTypeImage) {
        Mat mat = null;
        DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
        byte buffer [] = dataBufferByte.getData();
        mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), cvTypeImage);
        mat.put(0, 0, buffer);
        return mat;
    }

    public static Mat toMat(BufferedImage bufferedImage) {
        int typeImage = bufferedImage.getType();
        Mat mat = null;
        switch (typeImage) {
            case BufferedImage.TYPE_BYTE_GRAY:
                mat = toMat(bufferedImage, CvType.CV_8SC1);
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                mat = toMat(bufferedImage, CvType.CV_8SC3);
                break;
            default:
                mat = toMat(bufferedImage, CvType.CV_8SC3);
        }
        return mat;
    }



    public static BufferedImage toBufferedImage(Mat image) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if(image.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        // dimensao da imagem
        int h = image.rows(), w = image.cols();
        // numero de canais da imagem * (dimensao) = Total de bytes ocupados
        int totalBytes = image.channels() * h * w;
        //(int) (image.total() * image.elemSize());
        byte [] buffer = new byte[totalBytes];
        int g = image.get(0, 0, buffer);
        System.out.println(g);
        BufferedImage bufferedImage = new BufferedImage(w, h, type);
        DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
        byte [] pixels = dataBufferByte.getData();
        System.arraycopy(buffer, 0, pixels, 0, buffer.length);
        return bufferedImage;
    }

    public static boolean writer(String filename, String format,  Mat image) {
        BufferedImage bufferedImage = toBufferedImage(image);
        return writer(filename, format, bufferedImage);
    }

    public static boolean writer(String filename, String format, BufferedImage bufferedImage) {
        File outputFile = new File(filename);
        if( ! outputFile.exists() ) {
            String path = outputFile.getParent();
            new File(path).mkdirs();
        }
        try {
            ImageIO.write(bufferedImage, format, outputFile);
            return true;
        } catch (Exception e) {
            String message = String.format("%s\n%s", e.getMessage(), e.getCause());
            System.out.println(message);
            return false;
        }
    }

    public interface ApplyCallbackImageViewerFilter {
        public Mat executeAndUpdate(ImageViewer imageViewer, Mat mat);
    }

    public interface ApplyOpImageViewer {
        public void op(ImageViewer imageViewer, Mat mat);
    }


    public static void openOnFrame( Mat image, String title) {
        if(image != null) {
            ImageViewer imageView = new ImageViewer();
            imageView.show(image, title);
        }
    }

    public static void openOnFrame(ApplyCallbackImageViewerFilter applyCallbackImageViewerFilter
            , Mat image, String title) {
        if(image != null) {
            ImageViewer imageView = new ImageViewer();
            imageView.show(image, title);
            if(applyCallbackImageViewerFilter != null) {
                applyCallbackImageViewerFilter.executeAndUpdate(imageView, image);
            }
        }
    }


    public static void applyCallbacks(List<ApplyCallbackImageViewerFilter> callbacks, Mat image, String title) {
        if(image != null) {
            ImageViewer imageView = new ImageViewer();
            imageView.show(image, title);
            if (callbacks != null && callbacks.size() > 0) {
                for(ApplyCallbackImageViewerFilter callback : callbacks) {
                    Mat rs  = callback.executeAndUpdate(imageView, image);
                    image   = rs.clone();
                }
            }
        }
    }
}
