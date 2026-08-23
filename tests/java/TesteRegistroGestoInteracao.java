import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.interacao.ContextoRegistroGesto;
import gerard.interacao.DestinoGeometricoGesto;
import gerard.interacao.RegistroGestoInteracao;
import gerard.interacao.ResumoGestoArraste;
import gerard.pesquisador.log.LoggerGestosInteracaoGerard;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public final class TesteRegistroGestoInteracao {
    public static void main(String[] args) throws Exception {
        ItemTextoArrastavel item = new ItemTextoArrastavel(
                100, 240, 40, 28, "7", true, "7", "papel.parte1", "token-7");
        ResumoGestoArraste gesto = new ResumoGestoArraste(
                "gesto-1", 110, 250, 190, 330, 3, 1, 120L, 1000L);
        ContextoRegistroGesto contexto = new ContextoRegistroGesto(
                "sessao-1", "usuario-1", "problema-1", "tentativa-1");

        RegistroGestoInteracao registro = item.produzirRegistroGestoArraste(
                gesto, contexto,
                DestinoGeometricoGesto.FORA_DE_ELEMENTO_DO_DIAGRAMA);

        exigir(registro != null && "token-7".equals(registro.getObjetoInterfaceId()),
                "O objeto representacional deve produzir e identificar seu gesto.");
        exigir("ARRASTAR_POSICIONAR".equals(registro.getTipoGesto()),
                "O protocolo físico deveria permanecer explícito.");
        exigir(registro.getDestinoGeometrico()
                        == DestinoGeometricoGesto.FORA_DE_ELEMENTO_DO_DIAGRAMA,
                "Soltar fora deve permanecer um fato geométrico, sem virar erro.");
        exigir(registro.getAmostras() == 3
                        && registro.getMudancasOrientacao() == 1
                        && registro.getDistanciaPercorrida() == 120L,
                "O objeto não deve perder as observações fornecidas pelo handler.");

        File diretorio = Files.createTempDirectory("gerard-gestos-").toFile();
        File arquivo = new File(diretorio, "gestos.tsv");
        LoggerGestosInteracaoGerard logger =
                new LoggerGestosInteracaoGerard(arquivo);
        logger.publicar(registro);

        List<String> linhas = Files.readAllLines(
                arquivo.toPath(), StandardCharsets.UTF_8);
        exigir(linhas.size() == 2,
                "O logger deveria escrever cabeçalho e um único gesto.");
        exigir(linhas.get(0).contains("gesture_id")
                        && linhas.get(0).contains("destino_geometrico")
                        && !linhas.get(0).contains("\tce\t")
                        && !linhas.get(0).contains("action_id")
                        && !linhas.get(0).contains("diagnostico"),
                "O esquema de gestos não pode conter avaliação de ações.");
        exigir(linhas.get(1).contains("FORA_DE_ELEMENTO_DO_DIAGRAMA")
                        && linhas.get(1).contains("sessao-1")
                        && linhas.get(1).contains("tentativa-1"),
                "A persistência deve conservar destino físico e contexto factual.");

        System.out.println("Teste aprovado: gesto factual separado da ação instrumental.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
