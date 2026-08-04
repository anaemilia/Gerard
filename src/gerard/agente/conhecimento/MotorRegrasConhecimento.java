package gerard.agente.conhecimento;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Avalia regras da base de conhecimento integrada do Gérard contra um mapa
 * de fatos, implementando os operadores descritos em
 * dados/base_conhecimento_gerard_jsons/base_conhecimento_gerard/schema_regras_gerard.json
 * ("=", "!=", ">", ">=", "<", "<=", "entre", "contem",
 * "contem_semanticamente", "em", "nao_em", "existe", "nao_existe").
 *
 * Só considera regras com status "ativa" (as "experimental"/"arquivada"
 * ficam de fora por padrão — ver manifest_base_conhecimento_gerard.json:
 * "Regras J48/PART e Apriori são experimentais. Não substituem as regras
 * matemáticas normativas."). Conflito entre regras que concluem o mesmo
 * campo é resolvido por prioridade (maior primeiro); empate preserva a
 * ordem de aparição no arquivo, que já é a ordem de prioridade do
 * manifesto (domínio > consistência > pedagógica > integrada >
 * qualitativa > preditiva > associativa).
 *
 * Um fato ausente do mapa nunca bate com um antecedente (exceto
 * "existe"/"nao_existe") — é assim que regras cujos campos o app ainda não
 * calcula (ex.: "explicacao", "houve_feedback") ficam carregadas mas
 * inertes, sem lançar exceção.
 */
public final class MotorRegrasConhecimento {

    public List<RegraConhecimento> regrasQueBatem(Map<String, Object> fatos, List<RegraConhecimento> regras) {
        List<RegraConhecimento> resultado = new ArrayList<RegraConhecimento>();
        for (RegraConhecimento regra : regras) {
            if (regra.isAtiva() && todasBatem(fatos, regra.getAntecedentes())) {
                resultado.add(regra);
            }
        }
        Collections.sort(resultado, new Comparator<RegraConhecimento>() {
            @Override
            public int compare(RegraConhecimento r1, RegraConhecimento r2) {
                return Integer.valueOf(r2.getPrioridade()).compareTo(Integer.valueOf(r1.getPrioridade()));
            }
        });
        return resultado;
    }

    /**
     * Devolve a conclusão de maior prioridade para um campo específico,
     * entre as regras cujos antecedentes batem com os fatos, ou null se
     * nenhuma bater.
     */
    public ConclusaoVencedora melhorConclusao(Map<String, Object> fatos, List<RegraConhecimento> regras,
            String campoDesejado) {
        for (RegraConhecimento regra : regrasQueBatem(fatos, regras)) {
            for (Conclusao conclusao : regra.getConclusoes()) {
                if (campoDesejado.equals(conclusao.getCampo())) {
                    return new ConclusaoVencedora(regra.getRuleId(), conclusao.getValor(), regra.getExplicacao());
                }
            }
        }
        return null;
    }

    private boolean todasBatem(Map<String, Object> fatos, List<Condicao> antecedentes) {
        for (Condicao condicao : antecedentes) {
            if (!bate(fatos, condicao)) {
                return false;
            }
        }
        return true;
    }

    private boolean bate(Map<String, Object> fatos, Condicao condicao) {
        String operador = condicao.getOperador();
        String campo = condicao.getCampo();
        boolean existeFato = fatos.containsKey(campo) && fatos.get(campo) != null;

        if ("existe".equals(operador)) {
            return existeFato;
        }
        if ("nao_existe".equals(operador)) {
            return !existeFato;
        }
        if (!existeFato) {
            return false;
        }

        Object fatoValor = fatos.get(campo);
        Object condicaoValor = condicao.getValor();

        if ("=".equals(operador)) {
            return igual(fatoValor, condicaoValor);
        }
        if ("!=".equals(operador)) {
            return !igual(fatoValor, condicaoValor);
        }
        if (">".equals(operador) || ">=".equals(operador) || "<".equals(operador) || "<=".equals(operador)) {
            double a = paraDouble(fatoValor);
            double b = paraDouble(condicaoValor);
            if (Double.isNaN(a) || Double.isNaN(b)) {
                return false;
            }
            if (">".equals(operador)) {
                return a > b;
            }
            if (">=".equals(operador)) {
                return a >= b;
            }
            if ("<".equals(operador)) {
                return a < b;
            }
            return a <= b;
        }
        if ("entre".equals(operador)) {
            if (!(condicaoValor instanceof List) || ((List<?>) condicaoValor).size() < 2) {
                return false;
            }
            List<?> limites = (List<?>) condicaoValor;
            double a = paraDouble(fatoValor);
            double min = paraDouble(limites.get(0));
            double max = paraDouble(limites.get(1));
            if (Double.isNaN(a) || Double.isNaN(min) || Double.isNaN(max)) {
                return false;
            }
            return a >= min && a <= max;
        }
        if ("em".equals(operador) || "nao_em".equals(operador)) {
            boolean pertence = pertenceLista(fatoValor, condicaoValor);
            return "em".equals(operador) == pertence;
        }
        if ("contem".equals(operador)) {
            return contem(fatoValor, condicaoValor);
        }
        if ("contem_semanticamente".equals(operador)) {
            return contemSemanticamente(fatoValor, condicaoValor);
        }
        return false;
    }

    private boolean pertenceLista(Object fatoValor, Object condicaoValor) {
        if (!(condicaoValor instanceof List)) {
            return false;
        }
        String alvo = toStringValor(fatoValor);
        for (Object item : (List<?>) condicaoValor) {
            if (toStringValor(item).equals(alvo)) {
                return true;
            }
        }
        return false;
    }

    private boolean contem(Object fatoValor, Object condicaoValor) {
        String alvo = toStringValor(condicaoValor);
        if (fatoValor instanceof List) {
            for (Object item : (List<?>) fatoValor) {
                if (toStringValor(item).equals(alvo)) {
                    return true;
                }
            }
            return false;
        }
        return toStringValor(fatoValor).contains(alvo);
    }

    private boolean contemSemanticamente(Object fatoValor, Object condicaoValor) {
        if (!(condicaoValor instanceof List) || !(fatoValor instanceof String)) {
            return false;
        }
        String texto = normalizar((String) fatoValor);
        for (Object item : (List<?>) condicaoValor) {
            String termo = normalizar(toStringValor(item));
            if (termo.length() > 0 && texto.contains(termo)) {
                return true;
            }
        }
        return false;
    }

    private boolean igual(Object a, Object b) {
        if (a == null || b == null) {
            return a == b;
        }
        double da = paraDouble(a);
        double db = paraDouble(b);
        if (!Double.isNaN(da) && !Double.isNaN(db)) {
            return da == db;
        }
        return toStringValor(a).equals(toStringValor(b));
    }

    private double paraDouble(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        if (o instanceof String) {
            try {
                return Double.parseDouble((String) o);
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
        }
        return Double.NaN;
    }

    private String toStringValor(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Double) {
            double d = ((Double) o).doubleValue();
            if (d == Math.rint(d) && !Double.isInfinite(d)) {
                return String.valueOf((long) d);
            }
            return String.valueOf(d);
        }
        return String.valueOf(o);
    }

    /**
     * Mesmo critério de normalização de AnalisadorNivelConceitual: minúsculas
     * e sem acento, para casamento de texto livre robusto a variação de
     * grafia — não é o mesmo texto ali (esta classe não depende daquela),
     * só o mesmo padrão já estabelecido no projeto.
     */
    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
