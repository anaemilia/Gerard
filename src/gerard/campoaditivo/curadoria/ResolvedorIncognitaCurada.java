package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Resolve a incógnita exclusivamente a partir dos metadados curados.
 *
 * Há duas formas curadas equivalentes de declarar o item desconhecido:
 * 1) selecionar o campo termo_desconhecido;
 * 2) registrar o símbolo "?" no valor do papel correspondente.
 *
 * O símbolo efetivamente armazenado no papel tem prioridade operacional, pois
 * impede que "?" seja tratado como valor conhecido. Divergências entre as duas
 * formas são publicadas para validação da curadoria, nunca corrigidas por
 * inferência linguística do enunciado.
 */
public final class ResolvedorIncognitaCurada {

    public static final class Resultado {
        private final String chaveExplicita;
        private final String chaveEfetiva;
        private final String termoCuradoriaEfetivo;
        private final List<String> chavesMarcadasComInterrogacao;
        private final boolean conflito;

        Resultado(String chaveExplicita, String chaveEfetiva,
                String termoCuradoriaEfetivo,
                List<String> chavesMarcadasComInterrogacao,
                boolean conflito) {
            this.chaveExplicita = limpar(chaveExplicita);
            this.chaveEfetiva = limpar(chaveEfetiva);
            this.termoCuradoriaEfetivo = limpar(termoCuradoriaEfetivo);
            this.chavesMarcadasComInterrogacao = Collections.unmodifiableList(
                    new ArrayList<String>(chavesMarcadasComInterrogacao));
            this.conflito = conflito;
        }

        public String getChaveExplicita() { return chaveExplicita; }
        public String getChaveEfetiva() { return chaveEfetiva; }
        public String getTermoCuradoriaEfetivo() { return termoCuradoriaEfetivo; }
        public List<String> getChavesMarcadasComInterrogacao() {
            return chavesMarcadasComInterrogacao;
        }
        public boolean possuiIncognita() { return chaveEfetiva.length() > 0; }
        public boolean possuiUmaUnicaInterrogacao() {
            return chavesMarcadasComInterrogacao.size() == 1;
        }
        public boolean possuiMultiplasInterrogacoes() {
            return chavesMarcadasComInterrogacao.size() > 1;
        }
        public boolean possuiConflito() { return conflito; }

        public String mensagemInconsistencia() {
            if (possuiMultiplasInterrogacoes()) {
                return "Há mais de um papel curado com o símbolo ?. "
                        + "Mantenha uma única incógnita: "
                        + chavesMarcadasComInterrogacao;
            }
            if (conflito) {
                return "O campo termo_desconhecido indica " + chaveExplicita
                        + ", mas o símbolo ? está em " + chaveEfetiva + ".";
            }
            return "";
        }
    }

    public Resultado resolver(SituacaoProblemaAditiva situacao) {
        if (situacao == null) {
            return vazio();
        }
        return resolver(situacao.getTipo(), situacao.getTermoDesconhecido(),
                situacao.getEstadoInicial(), situacao.getTransformacao(),
                situacao.getEstadoFinal(), situacao.getQuantidade1(),
                situacao.getQuantidade2(), situacao.getResultado(),
                situacao.getReferido(), situacao.getReferendo(),
                situacao.getValorRelativo());
    }

    public Resultado resolver(TipoSituacaoAditiva tipo, String termoDesconhecido,
            String estadoInicial, String transformacao, String estadoFinal,
            String quantidade1, String quantidade2, String resultado,
            String referido, String referendo, String valorRelativo) {
        String explicita = chaveSemanticaDoTermo(termoDesconhecido, tipo);
        List<Marcacao> marcacoes = marcacoesDaCategoria(tipo,
                estadoInicial, transformacao, estadoFinal,
                quantidade1, quantidade2, resultado,
                referido, referendo, valorRelativo);
        Set<String> unicas = new LinkedHashSet<String>();
        for (Marcacao marcacao : marcacoes) {
            if (SimboloDesconhecido.eh(marcacao.valor)) {
                unicas.add(marcacao.chave);
            }
        }
        List<String> marcadas = new ArrayList<String>(unicas);

        String efetiva = explicita;
        boolean conflito = false;
        if (marcadas.size() == 1) {
            efetiva = marcadas.get(0);
            conflito = explicita.length() > 0 && !explicita.equals(efetiva);
        } else if (marcadas.size() > 1) {
            if (explicita.length() > 0 && marcadas.contains(explicita)) {
                efetiva = explicita;
            } else {
                efetiva = marcadas.get(0);
            }
            conflito = true;
        }

        return new Resultado(explicita, efetiva,
                termoCuradoriaDaChave(efetiva, tipo), marcadas, conflito);
    }

