import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class KnnSolver {

    public static List<List<Integer>> trainData = new ArrayList<>();
    public static List<String> trainLabels = new ArrayList<>();
    public static List<List<Integer>> testData = new ArrayList<>();
    public static List<String> testLabels = new ArrayList<>();
    public static List<String> predictedLabels = new ArrayList<>();

    public static void runKnn(KNearestNeighbors kNearestNeighbors){
        prepareData(kNearestNeighbors);
        train();
        test(kNearestNeighbors);
        printMetrices(kNearestNeighbors);
    }

    public static void train(){

    }

    public static void prepareData(KNearestNeighbors kNearestNeighbors) {
        prepareTrainData(kNearestNeighbors);
        prepareTestData(kNearestNeighbors);
    }

    public static void prepareTrainData(KNearestNeighbors kNearestNeighbors) {
        List<List<String>> records = readData(kNearestNeighbors.trainFile);
        if(!records.isEmpty()) {
            for (int i = 0; i < records.size(); i++) {
                List<String> tempList = records.get(i);
                String label = tempList.get(tempList.size()-1);
                trainLabels.add(label);
                List<Integer> addList = new ArrayList<>();
                for (int j = 0; j < tempList.size()-1; j++) {
                    addList.add(Integer.parseInt(tempList.get(j)));
                }
                trainData.add(addList);
            }
        }
    }


    public static void prepareTestData(KNearestNeighbors kNearestNeighbors) {
        List<List<String>> records = readData(kNearestNeighbors.testFile);
        if(!records.isEmpty()) {
            for (int i = 0; i < records.size(); i++) {
                List<String> tempList = records.get(i);
                String label = tempList.get(tempList.size()-1);
                testLabels.add(label);
                List<Integer> addList = new ArrayList<>();
                for (int j = 0; j < tempList.size()-1; j++) {
                    addList.add(Integer.parseInt(tempList.get(j)));
                }
                testData.add(addList);
            }
        }
    }

    public static List<List<String>> readData(String path){
        List<List<String>> records = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    public static void test(KNearestNeighbors kNearestNeighbors){
        for(List<Integer> testItem : testData){
            List<List<Double>> knnIndices = findKnnIndices(kNearestNeighbors.unitw, testItem);
            Map<String, Double> labelScores = new HashMap<>();
            for(List<Double> data : knnIndices){
                String label = trainLabels.get((int)(double) data.get(1));
                double voteWeight = data.get(2);
                if (labelScores.containsKey(label)){
                    labelScores.put(label, labelScores.get(label) + voteWeight);
                } else{
                    labelScores.put(label, voteWeight);
                }
            }
            double max = Double.MIN_VALUE;
            String predictedLabel = "";
            for(String key : labelScores.keySet()){
                if(labelScores.get(key) > max){
                    max = labelScores.get(key);
                    predictedLabel = key;
                }
            }
            predictedLabels.add(predictedLabel);
        }
    }

    public static List<List<Double>> findKnnIndices(boolean unitw, List<Integer> testItem){
        List<List<Double>> distances = new ArrayList<>();
        int index = 0;
        Util util = new Util();
        for(List<Integer> train : trainData){
            double distance = util.calculateEucledean(train, testItem);
            double voteWeight = unitw ? 1 : 1 / Math.max(distance, 0.0001);
            List<Double> indexItem = new ArrayList<>();
            indexItem.add(distance);
            indexItem.add((double) index);
            indexItem.add(voteWeight);
            distances.add(indexItem);
            index++;
        }
        distances.sort((l1, l2) -> l1.get(0).compareTo(l2.get(0)));
        return distances;
    }

    public static void printMetrices(KNearestNeighbors kNearestNeighbors){
        for (int i = 0; i < testLabels.size(); i++) {
            System.out.println("want=" + testLabels.get(i) + " got=" + predictedLabels.get(i));
        }
        Map<String, Pair> precision = new HashMap<>();
        Map<String, Pair> recall = new HashMap<>();

        for (int i = 0; i < testLabels.size(); i++) {
            precision.put(predictedLabels.get(i), new Pair(0,0));
            recall.put(testLabels.get(i), new Pair(0,0));
        }

        for (int i = 0; i < testLabels.size(); i++) {
            if(testLabels.get(i).equals(predictedLabels.get(i))){
                precision.put(predictedLabels.get(i),new Pair(precision.get(predictedLabels.get(i)).predictedCount + 1, precision.get(predictedLabels.get(i)).actualCount + 1));
                recall.put(testLabels.get(i),new Pair(recall.get(testLabels.get(i)).predictedCount + 1, recall.get(testLabels.get(i)).actualCount + 1));
            }
            else {
                precision.put(predictedLabels.get(i), new Pair(precision.get(predictedLabels.get(i)).predictedCount + 1, precision.get(predictedLabels.get(i)).actualCount));
                recall.put(testLabels.get(i),new Pair(recall.get(testLabels.get(i)).predictedCount, recall.get(testLabels.get(i)).actualCount + 1));
            }
        }
        List<String> labels = new ArrayList<>();
        for( String key : precision.keySet()){
            if(!labels.contains(key)) {
                labels.add(key);
            }
        }
        for( String key : recall.keySet()){
            if(!labels.contains(key)) {
                labels.add(key);
            }
        }
        Collections.sort(labels);
        System.out.println();
        for(String label : labels){
            Pair precisionData = precision.containsKey(label) ? precision.get(label) : new Pair(0,0);
            Pair recallData = recall.containsKey(label) ? recall.get(label) : new Pair(0,0);
            System.out.println("Label=" + label + " Precision=" + precisionData.actualCount + "/" + precisionData.predictedCount + " Recall=" + recallData.predictedCount + "/" + recallData.actualCount);
        }
    }

    public static void knn(int k, String train, String test){
        KNearestNeighbors kNearestNeighbors = new KNearestNeighbors(k,"e2",false,train, test);
        runKnn(kNearestNeighbors);
    }
}