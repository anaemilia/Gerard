import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.MapeadorPapelSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTextoAditivo;
import java.util.ArrayList;
import java.util.List;

public final class TesteSincronizacaoElementosSemanticosTexto {
    public static void main(String[] args) {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        estado.limpar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] { 4, 8, 12 },
                new boolean[] { true, true, true },
                0,
                EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);

        ElementoTextoMovel referido = new ElementoTextoMovel("6,", 10);
        referido.vincularSemantica("papel.referido", 0, 1, "6");
        ElementoTextoMovel relativo = new ElementoTextoMovel("8", 20);
        relativo.vincularSemantica("papel.diferenca", 0, 1, "8");
        ElementoTextoMovel referendo = new ElementoTextoMovel("?", 30);
        referendo.vincularSemantica("papel.referendo", 0, 1, "?");
        ItemTextoArrastavel marcadorMovido = new ItemTextoArrastavel(
                120, 120, 18, 20, "6", false, "6", "papel.referido");
        ItemTextoArrastavel incognitaMovidaNoTexto = new ItemTextoArrastavel(
                160, 120, 18, 20, "?", false, "?", "papel.referendo");

        List<ElementoSemanticoTexto> elementos = new ArrayList<ElementoSemanticoTexto>();
        elementos.add(referido);
        elementos.add(relativo);
        elementos.add(referendo);
        elementos.add(marcadorMovido);
        elementos.add(incognitaMovidaNoTexto);

        SincronizadorElementosSemanticosTexto sincronizador =
                new SincronizadorElementosSemanticosTextoAditivo();
        sincronizador.sincronizar(elementos, snapshot,
                new MapeadorPapelSemanticoTexto() {
                    @Override
                    public int paraIndiceSemantico(String chave) {
                        if ("papel.referido".equals(chave)) return 0;
                        if ("papel.diferenca".equals(chave)) return 1;
                        if ("papel.referendo".equals(chave)) return 2;
                        return -1;
                    }
                });

        exigir("4,".equals(referido.valor),
                "O referido textual deveria acompanhar o diagrama e preservar a pontuação.");
        exigir("8".equals(relativo.valor),
                "O valor relativo textual deveria permanecer sincronizado.");
        exigir("?".equals(referendo.valor),
                "A interrogação textual deve permanecer inalterada.");
        exigir("4".equals(marcadorMovido.valor),
                "O marcador deslocado dentro da área do enunciado deveria ser atualizado.");
        exigir("?".equals(incognitaMovidaNoTexto.valor),
                "A interrogação deslocada na área do enunciado deve permanecer inalterada.");
        exigir("?".equals(referendo.getValorSemanticoOriginal()),
                "A origem da incógnita deve permanecer estável para logs e validações.");

        System.out.println("Teste aprovado: valores conhecidos acompanham os diagramas e a interrogação permanece estável.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
