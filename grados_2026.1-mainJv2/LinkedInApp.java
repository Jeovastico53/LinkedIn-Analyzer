package br.com.unipe;

import java.util.List;

/**
 * LinkedInApp - Aplicação principal para testar o LinkedInAnalyzer.
 * 
 * Cenário de testes:
 * - Rede Principal: Ana, Bruno, Carlos, Daniela, Eduardo, Fernanda
 * - Grupo Isolado 1: Gabriel, Hugo
 * - Grupo Isolado 2: Igor, Juliana
 * 
 * Conexões:
 * 1. Ana <-> Bruno (Peso 1)
 * 2. Ana <-> Carlos (Peso 2)
 * 3. Ana <-> Daniela (Peso 8)
 * 4. Bruno <-> Eduardo (Peso 1)
 * 5. Carlos <-> Eduardo (Peso 1)
 * 6. Daniela <-> Fernanda (Peso 5)
 * 7. Eduardo <-> Fernanda (Peso 1)
 * 8. Gabriel <-> Hugo (Peso 1)
 * 9. Igor <-> Juliana (Peso 1)
 */
public class LinkedInApp {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║     LINKEDIN ANALYZER - Motor de Análises de Rede          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // ============================================================
        // CONSTRUÇÃO DO GRAFO (NÃO-DIRECIONADO E PONDERADO)
        // ============================================================
        Grafo redeSocial = new Grafo(false, true);

        // Adiciona todos os vértices (perfis)
        redeSocial.adicionaVertices(
            "Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda",
            "Gabriel", "Hugo", "Igor", "Juliana"
        );

        // Adiciona as conexões com seus pesos (afinidade)
        // Rede Principal
        redeSocial.addAresta("Ana", "Bruno", 1);      // Muita afinidade
        redeSocial.addAresta("Ana", "Carlos", 2);
        redeSocial.addAresta("Ana", "Daniela", 8);    // Pouca afinidade
        redeSocial.addAresta("Bruno", "Eduardo", 1);  // Muita afinidade
        redeSocial.addAresta("Carlos", "Eduardo", 1); // Muita afinidade
        redeSocial.addAresta("Daniela", "Fernanda", 5);
        redeSocial.addAresta("Eduardo", "Fernanda", 1); // Muita afinidade

        // Grupos Isolados
        redeSocial.addAresta("Gabriel", "Hugo", 1);
        redeSocial.addAresta("Igor", "Juliana", 1);

        System.out.println("📊 Rede social construída com sucesso!");
        System.out.println("   → Ordem (nº de perfis): " + redeSocial.getOrdem());
        System.out.println("   → Tamanho (nº de conexões): " + redeSocial.getTamanho());
        System.out.println("   → Tipo: Não-direcionado e Ponderado");
        System.out.println();

        // ============================================================
        // INSTANCIA O ANALYZER
        // ============================================================
        LinkedInAnalyzer analyzer = new LinkedInAnalyzer(redeSocial);

        // ============================================================
        // TESTE 1: SUGESTÃO DE CONEXÕES (AMIGOS DE 2º GRAU)
        // ============================================================
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🤝 MISSÃO 2: Sugestão de Conexões (Amigos de 2º Grau)");
        System.out.println("═══════════════════════════════════════════════════════════════");

        System.out.println("\n👉 Para Ana:");
        List<LinkedInAnalyzer.SugestaoConexao> sugestoesAna = analyzer.sugerirConexoes("Ana");
        if (sugestoesAna.isEmpty()) {
            System.out.println("   Nenhuma sugestão encontrada.");
        } else {
            for (LinkedInAnalyzer.SugestaoConexao s : sugestoesAna) {
                System.out.println("   • " + s);
            }
        }

        System.out.println("\n👉 Para Eduardo:");
        List<LinkedInAnalyzer.SugestaoConexao> sugestoesEduardo = analyzer.sugerirConexoes("Eduardo");
        if (sugestoesEduardo.isEmpty()) {
            System.out.println("   Nenhuma sugestão encontrada.");
        } else {
            for (LinkedInAnalyzer.SugestaoConexao s : sugestoesEduardo) {
                System.out.println("   • " + s);
            }
        }