    public String chaveSemanticaDoTermo(String termo,
            TipoSituacaoAditiva tipo) {
        String t = normalizar(termo);
        if (t.length() == 0 || tipo == null) {
            return "";
        }
        switch (tipo) {
            case COMPOSICAO_MEDIDAS:
                if (eh(t, "parte1", "quantidade1", "q1")) return "papel.parte1";
                if (eh(t, "parte2", "quantidade2", "q2")) return "papel.parte2";
                if (eh(t, "todo", "total", "resultado")) return "papel.todo";
                break;
            case TRANSFORMACAO_MEDIDAS:
                if (eh(t, "estadoinicial", "inicial")) return "papel.estadoInicial";
                if (eh(t, "transformacao", "mudanca")) return "papel.transformacao";
                if (eh(t, "estadofinal", "final", "resultado")) return "papel.estadoFinal";
                break;
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                if (eh(t, "parte1", "quantidade1", "q1")) return "papel.parte1";
                if (eh(t, "parte2", "quantidade2", "q2")) return "papel.parte2";
                if (eh(t, "todo", "total", "resultado")) return "papel.todo";
                if (eh(t, "transformacao", "mudanca")) return "papel.transformacao";
                if (eh(t, "estadofinal", "final")) return "papel.estadoFinal";
                break;
            case COMPARACAO_MEDIDAS:
                if (eh(t, "referido")) return "papel.referido";
                if (eh(t, "referendo", "referente")) return "papel.referendo";
                if (eh(t, "diferenca", "valorrelativo", "resultado")) return "papel.diferenca";
                break;
            case COMPOSICAO_TRANSFORMACOES:
                if (eh(t, "transformacao1", "quantidade1", "q1")) return "papel.transformacao1";
                if (eh(t, "transformacao2", "quantidade2", "q2")) return "papel.transformacao2";
                if (eh(t, "transformacaofinal", "transformacaoresultante",
                        "resultado", "total")) return "papel.transformacaoFinal";
                break;
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                if (eh(t, "estadoinicial", "inicial")) return "papel.estadoInicial";
                if (eh(t, "transformacao1", "quantidade1", "q1")) return "papel.transformacao1";
                if (eh(t, "transformacao2", "quantidade2", "q2")) return "papel.transformacao2";
                if (eh(t, "estadofinal", "final", "resultado")) return "papel.estadoFinal";
                break;
            case TRANSFORMACAO_RELACAO:
                if (eh(t, "relacaoinicial", "estadoinicial", "inicial")) return "papel.relacaoInicial";
                if (eh(t, "transformacao", "mudanca")) return "papel.transformacao";
                if (eh(t, "relacaofinal", "estadofinal", "final", "resultado")) return "papel.relacaoFinal";
                break;
            case COMPOSICAO_RELACOES:
                if (eh(t, "relacao1", "quantidade1", "q1")) return "papel.relacao1";
                if (eh(t, "relacao2", "quantidade2", "q2")) return "papel.relacao2";
                if (eh(t, "relacaofinal", "relacaoresultante", "resultado", "total")) return "papel.relacaoFinal";
                break;
            default:
                break;
        }
        return "";
    }

