package edu.jhu.Barbara.cs335.hw4;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Created by Administrator on 4/1/2015.
 */
public class geneticAlgorithm {
    public int crossoverLimit;
    public geneticAlgorithm(String filename) throws FileNotFoundException {
        mate(growParents(filename));
    }

    private void mate(PriorityQueue<evolutionaryDecisionTree> family) {
        evolutionaryDecisionTree mom = family.poll();
        evolutionaryDecisionTree dad = family.poll();

        /** Genetic Crossover: */
        Random random = new Random();
        int shearLocation = random.nextInt(this.crossoverLimit);
        ArrayList<Node> branch = mom.train().get(shearLocation);
        dad.train().add(branch);
        mom.train().remove(branch);
    }

    private PriorityQueue<evolutionaryDecisionTree> growParents(String filename) throws FileNotFoundException {
        PriorityQueue<evolutionaryDecisionTree> family = new PriorityQueue<evolutionaryDecisionTree>(4, new Comparator<evolutionaryDecisionTree>() {
            @Override
            public int compare(evolutionaryDecisionTree mom, evolutionaryDecisionTree dad) throws NullPointerException, ClassCastException {
                double accuracyMom = test(mom.train(), mom.test());
                double accuracyDad = test(dad.train(), dad.test());
                double sizeMom = mom.train().size();
                double sizeDad = dad.train().size();
                if ((accuracyMom - sizeMom) < (accuracyDad - sizeDad)) {
                    return -1;
                } else if ((accuracyMom - sizeMom) == (accuracyDad - sizeDad)) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (int i = 0; i < 2; i++) {
            evolutionaryDecisionTree parent = new evolutionaryDecisionTree(filename);
            family.add(parent);
        }
        return family;
    }

    public double test(ArrayList<ArrayList<Node>> tree, ArrayList<Node> data) {
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
        return right/(right + wrong);
    }
}
