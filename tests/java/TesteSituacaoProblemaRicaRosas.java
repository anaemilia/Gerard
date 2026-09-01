import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.OperacaoAditiva;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalOperacaoBinaria;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacao;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.EstadoNarrativo;
import gerard.dominio.campoaditivo.situacao.EstruturaAditiva;
import gerard.dominio.campoaditivo.situacao.EventoNarrativoCurado;
import gerard.dominio.campoaditivo.situacao.FamiliaObjeto;
import gerard.dominio.campoaditivo.situacao.InventarioNarrativo;
import gerard.dominio.campoaditivo.situacao.MarcadorTemporal;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.ObjetoContado;
import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.RelacaoEstruturalVinculada;
import gerard.dominio.campoaditivo.situacao.ResultadoValidacaoSituacao;
import gerard.dominio.campoaditivo.situacao.SequenciaNarrativa;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import gerard.dominio.campoaditivo.situacao.StatusCuradoriaSituacao;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Caso executável inicial do agregado rico, sem UI e sem alterar o TSV. */
public class TesteSituacaoProblemaRicaRosas {

    public static void main(String[] args) {
        Cenario cenario = criarCenario(1, false);
        SituacaoProblema situacao = cenario.situacao;
        ResultadoValidacaoSituacao resultado = situacao.validar();

        checar("a situação rica das rosas é consistente",
                String.valueOf(resultado.ehValida()), "true");
        checar("usa a categoria consolidada já existente",
                situacao.getEstrutura().getCategoria().name(),
                "COMPOSICAO_TRANSFORMACOES");
        checar("a conversão ainda aguarda validação humana",
                situacao.getStatusCuradoria().name(), "CANDIDATA_NAO_CURADA");
        checar("preserva a proveniência curada",
                situacao.getReferenciasCuradas().get(0),
                "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114");

        checar("todos os seis papéis têm correspondência narrativa explícita",
                String.valueOf(situacao.getCorrespondencias().size()), "6");
        checar("estado final foi ligado nominalmente à Vovó",
                cenario.correspondenciaEstadoFinal.getReferencia()
                        .getParticipante().getId(), "participante.vovo");
        checar("estado final foi ligado nominalmente à família rosas",
                cenario.correspondenciaEstadoFinal.getReferencia()
                        .getFamilia().getId(), "familia.rosas");

        SequenciaNarrativa sequencia = situacao.criarSequenciaNarrativa();
        checar("a sequência possui os dois eventos curados",
                String.valueOf(sequencia.getEventos().size()), "2");
        checar("há um estado calculado depois de cada evento",
                String.valueOf(sequencia.getEstadosAposEventos().size()), "2");
        checar("depois da primeira entrega, Vovó fica com quatro rosas",
                total(sequencia.getEstadosAposEventos().get(0),
                        cenario.vovo, cenario.rosas), "4");
        checar("depois da segunda entrega, Vovó fica com três rosas",
                total(sequencia.getEstadoFinalCalculado(),
                        cenario.vovo, cenario.rosas), "3");
        checar("a netinha recebe duas rosas ao fim",
                total(sequencia.getEstadoFinalCalculado(),
                        cenario.netinha, cenario.rosas), "2");

        checar("a primeira transformação narrativa é -1",
                valor(cenario.transformacao1), "-1");
        checar("a segunda transformação narrativa é -1",
                valor(cenario.transformacao2), "-1");
        checar("a transformação resultante é -2",
                valor(cenario.transformacaoResultante), "-2");
        checar("o estado inicial total é 5",
                valor(cenario.estadoInicial), "5");
        checar("o estado intermediário total é 4",
                valor(cenario.estadoIntermediario), "4");
        checar("o estado final total é 3",
                valor(cenario.estadoFinal), "3");

        Cenario finalDivergente = criarCenario(2, false);
        checar("estado final declarado divergente é diagnosticado, não corrigido",
                String.valueOf(finalDivergente.situacao.validar().possuiCodigo(
                        "narrativa.estado_final.divergente")), "true");

        Cenario mapeamentoDivergente = criarCenario(1, true);
        checar("correspondência humana divergente permanece visível",
                String.valueOf(mapeamentoDivergente.situacao.validar().possuiCodigo(
                        "situacao.correspondencia.valor_divergente")), "true");

        boolean bloqueouMidiaInconsistente;
        try {
            finalDivergente.situacao.criarSequenciaNarrativa();
            bloqueouMidiaInconsistente = false;
        } catch (IllegalStateException esperada) {
            bloqueouMidiaInconsistente = true;
        }
        checar("mídia não recebe uma situação semanticamente inconsistente",
                String.valueOf(bloqueouMidiaInconsistente), "true");

        System.out.println("APROVADO: agregado rico da situação das rosas.");
    }

