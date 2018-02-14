package br.ufscar.hcga.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author gean_
 */
public class Evolution {

private static ArrayList<Individual> currentPopulation;
private static Individual bestIndividual;

public Evolution() {

    //int good = 0;
    //while (good == 0) {
    //Create an initial population
    Population population = new Population();

    currentPopulation = new ArrayList<Individual>();

    //Initializing current population
    int populationSize = Parameters.getNumberInitialRules();

    for (int i = 0; i < populationSize; i++) {
        currentPopulation.add(new Individual(Population.getPopulation().get(i), Population.getActiveTerms().get(i)));
    }

    //Results.printRules(currentPopulation);
    //Local Search
    currentPopulation = GeneticOperators.localSearchOperatorMinMax(currentPopulation);
    Collections.sort(currentPopulation);

    //The best individual is the individual that has the best fitness among the individuals that cover
    //the minimum and maximum number of examples
    /*for (int i = 0; i < currentPopulation.size(); i++) {

         if (currentPopulation.get(i).getNumberCoveredExamples() >= Parameters.getMinCoveredExamplesRule()) {
         //&& currentPopulation.get(i).getNumberCoveredExamples() <= Parameters.getMaxCoveredExamplesRule()) {

         bestIndividual = new Individual(currentPopulation.get(i).getRule().clone(), currentPopulation.get(i).getPosActiveTerms());
         //good = 1;
         break;
         }
         }*/
    //}
    bestIndividual = new Individual(currentPopulation.get(0).getRule().clone(), currentPopulation.get(0).getPosActiveTerms());
    //Will store children generated and the whole next generation
    ArrayList<Individual> children;
    ArrayList<Individual> nextPopulation = new ArrayList<Individual>();
    int numGenerations = Parameters.getNumberGenerations();
    int generation = 1;
    int attempts = 0;

    //Sort the current Population according to their fitness values
//        System.out.println("Generation = " + generation);
//        System.out.println("======================================= Initial best rule =======================================");
//        Results.printRule(bestIndividual.getRule());
//        System.out.println("Fitness = " + bestIndividual.getFitness());
//        System.out.println("=================================================================================================");
    //Results.printRules(currentPopulation);

    /*for (int i = 0; i < currentPopulation.size(); i++) {
         ArrayList<Integer> posActiveTerms = GeneticOperators.getPosActiveTerms(currentPopulation.get(i).getRule());
         System.out.print("Rule " + i + " = ");
         for (int j = 0; j < posActiveTerms.size(); j++) {
         System.out.print(posActiveTerms.get(j) + " ");
         }
         System.out.println();
         }
         System.out.println("====================================");
         * 
     */
    Random rand = new Random();

    //Evolve initial population
//        while (numGenerations > 0 && attempts <= (numGenerations * 0.8)) {
    while (numGenerations > 0 && attempts <= (numGenerations * 0.5)) {
        attempts++;
        generation++;
        nextPopulation.addAll(GeneticOperators.elitism());
        //children = GeneticOperators.uniformCrossoverRandom();
        children = GeneticOperators.uniformCrossoverDistance();
        children = GeneticOperators.mutation(children);
        nextPopulation.addAll(children);
        //Results.printRules(nextPopulation);
//
//            int n = rand.nextInt(2);
//            if (n == 1) {
//                nextPopulation = GeneticOperators.localSearchOperatorMaxFitness(nextPopulation);
//            }

        nextPopulation = GeneticOperators.localSearchOperatorMinMax(nextPopulation);
//            nextPopulation = GeneticOperators.localSearchOperatorFitness(nextPopulation);

        currentPopulation.clear();
        currentPopulation.addAll(nextPopulation);
        nextPopulation.clear();
        Collections.sort(currentPopulation);

//            System.out.println("Generation = " + generation);
//            System.out.println("=========================== Previous best rule and current best rule ===========================");
//            Results.printRule(bestIndividual.getRule());
//            System.out.println("Fitness = " + bestIndividual.getFitness() + ", Number of covered examples = " + bestIndividual.getNumberCoveredExamples());
//int indexBestRule = 0;
        //Update the best rule
        /*for (int i = 0; i < currentPopulation.size(); i++) {
             if (currentPopulation.get(i).getFitness() > bestIndividual.getFitness()
             && currentPopulation.get(i).getNumberCoveredExamples() >= Parameters.getMinCoveredExamplesRule()) {
             //&& currentPopulation.get(i).getNumberCoveredExamples() <= Parameters.getMaxCoveredExamplesRule()) {

             bestIndividual = new Individual(currentPopulation.get(i).getRule().clone(), currentPopulation.get(i).getPosActiveTerms());
             attempts = 0;
             indexBestRule = i;
             break;
             }
             }*/
        if (currentPopulation.get(0).getFitness() > bestIndividual.getFitness()) {
            bestIndividual = new Individual(currentPopulation.get(0).getRule().clone(), currentPopulation.get(0).getPosActiveTerms());
            attempts = 0;
        }
        //Results.printRule(currentPopulation.get(indexBestRule).getRule());
        //System.out.println("Fitness = " + currentPopulation.get(indexBestRule).getFitness() + ", Number of covered examples = " + currentPopulation.get(indexBestRule).getNumberCoveredExamples());

//            Results.printRule(currentPopulation.get(0).getRule());
//            System.out.println("Fitness = " + currentPopulation.get(0).getFitness() + ", Number of covered examples = " + currentPopulation.get(0).getNumberCoveredExamples());
//            System.out.println("================================================================================================");
        numGenerations--;

        /*for (int i = 0; i < currentPopulation.size(); i++) {
             ArrayList<Integer> posActiveTerms = GeneticOperators.getPosActiveTerms(currentPopulation.get(i).getRule());
             System.out.print("Rule " + i + " = ");
             for (int j = 0; j < posActiveTerms.size(); j++) {
             System.out.print(posActiveTerms.get(j) + " ");
             }
             System.out.println();
             }*/
    }
    //System.out.println("====================================");
    //System.out.println();
}

public static ArrayList<Individual> getCurrentPopulation() {
    return currentPopulation;
}

public static Individual getBestIndividual() {
    return bestIndividual;
}
}