    public String termoCuradoriaDaChave(String chave,
            TipoSituacaoAditiva tipo) {
        String c = limpar(chave);
        if (c.length() == 0) return "";
        if ("papel.parte1".equals(c)) return "parte_1";
        if ("papel.parte2".equals(c)) return "parte_2";
        if ("papel.todo".equals(c)) return "todo";
        if ("papel.estadoInicial".equals(c)) return "estado_inicial";
        if ("papel.estadoFinal".equals(c)) return "estado_final";
        if ("papel.transformacao".equals(c)) return "transformação";
        if ("papel.transformacao1".equals(c)) return "transformacao_1";
        if ("papel.transformacao2".equals(c)) return "transformacao_2";
        if ("papel.transformacaoFinal".equals(c)) return "transformacao_resultante";
        if ("papel.referido".equals(c)) return "referido";
        if ("papel.referendo".equals(c)) return "referendo";
        if ("papel.diferenca".equals(c)) return "valor_relativo";
        if ("papel.relacaoInicial".equals(c)) return "relacao_inicial";
        if ("papel.relacao1".equals(c)) return "relacao_1";
        if ("papel.relacao2".equals(c)) return "relacao_2";
        if ("papel.relacaoFinal".equals(c)) {
            return tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES
                    ? "relacao_resultante" : "relacao_final";
        }
        return "";
    }

    private List<Marcacao> marcacoesDaCategoria(TipoSituacaoAditiva tipo,
            String estadoInicial, String transformacao, String estadoFinal,
            String quantidade1, String quantidade2, String resultado,
            String referido, String referendo, String valorRelativo) {
        List<Marcacao> r = new ArrayList<Marcacao>();
        if (tipo == null) return r;
        switch (tipo) {
            case COMPOSICAO_MEDIDAS:
                add(r, "papel.parte1", quantidade1);
                add(r, "papel.parte2", quantidade2);
                add(r, "papel.todo", resultado);
                break;
            case TRANSFORMACAO_MEDIDAS:
                add(r, "papel.estadoInicial", estadoInicial);
                add(r, "papel.transformacao", transformacao);
                add(r, "papel.estadoFinal", estadoFinal);
                break;
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                add(r, "papel.parte1", quantidade1);
                add(r, "papel.parte2", quantidade2);
                add(r, "papel.todo", resultado);
                add(r, "papel.transformacao", transformacao);
                add(r, "papel.estadoFinal", estadoFinal);
                break;
            case COMPARACAO_MEDIDAS:
                add(r, "papel.referido", referido);
                add(r, "papel.diferenca", valorRelativo);
                add(r, "papel.diferenca", resultado); // compatibilidade legada
                add(r, "papel.referendo", referendo);
                break;
            case COMPOSICAO_TRANSFORMACOES:
                add(r, "papel.transformacao1", quantidade1);
                add(r, "papel.transformacao2", quantidade2);
                add(r, "papel.transformacaoFinal", resultado);
                break;
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                add(r, "papel.estadoInicial", estadoInicial);
                add(r, "papel.transformacao1", quantidade1);
                add(r, "papel.transformacao2", quantidade2);
                add(r, "papel.estadoFinal", resultado);
                break;
            case TRANSFORMACAO_RELACAO:
                add(r, "papel.relacaoInicial", estadoInicial);
                add(r, "papel.transformacao", transformacao);
                add(r, "papel.relacaoFinal", estadoFinal);
                break;
            case COMPOSICAO_RELACOES:
                add(r, "papel.relacao1", quantidade1);
                add(r, "papel.relacao2", quantidade2);
                add(r, "papel.relacaoFinal", resultado);
                break;
            default:
                break;
        }
        return r;
    }

    private void add(List<Marcacao> lista, String chave, String valor) {
        lista.add(new Marcacao(chave, valor));
    }

    private boolean eh(String normalizado, String... possibilidades) {
        for (String possibilidade : possibilidades) {
            if (normalizado.equals(possibilidade)) return true;
        }
        return false;
    }

    private Resultado vazio() {
        return new Resultado("", "", "",
                Collections.<String>emptyList(), false);
    }

    private static final class Marcacao {
        final String chave;
        final String valor;
        Marcacao(String chave, String valor) {
            this.chave = limpar(chave);
            this.valor = limpar(valor);
        }
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(limpar(texto), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "");
    }
}
