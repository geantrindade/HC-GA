package br.ufscar.hcga.classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gean_
 */
public class Validation {

public final String saveInDirectory = leConfig()[0];
public final int numClasses = Integer.valueOf(leConfig()[1]);
public final int numberOfTrainIntances = Integer.valueOf(leConfig()[2]);
public final String realTestClassesFile = leConfig()[3];
public final String predictedClassesFile = leConfig()[4];
public final String rulesFile = leConfig()[5];

public Double threshold = Double.valueOf(leConfig()[6]);
public LinkedList<String> whichFoldCollec = new LinkedList<>();
public LinkedList<Integer> reaisCollec = new LinkedList<>();
public LinkedList<int[]> reaisCollecLevels = new LinkedList<>();
public LinkedList<Integer> preditasCollec = new LinkedList<>();
public LinkedList<int[]> preditasCollecLevels = new LinkedList<>();
public LinkedList<Integer> acertosCollec = new LinkedList<>();
public LinkedList<int[]> acertosCollecLevels = new LinkedList<>();
public LinkedList<Double> precisionCollec = new LinkedList<>();
public LinkedList<double[]> precisionCollecLevels = new LinkedList<>();
public LinkedList<Double> recallCollec = new LinkedList<>();
public LinkedList<double[]> recallCollecLevels = new LinkedList<>();
public LinkedList<Double> fmeasureCollec = new LinkedList<>();
public LinkedList<double[]> fmeasureCollecLevels = new LinkedList<>();
public int allFolds = Integer.valueOf(leConfig()[7]); //0(nao) ou 1 (sim)
public int specificFold;
public boolean hierarchical = Boolean.parseBoolean(leConfig()[9]);

public String aplicaThreshold(String file, double threshold) {
    String strPredTh = "";
    String[] aux = file.split(" ");
    int cont = 0;

    for (int i = 0; i < aux.length; i++) {
        if (!aux[i].trim().isEmpty()) {
            if (aux[i].contentEquals("E")) {
                aux[i] = aux[i].replace("E", "");
                aux[i] = aux[i].replace("-", "");

                if (Double.parseDouble(aux[i]) >= threshold) { //a fim de evitar os E-... do float
                    strPredTh += "1";
                }

            } else if (Double.parseDouble(aux[i]) >= threshold) {
                strPredTh += "1";

            } else {
                strPredTh += "0";
            }

            strPredTh += " ";
            cont++;
        }

        if (cont == numClasses) {
            strPredTh += "\n";
            cont = 0;
        }
    }

    return strPredTh;
}

public String leArquivo(String path) {
    FileReader reader;
    BufferedReader bufferReader;
    String line;
    String[] aux;
    String arq = "";

    try {
        reader = new FileReader(path);
        bufferReader = new BufferedReader(reader);

        while ((line = bufferReader.readLine()) != null) {
            aux = line.split(" ");

//            if (aux.length > 4) { //arq de predicoes contem AU(PRC)
            for (int i = 0; i < aux.length; i++) {
                arq += aux[i] + " ";
            }
//            }
            arq += "";
        }
    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        return null;
    } catch (IOException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        return null;
    }

    return arq;
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
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public void calcHierarchicalPrecRevFmeasure(String strReal, String strPredTh) {
    int reais = 0;
    int preditas = 0;
    int acertos = 0;
    double prec = 0.0;
    double rec = 0.0;
    double fMeasure = 0.0;

    String[] auxR = strReal.split(" ");
    String[] auxP = strPredTh.split(" ");

    ArrayList<String> esperado = new ArrayList<>();
    ArrayList<String> obtido = new ArrayList<>();

    for (int i = 0; i < auxP.length; i++) {
        esperado.add(auxR[i]);
        obtido.add(auxP[i]);
    }

    for (int i = 0; i < esperado.size(); i++) {
        if (esperado.get(i).equals("1")) {
            reais++;

            if (esperado.get(i).equals(obtido.get(i))) {
                acertos++;
                preditas++;
            }

        } else if (obtido.get(i).equals("1")) {
            preditas++;
        }
    }

    prec += (double) acertos / preditas;
    rec += (double) acertos / reais;
    fMeasure = (2 * prec * rec) / (prec + rec);

    reaisCollec.add(reais);
    preditasCollec.add(preditas);
    acertosCollec.add(acertos);
    precisionCollec.add(prec);
    recallCollec.add(rec);
    fmeasureCollec.add(fMeasure);
}

public void calcHierarchicalPrecRevFmeasureRuleTxt(String strReal, String strPredTh) {
    int reais = 0;
    int preditas = 0;
    int acertos = 0;
    double prec = 0.0;
    double rec = 0.0;
    double fMeasure = 0.0;

    String[] auxR = strReal.split(" ");
    strPredTh = strPredTh.replace("\n", "");
    String[] auxP = strPredTh.split(" ");

    ArrayList<String> esperado = new ArrayList<>();
    ArrayList<String> obtido = new ArrayList<>();

    for (int i = 0; i < auxP.length; i++) {
        esperado.add(auxR[i]);
        obtido.add(auxP[i]);
    }

    for (int i = 0; i < esperado.size(); i++) {
        if (esperado.get(i).equals("1")) {
            reais++;

            if (esperado.get(i).equals(obtido.get(i))) {
                acertos++;
                preditas++;
            }

        } else if (obtido.get(i).equals("1")) {
            preditas++;
        }
    }

    prec += (double) acertos / preditas;
    rec += (double) acertos / reais;
    fMeasure = (2 * prec * rec) / (prec + rec);

    reaisCollec.add(reais);
    preditasCollec.add(preditas);
    acertosCollec.add(acertos);
    precisionCollec.add(prec);
    recallCollec.add(rec);
    fmeasureCollec.add(fMeasure);
}

public void calcPrecRevFmeasure(String strReal, String strPredTh) {
    int reais = 0;
    int preditas = 0;
    int acertos = 0;
    Double prec = 0.0;
    Double rec = 0.0;
    Double fMeasure = 0.0;

    int ini = 0, fim = numClasses;

    ArrayList<String> rcList = new ArrayList<String>(Arrays.asList(strReal.split(" ")));
    ArrayList<String> pcList = new ArrayList<String>(Arrays.asList(strPredTh.split(" ")));

    while (fim <= rcList.size()) {
        int leafRealClassIndex = rcList.subList(ini, fim).lastIndexOf("1");

        if (leafRealClassIndex != -1) {
            reais++;
        }

        int leafPredClassIndex = pcList.subList(ini, fim).lastIndexOf("1");

        if (leafPredClassIndex != -1) {
            preditas++;
        }

        if (leafRealClassIndex == leafPredClassIndex) {
            acertos++;
        }

        ini = fim;
        fim = fim + numClasses;
    }

    prec += (double) acertos / preditas;
    rec += (double) acertos / reais;
    fMeasure = (2 * prec * rec) / (prec + rec);

    reaisCollec.add(reais);
    preditasCollec.add(preditas);
    acertosCollec.add(acertos);
    precisionCollec.add(prec);
    recallCollec.add(rec);
    fmeasureCollec.add(fMeasure);
}

public int countRules(String rulesFile) {
    int cont = 0;
    FileReader reader;
    BufferedReader bufferReader;
    String line;

    try {
        reader = new FileReader(rulesFile);
        bufferReader = new BufferedReader(reader);

        while ((line = bufferReader.readLine()) != null) {
            if (!line.isEmpty()) {
                cont++;
            }
        }

    } catch (FileNotFoundException ex) {
        return -1;
    } catch (IOException ex) {
        return -1;
    }

    return cont;
}

public int countTestsInRules(String rulesFile) {
    int cont = 0;
    FileReader reader;
    BufferedReader bufferReader;
    String line;
    int ini = 0;

    try {
        reader = new FileReader(rulesFile);
        bufferReader = new BufferedReader(reader);

        while ((line = bufferReader.readLine()) != null) {
            if (!line.isEmpty()) {
                ini = line.length();

                while (line.lastIndexOf("AND", ini) != -1) {
                    cont++;
                    ini = line.lastIndexOf("AND", ini) - 1;
                }

                cont++; //pq o numero de testes é igual ao numero de ANDs + 1;
            }
        }

    } catch (FileNotFoundException ex) {
        return -1;
    } catch (IOException ex) {
        return -1;
    }

    return cont;
}

public String[] leConfig() {
    String regExp[] = {"saveInDirectory =",
        "numClasses =",
        "numberOfTrainIntances =",
        "realTestClassesFile =",
        "predictedClassesFile =",
        "rulesFile =",
        "threshold =",
        "allFolds =",
        "specificFold =",
        "hierarchical ="};

    Pattern comment = Pattern.compile("#");
    String[] result = new String[10];

    for (int i = 0; i < regExp.length; i++) {
        try {
            FileReader reader = new FileReader("C:\\Users\\gean_\\Dropbox\\posGrad\\GAs\\HC-GA\\src\\main\\java\\br\\ufscar\\hcga\\config\\validation.txt");
//            FileReader reader = new FileReader("validacao.txt");
//            FileReader reader = new FileReader("/home/geantrindade/Dropbox/posGrad/GACerriMaven/src/main/java/hmc_ga/validacao.txt");
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

public void calcPrecRevFmeasureByLevel(String strReal, String strPredTh, int numberOfLevels, int numberOfClasses) {
    int[] reais = new int[numberOfLevels];
    int[] preditas = new int[numberOfLevels];
    int[] acertos = new int[numberOfLevels];
    double[] prec = new double[numberOfLevels];
    double[] rec = new double[numberOfLevels];
    double[] fMeasure = new double[numberOfLevels];

    String[] auxR = strReal.split(" ");
//    strPredTh = strPredTh.replace("\n", "");
    String[] auxP = strPredTh.split(" ");

    ArrayList<String> esperado = new ArrayList<>();
    ArrayList<String> obtido = new ArrayList<>();

    for (int i = 0; i < auxP.length; i++) {
        esperado.add(auxR[i]);
        obtido.add(auxP[i]);
    }

    //each value corresponds to an index of a level (i.e value 0 -> lvl0, value 1 -> lvl1...)
    //each position corresponds to reais, acertos and preditas...
    int[] indexLevels = {0, 0, 0};
    int cont = 0;

    for (int i = 0; i < esperado.size(); i++) {

        if (esperado.get(i).equals("1")) {
            reais[indexLevels[0]]++;
            indexLevels[0]++;

            if (esperado.get(i).equals(obtido.get(i))) {
                preditas[indexLevels[1]]++;
                acertos[indexLevels[2]]++;
                indexLevels[1]++;
            }

            indexLevels[2]++;

        } else if (obtido.get(i).equals("1")) {
            preditas[indexLevels[1]]++;
            indexLevels[1]++;
        }

        cont++;

        if (cont == numberOfClasses) {
            indexLevels[0] = 0;
            indexLevels[1] = 0;
            indexLevels[2] = 0;
            cont = 0;
        }
//        if (esperado.get(i).equals("1")) {
//            reais[indexLevels[0]]++;
//            indexLevels[0]++;
//
//            if (esperado.get(i).equals(obtido.get(i))) {
//                acertos[indexLevels[2]]++;
//                preditas[indexLevels[1]]++;
//
//                indexLevels[1]++;
//                indexLevels[2]++;
//            }
//
//        } else if (obtido.get(i).equals("1")) {
//            preditas[indexLevels[1]]++;
//            indexLevels[1]++;
//        }
//
//        cont++;
//
//        if (cont == numberOfClasses) {
//            indexLevels[0] = 0;
//            indexLevels[1] = 0;
//            indexLevels[2] = 0;
//            cont = 0;
//        }
    }

    for (int i = 0; i < numberOfLevels; i++) {
        prec[i] += (double) acertos[i] / preditas[i];
        rec[i] += (double) acertos[i] / reais[i];
        fMeasure[i] = (2 * prec[i] * rec[i]) / (prec[i] + rec[i]);
    }

    reaisCollecLevels.add(reais);
    preditasCollecLevels.add(preditas);
    acertosCollecLevels.add(acertos);
    precisionCollecLevels.add(prec);
    recallCollecLevels.add(rec);
    fmeasureCollecLevels.add(fMeasure);
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

public int countTestsInRules(ArrayList<String> rulesFile) {
    int cont = 0;
    int ini;

    while (!rulesFile.isEmpty()) {
        String line = rulesFile.remove(0).trim();

        if (!line.isEmpty()) {
            ini = 0;

            if (line.indexOf("AND") == -1) {
                cont++;

            } else {
                cont++;

                while ((ini = line.indexOf("AND", ini)) != -1) {
                    cont++;
                    ini += 3;
                }
            }
        }
    }

    return cont;
}

public void run(String pathRules, String dataset, int folds, int numberOfTrainIntances) {

    double meanNumberOfRules = 0, meanTestsInRules = 0;
    double meanAverageTestPerRule = 0, meanAverageCoveragePerRule = 0;

    System.out.println("=====================================================");
    System.out.println("dataset: " + dataset);

    for (int i = 1; i <= folds; i++) {
//        ArrayList<String> rules = readRulesFileClus(pathRules + dataset + i + "Rules.txt");
        ArrayList<String> rules = readRulesFile(pathRules + dataset + i + "Rules.txt");

        int numberOfRules = rules.size();
        meanNumberOfRules += numberOfRules;

        int testsInRules = countTestsInRules(rules);
        meanTestsInRules += testsInRules;

        Double averageTestPerRule = (double) testsInRules / (double) numberOfRules;
        meanAverageTestPerRule += averageTestPerRule;

        Double averageCoveragePerRule = (double) numberOfTrainIntances / (double) numberOfRules;
        meanAverageCoveragePerRule += averageCoveragePerRule;

        System.out.println("fold " + i);
        System.out.println("numberOfRules: " + numberOfRules);
        System.out.println("numberOfActiveTests: " + testsInRules);
        System.out.println("averageTestPerRule: " + averageTestPerRule);
        System.out.println("averageCoveragePerRule: " + averageCoveragePerRule + "\n");
    }

    System.out.println("\nmeanNumberOfRules: " + meanNumberOfRules / folds);
    System.out.println("meanNumberOfActiveTests: " + meanTestsInRules / folds);
    System.out.println("meanAverageTestPerRule: " + meanAverageTestPerRule / folds);
    System.out.println("meanAverageCoveragePerRule: " + meanAverageCoveragePerRule / folds);
    System.out.println("=====================================================\n\n");
}

public void run(ArrayList<String> strsReal, int index, String realTestClasses, String predictions, String pathToSave) {

    String strReal;

    if (strsReal.size() < index) {
        strReal = leArquivo(realTestClasses + ".txt");
        strsReal.add(index - 1, strReal);

    } else {
        strReal = strsReal.get(index - 1);
    }

//        strPred = leArquivo(predictedClassesFile + "fold" + specificFold + "\\" + predictions + ".txt");
    String strPred = leArquivo(predictions + ".txt");
//  

    //aplica thresholds e calcula fmeasures
//    strPredTh = aplicaThreshold(predictions, threshold);
//    if (hierarchical) {
    calcHierarchicalPrecRevFmeasure(strReal, strPred);

//    } else {
//        calcPrecRevFmeasure(realTestClasses, predictions);
//    }
//faz as medias dos resultados dos folds e salva-os
    try {
        PrintWriter writer;
        String configTested = "";

        String real = String.valueOf(reaisCollec.removeFirst());
        String predita = String.valueOf(preditasCollec.removeFirst());
        String acerto = String.valueOf(acertosCollec.removeFirst());
        String prec = String.valueOf(precisionCollec.removeFirst());
        String rev = String.valueOf(recallCollec.removeFirst());
        String fmeasure = String.valueOf(fmeasureCollec.removeFirst());
//            int numberOfRules = countRules(homeDirectory + fold + "/" + rulesFile);
//            int testsInRules = countTestsInRules(homeDirectory + fold + "/" + rulesFile);
//            Double averageTestPerRule = (double) testsInRules / (double) numberOfRules;
//            Double averageCoveragePerRule = (double) numberOfTrainIntances / (double) numberOfRules;

        configTested += "Reais: " + real + "\n"
                + "Preditas: " + predita + "\n"
                + "Acertos: " + acerto + "\n"
                + "Prec: " + prec + "\n"
                + "Rev: " + rev + "\n"
                + "Fmeasure: " + fmeasure + "\n\n" //                    + "Numero de regras: " + String.valueOf(numberOfRules) + ""
                //                    + "Numero de testes ativos: " + String.valueOf(testsInRules) + ""
                //                    + "Media de testes por regra: " + String.valueOf(averageTestPerRule) + ""
                //                    + "Media de exemplos cobertos por regra: " + averageCoveragePerRule;
                ;

        writer = new PrintWriter(pathToSave + "analysisGeneral_"
                + predictions.substring(predictions.indexOf("fold"),
                        predictions.length()) + ".txt", "UTF-8");

        writer.println(configTested);

        System.out.println("General Analysis " + predictions.substring(predictions.indexOf("fold"),
                predictions.length()) + " done");

        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public void runForLevels(ArrayList<String> strsReal, int index, String realClasses, String predictions, int numberOfLevels, int numberOfClasses,
        String pathToSave) {

    String strReal;

    if (strsReal.size() < index) {
        strReal = leArquivo(realClasses + ".txt");
        strsReal.add(index - 1, strReal);
    } else {
        strReal = strsReal.get(index - 1);
    }

    String strPred = leArquivo(predictions + ".txt");

    calcPrecRevFmeasureByLevel(strReal, strPred, numberOfLevels, numberOfClasses);

    try {
        PrintWriter writer;

        String configTested = "";

        for (int i = 0; i < numberOfLevels; i++) {
            int real = reaisCollecLevels.get(0)[i];
            int predita = preditasCollecLevels.get(0)[i];
            int acerto = acertosCollecLevels.get(0)[i];
            double prec = precisionCollecLevels.get(0)[i];
            double rev = recallCollecLevels.get(0)[i];
            double fmeasure = fmeasureCollecLevels.get(0)[i];

            configTested += "Level " + i + ":\n"
                    + "Reais: " + real + "\n"
                    + "Preditas: " + predita + "\n"
                    + "Acertos: " + acerto + "\n"
                    + "Prec: " + prec + "\n"
                    + "Rev: " + rev + "\n"
                    + "Fmeasure: " + fmeasure + "\n\n";
        }

        reaisCollecLevels.clear();
        preditasCollecLevels.clear();
        acertosCollecLevels.clear();
        precisionCollecLevels.clear();
        recallCollecLevels.clear();
        fmeasureCollecLevels.clear();

        writer = new PrintWriter(pathToSave + "analysisLevels_"
                + predictions.substring(predictions.indexOf("fold"),
                        predictions.length()) + ".txt", "UTF-8");

        writer.println(configTested);

        System.out.println("Analysis per Level " + predictions.substring(predictions.indexOf("fold"),
                predictions.length()) + " done");

        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
    }
}

@SuppressWarnings("empty-statement")
public void calculateAverageOfMetrics() {
    String directory = "C:\\Users\\gean_\\Dropbox\\posGrad\\Experimentos\\LCN Bruna\\ECprosite\\Abordagem_So_Primos\\svm\\Evaluation\\";

    System.out.println(directory);

//    String[] classifier = {"dtree", "rforest", "nb", "knn", "mlp", "mlp2", "svm"};
    String fileName = "analysisLevels_fold";

    FileReader reader;
    BufferedReader bufferReader;
    String line;
    String aux;

    double[] precLevels = new double[4];
    double[] revLevels = new double[4];
    double[] fmLevels = new double[4];

    int[] level = {0, 0, 0};
    int index;

    for (int i = 1; i <= 10; i++) {
        try {
            reader = new FileReader(directory + fileName + i + ".txt");
            bufferReader = new BufferedReader(reader);
            level[0] = 0;
            level[1] = 0;
            level[2] = 0;

            while ((line = bufferReader.readLine()) != null) {
                if (line.contains("Prec:")) {
                    index = line.trim().indexOf(":");
                    String auxB = line.substring(index + 1, line.length());

                    if (auxB.trim().equalsIgnoreCase("NaN")) {
                        precLevels[level[0]] += 0.0;
                    } else {
                        precLevels[level[0]] += Double.parseDouble(auxB);
                    }
                    level[0]++;

                } else if (line.contains("Rev:")) {
                    index = line.trim().indexOf(":");
                    String auxB = line.substring(index + 1, line.length());

                    if (auxB.trim().equalsIgnoreCase("NaN")) {
                        revLevels[level[1]] += 0.0;
                    } else {
                        revLevels[level[1]] += Double.parseDouble(auxB);
                    }
                    level[1]++;

                } else if (line.contains("Fmeasure:")) {
                    index = line.trim().indexOf(":");
                    String auxB = line.substring(index + 1, line.length());

                    if (auxB.trim().equalsIgnoreCase("NaN")) {
                        fmLevels[level[2]] += 0.0;
                    } else {
                        fmLevels[level[2]] += Double.parseDouble(auxB);
                    }
                    level[2]++;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//
//    for (int i = 0; i < 4; i++) {
//        System.out.println("level " + i);
//        System.out.println("prec: " + precLevels[i]);
//        System.out.println("rev: " + revLevels[i]);
//        System.out.println("fmeasure: " + fmLevels[i] + "\n");
//    }

    System.out.println("average:\n");

    double[] precAv = new double[4];
    double[] revAv = new double[4];
    double[] fmAv = new double[4];

    for (int i = 0; i < 4; precAv[i] = precLevels[i] / 10, revAv[i] = revLevels[i] / 10, fmAv[i] = fmLevels[i] / 10, i++);

    for (int i = 0; i < 4; i++) {
        System.out.println("level " + i);
        System.out.println("prec: " + String.valueOf(precAv[i]).replace(".", ","));
        System.out.println("rev: " + String.valueOf(revAv[i]).replace(".", ","));
        System.out.println("fmeasure: " + String.valueOf(fmAv[i]).replace(".", ",") + "\n");
    }

}

public void runLCNAnalysis() {
    int levels = 4;

    int nClasses = 333;

    String mainPath = "C:\\Users\\gean_\\Dropbox\\posGrad\\Experimentos\\LCN Bruna";

    String data = "ECpfam";

    String realTestClassesFileName = "realTestClasses_test";
    String predictionClassesFileName = "predictionClasses_fold";

//    String[] abordagens = {"Abordagem_Exclusiva", "Abordagem_Inclusiva", "Abordagem_Menos_Exclusiva",
//        "Abordagem_Menos_Inclusiva", "Abordagem_Primos", "Abordagem_Primos_Exclusiva", "Abordagem_So_Primos"};
    String[] abordagens = {"Abordagem_Primos", "Abordagem_Primos_Exclusiva", "Abordagem_So_Primos"};

//    String[] classificadores = {"dtree", "knn", "mlp", "mlp2", "nb", "rforest", "svm"};
    String[] classificadores = {"svm"};
    //falta a dtree nas demais dps da abordagem exclusiva

    ArrayList<String> strsReal = new ArrayList<>();

    System.out.println("\ndataset: " + data + "\n");

    for (String abordagem : abordagens) {
        System.out.println("\nabordagem: " + abordagem + "\n");

        for (String classificador : classificadores) {

            System.out.println("\nclassificador: " + classificador + "\n");

            for (int i = 1; i <= 10; i++) {
                String path = mainPath + "\\" + data + "\\" + abordagem + "\\"
                        + classificador + "\\Evaluation\\";

                runForLevels(strsReal, i, path + realTestClassesFileName + i,
                        path + predictionClassesFileName + i,
                        levels, nClasses, path);

                run(strsReal, i, path + realTestClassesFileName + i, path + predictionClassesFileName + i, path);
            }
        }
    }
}

public void run(String predictions) {
    String strReal = null;
    String strPred = null;
    String strPredTh = null;
    int iterator = 0;
    specificFold = Integer.valueOf(leConfig()[8]);

    if (allFolds == 1) {
        iterator = 10;
        specificFold = 1;
    } else if (allFolds == 0) {
        iterator = 1;
    } else {
        System.out.println("Parametro 'allFolds' não especificado!");
        System.exit(0);
    }

    //aplica thresholds e calcula fmeasures
    for (int i = 1; i <= iterator; i++) {
        if (iterator == 1) {
//        strReal = leArquivo(realTestClassesFile + "fold" + specificFold + "\\realTestClasses" + ".txt");
            strReal = leArquivo(realTestClassesFile + "realTestClasses" + ".txt");
//        strPred = leArquivo(predictedClassesFile + "fold" + specificFold + "\\" + predictions + ".txt");
            strPred = leArquivo(predictedClassesFile + predictions + ".txt");
            strPredTh = aplicaThreshold(strPred, Double.valueOf(leConfig()[6]));
//        escreveArquivo(saveInDirectory + "fold" + specificFold + "\\" + predictions + "_predictedClassesTr_" + threshold + "_.txt", strPredTh);
            escreveArquivo(saveInDirectory + predictions + "_Thr" + threshold + ".txt", strPredTh);
            whichFoldCollec.add("fold" + specificFold);
        } else {
            strReal = leArquivo(realTestClassesFile + "fold" + i + "\\realTestClasses" + ".txt");
            strPred = leArquivo(predictedClassesFile + "fold" + i + "\\" + predictions + ".txt");
            strPredTh = aplicaThreshold(strPred, Double.valueOf(leConfig()[6]));
            escreveArquivo(saveInDirectory + "fold" + i + "\\" + predictions + "_predictedClassesTr_" + threshold + "_.txt", strPredTh);
            whichFoldCollec.add("fold" + i);
        }

        if (hierarchical) {
            calcHierarchicalPrecRevFmeasureRuleTxt(strReal, strPredTh);

        } else {
            calcPrecRevFmeasure(strReal, strPredTh);
        }
        specificFold++;
    }

    //faz as medias dos resultados dos folds e salva-os
    try {
        PrintWriter writer;
        Double pMean = 0.0;
        Double rMean = 0.0;
        Double fMean = 0.0;
        for (int i = 0; i < iterator; pMean += precisionCollec.get(i), rMean += recallCollec.get(i), fMean += fmeasureCollec.get(i), i++);
        pMean /= iterator;
        rMean /= iterator;
        fMean /= iterator;

        String configTested = "";

        while (whichFoldCollec.size() > 0) {
            String fold = whichFoldCollec.removeFirst();
            String real = String.valueOf(reaisCollec.removeFirst());
            String predita = String.valueOf(preditasCollec.removeFirst());
            String acerto = String.valueOf(acertosCollec.removeFirst());
            String prec = String.valueOf(precisionCollec.removeFirst());
            String rev = String.valueOf(recallCollec.removeFirst());
            String fmeasure = String.valueOf(fmeasureCollec.removeFirst());
//            int numberOfRules = countRules(homeDirectory + fold + "/" + rulesFile);
//            int testsInRules = countTestsInRules(homeDirectory + fold + "/" + rulesFile);
//            Double averageTestPerRule = (double) testsInRules / (double) numberOfRules;
//            Double averageCoveragePerRule = (double) numberOfTrainIntances / (double) numberOfRules;

            configTested += fold + "\n"
                    + "Reais: " + real + "\n"
                    + "Preditas: " + predita + "\n"
                    + "Acertos: " + acerto + "\n"
                    + "Prec: " + prec + "\n"
                    + "Rev: " + rev + "\n"
                    + "Fmeasure: " + fmeasure + "\n\n" //                    + "Numero de regras: " + String.valueOf(numberOfRules) + ""
                    //                    + "Numero de testes ativos: " + String.valueOf(testsInRules) + ""
                    //                    + "Media de testes por regra: " + String.valueOf(averageTestPerRule) + ""
                    //                    + "Media de exemplos cobertos por regra: " + averageCoveragePerRule;
                    ;
        }

        writer = new PrintWriter(saveInDirectory + "fold" + (specificFold - 1) + "_" + predictions
                + "_configsTestadas" + threshold + ".txt", "UTF-8");
//        writer = new PrintWriter(saveInDirectory + "fold" + (specificFold - 1) + prediction
//                + "_configsTestadas" + threshold + ".txt", "UTF-8");
        writer.println(configTested);
        writer.println("PrecisionMean: " + pMean);
        writer.println("RecallMean: " + rMean);
        writer.println("Fmeasure: " + fMean);
        writer.flush();
        writer.close();

    } catch (FileNotFoundException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public static void main(String[] args) throws FileNotFoundException, IOException {
    Validation v = new Validation();
//    v.run("predictionsFirstRule");
    v.run("predictionsBasedOnSimilarTrainInstances");
//    v.run("predictionsBasedOnTrain");
//    v.run("predictionsBasedOnTest");
//    v.run("trainPredictions");
//
//
//    v.runLCNAnalysis();
//    v.calculateAverageOfMetrics();
}
//class
}
