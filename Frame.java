package gui;

import static gui.Seqandparallel.imageOutput1;
import static gui.Seqandparallel.imageOutput2;

import java.awt.Color;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author hamza
 */
public class Frame extends JFrame {

    JPanel panel, panel2;
    JLabel label1, label2, label3, label4, label5, label_time;
    JButton b1, b2, save, runTime;
    JFileChooser chooser, save_file;
    File f, image, output;
    int w0, h0, w1, h1;
    JComboBox cb1, cb2, cb3;
    ImageIcon imgThisImg, image_panel2;
    public static int w, h;
    public BufferedImage imageInput = null;
    public BufferedImage imageOutput = null;
    public int size;
    public float matrix[];
    public long startTime, endTime, total_time_seq;
    public int numberOfThreads = 4;
    public kernel threads[] = new kernel[numberOfThreads];
    String method[] = {"Sequential", "PThreads", "Openmp"};
    String size_kernel[] = {"3", "5"};
    String filters[] = {"Gaussian blur", "sharpen", "edge detection", "outline", "eboss", "bottom Sobel"};
    byte[] person_image = null;
    String path;
    String filename = null;
    ByteArrayOutputStream test, test1, test2;
    PrintStream PS;
    PrintStream old;

    public Frame() {
        setSize(1700, 1000);
        this.setLayout(null);
        this.setTitle("Image Processing Program");

        panel = new JPanel();
        panel.setBounds(0, 0, 700, 700);
        panel.setBackground(Color.LIGHT_GRAY);

        panel2 = new JPanel();
        panel2.setBounds(1000, 0, 700, 700);
        panel2.setBackground(Color.LIGHT_GRAY);

        label1 = new JLabel();
        label1.setBounds(0, 0, 700, 700);

        label2 = new JLabel("Select The Method :-");
        label2.setBounds(120, 700, 120, 80);

        label3 = new JLabel("Select the Size of the Kernel :-");
        label3.setBounds(420, 700, 250, 80);

        label4 = new JLabel();
        label4.setBounds(1000, 0, 700, 700);

        label5 = new JLabel("Select Type of Filter :- ");
        label5.setBounds(260, 765, 130, 100);

        label_time = new JLabel();
        label_time.setBounds(1010, 690, 500, 100);

        b1 = new JButton("Browse");
        b1.setBounds(10, 725, 80, 30);
        b1.setBackground(Color.PINK);

        b2 = new JButton("Ok");
        b2.setBounds(720, 725, 80, 30);
        b2.setBackground(Color.PINK);

        runTime = new JButton("Run Time");
        runTime.setBounds(900, 725, 100, 30);
        runTime.setBackground(Color.PINK);

        save = new JButton("Save");
        save.setBounds(1310, 725, 100, 30);
        save.setBackground(Color.pink);

        chooser = new JFileChooser();
        save_file = new JFileChooser();

        cb1 = new JComboBox(method);
        cb1.setBounds(250, 725, 100, 30);
        cb1.setBackground(Color.orange);

        cb2 = new JComboBox(size_kernel);
        cb2.setBounds(600, 725, 100, 30);
        cb2.setBackground(Color.orange);

        cb3 = new JComboBox(filters);
        cb3.setBounds(400, 800, 120, 30);
        cb3.setBackground(Color.orange);

        b1.addActionListener(new execute1());
        b2.addActionListener(new execute2());
        save.addActionListener(new execute3());
        runTime.addActionListener(new execute4());

        this.add(panel);
        panel.add(label1);
        this.add(panel2);
        panel2.add(label4);
        this.add(b1);
        this.add(label2);
        this.add(label3);
        this.add(label5);
        this.add(runTime);
        this.add(label_time);
        this.add(chooser);
        this.add(cb1);
        this.add(cb3);
        this.add(cb2);
        this.add(save);
        this.add(b2);
    }

