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

/** Prova executável da ponte rica de Comparação de Medidas. */
public class TesteConversorComparacaoMedidasRica {

    public static void main(String[] args) {
        ConversorSituacaoProblemaRica conversor =
                new ConversorSituacaoProblemaRica();

        Complemento positivo = complemento("Paulo", 6, "José", 14);
        ResultadoConversaoSituacaoProblemaRica maisOito = conversor.converter(
                registro("cmp-positivo", "6", "14", "8", "positivo",
                        "referendo"),
                positivo.narrativa, positivo.correspondencias);
        checar("comparação positiva é construída", maisOito.foiConstruida(), true);
        checar("comparação positiva é consistente", maisOito.ehValida(), true);
        SituacaoProblema situacao = maisOito.getSituacaoOuFalhar();
        checar("estrutura tem os três papéis de comparação",
                situacao.getEstrutura().getPapeis().size() == 3, true);
        checar("valor relativo usa a chave viva canônica",
                situacao.getEstrutura().papel("papel.diferenca") != null, true);
        checar("estrutura tem uma relação local",
                situacao.getEstrutura().getRelacoes().size() == 1, true);

        Complemento negativo = complemento("Marcus", 23, "Jardel", 11);
        ResultadoConversaoSituacaoProblemaRica menosDoze = conversor.converter(
                registro("cmp-negativo", "23", "11", "12", "negativo",
                        "valor_relativo"),
                negativo.narrativa, negativo.correspondencias);
        checar("comparação negativa é consistente", menosDoze.ehValida(), true);
        checar("diferença narrativa preserva o sinal",
                menosDoze.getSituacaoOuFalhar().getEstrutura()
                        .papel("papel.diferenca").valorAtual()
                        .valorOuNull().intValue() == -12, true);

        Complemento iguais = complemento("Ana", 5, "Bia", 5);
        ResultadoConversaoSituacaoProblemaRica zero = conversor.converter(
                registro("cmp-zero", "5", "5", "0", "neutro",
                        "valor_relativo"),
                iguais.narrativa, iguais.correspondencias);
        checar("valor relativo nulo continua válido na comparação",
                zero.ehValida(), true);

        ResultadoConversaoSituacaoProblemaRica semSinal = conversor.converter(
                registro("cmp-sem-sinal", "6", "14", "8", "",
                        "referendo"),
                positivo.narrativa, positivo.correspondencias);
        checar("valor relativo sem sinal não é convertido",
                semSinal.foiConstruida(), false);
        checar("ausência do sinal fica diagnosticada",
                semSinal.possuiCodigo("conversao.sinal.ausente_ou_invalido"), true);

        ResultadoConversaoSituacaoProblemaRica medidaNegativa = conversor.converter(
                registro("cmp-medida-negativa", "-1", "14", "15", "positivo",
                        "referendo"),
                positivo.narrativa, positivo.correspondencias);
        checar("medida negativa não é convertida", medidaNegativa.foiConstruida(), false);
        checar("domínio natural violado fica explícito",
                medidaNegativa.possuiCodigo("conversao.campo.nao_natural"), true);

        ResultadoConversaoSituacaoProblemaRica invertida = conversor.converter(
                registro("cmp-invertida", "6", "14", "8", "positivo",
                        "referendo"),
                positivo.narrativa, positivo.correspondenciasInvertidas);
        checar("mapeamento humano invertido não é corrigido silenciosamente",
                invertida.foiConstruida(), true);
        checar("mapeamento invertido é rejeitado pela validação",
                invertida.ehValida(), false);
        checar("divergência narrativa fica explícita",
                invertida.possuiCodigo(
                        "situacao.correspondencia.valor_divergente"), true);

        System.out.println(
                "APROVADO: ponte rica de COMPARACAO_MEDIDAS sem inferência posicional.");
    }