        System.out.println("\n👉 Para Fernanda:");
        List<LinkedInAnalyzer.SugestaoConexao> sugestoesFernanda = analyzer.sugerirConexoes("Fernanda");
        if (sugestoesFernanda.isEmpty()) {
            System.out.println("   Nenhuma sugestão encontrada.");
        } else {
            for (LinkedInAnalyzer.SugestaoConexao s : sugestoesFernanda) {
                System.out.println("   • " + s);
            }
        }

        // ============================================================
        // TESTE 2: GRAU DE SEPARAÇÃO
        // ============================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("📏 MISSÃO 3: Grau de Separação (Menor nº de passos)");
        System.out.println("═══════════════════════════════════════════════════════════════");

        String[][] paresTeste = {
            {"Ana", "Fernanda"},
            {"Ana", "Bruno"},
            {"Ana", "Eduardo"},
            {"Bruno", "Carlos"},
            {"Daniela", "Eduardo"},
            {"Ana", "Gabriel"},   // Isolados → -1
            {"Igor", "Juliana"},  // Direto → 1
            {"Gabriel", "Hugo"}   // Direto → 1
        };

        for (String[] par : paresTeste) {
            int grau = analyzer.grauDeSeparacao(par[0], par[1]);
            String resultado = grau == -1 ? "isolados (sem conexão)" : grau + " passo(s)";
            System.out.printf("   %s <-> %s: %s%n", par[0], par[1], resultado);
        }

        // ============================================================
        // TESTE 3: ROTA E CUSTO DE MAIOR AFINIDADE (DIJKSTRA)
        // ============================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🛤️  MISSÃO 4: Rota e Custo de Maior Afinidade (Dijkstra)");
        System.out.println("═══════════════════════════════════════════════════════════════");

        System.out.println("\n⭐ CASO ESPECIAL: Ana → Fernanda");
        System.out.println("   A rota mais CURTA em passos seria: Ana → Daniela → Fernanda (2 passos)");
        System.out.println("   Mas o custo seria: 8 + 5 = 13");
        System.out.println("   A rota de MAIOR AFINIDADE deve ser: Ana → Bruno → Eduardo → Fernanda (3 passos)");
        System.out.println("   Com custo: 1 + 1 + 1 = 3");
        System.out.println();

        LinkedInAnalyzer.ResultadoDijkstra resultadoAnaFernanda = 
            analyzer.rotaMaiorAfinidade("Ana", "Fernanda");
        System.out.println("   Resultado: " + resultadoAnaFernanda);

        System.out.println("\n👉 Outros testes:");
        String[][] paresDijkstra = {
            {"Ana", "Eduardo"},
            {"Bruno", "Carlos"},
            {"Carlos", "Fernanda"},
            {"Ana", "Gabriel"}  // Isolados
        };

        for (String[] par : paresDijkstra) {
            LinkedInAnalyzer.ResultadoDijkstra res = analyzer.rotaMaiorAfinidade(par[0], par[1]);
            System.out.println("   " + par[0] + " → " + par[1] + ": " + res);
        }

        // ============================================================
        // TESTE 4: MAPEAR GRUPOS ISOLADOS
        // ============================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🗺️  MISSÃO 5: Mapear Grupos Isolados (Componentes Conexos)");
        System.out.println("═══════════════════════════════════════════════════════════════");

        List<List<String>> grupos = analyzer.mapearGruposIsolados();
        System.out.println("   Total de grupos/sub-redes encontrados: " + grupos.size());
        System.out.println();

        for (int i = 0; i < grupos.size(); i++) {
            List<String> grupo = grupos.get(i);
            String nomeGrupo;
            if (grupo.contains("Ana")) {
                nomeGrupo = "Rede Principal";
            } else if (grupo.contains("Gabriel")) {
                nomeGrupo = "Grupo Isolado 1";
            } else if (grupo.contains("Igor")) {
                nomeGrupo = "Grupo Isolado 2";
            } else {
                nomeGrupo = "Grupo " + (i + 1);
            }
            System.out.println("   📌 " + nomeGrupo + ": " + grupo);
        }

        // ============================================================
        // RESUMO FINAL
        // ============================================================
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              ✅ TODAS AS MISSÕES COMPLETADAS!               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}