    private static Cenario criarCenario(
            int rosasBrancasFinaisDaVovo,
            boolean mapearPrimeiraTransformacaoParaNetinha) {
        FamiliaObjeto rosas = new FamiliaObjeto("familia.rosas", "Rosas");
        ObjetoContado rosaBranca = new ObjetoContado(
                "objeto.rosa.branca", rosas,
                caracteristica("cor", "branca"), "visual.rosa.branca");
        ObjetoContado rosaAmarela = new ObjetoContado(
                "objeto.rosa.amarela", rosas,
                caracteristica("cor", "amarela"), "visual.rosa.amarela");
        ParticipanteNarrativo vovo = new ParticipanteNarrativo(
                "participante.vovo", "Vovó");
        ParticipanteNarrativo netinha = new ParticipanteNarrativo(
                "participante.netinha", "Netinha");

        EstadoNarrativo inicial = new EstadoNarrativo(
                new MarcadorTemporal(0, "tempo.inicial"),
                inventarios(
                        vovo, inventario(rosaBranca, 2, rosaAmarela, 3),
                        netinha, InventarioNarrativo.vazio()));

        EventoNarrativoCurado primeiraEntrega = EventoNarrativoCurado.transferencia(
                new MarcadorTemporal(1, "evento.entrega.rosa_branca"),
                vovo, netinha, rosaBranca, new NumeroNatural(1));
        EventoNarrativoCurado segundaEntrega = EventoNarrativoCurado.transferencia(
                new MarcadorTemporal(2, "evento.entrega.rosa_amarela"),
                vovo, netinha, rosaAmarela, new NumeroNatural(1));

        EstadoNarrativo finalDeclarado = new EstadoNarrativo(
                new MarcadorTemporal(3, "tempo.final"),
                inventarios(
                        vovo, inventario(
                                rosaBranca, rosasBrancasFinaisDaVovo,
                                rosaAmarela, 2),
                        netinha, inventario(rosaBranca, 1, rosaAmarela, 1)));

        NarrativaCurada narrativa = new NarrativaCurada(
                "Vovó entrega rosas à netinha em duas etapas.",
                inicial,
                Arrays.asList(primeiraEntrega, segundaEntrega),
                finalDeclarado);

        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo estadoInicial =
                FabricaPapeisComposicaoDeTransformacoes.estadoInicial(nenhum);
        PapelQuantitativo transformacao1 =
                FabricaPapeisComposicaoDeTransformacoes.transformacao1(nenhum);
        PapelQuantitativo transformacao2 =
                FabricaPapeisComposicaoDeTransformacoes.transformacao2(nenhum);
        PapelQuantitativo estadoIntermediario =
                FabricaPapeisComposicaoDeTransformacoes.estadoIntermediario(nenhum);
        PapelQuantitativo transformacaoResultante =
                FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(nenhum);
        PapelQuantitativo estadoFinal =
                FabricaPapeisComposicaoDeTransformacoes.estadoFinal(nenhum);
        estadoInicial.posicionar(new NumeroNatural(5));
        transformacao1.posicionar(new NumeroInteiro(-1));
        estadoIntermediario.posicionar(new NumeroNatural(4));
        transformacao2.posicionar(new NumeroInteiro(-1));
        transformacaoResultante.posicionar(new NumeroInteiro(-2));
        estadoFinal.posicionar(new NumeroNatural(3));

        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                Arrays.asList(
                        estadoInicial, transformacao1, estadoIntermediario, transformacao2,
                        transformacaoResultante, estadoFinal),
                Arrays.asList(
                        new RelacaoEstruturalVinculada(
                                "relacao.composicao_transformacoes",
                                RelacaoEstruturalOperacaoBinaria.com(
                                        OperacaoAditiva.SOMA),
                                transformacao1, transformacao2,
                                transformacaoResultante),
                        new RelacaoEstruturalVinculada(
                                "relacao.estado.primeiro_evento",
                                RelacaoEstruturalTransformacao.transformacaoDeMedidas(),
                                estadoInicial, transformacao1,
                                estadoIntermediario),
                        new RelacaoEstruturalVinculada(
                                "relacao.estado.segundo_evento",
                                RelacaoEstruturalTransformacao.transformacaoDeMedidas(),
                                estadoIntermediario, transformacao2, estadoFinal),
                        new RelacaoEstruturalVinculada(
                                "relacao.estado.transformacao_resultante",
                                RelacaoEstruturalOperacaoBinaria.com(
                                        OperacaoAditiva.SOMA),
                                estadoInicial, transformacaoResultante,
                                estadoFinal)),
                estadoFinal.getChave());

