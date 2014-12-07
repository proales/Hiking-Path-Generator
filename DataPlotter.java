import java.awt.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class DataPlotter{
    static final String FILE_NAME = "Colorado.dat";
    static final int PIXELS_TALL = 480;
    static final int PIXELS_WIDE = 844;

    public static void main(String[] args) throws Exception {
        //Main program routine
        File F = new File(FILE_NAME);
        Scanner S = new Scanner(F);
        int[][] data = readFile(S, PIXELS_TALL, PIXELS_WIDE);
        
        DrawingPanel panel = new DrawingPanel(PIXELS_WIDE, PIXELS_TALL);
        Graphics G = panel.getGraphics();
        
        //Drawing routines
        plotTopo(G, data);
        plotPath(G, data);
    }
    
    public static int[][] readFile(Scanner S, int rowSize, int colSize){
        //Read in the data from a file
        int[][] data = new int[rowSize][colSize];
        for (int rowIndex=0; rowIndex < data.length; rowIndex++){
            for (int colIndex=0; colIndex < data[0].length; colIndex++){
                data[rowIndex][colIndex] = S.nextInt();
            }
        }
        return data;
    }
    
    public static void plotTopo(Graphics G, int[][] data){
        //Plots the Topographic representaiton of the data
        double min=data[0][0];
        double max=data[0][0];
        int grayScaleValue=0;
        
        for (int rowIndex=0; rowIndex < data.length; rowIndex++){
            for (int colIndex=0; colIndex < data[0].length; colIndex++){
                if (data[rowIndex][colIndex] < min) min = data[rowIndex][colIndex];     //Find the min value
                if (data[rowIndex][colIndex] > max) max = data[rowIndex][colIndex];     //Find the max value
            }
        }
        double scale = ((255-0)/(max-min)); //Calculate the display scale
        
        for (int rowIndex=0; rowIndex < data.length; rowIndex++){
            for (int colIndex=0; colIndex < data[0].length; colIndex++){
                //Figure out the gray color to display in each pixel
                grayScaleValue = (int)(scale*(data[rowIndex][colIndex]-min));
                G.setColor(new Color(grayScaleValue,grayScaleValue,30));
                G.fillRect(colIndex,rowIndex,1,1);
            }
        }
    }
    
    public static void plotPath(Graphics G, int[][] data){
        //Plots the hiking path through the terrain
        int min=data[0][0];
        int minIndex=0;
        G.setColor(Color.RED);
        
        //Find the lowest point in the terain on the first column
        for (int rowIndex=0; rowIndex < data.length; rowIndex++){
            if (data[rowIndex][0] < min){
                min = data[rowIndex][0];
                minIndex = rowIndex;
            }
        }
        G.fillRect(0,minIndex,1,1);
        
        //For every other column figure out what the lowest adjacent point is
        int tempIndex=minIndex;
        int lastMin=minIndex;
        int rasterUp=15;
        int rasterOut=1;
        for (int colIndex=1; colIndex < data[0].length-rasterOut; colIndex++){   
            min = 999999;
            if (minIndex-rasterUp == 0){
                //If the path is at the top of the screen dont test one above it
                for (int rowIndex=(minIndex); rowIndex < (minIndex+2); rowIndex++){
                    if (data[rowIndex][colIndex] < min){
                        min = data[rowIndex][colIndex];
                        tempIndex = rowIndex;
                    }
                    if (data[rowIndex][colIndex+1] < min){
                        min = data[rowIndex][colIndex+1];
                        tempIndex = rowIndex;
                    }
                }    
            } else if (minIndex == data.length-rasterUp){
                //If the path is at the bottom of the screen dont test one pixel below it
                for (int rowIndex=(minIndex-1); rowIndex < (minIndex+1); rowIndex++){
                    if (data[rowIndex][colIndex] < min){
                        min = data[rowIndex][colIndex];
                        tempIndex = rowIndex;
                    }
                    if (data[rowIndex][colIndex+1] < min){
                        min = data[rowIndex][colIndex+1];
                        tempIndex = rowIndex;
                    }
                }
            } else {
                //If the path is somewhere in the middle of the screen test forward, above and below in next column
                for (int rowIndex=(minIndex-rasterUp); rowIndex < (minIndex+1+rasterUp); rowIndex++){
                    for (int raster=0; raster<rasterOut;raster++){
                        if (data[rowIndex][colIndex+raster] < min){
                            min = data[rowIndex][colIndex+raster];
                            tempIndex = rowIndex;
                        }
                    }
                }
            }
            if (tempIndex < lastMin) tempIndex = lastMin-1;
            if (tempIndex > lastMin) tempIndex = lastMin+1;
            minIndex = tempIndex;
            //Paint the path on the screen
            if (data[minIndex][colIndex] < data[lastMin][colIndex-1]){
                G.setColor(Color.GREEN);
                G.fillRect(colIndex,minIndex,1,1);
            } else {
                G.setColor(Color.RED);
                G.fillRect(colIndex,minIndex,1,1);
            }
            lastMin = minIndex;
            //Setup the minimum value for the next iteration
        }
            
            
            

            
        
    }
}
