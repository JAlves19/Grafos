import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Grafo g = new Grafo();
        String nomeArquivoTxt = "roadNet-PA.txt";
        String urlGz = "https://snap.stanford.edu/data/roadNet-PA.txt.gz";

        System.out.println("--- Verificando Dataset ---");

        // Verifica se o arquivo está descompactado
        if (!Files.exists(Paths.get(nomeArquivoTxt))) {
            System.out.println("Arquivo não encontrado. Baixando e descompactando do SNAP...");
            try {
                baixarEDescompactar(urlGz, nomeArquivoTxt);
                System.out.println("Arquivo descompactado com sucesso na pasta do projeto.");
            } catch (IOException e) {
                System.err.println("Erro ao baixar/descompactar: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("O arquivo " + nomeArquivoTxt + " já existe localmente. Pulando download.");
        }

        // LEITURA DO ARQUIVO
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivoTxt))) {
            String linha;
            int contadorLinhas = 0;
            while ((linha = br.readLine()) != null) {
                if (linha.startsWith("#") || linha.isBlank()) continue;
                String[] partes = linha.split("\\s+");
                if (partes.length >= 2) {
                    int v = Integer.parseInt(partes[0]);
                    int w = Integer.parseInt(partes[1]);
                    if (v < w) g.addEdge(v, w);
                }
                contadorLinhas++;
                if (contadorLinhas % 1000000 == 0) System.out.println("Processadas " + contadorLinhas + " linhas...");
            }
        } catch (IOException e) {
            System.err.println("Erro na leitura do arquivo: " + e.getMessage());
        }

        System.out.println("\n---- Gerando resultados ----");
        g.salvarDistribuicaoGraus("distribuicao_graus.csv");
        g.salvarResumoMetricas("resumo_metricas.csv");
    }

    // Metodo para baixar e descompactar na pasta do projeto
    private static void baixarEDescompactar(String urlStr, String destinoTxt) throws IOException {
        URL url = new URL(urlStr);
        try (GZIPInputStream gis = new GZIPInputStream(url.openStream());
             FileOutputStream fos = new FileOutputStream(destinoTxt)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }
}