import java.util.*;

public class NaiveBayes {
    public static List<List<String>> trainData = new ArrayList<>();
    public static List<List<String>> testData = new ArrayList<>();
    public static List<String> trainLabels = new ArrayList<>();
    public static List<String> testLabels = new ArrayList<>();
    public static Map<String,Double> scoreMap = new HashMap<>();
    public static Map<String,Double> scoreSetMap = new HashMap<>();
    public static Set<String> labelSet = new HashSet<>();
    public static List<String> naiveBayes = new ArrayList<>();
    public static Map<String,Double> nbMap = new HashMap<>();
    public static Map<String,Double> precisionBase = new HashMap<>();
    public static Map<String,Double> recallBase = new HashMap<>();
    public static Map<String,Double> topPR = new HashMap<>();
    public static Map<Integer, Set<String>> rowCount = new HashMap<>();
    public static Map<String, Double> matchMap = new HashMap<>();
    public static boolean verbose = false;

    public static void runNB(boolean v, String trainFile, String testFile, int laplacian){
        verbose = v;
        prepareData(trainFile, testFile);
        train();
        test(laplacian);
    }

    public static void train(){

    }

    public static void prepareData(String trainFile, String testFile) {
        prepareTrainData(trainFile);
        prepareTestData(testFile);
        getRowCount();
    }

    public static void prepareTrainData(String trainFile) {
        List<List<String>> records = KnnSolver.readData(trainFile);
        if(!records.isEmpty()) {
            for (int i = 0; i < records.size(); i++) {
                List<String> tempList = records.get(i);
                if(!tempList.isEmpty()) {
                    trainData.addAll(Collections.singleton(tempList));
                    trainLabels.add(tempList.get(tempList.size() - 1));
                }
            }
        }
        for (int i = 0; i < trainData.size(); i++) {
            if(checkEmpty(trainData.get(i))){
                trainData.remove(i);
            }
        }
        for (int i = 0; i < trainLabels.size(); i++) {
            if(trainLabels.get(i).isEmpty()){
                trainLabels.remove(i);
            }
        }
        for (int i = 0; i < trainData.size(); i++) {
                labelSet.add(trainData.get(i).get(trainData.get(i).size()-1));
        }
    }

    public static boolean checkEmpty(List<String> tempList){
        int count =0;
        for (int i = 0; i < tempList.size(); i++) {
            if(null == tempList.get(i) || tempList.get(i).isEmpty())
                count++;
        }
        if(!(count == tempList.size()))
            return false;
        return true;
    }

    public static void prepareTestData(String testFile) {
        List<List<String>> records = KnnSolver.readData(testFile);
        if(!records.isEmpty()) {
            for (int i = 0; i < records.size(); i++) {
                List<String> tempList = records.get(i);
                if(!tempList.isEmpty()) {
                    testData.addAll(Collections.singleton(tempList));
                }
                testLabels.add(tempList.get(tempList.size()-1));
            }
        }
    }

    public static void test(int laplacian){
        generateScoreMap();
        generateScoreSet();
        for (int i = 0; i < testLabels.size(); i++) {
            matchMap  = new HashMap<>();
            String test = testLabels.get(i);
            List<String> testValue = testData.get(i);
            for (String s : labelSet) {
                generateValueList(s, testValue, laplacian, matchMap);
            }
            if(verbose) {
                printNaiveBayes();
            }
            checkMatch(test);
            calculateTopPRAndPrecisionBase(test);
            if(verbose) {
                System.out.println();
            }
        }
        calculateRecallBase();
        printMetrices();
    }

    public static void checkMatch(String test){
        String output = "";
        double max = Integer.MIN_VALUE;
        for(String key : matchMap.keySet()){
            if(matchMap.get(key) > max) {
                max = matchMap.get(key);
                output = key;
            }
        }
        if(verbose) {
            if (output.equals(test)) {
                System.out.println("match: " + output);
            } else {
                System.out.println("fail: got '" + output + "' != want = '" + test + "'");
            }
        }
    }

