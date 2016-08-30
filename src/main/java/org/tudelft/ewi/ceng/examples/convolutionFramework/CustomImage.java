package org.tudelft.ewi.ceng.examples.convolutionFramework;

import org.apache.spark.input.PortableDataStream;
import org.tudelft.ewi.ceng.annotations.JNI_class;
import org.tudelft.ewi.ceng.annotations.JNI_field;
import org.tudelft.ewi.ceng.annotations.JNI_method;
import org.tudelft.ewi.ceng.annotations.JNI_param;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.Serializable;

@JNI_class public class CustomImage implements Serializable {
    @JNI_field()
    int width;
    @JNI_field()
    int height;
    @JNI_field()
    int[] data;
    @JNI_field()
    int[] kernel;
    String originalFileName;
    String destinationFileName;
    transient BufferedImage outputImg;
    long execTime;
    public boolean isValidImg = true;

    public static volatile Object lock = new Object();

    /**
     * Creates a custom image based on an Spark Input Data Stream.
     */
    public CustomImage(PortableDataStream imgDataStream, String origin) {
        BufferedImage img;
        originalFileName = origin.split("/")[origin.split("/").length - 1];
        if (!origin.endsWith(".jpg")) {
            isValidImg = false;
            return;
        }

        long start = System.currentTimeMillis();

        try {
            destinationFileName = originalFileName.substring(0, originalFileName.length() - 4) + "_proc.jpg";

            img = ImageIO.read(imgDataStream.open());

            height = img.getHeight();
            width = img.getWidth();
            // System.out.println("\nHEIGHT:"+height+" WIDTH:"+width);

            int imgSize = height * width;
            Raster imgRaster = img.getData();

            data = imgRaster.getSamples(0, 0, width, height, 0, new int[imgSize]);

            imgDataStream.close();
            // System.out.println("after closing dataStream");
        } catch (javax.imageio.IIOException un) {
            System.out.println("unsupported image exception:" + origin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        execTime = System.currentTimeMillis() - start;
    }

    @JNI_method(target = "constructor")
    public CustomImage(@JNI_param(target = "data") int[] data,
                       @JNI_param(target = "width") int width,
                       @JNI_param(target = "height") int height) {
        this.data = data;
        this.height = height;
        this.width = width;
    }

    public CustomImage(int[] data, int width, int height, int[] kernel) {
        this.data = data;
        this.height = height;
        this.width = width;
        this.kernel = kernel;
    }

    public void setDestinationFn(String fn) {
        destinationFileName = fn;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int[] getDataArr() {
        return data;
    }

    public void setDataArr(int[] dataIn) {
        data = dataIn;
    }

    public String getDestinationFn() {
        return destinationFileName;
    }

    public void setBuffImg(BufferedImage img) {
        outputImg = img;
    }

    public BufferedImage getBuffImg() {
        return outputImg;
    }

    public void setExecutionTime(long execTime) {
        this.execTime = execTime;
    }

    public long getExecTime() {
        return execTime;
    }
}