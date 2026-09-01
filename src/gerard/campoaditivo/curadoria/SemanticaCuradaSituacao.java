package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.TentativaEscolhaSinalPapelQuantitativo;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.ConversorTextoParaInteiroSemantico;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.papel.CatalogoPapeisSemanticos;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fonte única dos papéis semânticos associados a uma situação curada.
 *
 * Os nomes formais dos papéis são localizados pela interface, enquanto valores,
 * sinais, termo desconhecido e participantes/objetos são obtidos exclusivamente
 * dos campos da curadoria. Nenhuma associação específica do enunciado deve ser
 * codificada diretamente na tela ou nos renderizadores.
 */
public final class SemanticaCuradaSituacao {

    private static final CatalogoPapeisSemanticos CATALOGO_PAPEIS =
            new CatalogoPapeisSemanticos();
    private static final ConversorTextoParaInteiroSemantico CONVERSOR_INTEIRO =
            new ConversorTextoParaInteiroSemantico();

    public static final class PapelCurado {
        private final String chave;
        private final String rotulo;
        private final String valor;
        private final String participante;
        private final boolean desconhecido;

        PapelCurado(String chave, String rotulo, String valor, String participante, boolean desconhecido) {
            this.chave = limpar(chave);
            this.rotulo = limpar(rotulo);
            this.valor = limpar(valor);
            this.participante = limpar(participante);
            this.desconhecido = desconhecido;
        }

        public String getChave() { return chave; }
        public String getRotulo() { return rotulo; }
        public String getValor() { return valor; }
        public String getParticipante() { return participante; }
        public boolean isDesconhecido() { return desconhecido; }

        /**
         * O próprio papel curado responde se a modelagem pode cobrá-lo do
         * aluno — localidade do conhecimento: quem sabe se existe curadoria
         * para este papel é o papel, não a tela que o desenha.
         *
         * É exigível quando a curadoria de fato o definiu: ou tem valor, ou
         * é a incógnita declarada (cujo campo fica vazio de propósito, para
         * o aluno preencher). Um papel sem valor e que não é a incógnita não
         * foi curado — cobrá-lo tornaria a situação impossível de concluir,
         * já que não há nada para o aluno colocar ali nem com o que conferir.
         *
         * Isso mantém a regra geral (2026-08-23): a conclusão cobra os
         * papéis que a curadoria definiu, não os que a cena desenha. Assim,
         * quando a curadoria de uma categoria for completada, os papéis
         * novos passam a ser exigidos sozinhos, sem mudar código.
         */
        public boolean isExigidoNaModelagem() {
            return desconhecido || valor.length() > 0;
        }

        /** Valor numérico curado deste papel, ou null quando não materializável. */
        public Integer getValorInteiro() {
            return CONVERSOR_INTEIRO.converter(valor);
        }

        /**
         * Compara um valor materializado pela atividade com o valor deste
         * papel na curadoria. A ausência de qualquer um dos dois valores
         * numéricos produz {@code null}: não há base factual para afirmar que
         * o estado foi ou não modificado.
         */
        public Boolean estadoModificadoPor(String valorAtual) {
            Integer valorCurado = getValorInteiro();
            Integer atual = CONVERSOR_INTEIRO.converter(valorAtual);
            if (valorCurado == null || atual == null) {
                return null;
            }
            return Boolean.valueOf(atual.intValue() != valorCurado.intValue());
        }
    }

    private SemanticaCuradaSituacao() {
    }

