_## Artificial Intelligence
#### [Lab 3]
#### Priyanka Mulagandla (pm3392)

### Running the code

console
usage:  [-v] [-K knn] [-C laplacian coefficient] [-train trainInput] [-test testInput]

#### Executing the programs

Compile the files using command line (in the folder and unzipped)
javac -classpath "*:." *.java

To run the program in knn mode mode, please use the command
java -classpath "*:." Learn -K 7 -train "/Users/priyanka/Desktop/train2.csv" -test "/Users/priyanka/Desktop/test2.csv"

To run the program in Naive Bayes mode without verbose, please use the command
java -classpath "*:." Learn -C 0 -train "/Users/priyanka/Downloads/ex1_train.csv" -test "/Users/priyanka/Downloads/ex1_test.csv"

To run the program in Naive Bayes mode with verbose, please use the command
java -classpath "*:." Learn -v -C 0 -train "/Users/priyanka/Downloads/ex1_train.csv" -test "/Users/priyanka/Downloads/ex1_test.csv"