    private static SituacaoProblemaAditiva registro(
            String id, String referido, String referendo,
            String valorRelativo, String sinal, String termoDesconhecido) {
        return new SituacaoProblemaAditiva(
                id, true, TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                IdiomaInterface.PORTUGUES,
                "Duas quantidades explicitamente comparadas.",
                "Objetos comparáveis", "teste", "",
                "", "", "", "", "", "", "",
                referido, referendo, valorRelativo, sinal,
                termoDesconhecido, "COMPARACAO_MEDIDAS", "");
    }

    private static Complemento complemento(
            String nomeReferido, int quantidadeReferido,
            String nomeReferendo, int quantidadeReferendo) {
        FamiliaObjeto bolas = new FamiliaObjeto("familia.bolas", "Bolas");
        ObjetoContado bola = new ObjetoContado(
                "objeto.bola", bolas,
                Collections.<String, String>emptyMap(), "visual.bola");
        ParticipanteNarrativo referido = new ParticipanteNarrativo(
                "participante.referido", nomeReferido);
        ParticipanteNarrativo referendo = new ParticipanteNarrativo(
                "participante.referendo", nomeReferendo);

        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        inventarios.put(referido, inventario(bola, quantidadeReferido));
        inventarios.put(referendo, inventario(bola, quantidadeReferendo));
        EstadoNarrativo inicial = new EstadoNarrativo(
                new MarcadorTemporal(0, "tempo.comparacao"), inventarios);
        EstadoNarrativo finalDeclarado = new EstadoNarrativo(
                new MarcadorTemporal(1, "tempo.comparacao.final"), inventarios);
        NarrativaCurada narrativa = new NarrativaCurada(
                "Comparação estática explicitamente curada.",
                inicial, Collections.emptyList(), finalDeclarado);

        List<CorrespondenciaPapelNarrativa> correspondencias =
                correspondencias(referido, referendo, bolas, false);
        List<CorrespondenciaPapelNarrativa> invertidas =
                correspondencias(referido, referendo, bolas, true);
        return new Complemento(narrativa, correspondencias, invertidas);
    }

    private static List<CorrespondenciaPapelNarrativa> correspondencias(
            ParticipanteNarrativo referido,
            ParticipanteNarrativo referendo,
            FamiliaObjeto familia,
            boolean inverterDiferenca) {
        List<CorrespondenciaPapelNarrativa> resultado = new ArrayList<>();
        resultado.add(correspondencia("papel.referido",
                ReferenciaValorNarrativo.quantidadeInicial(referido, familia)));
        resultado.add(correspondencia("papel.diferenca",
                inverterDiferenca
                        ? ReferenciaValorNarrativo.diferencaEntreQuantidadesIniciais(
                                referido, referendo, familia)
                        : ReferenciaValorNarrativo.diferencaEntreQuantidadesIniciais(
                                referendo, referido, familia)));
        resultado.add(correspondencia("papel.referendo",
                ReferenciaValorNarrativo.quantidadeInicial(referendo, familia)));
        return resultado;
    }

    private static CorrespondenciaPapelNarrativa correspondencia(
            String papel, ReferenciaValorNarrativo referencia) {
        return new CorrespondenciaPapelNarrativa(papel, referencia);
    }

    private static InventarioNarrativo inventario(
            ObjetoContado objeto, int quantidade) {
        Map<ObjetoContado, NumeroNatural> itens = new LinkedHashMap<>();
        itens.put(objeto, new NumeroNatural(quantidade));
        return new InventarioNarrativo(itens);
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
        private final List<CorrespondenciaPapelNarrativa>
                correspondenciasInvertidas;

        private Complemento(
                NarrativaCurada narrativa,
                List<CorrespondenciaPapelNarrativa> correspondencias,
                List<CorrespondenciaPapelNarrativa> correspondenciasInvertidas) {
            this.narrativa = narrativa;
            this.correspondencias = correspondencias;
            this.correspondenciasInvertidas = correspondenciasInvertidas;
        }
    }
}
