package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.CatalogoRelacoesEstruturaisAditivas;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Projeção exploratória da reta dos inteiros, sem gravar nada: o papel publica o novo valor, a relação estrutural do
 * domínio calcula o dependente e, na Composição de Transformações, os estados acompanham. Fonte única da regra para a web
 * (ServicoSorteioAtividade) e para o desktop (Main): quem recusa um valor que o domínio não aceita é este objeto, nos dois.
 */
public final class ProjecaoAlteracaoEixo {
    private ProjecaoAlteracaoEixo() {
    }

    public static Map<String, Object> projetar(
            TipoSituacaoAditiva tipo, String papelId, int valor,
            Map<String, Object> atuais) {
        CatalogoRelacoesEstruturaisAditivas catalogo = new CatalogoRelacoesEstruturaisAditivas();
        CatalogoRelacoesEstruturaisAditivas.RelacaoContextualizada descritor =
                catalogo.criar(tipo, null);
        Map<String, Object> valores = new LinkedHashMap<String, Object>();
        valores.put(papelId, Integer.valueOf(valor));
        Map<String, Object> resultado = new LinkedHashMap<String, Object>();
        resultado.put("schema", ServicoAtividadeComposicao.SCHEMA_RESULTADO);
        resultado.put("valores", valores);
        if (descritor == null || tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return resultado;
        }
        String[] chaves = descritor.chavesDosPapeis();
        int indiceAlterado = -1;
        gerard.semantica.numero.ValorNumerico[] numeros =
                new gerard.semantica.numero.ValorNumerico[chaves.length];
        for (int i = 0; i < chaves.length; i++) {
            Object atual = chaves[i].equals(papelId) ? Integer.valueOf(valor) : atuais.get(chaves[i]);
            if (chaves[i].equals(papelId)) indiceAlterado = i;
            if (atual instanceof Number) {
                numeros[i] = new gerard.semantica.numero.NumeroInteiro(((Number) atual).intValue());
            }
        }
        if (indiceAlterado < 0) {
            return resultado;
        }
        // O domínio do próprio papel que a pessoa mexeu (zero não é transformação) decide antes de qualquer cálculo.
        if (!descritor.papelAceita(indiceAlterado, valor)) {
            resultado.put("aceita", Boolean.FALSE);
            valores.clear();
            return resultado;
        }
        // Mesma regra do desktop (EstadoSemanticoCompartilhado.tentativaPreservaDominios): o papel que a pessoa mexeu e o
        // que será recalculado têm que continuar nos seus domínios. Sem isso, um valor que o domínio recusa (zero numa
        // transformação, por exemplo) era aceito e ficava sozinho, sem que o resto do diagrama o acompanhasse.
        if (!new gerard.campoaditivo.sincronizacao.ResolvedorRelacoesEstruturaisAditivas()
                .tentativaPreservaDominios(tipo, numeros, indiceAlterado, Integer.valueOf(valor))) {
            resultado.put("aceita", Boolean.FALSE);
            valores.clear();
            return resultado;
        }
        gerard.dominio.campoaditivo.ResultadoCalculo calculo = catalogo.criar(tipo, numeros)
                .recalcularParaConsistencia(indiceAlterado);
        if (calculo != null && calculo.temValorCalculavel()
                && calculo.getValorCalculado().valorOuNull() != null) {
            // O domínio do papel dependente decide (ex.: não negatividade das
            // medidas): uma posição da reta que o violaria não é projetada.
            if (!calculo.getPapelCalculado().aceita(calculo.getValorCalculado())) {
                resultado.put("aceita", Boolean.FALSE);
                valores.clear();
                return resultado;
            }
            valores.put(calculo.getPapelCalculado().getChave(),
                    Integer.valueOf(((Number) calculo.getValorCalculado().valorOuNull()).intValue()));
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                && !acompanharComOsEstados(valores, atuais)) {
            resultado.put("aceita", Boolean.FALSE);
            valores.clear();
            return resultado;
        }
        resultado.put("aceita", Boolean.TRUE);
        return resultado;
    }

    /**
     * Na Composição de Transformações os estados acompanham as transformações (ver
     * EstadosDaComposicaoDeTransformacoes). Acrescenta aos valores projetados os dos estados; false se algum sairia do
     * domínio.
     */
    private static boolean acompanharComOsEstados(Map<String, Object> valores, Map<String, Object> atuais) {
        Integer transformacao1 = inteiroOuNull(valores.containsKey("papel.transformacao1")
                ? valores.get("papel.transformacao1") : atuais.get("papel.transformacao1"));
        Integer resultante = inteiroOuNull(valores.containsKey("papel.transformacaoFinal")
                ? valores.get("papel.transformacaoFinal") : atuais.get("papel.transformacaoFinal"));
        Integer inicial = inteiroOuNull(atuais.get(
                gerard.campoaditivo.sincronizacao.EstadosDaComposicaoDeTransformacoes.ESTADO_INICIAL));
        boolean temIntermediario = atuais.get(
                gerard.campoaditivo.sincronizacao.EstadosDaComposicaoDeTransformacoes.ESTADO_INTERMEDIARIO) instanceof Number;
        Map<String, Integer> estados = gerard.campoaditivo.sincronizacao.EstadosDaComposicaoDeTransformacoes
                .recalcular(inicial, transformacao1, resultante, temIntermediario);
        if (estados == null) {
            return false;
        }
        valores.putAll(estados);
        return true;
    }

    private static Integer inteiroOuNull(Object valor) {
        return valor instanceof Number ? Integer.valueOf(((Number) valor).intValue()) : null;
    }
}
