package com.mycompany.agsemroleta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AG {

    private Random random = new Random();

    public Individuo executar(IndividuoFactory indFact, int nPop, int nElite, int numGer) {
        List<Individuo> popIni = new ArrayList<>();
        for (int i = 0; i < nPop; i++) {
            popIni.add(indFact.getIndividuo());
        }

        Individuo melhorIndividuo = null;

        for (int g = 0; g < numGer; g++) {
            // Gerar os filhos utilizando o popIni
            List<Individuo> popAux = new ArrayList<>(popIni);
            List<Individuo> filhos = new ArrayList<>();
            for (int i = 0; i < nPop / 2; i++) {
                // Retirar o pai1 aleatoriamente de popAux
                Individuo pai1 = popAux.remove(random.nextInt(popAux.size()));
                // Retirar o pai2 aleatoriamente de popAux
                Individuo pai2 = popAux.remove(random.nextInt(popAux.size()));
                // List<Individuo> 2filhos = pai1.recombinar(pai2);
                List<Individuo> doisFilhos = pai1.recombinar(pai2);
                filhos.addAll(doisFilhos);
            }

            // Gerar os mutantes utilizando o popIni
            List<Individuo> mutantes = new ArrayList<>();
            for (int i = 0; i < nPop; i++) {
                Individuo pai1 = popIni.get(i);
                mutantes.add(pai1.mutar());
            }

            List<Individuo> joinList = new ArrayList<>();
            joinList.addAll(popIni);
            joinList.addAll(filhos);
            joinList.addAll(mutantes);

            List<Individuo> newPop = new ArrayList<>();

            // Selecionar os nElite melhores indivíduos de joinList usando o método Elite
            Elite(joinList, nElite, true, newPop);

            // Selecionar os outros (nPop - nElite) indivíduos utilizando seleção por roleta
            Roleta(joinList, nPop - nElite, true, newPop);

            popIni.clear();
            popIni.addAll(newPop);

            // Parar se encontrar um indivíduo com conflitos 0
            melhorIndividuo = popIni.get(0);
            double melhorAvaliacao = melhorIndividuo.getAvaliacao();
            System.out.println("Geração " + (g + 1) + ": Melhor Avaliação = " + melhorAvaliacao);

            if (melhorAvaliacao == 0) {
                System.out.println("Solução encontrada na geração " + (g + 1));
                break;
            }
        }

        // Imprimir o tabuleiro do melhor indivíduo encontrado ao final da execução
        if (melhorIndividuo != null && melhorIndividuo instanceof IndividuoNRainhas) {
            System.out.println("Tabuleiro da melhor solução encontrada:");
            ((IndividuoNRainhas) melhorIndividuo).imprimirTabuleiro();
        }
        return melhorIndividuo;
    }

    private boolean NaoExisteEmNewPop(List<Individuo> newPop, Individuo ind) {
        for (Individuo individuo : newPop) {
            if (individuo.equals(ind)) {
                return false;
            }
        }
        return true;
    }

    public void Roleta(List<Individuo> joinList, int nSelecao, boolean isMinimizacao, List<Individuo> newPop) {
        int qtd = 0;
        Random random = new Random();

        // Calcular a soma das avaliações
        double somaAvaliacoes = 0;
        for (Individuo ind : joinList) {
            if (isMinimizacao) {
                somaAvaliacoes += (1.0 / (0.1 + ind.getAvaliacao())); // Inverter avaliação para minimização
            } else {
                somaAvaliacoes += ind.getAvaliacao();
            }
        }

        while (qtd < nSelecao) {
            //b. gerar um numero aleatorio entre 0 e somaAvaliações
            double valorAleatorio = random.nextDouble() * somaAvaliacoes;
            double somaParcial = 0;
            Individuo IndSelecionado = null;

            //c. percorrer a lista de individuos somando suas avaliações até soma ser >= ao numero aleatorio
            for (Individuo ind : joinList) {
                if (isMinimizacao) {
                    somaParcial += (1.0 / (0.1 + ind.getAvaliacao()));
                } else {
                    somaParcial += ind.getAvaliacao();
                }

                //d. O indice em que o loop anterior parar e o indice do individuo selecionado
                if (somaParcial >= valorAleatorio) {
                    IndSelecionado = ind;
                    break;
                }
            }

            if (IndSelecionado != null) {
                joinList.remove(IndSelecionado);
                if (NaoExisteEmNewPop(newPop, IndSelecionado)) {
                    newPop.add(IndSelecionado);
                    qtd++;
                }

                // Recalcular a soma das avaliações
                somaAvaliacoes = 0;
                for (Individuo ind : joinList) {
                    if (isMinimizacao) {
                        somaAvaliacoes += (1.0 / (0.1 + ind.getAvaliacao()));
                    } else {
                        somaAvaliacoes += ind.getAvaliacao();
                    }
                }
            }
        }
    }

    public void Elite(List<Individuo> joinList, int nElite, boolean isMinimizacao, List<Individuo> newPop) {
        int qtd = 0;

        // Ordenar a lista de indivíduos com base na avaliação
        if (isMinimizacao) {
            joinList.sort((ind1, ind2) -> Double.compare(ind1.getAvaliacao(), ind2.getAvaliacao()));
        } else {
            joinList.sort((ind1, ind2) -> Double.compare(ind2.getAvaliacao(), ind1.getAvaliacao()));
        }

        while (qtd < nElite && !joinList.isEmpty()) {
            Individuo IndSelecionado = joinList.remove(0); // Pega o melhor indivíduo

            if (NaoExisteEmNewPop(newPop, IndSelecionado)) {
                newPop.add(IndSelecionado);
                qtd++;
            }
        }
    }
}
