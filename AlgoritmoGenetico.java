import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AlgoritmoGenetico {

    public AlgoritmoGenetico() {
    }

    public List<Individuo> init(List<Paciente> pacientes, int qtdQuantos) {
        List<Individuo> populacao = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 100; i++) {
            Map<Paciente, Integer> genes = new HashMap<>();

            for (Paciente p : pacientes) {
                int quarto = random.nextInt(qtdQuantos);
                genes.put(p, quarto);
            }

            Individuo individuo = new Individuo(genes, 100);
            populacao.add(individuo);
        }

        return populacao;
    }

    public void avaliarFitness(List<Individuo> populacao, List<Quarto> quartos) {
        for (Individuo individuo : populacao) {
            Map<Integer, Integer> ocupacao = new HashMap<>();
            int currentFitness = 0;
            int qtdfila = 0;

            for (Paciente paciente : individuo.getGenes().keySet()) {
                Integer quartoId = individuo.getGenes().get(paciente);

                if (quartoId == 0) {
                    qtdfila++;
                } else {
                    ocupacao.put(quartoId, ocupacao.getOrDefault(quartoId, 0) + 1);
                }

                Quarto quarto = quartoId == 0 ? null : quartos.get(quartoId - 1);

                if (quarto == null) {
                    switch (paciente.getGrauDeUrgencia()) {
                        case 3 -> currentFitness += 100;
                        case 2 -> currentFitness += 50;
                        case 1 -> currentFitness += 20;
                    }
                    if (paciente.isPrecisaDeUti())
                        currentFitness += 100;
                    if (paciente.isPrecisaDeIsolamento())
                        currentFitness += 50;

                } else {
                    int urgencia = paciente.getGrauDeUrgencia();
                    int cuidado = quarto.getNivelDeCuidado();

                    if (urgencia > cuidado)
                        currentFitness += 50;
                    else if (urgencia < cuidado)
                        currentFitness += 20;
                    else
                        currentFitness -= 10;

                    if (paciente.isPrecisaDeIsolamento()) {
                        if (!quarto.getTipoDeUrgencia().equals("ISOLAMENTO"))
                            currentFitness += 40;
                        else
                            currentFitness -= 50;
                    }

                    if (paciente.isPrecisaDeUti()) {
                        if (!quarto.getTipoDeUrgencia().equals("UTI"))
                            currentFitness += 100;
                        else
                            currentFitness -= 30;
                    }

                    if (!paciente.isPrecisaDeUti() && quarto.getTipoDeUrgencia().equals("UTI"))
                        currentFitness += 10;
                    if (!paciente.isPrecisaDeIsolamento() && quarto.getTipoDeUrgencia().equals("ISOLAMENTO"))
                        currentFitness += 10;
                }
            }

            if (qtdfila > 0) {
                currentFitness += qtdfila * 10;
            }

            for (Quarto q : quartos) {
                int ocupados = ocupacao.getOrDefault(q.getId(), 0);
                if (ocupados > q.getCapacidade()) {
                    int excesso = ocupados - q.getCapacidade();
                    currentFitness += excesso * 25;
                }
            }

            individuo.setFitness(Math.max(1, 1000 - currentFitness));
        }
    }

    public List<Individuo> cruzamentos(List<Individuo> populacao, List<Quarto> quartos) {
        List<Individuo> novaPopulacao = new ArrayList<>();
        Individuo melhor = Collections.max(populacao, Comparator.comparing(Individuo::getFitness));
        novaPopulacao.add(new Individuo(melhor));

        while (novaPopulacao.size() < populacao.size()) {
            Individuo pai1 = selecaoTorneio(populacao);
            Individuo pai2;
            Individuo filho;
            double aleatorio = Math.random();

            do {
                pai2 = selecaoTorneio(populacao);
            } while (pai1 == pai2);

            if (aleatorio < 0.8) {
                filho = crossover(pai1, pai2, quartos);
            } else if (aleatorio < 0.9) {
                filho = new Individuo(pai1);
            } else {
                filho = new Individuo(pai2);
            }

            mutacao(filho, quartos);

            novaPopulacao.add(filho);
        }

        return novaPopulacao;
    }

    private Individuo selecaoTorneio(List<Individuo> populacao) {
        Random random = new Random();

        Individuo a = populacao.get(random.nextInt(populacao.size()));
        Individuo b = populacao.get(random.nextInt(populacao.size()));
        Individuo c = populacao.get(random.nextInt(populacao.size()));
        Individuo d = populacao.get(random.nextInt(populacao.size()));
        Individuo e = populacao.get(random.nextInt(populacao.size()));

        Individuo melhor = a;

        if (b.getFitness() > melhor.getFitness())
            melhor = b;
        if (c.getFitness() > melhor.getFitness())
            melhor = c;
        if (d.getFitness() > melhor.getFitness())
            melhor = d;
        if (e.getFitness() > melhor.getFitness())
            melhor = e;

        return melhor;
    }

    private void mutacao(Individuo individuo, List<Quarto> quartos) {
        Random random = new Random();

        double taxaMutacao = 0.05;

        for (Paciente p : individuo.getGenes().keySet()) {
            if (random.nextDouble() < taxaMutacao) {
                List<Integer> quartoPossiveis = new ArrayList<>();
                quartoPossiveis.add(0);
                for (Quarto quarto : quartos) {
                    quartoPossiveis.add(quarto.getId());
                }
                int novoQuarto = quartoPossiveis.get(random.nextInt(quartoPossiveis.size()));
                individuo.getGenes().put(p, novoQuarto);
            }
        }
    }

    private Individuo crossover(Individuo pai1, Individuo pai2, List<Quarto> quartos) {
        Individuo filho = new Individuo();

        for (Paciente p : pai1.getGenes().keySet()) {
            int quartoDoPai1 = pai1.getGenes().get(p);
            int quartoDoPai2 = pai2.getGenes().get(p);

            if (quartoDoPai1 == quartoDoPai2) {
                filho.getGenes().put(p, quartoDoPai1);
            } else {
                int grau = p.getGrauDeUrgencia();
                boolean precisaDeUti = p.isPrecisaDeUti();
                boolean precisaDeIsolamento = p.isPrecisaDeIsolamento();

                if (grau == 3 || precisaDeIsolamento || precisaDeUti) {
                    int melhorQuarto = fitnessGene(pai1, p, quartos) > fitnessGene(pai2, p, quartos) ? quartoDoPai1
                            : quartoDoPai2;

                    if (Math.random() < 0.5) {
                        filho.getGenes().put(p, melhorQuarto);
                    } else {
                        filho.getGenes().put(p, melhorQuarto == quartoDoPai2 ? quartoDoPai1 : quartoDoPai2);
                    }
                } else {
                    filho.getGenes().put(p, Math.random() < 0.5 ? quartoDoPai1 : quartoDoPai2);
                }
            }
        }
        return new Individuo(filho);
    }

    private int fitnessGene(Individuo pai1, Paciente paciente, List<Quarto> quartos) {
        int quartoId = pai1.getGenes().get(paciente);
        int score = 0;
        if (quartoId == 0) {
            return 0;
        }
        Quarto quarto = quartos.get(quartoId - 1);

        if (paciente.isPrecisaDeUti() && quarto.getTipoDeUrgencia().equals("UTI")) {
            score += 50;
        }
        if (paciente.isPrecisaDeIsolamento() && quarto.getTipoDeUrgencia().equals("ISOLAMENTO")) {
            score += 30;
        }

        score += paciente.getGrauDeUrgencia() * 10;
        return score;
    }

    public void imprimirAlocacao(Individuo individuo, List<Quarto> quartos) {
        System.out.println("=== Alocação do Individuo ===");
        System.out.printf("%-10s %-6s %-12s %-8s %-10s %-10s%n",
                "Paciente", "Idade", "Sexo", "Urgência", "UTI", "Isolamento");

        for (Paciente paciente : individuo.getGenes().keySet()) {
            Integer quartoId = individuo.getGenes().get(paciente);
            String local;

            if (quartoId == 0) {
                local = "Fila";
            } else {
                Quarto quarto = quartos.get(quartoId - 1);
                local = "Quarto " + quarto.getId() + " (" + quarto.getTipoDeUrgencia() + ")";
            }

            System.out.printf("%-10s %-6d %-12s %-8d %-10s %-10s -> %s%n",
                    paciente.getNome(),
                    paciente.getIdade(),
                    paciente.getSexo(),
                    paciente.getGrauDeUrgencia(),
                    paciente.isPrecisaDeUti() ? "Sim" : "Não",
                    paciente.isPrecisaDeIsolamento() ? "Sim" : "Não",
                    local);
        }
    }
}