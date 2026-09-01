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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prova da conversao explicita de transformacao de medidas. */
public class TesteConversorTransformacaoMedidasRica {

    public static void main(String[] args) {
        ConversorSituacaoProblemaRica conversor =
                new ConversorSituacaoProblemaRica();

        Cenario acrescimo = cenarioAcrescimo();
        ResultadoConversaoSituacaoProblemaRica positiva = conversor.converter(
                registro("32", "22", "positivo", "54", "estado_final"),
                acrescimo.narrativa,
                acrescimo.correspondencias);
        checar("transformacao positiva explicitamente curada e construida",
                positiva.foiConstruida(), true);
        checar("transformacao positiva e consistente",
                positiva.ehValida(), true);
        verificarEstrutura(situacaoConstruida(positiva));

        Cenario reducao = cenarioReducao();
        ResultadoConversaoSituacaoProblemaRica negativa = conversor.converter(
                registro("10", "-3", "negativo", "7", "transformacao"),
                reducao.narrativa,
                reducao.correspondencias);
        checar("transformacao negativa explicitamente curada e construida",
                negativa.foiConstruida(), true);
        checar("transformacao negativa e consistente",
                negativa.ehValida(), true);
        checar("sinal negativo curado e preservado",
                negativa.getSituacaoOuFalhar().getEstrutura()
                        .papel("papel.transformacao")
                        .valorAtual().valorOuNull().intValue() == -3,
                true);

        ResultadoConversaoSituacaoProblemaRica semSinal = conversor.converter(
                registro("32", "22", "", "54", "estado_final"),
                acrescimo.narrativa,
                acrescimo.correspondencias);
        checar("magnitude positiva sem sinal curado nao e convertida",
                semSinal.foiConstruida(), false);
        checar("ausencia de sinal recebe diagnostico factual",
                semSinal.possuiCodigo(
                        "conversao.sinal.ausente_ou_invalido"), true);

        ResultadoConversaoSituacaoProblemaRica nula = conversor.converter(
                registro("10", "0", "neutro", "10", "estado_final"),
                reducao.narrativa,
                reducao.correspondencias);
        checar("transformacao direta nula nao e convertida",
                nula.foiConstruida(), false);
        checar("transformacao direta nula recebe diagnostico factual",
                nula.possuiCodigo(
                        "conversao.sinal.ausente_ou_invalido"), true);

        ResultadoConversaoSituacaoProblemaRica divergente = conversor.converter(
                registro("32", "21", "positivo", "53", "estado_final"),
                acrescimo.narrativa,
                acrescimo.correspondencias);
        checar("declaracao divergente ainda preserva o agregado candidato",
                divergente.foiConstruida(), true);
        checar("declaracao divergente nao e corrigida silenciosamente",
                divergente.possuiCodigo(
                        "situacao.correspondencia.valor_divergente"), true);

        ResultadoConversaoSituacaoProblemaRica estadoNegativo = conversor.converter(
                registro("3", "5", "negativo", "-2", "estado_final"),
                reducao.narrativa,
                reducao.correspondencias);
        checar("estado final negativo nao constroi transformacao de medidas",
                estadoNegativo.foiConstruida(), false);
        checar("dominio natural rejeitado fica explicito",
                estadoNegativo.possuiCodigo(
                        "conversao.campo.nao_natural"), true);

        System.out.println(
                "APROVADO: conversor rico de transformacao de medidas.");
    }

    private static SituacaoProblema situacaoConstruida(
            ResultadoConversaoSituacaoProblemaRica resultado) {
        return resultado.getSituacaoOuFalhar();
    }

    private static void verificarEstrutura(SituacaoProblema situacao) {
        checar("categoria canonica e preservada",
                situacao.getEstrutura().getCategoria()
                        == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                true);
        checar("transformacao de medidas possui tres papeis",
                situacao.getEstrutura().getPapeis().size() == 3, true);
        checar("transformacao de medidas possui uma relacao estrutural",
                situacao.getEstrutura().getRelacoes().size() == 1, true);
        checar("narrativa preserva o unico evento explicitamente curado",
                situacao.criarSequenciaNarrativa().getEventos().size() == 1,
                true);
        checar("conversao permanece candidata ate revisao humana",
                "CANDIDATA_NAO_CURADA".equals(
                        situacao.getStatusCuradoria().name()), true);
    }

