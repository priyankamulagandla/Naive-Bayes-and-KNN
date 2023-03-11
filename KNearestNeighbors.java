public class KNearestNeighbors {
    int k;
    String distFunction;
    boolean unitw;
    String trainFile;
    String testFile;

    public KNearestNeighbors(int k, String distFunction, boolean unitw, String trainFile, String testFile) {
        this.k = k;
        this.distFunction = distFunction;
        this.unitw = unitw;
        this.trainFile = trainFile;
        this.testFile = testFile;
    }
}
