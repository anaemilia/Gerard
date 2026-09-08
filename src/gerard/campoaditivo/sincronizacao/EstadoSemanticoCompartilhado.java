package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.ValorNumerico;

/**
 * Estado semântico único compartilhado pelas representações manipuláveis.
 *
 * Os valores são objetos do domínio numérico. Assim, texto, Vergnaud, barras,
 * eixo e tabuleiro não decidem localmente se um valor aceita sinal.
 */
public final class EstadoSemanticoCompartilhado {
    private final ConversorValoresEstadoAditivo conversorValores =
            new ConversorValoresEstadoAditivo();
    private final ResolvedorRelacoesEstruturaisAditivas resolvedorRelacoes =
            new ResolvedorRelacoesEstruturaisAditivas();

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

    /**
     * Simula a alteração de um papel sobre o estado corrente sem publicá-la.
     * A própria fronteira que possui os valores e seus domínios prepara a
     * tentativa; adaptadores de interface não precisam copiar o snapshot para
     * reconstruir essa decisão.
     */
    public synchronized boolean tentativaPreservaDominios(
            int indicePapel, Integer valorProposto) {
        if (indicePapel < 0 || indicePapel > 2) {
            return true;
        }
        ValorNumerico[] atuais = new ValorNumerico[] {
            valores[0], valores[1], valores[2]
        };
        return resolvedorRelacoes.tentativaPreservaDominios(
                tipo, atuais, indicePapel, valorProposto);
    }

    /**
     * Projeta os três papéis canônicos para consumidores visuais que operam
     * com quantidades concretas. Um estado de outra categoria, ou um papel
     * ainda desconhecido, é representado por zero sem expor o armazenamento
     * interno ao adaptador.
     */
    public synchronized int[] valoresOuZeroPara(TipoSituacaoAditiva tipoEsperado) {
        if (tipo != tipoEsperado) {
            return new int[] {0, 0, 0};
        }
        return new int[] {
            valorOuZero(valores[0]),
            valorOuZero(valores[1]),
            valorOuZero(valores[2])
        };
    }

    private static int valorOuZero(ValorNumerico valor) {
        Integer inteiro = valor == null ? null : valor.valorOuNull();
        return inteiro == null ? 0 : inteiro.intValue();
    }

    public synchronized void limpar(TipoSituacaoAditiva novoTipo) {
        tipo = novoTipo;
        for (int i = 0; i < valores.length; i++) {
            valores[i] = conversorValores.desconhecido(novoTipo, i);
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
            valores[i] = conversorValores.normalizarEntrada(
                    tipo, i, bruto, conhecido);
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
        ResolvedorRelacoesEstruturaisAditivas.ResolucaoAutomatica resolucao =
                resolvedorRelacoes.resolver(tipo, valores, indiceAlterado);
        if (resolucao.foiResolvida()) {
            definirSePermitido(resolucao.getIndice(), resolucao.getValor(),
                    indiceIncognitaProtegida, permitirPreenchimentoIncognita);
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
        ValorNumerico calculado = conversorValores.criarCalculadoOuNull(
                tipo, indice, valor);
        if (calculado != null) {
            valores[indice] = calculado;
        }
        Integer valorDepois = valores[indice] == null ? null : valores[indice].valorOuNull();
        if (valorDepois != null && !valorDepois.equals(valorAntes)) {
            indiceResolvidoAutomaticamente = indice;
        }
    }
}