    private static SituacaoProblemaAditiva registro(
            String estadoInicial,
            String transformacao,
            String sinalTransformacao,
            String estadoFinal,
            String termoDesconhecido) {
        return new SituacaoProblemaAditiva(
                "PO_TRANSFORMACAO_MEDIDAS_teste_001",
                "SP_CURADA_TRANSFORMACAO_001", "original", "", true,
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "pt-BR",
                "Situacao com uma transformacao explicitamente curada.",
                "Objetos", "curadoria", "",
                estadoInicial, transformacao, sinalTransformacao, estadoFinal,
                "", "", "",
                "", "", "", "",
                termoDesconhecido,
                "TRANSFORMACAO_MEDIDAS", "",
                "Objetos", "Objetos", "Participante",
                "", "", "", "", "", "",
                "", "", "");
    }

    private static Cenario cenarioAcrescimo() {
        FamiliaObjeto figurinhas = new FamiliaObjeto(
                "familia.figurinhas", "Figurinhas");
        ObjetoContado figurinha = new ObjetoContado(
                "objeto.figurinha", figurinhas,
                Collections.<String, String>emptyMap(), "visual.figurinha");
        ParticipanteNarrativo maria = new ParticipanteNarrativo(
                "participante.maria", "Maria");
        String evento = "evento.presente";
        EstadoNarrativo inicial = estado(
                0, "tempo.inicial", maria, figurinha, 32);
        EventoNarrativoCurado acrescimo = EventoNarrativoCurado.acrescimo(
                new MarcadorTemporal(1, evento),
                maria, figurinha, new NumeroNatural(22));
        EstadoNarrativo finalDeclarado = estado(
                2, "tempo.final", maria, figurinha, 54);
        NarrativaCurada narrativa = new NarrativaCurada(
                "Presente explicitamente curado.", inicial,
                Collections.singletonList(acrescimo), finalDeclarado);
        return new Cenario(
                narrativa,
                correspondencias(maria, figurinhas, evento));
    }

    private static Cenario cenarioReducao() {
        FamiliaObjeto morangos = new FamiliaObjeto(
                "familia.morangos", "Morangos");
        ObjetoContado morango = new ObjetoContado(
                "objeto.morango", morangos,
                Collections.<String, String>emptyMap(), "visual.morango");
        ParticipanteNarrativo nadia = new ParticipanteNarrativo(
                "participante.nadia", "Nadia");
        String evento = "evento.consumo";
        EstadoNarrativo inicial = estado(
                0, "tempo.inicial", nadia, morango, 10);
        EventoNarrativoCurado reducao = EventoNarrativoCurado.reducao(
                new MarcadorTemporal(1, evento),
                nadia, morango, new NumeroNatural(3));
        EstadoNarrativo finalDeclarado = estado(
                2, "tempo.final", nadia, morango, 7);
        NarrativaCurada narrativa = new NarrativaCurada(
                "Consumo explicitamente curado.", inicial,
                Collections.singletonList(reducao), finalDeclarado);
        return new Cenario(
                narrativa,
                correspondencias(nadia, morangos, evento));
    }

    private static List<CorrespondenciaPapelNarrativa> correspondencias(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia,
            String evento) {
        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        correspondencias.add(correspondencia(
                "papel.estadoInicial",
                ReferenciaValorNarrativo.quantidadeInicial(
                        participante, familia)));
        correspondencias.add(correspondencia(
                "papel.transformacao",
                ReferenciaValorNarrativo.variacaoDeEvento(
                        evento, participante, familia)));
        correspondencias.add(correspondencia(
                "papel.estadoFinal",
                ReferenciaValorNarrativo.quantidadeFinalCalculada(
                        participante, familia)));
        return correspondencias;
    }

    private static CorrespondenciaPapelNarrativa correspondencia(
            String papel,
            ReferenciaValorNarrativo referencia) {
        return new CorrespondenciaPapelNarrativa(papel, referencia);
    }

    private static EstadoNarrativo estado(
            int ordem,
            String chave,
            ParticipanteNarrativo participante,
            ObjetoContado objeto,
            int quantidade) {
        Map<ObjetoContado, NumeroNatural> itens = new LinkedHashMap<>();
        itens.put(objeto, new NumeroNatural(quantidade));
        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        inventarios.put(participante, new InventarioNarrativo(itens));
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
