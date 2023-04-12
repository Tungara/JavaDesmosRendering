import ij.*;
import ij.process.*;
import ij.plugin.filter.*;

/** This plugin counts the number of unique colors in an RGB image. Displays
 the pixels counts for each of the colors if there are no more than 64 colors.  */
public class Color_Counter {
    ImagePlus imp;
    int colors;
    static final int MAX_COLORS = 16777216;
    int[] counts = new int[MAX_COLORS];
    int slice;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return 0;//DOES_RGB+NO_UNDO+NO_CHANGES+DOES_STACKS;
    }

    public String[] run(ImageProcessor ip, int numColors) {
        String[] colorsList = new String[numColors];
        int index= 0;
        int[] pixels = (int[]) ip.getPixels();
        for (int i = 0; i < pixels.length; i++)
            counts[pixels[i] & 0xffffff]++;
        if (++slice == imp.getStackSize()) {
            for (int i = 0; i < MAX_COLORS; i++) {
                if (counts[i] > 0) colors++;
            }
            //IJ.log("Unique colors: " + colors);
            if (colors <= 64) {
                for (int i = 0; i < MAX_COLORS; i++) {
                    if (counts[i] > 0) {
                        colorsList[index] = Integer.toHexString(i);
                        index++;
                    }
                }
            }
        }
        return colorsList;
    }
}