        List<CorrespondenciaPapelNarrativa> correspondencias = new ArrayList<>();
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                estadoInicial.getChave(),
                ReferenciaValorNarrativo.quantidadeInicial(vovo, rosas)));
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                transformacao1.getChave(),
                ReferenciaValorNarrativo.variacaoDeEvento(
                        primeiraEntrega.getMarcador().getChave(),
                        mapearPrimeiraTransformacaoParaNetinha ? netinha : vovo,
                        rosas)));
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                estadoIntermediario.getChave(),
                ReferenciaValorNarrativo.quantidadeAposEvento(
                        primeiraEntrega.getMarcador().getChave(), vovo, rosas)));
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                transformacao2.getChave(),
                ReferenciaValorNarrativo.variacaoDeEvento(
                        segundaEntrega.getMarcador().getChave(), vovo, rosas)));
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                transformacaoResultante.getChave(),
                ReferenciaValorNarrativo.variacaoTotal(vovo, rosas)));
        CorrespondenciaPapelNarrativa correspondenciaEstadoFinal =
                new CorrespondenciaPapelNarrativa(
                        estadoFinal.getChave(),
                        ReferenciaValorNarrativo.quantidadeFinalCalculada(
                                vovo, rosas));
        correspondencias.add(correspondenciaEstadoFinal);

        SituacaoProblema situacao = new SituacaoProblema(
                "situacao.rica.vovo_rosas",
                StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA,
                Collections.singletonList(
                        "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114"),
                estrutura,
                narrativa,
                correspondencias);
        return new Cenario(
                situacao, rosas, vovo, netinha,
                estadoInicial, transformacao1, estadoIntermediario, transformacao2,
                transformacaoResultante, estadoFinal,
                correspondenciaEstadoFinal);
    }

    private static Map<String, String> caracteristica(String chave, String valor) {
        LinkedHashMap<String, String> mapa = new LinkedHashMap<>();
        mapa.put(chave, valor);
        return mapa;
    }

    private static InventarioNarrativo inventario(
            ObjetoContado objeto1, int quantidade1,
            ObjetoContado objeto2, int quantidade2) {
        LinkedHashMap<ObjetoContado, NumeroNatural> mapa = new LinkedHashMap<>();
        if (quantidade1 > 0) {
            mapa.put(objeto1, new NumeroNatural(quantidade1));
        }
        if (quantidade2 > 0) {
            mapa.put(objeto2, new NumeroNatural(quantidade2));
        }
        return new InventarioNarrativo(mapa);
    }

    private static Map<ParticipanteNarrativo, InventarioNarrativo> inventarios(
            ParticipanteNarrativo participante1, InventarioNarrativo inventario1,
            ParticipanteNarrativo participante2, InventarioNarrativo inventario2) {
        LinkedHashMap<ParticipanteNarrativo, InventarioNarrativo> mapa =
                new LinkedHashMap<>();
        mapa.put(participante1, inventario1);
        mapa.put(participante2, inventario2);
        return mapa;
    }

    private static String total(
            EstadoNarrativo estado,
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return Integer.toString(
                estado.inventarioDe(participante).totalDaFamilia(familia).intValue());
    }

    private static String valor(PapelQuantitativo papel) {
        return Integer.toString(papel.valorAtual().valorOuNull().intValue());
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(
                    rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }

    private static final class Cenario {
        private final SituacaoProblema situacao;
        private final FamiliaObjeto rosas;
        private final ParticipanteNarrativo vovo;
        private final ParticipanteNarrativo netinha;
        private final PapelQuantitativo estadoInicial;
        private final PapelQuantitativo transformacao1;
        private final PapelQuantitativo estadoIntermediario;
        private final PapelQuantitativo transformacao2;
        private final PapelQuantitativo transformacaoResultante;
        private final PapelQuantitativo estadoFinal;
        private final CorrespondenciaPapelNarrativa correspondenciaEstadoFinal;

        private Cenario(
                SituacaoProblema situacao,
                FamiliaObjeto rosas,
                ParticipanteNarrativo vovo,
                ParticipanteNarrativo netinha,
                PapelQuantitativo estadoInicial,
                PapelQuantitativo transformacao1,
                PapelQuantitativo estadoIntermediario,
                PapelQuantitativo transformacao2,
                PapelQuantitativo transformacaoResultante,
                PapelQuantitativo estadoFinal,
                CorrespondenciaPapelNarrativa correspondenciaEstadoFinal) {
            this.situacao = situacao;
            this.rosas = rosas;
            this.vovo = vovo;
            this.netinha = netinha;
            this.estadoInicial = estadoInicial;
            this.transformacao1 = transformacao1;
            this.estadoIntermediario = estadoIntermediario;
            this.transformacao2 = transformacao2;
            this.transformacaoResultante = transformacaoResultante;
            this.estadoFinal = estadoFinal;
            this.correspondenciaEstadoFinal = correspondenciaEstadoFinal;
        }
    }
}
