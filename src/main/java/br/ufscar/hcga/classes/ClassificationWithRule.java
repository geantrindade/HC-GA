package br.ufscar.hcga.classes;

import static br.ufscar.hcga.classes.Evaluation.applyThresholds;
import static br.ufscar.hcga.classes.Evaluation.getDataInterpolation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassificationWithRule {

private String nameDataset = leConfig()[0];
private int specificFold = Integer.parseInt(leConfig()[1]);
private int folds = Integer.parseInt(leConfig()[2]);
private String pathTrainDataset = leConfig()[3];
private String pathTestDataset = leConfig()[4];
private String pathRules = leConfig()[5];
private int numberOfRules;
private String pathToSavePredictions = leConfig()[6];

private ArrayList<String[]> datasetTrain;
private ArrayList<String[]> datasetTest;
private ArrayList<int[]> binaryClassesTrain;
private ArrayList<int[]> binaryClassesTest;
private String[] classes;
private double[] weightingScheme;
private double[] meanClassLabelVectorAllClassesTrain;
private double[] meanClassLabelVectorAllClassesTest;
private static ArrayList<ArrayList<Integer>> positionClassesLevel;
private int numberOfClasses;
private String[] attributes = {"AA", "AT", "AC", "AG", "TT", "TA", "TC", "TG", "CC", "CA", "CG", "CT", "GG", "GA", "GT", "GC", "AAA", "AAT", "AAC", "AAG", "ATA", "ACA", "AGA", "ATT", "ATC", "ATG",
    "ACC", "ACT", "ACG", "AGG", "AGC", "AGT", "CCC", "CCT", "CCA", "CCG", "CTC", "CAC", "CGC", "CTT", "CTA", "CTG", "CGG", "CGA", "CGT", "CAA", "CAT", "CAG", "GGG", "GGT", "GGA", "GGC", "GTG", "GAG", "GCG", "GTT", "GTA", "GTC",
    "GCC", "GCA", "GCT", "GAA", "GAT", "GAC", "TTT", "TTA", "TTC", "TTG", "TAT", "TCT", "TGT", "TGG", "TGA", "TGC", "TAA", "TAC", "TAG", "TCC", "TCA", "TCG", "AAAA", "AAAT", "AAAC", "AAAG", "AATA", "AACA", "AAGA", "AATT", "AATC", "AATG",
    "AACC", "AACT", "AACG", "AAGG", "AAGC", "AAGT", "ACCC", "ACCT", "ACCA", "ACCG", "ACTC", "ACAC", "ACGC", "ACTT", "ACTA", "ACTG", "ACGG", "ACGA", "ACGT", "ACAA", "ACAT", "ACAG", "AGGG", "AGGT", "AGGA", "AGGC", "AGTG", "AGAG", "AGCG", "AGTT", "AGTA", "AGTC",
    "AGCC", "AGCA", "AGCT", "AGAA", "AGAT", "AGAC", "ATTT", "ATTA", "ATTC", "ATTG", "ATAT", "ATCT", "ATGT", "ATGG", "ATGA", "ATGC", "ATAA", "ATAC", "ATAG", "ATCC", "ATCA", "ATCG", "CAAA", "CAAT", "CAAC", "CAAG", "CATA", "CACA", "CAGA", "CATT", "CATC", "CATG",
    "CACC", "CACT", "CACG", "CAGG", "CAGC", "CAGT", "CCCC", "CCCT", "CCCA", "CCCG", "CCTC", "CCAC", "CCGC", "CCTT", "CCTA", "CCTG", "CCGG", "CCGA", "CCGT", "CCAA", "CCAT", "CCAG", "CGGG", "CGGT", "CGGA", "CGGC", "CGTG", "CGAG", "CGCG", "CGTT", "CGTA", "CGTC",
    "CGCC", "CGCA", "CGCT", "CGAA", "CGAT", "CGAC", "CTTT", "CTTA", "CTTC", "CTTG", "CTAT", "CTCT", "CTGT", "CTGG", "CTGA", "CTGC", "CTAA", "CTAC", "CTAG", "CTCC", "CTCA", "CTCG", "GAAA", "GAAT", "GAAC", "GAAG", "GATA", "GACA", "GAGA", "GATT", "GATC", "GATG",
    "GACC", "GACT", "GACG", "GAGG", "GAGC", "GAGT", "GCCC", "GCCT", "GCCA", "GCCG", "GCTC", "GCAC", "GCGC", "GCTT", "GCTA", "GCTG", "GCGG", "GCGA", "GCGT", "GCAA", "GCAT", "GCAG", "GGGG", "GGGT", "GGGA", "GGGC", "GGTG", "GGAG", "GGCG", "GGTT", "GGTA", "GGTC",
    "GGCC", "GGCA", "GGCT", "GGAA", "GGAT", "GGAC", "GTTT", "GTTA", "GTTC", "GTTG", "GTAT", "GTCT", "GTGT", "GTGG", "GTGA", "GTGC", "GTAA", "GTAC", "GTAG", "GTCC", "GTCA", "GTCG", "TAAA", "TAAT", "TAAC", "TAAG", "TATA", "TACA", "TAGA", "TATT", "TATC", "TATG",
    "TACC", "TACT", "TACG", "TAGG", "TAGC", "TAGT", "TCCC", "TCCT", "TCCA", "TCCG", "TCTC", "TCAC", "TCGC", "TCTT", "TCTA", "TCTG", "TCGG", "TCGA", "TCGT", "TCAA", "TCAT", "TCAG", "TGGG", "TGGT", "TGGA", "TGGC", "TGTG", "TGAG", "TGCG", "TGTT", "TGTA", "TGTC",
    "TGCC", "TGCA", "TGCT", "TGAA", "TGAT", "TGAC", "TTTT", "TTTA", "TTTC", "TTTG", "TTAT", "TTCT", "TTGT", "TTGG", "TTGA", "TTGC", "TTAA", "TTAC", "TTAG", "TTCC", "TTCA", "TTCG"};

public ClassificationWithRule() {
    if (nameDataset.equalsIgnoreCase("Mips")) {
        numberOfClasses = 14;
    } else if (nameDataset.equalsIgnoreCase("Repbase")) {
        numberOfClasses = 31;
    } else {
        numberOfClasses = -1;
    }
//
//    setMeanClassLabelVectorAll();
}

public ArrayList<String> readRulesFile(String rulesFile) {
    ArrayList<String> result = new ArrayList<>();

    try {
        FileReader reader = new FileReader(rulesFile);
        BufferedReader buffReader = new BufferedReader(reader);

        String line = null;
        while ((line = buffReader.readLine()) != null) {
            if (!line.isEmpty()) {
                result.add(line);
            }
        }

        buffReader.close();
        reader.close();

    } catch (IOException ioe) {
        ioe.printStackTrace();
    }

    return result;
}

public ArrayList<String> readRulesFileClus(String rulesFile) {
    ArrayList<String> result = new ArrayList<>();
    String wholeRule = "";
    try {
        FileReader reader = new FileReader(rulesFile);
        BufferedReader buffReader = new BufferedReader(reader);

        String line = null;
        while ((line = buffReader.readLine()) != null) {
            if (!line.isEmpty()) {
                wholeRule += line;

                if (line.contains("THEN")) {
                    result.add(wholeRule);
                    wholeRule = "";
                }
            }
        }

        buffReader.close();
        reader.close();

    } catch (IOException ioe) {
        ioe.printStackTrace();
    }

    for (int i = 0; i < result.size(); i++) {
        String aux = result.get(i);
        aux = aux.replaceAll("=", "");
        aux = aux.replace(":", "=");
        aux = aux.replace("IF", "");
        aux = aux.replace("[", "");
        int index = aux.indexOf("]");
        aux = aux.substring(0, index);
        aux = aux.trim();
        result.remove(i);
        result.add(i, aux);
    }

    return result;
}

public void readTestData(String testDatasetFile) {
    datasetTest = new ArrayList<String[]>();
    int numAttribute = -1;

    Pattern patternData = Pattern.compile("@Data", Pattern.CASE_INSENSITIVE);
    Pattern patternNumeric = Pattern.compile("numeric", Pattern.CASE_INSENSITIVE);
    Pattern patternAttribute = Pattern.compile("@ATTRIBUTE", Pattern.CASE_INSENSITIVE);
    Pattern patternHierarchical = Pattern.compile("hierarchical", Pattern.CASE_INSENSITIVE);

    try {
        FileReader readerTest = new FileReader(testDatasetFile);
        BufferedReader rTest = new BufferedReader(readerTest);
        String line = null;
        int dataFound = 0;

        while ((line = rTest.readLine()) != null) {

            //Just read the file util do not find @DATA token
            if (dataFound == 0) {
                Matcher mAttribute = patternAttribute.matcher(line);
                Matcher mData = patternData.matcher(line);

                //See if reached @DATA token
                if (mData.find()) {
                    dataFound = 1;

                } else if (mAttribute.find()) {
                    numAttribute++;

                    //If so, check if attribute is hierarchical, numeric or categoric
                    Matcher mNumeric = patternNumeric.matcher(line);
                    Matcher mHierarchical = patternHierarchical.matcher(line);

                    if (mHierarchical.find()) {
                        //Build structure to store the classes
                        setTreeClasses(line, "hierarchical");
                    }
                }
            } //Token @DATA was found
            else {
                String[] vetLine = line.split(",");
                datasetTest.add(vetLine);
            }
        }
        rTest.close();
        readerTest.close();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
}

public void readTrainDataCSV(String trainDatasetFile) {
    datasetTrain = new ArrayList<String[]>();
    String csvFile = trainDatasetFile;
    String line = "";
    String cvsSplitBy = ",";

    try {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        int skip = 0;
        while ((line = br.readLine()) != null) {

            if (skip > 0) {
                // use comma as separator
//            String[] country = line.split(cvsSplitBy);
                String[] vetLine = line.split(cvsSplitBy);
                datasetTrain.add(vetLine);
            }
            skip = 1;
        }
    } catch (IOException ex) {
        Logger.getLogger(ClassificationWithRule.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public void readTestDataCSV(String trainDatasetFile) {
    datasetTest = new ArrayList<String[]>();
    String csvFile = trainDatasetFile;
    String line = "";
    String cvsSplitBy = ",";

    try {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        int skip = 0;
        while ((line = br.readLine()) != null) {

            if (skip > 0) {
                // use comma as separator
//            String[] country = line.split(cvsSplitBy);
                String[] vetLine = line.split(cvsSplitBy);
                datasetTest.add(vetLine);
            }
            skip = 1;
        }
    } catch (IOException ex) {
        Logger.getLogger(ClassificationWithRule.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public ArrayList getTestDataCSV(String trainDatasetFile) {
    ArrayList<String[]> datasetTest = new ArrayList<String[]>();
    String csvFile = trainDatasetFile;
    String line = "";
    String cvsSplitBy = ",";

    try {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        int skip = 0;
        while ((line = br.readLine()) != null) {

            if (skip > 0) {
                String[] vetLine = line.split(cvsSplitBy);
                datasetTest.add(vetLine);
            }
            skip = 1;
        }
    } catch (IOException ex) {
        Logger.getLogger(ClassificationWithRule.class.getName()).log(Level.SEVERE, null, ex);
    }

    return datasetTest;
}

public void readTrainData(String trainDatasetFile) {
    datasetTrain = new ArrayList<String[]>();
    int numAttribute = -1;

    Pattern patternData = Pattern.compile("@Data", Pattern.CASE_INSENSITIVE);
    Pattern patternNumeric = Pattern.compile("numeric", Pattern.CASE_INSENSITIVE);
    Pattern patternAttribute = Pattern.compile("@ATTRIBUTE", Pattern.CASE_INSENSITIVE);
    Pattern patternHierarchical = Pattern.compile("hierarchical", Pattern.CASE_INSENSITIVE);

    try {
        FileReader readerTrain = new FileReader(trainDatasetFile);
        BufferedReader rTrain = new BufferedReader(readerTrain);
        String line = null;
        int dataFound = 0;

        while ((line = rTrain.readLine()) != null) {

            //Just read the file util do not find @DATA token
            if (dataFound == 0) {
                Matcher mAttribute = patternAttribute.matcher(line);
                Matcher mData = patternData.matcher(line);

                //See if reached @DATA token
                if (mData.find()) {
                    dataFound = 1;

                } else if (mAttribute.find()) {
                    numAttribute++;

                    //If so, check if attribute is hierarchical, numeric or categoric
                    Matcher mNumeric = patternNumeric.matcher(line);
                    Matcher mHierarchical = patternHierarchical.matcher(line);

                    if (mHierarchical.find()) {
                        //Build structure to store the classes
                        setTreeClasses(line, "hierarchical");
                    }
                }
            } //Token @DATA was found
            else {
                String[] vetLine = line.split(",");
                datasetTrain.add(vetLine);
            }
        }
        rTrain.close();
        readerTrain.close();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
}

public ArrayList<Integer> getPosClasses(String actualClasses) {
    ArrayList<Integer> positions = new ArrayList<Integer>();
    String[] vectorClasses = actualClasses.split("@");
    ArrayList<String> allClasses = new ArrayList<String>();

    String[] vetClasses;
    String aClass = "";

    for (int i = 0; i < vectorClasses.length; i++) {
        vetClasses = vectorClasses[i].split("/");
//        vetClasses = vectorClasses[i].split("\\.");

        for (int j = 0; j < vetClasses.length; j++) {
            aClass = aClass.concat(vetClasses[j]);
            allClasses.add(aClass);

            if (j < vetClasses.length - 1) {
                aClass = aClass.concat("/");
//                aClass = aClass.concat(".");
            }
        }
    }

    HashSet hs = new HashSet();
    hs.addAll(allClasses);
    allClasses.clear();
    allClasses.addAll(hs);

    for (int i = 0; i < allClasses.size(); i++) {
        for (int j = 0; j < classes.length; j++) {
            if (allClasses.get(i).equals(classes[j]) == true) {
                positions.add(j);
                break;
            }
        }
    }

    return positions;
}

public void buildClassesStructureTrain() {
    int numberClasses = this.numberOfClasses;
    binaryClassesTrain = new ArrayList<int[]>();

    int[] binaryVector = new int[numberClasses];
    String actualClasses;
    ArrayList<Integer> posClasses;

    for (int i = 0; i < datasetTrain.size(); i++) {
        actualClasses = datasetTrain.get(i)[datasetTrain.get(i).length - 1];
        posClasses = getPosClasses(actualClasses);

        for (int j = 0; j < posClasses.size(); j++) {
            binaryVector[posClasses.get(j)] = 1;
        }

        binaryClassesTrain.add(binaryVector.clone());

        for (int j = 0; j < binaryVector.length; j++) {
            binaryVector[j] = 0;
        }
    }
}

public void buildClassesStructureTest() {
    int numberClasses = this.numberOfClasses;
    binaryClassesTest = new ArrayList<int[]>();

    int[] binaryVector = new int[numberClasses];
    String actualClasses;
    ArrayList<Integer> posClasses;

    for (int i = 0; i < datasetTest.size(); i++) {
        actualClasses = datasetTest.get(i)[datasetTest.get(i).length - 1].trim();
        posClasses = getPosClasses(actualClasses);

        for (int j = 0; j < posClasses.size(); j++) {
            binaryVector[posClasses.get(j)] = 1;
        }

        binaryClassesTest.add(binaryVector.clone());

        for (int j = 0; j < binaryVector.length; j++) {
            binaryVector[j] = 0;
        }
    }
}

public ArrayList getClassesStructureTest(ArrayList<String[]> datasetTest) {
    int numberClasses = this.numberOfClasses;
    ArrayList<int[]> binaryClassesTest = new ArrayList<int[]>();

    int[] binaryVector = new int[numberClasses];
    String actualClasses;
    ArrayList<Integer> posClasses;

    for (int i = 0; i < datasetTest.size(); i++) {
        actualClasses = datasetTest.get(i)[datasetTest.get(i).length - 1].trim();
        posClasses = getPosClasses(actualClasses);

        for (int j = 0; j < posClasses.size(); j++) {
            binaryVector[posClasses.get(j)] = 1;
        }

        binaryClassesTest.add(binaryVector.clone());

        for (int j = 0; j < binaryVector.length; j++) {
            binaryVector[j] = 0;
        }
    }

    return binaryClassesTest;
}

private void setTreeClassesWeights(double[] weightingScheme, String rootClass, ArrayList<Integer> superClassesPositionsAux) {
    rootClass = rootClass.concat("/[0-9]+");
    Pattern pattern = Pattern.compile(rootClass + "$");

    ArrayList<Integer> superClassesPositions = new ArrayList<Integer>();
    int numParents = 0;
    double sum = 0.0;
    Matcher m;

    for (int i = 0; i < classes.length; i++) {
        m = pattern.matcher(classes[i]);

        if (m.find()) {
            for (int j = 0; j < superClassesPositionsAux.size(); j++) {
                numParents++;
                sum += weightingScheme[superClassesPositionsAux.get(j)];
                superClassesPositions.add(superClassesPositionsAux.get(j));
            }

            weightingScheme[i] = 0.75 * (sum / numParents);
            superClassesPositions.add(i);
            setTreeClassesWeights(weightingScheme, "^" + classes[i], superClassesPositions);
        }
    }
}

private void setWeightingScheme(double[] weightingScheme) {
    ArrayList<Integer> topLevelClassesPositions = new ArrayList<Integer>();

    for (int i = 0; i < classes.length; i++) {
        if (classes[i].contains("/") == false) {
            topLevelClassesPositions.add(i);
        }
    }

    ArrayList<Integer> superClassesPositions = new ArrayList<Integer>();
    String rootClass = "";

    for (int i = 0; i < topLevelClassesPositions.size(); i++) {
        weightingScheme[topLevelClassesPositions.get(i)] = 0.75;
        rootClass = "^" + classes[topLevelClassesPositions.get(i)];
        superClassesPositions.add(topLevelClassesPositions.get(i));
        setTreeClassesWeights(weightingScheme, rootClass, superClassesPositions);
    }
}

public void setTreeClasses(String lineClasses, String tokenHierarchical) {
    String[] vetLine = lineClasses.split(tokenHierarchical);
    classes = vetLine[1].split(",");
    classes[0] = classes[0].trim();

    weightingScheme = new double[classes.length];
    setWeightingScheme(weightingScheme);

    ArrayList<ArrayList<Integer>> positionClassesLevels = new ArrayList<ArrayList<Integer>>();

    String rootClass = "";
    ArrayList<Integer> positions = new ArrayList<Integer>();
    Pattern pattern;
    Matcher m;

    for (int i = 0; i < Parameters.getNumLevels(); i++) {
        rootClass = rootClass.concat("[0-9]+");
        pattern = Pattern.compile("^" + rootClass + "$");

        for (int j = 0; j < classes.length; j++) {
            m = pattern.matcher(classes[j]);

            if (m.find()) {
                positions.add(j);
            }
        }

        rootClass = rootClass.concat("/");
        positionClassesLevels.add(positions);
    }

    //Set the position of the classes by level
    positionClassesLevel = positionClassesLevels;
}

public void setMeanClassLabelVectorAllTrain(String trainDatasetFile) {
    readTrainData(trainDatasetFile);
    buildClassesStructureTrain();
    meanClassLabelVectorAllClassesTrain = new double[classes.length];

    for (int i = 0; i < binaryClassesTrain.size(); i++) {
        for (int j = 0; j < classes.length; j++) {
            meanClassLabelVectorAllClassesTrain[j] += binaryClassesTrain.get(i)[j];
        }
    }

    for (int i = 0; i < meanClassLabelVectorAllClassesTrain.length; i++) {
        meanClassLabelVectorAllClassesTrain[i] = meanClassLabelVectorAllClassesTrain[i] / binaryClassesTrain.size();
    }
}

public void setMeanClassLabelVectorAllTest(String testDatasetFile) {
    readTestData(testDatasetFile);
    buildClassesStructureTest();
    meanClassLabelVectorAllClassesTest = new double[classes.length];
    for (int i = 0; i < binaryClassesTest.size(); i++) {
        for (int j = 0; j < classes.length; j++) {
            meanClassLabelVectorAllClassesTest[j] += binaryClassesTest.get(i)[j];
        }
    }

    for (int i = 0; i < meanClassLabelVectorAllClassesTest.length; i++) {
        meanClassLabelVectorAllClassesTest[i] = meanClassLabelVectorAllClassesTest[i] / binaryClassesTest.size();
    }
}

public ArrayList<String> getRuleAntecedents(String rule) {
    ArrayList<String> result = new ArrayList<>();
    int index;

    rule = rule.trim();
    index = rule.indexOf("=");
    rule = rule.substring(index + 1, rule.length());
    index = rule.indexOf("THEN");
    rule = rule.substring(0, index);

    if (!rule.contains("AND")) {
        result.add(rule);

    } else {
        while (!rule.isEmpty()) {
            index = rule.indexOf("AND");

            if (index == -1) {
                result.add(rule);
                break;
            }

            result.add(rule.substring(0, index));
            rule = rule.substring(index + 3, rule.length());
        }
    }

    return result;
}

public double[] getRuleConsequent(String rule) {
    int indexThen = rule.indexOf("THEN");
    String[] strSplit = rule.substring(indexThen + 5).split(" ");
    double[] result = new double[strSplit.length];

    for (int i = 0; i < strSplit.length; i++) {
        result[i] = Double.parseDouble(strSplit[i]);
    }

    return result;
}

public double[] getRuleConsequentClus(String rule) {
    rule = rule.replace("[", "");
    rule = rule.replace("]", "");

    int indexThen = rule.indexOf("THEN");
    String[] strSplit = rule.substring(indexThen + 5).split(",");
    double[] result = new double[strSplit.length];

    for (int i = 0; i < strSplit.length; i++) {
        result[i] = Double.parseDouble(strSplit[i]);
    }

    return result;
}

public int getAttrIndexTestedInARule(String antecedent) {
    antecedent = antecedent.trim();
    int index;
    String attr;

    if (!String.valueOf(antecedent.charAt(0)).matches("^[0-9]+$") && !antecedent.startsWith("-")) { // so is: attributeValue <= supLim  or attributeValue >= infLim, or > or <     
        if (antecedent.contains(">") && !antecedent.contains("=")) {
            index = antecedent.indexOf(">");
            attr = antecedent.substring(0, index);
            attr = attr.trim();

            return searchForAttr(attr);

        } else if (antecedent.contains("<") && !antecedent.contains("=")) {
            index = antecedent.indexOf("<");
            attr = antecedent.substring(0, index);
            attr = attr.trim();

            return searchForAttr(attr);

        } else { //<= or >=
            index = antecedent.indexOf("=");
            attr = antecedent.substring(0, index - 1);
            attr = attr.trim();

            return searchForAttr(attr);
        }

    } else { // then: // infLim <= attributeValue <= supLim
        index = antecedent.indexOf("=");
        attr = antecedent.substring(index + 2, antecedent.lastIndexOf("<="));
        attr = attr.trim();

        return searchForAttr(attr);
    }
}

public int searchForAttr(String attr) {
    for (int j = 0; j < attributes.length; j++) {
        if (attributes[j].contentEquals(attr)) {
            return j;
        }
    }

    return -1;
}

public int classifyWithARule(String[] example, String rule) {
    ArrayList<String> antecedents = getRuleAntecedents(rule);
//    ArrayList<String> antecedents = getRuleAntecedentsClus(rule);
    int coverage = 0;
    int index;
    Double infLim;
    Double supLim;
    int posAttribute;
    double attributeValue;

    for (int i = 0; i < antecedents.size(); i++) {
        String test = antecedents.get(i);
        test = test.trim();
        posAttribute = getAttrIndexTestedInARule(test);
        attributeValue = Double.parseDouble(example[posAttribute]);

        if (test.contains(">=")) {
            index = test.indexOf(">=");
            infLim = Double.parseDouble(test.substring(index + 2, test.length()));
            coverage = greaterEqual(attributeValue, infLim);

        } else if (test.startsWith("-") || String.valueOf(test.charAt(0)).matches("^[0-9]+$")) { //<= att <=
            infLim = Double.parseDouble(test.substring(0, test.indexOf("<=")));
            supLim = Double.parseDouble(test.substring(test.lastIndexOf("<=") + 2, test.length()));
            coverage = compoundTerm(infLim, supLim, attributeValue);

        } else if (test.contains("<=")) {
            index = test.indexOf("<=");
            supLim = Double.parseDouble(test.substring(index + 2, test.length()));
            coverage = lessEqual(attributeValue, supLim);

        } else if (test.contains(">")) {
            index = test.indexOf(">");
            infLim = Double.parseDouble(test.substring(index + 1, test.length()));
            coverage = greater(attributeValue, infLim);

        } else {
            index = test.indexOf("<");
            supLim = Double.parseDouble(test.substring(index + 1, test.length()));
            coverage = less(attributeValue, supLim);

        }

        if (coverage == 0) {
            return coverage;
        }
    }

    return coverage;
}

private static int lessEqual(double attributeValue, double supLim) {
    if (attributeValue <= supLim) {
        return 1;
    } else {
        return 0;
    }
}

private static int less(double attributeValue, double supLim) {
    if (attributeValue < supLim) {
        return 1;
    } else {
        return 0;
    }
}

public static int greaterEqual(double attributeValue, double infLim) {
    if (attributeValue >= infLim) {
        return 1;
    } else {
        return 0;
    }
}

public static int greater(double attributeValue, double infLim) {
    if (attributeValue > infLim) {
        return 1;
    } else {
        return 0;
    }
}

private static int compoundTerm(double infLim, double supLim, double attributeValue) {
    int left = greaterEqual(attributeValue, infLim);
    int right = lessEqual(attributeValue, supLim);

    if (left == 1 && right == 1) {
        return 1;
    } else {
        return 0;
    }
}

public String[] leConfig() {
    String regExp[] = {"nameDataset =",
        "specificFold =",
        "folds =",
        "pathTrainDataset =",
        "pathTestDataset =",
        "pathRules =",
        "pathToSavePredictions ="};

    Pattern comment = Pattern.compile("#");
    String[] result = new String[7];

    for (int i = 0; i < regExp.length; i++) {
        try {
//                FileReader reader = new FileReader("/home/geantrindade/Dropbox/posGrad/GACerriMaven/src/main/java/hmc_ga/teste.txt");
//            FileReader reader = new FileReader("teste.txt");
            FileReader reader = new FileReader("C:\\Users\\gean_\\Dropbox\\posGrad\\GAs\\HC-GA\\src\\main\\java\\br\\ufscar\\hcga\\config\\test.txt");
            BufferedReader buffReader = new BufferedReader(reader);

            Pattern pattern = Pattern.compile(regExp[i]);
            String line = null;

            while ((line = buffReader.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                Matcher m1 = comment.matcher(line);

                if (m.find() && !m1.find()) {
                    String[] vectorLine = line.split(" = ");
                    result[i] = vectorLine[1];
                    break;
                }
            }

            buffReader.close();
            reader.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    return result;
}

public double fMeasurePrediction(ArrayList<int[]> binaryClassesTestOrTrain, int indexExample,
        double[] prediction) {
    double sumIntersection = 0;
    double sumReal = 0;
    double sumPredicted = 0;

    ArrayList<int[]> binaryClasses = binaryClassesTestOrTrain;

    for (int j = 0; j < classes.length; j++) {
        if (prediction[j] >= 0.5 && binaryClasses.get(indexExample)[j] == 1) {
            sumIntersection++;
        }

        if (prediction[j] > 0) {
            sumPredicted++;
        }

        if (binaryClasses.get(indexExample)[j] == 1) {
            sumReal++;
        }
    }

    //Hierarchical Precision
    double hPrecision = sumIntersection / sumPredicted;

    //Hierarchical Recall
    double hRecall = sumIntersection / sumReal;

    //Fmeasure
    double fmeasure = 0;
    if (hPrecision != 0 || hRecall != 0) {
        fmeasure = (2 * hPrecision * hRecall) / (hPrecision + hRecall);
    }

    return fmeasure;
}

//for all rules
public ArrayList<ArrayList<Integer>> obtainRulesIndexCoveredExamples(ArrayList<String> rules) {
    ArrayList<ArrayList<Integer>> indexCoveredExamples = new ArrayList<>();

    for (int i = 0; i < rules.size(); i++) {
        indexCoveredExamples.add(i, new ArrayList<Integer>());
    }

    for (int i = 0; i < datasetTrain.size(); i++) {
        String[] example = datasetTrain.get(i);

        for (int j = 0; j < rules.size(); j++) {
            int coverage = classifyWithARule(example, rules.get(j));

            if (coverage == 1) { //Rule satisfies example
                indexCoveredExamples.get(j).add(i);
            }
        }
    }

    return indexCoveredExamples;
}

//just rules who can cover the test example
public int getExamplesBasedOnSimilarity(String[] testExample, ArrayList<String> selectedRules, ArrayList<ArrayList<Integer>> selectedRulesIndexCoveredExamples, int rangeOfConsideration) {
    int result = 0;

    for (int i = 0; i < selectedRules.size(); i++) {
        if (checkSimilarity(testExample, selectedRulesIndexCoveredExamples.get(i), rangeOfConsideration)) {
            result += 1;
        }
    }

    return result;
}

//return the indexes
public ArrayList<Integer> getExamplesBasedOnSimilarity(String[] testExample, int rangeOfConsideration) {
    ArrayList<Integer> trainExamples = new ArrayList<>();

    for (int i = 0; i < datasetTrain.size(); i++) {
        if (checkSimilarityBetweenExamples(testExample, datasetTrain.get(i), rangeOfConsideration)) {
            trainExamples.add(i);
        }
    }

    return trainExamples;
}

public Integer getMostSimilarExampleBasedOnSum(ArrayList<Integer> similarTrainExamples, String[] testExample) {
    int valueTest = 0;
    for (int i = 0; i < testExample.length - 1; i++) {
        valueTest += Integer.valueOf(testExample[i]);
    }

    int valueTrain = 0, minDiferrence = 0, diferrenceAux = 0, indexMostSimilar = 0;
    for (int i = 0; i < similarTrainExamples.size(); i++) {
        String[] trainExample = datasetTrain.get(similarTrainExamples.get(i));

        for (int j = 0; j < trainExample.length - 1; j++) {
            valueTrain += Integer.valueOf(trainExample[j]);
        }

        diferrenceAux = Math.abs(valueTest - valueTrain);

        if (minDiferrence == 0) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        if (diferrenceAux < minDiferrence) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        valueTrain = 0;
        diferrenceAux = 0;
    }

    return indexMostSimilar;
}

public Integer getMostSimilarExampleBasedOnAverage(ArrayList<Integer> similarTrainExamples, String[] testExample) {
    int valueTest = 0;
    for (int i = 0; i < testExample.length - 1; i++) {
        valueTest += Integer.valueOf(testExample[i]);
    }
    valueTest = valueTest / testExample.length;

    int valueTrain = 0, minDiferrence = 0, diferrenceAux = 0, indexMostSimilar = 0;
    for (int i = 0; i < similarTrainExamples.size(); i++) {
        String[] trainExample = datasetTrain.get(similarTrainExamples.get(i));

        for (int j = 0; j < trainExample.length - 1; j++) {
            valueTrain += Integer.valueOf(trainExample[j]);
        }
        valueTrain = valueTrain / trainExample.length;

        diferrenceAux = Math.abs(valueTest - valueTrain);

        if (minDiferrence == 0) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        if (diferrenceAux < minDiferrence) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        valueTrain = 0;
        diferrenceAux = 0;
    }

    return indexMostSimilar;
}

public Integer getMostSimilarExampleBasedOnEachDifference(ArrayList<Integer> similarTrainExamples, String[] testExample) {
    int[] valuesTest = new int[testExample.length];

    for (int i = 0; i < testExample.length - 1; i++) {
        valuesTest[i] = Integer.valueOf(testExample[i]);
    }

    int[] valuesTrain = new int[testExample.length];
    int minDiferrence = 0, diferrenceAux = 0, indexMostSimilar = 0;

    for (int i = 0; i < similarTrainExamples.size(); i++) {
        String[] trainExample = datasetTrain.get(similarTrainExamples.get(i));

        for (int j = 0; j < trainExample.length - 1; j++) {
            valuesTrain[j] = Integer.valueOf(trainExample[j]);
            diferrenceAux += Math.abs(valuesTest[j] - valuesTrain[j]); //removes minus signal
        }

        if (minDiferrence == 0) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        if (diferrenceAux < minDiferrence) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        diferrenceAux = 0;
    }

    return indexMostSimilar;
}

public Integer getMostSimilarExampleBasedOnEachDifferenceAverage(ArrayList<Integer> similarTrainExamples, String[] testExample) {
    int[] valuesTest = new int[testExample.length];

    for (int i = 0; i < testExample.length - 1; i++) {
        valuesTest[i] = Integer.valueOf(testExample[i]);
    }

    int[] valuesTrain = new int[testExample.length];
    int minDiferrence = 0, diferrenceAux = 0, indexMostSimilar = 0;

    for (int i = 0; i < similarTrainExamples.size(); i++) {
        String[] trainExample = datasetTrain.get(similarTrainExamples.get(i));

        for (int j = 0; j < trainExample.length - 1; j++) {
            valuesTrain[j] = Integer.valueOf(trainExample[j]);
            diferrenceAux += Math.abs(valuesTest[j] - valuesTrain[j]); //removes minus signal
        }

        diferrenceAux = diferrenceAux / trainExample.length - 1;

        if (minDiferrence == 0) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        if (diferrenceAux < minDiferrence) {
            minDiferrence = diferrenceAux;
            indexMostSimilar = i;
        }

        diferrenceAux = 0;
    }

    return indexMostSimilar;
}

// checar quantas instancias tem similaridade, ai pegar a regra com maior numero
public boolean checkSimilarityBetweenExamples(String[] testExample, String[] trainExample, int rangeOfConsideration) {
    // until 60% of similarity, lower than that it is not acceptable (202/336) = 134
    // until 70% of similarity, lower than that it is not acceptable (235/336) = 101
    // until 80% of similarity, lower than that it is not acceptable (269/336) = 67
    // until 85% of similarity, lower than that it is not acceptable (269/336) = 50
    // until 90% of similarity, lower than that it is not acceptable (302/336) = 34
    // until 95% of similarity, lower than that it is not acceptable (319/336) = 17
    // until 98% of similarity, lower than that it is not acceptable (329/336) = 7
    int tolerance = 0;

    for (int j = 0; j < trainExample.length - 1; j++) {
        int valueTrain = Integer.valueOf(trainExample[j]);
        int valueTest = Integer.valueOf(testExample[j]);

        if (valueTest >= (valueTrain - rangeOfConsideration) && valueTest <= (valueTrain + rangeOfConsideration)) {
            if (j == trainExample.length - 2) {
                return true;
            }

        } else if (tolerance < 34) {
            if (j == trainExample.length - 2) {
                return true;
            } else {
                tolerance++;
            }

        } else {
            j = trainExample.length;
        }
    }

    return false;
}

// checar quantas instancias tem similaridade, ai pegar a regra com maior numero
public boolean checkSimilarity(String[] testExample, ArrayList<Integer> indexCoveredExamples, int rangeOfConsideration) {
    for (int i = 0; i < indexCoveredExamples.size(); i++) {
        String[] trainExample = datasetTrain.get(indexCoveredExamples.get(i));

        for (int j = 0; j < trainExample.length - 1; j++) {
            int valueTrain = Integer.valueOf(trainExample[j]);
            int valueTest = Integer.valueOf(testExample[j]);

            if (valueTest >= valueTrain - rangeOfConsideration && valueTest <= valueTrain + rangeOfConsideration) {
                if (j == trainExample.length - 2) {
                    return true;
                }
            } else {
                j = trainExample.length;
            }
        }
    }

    return false;
}

public int getBestRuleBasedOnCountMax(ArrayList<String> selectedRules) {
    for (int i = 0; i < selectedRules.size(); i++) {
        double[] consequent = getRuleConsequent(selectedRules.get(i));

        if (checkMaxs(consequent)) {
            return i;
        }
    }

    return 0;
}

// checar quantas instancias tem similaridade, ai pegar a regra com maior numero
public boolean checkMaxs(double[] ruleConsequent) {
    return ruleConsequent[0] == 1.0 || ruleConsequent[6] == 1.0;
}

public int indexBestRuleFromAGroup(ArrayList<String> rules) {
    ArrayList<String> rulesGroup1 = new ArrayList<>();
    ArrayList<String> rulesGroup2 = new ArrayList<>();

    for (int i = 0; i < rules.size(); i++) {
        double[] consequent = getRuleConsequent(rules.get(i));

        if (consequent[0] == 1.0) {
            rulesGroup1.add(rules.get(i));
        } else {
            rulesGroup2.add(rules.get(i));
        }
    }

    return 0;
}

public HashMap getRankingOfRulesByFmeasure(ArrayList<String> rules) {
    HashMap<String, Double> fitness = new HashMap<>();
    ArrayList<Integer> numberCoveredExamples = new ArrayList<>();
    ArrayList<ArrayList<Integer>> indexCoveredExamples = new ArrayList<>();
    ArrayList<double[]> meanClassLabelVectorCovered = new ArrayList<>();

    for (int i = 0; i < rules.size(); i++) {
        numberCoveredExamples.add(0);
        indexCoveredExamples.add(i, new ArrayList<Integer>());
        meanClassLabelVectorCovered.add(new double[classes.length]);
    }

    for (int i = 0; i < datasetTrain.size(); i++) {
        String[] example = datasetTrain.get(i);

        for (int j = 0; j < rules.size(); j++) {
            int coverage = classifyWithARule(example, rules.get(j));
            numberCoveredExamples.set(j, numberCoveredExamples.get(j) + coverage);

            if (coverage == 1) { //Rule satisfies example
                indexCoveredExamples.get(j).add(i);
            }
        }
    }

    ArrayList<int[]> binaryClasses = binaryClassesTrain;

    for (int i = 0; i < rules.size(); i++) {

        for (int j = 0; j < indexCoveredExamples.get(i).size(); j++) {
            int[] binaryVector = binaryClasses.get(indexCoveredExamples.get(i).get(j));

            for (int k = 0; k < binaryVector.length; k++) {
                meanClassLabelVectorCovered.get(i)[k] += binaryVector[k];
            }
        }

        if (indexCoveredExamples.get(i).size() > 0) {
            for (int j = 0; j < meanClassLabelVectorCovered.get(i).length; j++) {
                meanClassLabelVectorCovered.get(i)[j] = meanClassLabelVectorCovered.get(i)[j] / indexCoveredExamples.get(i).size();
            }
        }

        double[][] matrixPredictions = new double[numberCoveredExamples.get(i)][classes.length];
        for (int j = 0; j < numberCoveredExamples.get(i); j++) {
            System.arraycopy(meanClassLabelVectorCovered.get(i), 0, matrixPredictions[j], 0, classes.length);
        }

        fitness.put(rules.get(i), evaluationFmeasureFitness(matrixPredictions, indexCoveredExamples.get(i)));
    }

    return fitness;
}

public double evaluationFmeasureFitness(double[][] predictedClasses, ArrayList<Integer> indexExamples) {
    double fmeasure = 0;
    double sumIntersection = 0;
    double minSumPredicted = 0;
    double sumReal = 0;

    //Matrix to store the outputs on the test data
    int[][] binaryMatrix = new int[indexExamples.size()][classes.length];

    applyThresholds(binaryMatrix, predictedClasses, 0.5, 0);

    for (int i = 0; i < indexExamples.size(); i++) {
        int numInst = indexExamples.get(i);

        double sumPredictedExample = 0;
        double sumRealExample = 0;

        for (int j = 0; j < classes.length; j++) {
            if (binaryMatrix[i][j] == 1 && binaryClassesTrain.get(numInst)[j] == 1) {
                sumIntersection++;
            }

            if (binaryMatrix[i][j] == 1) {
                sumPredictedExample++;
            }

            if (binaryClassesTrain.get(numInst)[j] == 1) {
                sumRealExample++;
                sumReal++;
            }
        }

        //Get the minimum value. This will not penalize over-specialization
        if (sumPredictedExample < sumRealExample) {
            minSumPredicted += sumPredictedExample;
        } else {
            minSumPredicted += sumRealExample;
        }
    }

    //Hierarchical Precision
    double hPrecision = 0.0;
    if (minSumPredicted != 0) {
        hPrecision = sumIntersection / minSumPredicted;
    }

    //Hierarchical Recall
    double hRecall = 0.0;
    if (sumReal != 0) {
        hRecall = sumIntersection / sumReal;
    }

    //Fmeasure
    if (hPrecision != 0 || hRecall != 0) {
        fmeasure = (2 * hPrecision * hRecall) / (hPrecision + hRecall);
    }

    return fmeasure;
}

public HashMap getRankingOfRulesByFitness(ArrayList<String> rules) {
    HashMap<String, Double> fitness = new HashMap<>();
    ArrayList<Integer> numberCoveredExamples = new ArrayList<>();
    ArrayList<ArrayList<Integer>> indexCoveredExamples = new ArrayList<>();
    ArrayList<ArrayList<Integer>> indexUncoveredExamples = new ArrayList<>();
    ArrayList<double[]> meanClassLabelVectorCovered = new ArrayList<>();
    ArrayList<double[]> meanClassLabelVectorUncovered = new ArrayList<>();

    for (int i = 0; i < rules.size(); i++) {
        numberCoveredExamples.add(0);
        indexCoveredExamples.add(i, new ArrayList<Integer>());
        indexUncoveredExamples.add(i, new ArrayList<Integer>());
        meanClassLabelVectorCovered.add(new double[classes.length]);
        meanClassLabelVectorUncovered.add(new double[classes.length]);
    }

    for (int i = 0; i < datasetTrain.size(); i++) {
        String[] example = datasetTrain.get(i);

        for (int j = 0; j < rules.size(); j++) {
            int coverage = classifyWithARule(example, rules.get(j));
            numberCoveredExamples.set(j, numberCoveredExamples.get(j) + coverage);

            if (coverage == 1) { //Rule satisfies example
                indexCoveredExamples.get(j).add(i);
            } else {
                indexUncoveredExamples.get(j).add(i);
            }
        }
    }

    ArrayList<int[]> binaryClasses = binaryClassesTrain;

    for (int i = 0; i < rules.size(); i++) {
        //Covered examples
        for (int j = 0; j < indexCoveredExamples.get(i).size(); j++) {
            int[] binaryVector = binaryClasses.get(indexCoveredExamples.get(i).get(j));

            for (int k = 0; k < binaryVector.length; k++) {
                meanClassLabelVectorCovered.get(i)[k] += binaryVector[k];
            }
        }

        if (indexCoveredExamples.get(i).size() > 0) {
            for (int j = 0; j < meanClassLabelVectorCovered.get(i).length; j++) {
                meanClassLabelVectorCovered.get(i)[j] = meanClassLabelVectorCovered.get(i)[j] / indexCoveredExamples.get(i).size();
            }
        }

        //Uncovered examples
        for (int j = 0; j < indexUncoveredExamples.get(i).size(); j++) {
            int[] binaryVector = binaryClasses.get(indexUncoveredExamples.get(i).get(j));

            for (int k = 0; k < binaryVector.length; k++) {
                meanClassLabelVectorUncovered.get(i)[k] += binaryVector[k];
            }
        }

        if (indexUncoveredExamples.get(i).size() > 0) {
            for (int j = 0; j < meanClassLabelVectorUncovered.get(i).length; j++) {
                meanClassLabelVectorUncovered.get(i)[j] = meanClassLabelVectorUncovered.get(i)[j] / indexUncoveredExamples.get(i).size();
            }
        }
    }

    setMeanClassLabelVectorAllTrain(pathTrainDataset + nameDataset.toLowerCase() + specificFold + "trainatt.arff");

    ArrayList<Double> AUPRCs = getAUPRC(numberCoveredExamples, meanClassLabelVectorCovered, indexCoveredExamples);
    ArrayList<Double> VarianceGains = getVarianceGain(indexCoveredExamples, indexUncoveredExamples, numberCoveredExamples,
            meanClassLabelVectorCovered, meanClassLabelVectorUncovered);

    if (AUPRCs.size() == VarianceGains.size()) {
        for (int i = 0; i < rules.size(); i++) {
            fitness.put(rules.get(i), (0.3 * AUPRCs.get(i)) + (0.7 * VarianceGains.get(i)));
        }
    } else {
        System.out.println("fatal error");
        System.exit(0);
    }

    return fitness;
}

public void countRules(String rule) {
    int cont = -1, index = 0;

    while (index != -1) {
        cont++;
        index = rule.indexOf("THEN", index);
        rule = rule.replaceFirst("THEN", "");
//        if(index != -1){
//            rule = rule.substring(index+4, rule.length());
//            cont++;
//        }
    }
    numberOfRules = cont;
}

public double getConsequentSum(double[] rule) {
    double sum = 0;
    for (int i = 0; i < rule.length; i++) {
        sum += rule[i];
    }

    return sum;
}

public double getConsequentAverage(double[] rule) {
    return (getConsequentSum(rule) / rule.length);
}

public double getNumberOfActivatedClasses(double[] rule) {
    double cont = 0;

    for (int i = 0; i < rule.length; i++) {
        if (rule[i] >= 0.5) {
            cont++;
        }
    }

    return cont;
}

public double getActivatedClassesMultiLevels(double[] rule) {
    double value = 0;
    double temp = 0;
//    1, 1.1, 1.1.1, 1.1.2, 1.4, 1.5, 2, 2.1, 2.1.1, 2.1.1.1, 2.1.1.2, 2.1.1.3, 2.1.1.8, 2.1.1.9

    double[] weightingAux = {0.95, 0.75, 0.6, 0.6, 0.75, 0.75, 0.95, 0.75, 0.45, 0.3, 0.3, 0.3, 0.3, 0.3};
    for (int i = 0; i < rule.length; i++) {
        if (rule[i] >= 0.5) {
            temp = rule[i] * weightingAux[i];
            value += temp;
        }
    }

    return value;
}

public double getActivatedClassesMultiLevelsAndAverage(double[] rule) {
    double value = 0;
    double temp = 0;
    for (int i = 0; i < rule.length; i++) {
//        if (rule[i] >= 0.5) {
        temp = rule[i] * weightingScheme[i];
        value += temp;
//        }
    }

    return value / rule.length;
}

public void escreveArquivo(String pathWithFile, String subject) {
    FileWriter writer;
    BufferedWriter bufferWriter;

    try {
        writer = new FileWriter(pathWithFile);
        bufferWriter = new BufferedWriter(writer);

        writer.write(subject);
        writer.flush();
        writer.close();

    } catch (IOException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);
    }
}

public int getIndexRuleWithBestFitness(HashMap<Integer, Double> rules) {
    double temp, bestValue = 0.0;
    int indexBestValue = -1;

    for (Integer key : rules.keySet()) {
        temp = rules.get(key);

        if (temp > bestValue) {
            bestValue = temp;
            indexBestValue = key;
        }
    }

    return indexBestValue;
}

public ArrayList<Double> getAUPRC(ArrayList<Integer> numberCoveredExamples, ArrayList<double[]> meanClassLabelVectorCovered, ArrayList<ArrayList<Integer>> indexCoveredExamples) {
    ArrayList<Double> AUPRC = new ArrayList<>();

    for (int i = 0; i < numberCoveredExamples.size(); i++) {
        //Obtain the predictions
        double[][] matrixPredictions = new double[numberCoveredExamples.get(i)][classes.length];

        for (int j = 0; j < numberCoveredExamples.get(i); j++) {
            System.arraycopy(meanClassLabelVectorCovered.get(i), 0, matrixPredictions[j], 0, classes.length);
        }

        //min Mips = 5
        if (indexCoveredExamples.get(i).size() >= 5) {
            AUPRC.add(i, evaluationAUPRCFitness(matrixPredictions, indexCoveredExamples.get(i)));
        }
    }

    return AUPRC;
}

public double evaluationAUPRCFitness(double[][] matrixPredictions, ArrayList<Integer> indexExamples) {

    //Store precision and recall values
    ArrayList<double[]> valuesPrecisionRecall = new ArrayList<double[]>();
    ArrayList<Double> thresholdValues = new ArrayList<Double>(Arrays.asList(0.0, 2.0, 4.0,
            6.0, 8.0, 10.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0, 26.0, 28.0, 30.0, 32.0, 34.0, 36.0, 38.0, 40.0, 42.0, 44.0,
            46.0, 48.0, 50.0, 52.0, 54.0, 56.0, 58.0, 60.0, 62.0, 64.0, 66.0, 68.0, 70.0, 72.0, 74.0, 76.0, 78.0, 80.0, 82.0, 84.0,
            86.0, 88.0, 90.0, 92.0, 94.0, 96.0, 98.0, 100.0));

    double AUPRCFitness = 0;

    //Iterate over all thresholds
    for (int indexThres = 0; indexThres < thresholdValues.size(); indexThres++) {

        //Matrix to store the outputs on the test data after applying thresholds
        int[][] binaryMatrix = new int[indexExamples.size()][classes.length];

        //Threshold values used
        double threshold = thresholdValues.get(indexThres) / 100;

        //Apply the threshold
//            applyThresholds(binaryMatrix, matrixPredictions, threshold, 1);
        Evaluation.applyThresholds(binaryMatrix, matrixPredictions, threshold, 0);

        ArrayList<int[]> trueClasses = new ArrayList<int[]>();
        int[] classesB = new int[classes.length];

        for (int i = 0; i < indexExamples.size(); i++) {
            int posExample = indexExamples.get(i);
            System.arraycopy(binaryClassesTrain.get(posExample), 0, classesB, 0, classes.length);
            trueClasses.add(classesB);
        }

        //Hierarchical Precision and Recall evaluation metrics
        double[] evalResults = evaluationPrecRec(trueClasses, binaryMatrix);
        valuesPrecisionRecall.add(evalResults);

    }

    //Calculate AU(PRC)
    AUPRCFitness = calculateAUPRCFitness(valuesPrecisionRecall);

    return AUPRCFitness;

}

static double calculateAUPRCFitness(ArrayList<double[]> valuesPrecisionRecall) {

    double AUPRC = 0;
    ArrayList<ArrayList<Double>> precision = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> recall = new ArrayList<ArrayList<Double>>();

    int count = 0;

    for (int i = valuesPrecisionRecall.size() - 1; i > 0; i--) {

        //Recover data for interpolation
        double[] dataInterpolation = getDataInterpolation(valuesPrecisionRecall.get(i), valuesPrecisionRecall.get(i - 1));

        //Get points between A and B to interpolate
        ArrayList<ArrayList<Double>> points = Evaluation.getPoints(dataInterpolation, count);

        if (i < (valuesPrecisionRecall.size() - 1)) {
            precision.get(precision.size() - 1).remove(precision.get(precision.size() - 1).size() - 1);
            recall.get(recall.size() - 1).remove(recall.get(recall.size() - 1).size() - 1);
        }

        precision.add(points.get(0));
        recall.add(points.get(1));

        count++;

    }

    AUPRC = calculateAreaUnderCurve(recall, precision);

    return AUPRC;
}

public static double calculateAreaUnderCurve(ArrayList<ArrayList<Double>> recall,
        ArrayList<ArrayList<Double>> precision) {

    double AUPRC = 0;
    ArrayList<Double> x = new ArrayList<Double>();
    ArrayList<Double> y = new ArrayList<Double>();

    for (int i = 0; i < recall.size(); i++) {
        for (int j = 0; j < recall.get(i).size(); j++) {
            x.add(recall.get(i).get(j));
            y.add(precision.get(i).get(j));
        }
    }

    for (int i = 0; i < x.size() - 1; i++) {
        AUPRC += (x.get(i + 1) - x.get(i)) * y.get(i + 1)
                + (x.get(i + 1) - x.get(i)) * (y.get(i) - y.get(i + 1)) / 2;
    }

    return AUPRC;
}

public double[] evaluationPrecRec(ArrayList<int[]> trueClasses, int[][] predictedClasses) {
    //Store the results
    double[] evalResults = new double[5];

    //Sum of predicted and real classes
    double sumIntersection = 0;
    double sumPredicted = 0;
    double sumReal = 0;
    double FP = 0;

    for (int numInst = 0; numInst < trueClasses.size(); numInst++) {
        for (int i = 0; i < trueClasses.get(0).length; i++) {
            if (predictedClasses[numInst][i] == 1 && trueClasses.get(numInst)[i] == 1) {
                sumIntersection++;
            }

            if (predictedClasses[numInst][i] == 1) {
                sumPredicted++;
            }

            if (predictedClasses[numInst][i] == 1 && trueClasses.get(numInst)[i] == 0) {
                FP++;
            }

            if (trueClasses.get(numInst)[i] == 1) {
                sumReal++;
            }
        }
    }

    //Hierarchical Precision
    double hPrecision = 0.0;
    if (sumPredicted != 0) {
        hPrecision = sumIntersection / sumPredicted;
    }

    //Hierarchical Recall
    double hRecall = 0.0;
    if (sumReal != 0) {
        hRecall = sumIntersection / sumReal;
    }

    evalResults[0] = hPrecision;
    evalResults[1] = hRecall;

    evalResults[2] = sumIntersection; //TP
    evalResults[3] = FP;              //FP
    evalResults[4] = sumReal;         //True

    return evalResults;
}

public ArrayList<Double> getVarianceGain(ArrayList<ArrayList<Integer>> indexCoveredExamples, ArrayList<ArrayList<Integer>> indexUncoveredExamples,
        ArrayList<Integer> numberCoveredExamples, ArrayList<double[]> meanClassLabelVectorCovered, ArrayList<double[]> meanClassLabelVectorUncovered) {

    ArrayList<Double> varianceGain = new ArrayList<>();

    for (int i = 0; i < indexCoveredExamples.size(); i++) {
        double[][] matrixPredictions = new double[numberCoveredExamples.get(i)][classes.length];

        for (int j = 0; j < numberCoveredExamples.get(i); j++) {
            System.arraycopy(meanClassLabelVectorCovered.get(i), 0, matrixPredictions[j], 0, classes.length);
        }

        //Variance gain of the set of all training examples
        double[] meanClassesLabelAll = meanClassLabelVectorAllClassesTrain;
        /*if (Parameters.getMultiLabel() == 0) {
             meanClassesLabelAll = Results.getHigherProbabilities(meanClassesLabelAll);
             }*/
        double varianceGainAll = getVarianceGain(datasetTrain.size(), meanClassesLabelAll);
        //Variance gain of the set of covered examples
        double varianceGainCovered = getVarianceGain(indexCoveredExamples.get(i), meanClassLabelVectorCovered.get(i));
        //Variance gain of the set of uncovered examples
        double varianceGainUncovered = getVarianceGain(indexUncoveredExamples.get(i), meanClassLabelVectorUncovered.get(i));

        int numTotalExamples = datasetTrain.size();
        double term11 = (double) indexCoveredExamples.get(i).size() / (double) numTotalExamples;
        double term1 = term11 * varianceGainCovered;
        double term22 = (double) indexUncoveredExamples.get(i).size() / (double) numTotalExamples;
        double term2 = term22 * varianceGainUncovered;

//        Variance Gain Fitness
//        -------------------------------------------------------------------------------
        double varianceGainTotal = varianceGainAll - term1 - term2;
//        double percentageCoverage = (double) numberCoveredExamples.get(i) / datasetTrain.size();
//        (0.4 * varianceGainTotal) + (0.6 * percentageCoverage) / percentageCoverage;
        varianceGain.add(i, varianceGainTotal);
    }

    return varianceGain;
}

public double getVarianceGain(int numExamples, double[] meanClassLabel) {
    double varianceGain = 0;
    double[] weights = weightingScheme;
    ArrayList<int[]> binaryClasses = binaryClassesTrain;

    for (int i = 0; i < numExamples; i++) {
        double sum = 0;
        for (int j = 0; j < meanClassLabel.length; j++) {
            sum += weights[j] * Math.pow((binaryClasses.get(i)[j] - meanClassLabel[j]), 2);
        }
        varianceGain += sum;
    }

    varianceGain = varianceGain / numExamples;

    return varianceGain;
}

public double getVarianceGain(ArrayList<Integer> indexExamples, double[] meanClassLabel) {

    double varianceGain = 0;
    double[] weights = weightingScheme;
    ArrayList<int[]> binaryClasses = binaryClassesTrain;

    for (int i = 0; i < indexExamples.size(); i++) {
        double sum = 0;
        int[] binaryVector = binaryClasses.get(indexExamples.get(i));
        for (int j = 0; j < meanClassLabel.length; j++) {
            sum += weights[j] * Math.pow((binaryVector[j] - meanClassLabel[j]), 2);
        }
        varianceGain += sum;
    }

    if (indexExamples.size() > 0) {
        varianceGain = varianceGain / indexExamples.size();
    }

    return varianceGain;
}

public void lcnAnalysis() {
    numberOfClasses = 333;

    classes = "1,1.1,1.1.1,1.1.1.1,1.1.1.145,1.1.1.195,1.1.1.2,1.1.1.21,1.1.1.25,1.1.1.44,1.1.3,1.1.99,1.1.99.1,1.10,1.10.3,1.11,1.11.1,1.11.1.15,1.11.1.5,1.11.1.6,1.13,1.13.11,1.14,1.14.11,1.14.12,1.14.13,1.14.13.70,1.14.13.81,1.14.14,1.14.14.1,1.14.15,1.14.15.4,1.14.15.6,1.14.17,1.14.17.4,1.14.18,1.14.18.2,1.14.19,1.14.19.1,1.14.99,1.14.99.9,1.18,1.18.6,1.18.6.1,1.2,1.2.1,1.2.1.3,1.2.1.36,1.2.1.38,1.2.1.5,1.2.1.8,1.2.3,1.2.3.1,1.2.99,1.2.99.2,1.20,1.20.4,1.20.4.1,1.3,1.3.1,1.3.1.26,1.3.3,1.3.3.6,1.3.99,1.4,1.4.1,1.4.1.21,1.4.3,1.4.3.4,1.4.3.6,1.4.99,1.4.99.1,1.5,1.5.1,1.6,1.6.4,1.8,1.8.1,1.8.1.2,1.8.4,1.8.4.8,1.9,1.9.3,1.9.3.1,2,2.1,2.1.1,2.1.1.104,2.1.1.68,2.1.1.80,2.1.3,2.3,2.3.1,2.3.1.1,2.3.1.21,2.3.1.28,2.3.1.30,2.3.1.35,2.3.1.40,2.3.1.47,2.3.1.5,2.3.1.74,2.3.2,2.3.2.8,2.3.3,2.3.3.1,2.4,2.4.1,2.4.1.12,2.4.1.135,2.4.1.142,2.4.1.16,2.4.1.19,2.4.2,2.4.2.21,2.4.2.7,2.5,2.5.1,2.5.1.19,2.5.1.47,2.5.1.54,2.6,2.6.1,2.6.1.1,2.6.1.11,2.6.1.21,2.6.1.42,2.6.1.62,2.7,2.7.1,2.7.1.112,2.7.1.116,2.7.1.16,2.7.1.20,2.7.1.24,2.7.1.25,2.7.1.33,2.7.1.37,2.7.1.71,2.7.2,2.7.2.1,2.7.2.2,2.7.2.4,2.7.2.7,2.7.2.8,2.7.3,2.7.7,2.7.7.25,2.7.7.3,2.7.7.4,2.7.7.41,2.7.7.61,2.7.8,2.7.8.25,2.7.8.7,2.8,2.8.1,2.8.1.6,2.8.1.7,2.8.2,2.8.3,3,3.1,3.1.1,3.1.1.1,3.1.1.31,3.1.1.61,3.1.1.7,3.1.1.74,3.1.1.8,3.1.2,3.1.2.1,3.1.27,3.1.3,3.1.3.16,3.1.3.33,3.1.3.48,3.1.3.5,3.1.3.71,3.1.4,3.1.4.14,3.1.4.17,3.1.6,3.2,3.2.1,3.2.1.1,3.2.1.14,3.2.1.2,3.2.1.20,3.2.1.21,3.2.1.22,3.2.1.23,3.2.1.3,3.2.1.55,3.2.2,3.4,3.4.11,3.4.11.1,3.4.11.18,3.4.11.2,3.4.15,3.4.15.1,3.4.16,3.4.16.4,3.4.17,3.4.17.1,3.4.21,3.4.21.1,3.4.21.92,3.4.22,3.4.22.15,3.4.22.39,3.4.22.50,3.4.23,3.4.23.24,3.4.24,3.5,3.5.1,3.5.1.1,3.5.1.15,3.5.1.16,3.5.1.28,3.5.1.4,3.5.2,3.5.2.6,3.5.3,3.5.3.1,3.5.3.12,3.5.3.19,3.5.3.4,3.5.3.6,3.5.4,3.5.4.2,3.5.4.4,3.5.4.5,3.5.4.6,3.5.99,3.5.99.7,3.6,3.6.1,3.6.1.26,3.6.1.41,3.6.1.7,3.6.3,3.6.3.1,3.6.3.10,3.6.3.12,3.6.3.14,3.6.3.15,3.6.3.16,3.6.3.17,3.6.3.25,3.6.3.3,3.6.3.33,3.6.3.4,3.6.3.41,3.6.3.8,3.6.3.9,4,4.1,4.1.1,4.1.1.31,4.1.1.36,4.1.1.4,4.1.2,4.1.2.13,4.1.3,4.1.3.1,4.2,4.2.1,4.2.1.1,4.2.1.10,4.2.1.104,4.2.1.3,4.2.1.52,4.2.2,4.2.3,4.2.3.4,4.2.3.5,4.2.99,4.2.99.18,4.3,4.3.2,4.3.2.1,4.4,4.4.1,4.4.1.14,4.6,4.6.1,4.6.1.1,4.99,4.99.1,4.99.1.3,5,5.1,5.1.1,5.1.1.1,5.1.3,5.2,5.2.1,5.2.1.8,5.3,5.3.1,5.3.1.4,5.4,5.4.2,5.4.2.1,5.5,5.5.1,5.5.1.6,6,6.2,6.2.1,6.2.1.1,6.2.1.12,6.2.1.3,6.3,6.3.1,6.3.1.1,6.3.2,6.3.3,6.3.3.3,6.3.4,6.3.4.5,6.3.5,6.3.5.4,6.3.5.5,6.4,6.4.1,6.4.1.2,6.6,6.6.1,6.6.1.1".split(",");

    String mainPath = "C:\\Users\\gean_\\Dropbox\\posGrad\\Experimentos\\LCN Bruna";

    String data = "ECpfam";

//String[] abordagens = {"Abordagem_Exclusiva", "Abordagem_Inclusiva", "Abordagem_Menos_Exclusiva",
//        "Abordagem_Menos_Inclusiva", "Abordagem_Primos", "Abordagem_Primos_Exclusiva", "Abordagem_So_Primos"};
    String[] abordagens = {"Abordagem_Primos", "Abordagem_Primos_Exclusiva", "Abordagem_So_Primos"};

//    String[] classificadores = {"dtree", "knn", "mlp", "mlp2", "nb", "rforest", "svm"};
    String[] classificadores = {"svm"};

    ArrayList<ArrayList<int[]>> binaryClassesTestArray = new ArrayList<>();

    for (String abordagem : abordagens) {

        for (String classificador : classificadores) {
            String directory = mainPath + "\\" + data + "\\" + abordagem + "\\"
                    + classificador + "\\Evaluation\\";

            String readFile = "test";
            String saveFile = "";

            new File(directory).mkdirs();
            BufferedWriter writer;

            // ========================================================================
            // =========================== dataset test ===============================
            // ========================================================================
            for (int i = 1; i <= 10; i++) {
                if (binaryClassesTestArray.size() < i) {
                    ArrayList<String[]> dataset = getTestDataCSV(mainPath + "\\" + data + "\\"
                            + readFile + i + ".csv");
                    binaryClassesTestArray.add(i - 1, getClassesStructureTest(dataset));
                }

                saveFile = "realTestClasses_" + readFile + i + ".txt";

                try {
                    writer = new BufferedWriter(new FileWriter(directory + saveFile));

                    for (int j = 0; j < binaryClassesTestArray.get(i - 1).size(); j++) {
                        for (int k = 0; k < numberOfClasses; k++) {
                            writer.write(binaryClassesTestArray.get(i - 1).get(j)[k] + " ");
                        }

                        writer.newLine();
                    }

                    writer.flush();
                    writer.close();

                } catch (IOException ex) {
                    Logger.getLogger(Classes.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            // ========================================================================
            // ============================ predictions ===============================
            // =====================B  ===================================================
            for (int i = 1; i <= 10; i++) {
                readFile = "clasfResult-fold" + i;

                ArrayList<String[]> datasetPred = getTestDataCSV(mainPath + "\\" + data + "\\" + abordagem + "\\"
                        + classificador + "\\" + readFile + ".txt");
                ArrayList<int[]> binaryClassesPred = getClassesStructureTest(datasetPred);

                saveFile = "predictionClasses_" + readFile.substring(readFile.indexOf("fold"),
                        readFile.length()) + ".txt";

                try {
                    writer = new BufferedWriter(new FileWriter(directory + saveFile));

                    for (int j = 0; j < binaryClassesPred.size(); j++) {
                        for (int k = 0; k < numberOfClasses; k++) {
                            writer.write(binaryClassesPred.get(j)[k] + " ");
                        }

                        writer.newLine();
                    }

                    writer.flush();
                    writer.close();

                } catch (IOException ex) {
                    Logger.getLogger(Classes.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

public void exportRealClassesFile(int numberOfClasses, String classes, String datasetPathAndFile,
        String datasetName, String pathToSave, String trainOrTest) {
    this.numberOfClasses = numberOfClasses;

//    classes = "1,1.1,1.1.1,1.1.1.1,1.1.1.145,1.1.1.195,1.1.1.2,1.1.1.21,1.1.1.25,1.1.1.44,1.1.3,1.1.99,1.1.99.1,1.10,1.10.3,1.11,1.11.1,1.11.1.15,1.11.1.5,1.11.1.6,1.13,1.13.11,1.14,1.14.11,1.14.12,1.14.13,1.14.13.70,1.14.13.81,1.14.14,1.14.14.1,1.14.15,1.14.15.4,1.14.15.6,1.14.17,1.14.17.4,1.14.18,1.14.18.2,1.14.19,1.14.19.1,1.14.99,1.14.99.9,1.18,1.18.6,1.18.6.1,1.2,1.2.1,1.2.1.3,1.2.1.36,1.2.1.38,1.2.1.5,1.2.1.8,1.2.3,1.2.3.1,1.2.99,1.2.99.2,1.20,1.20.4,1.20.4.1,1.3,1.3.1,1.3.1.26,1.3.3,1.3.3.6,1.3.99,1.4,1.4.1,1.4.1.21,1.4.3,1.4.3.4,1.4.3.6,1.4.99,1.4.99.1,1.5,1.5.1,1.6,1.6.4,1.8,1.8.1,1.8.1.2,1.8.4,1.8.4.8,1.9,1.9.3,1.9.3.1,2,2.1,2.1.1,2.1.1.104,2.1.1.68,2.1.1.80,2.1.3,2.3,2.3.1,2.3.1.1,2.3.1.21,2.3.1.28,2.3.1.30,2.3.1.35,2.3.1.40,2.3.1.47,2.3.1.5,2.3.1.74,2.3.2,2.3.2.8,2.3.3,2.3.3.1,2.4,2.4.1,2.4.1.12,2.4.1.135,2.4.1.142,2.4.1.16,2.4.1.19,2.4.2,2.4.2.21,2.4.2.7,2.5,2.5.1,2.5.1.19,2.5.1.47,2.5.1.54,2.6,2.6.1,2.6.1.1,2.6.1.11,2.6.1.21,2.6.1.42,2.6.1.62,2.7,2.7.1,2.7.1.112,2.7.1.116,2.7.1.16,2.7.1.20,2.7.1.24,2.7.1.25,2.7.1.33,2.7.1.37,2.7.1.71,2.7.2,2.7.2.1,2.7.2.2,2.7.2.4,2.7.2.7,2.7.2.8,2.7.3,2.7.7,2.7.7.25,2.7.7.3,2.7.7.4,2.7.7.41,2.7.7.61,2.7.8,2.7.8.25,2.7.8.7,2.8,2.8.1,2.8.1.6,2.8.1.7,2.8.2,2.8.3,3,3.1,3.1.1,3.1.1.1,3.1.1.31,3.1.1.61,3.1.1.7,3.1.1.74,3.1.1.8,3.1.2,3.1.2.1,3.1.27,3.1.3,3.1.3.16,3.1.3.33,3.1.3.48,3.1.3.5,3.1.3.71,3.1.4,3.1.4.14,3.1.4.17,3.1.6,3.2,3.2.1,3.2.1.1,3.2.1.14,3.2.1.2,3.2.1.20,3.2.1.21,3.2.1.22,3.2.1.23,3.2.1.3,3.2.1.55,3.2.2,3.4,3.4.11,3.4.11.1,3.4.11.18,3.4.11.2,3.4.15,3.4.15.1,3.4.16,3.4.16.4,3.4.17,3.4.17.1,3.4.21,3.4.21.1,3.4.21.92,3.4.22,3.4.22.15,3.4.22.39,3.4.22.50,3.4.23,3.4.23.24,3.4.24,3.5,3.5.1,3.5.1.1,3.5.1.15,3.5.1.16,3.5.1.28,3.5.1.4,3.5.2,3.5.2.6,3.5.3,3.5.3.1,3.5.3.12,3.5.3.19,3.5.3.4,3.5.3.6,3.5.4,3.5.4.2,3.5.4.4,3.5.4.5,3.5.4.6,3.5.99,3.5.99.7,3.6,3.6.1,3.6.1.26,3.6.1.41,3.6.1.7,3.6.3,3.6.3.1,3.6.3.10,3.6.3.12,3.6.3.14,3.6.3.15,3.6.3.16,3.6.3.17,3.6.3.25,3.6.3.3,3.6.3.33,3.6.3.4,3.6.3.41,3.6.3.8,3.6.3.9,4,4.1,4.1.1,4.1.1.31,4.1.1.36,4.1.1.4,4.1.2,4.1.2.13,4.1.3,4.1.3.1,4.2,4.2.1,4.2.1.1,4.2.1.10,4.2.1.104,4.2.1.3,4.2.1.52,4.2.2,4.2.3,4.2.3.4,4.2.3.5,4.2.99,4.2.99.18,4.3,4.3.2,4.3.2.1,4.4,4.4.1,4.4.1.14,4.6,4.6.1,4.6.1.1,4.99,4.99.1,4.99.1.3,5,5.1,5.1.1,5.1.1.1,5.1.3,5.2,5.2.1,5.2.1.8,5.3,5.3.1,5.3.1.4,5.4,5.4.2,5.4.2.1,5.5,5.5.1,5.5.1.6,6,6.2,6.2.1,6.2.1.1,6.2.1.12,6.2.1.3,6.3,6.3.1,6.3.1.1,6.3.2,6.3.3,6.3.3.3,6.3.4,6.3.4.5,6.3.5,6.3.5.4,6.3.5.5,6.4,6.4.1,6.4.1.2,6.6,6.6.1,6.6.1.1".split(",");
    this.classes = classes.split(",");

    ArrayList<ArrayList<int[]>> binaryClasses = new ArrayList<>();

    new File(pathToSave).mkdirs();
    BufferedWriter writer;

    String fileName = "";

    // ========================================================================
    // =========================== dataset test ===============================
    // ========================================================================
    for (int i = 1; i <= 1; i++) {
        if (binaryClasses.size() < i) {
            readTestData(datasetPathAndFile);
            binaryClasses.add(i - 1, getClassesStructureTest(this.datasetTest));
        }

        fileName = datasetName + "_realClasses_" + trainOrTest + i + ".txt";

        try {
            writer = new BufferedWriter(new FileWriter(pathToSave + fileName));

            for (int j = 0; j < binaryClasses.get(i - 1).size(); j++) {
                for (int k = 0; k < numberOfClasses; k++) {
                    writer.write(binaryClasses.get(i - 1).get(j)[k] + " ");
                }

                writer.newLine();
            }

            writer.flush();
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(Classes.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public double[] fMeasurePredictionWholeDataset(ArrayList<String[]> dataset,
        ArrayList<int[]> binaryClassesTestOrTrain, String rule) {
    double sumIntersection = 0;
    double sumReal = 0;
    double sumPredicted = 0;
    double numCov = 0;

    double[] prediction = getRuleConsequent(rule);

    for (int i = 0; i < dataset.size(); i++) {
        int coverage = classifyWithARule(dataset.get(i), rule);

        if (coverage == 1) {
            for (int j = 0; j < classes.length; j++) {
                if (prediction[j] >= 0.5 && binaryClassesTestOrTrain.get(i)[j] == 1) {
                    sumIntersection++;
                }

                if (prediction[j] > 0) {
                    sumPredicted++;
                }

                if (binaryClassesTestOrTrain.get(i)[j] == 1) {
                    sumReal++;
                }
            }

            numCov++;
        }
    }

    double hPrecision = sumIntersection / sumPredicted;
    double hRecall = sumIntersection / sumReal;
    double hFmeasure = (2 * hPrecision * hRecall) / (hPrecision + hRecall);

    double[] ruleValues = {hFmeasure, numCov};

    return ruleValues;
}

//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
public void makePredictionsFirstRule(String rulesFile, String fullPathToSavePrediction) {
    ArrayList<String> rules = readRulesFile(rulesFile);
//    ArrayList<String> rules = readRulesFileClus(rulesFile);
    String[] example;
    int coverage;
    double[][] matrixPredictions = new double[binaryClassesTest.size()][binaryClassesTest.get(0).length];

    for (int i = 0; i < datasetTest.size(); i++) {
        example = datasetTest.get(i);
        coverage = 0;

        for (int j = 0; j < rules.size(); j++) {
            String rule = rules.get(j);
            coverage = classifyWithARule(example, rule);

            if (coverage == 1) {
                double[] vectorConsequent = getRuleConsequent(rule);
//                double[] vectorConsequent = getRuleConsequentClus(rule);
                System.arraycopy(vectorConsequent, 0, matrixPredictions[i], 0, vectorConsequent.length);
                break;
            }
        }

        //If no rule classify the example, apply the default rule  
        if (coverage == 0) {
            double[] defaultRule = meanClassLabelVectorAllClassesTrain.clone();
            System.arraycopy(defaultRule, 0, matrixPredictions[i], 0, defaultRule.length);
        }
    }

    try {
        PrintWriter writer = new PrintWriter(fullPathToSavePrediction + "predictionsFirstRule.txt", "UTF-8");

        for (int i = 0; i < matrixPredictions.length; i++) {
            for (int j = 0; j < matrixPredictions[i].length; j++) {
                writer.print(matrixPredictions[i][j] + " ");
            }

            writer.print("\n");
        }

        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);

    } catch (IOException ex) {
        Logger.getLogger(ClassificationWithRule.class.getName()).log(Level.SEVERE, null, ex);
    }
}

//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
public void makePredictionsFitnessBasedOnSimilarTrainInstances(String pathRules, String fullPathToSavePrediction) {
    ArrayList<String> rules = readRulesFile(pathRules);
    ArrayList<String> rulesThatCovers = new ArrayList<>();

    String[] example;
    int coverage;

    double[][] matrixPredictions = new double[binaryClassesTest.size()][binaryClassesTest.get(0).length];

    System.out.println("iniciando...");

    for (int i = 0; i < datasetTest.size(); i++) {
        example = datasetTest.get(i);
        int n = datasetTest.size() - i;
        System.out.println("remaining examples: " + n);

        for (int j = 0; j < rules.size(); j++) {
            String rule = rules.get(j);
            coverage = classifyWithARule(example, rule);

            if (coverage == 1) {
                rulesThatCovers.add(rule);
            }
        }

        if (!rulesThatCovers.isEmpty()) {
            ArrayList<Integer> trainExamplesSimilar = new ArrayList<>();
            int range = 0;

            while (trainExamplesSimilar.isEmpty()) {
                trainExamplesSimilar = getExamplesBasedOnSimilarity(example, range);
                range++;
            }

            int betterRuleIndex = 0;
            double temp = 0, temp2 = 0, betterValue = 0;

            if (!trainExamplesSimilar.isEmpty()) {

                for (int j = 0; j < rulesThatCovers.size() && betterValue < 1.0; j++) {
                    String rule = rulesThatCovers.get(j);
                    double[] consequent = getRuleConsequent(rule);

//                    for (int k = 0; k < trainExamplesSimilar.size() && temp < 1.0; k++) {
                    for (int k = 0; k < trainExamplesSimilar.size() && temp < 1.0; k++) {
                        temp = fMeasurePrediction(binaryClassesTrain, trainExamplesSimilar.get(k), consequent);
//                        temp += fMeasurePrediction(binaryClassesTrain, trainExamplesSimilar.get(k), consequent);
                        if (temp < temp2) {
                            temp = temp2;

                        } else {
                            temp2 = temp;
                        }
                    }

//                    temp = temp / trainExamplesSimilar.size();
                    if (temp > betterValue) {
                        betterValue = temp;
                        betterRuleIndex = j;
                    }

                    temp = 0;
                    temp2 = 0;
                }

                double[] vectorConsequent = getRuleConsequent(rulesThatCovers.get(betterRuleIndex));
                System.arraycopy(vectorConsequent, 0, matrixPredictions[i], 0, vectorConsequent.length);

            } else {
                System.out.println("default rule");
                double[] defaultRule = meanClassLabelVectorAllClassesTrain.clone();
                System.arraycopy(defaultRule, 0, matrixPredictions[i], 0, defaultRule.length);
            }

            rulesThatCovers.clear();
        }
    }

    try {
        PrintWriter writer = new PrintWriter(fullPathToSavePrediction + "predictionsBasedOnSimilarTrainInstances.txt", "UTF-8");

        for (int i = 0; i < matrixPredictions.length; i++) {
            for (int j = 0; j < matrixPredictions[i].length; j++) {
                writer.print(matrixPredictions[i][j] + " ");
            }

            writer.print("\n");
        }

        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);

    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);
    }
}

//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
public void makePredictionsFitnessBasedOnTrain(String pathRules, String fullPathToSavePrediction) {
    ArrayList<String> rules = readRulesFile(pathRules);
    ArrayList<String> selectedRules = new ArrayList<>();
    HashMap<String, Integer> rulesIndexes = new HashMap<>();
    HashMap<Integer, Double> rulesFitness = new HashMap<>();
    HashMap<Integer, Double> rulesNumCov = new HashMap<>();

    String[] example;
    int coverage;

    double[][] matrixPredictions = new double[binaryClassesTest.size()][binaryClassesTest.get(0).length];

    System.out.println("iniciando...");

    for (int i = 0; i < datasetTest.size(); i++) {
        example = datasetTest.get(i);

        for (int j = 0; j < rules.size(); j++) {
            String rule = rules.get(j);
            coverage = classifyWithARule(example, rule);

            if (coverage == 1) {
                if (rulesIndexes.get(rule) == null) {
                    rulesIndexes.put(rule, j);

                    double[] rulesValues = fMeasurePredictionWholeDataset(datasetTrain, binaryClassesTrain, rule);
                    double hF = rulesValues[0];
                    rulesFitness.put(j, hF);

                    double numCov = rulesValues[1];
                    rulesNumCov.put(j, numCov);
                } else {
                    System.out.println("fitness and cov already calculated!");
                }

                selectedRules.add(rule);
            }
        }

        if (!selectedRules.isEmpty()) {
            HashMap<Integer, Double> selectedRulesFitness = new HashMap<>();

            //get temp fitnesses
            for (int j = 0; j < selectedRules.size(); j++) {
                String rule = selectedRules.get(j);
                int indexId = rulesIndexes.get(rule);
                selectedRulesFitness.put(indexId, rulesFitness.get(indexId));
            }

            double temp, betterValue = 0;
            int betterRuleIndex = -1;

            HashMap<Integer, Double> selectedRulesCov = new HashMap<>();

            //find the rule with the best fitness
            for (Integer index : selectedRulesFitness.keySet()) {
                temp = selectedRulesFitness.get(index);

                if (temp > betterValue) {
                    betterValue = temp;
                    betterRuleIndex = index;

                } else if (temp == betterValue) {
                    selectedRulesCov.put(betterRuleIndex, betterValue);
                    selectedRulesCov.put(index, temp);
                }
            }

            //verify if there are equal fitnesses
            if (!selectedRulesCov.isEmpty()) {
                temp = 0;
                betterValue = 0;
                betterRuleIndex = -1;

                //get temp examples covered
                for (int j = 0; j < selectedRules.size(); j++) {
                    String rule = selectedRules.get(j);
                    int indexId = rulesIndexes.get(rule);
                    selectedRulesCov.put(indexId, rulesNumCov.get(indexId));
                }

                for (Integer index : selectedRulesCov.keySet()) {
                    temp = selectedRulesCov.get(index);

                    if (temp > betterValue) {
                        betterValue = temp;
                        betterRuleIndex = index;
                    }
                }
                System.out.println("numCov used!");
            }

            System.out.println("\nbetterValue: " + betterValue);
            System.out.println("betterRuleIndex: " + betterRuleIndex);

            double[] vectorConsequent = getRuleConsequent(rules.get(betterRuleIndex));
            System.arraycopy(vectorConsequent, 0, matrixPredictions[i], 0, vectorConsequent.length);

        } else {
            System.out.println("default rule");
            //If no rule classify the example, apply the default rule
            double[] defaultRule = meanClassLabelVectorAllClassesTrain.clone();
            System.arraycopy(defaultRule, 0, matrixPredictions[i], 0, defaultRule.length);
        }

        System.out.println("\n");
        selectedRules.clear();
    }

    try {
        PrintWriter writer = new PrintWriter(fullPathToSavePrediction + "predictionsBasedOnTrain.txt", "UTF-8");

        for (int i = 0; i < matrixPredictions.length; i++) {
            for (int j = 0; j < matrixPredictions[i].length; j++) {
                writer.print(matrixPredictions[i][j] + " ");
            }

            writer.print("\n");
        }

        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);

    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);
    }

    System.out.println(
            "\nfinalizou!!!!");
}

//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
public void makePredictionsFitnessBasedOnTest(String pathRules, String fullPathToSavePrediction) {
    ArrayList<String> rules = readRulesFile(pathRules);
    String[] example;

    double[][] matrixPredictions = new double[binaryClassesTest.size()][binaryClassesTest.get(0).length];

    for (int i = 0; i < datasetTest.size(); i++) {
        example = datasetTest.get(i);

        double temp, betterValue = 0.0;
        int betterRuleIndex = 0;

        for (int j = 0; j < rules.size(); j++) {
            if (betterValue == 1.0) {
                break;
            }

            String rule = rules.get(j);
            double[] consequent = getRuleConsequent(rule);
            temp = fMeasurePrediction(binaryClassesTest, i, consequent);

            if (temp > betterValue) {
                betterValue = temp;
                betterRuleIndex = j;
            }
        }

        if (betterValue > 0.0) {
            System.out.println("better rule fitness: " + betterValue);
            double[] vectorConsequent = getRuleConsequent(rules.get(betterRuleIndex));
            System.arraycopy(vectorConsequent, 0, matrixPredictions[i], 0, vectorConsequent.length);

        } else {
            System.out.println("default rule");

            double[] defaultRule = meanClassLabelVectorAllClassesTrain.clone();
            System.arraycopy(defaultRule, 0, matrixPredictions[i], 0, defaultRule.length);
        }
    }

    try {
        PrintWriter writer = new PrintWriter(fullPathToSavePrediction + "predictionsBasedOnTest.txt", "UTF-8");

        for (int i = 0; i < matrixPredictions.length; i++) {
            for (int j = 0; j < matrixPredictions[i].length; j++) {
                writer.print(matrixPredictions[i][j] + " ");
            }

            writer.print("\n");
        }

        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);

    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);
    }
}

//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
public void makePredictionsTrain(String pathRules, String fullPathToSavePrediction) {
    ArrayList<String> rules = readRulesFile(pathRules);
    String[] example;

    double[][] matrixPredictions = new double[binaryClassesTrain.size()][binaryClassesTrain.get(0).length];

    for (int i = 0; i < datasetTrain.size(); i++) {
        example = datasetTrain.get(i);

        double temp, betterValue = 0.0;
        int betterRuleIndex = 0;

        for (int j = 0; j < rules.size(); j++) {
            if (betterValue == 1.0) {
                break;
            }

            String rule = rules.get(j);
            double[] consequent = getRuleConsequent(rule);
            temp = fMeasurePrediction(binaryClassesTrain, i, consequent);

            if (temp > betterValue) {
                betterValue = temp;
                betterRuleIndex = j;
            }
        }

        if (betterValue > 0.0) {
            System.out.println("better rule fitness: " + betterValue);
            double[] vectorConsequent = getRuleConsequent(rules.get(betterRuleIndex));
            System.arraycopy(vectorConsequent, 0, matrixPredictions[i], 0, vectorConsequent.length);

        } else {
            System.out.println("default rule");

            double[] defaultRule = meanClassLabelVectorAllClassesTrain.clone();
            System.arraycopy(defaultRule, 0, matrixPredictions[i], 0, defaultRule.length);
        }
    }

    try {
        PrintWriter writer = new PrintWriter(fullPathToSavePrediction + "trainPredictions.txt", "UTF-8");

        for (int i = 0; i < matrixPredictions.length; i++) {
            for (int j = 0; j < matrixPredictions[i].length; j++) {
                writer.print(matrixPredictions[i][j] + " ");
            }

            writer.print("\n");
        }

        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);

    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Validation.class
                .getName()).log(Level.SEVERE, null, ex);
    }
}

//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
//=============================================
public void run(String prediction) {
    if (specificFold > 0) {
        setMeanClassLabelVectorAllTrain(pathTrainDataset + nameDataset.toLowerCase() + specificFold + "trainatt.arff");
        setMeanClassLabelVectorAllTest(pathTestDataset + nameDataset.toLowerCase() + specificFold + "testatt.arff");

        if (prediction.equals("makePredictionsFirstRule")) {
            makePredictionsFirstRule(pathRules + "rules.txt", pathToSavePredictions);

        } else if (prediction.equals("makePredictionsFitnessBasedOnSimilarTrainInstances")) {
            makePredictionsFitnessBasedOnSimilarTrainInstances(pathRules + "rules.txt", pathToSavePredictions);

        } else if (prediction.equals("makePredictionsFitnessBasedOnTrain")) {
            makePredictionsFitnessBasedOnTrain(pathRules + "rules.txt", pathToSavePredictions);

        } else if (prediction.equals("makePredictionsFitnessBasedOnTest")) {
            makePredictionsFitnessBasedOnTest(pathRules + "rules.txt", pathToSavePredictions);

        } else if (prediction.equals("makePredictionsTrain")) {
            makePredictionsTrain(pathRules + "rules.txt", pathToSavePredictions);
        }

    } else if (specificFold == -1) {
        if (prediction.equals("makePredictions")) {
            for (int i = 1; i <= folds; i++) {
                setMeanClassLabelVectorAllTrain(pathTrainDataset + nameDataset.toLowerCase() + i + "trainatt.arff");
                setMeanClassLabelVectorAllTest(pathTestDataset + nameDataset.toLowerCase() + i + "testatt.arff");
                makePredictionsFirstRule(pathRules + "fold" + i + "\\rules.txt",
                        pathToSavePredictions + "fold" + i + "\\");
            }

        } else if (prediction.equals("makePredictionsImproved")) {
            for (int i = 1; i <= folds; i++) {
                setMeanClassLabelVectorAllTrain(pathTrainDataset + nameDataset.toLowerCase() + i + "trainatt.arff");
                setMeanClassLabelVectorAllTest(pathTestDataset + nameDataset.toLowerCase() + i + "testatt.arff");
                makePredictionsFitnessBasedOnSimilarTrainInstances(pathRules + "fold" + i + "\\rules.txt",
                        pathToSavePredictions + "fold" + i + "\\");
            }
        }
    }
}

public static void main(String[] args) {
    ClassificationWithRule c = new ClassificationWithRule();
//    c.run("makePredictionsFirstRule");
    c.run("makePredictionsFitnessBasedOnSimilarTrainInstances");
//    c.run("makePredictionsFitnessBasedOnTrain");
//    c.run("makePredictionsFitnessBasedOnTest");
//    c.run("makePredictionsTrain");

//    c.lcnAnalysis();
//    int numberOfClasses = 14;
//    String classes = "1,1/1,1/1/1,1/1/2,1/4,1/5,2,2/1,2/1/1,2/1/1/1,2/1/1/2,2/1/1/3,2/1/1/8,2/1/1/9";
//    String datasetPathAndFile = "C:\\Users\\gean_\\Dropbox\\posGrad\\datasets\\TEs\\FOLDS\\MIPS\\mips1trainatt.arff";
//    String datasetName = "mips1";
//    String pathToSave = "C:\\Users\\gean_\\Documents\\NetBeansProjects\\HC-LGA\\src\\main\\java\\br\\ufscar\\hclga\\obj2\\";
//    String trainOrTest = "train";
//    
//    c.exportRealClassesFile(numberOfClasses, classes, datasetPathAndFile, datasetName, pathToSave, trainOrTest);
//    Validation v = new Validation();
//    v.run("originalPredictions");
//    v.run("improvedPredictions");
//    v.runLCNAnalysis();
}
}
