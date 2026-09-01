import gerard.campoaditivo.curadoria.ConversorSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
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
import gerard.idioma.IdiomaInterface;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prova executável da sexta ponte rica: Composição de Relações. */
public class TesteConversorComposicaoRelacoesRica {

    public static void main(String[] args) {
        ConversorSituacaoProblemaRica conversor =
                new ConversorSituacaoProblemaRica();

        Cenario encadeado = cenario(10, 6, 4, Configuracao.ENCADEADA, false);
        ResultadoConversaoSituacaoProblemaRica soma = conversor.converter(
                registro("cr-soma", "+4", "+2", "+6", "soma"),
                encadeado.narrativa, encadeado.correspondencias);
        checar("composição encadeada é construída", soma.foiConstruida(), true);
        checar("composição encadeada por soma é consistente", soma.ehValida(), true);
        SituacaoProblema situacao = soma.getSituacaoOuFalhar();
        checar("sexta categoria canônica é preservada",
                situacao.getEstrutura().getCategoria()
                        == TipoSituacaoAditiva.COMPOSICAO_RELACOES, true);
        checar("estrutura possui três papéis relativos",
                situacao.getEstrutura().getPapeis().size() == 3, true);
        checar("operação curada integra a relação estrutural",
                situacao.getEstrutura().getRelacoes().get(0)
                        .descreverRelacao().contains("+"), true);

        Cenario referenciaComum = cenario(
                10, 7, 4, Configuracao.REFERENCIA_COMUM, false);
        ResultadoConversaoSituacaoProblemaRica subtracao = conversor.converter(
                registro("cr-subtracao", "+6", "+3", "+3", "subtracao"),
                referenciaComum.narrativa, referenciaComum.correspondencias);
        checar("composição com referência comum é construída",
                subtracao.foiConstruida(), true);
        checar("composição com referência comum usa subtração curada",
                subtracao.ehValida(), true);
        checar("subtração permanece na relação estrutural",
                subtracao.getSituacaoOuFalhar().getEstrutura().getRelacoes()
                        .get(0).descreverRelacao().contains("-"), true);

        Cenario empate = cenario(10, 6, 10, Configuracao.ENCADEADA, false);
        ResultadoConversaoSituacaoProblemaRica zero = conversor.converter(
                registro("cr-zero", "+4", "-4", "0", "soma"),
                empate.narrativa, empate.correspondencias);
        checar("relações opostas podem totalizar zero", zero.ehValida(), true);
        checar("os três papéis pertencem aos inteiros e aceitam zero",
                zero.getSituacaoOuFalhar().getEstrutura()
                        .papel("papel.relacaoFinal").valorAtual()
                        .valorOuNull().intValue() == 0, true);

        ResultadoConversaoSituacaoProblemaRica semOperacao = conversor.converter(
                registro("cr-sem-operacao", "+4", "+2", "+6", ""),
                encadeado.narrativa, encadeado.correspondencias);
        checar("operação ausente impede a ponte completa",
                semOperacao.foiConstruida(), false);
        checar("ausência da operação é diagnosticada",
                semOperacao.possuiCodigo(
                        "conversao.operacao.ausente_ou_invalida"), true);

        Cenario finalInvertida = cenario(
                10, 6, 4, Configuracao.ENCADEADA, true);
        ResultadoConversaoSituacaoProblemaRica divergente = conversor.converter(
                registro("cr-orientacao-divergente", "+4", "+2", "+6", "soma"),
                finalInvertida.narrativa, finalInvertida.correspondencias);
        checar("orientação humana divergente não é corrigida silenciosamente",
                divergente.foiConstruida(), true);
        checar("orientação divergente permanece candidata inválida",
                divergente.ehValida(), false);
        checar("divergência narrativa fica registrada",
                divergente.possuiCodigo(
                        "situacao.correspondencia.valor_divergente"), true);

        System.out.println(
                "APROVADO: ponte rica de COMPOSICAO_RELACOES preserva soma, subtração, zero e orientações nominais.");
    }

