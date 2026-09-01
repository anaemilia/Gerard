import gerard.campoaditivo.curadoria.ConversorSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.OperacaoAditiva;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prova da ponte rica de Transformação de Relação com orientação curada. */
public class TesteConversorTransformacaoRelacaoRica {

    public static void main(String[] args) {
        ConversorSituacaoProblemaRica conversor =
                new ConversorSituacaoProblemaRica();

        Cenario bonecas = cenarioBonecas(false);
        ResultadoConversaoSituacaoProblemaRica resultado = conversor.converter(
                registro("tr-bonecas", "+3", "5", "positivo", "+2",
                        "subtracao", "relacao_final"),
                bonecas.narrativa, bonecas.correspondencias);
        checar("transformação de relação orientada é construída",
                resultado.foiConstruida(), true);
        checar("orientações humana inicial e final são consistentes",
                resultado.ehValida(), true);
        SituacaoProblema situacao = resultado.getSituacaoOuFalhar();
        checar("categoria canônica é preservada",
                situacao.getEstrutura().getCategoria()
                        == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, true);
        checar("estrutura possui os três papéis relativos",
                situacao.getEstrutura().getPapeis().size() == 3, true);
        checar("estrutura possui uma relação orientada",
                situacao.getEstrutura().getRelacoes().size() == 1, true);
        checar("operação de modelagem não foi convertida em equação",
                situacao.getEstrutura().getCriteriosOperacao().size() == 1, true);
        checar("subtração curada permanece resposta correta independente",
                situacao.getEstrutura().getCriteriosOperacao().get(0)
                        .correspondeA(OperacaoAditiva.SUBTRACAO), true);

        Cenario empate = cenarioEmpate();
        ResultadoConversaoSituacaoProblemaRica zeroFinal = conversor.converter(
                registro("tr-zero-final", "+2", "2", "positivo", "0",
                        "subtracao", "relacao_final"),
                empate.narrativa, empate.correspondencias);
        checar("relação final nula é válida no contexto relativo",
                zeroFinal.ehValida(), true);

        ResultadoConversaoSituacaoProblemaRica transformacaoNula =
                conversor.converter(
                        registro("tr-transformacao-zero", "+2", "0", "neutro",
                                "+2", "soma", "relacao_final"),
                        empate.narrativa, empate.correspondencias);
        checar("evento de transformação nulo não é convertido",
                transformacaoNula.foiConstruida(), false);
        checar("restrição do papel operante fica explícita",
                transformacaoNula.possuiCodigo(
                        "conversao.sinal.ausente_ou_invalido"), true);

        ResultadoConversaoSituacaoProblemaRica semOperacao = conversor.converter(
                registro("tr-sem-operacao", "+3", "5", "positivo", "+2",
                        "", "relacao_final"),
                bonecas.narrativa, bonecas.correspondencias);
        checar("operação ausente impede a ponte completa",
                semOperacao.foiConstruida(), false);
        checar("ausência da operação é diagnosticada",
                semOperacao.possuiCodigo(
                        "conversao.operacao.ausente_ou_invalida"), true);

        Cenario orientacaoErrada = cenarioBonecas(true);
        ResultadoConversaoSituacaoProblemaRica invertida = conversor.converter(
                registro("tr-orientacao-errada", "+3", "5", "positivo", "+2",
                        "subtracao", "relacao_final"),
                orientacaoErrada.narrativa,
                orientacaoErrada.correspondencias);
        checar("orientação humana divergente não é corrigida silenciosamente",
                invertida.foiConstruida(), true);
        checar("declaração divergente permanece candidata inválida",
                invertida.ehValida(), false);

        System.out.println(
                "APROVADO: ponte rica de TRANSFORMACAO_RELACAO preserva orientação humana, resultado zero e operação independente.");
    }

    private static SituacaoProblemaAditiva registro(
            String id,
            String relacaoInicial,
            String transformacao,
            String sinalTransformacao,
            String relacaoFinal,
            String operacao,
            String termoDesconhecido) {
        return new SituacaoProblemaAditiva(
                id, id, "original", "", true,
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, "pt-BR",
                "Relações e transformação explicitamente curadas.",
                "Bonecas", "teste", "",
                relacaoInicial, transformacao, sinalTransformacao, relacaoFinal,
                "", "", "",
                "", "", "", "",
                termoDesconhecido, "TRANSFORMACAO_RELACAO", "",
                "Bonecas", "Maria", "Julia",
                "", "", "", "", "", "",
                operacao, "", "");
    }

