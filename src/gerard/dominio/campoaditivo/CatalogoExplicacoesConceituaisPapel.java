package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Coordenador de escopo fechado (gerard-knowledge-oriented-domain-objects:
 * "Relações entre vários objetos ficam em coordenadores de escopo
 * fechado") que resolve, a partir da chave de um papel — a mesma chave que
 * a interpretação linguística, a curadoria e Main.obterPapelIncognitaAtual()
 * já usam, ex.: "papel.parte1" — a chave de mensagem i18n que explica esse
 * papel conceitualmente.
 *
 * Implementa a decisão da usuária sobre o item 1 do levantamento de
 * pendências de 2026-08-11 (AG_EME): a explicação deve morar no objeto
 * rico (PapelQuantitativo/DescritorRepresentacaoPapel), não em texto solto
 * na camada de UI de Main.java. Esta classe NÃO inventa conhecimento novo —
 * só consulta o DescritorRepresentacaoPapel de cada papel já modelado pelas
 * fábricas existentes (PapelQuantitativo.parte1/parte2/todo e as
 * FabricaPapeis* do esquema Transformação de Medidas, Comparação de
 * Medidas, Composição de Transformações, Transformação de Relação e
 * Composição de Relações), que já eram objetos ricos completos, só não
 * estavam conectados à UI para esta finalidade.
 *
 * Duas chaves vivas em Main.java não têm fábrica própria porque são
 * sinônimos históricos de um papel que já tem fábrica — documentado em
 * cada entrada abaixo, nenhuma delas inventa um conceito novo.
 */
public final class CatalogoExplicacoesConceituaisPapel {

    /** Usada quando a chave do papel é desconhecida ou não tem explicação específica decidida. */
    public static final String CHAVE_EXPLICACAO_GENERICA = "explicacao.papel.generica";

    private static final Map<String, String> CHAVE_PAPEL_PARA_EXPLICACAO = construirMapa();

    private CatalogoExplicacoesConceituaisPapel() { }

    /**
     * @param chavePapel a mesma chave devolvida por
     *        Main.obterPapelIncognitaAtual() / PapelElementoInterpretado.getChavePapel()
     * @return a chave de mensagem i18n a passar para ServicoLocalizacao.texto(...);
     *         nunca null — devolve {@link #CHAVE_EXPLICACAO_GENERICA} para chave
     *         desconhecida ou nula.
     */
    public static String obterChaveExplicacao(String chavePapel) {
        if (chavePapel == null) {
            return CHAVE_EXPLICACAO_GENERICA;
        }
        String explicacao = CHAVE_PAPEL_PARA_EXPLICACAO.get(chavePapel.trim());
        return explicacao != null ? explicacao : CHAVE_EXPLICACAO_GENERICA;
    }

    private static Map<String, String> construirMapa() {
        Map<String, String> mapa = new LinkedHashMap<>();
        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;

        // Composição de Medidas: Todo = Parte1 + Parte2.
        registrar(mapa, PapelQuantitativo.parte1(nenhum));
        registrar(mapa, PapelQuantitativo.parte2(nenhum));
        registrar(mapa, PapelQuantitativo.todo(nenhum));

        // Transformação de Medidas: EstadoFinal = EstadoInicial + Transformacao.
        registrar(mapa, FabricaPapeisTransformacaoMedidas.estadoInicial(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoMedidas.transformacao(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoMedidas.estadoFinal(nenhum));

        // Comparação de Medidas: Referendo = Referido + ValorRelativo.
        registrar(mapa, FabricaPapeisComparacaoMedidas.referido(nenhum));
        registrar(mapa, FabricaPapeisComparacaoMedidas.referendo(nenhum));
        // A chave viva em Main.java para ValorRelativo é "papel.diferenca",
        // não "papel.valorRelativo" (ver Main.obterValorCuradoPorIndiceEChave
        // e o comentário em FabricaPapeisComparacaoMedidas.valorRelativo) —
        // registra sob a chave viva.
        mapa.put("papel.diferenca",
                FabricaPapeisComparacaoMedidas.valorRelativo(nenhum)
                        .descritorRepresentacao().getChaveExplicacaoConceitual());
        // "papel.referente" é sinônimo histórico de "papel.referendo" — mesmo
        // papel, nome alternativo vindo de outra via de interpretação (ver
        // CatalogoPapeisSemanticos.registrarNatural("papel.referente",
        // "Referendo") e ResolvedorIncognitaCurada.eh(t, "referendo", "referente")).
        mapa.put("papel.referente", mapa.get("papel.referendo"));

        // Composição de Transformações: TransformacaoFinal = Transformacao1 + Transformacao2.
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.transformacao1(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.transformacao2(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(nenhum));

        // Transformação de Relação: RelacaoFinal = RelacaoInicial + Transformacao.
        // transformacao() repete a chave "papel.transformacao" já registrada
        // acima (mesmo conceito, mesma chave de explicação) — reafirma, não
        // sobrescreve com sentido diferente.
        registrar(mapa, FabricaPapeisTransformacaoDeRelacao.relacaoInicial(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoDeRelacao.transformacao(nenhum));
        registrar(mapa, FabricaPapeisTransformacaoDeRelacao.relacaoFinal(nenhum));

        // Composição de Relações: RelacaoFinal = Relacao1 + Relacao2.
        // relacaoFinal() repete a chave "papel.relacaoFinal" já registrada
        // acima por Transformação de Relação — RelacaoFinal é o mesmo
        // conceito nos dois esquemas (resultado de uma operação entre
        // relações), mesma chave de explicação nos dois.
        registrar(mapa, FabricaPapeisComposicaoDeRelacoes.relacao1(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeRelacoes.relacao2(nenhum));
        registrar(mapa, FabricaPapeisComposicaoDeRelacoes.relacaoFinal(nenhum));

        return mapa;
    }

    private static void registrar(Map<String, String> mapa, PapelQuantitativo papel) {
        mapa.put(papel.getChave(), papel.descritorRepresentacao().getChaveExplicacaoConceitual());
    }
}
