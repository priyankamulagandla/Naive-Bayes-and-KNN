import java.util.List;

public class Util {
    public double calculateEucledean(List<Integer> p1, List<Integer> p2){
        double sum =0;
        for (int i = 0; i < p1.size(); i++) {
            sum += ((p1.get(i)-p2.get(i))*(p1.get(i)-p2.get(i)));
        }
        return sum;
    }
}
