package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.curadoria.sinal.OpcaoSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PapelSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PoliticaSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.ResultadoValidacaoSinalCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoMedidas;
import gerard.dominio.campoaditivo.OperacaoAditiva;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
import gerard.dominio.campoaditivo.RelacaoEstruturalComparacao;
import gerard.dominio.campoaditivo.RelacaoEstruturalOperacaoBinaria;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacao;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacaoDeRelacaoOrientada;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.CriterioOperacaoModelagem;
import gerard.dominio.campoaditivo.situacao.DiagnosticoSituacao;
import gerard.dominio.campoaditivo.situacao.EstruturaAditiva;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.RelacaoEstruturalVinculada;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.ResultadoValidacaoSituacao;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import gerard.dominio.campoaditivo.situacao.StatusCuradoriaSituacao;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Ponte explícita do registro tabular legado para o agregado rico.
 *
 * O conversor não lê personagens, objetos, eventos ou correspondências do
 * enunciado: esses conhecimentos precisam chegar como narrativa e mapeamentos
 * já declarados pela curadoria humana. A cobertura cresce uma categoria por
 * vez; atualmente inclui COMPOSICAO_MEDIDAS, TRANSFORMACAO_MEDIDAS,
 * COMPARACAO_MEDIDAS, COMPOSICAO_TRANSFORMACOES,
 * TRANSFORMACAO_RELACAO e COMPOSICAO_RELACOES.
 */
public final class ConversorSituacaoProblemaRica {

    /**
     * Aplica o status editorial explicitamente persistido pelo pesquisador.
     * A promoção nunca é derivada da marca validada do registro tabular.
     */
    public ResultadoConversaoSituacaoProblemaRica converter(
            SituacaoProblemaAditiva registro,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias,
            StatusCuradoriaSituacao statusCuradoria) {
        if (statusCuradoria == null) {
            throw new IllegalArgumentException(
                    "status da curadoria rica é obrigatório");
        }
        ResultadoConversaoSituacaoProblemaRica base = converter(
                registro, narrativa, correspondencias);
        if (statusCuradoria != StatusCuradoriaSituacao
                .VALIDADA_PELO_PESQUISADOR || !base.ehValida()) {
            return base;
        }
        SituacaoProblema candidata = base.getSituacaoOuFalhar();
        SituacaoProblema validada = new SituacaoProblema(
                candidata.getId(), statusCuradoria,
                candidata.getReferenciasCuradas(), candidata.getEstrutura(),
                candidata.getNarrativa(), candidata.getCorrespondencias());
        return resultado(validada, base.getDiagnosticos());
    }

    public ResultadoConversaoSituacaoProblemaRica converter(
            SituacaoProblemaAditiva registro,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias) {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        if (registro == null) {
            diagnosticos.add(diagnostico(
                    "conversao.registro.ausente", "registro curado"));
            return resultado(null, diagnosticos);
        }
        if (registro.getTipo() != TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                && registro.getTipo()
                        != TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                && registro.getTipo()
                        != TipoSituacaoAditiva.COMPARACAO_MEDIDAS
                && registro.getTipo()
                        != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                && registro.getTipo()
                        != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                && registro.getTipo()
                        != TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            diagnosticos.add(diagnostico(
                    "conversao.categoria.nao_suportada",
                    registro.getTipo() == null ? "" : registro.getTipo().name()));
        }
        if (narrativa == null) {
            diagnosticos.add(diagnostico(
                    "conversao.narrativa.ausente", registro.getId()));
        }
        if (correspondencias == null || correspondencias.isEmpty()) {
            diagnosticos.add(diagnostico(
                    "conversao.correspondencias.ausentes", registro.getId()));
        }

        ResolvedorIncognitaCurada.Resultado incognita =
                new ResolvedorIncognitaCurada().resolver(registro);
        if (!incognita.possuiIncognita()) {
            diagnosticos.add(diagnostico(
                    "conversao.incognita.ausente", registro.getId()));
        } else if (incognita.possuiConflito()
                || incognita.possuiMultiplasInterrogacoes()) {
            diagnosticos.add(diagnostico(
                    "conversao.incognita.inconsistente",
                    incognita.mensagemInconsistencia()));
        }

        if (!diagnosticos.isEmpty()) {
            return resultado(null, diagnosticos);
        }
        if (registro.getTipo() == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return converterComposicaoMedidas(
                    registro, narrativa, correspondencias, incognita);
        }
        if (registro.getTipo() == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            return converterTransformacaoMedidas(
                    registro, narrativa, correspondencias, incognita);
        }
        if (registro.getTipo() == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            return converterComparacaoMedidas(
                    registro, narrativa, correspondencias, incognita);
        }
        if (registro.getTipo() == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            return converterTransformacaoRelacao(
                    registro, narrativa, correspondencias, incognita);
        }
        if (registro.getTipo() == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return converterComposicaoRelacoes(
                    registro, narrativa, correspondencias, incognita);
        }
        return converterComposicaoTransformacoes(
                registro, narrativa, correspondencias, incognita);
    }

