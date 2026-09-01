import gerard.campoaditivo.curadoria.ConversorSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.EstadoNarrativo;
import gerard.dominio.campoaditivo.situacao.EventoNarrativoCurado;
import gerard.dominio.campoaditivo.situacao.FamiliaObjeto;
import gerard.dominio.campoaditivo.situacao.InventarioNarrativo;
import gerard.dominio.campoaditivo.situacao.MarcadorTemporal;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.ObjetoContado;
import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prova executável da ponte explícita entre curadoria tabular e agregado rico. */
public class TesteConversorSituacaoProblemaRica {

    public static void main(String[] args) {
        Complemento complemento = complementoNarrativo();
        ConversorSituacaoProblemaRica conversor =
                new ConversorSituacaoProblemaRica();

        ResultadoConversaoSituacaoProblemaRica incompleta = conversor.converter(
                registro("", "", "", "-1", "estado_final"),
                complemento.narrativa,
                complemento.correspondencias);
        checar("registro antigo incompleto não é convertido",
                incompleta.foiConstruida(), false);
        checar("estado intermediário ausente fica explícito",
                incompleta.possuiCodigo("conversao.campo.ausente"), true);
        checar("operação ausente fica explícita",
                incompleta.possuiCodigo(
                        "conversao.operacao.ausente_ou_invalida"), true);

        ResultadoConversaoSituacaoProblemaRica completa = conversor.converter(
                registro("4", "soma", "soma", "-1", "estado_final"),
                complemento.narrativa,
                complemento.correspondencias);
        checar("registro explicitamente completo é construído",
                completa.foiConstruida(), true);
        checar("registro completo e narrativa explícita são consistentes",
                completa.ehValida(), true);
        SituacaoProblema situacao = completa.getSituacaoOuFalhar();
        checar("estrutura convertida contém seis papéis",
                situacao.getEstrutura().getPapeis().size() == 6, true);
        checar("estrutura convertida preserva quatro relações locais",
                situacao.getEstrutura().getRelacoes().size() == 4, true);
        checar("a conversão continua candidata até revisão humana",
                "CANDIDATA_NAO_CURADA".equals(
                        situacao.getStatusCuradoria().name()), true);

        ResultadoConversaoSituacaoProblemaRica conflito = conversor.converter(
                registro("4", "soma", "soma", "?", "estado_final"),
                complemento.narrativa,
                complemento.correspondencias);
        checar("divergência entre termo e símbolo não é harmonizada",
                conflito.possuiCodigo(
                        "conversao.incognita.inconsistente"), true);
        checar("registro com incógnita conflitante não é construído",
                conflito.foiConstruida(), false);

        ResultadoConversaoSituacaoProblemaRica componenteNulo = conversor.converter(
                registro("5", "soma", "soma", "0", "estado_final"),
                complemento.narrativa,
                complemento.correspondencias);
        checar("transformação componente nula não é convertida",
                componenteNulo.foiConstruida(), false);
        checar("domínio não nulo da transformação fica explícito",
                componenteNulo.possuiCodigo(
                        "conversao.campo.fora_do_dominio"), true);

        System.out.println(
                "APROVADO: conversor explícito da situação-problema rica.");
    }

    private static SituacaoProblemaAditiva registro(
            String estadoIntermediario,
            String operacaoTransformacoes,
            String operacaoEstadoTransformacao,
            String transformacao1,
            String termoDesconhecido) {
        return new SituacaoProblemaAditiva(
                "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114",
                "SP_LEGADO_0027", "original", "", true,
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, "pt-BR",
                "Vovó tinha cinco rosas e entregou uma rosa duas vezes.",
                "Flores", "pdf", "",
                "5", "", "", "3",
                transformacao1, "-1", "-2",
                "", "", "", "",
                termoDesconhecido,
                "COMPOSICAO_TRANSFORMACOES", "",
                "Rosas", "Netinha", "Vovó",
                "", "", "", "", "", "",
                operacaoTransformacoes,
                estadoIntermediario,
                operacaoEstadoTransformacao);
    }

