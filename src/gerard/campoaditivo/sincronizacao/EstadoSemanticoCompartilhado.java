package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoMedidas;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComparacao;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacao;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.categoria.CatalogoEsquemasCategoriasAditivas;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.FabricaValoresNumericos;
import gerard.semantica.numero.ValorNumerico;

/**
 * Estado semântico único compartilhado pelas representações manipuláveis.
 *
 * Os valores são objetos do domínio numérico. Assim, texto, Vergnaud, barras,
 * eixo e tabuleiro não decidem localmente se um valor aceita sinal.
 */
public final class EstadoSemanticoCompartilhado {
    private final PoliticaValoresAditivos politicaValores =
            new PoliticaValoresAditivos();
    private final CatalogoEsquemasCategoriasAditivas esquemas =
            new CatalogoEsquemasCategoriasAditivas();
    private final FabricaValoresNumericos fabricaValores =
            new FabricaValoresNumericos();

    public enum Origem {
        INICIALIZACAO,
        VERGNAUD,
        DIAGRAMA_COMPLEMENTAR,
        EIXO_X,
        EIXO_VERTICAL,
        EDICAO_TEXTO,
        ARRASTE,
        EXCLUSAO,
        PROTOCOLO
    }

    public static final class Snapshot {
        private final TipoSituacaoAditiva tipo;
        private final ValorNumerico[] valores;
        private final int indiceAlterado;
        private final Origem origem;
        private final long versao;
        private final int indiceResolvidoAutomaticamente;

        private Snapshot(TipoSituacaoAditiva tipo, ValorNumerico[] valores,
                int indiceAlterado, Origem origem, long versao,
                int indiceResolvidoAutomaticamente) {
            this.tipo = tipo;
            this.valores = valores;
            this.indiceAlterado = indiceAlterado;
            this.origem = origem;
            this.versao = versao;
            this.indiceResolvidoAutomaticamente = indiceResolvidoAutomaticamente;
        }

        public TipoSituacaoAditiva getTipo() { return tipo; }
        public ValorNumerico getValorNumerico(int indice) {
            return indice >= 0 && indice < valores.length ? valores[indice] : null;
        }
        public Integer getValor(int indice) {
            ValorNumerico valor = getValorNumerico(indice);
            return valor == null ? null : valor.valorOuNull();
        }
        public boolean isConhecido(int indice) {
            ValorNumerico valor = getValorNumerico(indice);
            return valor != null && valor.ehConhecido();
        }
        public int getIndiceAlterado() { return indiceAlterado; }
        public Origem getOrigem() { return origem; }
        public long getVersao() { return versao; }
        public int valorOuZero(int indice) {
            Integer valor = getValor(indice);
            return valor == null ? 0 : valor.intValue();
        }
        public DominioNumerico getDominio(int indice) {
            ValorNumerico valor = getValorNumerico(indice);
            return valor == null ? DominioNumerico.NATURAIS : valor.getDominio();
        }

        /**
         * Índice do papel que a relação estrutural (piloto) preencheu ou
         * recalculou automaticamente durante a chamada de atualizar() que
         * produziu este snapshot — -1 se nenhum papel foi resolvido
         * automaticamente desta vez. Nunca é o papel que o próprio usuário
         * acabou de alterar (ver EstadoSemanticoCompartilhado.definirSePermitido).
         * Fonte única desse fato: quem resolve a relação é quem sabe se
         * resolveu algo, e é aqui que essa informação deve ser lida — não
         * reconstruída externamente por diff de estado "antes"/"depois" (ver
         * TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md e
         * gerard-knowledge-locality-principle).
         */
        public int getIndiceResolvidoAutomaticamente() {
            return indiceResolvidoAutomaticamente;
        }
    }

    private TipoSituacaoAditiva tipo;
    private final ValorNumerico[] valores = new ValorNumerico[3];
    private int indiceAlterado = -1;
    private Origem origem = Origem.INICIALIZACAO;
    private long versao = 0L;
    private int indiceResolvidoAutomaticamente = -1;

    public EstadoSemanticoCompartilhado() {
        limpar(null);
    }

