package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;
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

        private Snapshot(TipoSituacaoAditiva tipo, ValorNumerico[] valores,
                int indiceAlterado, Origem origem, long versao) {
            this.tipo = tipo;
            this.valores = valores;
            this.indiceAlterado = indiceAlterado;
            this.origem = origem;
            this.versao = versao;
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
    }

    private TipoSituacaoAditiva tipo;
    private final ValorNumerico[] valores = new ValorNumerico[3];
    private int indiceAlterado = -1;
    private Origem origem = Origem.INICIALIZACAO;
    private long versao = 0L;

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
        resolverRelacaoAditiva(indiceIncognitaProtegida,
                permitirPreenchimentoIncognita);
        versao++;
        return snapshot();
    }

    public synchronized Snapshot snapshot() {
        return new Snapshot(tipo,
                new ValorNumerico[] { valores[0], valores[1], valores[2] },
                indiceAlterado, origem, versao);
    }

    private void resolverRelacaoAditiva(int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        if (indiceAlterado == 0) {
            if (conhecido(0) && conhecido(1)) {
                definirSePermitido(2, valor(0) + valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            } else if (conhecido(0) && conhecido(2)) {
                definirSePermitido(1, valor(2) - valor(0), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            }
            return;
        }
        if (indiceAlterado == 1) {
            if (conhecido(0) && conhecido(1)) {
                definirSePermitido(2, valor(0) + valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            } else if (conhecido(1) && conhecido(2)) {
                definirSePermitido(0, valor(2) - valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            }
            return;
        }
        if (indiceAlterado == 2) {
            if (conhecido(0) && conhecido(2)) {
                definirSePermitido(1, valor(2) - valor(0), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
            } else if (conhecido(1) && conhecido(2)) {
                definirSePermitido(0, valor(2) - valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
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
            definirSePermitido(0, valor(2) - valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        } else if (indiceFaltante == 1 && conhecido(0) && conhecido(2)) {
            definirSePermitido(1, valor(2) - valor(0), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        } else if (indiceFaltante == 2 && conhecido(0) && conhecido(1)) {
            definirSePermitido(2, valor(0) + valor(1), indiceIncognitaProtegida, permitirPreenchimentoIncognita);
        }
    }

    private boolean conhecido(int indice) {
        return valores[indice] != null && valores[indice].ehConhecido();
    }

    private int valor(int indice) {
        Integer valor = valores[indice].valorOuNull();
        return valor == null ? 0 : valor.intValue();
    }


    private void definirSePermitido(int indice, int valor,
            int indiceIncognitaProtegida,
            boolean permitirPreenchimentoIncognita) {
        if (indice == indiceIncognitaProtegida
                && !permitirPreenchimentoIncognita) {
            return;
        }
        definir(indice, valor);
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