    public static void printMetrices(){
        for(String label : labelSet){
            System.out.print("label="+label + " ");
            double prNum = 0.0;
            double recallDen = 0.0;
            double precisionDen = 0.0;
            if(recallBase.containsKey(label)){
                recallDen = recallBase.get(label);
            }
            if(precisionBase.containsKey(label)){
                precisionDen = precisionBase.get(label);
            }
            if(topPR.containsKey(label)){
                prNum = topPR.get(label);
            }
            System.out.print("Precision=" + (int)prNum + "/"+ (int)precisionDen +" ");
            System.out.print("Recall=" + (int)prNum + "/"+(int)recallDen +" ");
            System.out.println();
        }
    }

    public static void calculateRecallBase(){
        for(String label : testLabels){
            if(recallBase.containsKey(label)){
                recallBase.put(label, recallBase.get(label) + 1);
            } else {
                recallBase.put(label, 1.0);
            }
        }
    }

    public static void calculateTopPRAndPrecisionBase(String test){
        String output = "";
        double max = Integer.MIN_VALUE;
        for(String key : nbMap.keySet()){
            if(nbMap.get(key) > max) {
                max = nbMap.get(key);
                output = key;
            }
        }
        if(test.equals(output)){
            if(topPR.containsKey(test)){
                topPR.put(test, topPR.get(test)+1);
            } else {
                topPR.put(test, 1.0);
            }
        } else {
            if(!topPR.containsKey(test)){
                topPR.put(test, 0.0);
            }
        }

        if(precisionBase.containsKey(output)){
            precisionBase.put(output,precisionBase.get(output) + 1);
        } else {
            precisionBase.put(output, 1.0);
        }
        for(String label : labelSet){
            if(!precisionBase.containsKey(label)){
                precisionBase.put(label,0.0);
            }
        }
    }

    public static void printNaiveBayes(){
        for(String key : nbMap.keySet()){
            System.out.println("NB(C="+key+") = "+ nbMap.get(key));
        }
    }

    public static void generateScoreMap(){
        for (int i = 0; i < testLabels.size(); i++) {
            String test = testLabels.get(i);
            double value = (double) Collections.frequency(trainLabels,test)/trainLabels.size();
            scoreMap.put(test, value);
        }
    }

    public static void generateScoreSet(){
        for(String s : labelSet){
            for(String key : scoreMap.keySet()){
                if(key.equals(s)){
                    scoreSetMap.put(s,scoreMap.get(key));
                }
            }
        }
    }

    public static void generateValueList(String test, List<String> testValue, int laplacian, Map<String, Double> matchMap){
        if(verbose) {
            System.out.println("P(C=" + test + ") = " + Collections.frequency(trainLabels, test) + "/" + trainLabels.size());
        }
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < testData.get(0).size()-1; i++) {
            String label = testValue.get(i);
            int count = 0;
            for (int j = 0; j < trainData.size(); j++) {
                List<String> trainList = trainData.get(j);
                if(label.equals(trainList.get(i)) && test.equals(trainList.get(trainList.size()-1))){
                    count+=1;
                }
            }
            if(laplacian != 0){
                int num = count + laplacian;
                int den = Collections.frequency(trainLabels, test) + rowCount.get(i).size()*laplacian;
                if(verbose) {
                    System.out.println("P(A" + i + "=" + label + "/C=" + test + ") = " + num + "/" + den);
                }
                values.add((double) num/den);
            } else {
                if(verbose) {
                    System.out.println("P(A" + i + "=" + label + "/C=" + test + ") = " + count + "/" + Collections.frequency(trainLabels, test));
                }
                values.add((double) (count)/(Collections.frequency(trainLabels,test)));
            }
        }
        generateNB(test, values, matchMap);
    }

    public static void generateNB(String test, List<Double> values, Map<String, Double> matchMap){
        if(null != values){
            double value;
            for(String key : scoreSetMap.keySet()){
                if(test.equals(key)){
                    value = scoreMap.get(key);
                    double cal = value;
                    for (int i = 0; i < values.size(); i++) {
                        cal= cal * (double)values.get(i);
                    }
                    matchMap.put(key,cal);
                    nbMap.put(key, cal);
                }
            }
        }
    }

    public static void getRowCount(){
        for (int i = 0; i < trainData.get(0).size()-1; i++) {
            Set<String> row = new HashSet<>();
            if(trainData.get(i).size() == trainData.get(0).size()) {
                for (int j = 0; j < trainData.size(); j++) {
                    row.add(trainData.get(j).get(i));
                }
            }
            rowCount.put(i, row);
        }
    }
}