    public void Kernel(BufferedImage imageInput, BufferedImage imageOutput, float matrix[], int size, int w0, int h0, int w1, int h1) {
        this.imageInput = imageInput;
        this.imageOutput = imageOutput;
        this.matrix = matrix;
        this.size = size;
        this.w0 = w0;
        this.h0 = h0;
        this.w1 = w1;
        this.h1 = h1;
    }

    public void run() {
        int p, a, r, g, b;
        int i, j, x, y;
        int a1, r1, g1, b1;
        int k, radius;

        radius = (size - 1) / 2;
        for (i = w0; i < w1; i++) {
            for (j = h0; j < h1; j++) {
                a1 = r1 = g1 = b1 = 0;
                k = 0;
                if (i >= radius && i < imageInput.getWidth() - radius && j >= radius && j < imageInput.getHeight() - radius) {
                    for (x = i - radius; x <= i + radius; x++) {

                        for (y = j - radius; y <= j + radius; y++) {
                            p = imageInput.getRGB(x, y);

                            r = (p >> 16) & 0xff;
                            g = (p >> 8) & 0xff;
                            b = p & 0xff;
                            r1 += r * matrix[k];
                            g1 += g * matrix[k];
                            b1 += b * matrix[k];
                            k++;
                        }
                    }
                }
                p = 0;
                p = (r1 << 16) | (g1 << 8) | b1;
                imageOutput.setRGB(i, j, p);
            }
        }

    }

    public BufferedImage sequenceImplementation() {
        imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        long startTime = System.currentTimeMillis();
        Kernel(imageInput, imageOutput, matrix, size);
        long endTime = System.currentTimeMillis();
        long total_time_seq = endTime - startTime;
        test = new ByteArrayOutputStream();
        PS = new PrintStream(test);
        old = System.out;
        System.setOut(PS);

        System.out.println("Sequence Kernel processing took " + total_time_seq + " milliseconds " + test.toString());

        return imageOutput;
    }

    public BufferedImage parallelImplementation() {
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
        long total_time_seq = endTime - startTime;
        test1 = new ByteArrayOutputStream();
        PS = new PrintStream(test1);
        old = System.out;
        System.setOut(PS);

        System.out.println("Parallel Kernel processing took " + total_time_seq + " milliseconds " + test1.toString());
        return imageOutput1;
    }

    public BufferedImage openmpImplementation() {
        // #pragma Jamp parallel
        imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        threads[0] = new kernel(imageInput, imageOutput2, matrix, size, 0, 0, w / 2, h / 2);
        threads[1] = new kernel(imageInput, imageOutput2, matrix, size, w / 2, 0, w, h / 2);
        threads[2] = new kernel(imageInput, imageOutput2, matrix, size, 0, h / 2, w / 2, h);
        threads[3] = new kernel(imageInput, imageOutput2, matrix, size, w / 2, h / 2, w, h);

        startTime = System.currentTimeMillis();
        //pragma jamp for reduction 
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        while (threads[0].isAlive() || threads[1].isAlive() || threads[2].isAlive() || threads[3].isAlive()) {
        }
        endTime = System.currentTimeMillis();
        long total_time_seq = endTime - startTime;
        test2 = new ByteArrayOutputStream();
        PS = new PrintStream(test2);
        old = System.out;
        System.setOut(PS);

        System.out.println("Openmp Kernel processing took " + total_time_seq + " milliseconds " + test2.toString());
        return imageOutput2;
    }

