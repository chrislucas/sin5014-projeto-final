package com.br.umlrecognizer.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by C_Luc on 11/06/2017.
 *
 */
public class MatUtils {

    /**
     * CV_8U, CV_16U, CV_16S, CV_32F or CV_64F.
     * */
    public enum ECvType{

        CV_8U(CvType.CV_8U),
        CV_8UC1(CvType.CV_8UC1);

        private int type;

        ECvType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public static Mat copyMatDimesioon(Mat src) {
        int cols = src.width(), rows = src.height();
        //int ksize = cols * rows;
        Mat dst = new Mat(rows, cols, src.type());
        return dst;
    }

    public static Mat toGrayScale(Mat source) {
        Range rangeRows = new Range(0, source.rows());
        Range rangeCols = new Range(0, source.cols());
        Mat result      = new Mat(source, rangeRows, rangeCols);
        Imgproc.cvtColor(source, result, Imgproc.COLOR_RGB2GRAY);
        return result;
    }

    public static Mat convett(Mat source, ECvType type) {
        Range rangeRows = new Range(0, source.rows());
        Range rangeCols = new Range(0, source.cols());
        Mat result      = new Mat(source, rangeRows, rangeCols);
        Imgproc.cvtColor(source, result, type.getType());
        return result;
    }

    public static Mat toGrayScale(Mat source, ECvType eCvType) {
        Range rangeRows = new Range(0, source.rows());
        Range rangeCols = new Range(0, source.cols());
        Mat result      = new Mat(source.size(), eCvType.getType());
        Imgproc.cvtColor(source, result, Imgproc.COLOR_RGB2GRAY);
        return result;
    }

    public static boolean write(String filename, Mat image) {
        try {
            Imgcodecs.imwrite(filename, image);
            return true;
        } catch (Exception e) {
            String message = String.format("%s\n%s", e.getMessage(), e.getCause());
            System.out.println(message);
            return false;
        }
    }

    public static Mat read(String filename) {
        Mat image = null;
        try {
            image = Imgcodecs.imread(filename);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return image;
    }

    // https://www.tutorialspoint.com/opencv/opencv_storing_images.htm
    private static Mat simpleKernel() {
        Mat kernel = new Mat(3, 3, CvType.CV_8U);
        kernel.setTo(new Scalar(1,1,1));
        kernel.setTo(new Scalar(1,1,1));
        kernel.setTo(new Scalar(1,1,1));
        //System.out.println(kernel.dump());
        return kernel;
    }

    public static Mat buildMatrix(List<Scalar> scalarList, int cvTypeMatrix, Size sizeMatrix) {
        Mat kernel = new Mat(sizeMatrix, cvTypeMatrix);
        for(Scalar scalar : scalarList) {
            kernel.setTo(scalar);
        }
        return kernel;
    }

    public static Mat getKernelFromShapeDefault(int eSize, int eShape) {
        Mat kernel = Imgproc.getStructuringElement(eShape
                , new Size(eSize*2+1, eSize*2+1), new Point(eSize, eSize));
        return kernel;
    }

    public static Mat getKernelFromShape(int eShape, Size size, Point point) {
        Mat kernel = Imgproc.getStructuringElement(eShape, size, point);
        return kernel;
    }

    public static Rect getRect(int x1, int y1, int w, int h) {
        return new Rect(x1, y1, w, h);
    }

    public static Mat cropImage(Mat src, int x1, int y1, int w, int h) {
        return new Mat(src, getRect(x1, y1, w, h));
    }

    public static Mat cropImage(Mat src, Rect rect) {
        return src.submat(rect);
    }

    public static void resizeImage(Mat src, Mat resized, Size sz) {
        Imgproc.resize(src, resized, sz);
    }

}
