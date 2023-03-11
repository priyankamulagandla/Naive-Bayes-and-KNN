import org.apache.commons.cli.*;

public class Learn {
    public static void main(String[] args) {
        Options commandOptions = new Options();
        Option trainInput = new Option("train", "train-input", true,
                "train input file path");
        Option testInput = new Option("test", "test-input", true,
                "test input file path");
        Option k = new Option("K", true, "K-means");
        Option laplacian = new Option("C", true, "laplacian coefficient");
        Option verboseOption = new Option("v","verbose", false,"Verbose Option");

        commandOptions.addOption(trainInput);
        commandOptions.addOption(testInput);
        commandOptions.addOption(k);
        commandOptions.addOption(laplacian);
        commandOptions.addOption(verboseOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try{
            cmd = parser.parse(commandOptions, args);
        } catch(ParseException e){
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", commandOptions);
            System.exit(1);
        }

        boolean verbose = cmd.hasOption("verbose");
        String trainFile = cmd.getOptionValue("train");
        String testFile = cmd.getOptionValue("test");
        int knn = cmd.hasOption("K") ? Integer.parseInt(cmd.getOptionValue("K")) : 0;
        int laplacianCoefficient = cmd.hasOption("C") ? Integer.parseInt(cmd.getOptionValue("C")) : 0;
        if(knn > 0 && laplacianCoefficient > 0){
            System.out.println("Illegal Arguments");
            System.exit(0);
        }
        else if (knn > 0){
            KnnSolver.knn(knn, trainFile, testFile);
        } else {
            NaiveBayes.runNB(verbose, trainFile, testFile, laplacianCoefficient);
        }
    }
}
