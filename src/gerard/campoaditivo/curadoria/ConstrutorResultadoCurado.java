package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.PistaLinguistica;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.modelo.SubtipoVergnaud;
import gerard.interpretacao.servico.InferidorSubtipoVergnaud;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fonte da verdade: curadoria humana.
 *
 * Esta classe constrói a estrutura usada pela interface a partir dos metadados
 * validados. Esta classe não classifica o texto automaticamente. O interpretador
 * linguístico fica restrito à aba Curadoria/auditoria.
 */
public class ConstrutorResultadoCurado {
    private final InferidorSubtipoVergnaud inferidorSubtipo = new InferidorSubtipoVergnaud();

    public ResultadoInterpretacao construir(SituacaoProblemaAditiva s) {
        return construir(s, s == null ? null : s.getEnunciado());
    }

    /**
     * Constrói a interpretação usando exatamente o texto exibido pela interface.
     * Isso mantém as faixas dos elementos semânticos coerentes quando o texto
     * original é normalizado para a renderização (por exemplo, seis -> 6).
     */
    public ResultadoInterpretacao construir(SituacaoProblemaAditiva s, String textoExibido) {
        if (s == null) {
            return null;
        }
        CategoriaProblema categoria = categoriaDeTipo(s.getTipo());
        ResolvedorIncognitaCurada.Resultado resolucaoIncognita =
                new ResolvedorIncognitaCurada().resolver(s);
        List<PapelElementoInterpretado> papeis = papeis(s);
        List<NumeroEncontrado> numeros = numeros(textoExibido, papeis);
        List<PistaLinguistica> pistas = new ArrayList<PistaLinguistica>();
        List<String> avisos = new ArrayList<String>();
        if (!s.isValidada()) {
            avisos.add("Situação-problema não validada pela curadoria humana.");
        }
        if (papeis.isEmpty()) {
            avisos.add("Metadados curados incompletos.");
        }
        if (resolucaoIncognita.possuiConflito()
                || resolucaoIncognita.possuiMultiplasInterrogacoes()) {
            avisos.add("Inconsistência na incógnita curada: "
                    + resolucaoIncognita.mensagemInconsistencia());
        }
        SubtipoVergnaud subtipo = inferidorSubtipo.inferir(categoria, papeis);
        return new ResultadoInterpretacao(idioma(s.getIdioma()), categoria, 1.0, pistas, numeros, avisos, papeis, relacao(s), subtipo);
    }

    private List<PapelElementoInterpretado> papeis(SituacaoProblemaAditiva s) {
        List<PapelElementoInterpretado> r = new ArrayList<PapelElementoInterpretado>();
        for (SemanticaCuradaSituacao.PapelCurado papelCurado :
                SemanticaCuradaSituacao.mapear(s, gerard.i18n.ServicoLocalizacao.getInstancia())) {
            papel(r, papelCurado.getValor(), papelCurado.getChave(),
                    papelCurado.isDesconhecido());
        }
        return r;
    }

    private String primeiroNaoVazio(String principal, String fallback) {
        String p = principal == null ? "" : principal.trim();
        if (p.length() > 0) {
            return p;
        }
        return fallback == null ? "" : fallback.trim();
    }

    private String valorComSinalCurado(String valor, String sinal) {
        String v = valor == null ? "" : valor.trim();
        if (v.length() == 0 || v.startsWith("+") || v.startsWith("-") || SimboloDesconhecido.eh(v)) {
            return v;
        }
        String s = normalizar(sinal);
        if ("negativo".equals(s) || (sinal != null && sinal.trim().startsWith("-"))) {
            return "-" + v;
        }
        if ("positivo".equals(s) || (sinal != null && sinal.trim().startsWith("+"))) {
            return "+" + v;
        }
        return v;
    }

    private void papel(List<PapelElementoInterpretado> r, String valor, String chave,
            boolean desconhecidoCurado) {
        String v = valor == null ? "" : valor.trim();
        boolean desconhecido = desconhecidoCurado || SimboloDesconhecido.eh(v);
        if (desconhecido) {
            // Regra geral da curadoria: qualquer papel desconhecido é sempre "?".
            // O valor numérico eventualmente existente em registros legados não é
            // usado pela interface nem inserido automaticamente no diagrama.
            v = "?";
        } else if (v.length() == 0) {
            return;
        }
        r.add(new PapelElementoInterpretado(v, chave, !desconhecido));
    }

