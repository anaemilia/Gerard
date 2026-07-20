package gerard.interpretacao.classificacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classificador Naive Bayes multinomial leve para categorias de Vergnaud.
 *
 * Foi escolhido por ser explicável, treinável com poucos exemplos e não exigir
 * bibliotecas externas.
 */
public class ClassificadorNaiveBayesVergnaud implements ClassificadorCategoriaVergnaud {
    private final ExtratorAtributosLinguisticos extrator;
    private final Map<TipoSituacaoAditiva, Map<String, Integer>> frequenciasPorCategoria;
    private final Map<TipoSituacaoAditiva, Integer> totalAtributosPorCategoria;
    private final Map<TipoSituacaoAditiva, Integer> totalExemplosPorCategoria;
    private final Set<String> vocabulario;
    private int totalExemplos;
    private boolean treinado;

    public ClassificadorNaiveBayesVergnaud() {
        this.extrator = new ExtratorAtributosLinguisticos();
        this.frequenciasPorCategoria = new EnumMap<TipoSituacaoAditiva, Map<String, Integer>>(TipoSituacaoAditiva.class);
        this.totalAtributosPorCategoria = new EnumMap<TipoSituacaoAditiva, Integer>(TipoSituacaoAditiva.class);
        this.totalExemplosPorCategoria = new EnumMap<TipoSituacaoAditiva, Integer>(TipoSituacaoAditiva.class);
        this.vocabulario = new HashSet<String>();
        this.totalExemplos = 0;
        this.treinado = false;
    }

    public void treinar(List<ExemploSituacaoVergnaud> exemplos) {
        frequenciasPorCategoria.clear();
        totalAtributosPorCategoria.clear();
        totalExemplosPorCategoria.clear();
        vocabulario.clear();
        totalExemplos = 0;

        if (exemplos == null) {
            treinado = false;
            return;
        }

        for (ExemploSituacaoVergnaud exemplo : exemplos) {
            if (exemplo == null || exemplo.getCategoria() == null || exemplo.getTexto().trim().length() == 0) {
                continue;
            }
            adicionarExemplo(exemplo);
        }

        treinado = totalExemplos > 0 && !vocabulario.isEmpty();
    }

    private void adicionarExemplo(ExemploSituacaoVergnaud exemplo) {
        TipoSituacaoAditiva categoria = exemplo.getCategoria();
        List<String> atributos = extrator.extrair(exemplo.getTexto());
        if (atributos.isEmpty()) {
            return;
        }

        Map<String, Integer> mapa = frequenciasPorCategoria.get(categoria);
        if (mapa == null) {
            mapa = new HashMap<String, Integer>();
            frequenciasPorCategoria.put(categoria, mapa);
        }

        int totalCategoria = obter(totalAtributosPorCategoria, categoria);
        for (String atributo : atributos) {
            mapa.put(atributo, obter(mapa, atributo) + 1);
            vocabulario.add(atributo);
            totalCategoria++;
        }
        totalAtributosPorCategoria.put(categoria, totalCategoria);
        totalExemplosPorCategoria.put(categoria, obter(totalExemplosPorCategoria, categoria) + 1);
        totalExemplos++;
    }

    public ResultadoClassificacaoVergnaud classificar(String enunciado, TipoSituacaoAditiva categoriaManual) {
        if (!treinado) {
            List<String> pistas = extrator.extrairPistasHumanas(enunciado);
            return new ResultadoClassificacaoVergnaud(categoriaManual, categoriaManual, null, categoriaManual,
                    0.0, 0.0, 0.0, "modelo_nao_treinado", pistas);
        }

        List<String> atributos = extrator.extrair(enunciado);
        if (atributos.isEmpty()) {
            List<String> pistas = extrator.extrairPistasHumanas(enunciado);
            return new ResultadoClassificacaoVergnaud(categoriaManual, categoriaManual, null, categoriaManual,
                    0.0, 0.0, 0.0, "sem_atributos", pistas);
        }

        List<PontuacaoCategoria> pontuacoes = new ArrayList<PontuacaoCategoria>();
        int vocab = Math.max(1, vocabulario.size());
        int numCategorias = Math.max(1, totalExemplosPorCategoria.size());

        for (TipoSituacaoAditiva categoria : TipoSituacaoAditiva.values()) {
            int exemplosCategoria = obter(totalExemplosPorCategoria, categoria);
            if (exemplosCategoria <= 0) {
                continue;
            }
            double logProb = Math.log((exemplosCategoria + 1.0) / (totalExemplos + numCategorias));
            Map<String, Integer> mapa = frequenciasPorCategoria.get(categoria);
            int totalAtributos = obter(totalAtributosPorCategoria, categoria);

            for (String atributo : atributos) {
                int freq = mapa == null ? 0 : obter(mapa, atributo);
                logProb += Math.log((freq + 1.0) / (totalAtributos + vocab));
            }
            pontuacoes.add(new PontuacaoCategoria(categoria, logProb));
        }

        if (pontuacoes.isEmpty()) {
            List<String> pistas = extrator.extrairPistasHumanas(enunciado);
            return new ResultadoClassificacaoVergnaud(categoriaManual, categoriaManual, null, categoriaManual,
                    0.0, 0.0, 0.0, "modelo_sem_categorias", pistas);
        }

        Collections.sort(pontuacoes, new Comparator<PontuacaoCategoria>() {
            public int compare(PontuacaoCategoria a, PontuacaoCategoria b) {
                return Double.compare(b.logProb, a.logProb);
            }
        });

        double maior = pontuacoes.get(0).logProb;
        double somaExp = 0.0;
        for (PontuacaoCategoria p : pontuacoes) {
            somaExp += Math.exp(p.logProb - maior);
        }
        double probMelhor = Math.exp(pontuacoes.get(0).logProb - maior) / somaExp;
        double margem = 0.0;
        if (pontuacoes.size() > 1) {
            double probSegundo = Math.exp(pontuacoes.get(1).logProb - maior) / somaExp;
            margem = Math.max(0.0, probMelhor - probSegundo);
        }

        double confianca = Math.max(probMelhor, Math.min(1.0, 0.50 + margem));
        List<String> pistas = extrator.extrairPistasHumanas(enunciado);
        pistas.add("modelo=" + pontuacoes.get(0).categoria.name());
        if (pontuacoes.size() > 1) {
            pistas.add("alternativa=" + pontuacoes.get(1).categoria.name());
        }

        return new ResultadoClassificacaoVergnaud(pontuacoes.get(0).categoria,
                pontuacoes.get(0).categoria, null, categoriaManual,
                confianca, confianca, 0.0, "naive_bayes", pistas);
    }

    private int obter(Map<TipoSituacaoAditiva, Integer> mapa, TipoSituacaoAditiva chave) {
        Integer valor = mapa.get(chave);
        return valor == null ? 0 : valor.intValue();
    }

    private int obter(Map<String, Integer> mapa, String chave) {
        Integer valor = mapa.get(chave);
        return valor == null ? 0 : valor.intValue();
    }

    private static class PontuacaoCategoria {
        private final TipoSituacaoAditiva categoria;
        private final double logProb;

        private PontuacaoCategoria(TipoSituacaoAditiva categoria, double logProb) {
            this.categoria = categoria;
            this.logProb = logProb;
        }
    }
}
