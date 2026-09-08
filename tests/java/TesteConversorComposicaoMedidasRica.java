import gerard.campoaditivo.curadoria.ConversorSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.aplicacao.ResolvedorValorEsperadoIncognita;
import gerard.aplicacao.ServicoAvaliacaoAcaoIncognita;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.EstadoNarrativo;
import gerard.dominio.campoaditivo.situacao.FamiliaObjeto;
import gerard.dominio.campoaditivo.situacao.InventarioNarrativo;
import gerard.dominio.campoaditivo.situacao.MarcadorTemporal;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.ObjetoContado;
import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prova da conversao explicita e estatica de composicao de medidas. */
public class TesteConversorComposicaoMedidasRica {

    public static void main(String[] args) {
        Complemento complemento = complementoNarrativo();
        ConversorSituacaoProblemaRica conversor =
                new ConversorSituacaoProblemaRica();

        ResultadoConversaoSituacaoProblemaRica completa = conversor.converter(
                registro("2", "3", "5"),
                complemento.narrativa,
                complemento.correspondencias);
        checar("composicao explicitamente completa e construida",
                completa.foiConstruida(), true);
        checar("estrutura e narrativa curadas sao consistentes",
                completa.ehValida(), true);

        SituacaoProblema situacao = completa.getSituacaoOuFalhar();
        checar("categoria canonica e preservada",
                situacao.getEstrutura().getCategoria()
                        == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                true);
        checar("composicao possui tres papeis",
                situacao.getEstrutura().getPapeis().size() == 3, true);
        checar("composicao possui uma relacao estrutural",
                situacao.getEstrutura().getRelacoes().size() == 1, true);
        checar("conversao permanece candidata ate revisao humana",
                "CANDIDATA_NAO_CURADA".equals(
                        situacao.getStatusCuradoria().name()), true);
        checar("composicao estatica nao inventa eventos",
                situacao.criarSequenciaNarrativa().getEventos().isEmpty(), true);
        SemanticaCuradaSituacao.PapelCurado todoCurado =
                SemanticaCuradaSituacao.buscar(
                        registro("2", "3", "5"), null, "papel.todo");
        checar("papel curado reconhece valor preservado",
                Boolean.FALSE.equals(todoCurado.estadoModificadoPor("5")), true);
        checar("papel curado reconhece valor modificado",
                Boolean.TRUE.equals(todoCurado.estadoModificadoPor("6")), true);
        checar("papel curado nao inventa comparacao sem valor numerico",
                todoCurado.estadoModificadoPor("?") == null, true);
        checar("consulta portatil reconhece valor preservado",
                Boolean.FALSE.equals(SemanticaCuradaSituacao.estadoModificadoPor(
                        registro("2", "3", "5"), null,
                        "papel.todo", "5")), true);
        checar("consulta portatil reconhece valor modificado",
                Boolean.TRUE.equals(SemanticaCuradaSituacao.estadoModificadoPor(
                        registro("2", "3", "5"), null,
                        "papel.todo", "6")), true);
        checar("consulta portatil nao inventa ausencia de papel",
                SemanticaCuradaSituacao.estadoModificadoPor(
                        registro("2", "3", "5"), null,
                        "papel.inexistente", "5") == null, true);
        ResolvedorValorEsperadoIncognita resolvedorValor =
                new ResolvedorValorEsperadoIncognita();
        checar("valor esperado usa curadoria como fallback",
                Integer.valueOf(5).equals(resolvedorValor.resolver(
                        registro("2", "3", "5"), null, "papel.todo",
                        "papel.todo", null, -1)), true);
        EstadoSemanticoCompartilhado estadoVivo =
                new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot snapshotVivo = estadoVivo.atualizar(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[] { Integer.valueOf(2), Integer.valueOf(4), null },
                new boolean[] { true, true, false }, 0,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        checar("valor esperado prioriza estado vivo recalculado",
                Integer.valueOf(6).equals(resolvedorValor.resolver(
                        registro("2", "3", "5"), null, "papel.todo",
                        "papel.todo", snapshotVivo, 2)), true);
        IncognitaQuantitativa incognita = new IncognitaQuantitativa(
                "papel.todo", TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                PapelQuantitativo.todo(PublicadorEventoDominio.NENHUM));
        ServicoAvaliacaoAcaoIncognita avaliadorCorrespondencia =
                new ServicoAvaliacaoAcaoIncognita();
        checar("incognita compara texto com fallback curado",
                Boolean.TRUE.equals(avaliadorCorrespondencia.correspondeAoEsperado(
                        incognita, registro("2", "3", "5"), null,
                        "papel.todo", null, -1, "5")), true);
        checar("incognita rejeita divergencia do fallback curado",
                Boolean.FALSE.equals(avaliadorCorrespondencia.correspondeAoEsperado(
                        incognita, registro("2", "3", "5"), null,
                        "papel.todo", null, -1, "6")), true);
        checar("incognita compara texto com estado vivo prioritario",
                Boolean.TRUE.equals(avaliadorCorrespondencia.correspondeAoEsperado(
                        incognita, registro("2", "3", "5"), null,
                        "papel.todo", snapshotVivo, 2, "6")), true);
        checar("incognita nao inventa avaliacao para texto nao numerico",
                avaliadorCorrespondencia.correspondeAoEsperado(
                        incognita, registro("2", "3", "5"), null,
                        "papel.todo", snapshotVivo, 2, "?") == null, true);
        checar("parte 1 referencia a variante declarada pelo humano",
                complemento.correspondencias.get(0).getReferencia().getObjeto()
                        .equals(complemento.rosaBranca),
                true);

        List<CorrespondenciaPapelNarrativa> divergentes = new ArrayList<>(
                complemento.correspondencias);
        divergentes.set(0, correspondencia(
                "papel.parte1",
                ReferenciaValorNarrativo.quantidadeInicialDoObjeto(
                        complemento.vovo, complemento.rosaAmarela)));
        ResultadoConversaoSituacaoProblemaRica divergente = conversor.converter(
                registro("2", "3", "5"),
                complemento.narrativa,
                divergentes);
        checar("mapeamento humano divergente nao e corrigido silenciosamente",
                divergente.foiConstruida(), true);
        checar("mapeamento humano divergente recebe diagnostico factual",
                divergente.possuiCodigo(
                        "situacao.correspondencia.valor_divergente"), true);

        ResultadoConversaoSituacaoProblemaRica negativa = conversor.converter(
                registro("-2", "3", "1"),
                complemento.narrativa,
                complemento.correspondencias);
        checar("parte negativa nao constroi composicao de medidas",
                negativa.foiConstruida(), false);
        checar("dominio natural rejeitado fica explicito",
                negativa.possuiCodigo("conversao.campo.nao_natural"), true);

        System.out.println(
                "APROVADO: conversor rico de composicao de medidas.");
    }

    private static SituacaoProblemaAditiva registro(
            String parte1, String parte2, String todo) {
        return new SituacaoProblemaAditiva(
                "PO_COMPOSICAO_MEDIDAS_rosas_001",
                "SP_CURADA_001", "original", "", true,
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "pt-BR",
                "Vovo possui duas rosas brancas e tres rosas amarelas.",
                "Rosas", "curadoria", "",
                "", "", "", "",
                parte1, parte2, todo,
                "", "", "", "",
                "todo",
                "COMPOSICAO_MEDIDAS", "",
                "Rosas brancas", "Rosas amarelas", "Vovo",
                "", "", "", "", "", "",
                "", "", "");
    }

    private static Complemento complementoNarrativo() {
        FamiliaObjeto rosas = new FamiliaObjeto("familia.rosas", "Rosas");
        ObjetoContado rosaBranca = objeto(
                "objeto.rosa.branca", rosas, "branca", "visual.rosa.branca");
        ObjetoContado rosaAmarela = objeto(
                "objeto.rosa.amarela", rosas, "amarela", "visual.rosa.amarela");
        ParticipanteNarrativo vovo = new ParticipanteNarrativo(
                "participante.vovo", "Vovo");

        InventarioNarrativo inventario = inventario(
                rosaBranca, 2, rosaAmarela, 3);
        EstadoNarrativo inicial = new EstadoNarrativo(
                new MarcadorTemporal(0, "tempo.composicao"),
                inventarios(vovo, inventario));
        EstadoNarrativo finalDeclarado = new EstadoNarrativo(
                new MarcadorTemporal(1, "tempo.composicao.declarado"),
                inventarios(vovo, inventario));
        NarrativaCurada narrativa = new NarrativaCurada(
                "Duas variantes de uma familia, explicitamente curadas.",
                inicial,
                Collections.emptyList(),
                finalDeclarado);

        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        correspondencias.add(correspondencia(
                "papel.parte1",
                ReferenciaValorNarrativo.quantidadeInicialDoObjeto(
                        vovo, rosaBranca)));
        correspondencias.add(correspondencia(
                "papel.parte2",
                ReferenciaValorNarrativo.quantidadeInicialDoObjeto(
                        vovo, rosaAmarela)));
        correspondencias.add(correspondencia(
                "papel.todo",
                ReferenciaValorNarrativo.quantidadeInicial(vovo, rosas)));
        return new Complemento(
                narrativa, correspondencias, vovo, rosaBranca, rosaAmarela);
    }

    private static ObjetoContado objeto(
            String id,
            FamiliaObjeto familia,
            String cor,
            String chaveVisual) {
        Map<String, String> caracteristicas = new LinkedHashMap<>();
        caracteristicas.put("cor", cor);
        return new ObjetoContado(id, familia, caracteristicas, chaveVisual);
    }

    private static CorrespondenciaPapelNarrativa correspondencia(
            String papel,
            ReferenciaValorNarrativo referencia) {
        return new CorrespondenciaPapelNarrativa(papel, referencia);
    }

    private static InventarioNarrativo inventario(
            ObjetoContado objeto1,
            int quantidade1,
            ObjetoContado objeto2,
            int quantidade2) {
        Map<ObjetoContado, NumeroNatural> itens = new LinkedHashMap<>();
        itens.put(objeto1, new NumeroNatural(quantidade1));
        itens.put(objeto2, new NumeroNatural(quantidade2));
        return new InventarioNarrativo(itens);
    }

    private static Map<ParticipanteNarrativo, InventarioNarrativo> inventarios(
            ParticipanteNarrativo participante,
            InventarioNarrativo inventario) {
        Map<ParticipanteNarrativo, InventarioNarrativo> mapa =
                new LinkedHashMap<>();
        mapa.put(participante, inventario);
        return mapa;
    }

    private static void checar(String rotulo, boolean obtido, boolean esperado) {
        if (obtido != esperado) {
            throw new AssertionError(
                    rotulo + ": esperado [" + esperado
                            + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }

    private static final class Complemento {
        private final NarrativaCurada narrativa;
        private final List<CorrespondenciaPapelNarrativa> correspondencias;
        private final ParticipanteNarrativo vovo;
        private final ObjetoContado rosaBranca;
        private final ObjetoContado rosaAmarela;

        private Complemento(
                NarrativaCurada narrativa,
                List<CorrespondenciaPapelNarrativa> correspondencias,
                ParticipanteNarrativo vovo,
                ObjetoContado rosaBranca,
                ObjetoContado rosaAmarela) {
            this.narrativa = narrativa;
            this.correspondencias = correspondencias;
            this.vovo = vovo;
            this.rosaBranca = rosaBranca;
            this.rosaAmarela = rosaAmarela;
        }
    }
}
