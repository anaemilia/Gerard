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
        if (resolverViaRelacaoEstruturalRica(indiceIncognitaProtegida, permitirPreenchimentoIncognita)) {
            return;
        }
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
     * já preenchido) continua no algoritmo genérico abaixo, inalterado — não
     * é o mesmo problema que calcularValorAusente resolve, e forçar os dois
     * no mesmo contrato mudaria comportamento hoje em produção.
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

    private ResultadoCalculo calcularComRelacaoRica() {
        ValorNumerico v0 = valores[0];
        ValorNumerico v1 = valores[1];
        ValorNumerico v2 = valores[2];
        PublicadorEventoDominio semEventos = PublicadorEventoDominio.NENHUM;

        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            PapelQuantitativo parte1 = PapelQuantitativo.parte1(semEventos);
            PapelQuantitativo parte2 = PapelQuantitativo.parte2(semEventos);
            PapelQuantitativo todo = PapelQuantitativo.todo(semEventos);
            posicionarSeConhecido(parte1, v0);
            posicionarSeConhecido(parte2, v1);
            posicionarSeConhecido(todo, v2);
            return RelacaoEstruturalComposicao.composicaoDeMedidas()
                    .calcularValorAusente(parte1, parte2, todo);
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            PapelQuantitativo estadoInicial = FabricaPapeisTransformacaoMedidas.estadoInicial(semEventos);
            PapelQuantitativo transformacao = FabricaPapeisTransformacaoMedidas.transformacao(semEventos);
            PapelQuantitativo estadoFinal = FabricaPapeisTransformacaoMedidas.estadoFinal(semEventos);
            posicionarSeConhecido(estadoInicial, v0);
            posicionarSeConhecido(transformacao, v1);
            posicionarSeConhecido(estadoFinal, v2);
            return RelacaoEstruturalTransformacao.transformacaoDeMedidas()
                    .calcularValorAusente(estadoInicial, transformacao, estadoFinal);
        }
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            PapelQuantitativo referido = FabricaPapeisComparacaoMedidas.referido(semEventos);
            PapelQuantitativo valorRelativo = FabricaPapeisComparacaoMedidas.valorRelativo(semEventos);
            PapelQuantitativo referendo = FabricaPapeisComparacaoMedidas.referendo(semEventos);
            posicionarSeConhecido(referido, v0);
            posicionarSeConhecido(valorRelativo, v1);
            posicionarSeConhecido(referendo, v2);
            return RelacaoEstruturalComparacao.comparacaoDeMedidas()
                    .calcularValorAusente(referido, valorRelativo, referendo);
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            PapelQuantitativo transformacao1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(semEventos);
            PapelQuantitativo transformacao2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(semEventos);
            PapelQuantitativo transformacaoFinal = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(semEventos);
            posicionarSeConhecido(transformacao1, v0);
            posicionarSeConhecido(transformacao2, v1);
            posicionarSeConhecido(transformacaoFinal, v2);
            return RelacaoEstruturalComposicaoDeTransformacoes.composicaoDeTransformacoes()
                    .calcularValorAusente(transformacao1, transformacao2, transformacaoFinal);
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            PapelQuantitativo relacaoInicial = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(semEventos);
            PapelQuantitativo transformacao = FabricaPapeisTransformacaoDeRelacao.transformacao(semEventos);
            PapelQuantitativo relacaoFinal = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(semEventos);
            posicionarSeConhecido(relacaoInicial, v0);
            posicionarSeConhecido(transformacao, v1);
            posicionarSeConhecido(relacaoFinal, v2);
            return RelacaoEstruturalTransformacaoDeRelacao.transformacaoDeRelacao()
                    .calcularValorAusente(relacaoInicial, transformacao, relacaoFinal);
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            PapelQuantitativo relacao1 = FabricaPapeisComposicaoDeRelacoes.relacao1(semEventos);
            PapelQuantitativo relacao2 = FabricaPapeisComposicaoDeRelacoes.relacao2(semEventos);
            PapelQuantitativo relacaoFinal = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(semEventos);
            posicionarSeConhecido(relacao1, v0);
            posicionarSeConhecido(relacao2, v1);
            posicionarSeConhecido(relacaoFinal, v2);
            return RelacaoEstruturalComposicaoDeRelacoes.composicaoDeRelacoes()
                    .calcularValorAusente(relacao1, relacao2, relacaoFinal);
        }
        return null; // inalcançável — já filtrado em resolverViaRelacaoEstruturalRica
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
