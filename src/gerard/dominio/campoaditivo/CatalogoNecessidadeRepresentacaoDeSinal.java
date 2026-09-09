package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Único ponto que sabe, para cada chave de papel quantitativo já definida
 * pelas fábricas do domínio, se esse papel precisa de representação de sinal
 * — o fato pedagógico de {@link PapelQuantitativo#necessitaRepresentacaoDeSinal()}
 * ("todo número relativo ou transformação carrega uma lupa", decisão da
 * usuária de 2026-08-18).
 *
 * Antes desta classe, a camada de renderização
 * (RenderizadorDiagramaAditivoBase, em gerard.campoaditivo.diagrama.servico)
 * decidia o mesmo fato de forma paralela e hardcoded — um literal true/false
 * escolhido por forma (elipse vs retângulo) em cada método
 * relacao/transformacao/relacaoGrande/medida. Duas fontes de verdade para o
 * mesmo fato semântico é exatamente o que a Regra 3 do CLAUDE.md deste
 * repositório proíbe (dívida arquitetural a resolver antes de qualquer rota
 * nova) — esta classe centraliza a consulta no objeto de domínio real,
 * eliminando o literal hardcoded como fonte paralela. Usada tanto pelo
 * desktop (Main.java, via GeradorCenaDiagramaAditivo) quanto pelo web
 * (ServicoSorteioAtividadeWeb), já que ambos compartilham o mesmo pipeline de
 * geração de cena.
 *
 * A mesma chave de papel pode existir em mais de uma categoria (ex.:
 * "papel.transformacao" em FabricaPapeisTransformacaoMedidas e em
 * FabricaPapeisTransformacaoDeRelacao; "papel.relacaoFinal" em
 * FabricaPapeisComposicaoDeRelacoes e em FabricaPapeisTransformacaoDeRelacao)
 * — o static initializer abaixo verifica que todas concordam e falha
 * ruidosamente ao carregar a classe (nunca silenciosamente) se alguma
 * divergir no futuro.
 */
public final class CatalogoNecessidadeRepresentacaoDeSinal {
    private static final Map<String, Boolean> NECESSIDADE_POR_CHAVE = construir();

    private CatalogoNecessidadeRepresentacaoDeSinal() {
    }

    public static boolean necessitaRepresentacaoDeSinal(String chavePapel) {
        Boolean necessita = NECESSIDADE_POR_CHAVE.get(chavePapel);
        if (necessita == null) {
            throw new IllegalArgumentException(
                    "chave de papel quantitativo desconhecida no catálogo de sinal: " + chavePapel);
        }
        return necessita.booleanValue();
    }

    private static Map<String, Boolean> construir() {
        Map<String, Boolean> mapa = new LinkedHashMap<String, Boolean>();
        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;

        registrar(mapa, PapelQuantitativo.parte1(nenhum));
        registrar(mapa, PapelQuantitativo.parte2(nenhum));
        registrar(mapa, PapelQuantitativo.todo(nenhum));

        registrar(mapa, FabricaPapeisComparacaoMedidas.referido(nenhum));
        registrar(mapa, FabricaPapeisComparacaoMedidas.valorRelativo(nenhum));
        registrar(mapa, FabricaPapeisComparacaoMedidas.referendo(nenhum));

        registrar(mapa, FabricaPapeisTransformacaoMedidas.estadoInicial(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoMedidas.transformacao(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoMedidas.estadoFinal(nenhum));

        registrar(mapa, FabricaPapeisComposicaoDeRelacoes.relacao1(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeRelacoes.relacao2(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeRelacoes.relacaoFinal(nenhum));

        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.estadoInicial(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.transformacao1(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.estadoIntermediario(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.transformacao2(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.estadoFinal(nenhum));

        registrar(mapa, FabricaPapeisTransformacaoDeRelacao.relacaoInicial(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoDeRelacao.transformacao(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoDeRelacao.relacaoFinal(nenhum));

        return mapa;
    }

    private static void registrar(Map<String, Boolean> mapa, PapelQuantitativo papel) {
        Boolean valor = Boolean.valueOf(papel.necessitaRepresentacaoDeSinal());
        Boolean existente = mapa.get(papel.getChave());
        if (existente != null && !existente.equals(valor)) {
            throw new IllegalStateException(
                    "chave de papel quantitativo com necessidade de sinal divergente entre categorias: "
                            + papel.getChave());
        }
        mapa.put(papel.getChave(), valor);
    }
}
