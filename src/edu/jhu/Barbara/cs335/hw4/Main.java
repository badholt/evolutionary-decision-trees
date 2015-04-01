package edu.jhu.Barbara.cs335.hw4;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        boolean firstInput = true;
        String absoluteFilePath = "";
        Scanner inputScan = new Scanner(System.in);
        System.out.println(
                "\n/---------------------------------\\" +
                        "\n  Specify the dataset file:\n  No. Input\t Corresponding Dataset\n  ---------\t ---------------------" +
                        "\n  0\tEXIT PROGRAM\n  1\tCongressional Voting Data\n  2\tMonk's Problem Data 1\n  3\tMonk's Problem Data 2\n  4\tMonk's Problem Data 3\n  5 Mushroom Data\n" +
                        "\\--------------------------------/"
        );
        String dataSet = "";
        while (inputScan.hasNext()) {
            if (firstInput) {
                try {
                    String input = inputScan.nextLine();
                    int dataNum = Integer.parseInt(input);
                    switch (dataNum) {
                        case 0:
                            return;
                        case 1:
                            dataSet = "/data/house-votes-84.data";
                            break;
                        case 2:
                            dataSet = "/data/monks-1.train";
                            break;
                        case 3:
                            dataSet = "/data/monks-2.train";
                            break;
                        case 4:
                            dataSet = "/data/monks-3.train";
                            break;
                        case 5:
                            dataSet = "/data/agaricus-lepiota.data";
                            break;
                    }

                    /** Loads specified dataset: */
                    String workingDirectory = System.getProperty("user.dir");
                    workingDirectory = workingDirectory.replace("src", "/");
                    absoluteFilePath = workingDirectory + dataSet;

                    /** Prepare for second input: */
                    firstInput = !firstInput;
                    System.out.println(
                            "\n/--------------------------------------\\" +
                                    "\n  Specify the Decision Tree Algorithm:\n  Input\t Corresponding Algorithm\n  -----\t ------------------------" +
                                    "\n  0  \t EXIT PROGRAM\n  trad\t Traditional Decision Tree\n  evo\t Evolutionary Decision Tree\n" +
                                    "\\--------------------------------------/"
                    );
                }catch(NumberFormatException e){
                    System.out.println(
                            "\n/---------------------------------\\" +
                                    "\n  Specify the dataset file:\n  No. Input\t Corresponding Dataset\n  ---------\t ---------------------" +
                                    "\n  0\tEXIT PROGRAM\n  1\tCongressional Voting Data\n  2\tMonk's Problem Data 1\n  3\tMonk's Problem Data 2\n  4\tMonk's Problem Data 3\n  5 Mushroom Data\n" +
                                    "\\--------------------------------/"
                    );
                }

            } else {
                String algorithm = inputScan.nextLine();
                if (algorithm.toUpperCase().equals("TRAD")) {
                    /** Traditional Decision Tree Implementation: */
                    traditionalDecisionTree trad = new traditionalDecisionTree(absoluteFilePath);
                    test(trad.train(), trad.test());
                } else if (algorithm.toUpperCase().equals("EVO")) {
                    /** Evolutionary Decision Tree Implementation: */
                    evolutionaryDecisionTree evo = new evolutionaryDecisionTree(absoluteFilePath);
                    //test(evo.test());
                } else {
                    System.out.println(
                            "\n/--------------------------------------\\" +
                                    "\n  Specify the Decision Tree Algorithm:\n  Input\t Corresponding Algorithm\n  -----\t ------------------------" +
                                    "\n  0  \t EXIT PROGRAM\n  trad\t Traditional Decision Tree\n  evo\t Evolutionary Decision Tree\n" +
                                    "\\--------------------------------------/"
                    );
                }
                firstInput = !firstInput;
                System.out.println(
                        "\n/---------------------------------\\" +
                                "\n  Specify the dataset file:\n  No. Input\t Corresponding Dataset\n  ---------\t ---------------------" +
                                "\n  0\tEXIT PROGRAM\n  1\tCongressional Voting Data\n  2\tMonk's Problem Data 1\n  3\tMonk's Problem Data 2\n  4\tMonk's Problem Data 3\n  5 Mushroom Data\n" +
                                "\\--------------------------------/"
                );
            }
        }
        inputScan.close();
    }

    public static void test(ArrayList<ArrayList<Node>> tree, ArrayList<Node> data) {
        double right = 0;
        double wrong = 0;
        for (Node d : data) {
            String actual = d.label;
            d.ID = d.label;
            int nav = 1;
            boolean identified = false;
            while (!identified) {
                Node sample = tree.get(nav).get(0);
                int feature = sample.previous.get(sample.previous.size() - 1);

                if (d.features.get(feature).equals(sample.features.get(feature))) {
                    nav = sample.startNext;
                    if (sample.startNext == 0) {
                        d.label = sample.label;
                        identified = true;
                    }
                } else {
                    do {
                        nav++;
                        if (nav >= tree.size()) {
                            d.label = "";
                            identified = true;
                        }
                    } while (!identified && tree.get(nav).isEmpty());
                }
            }
            if (actual.equals(d.label)) {
                //System.out.println("That's correct!");
                right++;
            } else {
                //System.out.println("No, that's wrong, computer!");
                wrong++;
            }
        }
        System.out.println("Accuracy:\t" + right/(right + wrong));
        //System.out.println("Precision:\t" + right/(right + wrong));
    }
}