    private List<NumeroEncontrado> numeros(String textoExibido, List<PapelElementoInterpretado> papeis) {
        List<NumeroEncontrado> r = new ArrayList<NumeroEncontrado>();
        String texto = textoExibido == null ? "" : textoExibido;
        // Faixas [inicio,fim) já atribuídas a um papel anterior nesta mesma
        // passada — sem isto, dois papéis curados com o MESMO valor (ex.:
        // resultado da transformação coincidindo numericamente com uma das
        // partes do estado inicial) reivindicam o mesmo número do enunciado,
        // e o segundo papel (posição textual idêntica) nunca fica arrastável
        // — quem "ganha" depende só da ordem de iteração de papeis(), não do
        // que o número realmente representa no texto.
        List<int[]> usadas = new ArrayList<int[]>();
        // Prioridade de reivindicação: partes do estado inicial primeiro —
        // seu valor é sempre um número escrito literalmente no enunciado
        // ("2 rosas brancas"), enquanto papéis como transformacaoFinal podem
        // colidir numericamente com elas (o resultado da transformação pode
        // coincidir com a quantidade de uma das partes) sem terem, eles
        // próprios, uma escrita literal no texto — dar prioridade a quem
        // sempre está escrito evita que o outro "roube" a única ocorrência.
        // A ordem de SAÍDA (lista r) não depende disto — cada NumeroEncontrado
        // carrega sua própria chave de papel (ver obterChavePapelDoNumero em
        // ResolvedorPapelInterpretado, que lê numero.getChavePapelSemantico()
        // antes de qualquer índice posicional).
        List<PapelElementoInterpretado> ordemDeReivindicacao = new ArrayList<PapelElementoInterpretado>();
        for (PapelElementoInterpretado p : papeis) {
            if (p != null && p.getChavePapel() != null
                    && p.getChavePapel().startsWith("papel.estadoInicialParte")) {
                ordemDeReivindicacao.add(p);
            }
        }
        for (PapelElementoInterpretado p : papeis) {
            if (p != null && (p.getChavePapel() == null
                    || !p.getChavePapel().startsWith("papel.estadoInicialParte"))) {
                ordemDeReivindicacao.add(p);
            }
        }
        for (PapelElementoInterpretado p : ordemDeReivindicacao) {
            if (p == null || !p.isConhecido()) continue;
            String canonico = canonizar(p.getElemento());
            if (canonico.length() == 0) continue;
            int[] pos = localizar(texto, p.getElemento(), canonico, usadas);
            String original = pos[0] >= 0 ? texto.substring(pos[0], pos[1]) : p.getElemento();
            if (pos[0] >= 0) {
                usadas.add(pos);
            }
            r.add(new NumeroEncontrado(original, pos[0], pos[1], canonico,
                    p.getChavePapel()));
        }
        return r;
    }

    private int[] localizar(String texto, String valor, String canonico, List<int[]> usadas) {
        if (texto == null) {
            return new int[] {-1, -1};
        }

        String valorCurado = valor == null ? "" : valor.trim();
        String canonicoCurado = canonico == null ? "" : canonico.trim();
        String valorSemSinal = removerSinalInicial(valorCurado);
        String canonicoSemSinal = removerSinalInicial(canonicoCurado);

        // O sinal da transformação/valor relativo pertence ao metadado semântico.
        // No enunciado, normalmente aparece apenas a magnitude (por exemplo,
        // transformação = -18,00 e texto = "Gastou R$ 18,00"). Portanto, a
        // localização deve aceitar tanto a forma sinalizada quanto a magnitude.
        String[] candidatos = new String[] {
            valorCurado,
            valorSemSinal,
            canonicoCurado,
            canonicoSemSinal,
            canonicoCurado.replace("+", ""),
            canonicoSemSinal.replace('.', ','),
            canonicoSemSinal.replace(',', '.')
        };

        for (int i = 0; i < candidatos.length; i++) {
            int[] posicao = localizarCandidato(texto, candidatos[i], usadas);
            if (posicao[0] >= 0) {
                return posicao;
            }
        }

        // Última tentativa: compara numericamente cada número do enunciado,
        // preservando a faixa textual completa (incluindo casas decimais).
        Double magnitudeCurada = converterMagnitude(canonicoCurado.length() > 0 ? canonicoCurado : valorCurado);
        if (magnitudeCurada != null) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("(?<![\\d])[-+]?\\d+(?:[.,]\\d+)?(?![\\d])")
                    .matcher(texto);
            while (matcher.find()) {
                if (sobrepoe(usadas, matcher.start(), matcher.end())) {
                    continue;
                }
                Double magnitudeTexto = converterMagnitude(matcher.group());
                if (magnitudeTexto != null && Math.abs(magnitudeTexto.doubleValue() - magnitudeCurada.doubleValue()) < 0.000001d) {
                    return new int[] {matcher.start(), matcher.end()};
                }
            }
        }

