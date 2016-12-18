package sparkJNIPi;

public class SparkPi {
    public static String appName = "SparkPi";

//    public static void main(String[] args) {
//        init(args);
//        long start = System.currentTimeMillis();
//        List<Integer> input = getInputMocks(Utils.slices);
//        JavaRDD<RandNumArray> piBeanJavaRDD = Utils.getSparkContext().parallelize(input).map(Utils.callSparkJNIPiNativeFunction);
//        JavaRDD<SumArray> intermediateResult = piBeanJavaRDD.map(Utils.insideCircleFunction);
//        SumArray result = intermediateResult.reduce(Utils.reduceCountPi);
//        Utils.computePiAndLog(result, start);
//    }
//
//    private static void init(String[] args) {
//        Utils.slices = args.length > 0 ? Integer.parseInt(args[0]) : 4;
//        Utils.sliceSize = args.length > 1 ? Integer.parseInt(args[1]) : 4194304 * 8;
//        Utils.noExecs = args.length > 2 ? Integer.parseInt(args[2]) : 1;
//        Utils.logFilePath = args.length > 3 ? args[3] : String.format("resources/SparkPi.log", Utils.slices, Utils.sliceSize, Utils.noExecs);
//    }
}