    private static Cenario cenarioBonecas(boolean manterOrientacaoFinal) {
        FamiliaObjeto bonecas = new FamiliaObjeto(
                "familia.bonecas", "Bonecas");
        ObjetoContado boneca = new ObjetoContado(
                "objeto.boneca", bonecas,
                Collections.<String, String>emptyMap(), "visual.boneca");
        ParticipanteNarrativo julia = new ParticipanteNarrativo(
                "participante.julia", "Julia");
        ParticipanteNarrativo maria = new ParticipanteNarrativo(
                "participante.maria", "Maria");
        String evento = "evento.compra.maria";

        EstadoNarrativo inicial = estado(
                0, "tempo.inicial", julia, inventario(boneca, 8),
                maria, inventario(boneca, 5));
        EventoNarrativoCurado compra = EventoNarrativoCurado.acrescimo(
                new MarcadorTemporal(1, evento),
                maria, boneca, new NumeroNatural(5));
        EstadoNarrativo finalDeclarado = estado(
                2, "tempo.final", julia, inventario(boneca, 8),
                maria, inventario(boneca, 10));
        NarrativaCurada narrativa = new NarrativaCurada(
                "Maria recebe cinco bonecas.", inicial,
                Collections.singletonList(compra), finalDeclarado);

        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        correspondencias.add(correspondencia(
                "papel.relacaoInicial",
                ReferenciaValorNarrativo.diferencaEntreQuantidadesIniciais(
                        julia, maria, bonecas)));
        correspondencias.add(correspondencia(
                "papel.transformacao",
                ReferenciaValorNarrativo.variacaoDeEvento(
                        evento, maria, bonecas)));
        correspondencias.add(correspondencia(
                "papel.relacaoFinal",
                manterOrientacaoFinal
                        ? ReferenciaValorNarrativo.diferencaEntreQuantidadesFinais(
                                julia, maria, bonecas)
                        : ReferenciaValorNarrativo.diferencaEntreQuantidadesFinais(
                                maria, julia, bonecas)));
        return new Cenario(narrativa, correspondencias);
    }

    private static Cenario cenarioEmpate() {
        FamiliaObjeto bilas = new FamiliaObjeto("familia.bilas", "Bilas");
        ObjetoContado bila = new ObjetoContado(
                "objeto.bila", bilas,
                Collections.<String, String>emptyMap(), "visual.bila");
        ParticipanteNarrativo ana = new ParticipanteNarrativo(
                "participante.ana", "Ana");
        ParticipanteNarrativo bia = new ParticipanteNarrativo(
                "participante.bia", "Bia");
        String evento = "evento.ganho.bia";
        EstadoNarrativo inicial = estado(
                0, "tempo.inicial", ana, inventario(bila, 7),
                bia, inventario(bila, 5));
        EventoNarrativoCurado ganho = EventoNarrativoCurado.acrescimo(
                new MarcadorTemporal(1, evento),
                bia, bila, new NumeroNatural(2));
        EstadoNarrativo finalDeclarado = estado(
                2, "tempo.final", ana, inventario(bila, 7),
                bia, inventario(bila, 7));
        NarrativaCurada narrativa = new NarrativaCurada(
                "Bia iguala a quantidade de Ana.", inicial,
                Collections.singletonList(ganho), finalDeclarado);
        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        correspondencias.add(correspondencia(
                "papel.relacaoInicial",
                ReferenciaValorNarrativo.diferencaEntreQuantidadesIniciais(
                        ana, bia, bilas)));
        correspondencias.add(correspondencia(
                "papel.transformacao",
                ReferenciaValorNarrativo.variacaoDeEvento(
                        evento, bia, bilas)));
        correspondencias.add(correspondencia(
                "papel.relacaoFinal",
                ReferenciaValorNarrativo.diferencaEntreQuantidadesFinais(
                        ana, bia, bilas)));
        return new Cenario(narrativa, correspondencias);
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

    private static EstadoNarrativo estado(
            int ordem,
            String chave,
            ParticipanteNarrativo primeiro,
            InventarioNarrativo inventarioPrimeiro,
            ParticipanteNarrativo segundo,
            InventarioNarrativo inventarioSegundo) {
        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        inventarios.put(primeiro, inventarioPrimeiro);
        inventarios.put(segundo, inventarioSegundo);
        return new EstadoNarrativo(
                new MarcadorTemporal(ordem, chave), inventarios);
    }

    private static void checar(String rotulo, boolean obtido, boolean esperado) {
        if (obtido != esperado) {
            throw new AssertionError(
                    rotulo + ": esperado [" + esperado
                            + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
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