    public static List<PapelCurado> mapear(SituacaoProblemaAditiva situacao, ServicoLocalizacao localizacao) {
        if (situacao == null) {
            return Collections.emptyList();
        }
        ServicoLocalizacao loc = localizacao == null ? ServicoLocalizacao.getInstancia() : localizacao;
        List<PapelCurado> papeis = new ArrayList<PapelCurado>();
        ResolvedorIncognitaCurada.Resultado resolucaoIncognita =
                new ResolvedorIncognitaCurada().resolver(situacao);
        String desconhecido = resolucaoIncognita.getChaveEfetiva();
        TipoSituacaoAditiva tipo = situacao.getTipo();

        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            adicionar(papeis, loc, "papel.parte1", situacao.getQuantidade1(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.parte2", situacao.getQuantidade2(), situacao.getPersonagem2(), desconhecido);
            adicionar(papeis, loc, "papel.todo", situacao.getResultado(), situacao.getPersonagem3(), desconhecido);
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            adicionar(papeis, loc, "papel.estadoInicial", situacao.getEstadoInicial(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.transformacao", aplicarSinal(situacao.getTransformacao(), situacao.getSinalTransformacao()), "", desconhecido);
            adicionar(papeis, loc, "papel.estadoFinal", situacao.getEstadoFinal(), situacao.getPersonagem2(), desconhecido);
        } else if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            adicionar(papeis, loc, "papel.referido", primeiroNaoVazio(situacao.getReferido(), situacao.getQuantidade2()), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.diferenca", primeiroNaoVazio(aplicarSinal(situacao.getValorRelativo(), situacao.getSinalValorRelativo()), situacao.getResultado()), "", desconhecido);
            adicionar(papeis, loc, "papel.referendo", primeiroNaoVazio(situacao.getReferendo(), situacao.getQuantidade1()), situacao.getPersonagem2(), desconhecido);
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            adicionar(papeis, loc, "papel.transformacao1", situacao.getQuantidade1(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.transformacao2", situacao.getQuantidade2(), situacao.getPersonagem2(), desconhecido);
            adicionar(papeis, loc, "papel.transformacaoFinal", situacao.getResultado(), situacao.getPersonagem3(), desconhecido);
            // Os 3 estados também são papéis semânticos (2026-08-23, pedido
            // da usuária: "deixe todos os elementos como elementos
            // semânticos"). Ficam DEPOIS das 3 transformações de propósito:
            // aplicarRotulos() abaixo usa papeis.get(0/1/2) para rotular os
            // 3 círculos de transformação da cena, então a ordem dos três
            // primeiros não pode mudar.
            adicionar(papeis, loc, "papel.estadoInicial", situacao.getEstadoInicial(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.estadoIntermediario", situacao.getEstadoIntermediario(), "", desconhecido);
            adicionar(papeis, loc, "papel.estadoFinal", situacao.getEstadoFinal(), situacao.getPersonagem3(), desconhecido);
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            adicionar(papeis, loc, "papel.relacaoInicial", situacao.getEstadoInicial(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.transformacao", aplicarSinal(situacao.getTransformacao(), situacao.getSinalTransformacao()), "", desconhecido);
            adicionar(papeis, loc, "papel.relacaoFinal", situacao.getEstadoFinal(), situacao.getPersonagem2(), desconhecido);
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            adicionar(papeis, loc, "papel.relacao1", situacao.getQuantidade1(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.relacao2", situacao.getQuantidade2(), situacao.getPersonagem2(), desconhecido);
            adicionar(papeis, loc, "papel.relacaoFinal", situacao.getResultado(), situacao.getPersonagem3(), desconhecido);
        }
        return papeis;
    }

    public static PapelCurado buscar(SituacaoProblemaAditiva situacao, ServicoLocalizacao localizacao, String chave) {
        String procurada = limpar(chave);
        for (PapelCurado papel : mapear(situacao, localizacao)) {
            if (procurada.equals(papel.getChave())) {
                return papel;
            }
        }
        return null;
    }

    /**
     * Cria os proprietários de avaliação de sinal para os papéis inteiros que
     * possuem valor normativo na situação curada.
     *
     * <p>A curadoria apenas converte seus dados em objetos de domínio. A
     * comparação posterior pertence ao papel e ao {@link NumeroInteiro}; a
     * interface não recebe nem interpreta o valor esperado. Papéis sem valor
     * numérico, inclusive a incógnita marcada com {@code ?}, permanecem sem
     * critério, preservando o comportamento atual.</p>
     */
    public static Map<String, TentativaEscolhaSinalPapelQuantitativo>
            criarTentativasEscolhaSinal(
                    SituacaoProblemaAditiva situacao,
                    ServicoLocalizacao localizacao) {
        if (situacao == null || situacao.getTipo() == null) {
            return Collections.emptyMap();
        }
        String situacaoGrupoId = primeiroNaoVazio(
                situacao.getSituacaoGrupoId(), situacao.getId());
        if (situacaoGrupoId.length() == 0) {
            return Collections.emptyMap();
        }
        Map<String, TentativaEscolhaSinalPapelQuantitativo> tentativas =
                new LinkedHashMap<String, TentativaEscolhaSinalPapelQuantitativo>();
        for (PapelCurado papel : mapear(situacao, localizacao)) {
            if (!CATALOGO_PAPEIS.papelPermiteSinal(papel.getChave())) {
                continue;
            }
            NumeroInteiro numero = converterNumeroInteiroCurado(
                    papel.getValor(), situacao);
            if (numero != null) {
                tentativas.put(papel.getChave(),
                        new TentativaEscolhaSinalPapelQuantitativo(
                                situacaoGrupoId, situacao.getTipo(),
                                papel.getChave(), numero));
            }
        }
        return Collections.unmodifiableMap(tentativas);
    }

    /**
     * A modelagem pode cobrar este papel do aluno? Delega a decisão ao
     * próprio papel curado (ver PapelCurado.isExigidoNaModelagem) — quem
     * pergunta não inspeciona campo nenhum da situação.
     *
     * Devolve false quando não há situação curada com esse papel; cabe a
     * quem chama decidir o que fazer nesse caso (ver
     * Main.papelValidoParaConclusao, que preserva o comportamento anterior
     * quando não há curadoria alguma carregada).
     */
    public static boolean papelExigidoNaModelagem(SituacaoProblemaAditiva situacao,
            ServicoLocalizacao localizacao, String chave) {
        PapelCurado papel = buscar(situacao, localizacao, chave);
        return papel != null && papel.isExigidoNaModelagem();
    }

    /**
     * Aplica à definição visual os papéis que correspondem à categoria curada.
     * A geometria continua sendo definida pelo modelo formal; apenas a fonte dos
     * papéis apresentados é centralizada na semântica da curadoria.
     */
    public static DefinicaoDiagramaAditivo aplicarRotulos(
            DefinicaoDiagramaAditivo base,
            SituacaoProblemaAditiva situacao,
            ServicoLocalizacao localizacao) {
        if (base == null || situacao == null) {
            return base;
        }
        ServicoLocalizacao loc = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        List<PapelCurado> papeis = mapear(situacao, loc);
        if (papeis.isEmpty()) {
            return base;
        }
        String rotulo1 = base.getRotulo1();
        String rotulo2 = base.getRotulo2();
        String rotulo3 = base.getRotulo3();

        // Regra da usuária (2026-08-23): "quadrado é estado, inicial,
        // intermediário e final. Círculo é transformação, primeira e
        // segunda [e a resultante]." Em Composição de Transformações, os 3
        // papéis aqui (rotulo1/2/3) vão para as 3 figuras-círculo (t1, t2,
        // tr — ver RenderizadorComposicaoTransformacoes); os rótulos dos 3
        // quadrados de estado (inicial/intermediário/final) são fixos e
        // aplicados diretamente pelo próprio renderizador, não por aqui.
        // Havia um desvio aqui que rotulava os círculos com "Estado
        // inicial"/"Transformação 1"/"Estado intermediário" — misturava as
        // duas famílias de papel na figura errada. papeis (de mapear())
        // já traz os 3 rótulos corretos de transformação para esta
        // categoria, então basta usar o mesmo caminho das demais.
        if (papeis.size() >= 3) {
            rotulo1 = papeis.get(0).getRotulo();
            rotulo2 = papeis.get(1).getRotulo();
            rotulo3 = papeis.get(2).getRotulo();
        }

        return new DefinicaoDiagramaAditivo(
                base.getTitulo(),
                rotulo1,
                rotulo2,
                rotulo3);
    }

    public static String chaveTermoDesconhecido(String termo, TipoSituacaoAditiva tipo) {
        String chave = new ResolvedorIncognitaCurada()
                .chaveSemanticaDoTermo(termo, tipo);
        return chave.length() == 0 ? null : chave;
    }

    private static void adicionar(List<PapelCurado> papeis, ServicoLocalizacao loc,
                                  String chave, String valor, String participante,
                                  String chaveDesconhecido) {
        boolean marcadoNoValor = gerard.interpretacao.simbolo.SimboloDesconhecido.eh(valor);
        papeis.add(new PapelCurado(chave, loc.texto(chave), valor, participante,
                marcadoNoValor || (chave != null && chave.equals(chaveDesconhecido))));
    }

    private static String aplicarSinal(String valor, String sinal) {
        String v = limpar(valor).trim();
        if (v.length() == 0 || v.startsWith("+") || v.startsWith("-") || "?".equals(v)) {
            return v;
        }
        String s = normalizar(sinal);
        if ("negativo".equals(s) || limpar(sinal).trim().startsWith("-")) {
            return "-" + v;
        }
        if ("positivo".equals(s) || limpar(sinal).trim().startsWith("+")) {
            return "+" + v;
        }
        return v;
    }

    private static String primeiroNaoVazio(String principal, String alternativa) {
        String p = limpar(principal).trim();
        return p.length() > 0 ? p : limpar(alternativa).trim();
    }

    private static NumeroInteiro converterNumeroInteiroCurado(String valor,
            SituacaoProblemaAditiva situacao) {
        String texto = limpar(valor).trim();
        if (texto.length() == 0 || "?".equals(texto)) {
            return null;
        }
        Integer inteiro = CONVERSOR_INTEIRO.converter(texto, situacao);
        return inteiro == null ? null : new NumeroInteiro(inteiro.intValue());
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto;
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(limpar(texto), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "");
    }
}
