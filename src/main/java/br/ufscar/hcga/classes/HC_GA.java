package br.ufscar.hcga.classes;

import java.util.ArrayList;

/**
 *
 * @author gean_
 */
public class HC_GA {

public static int numRun;
private static ArrayList<double[]> meansAllAUPRCClasses;
private static double[] meanAUPRCTest;
private static ArrayList<double[]> meanFmeasures;

/**
 * @param args the command line arguments
 */
public static void main(String[] args) {
    //Get the parameters
    new Parameters("C:\\Users\\gean_\\Dropbox\\posGrad\\GAs\\HC-GA\\src\\main\\java\\br\\ufscar\\hcga\\config\\configFile.txt");
//    new Parameters("configFile.txt");

    //Create directories for results
    new Paths();

    //Store the AUPRCs for each run
    ArrayList<ArrayList<double[]>> AUPRCsRunsClasses = new ArrayList<ArrayList<double[]>>();
    double[] allAUPRCTest = new double[Parameters.getNumberRuns()];

    //Store the fmeasures for each run
    ArrayList<double[]> fmeasureRuns = new ArrayList<double[]>();

    //Will save the probability of using clausule because it can be modified during execution
    double probabilityUseClausule = Parameters.getProbabilityUseClausule();

    //Will store size of the test dataset to calculate means
    int sizeDatasetTest = 0;

    //Store the training times of all executions
    double[] allMsTimes = new double[Parameters.getNumberRuns()];
    double[] allSTimes = new double[Parameters.getNumberRuns()];
    double[] allMTimes = new double[Parameters.getNumberRuns()];
    double[] allHTimes = new double[Parameters.getNumberRuns()];

    //Execute the algorithm as many times as the user wants
    for (numRun = 1; numRun <= Parameters.getNumberRuns(); numRun++) {
        //Will save training times
        double[] trainingTimes = new double[4];

        //Evaluate the predictions in the test dataset
        Evaluation evaluation = new Evaluation();

        //Set the probability of using clausule in the beggining of each executuon
        Parameters.setProbabilityUseClausule(probabilityUseClausule);

        //Read the training and validation datasets
        Datasets datasets = new Datasets();

        //Mark training time
        Chronometer chron = new Chronometer();
        chron.start();

        //Build the binary structure to store the classes
        Classes.buildClassesStructureTrain();
        //Classes.buildClassesStructureValid();

        double[] defaultRule = Classes.getMeanClassLabelVectorAllClasses().clone();

        if (Parameters.getMultiLabel() == 0) {
            defaultRule = Results.getHigherProbabilities(defaultRule);
        }

        int numberAttempt = 0;
        int maxAttempts = 0;

        ArrayList<Individual> bestIndividuals = new ArrayList<Individual>();
        int maxUncoveredExamples = Parameters.getMaxUncoveredExamples();

        while (Datasets.getDatasetTrain().size() > maxUncoveredExamples) {
            numberAttempt++;
            maxAttempts++;

            //Initiate evolution          
            Evolution evolution = new Evolution();

            //Gets best rules
            Individual bestIndividual = Evolution.getBestIndividual();

            //Verify if the best rule covers a minimum specified number of examples
            if (bestIndividual.getNumberCoveredExamples() >= Parameters.getMinCoveredExamplesRule()) {
                //&& bestIndividual.getNumberCoveredExamples() <= Parameters.getMaxCoveredExamplesRule()) {

                //if (Parameters.getMultiLabel() == 1) {
                bestIndividuals.add(bestIndividual);

                //Remove covered examples from the dataset
                ArrayList<Integer> indexesCoveredExamples = bestIndividual.getIndexCoveredExamples();
                Datasets.removeTrainExamples(indexesCoveredExamples);
                //Build the binary structure to store the classes of the training data
                Classes.buildClassesStructureTrain();

                maxAttempts = 0;
                /*} else {
                     //Will remove only the examples that were correctly predicted by the rule
                     double[] prediction = Results.getHigherProbabilities(bestIndividual.getMeanClassLabelVectorCovered());

                     ArrayList<Integer> indexesCoveredExamples = bestIndividual.getIndexCoveredExamples();
                     ArrayList<Integer> indexesExamplesCorrectlyPredicted = new ArrayList<Integer>();

                     for (int pos = 0; pos < indexesCoveredExamples.size(); pos++) {
                     int indexExample = indexesCoveredExamples.get(pos);
                     double fMeasure = evaluation.fMeasurePrediction(indexExample, prediction);

                     if (fMeasure >= 0.8) {
                     indexesExamplesCorrectlyPredicted.add(indexExample);
                     }
                     }

                     if (indexesExamplesCorrectlyPredicted.size() >= Parameters.getMinCoveredExamplesRule()) {

                     Datasets.removeTrainExamples(indexesExamplesCorrectlyPredicted);

                     //Build the binary structure to store the classes of the training data
                     Classes.buildClassesStructureTrain();

                     maxAttempts = 0;
                     }
                     }*/
            }

            //If it stays X attempts without removing examples, let's
            //decrease the number of initial terms by 2
            int approxNumUsedTerms = (int) (Datasets.getInfoAttributes().size() * Parameters.getProbabilityUseClausule());
            int newApproxNumberUsedTerms = approxNumUsedTerms - 2;
//                if (maxAttempts == 50 && newApproxNumberUsedTerms > 0) {
            if (maxAttempts == Parameters.getNumberGenerations() / 2 && newApproxNumberUsedTerms > 0) {
                double newPercentage = (double) (newApproxNumberUsedTerms * 100) / (Datasets.getInfoAttributes().size() * 100);
                Parameters.setProbabilityUseClausule(newPercentage);
                maxAttempts = 0;
            }

            //After 100 executions without finding a rule that covers the examples,
            //let the remaining examples uncovered
            if (maxAttempts == Parameters.getNumberGenerations()) {
                maxUncoveredExamples = Datasets.getDatasetTrain().size();
            }

                System.out.println();
                System.out.print("Run " + numRun + " ---> Attempt: " + numberAttempt
                        + " ---> Uncovered examples = " + Datasets.getDatasetTrain().size());
                System.out.println();
        }
        
        System.out.println("============================ BEST RULES ============================");
        Results.printRules(bestIndividuals);
        System.out.println("Number of remaining examples = " + Datasets.getDatasetTrain().size());

        //Save training time
        chron.stop();

        trainingTimes[0] = chron.time();
        trainingTimes[1] = chron.stime();
        trainingTimes[2] = chron.mtime();
        trainingTimes[3] = chron.htime();

        Results.saveTrainingTimesRun(trainingTimes);

        allMsTimes[numRun - 1] = trainingTimes[0];
        allSTimes[numRun - 1] = trainingTimes[1];
        allMTimes[numRun - 1] = trainingTimes[2];
        allHTimes[numRun - 1] = trainingTimes[3];

        //Load test data
        datasets.readTestData(Paths.getPathDatasets() + Parameters.getFileDatasetTest());
        Classes.buildClassesStructureTest();
        sizeDatasetTest = Datasets.getDatasetTest().size();
        /*if(Datasets.getDatasetTest().size() == 0 || bestIndividuals.size() == 0){
                System.out.println();
            }*/

        //Order the indivuduals by the fitness in the whole training set
        //---------------------------------------------------------------------------------------------------------------------
        /*new Datasets();
             Classes.buildClassesStructureTrain();

             ArrayList<Individual> finalIndividuals = new ArrayList<Individual>();
             for (int i = 0; i < bestIndividuals.size(); i++) {
             finalIndividuals.add(new Individual(bestIndividuals.get(i).getRule(), bestIndividuals.get(i).getPosActiveTerms()));
             }

             Collections.sort(finalIndividuals);*/
        //---------------------------------------------------------------------------------------------------------------------
        //Obtain the predictions
        double[][] matrixPredictions = Results.obtainPredictions(bestIndividuals, defaultRule);
        //For single-label, lets get only the higher probabilities in the vectors for each level
        if (Parameters.getMultiLabel() == 0) {
            matrixPredictions = Results.getHigherProbabilities(matrixPredictions);

            //Lets calculate other evaluation measures for single-label problems
            //F-measure per level
            evaluation.evaluationFmeasure(matrixPredictions);
            double[] fmeasureLevels = evaluation.getFmeasureLevels();

            //Save the fmeasures for this run
            Results.saveFmeasureRun(fmeasureLevels);
            fmeasureRuns.add(fmeasureLevels);
        }

        evaluation.evaluationAUPRC(matrixPredictions);
        double AUPRC = evaluation.getAUPRC();
        ArrayList<double[]> AUPRCClasses = evaluation.getAUPRCClasses();

        //Save predictions, rules and AUPRC values
        Results.savePredictions(matrixPredictions, AUPRC, AUPRCClasses);

        String predictions = "";
        for (int i = 0; i < matrixPredictions.length; i++) {
            for (int j = 0; j < matrixPredictions[i].length; j++) {
                predictions += matrixPredictions[i][j] + " ";
            }
            predictions += "\n";
        }

        String realTestClasses = "";
        int[] aux;
        for (int i = 0; i < Classes.getBinaryClassesTest().size(); i++) {
            aux = Classes.getBinaryClassesTest().get(i);

            for (int j = 0; j < aux.length; j++) {
                realTestClasses += aux[j] + " ";
            }
            realTestClasses += "\n";
        }

//        Validacao v = new Validacao();
//        String eval = v.run(realTestClasses, predictions, 0.5);
//        System.out.println("eval: " + eval);
//        v.escreveArquivo("C:\\Users\\gean_\\Dropbox\\posGrad\\GACerriMaven\\src\\main\\java\\hmc_ga\\evaluation.txt", eval);
//        v.escreveArquivo("evaluation.txt", eval);
        Results.saveRules(bestIndividuals);

        allAUPRCTest[numRun - 1] = AUPRC;
        AUPRCsRunsClasses.add(AUPRCClasses);

        //Free test dataset
        datasets.freeTestDataset();
    }

    //Calculate the meanAUPRC for each class and overall
    meansAllAUPRCClasses = Results.calculateMeansAUPRCClasses(AUPRCsRunsClasses);
    meanAUPRCTest = Results.calculateMeanSd(allAUPRCTest);

    //Calculate mean execution times
    double[] meanMsTimes = Results.calculateMeanSd(allMsTimes);
    double[] meanSTimes = Results.calculateMeanSd(allSTimes);
    double[] meanMTimes = Results.calculateMeanSd(allMTimes);
    double[] meanHTimes = Results.calculateMeanSd(allHTimes);

    //Save mean AUPRC for each class and overall
    Results.saveMeansAUPRCClasses(meansAllAUPRCClasses, meanAUPRCTest, allAUPRCTest, sizeDatasetTest);

    //Save mean training times
    Results.saveMeanTrainingTimes(meanMsTimes, meanSTimes, meanMTimes, meanHTimes);

    if (Parameters.getMultiLabel() == 0) {
        //Calculate mean and sd for all executions in each level
        meanFmeasures = Results.calculateMeanSdFmesureLevels(fmeasureRuns);
        //Save the mean Fmeasure for each level
        Results.saveMeanFmeasureLevels(meanFmeasures);
    }

//        Validacao v = new Validacao();
//        v.run();
}

public static int getNumRun() {
    return numRun;
}
}
