import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Quarto> quartos = new ArrayList<>();
        List<Paciente> pacientes = new ArrayList<>();
        AlgoritmoGenetico algoritmoGenetico = new AlgoritmoGenetico();

        int gerecaoAtual = 0;
        int geracoes = 1000;

        quartos.add(new Quarto(1, 2, "M", "NORMAL", 1));
        quartos.add(new Quarto(2, 1, "F", "UTI", 3));
        quartos.add(new Quarto(3, 1, "M", "ISOLAMENTO"));
        quartos.add(new Quarto(4, 1, "F", "ISOLAMENTO"));
        quartos.add(new Quarto(5, 3, "MISTO", "NORMAL", 1));
        quartos.add(new Quarto(6, 3, "MISTO", "NORMAL", 1));
        quartos.add(new Quarto(7, 1, "M", "UTI", 3));
        quartos.add(new Quarto(8, 3, "M", "NORMAL", 1));
        quartos.add(new Quarto(9, 1, "MISTO", "ISOLAMENTO"));
        quartos.add(new Quarto(10, 4, "MISTO", "NORMAL", 1));

        // Alta urgência
        pacientes.add(new Paciente(1, "João", "M", 65, 3, false, true));
        pacientes.add(new Paciente(2, "Maria", "F", 72, 3, false, true));
        pacientes.add(new Paciente(3, "Carlos", "M", 50, 2, false, false));
        pacientes.add(new Paciente(4, "Ana", "F", 34, 2, false, false));
        pacientes.add(new Paciente(5, "Pedro", "M", 60, 3, true, true));

        // Média urgência
        pacientes.add(new Paciente(6, "Juliana", "F", 28, 3, false, false));
        pacientes.add(new Paciente(7, "Lucas", "M", 40, 3, false, false));
        pacientes.add(new Paciente(8, "Fernanda", "F", 55, 2, true, true));
        pacientes.add(new Paciente(9, "Rafael", "M", 47, 3, false, false));
        pacientes.add(new Paciente(10, "Patricia", "F", 36, 3, false, false));

        // Baixa urgência
        pacientes.add(new Paciente(11, "Marcos", "M", 22, 1, false, false));
        pacientes.add(new Paciente(12, "Beatriz", "F", 19, 1, false, false));
        pacientes.add(new Paciente(13, "Diego", "M", 31, 1, false, false));
        pacientes.add(new Paciente(14, "Camila", "F", 26, 1, false, false));
        pacientes.add(new Paciente(15, "Bruno", "M", 58, 2, false, true));

        // Extras
        pacientes.add(new Paciente(16, "Larissa", "F", 45, 2, false, false));
        pacientes.add(new Paciente(17, "Gustavo", "M", 38, 1, false, false));
        pacientes.add(new Paciente(18, "Renata", "F", 67, 3, true, true));
        pacientes.add(new Paciente(19, "Thiago", "M", 41, 3, false, false));
        pacientes.add(new Paciente(20, "Sofia", "F", 30, 2, false, false));

        List<Individuo> populacao = algoritmoGenetico.init(pacientes, quartos.size());

        while (gerecaoAtual < geracoes) {
            algoritmoGenetico.avaliarFitness(populacao, quartos);
            populacao = algoritmoGenetico.cruzamentos(populacao, quartos);
            Individuo melhor = Collections.max(populacao, Comparator.comparing(Individuo::getFitness));
            System.out.println("Geração " + gerecaoAtual + ": melhor fitness = " + melhor.getFitness());
            gerecaoAtual++;
        }

        Individuo melhor = Collections.max(populacao, Comparator.comparing(Individuo::getFitness));
        algoritmoGenetico.imprimirAlocacao(melhor, quartos);
    }
}