    private static SituacaoProblemaAditiva registro(
            String id,
            String relacao1,
            String relacao2,
            String relacaoFinal,
            String operacao) {
        return new SituacaoProblemaAditiva(
                id, id, "original", "", true,
                TipoSituacaoAditiva.COMPOSICAO_RELACOES, "pt-BR",
                "Três relações explicitamente curadas.",
                "Bilas", "teste", "",
                "", "", "", "",
                relacao1, relacao2, relacaoFinal,
                "", "", "", "",
                "relacao_resultante", "COMPOSICAO_RELACOES", "",
                "não inferir A", "não inferir B", "não inferir C",
                "", "", "", "", "", "",
                operacao, "", "");
    }

    private static Cenario cenario(
            int quantidadeA,
            int quantidadeB,
            int quantidadeC,
            Configuracao configuracao,
            boolean inverterRelacaoFinal) {
        FamiliaObjeto bilas = new FamiliaObjeto("familia.bilas", "Bilas");
        ObjetoContado bila = new ObjetoContado(
                "objeto.bila", bilas,
                Collections.<String, String>emptyMap(), "visual.bila");
        ParticipanteNarrativo a = new ParticipanteNarrativo(
                "participante.a", "Ana");
        ParticipanteNarrativo b = new ParticipanteNarrativo(
                "participante.b", "Bia");
        ParticipanteNarrativo c = new ParticipanteNarrativo(
                "participante.c", "Caio");

        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        inventarios.put(a, inventario(bila, quantidadeA));
        inventarios.put(b, inventario(bila, quantidadeB));
        inventarios.put(c, inventario(bila, quantidadeC));
        EstadoNarrativo inicial = new EstadoNarrativo(
                new MarcadorTemporal(0, "tempo.relacoes"), inventarios);
        EstadoNarrativo finalDeclarado = new EstadoNarrativo(
                new MarcadorTemporal(1, "tempo.relacoes.final"), inventarios);
        NarrativaCurada narrativa = new NarrativaCurada(
                "Relações estáticas nominalmente curadas.",
                inicial, Collections.emptyList(), finalDeclarado);

        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        if (configuracao == Configuracao.ENCADEADA) {
            correspondencias.add(correspondencia(
                    "papel.relacao1", diferenca(a, b, bilas)));
            correspondencias.add(correspondencia(
                    "papel.relacao2", diferenca(b, c, bilas)));
            correspondencias.add(correspondencia(
                    "papel.relacaoFinal",
                    inverterRelacaoFinal
                            ? diferenca(c, a, bilas)
                            : diferenca(a, c, bilas)));
        } else {
            correspondencias.add(correspondencia(
                    "papel.relacao1", diferenca(a, c, bilas)));
            correspondencias.add(correspondencia(
                    "papel.relacao2", diferenca(b, c, bilas)));
            correspondencias.add(correspondencia(
                    "papel.relacaoFinal",
                    inverterRelacaoFinal
                            ? diferenca(b, a, bilas)
                            : diferenca(a, b, bilas)));
        }
        return new Cenario(narrativa, correspondencias);
    }

    private static ReferenciaValorNarrativo diferenca(
            ParticipanteNarrativo primeiro,
            ParticipanteNarrativo segundo,
            FamiliaObjeto familia) {
        return ReferenciaValorNarrativo.diferencaEntreQuantidadesIniciais(
                primeiro, segundo, familia);
    }

    private static CorrespondenciaPapelNarrativa correspondencia(
            String papel,
            ReferenciaValorNarrativo referencia) {
        return new CorrespondenciaPapelNarrativa(papel, referencia);
    }

    private static InventarioNarrativo inventario(
            ObjetoContado objeto,
            int quantidade) {
        Map<ObjetoContado, NumeroNatural> itens = new LinkedHashMap<>();
        itens.put(objeto, new NumeroNatural(quantidade));
        return new InventarioNarrativo(itens);
    }

    private static void checar(String rotulo, boolean obtido, boolean esperado) {
        if (obtido != esperado) {
            throw new AssertionError(
                    rotulo + ": esperado [" + esperado
                            + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }

    private enum Configuracao {
        ENCADEADA,
        REFERENCIA_COMUM
    }

    private static final class Cenario {
        private final NarrativaCurada narrativa;
        private final List<CorrespondenciaPapelNarrativa> correspondencias;

        private Cenario(
                NarrativaCurada narrativa,
                List<CorrespondenciaPapelNarrativa> correspondencias) {
            this.narrativa = narrativa;
            this.correspondencias = correspondencias;
        }
    }
}
