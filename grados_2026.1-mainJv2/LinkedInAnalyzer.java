package br.com.unipe;

import java.util.*;

/**
 * Motor de análises e recomendações para uma rede social de conexões profissionais.
 * Implementa 5 missões principais:
 * 1. Construtor da Análise
 * 2. Sugestão de Conexões (Amigos de 2º Grau)
 * 3. Grau de Separação
 * 4. Rota e Custo de Maior Afinidade
 * 5. Mapear Grupos Isolados (Sub-redes)
 */
public class LinkedInAnalyzer {

    private final Grafo grafo;

    /**
     * Missão 1: Construtor da Análise
     * Recebe a instância do grafo que representa a rede social.
     */
    public LinkedInAnalyzer(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * Missão 2: Sugestão de Conexões (Amigos de 2º Grau)
     * 
     * Descobre quem são as pessoas que são "amigas de amigas",
     * mas que o usuário ainda não adicionou direto.
     * 
     * Regras:
     * 1. Não sugere quem já é contato direto (1º grau)
     * 2. Não sugere o próprio usuário
     * 3. Ordena por quantidade de amigos em comum (decrescente)
     * 
     * @param nomePessoa Nome do usuário para quem buscar sugestões
     * @return Lista de sugestões com nome e quantidade de amigos em comum
     */
    public List<SugestaoConexao> sugerirConexoes(String nomePessoa) {
        Vertice pessoa = grafo.encontraVertice(nomePessoa).orElse(null);
        if (pessoa == null) {
            return new ArrayList<>();
        }

        // Conjunto de amigos diretos (1º grau) + o próprio usuário
        Set<String> amigosDiretos = new HashSet<>();
        amigosDiretos.add(nomePessoa); // não se sugere a si mesmo
        for (Vertice amigo : pessoa.getAdjacencias()) {
            amigosDiretos.add(amigo.getNome());
        }

        // Map: nome do candidato -> quantidade de amigos em comum
        Map<String, Integer> amigosEmComum = new HashMap<>();

        // Para cada amigo direto, ver os amigos dele (2º grau)
        for (Vertice amigoDireto : pessoa.getAdjacencias()) {
            for (Vertice amigoDeAmigo : amigoDireto.getAdjacencias()) {
                String nomeCandidato = amigoDeAmigo.getNome();
                // Só conta se não for o próprio usuário nem amigo direto
                if (!amigosDiretos.contains(nomeCandidato)) {
                    amigosEmComum.put(nomeCandidato,
                        amigosEmComum.getOrDefault(nomeCandidato, 0) + 1);
                }
            }
        }

        // Converte o map em lista de sugestões e ordena por amigos em comum (decrescente)
        List<SugestaoConexao> sugestoes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : amigosEmComum.entrySet()) {
            sugestoes.add(new SugestaoConexao(entry.getKey(), entry.getValue()));
        }

        sugestoes.sort((a, b) -> Integer.compare(b.getAmigosEmComum(), a.getAmigosEmComum()));

        return sugestoes;
    }

    /**
     * Missão 3: Grau de Separação
     * 
     * Descobre a quantos "passos" de conexão direta/indireta
     * duas pessoas estão uma da outra.
     * 
     * Usa BFS (Busca em Largura) para encontrar o menor número
     * de conexões intermediárias de forma otimizada.
     * 
     * @param origem Nome da pessoa de origem
     * @param destino Nome da pessoa de destino
     * @return Número de passos (inteiro), ou -1 se isolados
     */
    public int grauDeSeparacao(String origem, String destino) {
        return grafo.bfsGrauDeSeparacao(origem, destino);
    }

    /**
     * Missão 4: Rota e Custo de Maior Afinidade
     * 
     * Encontra a melhor rota (menor soma de pesos = maior afinidade)
     * entre origem e destino usando o algoritmo de Dijkstra.
     * 
     * @param origem Nome da pessoa de origem
     * @param destino Nome da pessoa de destino
     * @return ResultadoDijkstra com o caminho e o custo total
     */
    public ResultadoDijkstra rotaMaiorAfinidade(String origem, String destino) {
        Map<String, Object> resultado = grafo.dijkstra(origem, destino);

        int custo = (Integer) resultado.get("custo");
        @SuppressWarnings("unchecked")
        List<String> caminho = (List<String>) resultado.get("caminho");

        return new ResultadoDijkstra(caminho, custo);
    }

    /**
     * Missão 5: Mapear Grupos Isolados (Sub-redes)
     * 
     * Acha todos os grupos de pessoas conectadas entre si,
     * mas totalmente isoladas dos outros grupos (componentes conexos).
     * 
     * @return Lista de grupos, cada grupo é uma lista de nomes
     */
    public List<List<String>> mapearGruposIsolados() {
        return grafo.encontrarComponentesConexos();
    }

    // ============================================================
    // CLASSES INTERNAS / RECORDS PARA RETORNOS TIPADOS
    // ============================================================

    /**
     * Representa uma sugestão de conexão com a quantidade de amigos em comum.
     */
    public static class SugestaoConexao {
        private final String nome;
        private final int amigosEmComum;

        public SugestaoConexao(String nome, int amigosEmComum) {
            this.nome = nome;
            this.amigosEmComum = amigosEmComum;
        }

        public String getNome() {
            return nome;
        }

        public int getAmigosEmComum() {
            return amigosEmComum;
        }

        @Override
        public String toString() {
            return nome + " (" + amigosEmComum + " amigo(s) em comum)";
        }
    }

    /**
     * Representa o resultado do algoritmo de Dijkstra:
     * o caminho encontrado e o custo total (soma dos pesos).
     */
    public static class ResultadoDijkstra {
        private final List<String> caminho;
        private final int custo;

        public ResultadoDijkstra(List<String> caminho, int custo) {
            this.caminho = caminho;
            this.custo = custo;
        }

        public List<String> getCaminho() {
            return caminho;
        }

        public int getCusto() {
            return custo;
        }

        public boolean temCaminho() {
            return custo != -1 && !caminho.isEmpty();
        }

        @Override
        public String toString() {
            if (!temCaminho()) {
                return "Não há caminho entre os perfis.";
            }
            return "Caminho: " + String.join(" -> ", caminho) + " | Custo total: " + custo;
        }
    }
}
