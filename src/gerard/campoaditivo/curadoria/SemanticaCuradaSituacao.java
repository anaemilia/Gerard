package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Fonte única dos papéis semânticos associados a uma situação curada.
 *
 * Os nomes formais dos papéis são localizados pela interface, enquanto valores,
 * sinais, termo desconhecido e participantes/objetos são obtidos exclusivamente
 * dos campos da curadoria. Nenhuma associação específica do enunciado deve ser
 * codificada diretamente na tela ou nos renderizadores.
 */
public final class SemanticaCuradaSituacao {

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
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS) {
            adicionar(papeis, loc, "papel.parte1", situacao.getQuantidade1(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.parte2", situacao.getQuantidade2(), situacao.getPersonagem2(), desconhecido);
            adicionar(papeis, loc, "papel.todo", situacao.getResultado(), situacao.getPersonagem3(), desconhecido);
            adicionar(papeis, loc, "papel.transformacao", aplicarSinal(situacao.getTransformacao(), situacao.getSinalTransformacao()), "", desconhecido);
            adicionar(papeis, loc, "papel.estadoFinal", situacao.getEstadoFinal(), "", desconhecido);
        } else if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            adicionar(papeis, loc, "papel.referido", primeiroNaoVazio(situacao.getReferido(), situacao.getQuantidade2()), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.diferenca", primeiroNaoVazio(aplicarSinal(situacao.getValorRelativo(), situacao.getSinalValorRelativo()), situacao.getResultado()), "", desconhecido);
            adicionar(papeis, loc, "papel.referendo", primeiroNaoVazio(situacao.getReferendo(), situacao.getQuantidade1()), situacao.getPersonagem2(), desconhecido);
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            adicionar(papeis, loc, "papel.transformacao1", situacao.getQuantidade1(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.transformacao2", situacao.getQuantidade2(), situacao.getPersonagem2(), desconhecido);
            adicionar(papeis, loc, "papel.transformacaoFinal", situacao.getResultado(), situacao.getPersonagem3(), desconhecido);
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS) {
            adicionar(papeis, loc, "papel.estadoInicial", situacao.getEstadoInicial(), situacao.getPersonagem1(), desconhecido);
            adicionar(papeis, loc, "papel.transformacao1", situacao.getQuantidade1(), "", desconhecido);
            adicionar(papeis, loc, "papel.transformacao2", situacao.getQuantidade2(), "", desconhecido);
            adicionar(papeis, loc, "papel.estadoFinal", situacao.getResultado(), situacao.getPersonagem2(), desconhecido);
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
        TipoSituacaoAditiva tipo = situacao.getTipo();

        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS) {
            rotulo1 = loc.texto("papel.estadoInicial");
            rotulo2 = loc.texto("papel.transformacao1");
            rotulo3 = loc.texto("papel.estadoIntermediario");
        } else if (papeis.size() >= 3) {
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
