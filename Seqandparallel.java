/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author hamza
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Seqandparallel extends Thread {

    public static int w, h;
    public static String imageName = null;
    public static BufferedImage imageInput = null;
    public static BufferedImage imageOutput = null;
    public static BufferedImage imageOutput1 = null;
    public static BufferedImage imageOutput2 = null;
    public static int size;
    public static int z;
    public static float matrix[];
    public static long startTime, endTime;
    public static int numberOfThreads = 4;
    public static kernel threads[] = new kernel[numberOfThreads];

    public static BufferedImage sequenceImplementation() {
        imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        long startTime = System.currentTimeMillis();
        Kernel(imageInput, imageOutput, matrix, size);
        long endTime = System.currentTimeMillis();

        return imageOutput;
    }

    public static BufferedImage parallelImplementation() {
        imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        threads[0] = new kernel(imageInput, imageOutput1, matrix, size, 0, 0, w / 2, h / 2);
        threads[1] = new kernel(imageInput, imageOutput1, matrix, size, w / 2, 0, w, h / 2);
        threads[2] = new kernel(imageInput, imageOutput1, matrix, size, 0, h / 2, w / 2, h);
        threads[3] = new kernel(imageInput, imageOutput1, matrix, size, w / 2, h / 2, w, h);

        startTime = System.currentTimeMillis();
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        while (threads[0].isAlive() || threads[1].isAlive() || threads[2].isAlive() || threads[3].isAlive()) {
        }
        endTime = System.currentTimeMillis();

        return imageOutput1;
    }

    public static BufferedImage openmpImplementation() {
        imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        threads[0] = new kernel(imageInput, imageOutput2, matrix, size, 0, 0, w / 2, h / 2);
        threads[1] = new kernel(imageInput, imageOutput2, matrix, size, w / 2, 0, w, h / 2);
        threads[2] = new kernel(imageInput, imageOutput2, matrix, size, 0, h / 2, w / 2, h);
        threads[3] = new kernel(imageInput, imageOutput2, matrix, size, w / 2, h / 2, w, h);

        startTime = System.currentTimeMillis();
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        while (threads[0].isAlive() || threads[1].isAlive() || threads[2].isAlive() || threads[3].isAlive()) {
        }
        endTime = System.currentTimeMillis();

        return imageOutput2;
    }

    public static void Kernel(BufferedImage pic1, BufferedImage pic2, float matrix[], int size) {

        int p, a, r, g, b;
        int i, j, x, y;
        int a1, r1, g1, b1;
        int k, radius;

        radius = (size - 1) / 2;
        w = pic1.getWidth();
        h = pic1.getHeight();
        for (i = radius; i < w - radius; i++) {
            for (j = radius; j < h - radius; j++) {
                a1 = r1 = g1 = b1 = 0;
                k = 0;
                for (x = i - radius; x <= i + radius; x++) {

                    for (y = j - radius; y <= j + radius; y++) {
                        p = pic1.getRGB(x, y);
                        r = (p >> 16) & 0xff;
                        g = (p >> 8) & 0xff;
                        b = p & 0xff;
                        r1 += r * matrix[k];
                        g1 += g * matrix[k];
                        b1 += b * matrix[k];
                        k++;
                    }
                }

                p = 0;
                p = (r1 << 16) | (g1 << 8) | b1;
                pic2.setRGB(i, j, p);
            }
        }

    }

}
