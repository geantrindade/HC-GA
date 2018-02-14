package br.ufscar.hcga.classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gean_
 */
public class Parameters {

//Parameters
private String parameters[] = new String[19];
private static ArrayList<Double> thresholdValues = new ArrayList<Double>();
private static int numberRuns;
private static int elitismNumber;
private static Double mutationRate;
//private static Double mutationGenProbability;
private static Double probabilityUseClausule;
private static Double crossoverRate;
private static String datasetTrain;
private static String datasetValid;
private static String datasetTest;
private static int numberGenerations;
private static int sizeTournament;
private static int maxUncoveredExamples;
private static int minCoveredExamplesRule;
private static int maxCoveredExamplesRule;
private static int numLevels;
private static int numberInitialRules;
private static String hierarchyType;
private static int multiLabel;
private static String pathDatasets;
private static int relationalTests;

public Parameters(String configFile) {

    String regExp[] = {"number of runs =",
        "elitism number =",
        "mutation rate =",
        //"mutation gen probability =",
        "crossover rate =",
        "dataset train =",
        "dataset valid =",
        "dataset test =",
        "number of generations =",
        "size tournament =",
        "max uncovered examples =",
        "min covered examples per rule =",
        "number of levels =",
        "hierarchy type =",
        "probability using clausule =",
        "number initial rules =",
        "max covered examples per rule =",
        "multi-label =",
        "path datasets =",
        "relational tests =",
        "threshold values ="};

    Pattern comment = Pattern.compile("#");

    for (int i = 0; i < regExp.length; i++) {

        try {
            FileReader reader = new FileReader(configFile);
            BufferedReader buffReader = new BufferedReader(reader);

            Pattern pattern = Pattern.compile(regExp[i]);
            String line = null;

            if (i == 19) {//Get threshold values

                while ((line = buffReader.readLine()) != null) {
                    Matcher m = pattern.matcher(line);
                    Matcher m1 = comment.matcher(line);
                    if (m.find() && !m1.find()) {
                        String[] vectorLine1 = line.split("\\[");
                        String[] vectorLine2 = vectorLine1[1].split("\\]");
                        String[] vectorLine3 = vectorLine2[0].split(",");

                        for (int j = 0; j < vectorLine3.length; j++) {
                            thresholdValues.add(Double.parseDouble(vectorLine3[j]));
                        }
                        break;
                    }
                }
            }

            while ((line = buffReader.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                Matcher m1 = comment.matcher(line);
                if (m.find() && !m1.find()) {
                    String[] vectorLine = line.split(" = ");
                    parameters[i] = vectorLine[1];
                    break;
                }
            }
            buffReader.close();
            reader.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    Parameters.numberRuns = Integer.parseInt(parameters[0]);
    Parameters.elitismNumber = Integer.parseInt(parameters[1]);
    Parameters.mutationRate = Double.parseDouble(parameters[2]);
    //Parameters.mutationGenProbability = Double.parseDouble(parameters[3]);
    Parameters.crossoverRate = Double.parseDouble(parameters[3]);
    Parameters.datasetTrain = parameters[4];
    Parameters.datasetValid = parameters[5];
    Parameters.datasetTest = parameters[6];
    Parameters.numberGenerations = Integer.parseInt(parameters[7]);
    Parameters.sizeTournament = Integer.parseInt(parameters[8]);
    Parameters.maxUncoveredExamples = Integer.parseInt(parameters[9]);
    Parameters.minCoveredExamplesRule = Integer.parseInt(parameters[10]);
    Parameters.numLevels = Integer.parseInt(parameters[11]);
    Parameters.hierarchyType = parameters[12];
    Parameters.probabilityUseClausule = Double.parseDouble(parameters[13]);
    Parameters.numberInitialRules = Integer.parseInt(parameters[14]);
    Parameters.maxCoveredExamplesRule = Integer.parseInt(parameters[15]);
    Parameters.multiLabel = Integer.parseInt(parameters[16]);
    Parameters.pathDatasets = parameters[17];
    Parameters.relationalTests = Integer.parseInt(parameters[18]);
    //System.out.println();
}

public static int getNumberInitialRules() {
    return numberInitialRules;
}

public static Double getCrossoverRate() {
    return crossoverRate;
}

public static String getFileDatasetTest() {
    return datasetTest;
}

public static String getFileDatasetTrain() {
    return datasetTrain;
}

public static String getFileDatasetValid() {
    return datasetValid;
}

public static int getElitismNumber() {
    return elitismNumber;
}

public static int getMaxUncoveredExamples() {
    return maxUncoveredExamples;
}

public static int getMinCoveredExamplesRule() {
    return minCoveredExamplesRule;
}

public static int getMaxCoveredExamplesRule() {
    return maxCoveredExamplesRule;
}

/*public static Double getMutationGenProbability() {
    return mutationGenProbability;
    }*/
public static Double getMutationRate() {
    return mutationRate;
}

public static int getNumLevels() {
    return numLevels;
}

public static int getNumberGenerations() {
    return numberGenerations;
}

public static int getNumberRuns() {
    return numberRuns;
}

public static int getSizeTournament() {
    return sizeTournament;
}

public static ArrayList<Double> getThresholdValues() {
    return thresholdValues;
}

public static String getHierarchyType() {
    return hierarchyType;
}

public static Double getProbabilityUseClausule() {
    return probabilityUseClausule;
}

public static int getMultiLabel() {
    return multiLabel;
}

public static String getPathDatasets() {
    return pathDatasets;
}

public static int getRelationalTests() {
    return relationalTests;
}

public static void setProbabilityUseClausule(Double probabilityUseClausule) {
    Parameters.probabilityUseClausule = probabilityUseClausule;
}
}