    public synchronized void limpar(TipoSituacaoAditiva novoTipo) {
        tipo = novoTipo;
        for (int i = 0; i < valores.length; i++) {
            valores[i] = fabricaValores.desconhecido(dominioDoIndice(novoTipo, i));
        }
        indiceAlterado = -1;
        origem = Origem.INICIALIZACAO;
        versao++;
    }

    /**
     * Atualiza o snapshot com dados observados em uma representação. Valores
     * incompatíveis com o universo do papel são mantidos como desconhecidos.
     */
    public synchronized Snapshot atualizar(TipoSituacaoAditiva novoTipo,
            Integer[] novosValores, boolean[] novosConhecidos,
            int novoIndiceAlterado, Origem novaOrigem) {
        return atualizar(novoTipo, novosValores, novosConhecidos,
                novoIndiceAlterado, novaOrigem, -1, true);
    }

    /**
     * Atualização com proteção explícita da incógnita. Quando o índice
     * protegido ainda não foi preenchido pelo protocolo de mouse/texto, o
     * estado permanece desconhecido e a relação aditiva não o resolve.
     */
    public synchronized Snapshot atualizar(TipoSituacaoAditiva novoTipo,
            Integer[] novosValores, boolean[] novosConhecidos,
            int novoIndiceAlterado, Origem novaOrigem,
            int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        tipo = novoTipo;
        for (int i = 0; i < 3; i++) {
            boolean conhecido = novosConhecidos != null
                    && i < novosConhecidos.length && novosConhecidos[i];
            Integer bruto = novosValores != null && i < novosValores.length
                    ? novosValores[i] : null;
            if (i == indiceIncognitaProtegida
                    && !permitirPreenchimentoIncognita) {
                conhecido = false;
                bruto = null;
            }
            if (conhecido && !politicaValores.valorEhValidoNoEstadoCompartilhado(
                    tipo, i, bruto)) {
                conhecido = false;
            }
            valores[i] = criarValorSeguro(dominioDoIndice(tipo, i), bruto, conhecido);
        }
        indiceAlterado = novoIndiceAlterado;
        origem = novaOrigem == null ? Origem.PROTOCOLO : novaOrigem;
        indiceResolvidoAutomaticamente = -1;
        resolverRelacaoAditiva(indiceIncognitaProtegida,
                permitirPreenchimentoIncognita);
        versao++;
        return snapshot();
    }

    public synchronized Snapshot snapshot() {
        return new Snapshot(tipo,
                new ValorNumerico[] { valores[0], valores[1], valores[2] },
                indiceAlterado, origem, versao, indiceResolvidoAutomaticamente);
    }

