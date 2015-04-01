package edu.jhu.Barbara.cs335.hw4;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by Barbara Holt on 3/15/2015.
 */
public class traditionalDecisionTree {
    private ArrayList<Node> data;
    private ArrayList<ArrayList<Node>> tree;
    public ArrayList<Node> testingData;
    private LinkedList<Integer> q;

    public traditionalDecisionTree(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)));
        if (filename.contains("/data/monks")) {
            readData(true, false, scanner);

            String testFile = filename.replace("train", "test");
            Scanner scanner2 = new Scanner(new BufferedInputStream(new FileInputStream(testFile)));
            readData(true, true, scanner2);
        } else {
            readData(false, false, scanner);
            divideData();
        }
        System.out.println("Building your tree...");
        tree();
        //report(this.tree);
        scanner.close();
    }

    private void divideData() {
        this.testingData = new ArrayList<Node>();
        double total = this.data.size();
        double portion = total * 0.3;
        for (double i = 0; i < portion; i++) {
            Node n = this.data.get((int) i);
            testingData.add(n);
            this.data.remove((int) i);
        }
    }

    private ArrayList<ArrayList<Node>> tree() {
        /** Sets up effective root of tree: */
        this.tree = new ArrayList<ArrayList<Node>>();
        this.tree.add(new ArrayList<Node>());
        Node root = new Node(null);
        root.startNext = 1;
        this.tree.get(0).add(root);

        branch(this.data, 0);
        boolean showGoesOn = true;
        int i = 1;
        while (showGoesOn) {
            ArrayList<Node> subset = this.tree.get(i);
            if (!subset.isEmpty()) {
                showGoesOn = checkSplit(i);
                if (showGoesOn) {
                    branch(subset, i);
                }
            }
            i++;
            showGoesOn = checkTree(i);
        }
        return this.tree;
    }

    private boolean checkSplit(int index) {
        ArrayList<Node> branch = this.tree.get(index);
        if (!branch.isEmpty()) {
            String label = branch.get(0).label;
            for (Node n : branch) {
                if (!n.label.equals(label)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkTree(int terminus) {
        if (terminus < this.tree.size()) {
            int last = this.tree.size();
            do {
                last--;
            } while (this.tree.get(last).isEmpty());
            ArrayList<Node> branch = this.tree.get(last);
            int lvl = branch.get(0).previous.size();
            int i = last;
            while (lvl == branch.get(0).previous.size()) {
                if (!branch.isEmpty()) {
                    if (branch.size() > 0) {
                        return true;
                    }
                }
                do {
                    i--;
                } while (this.tree.get(i).isEmpty());
                branch = this.tree.get(i);
            }
            return false;
        } else {
            return false;
        }
    }

    private void branch(ArrayList<Node> arr, int from) {
        int feature = bestSplit(arr);
        if (feature != Integer.MIN_VALUE) {
            for (int i = 0; i < arr.size(); i++) {
                Node n = arr.get(i);
                int value = n.features.get(feature);
                int arrayIndex = -1;
                int j = this.tree.size() - 1;
                if (this.tree.get(j).isEmpty()) {
                    do {
                        j--;
                    } while (this.tree.get(j).isEmpty());
                }
                while (from == this.tree.get(j).get(0).from && j > 0) {
                    if (this.tree.get(j).get(0).features.get(feature) == value) {
                        arrayIndex = j;
                    }
                    do {
                        j--;
                    } while (this.tree.get(j).isEmpty());
                }

                Node nCopy = new Node(n.label);
                nCopy.from = from;
                for (int feat : n.features) {
                    nCopy.features.add(feat);
                }
                for (int prev : n.previous) {
                    nCopy.previous.add(prev);
                }
                nCopy.previous.add(feature);

                if (arrayIndex != -1) {
                    this.tree.get(arrayIndex).add(nCopy);
                } else {
                    this.tree.add(new ArrayList<Node>());
                    this.tree.get(this.tree.size() - 1).add(nCopy);
                }

                if (i == 0) {
                    arr.get(0).startNext = this.tree.size() - 1;
                }
            }
            while ((this.tree.size() - 1)%3 > 0) {
                this.tree.add(new ArrayList<Node>());
            }
        }
    }

    private void loadQueues(ArrayList<Node> set) {
            this.q = new LinkedList<Integer>();

            /** All possible features for this bestSplit in the tree: */
            for (int i = 0; i < this.data.get(0).features.size(); i++) {
                if (this.tree.size() > 1) {
                    if (!set.isEmpty()) {
                        if (!set.get(0).previous.contains(i)) {
                            q.add(i);
                        }
                    }
                } else {
                    q.add(i);
                }
            }
    }

    private void readData(boolean containsIDs, boolean test, Scanner scan) {
        if (!test) {
            this.data = new ArrayList<Node>();
        } else {
            this.testingData = new ArrayList<Node>();
        }
        ArrayList<String> values = new ArrayList<String>();
        int adjuster = 0;
        if (containsIDs) {
            adjuster = 1;
        }
        while (scan.hasNext()) {
            String[] row;
            if (!containsIDs) {
                row = scan.nextLine().split(",");
            } else {
                row = scan.nextLine().split(" ");
            }
            Node n = new Node(row[adjuster]);
            for (int i = 1; i < row.length - adjuster; i++) {
                String value = row[i].toLowerCase();
                if (values.contains(value)) {
                    n.features.add(values.indexOf(value) - 1);
                } else {
                    values.add(value);
                    n.features.add(values.indexOf(value) - 1);
                }
            }
            if (containsIDs) {
                n.ID = row[row.length - adjuster];
            }
            if (!test) {
                this.data.add(n);
            } else {
                this.testingData.add(n);
            }
        }
    }

    private int bestSplit(ArrayList<Node> set) {
        loadQueues(set);
        if (this.q.size() > 0) {
            double S = set.size();
            int bestFeature = -1;
            double infoGain = Integer.MIN_VALUE;
            while (!this.q.isEmpty()) {
                LinkedList<Integer> totals = new LinkedList<Integer>();

                ArrayList<ArrayList<String>> numbers = new ArrayList<ArrayList<String>>();
                int feature = this.q.pop();
                for (Node n : set) {
                    String value = n.features.get(feature).toString();
                    boolean found = false;
                    for (ArrayList<String> arr : numbers) {
                        if (arr.get(0).equals(value)) {
                            for (int i = 1; i < arr.size() - 1; i++) {
                                if (arr.get(i).equals(n.label)) {
                                    int num = 1 + Integer.parseInt(arr.get(i + 1));
                                    arr.remove(i + 1);
                                    arr.add(i + 1, String.valueOf(num));
                                    i = arr.size();
                                    found = true;
                                }
                            }
                            if (!found)
                            {
                                arr.add(n.label);
                                arr.add("1");
                            }
                            found = true;
                        }
                    }
                    if (!found) {
                        numbers.add(new ArrayList<String>());
                        numbers.get(numbers.size() - 1).add(value);
                        numbers.get(numbers.size() - 1).add(n.label);
                        numbers.get(numbers.size() - 1).add("1");
                    }
                }


                double avgWeighted = 0;
                int biggest = 0;
                for (int j = 0; j < numbers.size(); j++) {
                    if (numbers.get(j).size() > numbers.get(biggest).size()) {
                        biggest = j;
                    }
                }
                int labelCount = (numbers.get(biggest).size() - 1) / 2;

                for (int j = 0; j < labelCount; j++) {
                    String label = numbers.get(biggest).get(1 + j*2);
                    int total = 0;
                    for (ArrayList<String> arr : numbers) {
                        for (int k = 0; k < arr.size() - 1; k++) {
                            String s = arr.get(k);
                            if (s.equals(label)) {
                                int instance = Integer.parseInt(arr.get(k + 1));
                                total += instance;
                                totals.addFirst(instance);
                            }
                        }
                    }

                    int instanceTotal = 0;
                    double entropyC = 0;
                    for (int k = 0; k < numbers.size(); k++) {
                        if (!totals.isEmpty()) {
                            double instance = totals.pop();
                            instanceTotal += instance;
                            entropyC += -(instance / total) * (Math.log(instance / total) / Math.log(2));
                        }
                    }
                    avgWeighted += (total / S) * entropyC;
                    totals.add(instanceTotal);
                }

                double entropyP = 0;
                while (!totals.isEmpty()) {
                    double total = totals.pop();
                    entropyP += -(total / S) * Math.log(total / S) / Math.log(2);
                }

                if (Double.isNaN(avgWeighted)) {
                    avgWeighted = 0;
                }

                double tempGain = entropyP - avgWeighted;
                if (infoGain < tempGain) {
                    infoGain = (entropyP - avgWeighted);
                    bestFeature = feature;
                }
            }
            return bestFeature;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public ArrayList<Node> test() {
        return this.testingData;
    }

    public ArrayList<ArrayList<Node>> train() {
        return this.tree;
    }

    public void report(ArrayList<ArrayList<Node>> decisionTree) {
        int j = decisionTree.size() - 1;
        int i = j;
        ArrayList<Node> branch;
        boolean print = true;
        while (print) {
            boolean search = true;
            do {
                i--;
                if (i > 0 && i < decisionTree.size() - 1) {
                    if (!decisionTree.get(i).isEmpty()) {
                        if (!checkSplit(i)) {
                            j = i;
                            search = false;
                        }
                    }
                } else {
                    search = false;
                    print = false;
                }
            } while (search);

            while (j != 0) {
                branch = decisionTree.get(j);
                if (!branch.isEmpty()) {
                    Node n = branch.get(0);
                    int feature = n.previous.get(n.previous.size() - 1);
                    System.out.print(j + " [" + feature + "]");
                    if (n.from > 0) {
                        System.out.print(" <- ");
                    } else {
                        System.out.print("\n");
                    }
                    j = n.from;
                }
                while (decisionTree.get(j).isEmpty()) {
                    j--;
                }
            }
        }
    }
}