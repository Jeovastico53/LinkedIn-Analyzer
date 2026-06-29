package br.com.unipe;


public class Main {
    public static void main(String[] args) {
        Grafo grafo = new Grafo(true, true);

        grafo.adicionaVertices("1", "2", "3", "4", "5", "6", "7");

        grafo.addAresta("5", "6", 1);
        grafo.addAresta("5", "7", 2);

        grafo.addAresta("6", "7", 1);
        grafo.addAresta("7", "6", 1);

        grafo.addAresta("6", "2", 3);
        grafo.addAresta("7", "4", 2);

        grafo.addAresta("2", "4", 1);
        grafo.addAresta("4", "2", 1);

        grafo.addAresta("1", "2", 2);
        grafo.addAresta("4", "1", 3);

        grafo.addAresta("2", "3", 2);
        grafo.addAresta("1", "3", 1);
        grafo.addAresta("4", "3", 4);

        System.out.println(grafo.greedySearch("1", "5"));

    }
}