    private ResultadoConversaoSituacaoProblemaRica converterTransformacaoMedidas(
            SituacaoProblemaAditiva registro,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias,
            ResolvedorIncognitaCurada.Resultado incognita) {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        Integer estadoInicial = natural(
                "estado_inicial", registro.getEstadoInicial(), diagnosticos);
        Integer transformacao = inteiroComSinalCurado(
                "transformacao",
                registro.getTransformacao(),
                registro.getSinalTransformacao(),
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                PapelSinalCuradoria.TRANSFORMACAO,
                diagnosticos);
        Integer estadoFinal = natural(
                "estado_final", registro.getEstadoFinal(), diagnosticos);
        if (!diagnosticos.isEmpty()) {
            return resultado(null, diagnosticos);
        }

        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo papelEstadoInicial =
                FabricaPapeisTransformacaoMedidas.estadoInicial(nenhum);
        PapelQuantitativo papelTransformacao =
                FabricaPapeisTransformacaoMedidas.transformacao(nenhum);
        PapelQuantitativo papelEstadoFinal =
                FabricaPapeisTransformacaoMedidas.estadoFinal(nenhum);
        papelEstadoInicial.posicionar(
                new NumeroNatural(estadoInicial.intValue()));
        if (!posicionarInteiro(
                papelTransformacao, transformacao,
                "transformacao", diagnosticos)) {
            return resultado(null, diagnosticos);
        }
        papelEstadoFinal.posicionar(new NumeroNatural(estadoFinal.intValue()));

        List<PapelQuantitativo> papeis = new ArrayList<>();
        papeis.add(papelEstadoInicial);
        papeis.add(papelTransformacao);
        papeis.add(papelEstadoFinal);
        List<RelacaoEstruturalVinculada> relacoes = new ArrayList<>();
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.transformacao_medidas",
                RelacaoEstruturalTransformacao.transformacaoDeMedidas(),
                papelEstadoInicial, papelTransformacao, papelEstadoFinal));
        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                papeis, relacoes, incognita.getChaveEfetiva());
        return concluirConversao(
                registro, narrativa, correspondencias,
                estrutura, diagnosticos);
    }

    private ResultadoConversaoSituacaoProblemaRica converterComparacaoMedidas(
            SituacaoProblemaAditiva registro,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias,
            ResolvedorIncognitaCurada.Resultado incognita) {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        Integer referido = natural(
                "referido", registro.getReferido(), diagnosticos);
        Integer valorRelativo = inteiroComSinalCurado(
                "valor_relativo",
                registro.getValorRelativo(),
                registro.getSinalValorRelativo(),
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                PapelSinalCuradoria.VALOR_RELATIVO,
                diagnosticos);
        Integer referendo = natural(
                "referendo", registro.getReferendo(), diagnosticos);
        if (!diagnosticos.isEmpty()) {
            return resultado(null, diagnosticos);
        }

        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo papelReferido =
                FabricaPapeisComparacaoMedidas.referido(nenhum);
        PapelQuantitativo papelValorRelativo =
                FabricaPapeisComparacaoMedidas.valorRelativo(nenhum);
        PapelQuantitativo papelReferendo =
                FabricaPapeisComparacaoMedidas.referendo(nenhum);
        papelReferido.posicionar(new NumeroNatural(referido.intValue()));
        if (!posicionarInteiro(
                papelValorRelativo, valorRelativo,
                "valor_relativo", diagnosticos)) {
            return resultado(null, diagnosticos);
        }
        papelReferendo.posicionar(new NumeroNatural(referendo.intValue()));

        List<PapelQuantitativo> papeis = new ArrayList<>();
        papeis.add(papelReferido);
        papeis.add(papelValorRelativo);
        papeis.add(papelReferendo);
        List<RelacaoEstruturalVinculada> relacoes = new ArrayList<>();
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.comparacao_medidas",
                RelacaoEstruturalComparacao.comparacaoDeMedidas(),
                papelReferido, papelValorRelativo, papelReferendo));
        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                papeis, relacoes, incognita.getChaveEfetiva());
        return concluirConversao(
                registro, narrativa, correspondencias,
                estrutura, diagnosticos);
    }

    private ResultadoConversaoSituacaoProblemaRica converterComposicaoMedidas(
            SituacaoProblemaAditiva registro,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias,
            ResolvedorIncognitaCurada.Resultado incognita) {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        Integer parte1 = natural(
                "parte_1", registro.getQuantidade1(), diagnosticos);
        Integer parte2 = natural(
                "parte_2", registro.getQuantidade2(), diagnosticos);
        Integer todo = natural(
                "todo", registro.getResultado(), diagnosticos);
        if (!diagnosticos.isEmpty()) {
            return resultado(null, diagnosticos);
        }

        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo papelParte1 = PapelQuantitativo.parte1(nenhum);
        PapelQuantitativo papelParte2 = PapelQuantitativo.parte2(nenhum);
        PapelQuantitativo papelTodo = PapelQuantitativo.todo(nenhum);
        papelParte1.posicionar(new NumeroNatural(parte1.intValue()));
        papelParte2.posicionar(new NumeroNatural(parte2.intValue()));
        papelTodo.posicionar(new NumeroNatural(todo.intValue()));

        List<PapelQuantitativo> papeis = new ArrayList<>();
        papeis.add(papelParte1);
        papeis.add(papelParte2);
        papeis.add(papelTodo);
        List<RelacaoEstruturalVinculada> relacoes = new ArrayList<>();
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.composicao_medidas",
                RelacaoEstruturalComposicao.composicaoDeMedidas(),
                papelParte1, papelParte2, papelTodo));
        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                papeis, relacoes, incognita.getChaveEfetiva());
        return concluirConversao(
                registro, narrativa, correspondencias,
                estrutura, diagnosticos);
    }

    private ResultadoConversaoSituacaoProblemaRica
            converterTransformacaoRelacao(
                    SituacaoProblemaAditiva registro,
                    NarrativaCurada narrativa,
                    List<CorrespondenciaPapelNarrativa> correspondencias,
                    ResolvedorIncognitaCurada.Resultado incognita) {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        Integer relacaoInicial = inteiro(
                "relacao_inicial", registro.getEstadoInicial(), diagnosticos);
        Integer transformacao = inteiroComSinalCurado(
                "transformacao",
                registro.getTransformacao(),
                registro.getSinalTransformacao(),
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                PapelSinalCuradoria.TRANSFORMACAO,
                diagnosticos);
        Integer relacaoFinal = inteiro(
                "relacao_final", registro.getEstadoFinal(), diagnosticos);
        OperacaoAditiva operacaoModelagem = operacao(
                "operacao_relacao", registro.getOperacaoRelacao(), diagnosticos);

        ReferenciaValorNarrativo referenciaInicial = referenciaDoPapel(
                "papel.relacaoInicial", correspondencias, diagnosticos);
        ReferenciaValorNarrativo referenciaTransformacao = referenciaDoPapel(
                "papel.transformacao", correspondencias, diagnosticos);
        ReferenciaValorNarrativo referenciaFinal = referenciaDoPapel(
                "papel.relacaoFinal", correspondencias, diagnosticos);
        if (!diagnosticos.isEmpty()) {
            return resultado(null, diagnosticos);
        }

        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo papelRelacaoInicial =
                FabricaPapeisTransformacaoDeRelacao.relacaoInicial(nenhum);
        PapelQuantitativo papelTransformacao =
                FabricaPapeisTransformacaoDeRelacao.transformacao(nenhum);
        PapelQuantitativo papelRelacaoFinal =
                FabricaPapeisTransformacaoDeRelacao.relacaoFinal(nenhum);
        boolean inicialValida = posicionarInteiro(
                papelRelacaoInicial, relacaoInicial,
                "relacao_inicial", diagnosticos);
        boolean transformacaoValida = posicionarInteiro(
                papelTransformacao, transformacao,
                "transformacao", diagnosticos);
        boolean finalValida = posicionarInteiro(
                papelRelacaoFinal, relacaoFinal,
                "relacao_final", diagnosticos);
        if (!inicialValida || !transformacaoValida || !finalValida) {
            return resultado(null, diagnosticos);
        }

        gerard.dominio.campoaditivo.RelacaoEstruturalAditiva relacaoOrientada;
        try {
            relacaoOrientada = RelacaoEstruturalTransformacaoDeRelacaoOrientada
                    .aPartirDasReferencias(
                            referenciaInicial,
                            referenciaTransformacao,
                            referenciaFinal);
        } catch (IllegalArgumentException orientacaoInvalida) {
            diagnosticos.add(diagnostico(
                    "conversao.relacao.orientacao_invalida",
                    orientacaoInvalida.getMessage()));
            return resultado(null, diagnosticos);
        }

        List<PapelQuantitativo> papeis = new ArrayList<>();
        papeis.add(papelRelacaoInicial);
        papeis.add(papelTransformacao);
        papeis.add(papelRelacaoFinal);
        List<RelacaoEstruturalVinculada> relacoes = new ArrayList<>();
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.transformacao_relacao.orientada",
                relacaoOrientada,
                papelRelacaoInicial, papelTransformacao, papelRelacaoFinal));
        List<CriterioOperacaoModelagem> criteriosOperacao = new ArrayList<>();
        criteriosOperacao.add(new CriterioOperacaoModelagem(
                "operacao.transformacao_relacao",
                operacaoModelagem,
                Arrays.asList(
                        "papel.relacaoInicial",
                        "papel.transformacao",
                        "papel.relacaoFinal")));
        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                papeis, relacoes, criteriosOperacao,
                incognita.getChaveEfetiva());
        return concluirConversao(
                registro, narrativa, correspondencias,
                estrutura, diagnosticos);
    }

    private ResultadoConversaoSituacaoProblemaRica
            converterComposicaoRelacoes(
                    SituacaoProblemaAditiva registro,
                    NarrativaCurada narrativa,
                    List<CorrespondenciaPapelNarrativa> correspondencias,
                    ResolvedorIncognitaCurada.Resultado incognita) {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        Integer relacao1 = inteiro(
                "relacao_1", registro.getQuantidade1(), diagnosticos);
        Integer relacao2 = inteiro(
                "relacao_2", registro.getQuantidade2(), diagnosticos);
        Integer relacaoFinal = inteiro(
                "relacao_resultante", registro.getResultado(), diagnosticos);
        OperacaoAditiva operacaoRelacoes = operacao(
                "operacao_relacao", registro.getOperacaoRelacao(), diagnosticos);

        referenciaDoPapel(
                "papel.relacao1", correspondencias, diagnosticos);
        referenciaDoPapel(
                "papel.relacao2", correspondencias, diagnosticos);
        referenciaDoPapel(
                "papel.relacaoFinal", correspondencias, diagnosticos);
        if (!diagnosticos.isEmpty()) {
            return resultado(null, diagnosticos);
        }

        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo papelRelacao1 =
                FabricaPapeisComposicaoDeRelacoes.relacao1(nenhum);
        PapelQuantitativo papelRelacao2 =
                FabricaPapeisComposicaoDeRelacoes.relacao2(nenhum);
        PapelQuantitativo papelRelacaoFinal =
                FabricaPapeisComposicaoDeRelacoes.relacaoFinal(nenhum);
        boolean primeiraValida = posicionarInteiro(
                papelRelacao1, relacao1, "relacao_1", diagnosticos);
        boolean segundaValida = posicionarInteiro(
                papelRelacao2, relacao2, "relacao_2", diagnosticos);
        boolean finalValida = posicionarInteiro(
                papelRelacaoFinal, relacaoFinal,
                "relacao_resultante", diagnosticos);
        if (!primeiraValida || !segundaValida || !finalValida) {
            return resultado(null, diagnosticos);
        }

        List<PapelQuantitativo> papeis = new ArrayList<>();
        papeis.add(papelRelacao1);
        papeis.add(papelRelacao2);
        papeis.add(papelRelacaoFinal);
        List<RelacaoEstruturalVinculada> relacoes = new ArrayList<>();
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.composicao_relacoes.curada",
                RelacaoEstruturalOperacaoBinaria.com(operacaoRelacoes),
                papelRelacao1, papelRelacao2, papelRelacaoFinal));
        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                papeis, relacoes, incognita.getChaveEfetiva());
        return concluirConversao(
                registro, narrativa, correspondencias,
                estrutura, diagnosticos);
    }

    private ResultadoConversaoSituacaoProblemaRica
            converterComposicaoTransformacoes(
                    SituacaoProblemaAditiva registro,
                    NarrativaCurada narrativa,
                    List<CorrespondenciaPapelNarrativa> correspondencias,
                    ResolvedorIncognitaCurada.Resultado incognita) {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();

        Integer estadoInicial = natural(
                "estado_inicial", registro.getEstadoInicial(), diagnosticos);
        Integer transformacao1 = inteiro(
                "transformacao_1", registro.getQuantidade1(), diagnosticos);
        Integer estadoIntermediario = natural(
                "estado_intermediario", registro.getEstadoIntermediario(), diagnosticos);
        Integer transformacao2 = inteiro(
                "transformacao_2", registro.getQuantidade2(), diagnosticos);
        Integer transformacaoResultante = inteiro(
                "transformacao_resultante", registro.getResultado(), diagnosticos);
        Integer estadoFinal = natural(
                "estado_final", registro.getEstadoFinal(), diagnosticos);
        OperacaoAditiva operacaoTransformacoes = operacao(
                "operacao_transformacao", registro.getOperacaoRelacao(), diagnosticos);
        OperacaoAditiva operacaoEstadoTransformacao = operacao(
                "operacao_estado_transformacao",
                registro.getOperacaoEstadoTransformacao(), diagnosticos);

        if (!diagnosticos.isEmpty()) {
            return resultado(null, diagnosticos);
        }

        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo papelEstadoInicial =
                FabricaPapeisComposicaoDeTransformacoes.estadoInicial(nenhum);
        PapelQuantitativo papelTransformacao1 =
                FabricaPapeisComposicaoDeTransformacoes.transformacao1(nenhum);
        PapelQuantitativo papelEstadoIntermediario =
                FabricaPapeisComposicaoDeTransformacoes.estadoIntermediario(nenhum);
        PapelQuantitativo papelTransformacao2 =
                FabricaPapeisComposicaoDeTransformacoes.transformacao2(nenhum);
        PapelQuantitativo papelTransformacaoResultante =
                FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(nenhum);
        PapelQuantitativo papelEstadoFinal =
                FabricaPapeisComposicaoDeTransformacoes.estadoFinal(nenhum);

        papelEstadoInicial.posicionar(new NumeroNatural(estadoInicial.intValue()));
        boolean primeiraValida = posicionarInteiro(
                papelTransformacao1, transformacao1,
                "transformacao_1", diagnosticos);
        papelEstadoIntermediario.posicionar(
                new NumeroNatural(estadoIntermediario.intValue()));
        boolean segundaValida = posicionarInteiro(
                papelTransformacao2, transformacao2,
                "transformacao_2", diagnosticos);
        boolean resultanteValida = posicionarInteiro(
                papelTransformacaoResultante, transformacaoResultante,
                "transformacao_resultante", diagnosticos);
        papelEstadoFinal.posicionar(new NumeroNatural(estadoFinal.intValue()));

        if (!primeiraValida || !segundaValida || !resultanteValida) {
            return resultado(null, diagnosticos);
        }

        List<PapelQuantitativo> papeis = new ArrayList<>();
        papeis.add(papelEstadoInicial);
        papeis.add(papelTransformacao1);
        papeis.add(papelEstadoIntermediario);
        papeis.add(papelTransformacao2);
        papeis.add(papelTransformacaoResultante);
        papeis.add(papelEstadoFinal);

        List<RelacaoEstruturalVinculada> relacoes = new ArrayList<>();
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.transformacoes.curada",
                RelacaoEstruturalOperacaoBinaria.com(operacaoTransformacoes),
                papelTransformacao1, papelTransformacao2,
                papelTransformacaoResultante));
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.estado.primeiro_evento",
                RelacaoEstruturalTransformacao.transformacaoDeMedidas(),
                papelEstadoInicial, papelTransformacao1,
                papelEstadoIntermediario));
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.estado.segundo_evento",
                RelacaoEstruturalTransformacao.transformacaoDeMedidas(),
                papelEstadoIntermediario, papelTransformacao2,
                papelEstadoFinal));
        relacoes.add(new RelacaoEstruturalVinculada(
                "relacao.estado.transformacao_resultante.curada",
                RelacaoEstruturalOperacaoBinaria.com(operacaoEstadoTransformacao),
                papelEstadoInicial, papelTransformacaoResultante,
                papelEstadoFinal));

        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                papeis, relacoes, incognita.getChaveEfetiva());
        return concluirConversao(
                registro, narrativa, correspondencias,
                estrutura, diagnosticos);
    }

    private ResultadoConversaoSituacaoProblemaRica concluirConversao(
            SituacaoProblemaAditiva registro,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias,
            EstruturaAditiva estrutura,
            List<DiagnosticoSituacao> diagnosticos) {
        SituacaoProblema situacao = new SituacaoProblema(
                registro.getId(),
                StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA,
                referencias(registro),
                estrutura,
                narrativa,
                correspondencias);

        ResultadoValidacaoSituacao validacao = situacao.validar();
        diagnosticos.addAll(validacao.getDiagnosticos());
        return resultado(situacao, diagnosticos);
    }

    private static List<String> referencias(SituacaoProblemaAditiva registro) {
        Set<String> unicas = new LinkedHashSet<>();
        adicionarSePresente(unicas, registro.getId());
        adicionarSePresente(unicas, registro.getSituacaoGrupoId());
        adicionarSePresente(unicas, registro.getVersaoOrigemId());
        return new ArrayList<String>(unicas);
    }

    private static void adicionarSePresente(Set<String> referencias, String valor) {
        String limpo = limpar(valor);
        if (!limpo.isEmpty()) referencias.add(limpo);
    }

    private static Integer natural(
            String campo,
            String valor,
            List<DiagnosticoSituacao> diagnosticos) {
        Integer numero = inteiro(campo, valor, diagnosticos);
        if (numero != null && numero.intValue() < 0) {
            diagnosticos.add(diagnostico(
                    "conversao.campo.nao_natural", campo));
            return null;
        }
        return numero;
    }

    private static Integer inteiro(
            String campo,
            String valor,
            List<DiagnosticoSituacao> diagnosticos) {
        String limpo = limpar(valor);
        if (limpo.isEmpty() || "?".equals(limpo)) {
            diagnosticos.add(diagnostico(
                    "conversao.campo.ausente", campo));
            return null;
        }
        try {
            return Integer.valueOf(limpo);
        } catch (NumberFormatException invalido) {
            diagnosticos.add(diagnostico(
                    "conversao.campo.numero_invalido", campo + ":" + limpo));
            return null;
        }
    }

    private static Integer inteiroComSinalCurado(
            String campo,
            String magnitude,
            String sinal,
            TipoSituacaoAditiva tipo,
            PapelSinalCuradoria papelSinal,
            List<DiagnosticoSituacao> diagnosticos) {
        OpcaoSinalCuradoria opcao = OpcaoSinalCuradoria.aPartirDoEstado(
                sinal, magnitude);
        ResultadoValidacaoSinalCuradoria validacao =
                new PoliticaSinalCuradoria().validar(
                        tipo,
                        papelSinal,
                        opcao,
                        magnitude);
        if (!validacao.isValido()) {
            diagnosticos.add(diagnostico(
                    "conversao.sinal.ausente_ou_invalido",
                    campo + ":" + validacao.getChaveMensagem()));
            return null;
        }
        return inteiro(
                campo,
                PoliticaSinalCuradoria.aplicarSinal(magnitude, opcao),
                diagnosticos);
    }

    private static boolean posicionarInteiro(
            PapelQuantitativo papel,
            Integer valor,
            String campo,
            List<DiagnosticoSituacao> diagnosticos) {
        if (papel.posicionar(new NumeroInteiro(valor.intValue())).isPresent()) {
            diagnosticos.add(diagnostico(
                    "conversao.campo.fora_do_dominio", campo));
            return false;
        }
        return true;
    }

    private static OperacaoAditiva operacao(
            String campo,
            String valor,
            List<DiagnosticoSituacao> diagnosticos) {
        OpcaoOperacaoCuradoria curada =
                OpcaoOperacaoCuradoria.aPartirDoEstado(valor);
        if (!curada.isEscolhaValida()) {
            diagnosticos.add(diagnostico(
                    "conversao.operacao.ausente_ou_invalida", campo));
            return null;
        }
        return curada == OpcaoOperacaoCuradoria.SOMA
                ? OperacaoAditiva.SOMA : OperacaoAditiva.SUBTRACAO;
    }

    private static ReferenciaValorNarrativo referenciaDoPapel(
            String chavePapel,
            List<CorrespondenciaPapelNarrativa> correspondencias,
            List<DiagnosticoSituacao> diagnosticos) {
        ReferenciaValorNarrativo encontrada = null;
        for (CorrespondenciaPapelNarrativa correspondencia : correspondencias) {
            if (!chavePapel.equals(correspondencia.getChavePapel())) {
                continue;
            }
            if (encontrada != null) {
                diagnosticos.add(diagnostico(
                        "conversao.correspondencia.papel_duplicado", chavePapel));
                return null;
            }
            encontrada = correspondencia.getReferencia();
        }
        if (encontrada == null) {
            diagnosticos.add(diagnostico(
                    "conversao.correspondencia.papel_ausente", chavePapel));
        }
        return encontrada;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static DiagnosticoSituacao diagnostico(String codigo, String detalhe) {
        return new DiagnosticoSituacao(codigo, detalhe);
    }

    private static ResultadoConversaoSituacaoProblemaRica resultado(
            SituacaoProblema situacao,
            List<DiagnosticoSituacao> diagnosticos) {
        return new ResultadoConversaoSituacaoProblemaRica(
                situacao, diagnosticos);
    }
}
