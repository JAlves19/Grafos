import java.util.*;
import java.io.*;

public class Grafo {
    private static final String NEWLINE = System.getProperty("line.separator");

    // A estrutura principal: Um Mapa onde a Chave é o Vértice (ID) e o Valor é uma Lista de vizinhos
    private final Map<Integer, List<Integer>> adj;
    private int V; // Contador de Vértices (Nós)
    private int E; // Contador de Arestas (Conexões)

    // CONSTRUTOR: Prepara o terreno para o grafo existir
    public Grafo(){
        this.adj = new HashMap<>(); // Inicializa o mapa vazio
        this.V = 0;
        this.E = 0;
    }

    // METODO DE INSERÇÃO: Cria a conexão entre dois pontos (v e w)
    public void addEdge(int v, int w){
        // Se o nó v ou w não existirem no mapa, eles são "criados" agora (instancia a lista)
        adj.putIfAbsent(v, new ArrayList<>());
        adj.putIfAbsent(w, new ArrayList<>());

        // Adiciona um na lista do outro (Grafo nao-direcionado/bidirecional)
        adj.get(v).add(w);
        adj.get(w).add(v);

        E++;
    }

    // Retorna a quantidade de vértices
    public int V(){
        return adj.size();
    }

    // Retorna o total de arestas
    public int E(){
        return E;
    }

    // CÁLCULO DE DENSIDADE: Mede o quão "conectado" o grafo está
    // (0.0 = sem conexões, 1.0 = todos conectados com todos)
    public double getDensity() {
        long v = (long) V();
        long e = (long) E();
        if (v <= 1) return 0.0;
        // Fórmula: 2 * Arestas / (Vértices * (Vértices - 1))
        return (2.0 * e) / (v * (v - 1));
    }

    // DISTRIBUIÇÃO DE GRAUS: Conta quantos nós tem 1 conexão, quantos têm 2, etc.
    public void gerarDistribuicaoGraus(){
        // TreeMap mantém os resultados ordenados pelo valor do Grau
        Map<Integer, Integer> distribuicao = new TreeMap<>();

        for (int v : adj.keySet()){
            int gd = degree(v); // Pega o grau do nó atual
            distribuicao.put(gd, distribuicao.getOrDefault(gd, 0) + 1);
        }

        System.out.println("\n=== Distribuição de Graus ===");
        System.out.println("Grau | Quantidade de Nós");
        for (var entry : distribuicao.entrySet()) {
            System.out.printf("%4d | %d\n", entry.getKey(), entry.getValue());
        }
    }

    // Retorna o grau de um vértice (quantos vizinhos ele tem)
    public int degree(int v){
        return adj.containsKey(v) ? adj.get(v).size() : 0;
    }

    // ESTATÍSTICAS: Calcula o grau máximo, mínimo e a média de conexões
    public Map<String, Integer> getEstatiscasGrau(){
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        long soma = 0;

        for (int v : adj.keySet()){
            int d = degree(v);
            if (d > max) max = d;
            if (d < min) min = d;
            soma += d;
        }

        Map<String, Integer> s = new HashMap<>();
        s.put("max", max);
        s.put("min", min);
        // Média simples: total de conexões / número de nós
        s.put("medio", (int) (soma/(V() == 0 ? 1 : V())));
        return s;
    }

    // EXPORTAÇÃO DOT --> Graphviz
    // Isso permite transformar o seu código Java em uma imagem real do grafo
    public String toDot(int limiteArestas){
        StringBuilder sb = new StringBuilder();
        sb.append("graph {" + NEWLINE);
        sb.append("  node[shape=point, color=blue];" + NEWLINE);

        int count = 0;
        for (int v : adj.keySet()){
            for(int w : adj.get(v)){
                // (v < w) evita desenhar a mesma aresta duas vezes (ida e volta)
                if (v < w){
                    sb.append(" " + v + " -- " + w + ";" + NEWLINE);
                    count ++;
                }
                if (count >= limiteArestas) break;
            }
            if (count >= limiteArestas) break;
        }
        sb.append("}" + NEWLINE);
        return sb.toString();
    }

    public void exportarGrausParaCSV(String nomeArquivo) {
        System.out.println("Exportando graus para " + nomeArquivo + "...");
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivo)))) {
            writer.println("id,grau"); // Cabeçalho

            // Para cada nó do grafo, salva o seu ID e o seu Grau (número de conexões)
            for (Integer v : adj.keySet()) {
                writer.println(v + "," + degree(v));
            }
            System.out.println("Exportação concluída!");
        } catch (IOException e) {
            System.err.println("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    public void salvarDistribuicaoGrausTxt(String nomeArquivo) {
        Map<Integer, Integer> distribuicao = new TreeMap<>();

        // Contabiliza os graus
        for (int v : adj.keySet()){
            int gd = degree(v);
            distribuicao.put(gd, distribuicao.getOrDefault(gd, 0) + 1);
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivo)))) {
            // Cabeçalho sem espaços complexos para facilitar a leitura via código
            out.println("Grau,Quantidade");

            for (var entry : distribuicao.entrySet()) {
                // Salva no formato "Grau,Quantidade" (estilo CSV, mas em .txt)
                // Isso é o padrão ouro para bibliotecas de gráficos
                out.printf("%d,%d\n", entry.getKey(), entry.getValue());
            }

            out.flush();
            System.out.println("Arquivo para gráfico gerado: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo de dados: " + e.getMessage());
        }
    }


}