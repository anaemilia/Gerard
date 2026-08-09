package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;
import gerard.dominio.campoaditivo.ResultadoCalculo;
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
    private final CatalogoRelacoesEstruturaisAditivas catalogoRelacoes =
            new CatalogoRelacoesEstruturaisAditivas();

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
        resolverConsistenciaViaRelacaoEstruturalRica(
                indiceIncognitaProtegida, permitirPreenchimentoIncognita);
    }

    /**
     * Delega o cálculo aritmético ao catálogo das relações estruturais
     * canônicas — ver
     * TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md, Fase B,
     * opção B1) quando exatamente um dos três papéis está incógnito e essa
     * Esse é o caso de preenchimento do único valor ausente. O preenchimento
     * automático de consistência (quando os
     * três já estão preenchidos e um deles muda, podendo sobrescrever outro
     * já preenchido) não é o mesmo problema que calcularValorAusente resolve
     * — por isso não está aqui —, mas também é delegado ao piloto, em
     * resolverConsistenciaViaRelacaoEstruturalRica (fechado em 2026-08-06,
     * Fase B2 completa). Desde a Fase 2.4, esses dois caminhos estruturais são
     * a única implementação da resolução aditiva.
     *
     * Cobre os 3 tipos "Medidas" (Composição, Transformação, Comparação) e,
     * desde 2026-08-06, os 3 tipos "Relações" alcançáveis pela UI
     * (Composição de Transformações, Transformação de Relação, Composição
     * de Relações — ver RELATORIO_INVESTIGACAO_5_TIPOS_NAO_COBERTOS_2026-08-06.md).
     * @return true se este caminho tratou a resolução (calculou e escreveu,
     *         ou não havia nada a calcular).
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
        CatalogoRelacoesEstruturaisAditivas.RelacaoContextualizada contexto =
                catalogoRelacoes.criar(tipo, valores);
        ResultadoCalculo resultado = contexto == null
                ? null : contexto.calcularValorAusente();
        if (resultado != null && resultado.temValorCalculavel()) {
            definirSePermitido(indiceIncognita,
                    resultado.getValorCalculado().valorOuNull().intValue(),
                    indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        }
        return true;
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
     * Só se aplica quando indiceAlterado é 0, 1 ou 2. Quando indiceAlterado
     * está fora desse intervalo, ou já foi tratado por
     * resolverViaRelacaoEstruturalRica, ou não há informação suficiente
     * sobre o que mudou) e os três papéis já estão conhecidos — se não, não
     * é "recálculo de consistência", é "primeiro preenchimento"
     * (resolverViaRelacaoEstruturalRica) ou estado ainda incompleto, e este
     * método não interfere.
     *
     * @return true se este caminho tratou a resolução; false se o tipo não
     *         é coberto ou não é o caso de consistência
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

        CatalogoRelacoesEstruturaisAditivas.RelacaoContextualizada contexto =
                catalogoRelacoes.criar(tipo, valores);
        if (contexto == null) {
            return false; // inalcançável — já filtrado acima
        }
        ResultadoCalculo resultado = contexto.recalcularParaConsistencia(indiceAlterado);
        if (resultado != null && resultado.temValorCalculavel()) {
            int indiceRecalculado = contexto.indiceDoPapel(resultado.getPapelCalculado());
            if (indiceRecalculado >= 0) {
                definirSePermitido(indiceRecalculado,
                        resultado.getValorCalculado().valorOuNull().intValue(),
                        indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            }
        }
        return true;
    }

    private boolean conhecido(int indice) {
        return valores[indice] != null && valores[indice].ehConhecido();
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
