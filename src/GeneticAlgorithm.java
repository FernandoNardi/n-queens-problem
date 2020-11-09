import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class GeneticAlgorithm {
    private int populationLength = 50;
    private int generation = 1;
    private double mutationProbability = 0.1;
    private double crossoverProbability = 0.9;
    private boolean withoutAttack = false;

    private int numberOfQueens;
    private int[] bestChessboard;

    private double startTime = 0;

    public void execute() {
        this.startTime();

        LinkedList<int[]> population = this.buildPopulation();

        while (true) {
            // crossover and mutation
            for (int i = 0; i < this.populationLength - 1; i += 2) {
                // start crossover
                boolean isCrossover = (1 * Math.random()) <= this.crossoverProbability;
                if (isCrossover) {
                    int crossoverSpot;
                    do {
                        crossoverSpot = (int) (this.numberOfQueens * Math.random());
                    } while(crossoverSpot == 0);

                    int[] chessboardChildA = this.crossover(population.get(i), population.get(i + 1), crossoverSpot);
                    boolean isMutation = (1 * Math.random()) <= this.mutationProbability;
                    if (isMutation) {
                        chessboardChildA = this.mutation(chessboardChildA);
                    }
                    population.add(chessboardChildA);
                    if (this.bestChessboard[this.numberOfQueens] == 0) {
                        this.withoutAttack = true;
                        break;
                    }

                    int[] chessboardChildB = this.crossover(population.get(i + 1), population.get(i), crossoverSpot);
                    isMutation = (1 * Math.random()) <= this.mutationProbability;
                    if (isMutation) {
                        chessboardChildB = this.mutation(chessboardChildB);
                    }
                    population.add(chessboardChildB);
                    if (this.bestChessboard[this.numberOfQueens] == 0) {
                        this.withoutAttack = true;
                        break;
                    }
                }
                // finish crossover

                // start mutation parents
                boolean isMutation = (1 * Math.random()) <= this.mutationProbability;
                if (isMutation) {
                    int[] chessboardMutant = this.mutation(population.get(i));
                    population.set(i, chessboardMutant);
                    if (this.bestChessboard[this.numberOfQueens] == 0) {
                        this.withoutAttack = true;
                        break;
                    }
                }

                isMutation = (1 * Math.random()) <= this.mutationProbability;
                if (isMutation) {
                    int[] chessboardMutant = this.mutation(population.get(i + 1));
                    population.set(i, chessboardMutant);
                    if (this.bestChessboard[this.numberOfQueens] == 0) {
                        this.withoutAttack = true;
                        break;
                    }
                }
                // finish mutation parents
            }

            if (this.withoutAttack) {
                break;
            }

            population = this.tournament(population);

            generation++;
        }

        this.finishTime();
        System.out.println("Generation: " + this.generation);
        System.out.println("Number of attacks: " + this.bestChessboard[this.numberOfQueens]);
        this.printResult(this.bestChessboard);
        this.printChessboard(this.bestChessboard);
        System.out.println();
    }

    public GeneticAlgorithm setNumberOfQueens() {
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Input the number of queens to define the board size (>= 4): ");
            this.numberOfQueens = scanner.nextInt();
            if (this.numberOfQueens < 4) {
                System.out.println("Invalid input. Try again !!!");
            }
        } while (this.numberOfQueens < 4);
        return this;
    }

    private void startTime() {
        this.startTime = System.currentTimeMillis();
//        System.out.println("Start time: " + this.startTime * 0.001);
    }

    private void finishTime() {
        // for test time
        double finishTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (finishTime - this.startTime) * 0.001);
    }

    private void printResult(int[] chessboard){
        System.out.printf("chessboard: ");
        System.out.printf("|");
        IntStream.rangeClosed(0, this.numberOfQueens).forEach(index -> {
            System.out.printf("%d|", chessboard[index]);
            if (index == 30 || index == 60 || index == 90 || index == 120 || index == 150 || index == 180 || index == 210) {
                System.out.println();
            }
        });
        System.out.println();
    }

    private int[] buildRandomChessboard(){
        int[] chessboard = new int[this.numberOfQueens + 1];
        IntStream.range(0, this.numberOfQueens).forEach(index -> chessboard[index] = (int) (this.numberOfQueens * Math.random()));
        return chessboard;
    }

    private int attackCounter(int[] chessboard) {
        AtomicInteger position = new AtomicInteger(1);
        AtomicInteger sum = new AtomicInteger();

        for(int i = 0; i < this.numberOfQueens; ++i) {
            for(int t = i + 1; t < this.numberOfQueens; ++t) {
                if((chessboard[i] - position.get()) == chessboard[t] || (chessboard[i] + position.get()) == chessboard[t] || chessboard[i] == chessboard[t]) {
                    sum.addAndGet(2);
                }
                position.getAndIncrement();
            }
            position.set(1);
        }

        return sum.get();
    }

    private LinkedList<int[]> buildPopulation() {
        LinkedList<int[]> population = new LinkedList<>();
        int[] chessboard = this.buildRandomChessboard();
        chessboard[this.numberOfQueens] = this.attackCounter(chessboard);

        this.bestChessboard = chessboard;
        population.add(chessboard);

        for(int i = 1; i  < this.populationLength; ++i){
            chessboard = this.buildRandomChessboard();
            chessboard[this.numberOfQueens] = this.attackCounter(chessboard);
            population.add(chessboard);

            if(this.bestChessboard[this.numberOfQueens] > chessboard[this.numberOfQueens]){
                this.bestChessboard = chessboard;
            }
        }
        return population;
    }

    private int[] crossover(int[] chessboardA, int[] chessboardB, int crossoverSpot) {
        int[] chessboard = new int[this.numberOfQueens + 1];
        for (int i = 0; i < this.numberOfQueens; ++i) {
            if (i < crossoverSpot) {
                chessboard[i] = chessboardA[i];
            } else {
                chessboard[i] = chessboardB[i];
            }
        }
        chessboard[this.numberOfQueens] = this.attackCounter(chessboard);
        if (this.bestChessboard[this.numberOfQueens] > chessboard[this.numberOfQueens]) {
            this.bestChessboard = chessboard;
        }
        return chessboard;
    }

    private int[] mutation(int[] chessboard){
        int random1 = (int) (this.numberOfQueens * Math.random());
        int random2;

        do {
            random2 = (int) (this.numberOfQueens * Math.random());
        } while(chessboard[random1] == random2);

        chessboard[random1] = random2;

        chessboard[this.numberOfQueens] = this.attackCounter(chessboard);
        if (this.bestChessboard[this.numberOfQueens] > chessboard[this.numberOfQueens]) {
            this.bestChessboard = chessboard;
        }
        return chessboard;
    }

    private LinkedList<int[]> tournament(LinkedList<int[]> population){
        LinkedList<int[]> aux = new LinkedList<>();
        int random;
        int random1;

        aux.add(this.bestChessboard);

        for(int i = 0; i < this.populationLength - 1; ++i){
            random = (int) (population.size() * Math.random());
            random1 = (int) (population.size() * Math.random());
            if(population.get(random)[this.numberOfQueens] < population.get(random1)[this.numberOfQueens]){
                aux.add(population.get(random));
            }
            else{
                aux.add(population.get(random1));
            }
        }
        return aux;
    }

    private void printChessboard(int[] chessboard){
        System.out.printf("Print chessboard ('Q' is queen position) : ");
        System.out.println();
        for (int i = 0; i < this.numberOfQueens; ++i) {
            for(int j = 0; j < this.numberOfQueens; ++j){
                if(i == chessboard[j]){
                    System.out.printf("| Q ");
                }
                else{
                    System.out.printf("|   ");
                }
            }
            System.out.println("|");
        }
    }
}