    public void Kernel(BufferedImage pic1, BufferedImage pic2, float matrix[], int size) {

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

    public class execute4 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cb1.getSelectedItem().equals("Sequential")) {
                label_time.setText(test.toString());
            } else if (cb1.getSelectedItem().equals("PThreads")) {
                label_time.setText(test1.toString());
            } else if (cb1.getSelectedItem().equals("Openmp")) {
                label_time.setText(test2.toString());
            }

        }

    }

    public class execute3 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            save_file.setFileFilter(new FileNameExtensionFilter("*.png", "png", "*.jpg", "jpg"));
            if (save_file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                f = save_file.getSelectedFile();
                try {

                    if (cb1.getSelectedItem().equals("Sequential")) {
                        ImageIO.write(imageOutput, "jpg", save_file.getSelectedFile());
                        JOptionPane.showMessageDialog(null, "Image File Successfully Loaded");
                    } else if (cb1.getSelectedItem().equals("PThreads")) {
                        ImageIO.write(imageOutput1, "jpg", save_file.getSelectedFile());
                        JOptionPane.showMessageDialog(null, "Image File Successfully Loaded");
                    }

                } catch (HeadlessException | IOException evt) {
                    JOptionPane.showMessageDialog(null, "Failed To load Image File", "Inane error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Failed To load Image File", "Inane error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public class execute1 implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                f = chooser.getSelectedFile();
                path = f + "";
                filename = f.getAbsolutePath();
                imgThisImg = new ImageIcon(new ImageIcon(filename)
                        .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT));
                label1.setIcon(imgThisImg);
                image = new File(filename);
                FileInputStream fis = new FileInputStream(image);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum);
                }
                person_image = bos.toByteArray();
            } catch (HeadlessException | IOException evt) {
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    public class execute2 implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            try {
                imageInput = ImageIO.read(new File(filename));
                w = imageInput.getWidth(null);
                h = imageInput.getHeight(null);

                if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("Gaussian blur")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0.0625f, 0.125f, 0.0625f,
                        0.125f, 0.25f, 0.125f,
                        0.0625f, 0.125f, 0.0625f};
                    size = 3;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("Gaussian blur")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{1 / 256f, 0.015625f, 0.0234375f, 0.015625f, 1 / 256f,
                        0.015625f, 0.0625f, 0.09375f, 0.0625f, 0.015625f,
                        0.0234375f, 0.09375f, 0.140625f, 0.09375f, 0.0234375f,
                        0.015625f, 0.0625f, 0.09375f, 0.0625f, 0.015625f,
                        1 / 256f, 0.015625f, 0.0234375f, 0.015625f, 1 / 256f};
                    size = 5;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));;
                }
                if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("sharpen")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0f, -1f, 0f,
                        -1f, 5f, -1f,
                        0f, -1f, 0f};
                    size = 3;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("sharpen")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{-1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, 25f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f};
                    size = 5;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("edge detection")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{-1f, -1f, -1f,
                        -1f, 8f, -1f,
                        -1f, -1f, -1f};
                    size = 3;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("edge detection")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0, 0, -1, 0, 0,
                        0, 0, -1, 0, 0,
                        0, 0, 4, 0, 0,
                        0, 0, -1, 0, 0,
                        0, 0, -1, 0, 0,};
                    size = 5;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("Gaussian blur")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0.0625f, 0.125f, 0.0625f,
                        0.125f, 0.25f, 0.125f,
                        0.0625f, 0.125f, 0.0625f};
                    size = 3;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("Gaussian blur")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{1 / 256f, 0.015625f, 0.0234375f, 0.015625f, 1 / 256f,
                        0.015625f, 0.0625f, 0.09375f, 0.0625f, 0.015625f,
                        0.0234375f, 0.09375f, 0.140625f, 0.09375f, 0.0234375f,
                        0.015625f, 0.0625f, 0.09375f, 0.0625f, 0.015625f,
                        1 / 256f, 0.015625f, 0.0234375f, 0.015625f, 1 / 256f};
                    size = 5;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("sharpen")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0f, -1f, 0f,
                        -1f, 5f, -1f,
                        0f, -1f, 0f};
                    size = 3;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("sharpen")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{-1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, 25f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f};
                    size = 5;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("edge detection")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{-1f, -1f, -1f,
                        -1f, 8f, -1f,
                        -1f, -1f, -1f};
                    size = 3;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("edge detection")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0, 0, -1, 0, 0,
                        0, 0, -1, 0, 0,
                        0, 0, 4, 0, 0,
                        0, 0, -1, 0, 0,
                        0, 0, -1, 0, 0,};
                    size = 5;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("Gaussian blur")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0.0625f, 0.125f, 0.0625f,
                        0.125f, 0.25f, 0.125f,
                        0.0625f, 0.125f, 0.0625f};
                    size = 3;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("Gaussian blur")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{1 / 256f, 0.015625f, 0.0234375f, 0.015625f, 1 / 256f,
                        0.015625f, 0.0625f, 0.09375f, 0.0625f, 0.015625f,
                        0.0234375f, 0.09375f, 0.140625f, 0.09375f, 0.0234375f,
                        0.015625f, 0.0625f, 0.09375f, 0.0625f, 0.015625f,
                        1 / 256f, 0.015625f, 0.0234375f, 0.015625f, 1 / 256f};
                    size = 5;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("sharpen")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0f, -1f, 0f,
                        -1f, 5f, -1f,
                        0f, -1f, 0f};
                    size = 3;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("sharpen")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{-1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, 25f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f,
                        -1f, -1f, -1f, -1f, -1f};
                    size = 5;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("edge detection")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{-1f, -1f, -1f,
                        -1f, 8f, -1f,
                        -1f, -1f, -1f};
                    size = 3;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                }
                if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("5") && cb3.getSelectedItem().equals("edge detection")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{0, 0, -1, 0, 0,
                        0, 0, -1, 0, 0,
                        0, 0, 4, 0, 0,
                        0, 0, -1, 0, 0,
                        0, 0, -1, 0, 0,};
                    size = 5;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("outline")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -1f, 8f, -1f,
                        -1f, 8f, -1f,
                        -1f, -1f, -1f
                    };
                    size = 3;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("outline")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -1f, 8f, -1f,
                        -1f, 8f, -1f,
                        -1f, -1f, -1f
                    };
                    size = 3;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("outline")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -1f, 8f, -1f,
                        -1f, 8f, -1f,
                        -1f, -1f, -1f
                    };
                    size = 3;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("eboss")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -2f, -1f, 0f,
                        -1f, 1f, 1f,
                        0f, 1f, 2f
                    };
                    size = 3;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("eboss")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -2f, -1f, 0f,
                        -1f, 1f, 1f,
                        0f, 1f, 2f
                    };
                    size = 3;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("eboss")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -2f, -1f, 0f,
                        -1f, 1f, 1f,
                        0f, 1f, 2f
                    };
                    size = 3;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("Sequential") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("bottom Sobel")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -1f, -2f, -1f,
                        0f, 0f, 0f,
                        1f, 2f, 1f
                    };
                    size = 3;

                    imageOutput = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput = sequenceImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("PThreads") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("bottom Sobel")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -1f, -2f, -1f,
                        0f, 0f, 0f,
                        1f, 2f, 1f
                    };
                    size = 3;

                    imageOutput1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput1 = parallelImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput1)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if (cb1.getSelectedItem().equals("Openmp") && cb2.getSelectedItem().equals("3") && cb3.getSelectedItem().equals("bottom Sobel")) {
                    JOptionPane.showMessageDialog(null, "images successfully filtered");
                    matrix = new float[]{
                        -1f, -2f, -1f,
                        0f, 0f, 0f,
                        1f, 2f, 1f
                    };
                    size = 3;

                    imageOutput2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                    imageOutput2 = openmpImplementation();

                    label4.setIcon(new ImageIcon(new ImageIcon(imageOutput2)
                            .getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT)));
                } else if ((cb1.getSelectedItem().equals("Sequential") || cb1.getSelectedItem().equals("PThreads") || cb1.getSelectedItem().equals("Openmp")) && (cb3.getSelectedItem().equals("outline") || cb3.getSelectedItem().equals("eboss") || cb3.getSelectedItem().equals("bottom Sobel")) && cb2.getSelectedItem().equals("5")) {
                    JOptionPane.showMessageDialog(null, "This Filter has only Kernel Size oF 3 ", "Alert", JOptionPane.WARNING_MESSAGE);
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Failed To load Image File", "Inane error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }
}
