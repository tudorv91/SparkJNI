package org.tudelft.ewi.ceng.examples.convolutionFramework;

import java.util.ArrayList;

public class Utils{

  public static final int KERNEL_SMOOTH = 1;
  public static final int KERNEL_GAUSSIAN = 2;

  /*
  * Generate convolution matrix. For the moment only generate smooth mat.
  */
  public static int[][] genConvMat(int conv_type, int mat_size){
    int[][] convMat = new int[mat_size][mat_size];
    if(mat_size % 2 == 0)

      throw new UnsupportedOperationException();
    for(int i = 0; i < mat_size; i++)
      for(int j = 0; j < mat_size; j++)
        convMat[i][j] = 1;

    return convMat;
  }

  public static int[] processCPU(int[] v_in, int width, int height, int[][] kernel){
  int[] procImg = new int[width * height];
  int KERNEL_SIZE = kernel.length,
    KERNEL_FULL_SIZE = KERNEL_SIZE * KERNEL_SIZE;

  System.out.println("##### >>>>> KERNEL FULL SIZE:"+KERNEL_FULL_SIZE);

  int startPosXY = (KERNEL_SIZE - 1) / 2,
    endPosY = height - (KERNEL_SIZE - 1) / 2,
    endPosX = width - (KERNEL_SIZE - 1) / 2; 

  for (int i = startPosXY; i < endPosY; i++) {
    for (int j = startPosXY; j < endPosX; j++) {
      int currIdx = i * width + j;
      procImg[currIdx] = 0;

      for (int k_i = -((KERNEL_SIZE - 1) / 2); k_i <= ((KERNEL_SIZE - 1) / 2); k_i++) {
        for (int k_j = -((KERNEL_SIZE - 1) / 2); k_j <= ((KERNEL_SIZE - 1) / 2); k_j++) {
          int currIdxIn = (i + k_i) * width + k_j + j;
          procImg[currIdx] += v_in[currIdxIn] * kernel[((KERNEL_SIZE - 1) / 2) + k_i][((KERNEL_SIZE - 1) / 2) + k_j];
        }
      }
      procImg[currIdx] = procImg[currIdx] / KERNEL_FULL_SIZE;
    }
  }
    return procImg;
  }

  public static ArrayList<PrimContainer> genImage(int width, int height, int noSlices) {
    // create dummy image (padding it with zeros on all borders
    ArrayList<PrimContainer> images = new ArrayList<PrimContainer>();

    int counter = 0;

    int[] image = new int[width * height];

    for(int i = 0; i < height; i++){
      for(int j = 0; j < width; j++)
        image[i * width + j] = counter++;
    }

    images.add(new PrimContainer(image, true));

    return images;
  }
}