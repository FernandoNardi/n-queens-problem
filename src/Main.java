public class Main {
    public static void main(String[] args) {
        while (true) {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

            geneticAlgorithm
                    .setNumberOfQueens()
                    .execute();
        }
    }
}
