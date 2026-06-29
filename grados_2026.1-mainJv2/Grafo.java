package br.com.unipe;

import java.util.*;

public class Grafo {
    private final List<Aresta> arestas;
    private final List<Vertice> vertices;
    private boolean eDirigido;
    private int ordem;
    private int tamanho;
    private final boolean ePonderado;

    public Grafo() {
        this(false, false);
    }

    public Grafo(boolean eDirigido, boolean ePonderado) {
        this.eDirigido = eDirigido;
        this.ePonderado = ePonderado;
        arestas = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    public void adicionaVertices(String... nomes) {
        for (String nome : nomes) {
            vertices.add(new Vertice(nome));
            ordem++;
        }
    }

    public void addAresta(String v1, String v2) {
        arestas.add(criaAresta("", v1, v2, null));
    }

    public void addAresta(String v1, String v2, int peso) {
        arestas.add(criaAresta("", v1, v2, peso));
    }

    public void addAresta(String nome, String v1, String v2) {
        arestas.add(criaAresta(nome, v1, v2, null));
    }

    public void addAresta(String nome, String v1, String v2, int peso) {
        arestas.add(criaAresta(nome, v1, v2, peso));
    }

    private Aresta criaAresta(String nomeAresta, String nomeVertice1, String nomeVertice2, Integer peso) {
        Vertice v1 = encontraVertice(nomeVertice1).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice1 + " não encontrado."));
        Vertice v2 = encontraVertice(nomeVertice2).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice2 + " não encontrado."));
        if (!eDirigido) {
            infereSeGrafoEDirecionado(v1, v2);
        }
        aumentaGrauDosVertices(v1, v2);
        resolveAdjacencias(v1, v2);
        tamanho++;
        return new Aresta(nomeAresta, v1, v2, peso);
    }

    private void resolveAdjacencias(Vertice v1, Vertice v2) {
        v1.adicionaAdjacencia(v2);
        v2.adicionaAdjacente(v1);
        if (!eDirigido) {
            v1.adicionaAdjacente(v2);
            v2.adicionaAdjacencia(v1);
        }
    }

    private void aumentaGrauDosVertices(Vertice v1, Vertice v2) {
        if (eDirigido) {
            v1.aumentaOutDegree();
            v2.aumentaInDegree();
        } else {
            v1.aumentaGrau();
            v2.aumentaGrau();
        }
    }

    private void infereSeGrafoEDirecionado(Vertice v1, Vertice v2) {
        if (eSelfLoop(v1, v2)) {
            reprocessamentoParaDigrafo();
        } else {
            for (Aresta aresta : arestas) {
                if (eViaMaoDupla(v1, v2, aresta) || eArestaDuplicada(v2, v1, aresta)) {
                    reprocessamentoParaDigrafo();
                    break;
                }
            }
        }
    }

    private static boolean eArestaDuplicada(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v1) && aresta.getVerticeDestino().equals(v2);
    }

    private static boolean eViaMaoDupla(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v2) && aresta.getVerticeDestino().equals(v1);
    }

    private static boolean eSelfLoop(Vertice v1, Vertice v2) {
        return v1.getNome().equals(v2.getNome());
    }

    public Optional<Vertice> encontraVertice(String nome) {
        for (Vertice vertice : vertices) {
            if (vertice.getNome().equalsIgnoreCase(nome)) {
                return Optional.of(vertice);
            }
        }
        return Optional.empty();
    }

    private void reprocessamentoParaDigrafo() {
        eDirigido = true;
        System.out.println("Reprocessamento para digrafo necessário. O grafo agora é direcionado.");
        limpezaGrausEAdjacencias();
        recalculaGrausEAdjacencias();
    }

    private void recalculaGrausEAdjacencias() {
        arestas.forEach(aresta -> {
            Vertice origem = aresta.getVerticeOrigem();
            Vertice destino = aresta.getVerticeDestino();
            aumentaGrauDosVertices(origem, destino);
            resolveAdjacencias(origem, destino);
        });
    }

    private void limpezaGrausEAdjacencias() {
        vertices.forEach(vertice -> {
            vertice.resetaGraus();
            vertice.resetaAdjacenciasEAdjacentes();
        });
    }

    public String exibeGrausDosVertices() {
        StringBuilder graus = new StringBuilder();
        for (Vertice vertice : vertices) {
            graus.append(vertice.exibeGraus());
        }
        return graus.toString();
    }

    public String exibeAdjacencias() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias.append("\n").append(vertice.getNome()).append(": ").append(vertice.getAdjacencias());
        }
        return adjacencias.toString();
    }

    public String exibeAdjacentes() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias.append("\n").append(vertice.getNome()).append(": ").append(vertice.getAdjacentes());
        }
        return adjacencias.toString();
    }

    public void exibeMatrizAdjacencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();

        StringBuilder matriz = new StringBuilder("\nMatriz de Adjacência\n");
        matriz.append("\t");
        verticesOrdenados.forEach(v -> matriz.append(v.getNome()).append("\t"));
        matriz.append("\n");

        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            List<Vertice> adjacencias = vertice.getAdjacencias();
            for (Vertice outroVertice : verticesOrdenados) {
                matriz.append(adjacencias.contains(outroVertice) ? "1" : "0").append("\t");
            }
            matriz.append("\n");
        }

        System.out.println(matriz);
    }

    public void exibeMatrizIncidencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();
        StringBuilder matriz = new StringBuilder("\nMatriz de Incidência\n\t");
        arestas.forEach(a -> matriz.append(a.getNome()).append("\t"));
        matriz.append("\n");
        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            for (Aresta aresta : arestas) {
                Vertice origem = aresta.getVerticeOrigem();
                Vertice destino = aresta.getVerticeDestino();
                String valor;
                if (origem.equals(vertice) && destino.equals(vertice)) {
                    valor = " 2";
                } else if (origem.equals(vertice)) {
                    valor = eDirigido ? "-1" : "1";
                } else if (destino.equals(vertice)) {
                    valor = " 1";
                } else {
                    valor = " 0";
                }
                matriz.append(valor).append("\t");
            }
            matriz.append("\n");
        }
        System.out.println(matriz);
    }

    public List<String> dfsIterativo(String origem, String destino) {
        Vertice verticeOrigem = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        Vertice verticeDestino = destino == null ? null
                : encontraVertice(destino).orElseThrow(
                        () -> new IllegalArgumentException("Vertice " + destino + " não encontrado."));

        Stack<Vertice> pilha = new Stack<>();
        List<Vertice> visitados = new ArrayList<>();
        StringBuilder percurso = new StringBuilder("Percurso = ");

        visitados.add(verticeOrigem);
        pilha.push(verticeOrigem);
        percurso.append(verticeOrigem.getNome()).append(", ");

        while (!pilha.isEmpty()) {
            Vertice atual = pilha.peek();
            if (atual.equals(verticeDestino)) break;

            List<Vertice> adjacencias = atual.getAdjacencias();
            List<Vertice> adjacenciasOrdenadas = adjacencias.stream()
                    .sorted(Comparator.comparing(Vertice::getNome)).toList();

            Optional<Vertice> proximo = adjacenciasOrdenadas.stream()
                    .filter(a -> !visitados.contains(a)).findFirst();

            if (proximo.isPresent()) {
                Vertice adjacencia = proximo.get();
                visitados.add(adjacencia);
                percurso.append(adjacencia.getNome()).append(", ");
                pilha.push(adjacencia);
            } else {
                pilha.pop();
            }
        }

        System.out.println(percurso);
        return visitados.stream().map(Vertice::getNome).toList();
    }

    public List<String> dfsRecursivo(String origem, String destino, List<Vertice> visitados) {
        final List<Vertice> visitadosAtual = visitados != null ? visitados : new ArrayList<>();
        Vertice v = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        visitadosAtual.add(v);

        if (origem.equals(destino)) {
            return visitadosAtual.stream().map(Vertice::getNome).toList();
        }

        for (Vertice adj : v.getAdjacencias()) {
            if (visitadosAtual.contains(adj)) continue;
            dfsRecursivo(adj.getNome(), destino, visitadosAtual);
            if (destino != null && visitadosAtual.stream().anyMatch(x -> x.getNome().equals(destino))) {
                return visitadosAtual.stream().map(Vertice::getNome).toList();
            }
        }
        return visitadosAtual.stream().map(Vertice::getNome).toList();
    }

    public int encontraComprimentoCaminho(String... caminho) {
        if (!ePonderado) {
            return caminho.length - 1;
        }
        int comprimento = 0;
        List<Aresta> arestasPercorridas = new ArrayList<>();

        for (int i = 0; i < caminho.length - 1; i++) {
            int indiceAtual = i;
            Vertice origem = encontraVertice(caminho[indiceAtual]).orElseThrow(
                    () -> new IllegalArgumentException("Vertice " + caminho[indiceAtual] + " não encontrado."));
            Vertice destino = encontraVertice(caminho[indiceAtual + 1]).orElseThrow(
                    () -> new IllegalArgumentException("Vertice " + caminho[indiceAtual + 1] + " não encontrado."));
            Optional<Aresta> aresta = arestas.stream()
                    .filter(a -> a.getVerticeOrigem().equals(origem) && a.getVerticeDestino().equals(destino))
                    .findFirst();
            if (aresta.isPresent()) {
                if (arestasPercorridas.contains(aresta.get())) {
                    throw new IllegalArgumentException("Aresta repetida!");
                }
                arestasPercorridas.add(aresta.get());
                comprimento += aresta.get().getPeso();
            }
        }
        return comprimento;
    }

    public boolean eConexo() {
        for (Vertice v : vertices)
            if (v.getInDegree() == 0 || v.getOutDegree() == 0) {
                return false;
            }
        for (Vertice v : vertices) {
            List<String> caminho = dfsIterativo(v.getNome(), null);
            if (caminho.size() < vertices.size()) {
                return false;
            }
        }
        return true;
    }

    public boolean eConexoSimplificado() {
        if (vertices.stream().anyMatch(v -> v.getInDegree() == 0 || v.getOutDegree() == 0)) {
            return false;
        }
        return vertices.stream().noneMatch(v -> dfsIterativo(v.getNome(), null).size() < vertices.size());
    }

    public List<String> greedySearch(String nomeVerticeOrigem, String nomeVerticeDestino) {
        List<Vertice> verticesVisitados = new ArrayList<>();
        int comprimentoCaminho = 0;

        Vertice verticeOrigem = encontraVertice(nomeVerticeOrigem).orElseThrow();
        Vertice verticeDestino = encontraVertice(nomeVerticeDestino).orElseThrow();

        verticesVisitados.add(verticeOrigem);
        Vertice atual = verticeOrigem;

        while (!atual.equals(verticeDestino)) {
            Vertice verticeAlvo = atual;
            List<Vertice> adjacencias = verticeAlvo.getAdjacencias();
            if (adjacencias == null || adjacencias.isEmpty()) {
                System.out.println("Caminho não encontrado. Busca falhou em: " + atual.getNome());
                return null;
            }

            List<Aresta> arestasVizinhas = new ArrayList<>();
            for (Vertice vizinho : adjacencias) {
                if (!verticesVisitados.contains(vizinho)) {
                    arestasVizinhas.addAll(obtemArestasParaVizinho(verticeAlvo, vizinho));
                }
            }

            if (arestasVizinhas.isEmpty()) {
                System.out.println("Caminho não encontrado. Busca falhou em: " + atual.getNome());
                return null;
            }

            Aresta melhorAresta = arestasVizinhas.stream()
                    .min(Comparator.comparing(Aresta::getPeso))
                    .orElseThrow();

            comprimentoCaminho += melhorAresta.getPeso() != null ? melhorAresta.getPeso() : 0;
            atual = obtemVerticeOposto(melhorAresta, verticeAlvo);
            verticesVisitados.add(atual);

            System.out.println("Percorrendo aresta " + melhorAresta.getNome() +
                    " (peso " + melhorAresta.getPeso() +
                    ") para o vértice " + atual.getNome());
        }

        List<String> nomesVisitados = verticesVisitados.stream().map(Vertice::getNome).toList();
        System.out.println("Destino " + verticeDestino.getNome() + " encontrado! Busca concluída com sucesso.");
        System.out.println("Caminho: " + String.join(" -> ", nomesVisitados));
        System.out.println("Comprimento do caminho: " + comprimentoCaminho);
        return nomesVisitados;
    }

    private List<Aresta> obtemArestasParaVizinho(Vertice atual, Vertice vizinho) {
        return arestas.stream()
                .filter(a -> (a.getVerticeOrigem().equals(atual) && a.getVerticeDestino().equals(vizinho)) ||
                        (!eDirigido && a.getVerticeDestino().equals(atual) && a.getVerticeOrigem().equals(vizinho)))
                .toList();
    }

    private Vertice obtemVerticeOposto(Aresta aresta, Vertice vertice) {
        return aresta.getVerticeOrigem().equals(vertice) ? aresta.getVerticeDestino() : aresta.getVerticeOrigem();
    }

    // ============================================================
    // NOVOS MÉTODOS PARA O LINKEDIN ANALYZER
    // ============================================================

    /**
     * BFS para encontrar o grau de separação (menor número de arestas)
     * entre dois vértices em um grafo não-direcionado.
     * @return número de arestas no menor caminho, ou -1 se não houver conexão
     */
    public int bfsGrauDeSeparacao(String nomeOrigem, String nomeDestino) {
        Vertice origem = encontraVertice(nomeOrigem).orElse(null);
        Vertice destino = encontraVertice(nomeDestino).orElse(null);

        if (origem == null || destino == null) return -1;
        if (origem.equals(destino)) return 0;

        Queue<Vertice> fila = new LinkedList<>();
        Map<Vertice, Integer> distancia = new HashMap<>();
        Set<Vertice> visitados = new HashSet<>();

        fila.add(origem);
        visitados.add(origem);
        distancia.put(origem, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            int distAtual = distancia.get(atual);

            for (Vertice vizinho : atual.getAdjacencias()) {
                if (!visitados.contains(vizinho)) {
                    if (vizinho.equals(destino)) {
                        return distAtual + 1;
                    }
                    visitados.add(vizinho);
                    distancia.put(vizinho, distAtual + 1);
                    fila.add(vizinho);
                }
            }
        }
        return -1; // Não há conexão
    }

    /**
     * Algoritmo de Dijkstra para encontrar o menor caminho ponderado
     * (maior afinidade = menor soma de pesos) entre dois vértices.
     * @return Map com "custo" (Integer) e "caminho" (List<String>)
     */
    public Map<String, Object> dijkstra(String nomeOrigem, String nomeDestino) {
        Vertice origem = encontraVertice(nomeOrigem).orElse(null);
        Vertice destino = encontraVertice(nomeDestino).orElse(null);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("custo", -1);
        resultado.put("caminho", new ArrayList<String>());

        if (origem == null || destino == null) return resultado;
        if (origem.equals(destino)) {
            resultado.put("custo", 0);
            resultado.put("caminho", List.of(nomeOrigem));
            return resultado;
        }

        // Mapa de distâncias (custo acumulado)
        Map<Vertice, Integer> distancias = new HashMap<>();
        // Mapa de predecessores para reconstruir o caminho
        Map<Vertice, Vertice> predecessores = new HashMap<>();
        // Set de visitados
        Set<Vertice> visitados = new HashSet<>();

        // Inicializa todas as distâncias como INFINITO
        for (Vertice v : vertices) {
            distancias.put(v, Integer.MAX_VALUE);
        }
        distancias.put(origem, 0);

        // PriorityQueue ordenada pelo menor custo
        PriorityQueue<Vertice> pq = new PriorityQueue<>(
                Comparator.comparingInt(distancias::get));
        pq.add(origem);

        while (!pq.isEmpty()) {
            Vertice atual = pq.poll();

            if (visitados.contains(atual)) continue;
            visitados.add(atual);

            if (atual.equals(destino)) break;

            // Para cada vizinho do vértice atual
            for (Vertice vizinho : atual.getAdjacencias()) {
                if (visitados.contains(vizinho)) continue;

                // Encontra o peso da aresta entre atual e vizinho
                int peso = getPesoAresta(atual, vizinho);
                if (peso == Integer.MAX_VALUE) continue;

                int novaDistancia = distancias.get(atual) + peso;

                if (novaDistancia < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDistancia);
                    predecessores.put(vizinho, atual);
                    pq.add(vizinho);
                }
            }
        }

        // Se o destino não foi alcançado
        if (distancias.get(destino) == Integer.MAX_VALUE) {
            return resultado;
        }

        // Reconstrói o caminho do destino até a origem
        List<String> caminho = new ArrayList<>();
        Vertice atual = destino;
        while (atual != null) {
            caminho.add(0, atual.getNome());
            atual = predecessores.get(atual);
        }

        resultado.put("custo", distancias.get(destino));
        resultado.put("caminho", caminho);
        return resultado;
    }

    /**
     * Retorna o peso da aresta entre dois vértices (grafo não-direcionado).
     * Se não houver aresta, retorna Integer.MAX_VALUE.
     */
    private int getPesoAresta(Vertice v1, Vertice v2) {
        for (Aresta a : arestas) {
            if ((a.getVerticeOrigem().equals(v1) && a.getVerticeDestino().equals(v2)) ||
                (!eDirigido && a.getVerticeOrigem().equals(v2) && a.getVerticeDestino().equals(v1))) {
                return a.getPeso() != null ? a.getPeso() : 1;
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Encontra todos os componentes conexos do grafo.
     * @return Lista de listas, onde cada lista interna é um componente conexo
     */
    public List<List<String>> encontrarComponentesConexos() {
        List<List<String>> componentes = new ArrayList<>();
        Set<Vertice> visitadosGlobal = new HashSet<>();

        for (Vertice v : vertices) {
            if (!visitadosGlobal.contains(v)) {
                // Faz BFS/DFS a partir deste vértice para encontrar todo o componente
                List<String> componente = new ArrayList<>();
                Queue<Vertice> fila = new LinkedList<>();
                fila.add(v);
                visitadosGlobal.add(v);

                while (!fila.isEmpty()) {
                    Vertice atual = fila.poll();
                    componente.add(atual.getNome());

                    for (Vertice vizinho : atual.getAdjacencias()) {
                        if (!visitadosGlobal.contains(vizinho)) {
                            visitadosGlobal.add(vizinho);
                            fila.add(vizinho);
                        }
                    }
                }
                componentes.add(componente);
            }
        }
        return componentes;
    }

    public List<Aresta> getArestas() {
        return arestas;
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    public boolean isEDirigido() {
        return eDirigido;
    }

    public boolean isEPonderado() {
        return ePonderado;
    }

    public int getOrdem() {
        return ordem;
    }

    public int getTamanho() {
        return tamanho;
    }

    @Override
    public String toString() {
        return """
                direcionado = %s,
                ordem = %d,
                tamanho = %d,
                vertices = %s,
                arestas = %s,
                graus = %s,
                adjacencias = %s,
                adjacentes = %s
                }""".formatted(eDirigido ? "sim" : "não", ordem, tamanho, vertices, arestas, exibeGrausDosVertices(),
                exibeAdjacencias(), exibeAdjacentes());
    }
}
