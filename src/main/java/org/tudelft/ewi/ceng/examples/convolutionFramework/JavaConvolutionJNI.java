package org.tudelft.ewi.ceng.examples.convolutionFramework;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.input.PortableDataStream;
import org.tudelft.ewi.ceng.JniFrameworkLoader;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tudor on 4/11/16.
 */

public class JavaConvolutionJNI{
	final static int FPGA_MODE = 1;
	final static int kernel_size = 11;
    final static int noOfImgs = 64;

	static int inputBatchImageSize;
	final static int CPU_MODE = 2;
	final static String libName = "libConvolution.so";
	final static String nativeLibPath = "/user/tvoicu/" + libName;
	final static String APP_NAME = "JavaConvolution";
	final static String HDFS_PATH = "hdfs://storage-host.cluster:9000";
	static String IMG_FOLDER = "sampleImgs/";

    final static boolean DEBUG_MODE = true;

    private static JavaSparkContext jscSingleton;

    public static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(APP_NAME);
            sparkConf.setMaster("local[*]");
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

	public static void main(String[] args) throws Exception {
		String modeString = "";
		String jdkPath = "/usr/lib/jvm/java-1.7.0-openjdk-amd64";
        String nativePath = "/home/tudor/Desktop/Thesis/projects/framework/codeGen", appName = "convolution";
        if(args.length >= 2){
            nativePath = args[0];
            appName = args[1];
        } else {
            System.out.println("Usage: <nativePath> <appName>");
        }

        JniFrameworkLoader.setJdkPath(jdkPath);
		JniFrameworkLoader.setNativePath(nativePath);
//        JniFrameworkLoader.setSparkContext(getSparkContext());
		JniFrameworkLoader.registerJniFunction(ConvolutionJniFunction.class);
		JniFrameworkLoader.registerContainer(CustomImage.class);
		JniFrameworkLoader.deploy(appName, appName+".cpp", getSparkContext());
        getSparkContext().addFile(nativePath + "/convolution.so");

        JavaRDD<CustomImage> dset, imgDataSet = null;

        if(!DEBUG_MODE) {
			if(args.length > 0){
				modeString = args[0];
			}

			if(args.length > 1){
				inputBatchImageSize = Integer.parseInt(args[1]);
				IMG_FOLDER = "sampleSized/"+inputBatchImageSize+"x"+inputBatchImageSize+"_64/";
			}

            JavaPairRDD<String, PortableDataStream> imgRDD = getSparkContext().binaryFiles(IMG_FOLDER, 16);

            try {
                Configuration conf = new Configuration();

                FileSystem fs = FileSystem.get(conf);

                if (fs.exists(new Path(nativeLibPath)))
                    System.out.println("File exists " + fs.getFileStatus(new Path(nativeLibPath)).getPath());
                else
                    System.out.println("File does not exist " + nativeLibPath);

                getSparkContext().addFile(HDFS_PATH + nativeLibPath);

            } catch (Exception e) {
                System.out.println("\n\n\nException " + e);
                getSparkContext().stop();
                return;
            }

            long startDecodingTime = System.currentTimeMillis();

            imgDataSet = imgRDD.map(new Function<Tuple2<String, PortableDataStream>, CustomImage>() {
                @Override
                public CustomImage call(Tuple2<String, PortableDataStream> img) {
                    return new CustomImage(img._2(), img._1());
                }
            }).filter(new Function<CustomImage, Boolean>() {
                public Boolean call(CustomImage img) {
                    return img.isValidImg;
                }
            });
        } else {
            ArrayList<CustomImage> debugImages = new ArrayList<>();
            int[] kernel = genKernel_1Darr(121, 0);
            for(int idx = 0; idx < noOfImgs; idx++)
                debugImages.add(new CustomImage(generateRandImage(256, 256), 256, 256, kernel));

            imgDataSet = getSparkContext().parallelize(debugImages);
        }

		imgDataSet.cache();
		long randCount = imgDataSet.count();

		long startImgProcessing = System.currentTimeMillis();

        dset = processNative(imgDataSet);

        dset.cache();
        randCount = dset.count();
        long doneImgProcessing = System.currentTimeMillis();

//		ImgDebug results = dset.map(new Function<CustomImage, CustomImage>(){
//			@Override
//			public CustomImage call(CustomImage buffImg){
//				int width = buffImg.getWidth(), height = buffImg.getHeight();
//				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//				WritableRaster raster = (WritableRaster) image.getData();
//				raster.setPixels(0, 0, width, height, buffImg.getDataArr());
//				image.setData(raster);
//				buffImg.setBuffImg(image);
//				return buffImg;
//			}
//		}).map(new Function<CustomImage, ImgDebug>(){
//			@Override
//			public ImgDebug call(CustomImage img){
//				try{
//					Configuration conf = new Configuration();
//					FileSystem fs = FileSystem.get(conf);
//					ImageIO.write(img.getBuffImg(), "jpg", fs.create(new Path(IMG_FOLDER+"/outputImages/"+img.getDestinationFn()), true));
//				} catch(Exception e){
//					e.printStackTrace();
//				}
//				ImgDebug dbgImg = new ImgDebug();
//				dbgImg.accu = img.getWidth()+","+img.getHeight()+","+img.getExecTime()+"\n";
//				dbgImg.millis = img.getExecTime();
//				return dbgImg;
//			}
//		}).reduce(new Function2<ImgDebug, ImgDebug, ImgDebug>(){
//			@Override
//			public ImgDebug call(ImgDebug i1, ImgDebug i2){
//				ImgDebug r = new ImgDebug();
//				r.accu = i1.accu + "" + i2.accu;
//				r.millis = i1.millis + i2.millis;
//				return r;
//			}
//		});

//		System.out.println("RESULTS "+modeString+"\n"+results.accu);
//		System.out.println("Kernel execution time "+modeString+": "+results.millis);
//		System.out.println("Image decoding done in:"+(startImgProcessing - startDecodingTime)+" ms");
//		System.out.println("Image processing done in:"+(doneImgProcessing - startImgProcessing)+" ms");
//
//		System.out.println("----> FULL TIME "+ modeString + (System.currentTimeMillis() - startDecodingTime)+" ms");
	}

	public static JavaRDD<CustomImage> processNative(JavaRDD<CustomImage> dataSet){
		return dataSet.map(new ConvolutionJniFunction<CustomImage, CustomImage>("convolution.so", "callNativeConvolution"));
	}

	public static JavaRDD<CustomImage> processJava(JavaRDD<CustomImage> dataSet){
		return dataSet.map(new Function<CustomImage, CustomImage>() {
			@Override
			public CustomImage call(CustomImage sliceData) throws Exception {
				int height = sliceData.getHeight(), width = sliceData.getWidth();

				long binLoad = System.nanoTime();

				sliceData.setDataArr(Utils.processCPU(sliceData.getDataArr(),
					width, height, Utils.genConvMat(Utils.KERNEL_SMOOTH, kernel_size)));

				sliceData.setExecutionTime(System.nanoTime() - binLoad);

				return sliceData;
			}
		});
	}

	private static int[] genKernel_1Darr(int kernel_size, int KERNEL_TYPE){
		int[] kernel = new int[kernel_size];
		for(int i = 0; i < kernel_size; i++){
			kernel[i] = 1;
		}

		return kernel;
	}

	private static int[] generateRandImage(int width, int height){
        int[] img = new int[width * height];
        for(int j = 0; j < height; j++)
            for(int i = 0; i < width; i++)
                img[j * width + i] = (i + j)%256;

        return img;
    }
}

class ImgDebug implements Serializable{
	public String accu = "";
	public long millis = 0;
}