    private static Complemento complementoNarrativo() {
        FamiliaObjeto rosas = new FamiliaObjeto("familia.rosas", "Rosas");
        ObjetoContado rosa = new ObjetoContado(
                "objeto.rosa", rosas,
                Collections.<String, String>emptyMap(), "visual.rosa");
        ParticipanteNarrativo vovo = new ParticipanteNarrativo(
                "participante.vovo", "Vovó");
        ParticipanteNarrativo netinha = new ParticipanteNarrativo(
                "participante.netinha", "Netinha");

        EstadoNarrativo inicial = new EstadoNarrativo(
                new MarcadorTemporal(0, "tempo.inicial"),
                inventarios(vovo, inventario(rosa, 5),
                        netinha, InventarioNarrativo.vazio()));
        EventoNarrativoCurado evento1 = EventoNarrativoCurado.transferencia(
                new MarcadorTemporal(1, "evento.entrega.1"),
                vovo, netinha, rosa, new NumeroNatural(1));
        EventoNarrativoCurado evento2 = EventoNarrativoCurado.transferencia(
                new MarcadorTemporal(2, "evento.entrega.2"),
                vovo, netinha, rosa, new NumeroNatural(1));
        EstadoNarrativo finalDeclarado = new EstadoNarrativo(
                new MarcadorTemporal(3, "tempo.final"),
                inventarios(vovo, inventario(rosa, 3),
                        netinha, inventario(rosa, 2)));
        NarrativaCurada narrativa = new NarrativaCurada(
                "Duas entregas explicitamente curadas.", inicial,
                Arrays.asList(evento1, evento2), finalDeclarado);

        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        correspondencias.add(correspondencia(
                "papel.estadoInicial",
                ReferenciaValorNarrativo.quantidadeInicial(vovo, rosas)));
        correspondencias.add(correspondencia(
                "papel.transformacao1",
                ReferenciaValorNarrativo.variacaoDeEvento(
                        "evento.entrega.1", vovo, rosas)));
        correspondencias.add(correspondencia(
                "papel.estadoIntermediario",
                ReferenciaValorNarrativo.quantidadeAposEvento(
                        "evento.entrega.1", vovo, rosas)));
        correspondencias.add(correspondencia(
                "papel.transformacao2",
                ReferenciaValorNarrativo.variacaoDeEvento(
                        "evento.entrega.2", vovo, rosas)));
        correspondencias.add(correspondencia(
                "papel.transformacaoFinal",
                ReferenciaValorNarrativo.variacaoTotal(vovo, rosas)));
        correspondencias.add(correspondencia(
                "papel.estadoFinal",
                ReferenciaValorNarrativo.quantidadeFinalCalculada(vovo, rosas)));
        return new Complemento(narrativa, correspondencias);
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

    private static Map<ParticipanteNarrativo, InventarioNarrativo> inventarios(
            ParticipanteNarrativo participante1,
            InventarioNarrativo inventario1,
            ParticipanteNarrativo participante2,
            InventarioNarrativo inventario2) {
        Map<ParticipanteNarrativo, InventarioNarrativo> mapa =
                new LinkedHashMap<>();
        mapa.put(participante1, inventario1);
        mapa.put(participante2, inventario2);
        return mapa;
    }

    private static void checar(String rotulo, boolean obtido, boolean esperado) {
        if (obtido != esperado) {
            throw new AssertionError(
                    rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }

    private static final class Complemento {
        private final NarrativaCurada narrativa;
        private final List<CorrespondenciaPapelNarrativa> correspondencias;

        private Complemento(
                NarrativaCurada narrativa,
                List<CorrespondenciaPapelNarrativa> correspondencias) {
            this.narrativa = narrativa;
            this.correspondencias = correspondencias;
        }
    }
}
