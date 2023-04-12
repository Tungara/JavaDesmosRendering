import jankovicsandras.imagetracer.ImageTracer;
import ij.process.ImageConverter;
import ij.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.File;
import java.util.List;

import static java.lang.Integer.parseInt;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) throws Exception {

        boolean color = false;
        int numColors = 16;

        Desmos_Driver.Setup();

        Desmos_Driver.startState();

        File dir = new File("path to the run file");
        File[] files = (dir.listFiles());
        int fileIndex = 0;
        for (File file : files) {
            String path = file.getPath();

            // Image Loading
            ImagePlus imp = IJ.openImage(path);

            if (imp == null) {
                IJ.error("Could not open image");
            }




            Desmos_Driver.setViewport(10, -1*imp.getHeight()+10,-10,imp.getWidth()+10);



            // Color is not finished
            if (color) {
                // Color Quantization
                ImageConverter simplifier = new ImageConverter(imp);
                simplifier.convertRGBtoIndexedColor(numColors);

                IJ.save(imp, "output.png");
                ImagePlus imp2 = IJ.openImage("output.png");

                imp2.show();

                // Palette Creation
                Color_Counter counter = new Color_Counter();
                counter.setup("", imp2);
                String[] colors = counter.run(imp2.getProcessor(), numColors);

                for (int i = 0; i < colors.length; i++) {
                    if (!(colors[i].length() == 6)) {
                        colors[i] = "0".repeat(6 - colors[i].length()) + colors[i];
                    }
                }

                HashMap<String, Float> options = new HashMap<String, Float>();
                options.put("numberofcolors", (float) numColors);
                options.put("blurradius", 0f);

                byte[][] palette = new byte[numColors][4];
                for (int colorcnt = 0; colorcnt < numColors; colorcnt++) {
                    palette[colorcnt][0] = (byte) (-128 + parseInt(colors[colorcnt].substring(0, 2), 16)); // R
                    palette[colorcnt][1] = (byte) (-128 + parseInt(colors[colorcnt].substring(2, 4), 16)); // G
                    palette[colorcnt][2] = (byte) (-128 + parseInt(colors[colorcnt].substring(4), 16)); // B
                    palette[colorcnt][3] = (byte) 127;              // A
                }


                //ImageTracer.saveString(
                //        "output.svg" ,
                String vectors = (ImageTracer.imageToSVG("output.png", options, palette));
                //);
            }

            else {
                imp.show();
                Canny_Edge_Detector edgesGetter = new Canny_Edge_Detector();
                edgesGetter.run("");

                IJ.save(imp, "output.png");
                imp.close();
                ImagePlus imp2 = IJ.openImage("output.png");

                HashMap<String, Float> options = new HashMap<String, Float>();
                options.put("blurradius", 0f);
                options.put("desc", 0f);

                String vectors = ImageTracer.imageToSVG("output.png", options, null);

                ArrayList<Integer> indices = new ArrayList<Integer>();

                int index = vectors.indexOf("d=");
                while (index >= 0) {
                    indices.add(index);
                    index = vectors.indexOf("d=", index + 1);
                }

                for (int i = 0; i < indices.size(); i++) {
                    String section = vectors.substring(indices.get(i) + 3, vectors.indexOf('"', indices.get(i) + 3));

                    // Lines
                    int j = section.indexOf("L");
                    while (j >= 0) {
                        String filtered = section.replaceAll("L", "-");
                        filtered = filtered.replaceAll("M", "-");
                        filtered = filtered.replaceAll("Z", "-");
                        filtered = filtered.replaceAll("Q", "-");
                        filtered = filtered.substring(filtered.lastIndexOf("-", j - 1) + 2, filtered.indexOf("-", j + 1));
                        filtered = filtered.replaceAll("[^.?0-9]+", " ");
                        List<String> cords = (Arrays.asList(filtered.trim().split(" ")));

                        String latex;
                        if (cords.size() == 6) {
                            latex = "(1-t)^2(" + cords.get(2) + ",-" + cords.get(3) + ") + 2(1-t)(t)(" + cords.get(2) + ",-" + cords.get(3) + ")+t^2(" + cords.get(4) + ",-" + cords.get(5) + ")";
                        } else {
                            latex = "(1-t)^2(" + cords.get(0) + ",-" + cords.get(1) + ") + 2(1-t)(t)(" + cords.get(0) + ",-" + cords.get(1) + ")+t^2(" + cords.get(2) + ",-" + cords.get(3) + ")";
                        }
                        Desmos_Driver.graphLatex(latex);

                        j = section.indexOf("L", j + 1);
                    }

                    // Quadratic Bezier Curves
                    j = section.indexOf("Q");
                    while (j >= 0) {
                        String filtered = section.replaceAll("L", "-");
                        filtered = filtered.replaceAll("M", "-");
                        filtered = filtered.replaceAll("Z", "-");
                        filtered = filtered.replaceAll("Q", "-");
                        filtered = filtered.substring(filtered.lastIndexOf("-", j - 1) + 2, filtered.indexOf("-", j + 1));
                        filtered = filtered.replaceAll("[^.?0-9]+", " ");
                        List<String> cords = (Arrays.asList(filtered.trim().split(" ")));

                        String latex;
                        if (cords.size() == 6) {
                            latex = "(1-t)^2(" + cords.get(0) + ",-" + cords.get(1) + ") + 2(1-t)(t)(" + cords.get(2) + ",-" + cords.get(3) + ")+t^2(" + cords.get(4) + ",-" + cords.get(5) + ")";
                        } else {
                            latex = "(1-t)^2(" + cords.get(2) + ",-" + cords.get(3) + ") + 2(1-t)(t)(" + cords.get(4) + ",-" + cords.get(5) + ")+t^2(" + cords.get(6) + ",-" + cords.get(7) + ")";
                        }
                        Desmos_Driver.graphLatex(latex);

                        j = section.indexOf("Q", j + 1);
                    }
                }
            }

            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "jpg", new File(path));

            Desmos_Driver.Reset();
        }
    }
}


// Only show certain Colors
// Pass 1-color only images to vectorizer
// Get svg equations
// Send them to Desmos
