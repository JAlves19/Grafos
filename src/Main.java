import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("O Java está procurando o arquivo em: " + System.getProperty("user.dir"));
    Grafo g = new Grafo();
    String caminhoArquivo = "roadNet-PA.txt";

    System.out.println("Lendo dataset do SNAP...");
    try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))){
        String linha;
        int linhasLidas = 0;
        while ((linha = br.readLine()) != null){
            linhasLidas++;
            if (linha.startsWith("#") || linha.isBlank()) continue; // Ignorar comentários

            String[] partes = linha.split("\\s+");
            if (partes.length >= 2){
                try {
                    int v = Integer.parseInt(partes[0]);
                    int w = Integer.parseInt(partes[1]);
                    if (v < w){
                        g.addEdge(v, w);
                    }

                } catch (NumberFormatException e){
                    continue;
                }
            }
            if (linhasLidas % 500000 == 0 && linhasLidas > 0){
                System.out.println("Processadas " + linhasLidas + " arestas...");
            }
        }
        System.out.println("Total de linhas lidas: " + linhasLidas);
    } catch (IOException e ){
        System.err.println("ERRO CRÍTICO: Não foi possível ler o arquivo.");
        System.err.println("Certifique-se que o nome é exatamente roadNet-PA.txt");
        e.printStackTrace();
    }
        System.out.println("Leitura concluída!");
        System.out.println("Número de vértices: " + g.V());
        System.out.println("Número de arestas: " + g.E());
        System.out.println("Densidade do grafo: " + g.getDensity());

        g.gerarDistribuicaoGraus();

        var s = g.getEstatiscasGrau();
        System.out.println("Grau máximo: " + s.get("max"));
        System.out.println("Grau mínimo: " + s.get("min"));
        System.out.println("Grau médio: " + s.get("medio"));

        System.out.println("Gerando amostra DOT para Graphviz...");
        saveToFile("amostra_pensilvania.dot", g.toDot(200));
    }

    private static void saveToFile(String nome, String conteudo){
        try(PrintWriter out = new PrintWriter(nome)) {
            out.println(conteudo);
        }catch (Exception e) {e.printStackTrace();}
        }
}