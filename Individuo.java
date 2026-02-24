import java.util.HashMap;
import java.util.Map;

public class Individuo {
    private Map<Paciente, Integer> genes;
    private double fitness;

    public Individuo() {
        this.genes = new HashMap<>();
        this.fitness = 0;
    }

    public Individuo(Individuo melhor) {
        this.genes = melhor.genes;
        this.fitness = melhor.fitness;
    }

    public Individuo(Map<Paciente, Integer> genes, double fitness) {
        this.genes = genes;
        this.fitness = fitness;
    }

    public Individuo(Map<Paciente, Integer> genes) {
        this.genes = genes;
        this.fitness = 0;
    }

    public Map<Paciente, Integer> getGenes() {
        return genes;
    }

    public void setGenes(Map<Paciente, Integer> genes) {
        this.genes = genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    @Override
    public String toString() {
        return "Individuo{" + "genes=" + genes + ", fitness=" + fitness + '}';
    }
}