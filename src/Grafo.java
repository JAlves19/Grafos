import java.util.*;
import java.io.*;

public class Grafo {
    private final Map<Integer, List<Integer>> adj;
    private int V;
    private int E;

    public Grafo(){
        this.adj = new HashMap<>();
        this.V = 0;
        this.E = 0;
    }

    public void addEdge(int v, int w){
        adj.putIfAbsent(v, new ArrayList<>());
        adj.putIfAbsent(w, new ArrayList<>());
        adj.get(v).add(w);
        adj.get(w).add(v);
        E++;
    }

    public int V() { return adj.size(); }
    public int E() { return E; }

    //Densidade
    public double getDensity() {
        long v = (long) V();
        long e = (long) E();
        if (v <= 1) return 0.0;
        return (2.0 * e) / (v * (v - 1));
    }

    //Retorna o grau de um no
    public int degree(int v){
        return adj.containsKey(v) ? adj.get(v).size() : 0;
    }

    //Representa a parte do Somatorio e a media
    public double getAverageClusteringCoefficient() {
        List<Integer> nos = new ArrayList<>(adj.keySet());
        Collections.shuffle(nos); // Aleatoriedade para validade estatística

        double somaCi = 0;
        int amostra = Math.min(nos.size(), 10000);

        for (int i = 0; i < amostra; i++) {
            somaCi += calcularClusteringLocal(nos.get(i));
        }

        return somaCi / amostra; // 1/n * soma de Ci
    }

    // Cálculo do Coeficiente Local (Ci)
    private double calcularClusteringLocal(int v) {
        List<Integer> vizinhos = adj.get(v);
        int k = vizinhos.size(); // k é o grau do nó (número de vizinhos)

        // Se o nó tem menos de 2 vizinhos, não pode formar triângulos
        if (k < 2) return 0.0;

        int ligacoesEntreVizinhos = 0;

        // Comparamos cada par de vizinhos (i, j)
        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                int vizinhoA = vizinhos.get(i);
                int vizinhoB = vizinhos.get(j);

                if (adj.get(vizinhoA).contains(vizinhoB)) {
                    ligacoesEntreVizinhos++;
                }
            }
        }

        // Representa: O denominador da fórmula Ci
        double possiveis = (double) k * (k - 1) / 2.0;
        // Retorna o valor de Ci
        return ligacoesEntreVizinhos / possiveis;
    }

    // Salva a distribuição de Graus
    public void salvarDistribuicaoGraus(String nomeArquivo) {
        Map<Integer, Integer> distribuicao = new TreeMap<>();
        for (int v : adj.keySet()) {
            int gd = degree(v);
            distribuicao.put(gd, distribuicao.getOrDefault(gd, 0) + 1);
        }

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivo)))) {
            writer.println("grau,quantidade");
            for (var entry : distribuicao.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println("Arquivo de distribuição pronto para o Notebook: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Gera um arquivo com as metricas
    public void salvarResumoMetricas(String nomeArquivo) {
        var s = getEstatiscasGrau();
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("Métrica,Valor");
            writer.println("Vertices," + V());
            writer.println("Arestas," + E());
            writer.println("Densidade," + getDensity());
            writer.println("Grau_Maximo," + s.get("max"));
            writer.println("Grau_Minimo," + s.get("min"));
            writer.println("Grau_Medio," + s.get("medio"));
            writer.println("Clustering_Medio," + getAverageClusteringCoefficient());

            System.out.println("Resumo de métricas salvo em: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Calcula estatísticas de graus
    public Map<String, Integer> getEstatiscasGrau(){
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE; // Inicializa com o maior valor possível
        long soma = 0;

        for (int v : adj.keySet()){
            int d = degree(v);
            if (d > max) max = d;
            if (d < min) min = d; // Lógica para o mínimo
            soma += d;
        }

        Map<String, Integer> s = new HashMap<>();
        s.put("max", max);
        s.put("min", min);
        s.put("medio", (int) (soma / (V() == 0 ? 1 : V())));
        return s;
    }

}