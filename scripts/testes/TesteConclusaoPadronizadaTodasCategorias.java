package gerard.campoaditivo.conclusao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class TesteConclusaoPadronizadaTodasCategorias {
    private static int verificacoes;

    public static void main(String[] args) {
        Map<TipoSituacaoAditiva, List<String>> papeis =
                new EnumMap<TipoSituacaoAditiva, List<String>>(
                        TipoSituacaoAditiva.class);
        papeis.put(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                lista("papel.parte1", "papel.parte2", "papel.todo"));
        papeis.put(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                lista("papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"));
        papeis.put(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS,
                lista("papel.parte1", "papel.parte2", "papel.todo",
                        "papel.transformacao", "papel.estadoFinal"));
        papeis.put(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                lista("papel.referido", "papel.diferenca", "papel.referendo"));
        papeis.put(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                lista("papel.transformacao1", "papel.transformacao2",
                        "papel.transformacaoFinal"));
        papeis.put(TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS,
                lista("papel.estadoInicial", "papel.transformacao1",
                        "papel.estadoIntermediario", "papel.transformacao2",
                        "papel.estadoFinal"));
        papeis.put(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                lista("papel.relacaoInicial", "papel.transformacao",
                        "papel.relacaoFinal"));
        papeis.put(TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                lista("papel.relacao1", "papel.relacao2", "papel.relacaoFinal"));

        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            List<String> esperados = papeis.get(tipo);
            confirmar(esperados != null,
                    "categoria sem contrato de conclusão: " + tipo);
            List<EstadoPosicionamentoModelagem> aguardando = criarEstados(
                    esperados, false);
            confirmar(avaliador.avaliar(esperados, aguardando)
                            == FaseConclusaoModelagem.AGUARDANDO_PREENCHIMENTO_INCOGNITA,
                    "a interrogação deve aguardar o protocolo em " + tipo);
            List<EstadoPosicionamentoModelagem> completos = criarEstados(
                    esperados, true);
            confirmar(avaliador.estaConcluida(esperados, completos),
                    "a conclusão com incógnita preenchida deve funcionar em " + tipo);
        }
        System.out.println("Conclusão padronizada nas oito categorias: "
                + verificacoes + " verificações.");
    }

    private static List<EstadoPosicionamentoModelagem> criarEstados(
            List<String> esperados, boolean preencherIncognita) {
        List<EstadoPosicionamentoModelagem> estados =
                new ArrayList<EstadoPosicionamentoModelagem>();
        for (int i = 0; i < esperados.size(); i++) {
            String papel = esperados.get(i);
            boolean incognita = i == esperados.size() - 1;
            estados.add(new EstadoPosicionamentoModelagem(
                    papel, papel,
                    incognita && !preencherIncognita ? "?" : Integer.toString(i + 1),
                    true, incognita, incognita && preencherIncognita));
        }
        return estados;
    }

    private static List<String> lista(String... papeis) {
        return Arrays.asList(papeis);
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