    private void resolverRelacaoAditiva(int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        if (resolverViaRelacaoEstruturalRica(indiceIncognitaProtegida, permitirPreenchimentoIncognita)) {
            return;
        }
        if (resolverConsistenciaViaRelacaoEstruturalRica(indiceIncognitaProtegida, permitirPreenchimentoIncognita)) {
            return;
        }
        if (indiceAlterado == 0) {
            if (conhecido(0) && conhecido(1)) {
                definirSomaSePermitido(2, valor(0), valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            } else if (conhecido(0) && conhecido(2)) {
                definirSubtracaoSePermitido(1, valor(2), valor(0), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            }
            return;
        }
        if (indiceAlterado == 1) {
            if (conhecido(0) && conhecido(1)) {
                definirSomaSePermitido(2, valor(0), valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            } else if (conhecido(1) && conhecido(2)) {
                definirSubtracaoSePermitido(0, valor(2), valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            }
            return;
        }
        if (indiceAlterado == 2) {
            if (conhecido(0) && conhecido(2)) {
                definirSubtracaoSePermitido(1, valor(2), valor(0), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            } else if (conhecido(1) && conhecido(2)) {
                definirSubtracaoSePermitido(0, valor(2), valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            }
            return;
        }

        int faltantes = 0;
        int indiceFaltante = -1;
        for (int i = 0; i < 3; i++) {
            if (!conhecido(i)) {
                faltantes++;
                indiceFaltante = i;
            }
        }
        if (faltantes != 1) {
            return;
        }
        if (indiceFaltante == 0 && conhecido(1) && conhecido(2)) {
            definirSubtracaoSePermitido(0, valor(2), valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        } else if (indiceFaltante == 1 && conhecido(0) && conhecido(2)) {
            definirSubtracaoSePermitido(1, valor(2), valor(0), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        } else if (indiceFaltante == 2 && conhecido(0) && conhecido(1)) {
            definirSomaSePermitido(2, valor(0), valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        }
    }

    /**
     * Delega o cálculo aritmético às classes ricas do pacote piloto
     * (RelacaoEstruturalComposicao/Transformacao/Comparacao — ver
     * TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md, Fase B,
     * opção B1) quando exatamente um dos três papéis está incógnito e essa
     * incógnita não é a posição que acabou de ser tocada (indiceAlterado).
     * Esse é o caso de "primeiro preenchimento": o sistema completa o único
     * valor que falta, nunca sobrescreve um valor que o usuário acabou de
     * editar/esvaziar. O preenchimento automático de consistência (quando os
     * três já estão preenchidos e um deles muda, podendo sobrescrever outro
     * já preenchido) não é o mesmo problema que calcularValorAusente resolve
     * — por isso não está aqui —, mas também é delegado ao piloto, em
     * resolverConsistenciaViaRelacaoEstruturalRica (fechado em 2026-08-06,
     * Fase B2 completa). O algoritmo genérico abaixo só continua ativo, para
     * os 6 tipos cobertos, nos casos que nenhum dos dois métodos ricos trata
     * (ex.: indiceAlterado fora de 0-2 com 2+ incógnitas) — e integralmente
     * para os 2 tipos "Em construção", que o piloto não cobre.
     *
     * Cobre os 3 tipos "Medidas" (Composição, Transformação, Comparação) e,
     * desde 2026-08-06, os 3 tipos "Relações" alcançáveis pela UI
     * (Composição de Transformações, Transformação de Relação, Composição
     * de Relações — ver RELATORIO_INVESTIGACAO_5_TIPOS_NAO_COBERTOS_2026-08-06.md).
     * Os 2 tipos restantes (Composição seguida de Transformação,
     * Transformação Composta em Dois Passos) ficam de fora: "Em construção"
     * no menu, nenhum caminho de UI os alcança hoje — sem urgência, sem
     * usuário para proteger.
     *
     * @return true se este caminho tratou a resolução (calculou e escreveu,
     *         ou não havia nada a calcular) — o chamador não deve rodar o
     *         algoritmo genérico por cima neste caso.
     */
    private boolean resolverViaRelacaoEstruturalRica(int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        if (tipo != TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                && tipo != TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                && tipo != TipoSituacaoAditiva.COMPARACAO_MEDIDAS
                && tipo != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                && tipo != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                && tipo != TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return false;
        }
        int indiceIncognita = -1;
        int quantidadeIncognitas = 0;
        for (int i = 0; i < 3; i++) {
            if (!conhecido(i)) {
                quantidadeIncognitas++;
                indiceIncognita = i;
            }
        }
        if (quantidadeIncognitas != 1) {
            return false;
        }
        if (indiceAlterado >= 0 && indiceAlterado <= 2 && indiceIncognita == indiceAlterado) {
            // O usuário acabou de tocar exatamente esta posição (ex.: apagou o
            // valor) — não é "preencher o que falta automaticamente", é o
            // próprio campo em edição. Preserva o comportamento atual: não
            // auto-preenche o campo que acabou de ser mexido.
            return false;
        }

        ResultadoCalculo resultado = calcularComRelacaoRica();
        if (resultado != null && resultado.temValorCalculavel()) {
            definirSePermitido(indiceIncognita,
                    resultado.getValorCalculado().valorOuNull().intValue(),
                    indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        }
        return true;
    }

    /**
     * Constrói os três papéis descartáveis (sem identidade persistente, sem
     * publicação de eventos — mesma "calculadora e se?" que este método já
     * era antes de ser extraído) que representam o tipo atual, posicionados
     * com os valores já conhecidos do estado compartilhado. Único ponto de
     * construção do trio, compartilhado entre o cálculo de "primeiro
     * preenchimento" (calcularValorAusenteDoTipo, via calcularComRelacaoRica)
     * e o de "recálculo de consistência" (recalcularParaConsistenciaDoTipo,
     * via resolverConsistenciaViaRelacaoEstruturalRica) — ver Fase B2
     * completa em TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md.
     *
     * @return os três papéis na ordem (índice 0, índice 1, índice 2), ou
     *         null se o tipo atual não é um dos 6 cobertos pela
     *         arquitetura rica do piloto
     */
    private PapelQuantitativo[] criarTrioDePapeis() {
        ValorNumerico v0 = valores[0];
        ValorNumerico v1 = valores[1];
        ValorNumerico v2 = valores[2];
        PublicadorEventoDominio semEventos = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo p0;
        PapelQuantitativo p1;
        PapelQuantitativo p2;
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            p0 = PapelQuantitativo.parte1(semEventos);
            p1 = PapelQuantitativo.parte2(semEventos);
            p2 = PapelQuantitativo.todo(semEventos);
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            p0 = FabricaPapeisTransformacaoMedidas.estadoInicial(semEventos);
            p1 = FabricaPapeisTransformacaoMedidas.transformacao(semEventos);
            p2 = FabricaPapeisTransformacaoMedidas.estadoFinal(semEventos);
        } else if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            p0 = FabricaPapeisComparacaoMedidas.referido(semEventos);
            p1 = FabricaPapeisComparacaoMedidas.valorRelativo(semEventos);
            p2 = FabricaPapeisComparacaoMedidas.referendo(semEventos);
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            p0 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(semEventos);
            p1 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(semEventos);
            p2 = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(semEventos);
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            p0 = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(semEventos);
            p1 = FabricaPapeisTransformacaoDeRelacao.transformacao(semEventos);
            p2 = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(semEventos);
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            p0 = FabricaPapeisComposicaoDeRelacoes.relacao1(semEventos);
            p1 = FabricaPapeisComposicaoDeRelacoes.relacao2(semEventos);
            p2 = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(semEventos);
        } else {
            return null; // inalcançável — já filtrado pelos chamadores
        }
        posicionarSeConhecido(p0, v0);
        posicionarSeConhecido(p1, v1);
        posicionarSeConhecido(p2, v2);
        return new PapelQuantitativo[] { p0, p1, p2 };
    }

    private ResultadoCalculo calcularComRelacaoRica() {
        PapelQuantitativo[] trio = criarTrioDePapeis();
        if (trio == null) {
            return null; // inalcançável — já filtrado em resolverViaRelacaoEstruturalRica
        }
        return calcularValorAusenteDoTipo(trio[0], trio[1], trio[2]);
    }

    private ResultadoCalculo calcularValorAusenteDoTipo(
            PapelQuantitativo p0, PapelQuantitativo p1, PapelQuantitativo p2) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return RelacaoEstruturalComposicao.composicaoDeMedidas().calcularValorAusente(p0, p1, p2);
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            return RelacaoEstruturalTransformacao.transformacaoDeMedidas().calcularValorAusente(p0, p1, p2);
        }
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            return RelacaoEstruturalComparacao.comparacaoDeMedidas().calcularValorAusente(p0, p1, p2);
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return RelacaoEstruturalComposicaoDeTransformacoes.composicaoDeTransformacoes()
                    .calcularValorAusente(p0, p1, p2);
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            return RelacaoEstruturalTransformacaoDeRelacao.transformacaoDeRelacao().calcularValorAusente(p0, p1, p2);
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return RelacaoEstruturalComposicaoDeRelacoes.composicaoDeRelacoes().calcularValorAusente(p0, p1, p2);
        }
        return null; // inalcançável — já filtrado pelos chamadores
    }

    /**
     * Recalcula um papel já conhecido para preservar a consistência da
     * relação aditiva quando os três papéis já estavam preenchidos e um
     * deles muda (ex.: o sujeito arrasta um valor já posicionado num
     * diagrama já completo) — delega às 6 classes RelacaoEstrutural* do
     * piloto (recalcularParaConsistencia), a capacidade que faltava no
     * Achado 2 de RELATORIO_INVESTIGACAO_FASE_B2_COMPLETA_2026-08-06.md e
     * foi fechada em 2026-08-06 (RELATORIO_RECALCULAR_PARA_CONSISTENCIA_2026-08-06.md).
     * Com esta chamada, resolverRelacaoAditiva passa a delegar 100% da
     * lógica de relação aditiva ao piloto para os 6 tipos cobertos — Main.java
     * não muda uma linha (já só chama atualizar(...) neste objeto), mas
     * tudo que acontece a partir daqui são objetos do piloto (Fase B2
     * completa, no espírito "Main só chama métodos de objetos que já
     * funcionam", em vez de reescrever os funis de escrita de Main.java).
     *
     * Cobre os mesmos 6 tipos que resolverViaRelacaoEstruturalRica; os 2
     * tipos "Em construção" continuam no algoritmo genérico abaixo, sem
     * cobertura no piloto.
     *
     * Só se aplica quando indiceAlterado é 0, 1 ou 2 (o algoritmo genérico
     * equivalente também só age nesses três casos — quando indiceAlterado
     * está fora desse intervalo, ou já foi tratado por
     * resolverViaRelacaoEstruturalRica, ou não há informação suficiente
     * sobre o que mudou) e os três papéis já estão conhecidos — se não, não
     * é "recálculo de consistência", é "primeiro preenchimento"
     * (resolverViaRelacaoEstruturalRica) ou estado ainda incompleto, e este
     * método não interfere.
     *
     * @return true se este caminho tratou a resolução (o chamador não deve
     *         rodar o algoritmo genérico por cima); false se o tipo não é
     *         coberto ou não é o caso de consistência
     */
    private boolean resolverConsistenciaViaRelacaoEstruturalRica(
            int indiceIncognitaProtegida, boolean permitirPreenchimentoIncognita) {
        if (tipo != TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                && tipo != TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                && tipo != TipoSituacaoAditiva.COMPARACAO_MEDIDAS
                && tipo != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                && tipo != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                && tipo != TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return false;
        }
        if (indiceAlterado < 0 || indiceAlterado > 2) {
            return false;
        }
        if (!conhecido(0) || !conhecido(1) || !conhecido(2)) {
            return false;
        }

        PapelQuantitativo[] trio = criarTrioDePapeis();
        if (trio == null) {
            return false; // inalcançável — já filtrado acima
        }
        PapelQuantitativo papelAlterado = trio[indiceAlterado];
        ResultadoCalculo resultado =
                recalcularParaConsistenciaDoTipo(trio[0], trio[1], trio[2], papelAlterado);
        if (resultado != null && resultado.temValorCalculavel()) {
            int indiceRecalculado = indiceDoPapel(trio, resultado.getPapelCalculado());
            if (indiceRecalculado >= 0) {
                definirSePermitido(indiceRecalculado,
                        resultado.getValorCalculado().valorOuNull().intValue(),
                        indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            }
        }
        return true;
    }

    private ResultadoCalculo recalcularParaConsistenciaDoTipo(PapelQuantitativo p0,
            PapelQuantitativo p1, PapelQuantitativo p2, PapelQuantitativo papelAlterado) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return RelacaoEstruturalComposicao.composicaoDeMedidas()
                    .recalcularParaConsistencia(p0, p1, p2, papelAlterado);
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            return RelacaoEstruturalTransformacao.transformacaoDeMedidas()
                    .recalcularParaConsistencia(p0, p1, p2, papelAlterado);
        }
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            return RelacaoEstruturalComparacao.comparacaoDeMedidas()
                    .recalcularParaConsistencia(p0, p1, p2, papelAlterado);
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return RelacaoEstruturalComposicaoDeTransformacoes.composicaoDeTransformacoes()
                    .recalcularParaConsistencia(p0, p1, p2, papelAlterado);
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            return RelacaoEstruturalTransformacaoDeRelacao.transformacaoDeRelacao()
                    .recalcularParaConsistencia(p0, p1, p2, papelAlterado);
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            return RelacaoEstruturalComposicaoDeRelacoes.composicaoDeRelacoes()
                    .recalcularParaConsistencia(p0, p1, p2, papelAlterado);
        }
        return null; // inalcançável — já filtrado pelos chamadores
    }

    private static int indiceDoPapel(PapelQuantitativo[] trio, PapelQuantitativo papel) {
        for (int i = 0; i < trio.length; i++) {
            if (trio[i] == papel) {
                return i;
            }
        }
        return -1;
    }

    private static void posicionarSeConhecido(PapelQuantitativo papel, ValorNumerico valor) {
        if (valor != null && valor.ehConhecido()) {
            papel.posicionar(valor, OrigemAcao.ORIGEM_SISTEMA, ContextoAcao.NAO_INFORMADO);
        }
    }

    private boolean conhecido(int indice) {
        return valores[indice] != null && valores[indice].ehConhecido();
    }

    private int valor(int indice) {
        Integer valor = valores[indice].valorOuNull();
        return valor == null ? 0 : valor.intValue();
    }


    /**
     * Soma protegida contra estouro de int (2026-08-06). Antes desta guarda,
     * 2000000000 + 2000000000 escrevia -294967296 no estado compartilhado e
     * as representações eram sincronizadas com esse número errado. Quando a
     * conta não é representável, nada é escrito — o valor anterior/ausente é
     * preservado, exatamente o mesmo tratamento que definir(...) já dava a um
     * valor inválido para o domínio do papel.
     *
     * O caminho da arquitetura rica (resolverViaRelacaoEstruturalRica) já está
     * protegido desde a guarda equivalente nas classes RelacaoEstrutural*:
     * calcularValorAusente devolve NAO_RESOLVIVEL_NESTE_ESTADO ao estourar, e
     * temValorCalculavel() falso impede a escrita. Esta guarda cobre o
     * algoritmo genérico abaixo — o de preenchimento automático de
     * consistência, que roda quando os três já estão preenchidos.
     */
    private void definirSomaSePermitido(int indice, int a, int b,
            int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        try {
            definirSePermitido(indice, Math.addExact(a, b),
                    indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        } catch (ArithmeticException estouro) {
            // Não representável: preserva o estado anterior sem publicar lixo.
        }
    }

    /** Subtração protegida contra estouro de int — ver definirSomaSePermitido. */
    private void definirSubtracaoSePermitido(int indice, int a, int b,
            int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        try {
            definirSePermitido(indice, Math.subtractExact(a, b),
                    indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        } catch (ArithmeticException estouro) {
            // Não representável: preserva o estado anterior sem publicar lixo.
        }
    }

    /**
     * Único ponto de escrita usado pela resolução automática da relação
     * aditiva (preenchimento do papel ausente e recálculo de consistência —
     * ver resolverRelacaoAditiva e as duas rotas ricas do piloto). Nunca é
     * chamado para o valor que o próprio usuário acabou de fornecer — esse é
     * escrito direto no laço de atualizar(), antes de resolverRelacaoAditiva
     * rodar. Por isso, uma mudança de valor aqui é, por construção, uma
     * resolução automática do sistema; registra o índice para
     * indiceResolvidoAutomaticamente cobrir os dois casos que interessam ao
     * log de produção — primeiro preenchimento (null→valor) e recálculo de
     * consistência (valor→outro valor) — sem precisar saber qual dos dois
     * caminhos chamou.
     */
    private void definirSePermitido(int indice, int valor,
            int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        if (indice == indiceIncognitaProtegida
                && !permitirPreenchimentoIncognita) {
            return;
        }
        Integer valorAntes = valores[indice] == null ? null : valores[indice].valorOuNull();
        definir(indice, valor);
        Integer valorDepois = valores[indice] == null ? null : valores[indice].valorOuNull();
        if (valorDepois != null && !valorDepois.equals(valorAntes)) {
            indiceResolvidoAutomaticamente = indice;
        }
    }
    private void definir(int indice, int valor) {
        try {
            valores[indice] = fabricaValores.conhecido(
                    dominioDoIndice(tipo, indice), valor);
        } catch (IllegalArgumentException ex) {
            // A relação matemática não pode converter uma medida em negativo.
            // O valor anterior/ausente é preservado sem publicar estado inválido.
        }
    }

    private ValorNumerico criarValorSeguro(DominioNumerico dominio,
                                            Integer valor,
                                            boolean conhecido) {
        try {
            return fabricaValores.criar(dominio, valor, conhecido);
        } catch (IllegalArgumentException ex) {
            return fabricaValores.desconhecido(dominio);
        }
    }

    private DominioNumerico dominioDoIndice(TipoSituacaoAditiva tipoAtual,
                                            int indice) {
        return esquemas.obter(tipoAtual).obterDominioCompartilhado(indice);
    }
}