        return new int[] {-1, -1};
    }

    private int[] localizarCandidato(String texto, String candidato, List<int[]> usadas) {
        String c = candidato == null ? "" : candidato.trim();
        if (c.length() == 0 || "+".equals(c) || "-".equals(c)) {
            return new int[] {-1, -1};
        }

        java.util.regex.Pattern padrao = java.util.regex.Pattern.compile(
                "(?<![\\d])" + java.util.regex.Pattern.quote(c) + "(?![\\d])");
        java.util.regex.Matcher matcher = padrao.matcher(texto);
        while (matcher.find()) {
            if (!sobrepoe(usadas, matcher.start(), matcher.end())) {
                return new int[] {matcher.start(), matcher.end()};
            }
        }
        return new int[] {-1, -1};
    }

    /**
     * Verdadeiro se [inicio,fim) intersecta alguma faixa já atribuída a
     * outro papel nesta mesma passada de numeros() — ver comentário lá.
     */
    private boolean sobrepoe(List<int[]> usadas, int inicio, int fim) {
        for (int[] faixa : usadas) {
            if (inicio < faixa[1] && faixa[0] < fim) {
                return true;
            }
        }
        return false;
    }

    private String removerSinalInicial(String valor) {
        if (valor == null) return "";
        String v = valor.trim();
        if (v.startsWith("+") || v.startsWith("-")) {
            return v.substring(1).trim();
        }
        return v;
    }

    private Double converterMagnitude(String valor) {
        if (valor == null) return null;
        String v = removerSinalInicial(valor).replace(',', '.').replaceAll("[^0-9.]", "");
        if (v.length() == 0 || ".".equals(v)) return null;
        try {
            return Double.valueOf(v);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String canonizar(String valor) {
        if (valor == null) return "";
        String v = valor.trim().replace(',', '.').replaceAll("[^0-9+\\-.]", "");
        if (v.length() == 0 || "+".equals(v) || "-".equals(v) || ".".equals(v)) return "";
        try {
            if (v.indexOf('.') >= 0) return String.valueOf((int)Math.round(Double.parseDouble(v)));
            Integer.parseInt(v.replace("+", ""));
            return v;
        } catch (NumberFormatException ex) {
            return "";
        }
    }

    private String chavePapelDoTermo(String termo, TipoSituacaoAditiva tipo) {
        return SemanticaCuradaSituacao.chaveTermoDesconhecido(termo, tipo);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
    }

    private String relacao(SituacaoProblemaAditiva s) {
        String sinalTransformacao = normalizar(s.getSinalTransformacao());
        String sinalValorRelativo = normalizar(s.getSinalValorRelativo());
        if ("negativo".equals(sinalTransformacao) || "negativo".equals(sinalValorRelativo)) {
            return "negativa";
        }
        if ("positivo".equals(sinalTransformacao) || "positivo".equals(sinalValorRelativo)) {
            return "positiva";
        }
        String t = (s.getTransformacao() == null ? "" : s.getTransformacao()) + " " + (s.getValorRelativo() == null ? "" : s.getValorRelativo()) + " " + (s.getResultado() == null ? "" : s.getResultado());
        return t.indexOf('-') >= 0 ? "negativa" : "positiva";
    }

    private IdiomaProblema idioma(IdiomaInterface idioma) {
        if (idioma == IdiomaInterface.INGLES) return IdiomaProblema.INGLES;
        if (idioma == IdiomaInterface.FRANCES) return IdiomaProblema.FRANCES;
        if (idioma == IdiomaInterface.ESPANHOL) return IdiomaProblema.ESPANHOL;
        return IdiomaProblema.PORTUGUES;
    }

    public CategoriaProblema categoriaDeTipo(TipoSituacaoAditiva tipo) {
        if (tipo == null) return CategoriaProblema.INDEFINIDA;
        switch (tipo) {
            case COMPOSICAO_MEDIDAS: return CategoriaProblema.COMPOSICAO_MEDIDAS;
            case TRANSFORMACAO_MEDIDAS: return CategoriaProblema.TRANSFORMACAO_MEDIDAS;
            case COMPARACAO_MEDIDAS: return CategoriaProblema.COMPARACAO_MEDIDAS;
            case COMPOSICAO_TRANSFORMACOES: return CategoriaProblema.COMPOSICAO_TRANSFORMACOES;
            case TRANSFORMACAO_RELACAO: return CategoriaProblema.TRANSFORMACAO_RELACAO;
            case COMPOSICAO_RELACOES: return CategoriaProblema.COMPOSICAO_RELACOES;
            default: return CategoriaProblema.INDEFINIDA;
        }
    }
}
