package gerard.interpretacao.servico;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.interpretacao.modelo.PistaLinguistica;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.regras.RegraLinguistica;
import gerard.interpretacao.regras.RegrasFrances;
import gerard.interpretacao.regras.RegrasEspanhol;
import gerard.interpretacao.regras.RegrasIngles;
import gerard.interpretacao.regras.RegrasPortugues;
import gerard.interpretacao.util.NormalizadorTexto;
import gerard.i18n.ServicoLocalizacao;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class InterpretadorLinguistico {
    private final List<RegraLinguistica> regras;
    private final ExtratorNumeros extratorNumeros;
    private final InferidorPapeisNumericos inferidorPapeisNumericos;
    private final InferidorSubtipoVergnaud inferidorSubtipoVergnaud;

    public InterpretadorLinguistico() {
        this.regras = new ArrayList<RegraLinguistica>();
        this.regras.add(new RegrasPortugues());
        this.regras.add(new RegrasIngles());
        this.regras.add(new RegrasFrances());
        this.regras.add(new RegrasEspanhol());
        this.extratorNumeros = new ExtratorNumeros();
        this.inferidorPapeisNumericos = new InferidorPapeisNumericos();
        this.inferidorSubtipoVergnaud = new InferidorSubtipoVergnaud();
    }

    public ResultadoInterpretacao interpretar(String texto) {
        return interpretar(texto, null);
    }

    public ResultadoInterpretacao interpretar(String texto, CategoriaProblema categoriaContexto) {
        String normalizado = NormalizadorTexto.normalizar(texto);
        IdiomaProblema idioma = detectarIdioma(normalizado);
        List<PistaLinguistica> pistas = coletarPistas(texto, normalizado, idioma);
        List<NumeroEncontrado> numeros = extratorNumeros.extrair(texto);

        CategoriaProblema categoriaDetectada = selecionarCategoria(pistas);
        CategoriaProblema categoria = categoriaContexto != null && categoriaContexto != CategoriaProblema.INDEFINIDA
                ? categoriaContexto
                : categoriaDetectada;
        double confianca = calcularConfianca(pistas, categoriaDetectada);
        String relacao = inferirRelacaoProvavel(pistas);
        List<String> avisos = gerarAvisos(categoria, numeros, pistas);
        List<PapelElementoInterpretado> papeis = inferidorPapeisNumericos.inferir(texto, categoria, numeros);

        return new ResultadoInterpretacao(idioma, categoria, confianca, pistas, numeros, avisos, papeis, relacao,
                inferidorSubtipoVergnaud.inferir(categoria, papeis));
    }

    private IdiomaProblema detectarIdioma(String textoNormalizado) {
        IdiomaProblema melhorIdioma = IdiomaProblema.PORTUGUES;
        int melhorPontuacao = -1;

        for (RegraLinguistica regra : regras) {
            int pontuacao = regra.pontuarIdioma(textoNormalizado);
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhorIdioma = regra.getIdioma();
            }
        }
        return melhorPontuacao <= 0 ? IdiomaProblema.INDEFINIDO : melhorIdioma;
    }

    private List<PistaLinguistica> coletarPistas(String textoOriginal, String textoNormalizado, IdiomaProblema idioma) {
        List<PistaLinguistica> pistas = new ArrayList<PistaLinguistica>();
        for (RegraLinguistica regra : regras) {
            if (idioma == IdiomaProblema.INDEFINIDO || regra.getIdioma() == idioma) {
                pistas.addAll(regra.avaliar(textoOriginal, textoNormalizado));
            }
        }
        return pistas;
    }

    private CategoriaProblema selecionarCategoria(List<PistaLinguistica> pistas) {
        if (pistas.isEmpty()) {
            return CategoriaProblema.INDEFINIDA;
        }
        Map<CategoriaProblema, Integer> pontuacao = new EnumMap<CategoriaProblema, Integer>(CategoriaProblema.class);
        for (PistaLinguistica pista : pistas) {
            Integer atual = pontuacao.get(pista.getCategoria());
            pontuacao.put(pista.getCategoria(), (atual == null ? 0 : atual) + pista.getPeso());
        }
        CategoriaProblema melhor = CategoriaProblema.INDEFINIDA;
        int maior = 0;
        for (Map.Entry<CategoriaProblema, Integer> entrada : pontuacao.entrySet()) {
            if (entrada.getValue() > maior) {
                maior = entrada.getValue();
                melhor = entrada.getKey();
            }
        }
        return melhor;
    }

    private double calcularConfianca(List<PistaLinguistica> pistas, CategoriaProblema categoria) {
        if (categoria == CategoriaProblema.INDEFINIDA || pistas.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        int categoriaTotal = 0;
        for (PistaLinguistica pista : pistas) {
            total += pista.getPeso();
            if (pista.getCategoria() == categoria) {
                categoriaTotal += pista.getPeso();
            }
        }
        double proporcao = total == 0 ? 0.0 : (double) categoriaTotal / (double) total;
        double reforcoPorPistas = Math.min(0.20, pistas.size() * 0.04);
        double confianca = proporcao * 0.80 + reforcoPorPistas;
        return confianca > 1.0 ? 1.0 : confianca;
    }

    private String inferirRelacaoProvavel(List<PistaLinguistica> pistas) {
        for (PistaLinguistica pista : pistas) {
            String exp = pista.getExpressao();
            if (exp.contains("menos") || exp.contains("less") || exp.contains("fewer") || exp.contains("moins") || exp.contains("perdeu") || exp.contains("lost") || exp.contains("perdu")) {
                return "negativa";
            }
            if (exp.contains("mais") || exp.contains("more") || exp.contains("plus") || exp.contains("ganhou") || exp.contains("won") || exp.contains("gagne")) {
                return "positiva";
            }
        }
        return "nao inferida";
    }

    private List<String> gerarAvisos(CategoriaProblema categoria, List<NumeroEncontrado> numeros, List<PistaLinguistica> pistas) {
        List<String> avisos = new ArrayList<String>();
        if (categoria == CategoriaProblema.INDEFINIDA) {
            avisos.add(ServicoLocalizacao.getInstancia().texto("warning.category_undefined"));
        }
        if ((categoria == CategoriaProblema.COMPARACAO_MEDIDAS || categoria == CategoriaProblema.TRANSFORMACAO_RELACAO || categoria == CategoriaProblema.COMPOSICAO_RELACOES) && numeros.size() < 2) {
            avisos.add(ServicoLocalizacao.getInstancia().texto("warning.incomplete_relation"));
        }
        if ((categoria == CategoriaProblema.TRANSFORMACAO_MEDIDAS || categoria == CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS || categoria == CategoriaProblema.COMPOSICAO_MEDIDAS || categoria == CategoriaProblema.COMPOSICAO_TRANSFORMACOES) && numeros.size() < 2) {
            avisos.add(ServicoLocalizacao.getInstancia().texto("warning.incomplete_problem"));
        }
        if (pistas.size() > 1) {
            CategoriaProblema primeira = pistas.get(0).getCategoria();
            for (PistaLinguistica pista : pistas) {
                if (pista.getCategoria() != primeira) {
                    avisos.add(ServicoLocalizacao.getInstancia().texto("warning.multiple_categories"));
                    break;
                }
            }
        }
        return avisos;
    }
}
