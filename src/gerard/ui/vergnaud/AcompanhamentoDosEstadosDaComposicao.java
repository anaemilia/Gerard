package gerard.ui.vergnaud;

import gerard.aplicacao.portabilidade.ProjecaoAlteracaoEixo;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadosDaComposicaoDeTransformacoes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Liga o diagrama de Vergnaud ao que o domínio decide sobre a Composição de Transformações: quando uma transformação
 * muda, os estados acompanham (EstadosDaComposicaoDeTransformacoes) e um valor que o domínio recusa não é aplicado
 * (ProjecaoAlteracaoEixo — a mesma regra da web). Só traduz elementos visuais em valores e de volta; não calcula nada.
 */
public final class AcompanhamentoDosEstadosDaComposicao {

    /** Valor numérico hoje exibido no elemento; null quando ele não tem valor (vazio ou incógnita). */
    public interface LeitorValores {
        Integer valorDe(ElementoVergnaud elemento);
    }

    /** Responde se o domínio aceita o novo valor do papel (e dos que acompanham). */
    public boolean admite(TipoSituacaoAditiva tipo, List<ElementoVergnaud> elementos, LeitorValores leitor,
            String chavePapel, int valor) {
        if (tipo == null || chavePapel == null) {
            return true;
        }
        Map<String, Object> projecao = ProjecaoAlteracaoEixo.projetar(tipo, chavePapel, valor,
                valoresAtuais(elementos, leitor));
        return !Boolean.FALSE.equals(projecao.get("aceita"));
    }

    /** Valores que os estados passam a ter depois que as transformações já foram aplicadas no diagrama. */
    public List<AtualizacaoElementoVergnaud> planejar(TipoSituacaoAditiva tipo, List<ElementoVergnaud> elementos,
            LeitorValores leitor) {
        List<AtualizacaoElementoVergnaud> atualizacoes = new ArrayList<AtualizacaoElementoVergnaud>();
        if (tipo != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return atualizacoes;
        }
        Map<String, Object> atuais = valoresAtuais(elementos, leitor);
        Integer inicial = inteiro(atuais.get(EstadosDaComposicaoDeTransformacoes.ESTADO_INICIAL));
        if (inicial == null) {
            // Estado inicial dado em duas partes: sem elemento próprio no enunciado, vem das partes já posicionadas.
            inicial = EstadosDaComposicaoDeTransformacoes.estadoInicialDasPartes(
                    inteiro(atuais.get(EstadosDaComposicaoDeTransformacoes.PARTE_1_DO_ESTADO_INICIAL)),
                    inteiro(atuais.get(EstadosDaComposicaoDeTransformacoes.PARTE_2_DO_ESTADO_INICIAL)));
            int indiceInicial = indiceDe(elementos, EstadosDaComposicaoDeTransformacoes.ESTADO_INICIAL);
            if (inicial != null && indiceInicial >= 0) {
                atualizacoes.add(new AtualizacaoElementoVergnaud(indiceInicial, inicial.intValue(),
                        AtualizacaoElementoVergnaud.NaturezaVisual.MEDIDA));
            }
        }
        Map<String, Integer> estados = EstadosDaComposicaoDeTransformacoes.recalcular(inicial,
                inteiro(atuais.get("papel.transformacao1")), inteiro(atuais.get("papel.transformacaoFinal")),
                atuais.get(EstadosDaComposicaoDeTransformacoes.ESTADO_INTERMEDIARIO) != null);
        if (estados == null) {
            return atualizacoes;
        }
        for (int i = 0; i < elementos.size(); i++) {
            Integer valor = estados.get(elementos.get(i).chavePapelSemantico);
            if (valor != null && atuais.get(elementos.get(i).chavePapelSemantico) != null) {
                atualizacoes.add(new AtualizacaoElementoVergnaud(i, valor.intValue(),
                        AtualizacaoElementoVergnaud.NaturezaVisual.MEDIDA));
            }
        }
        return atualizacoes;
    }

    private static int indiceDe(List<ElementoVergnaud> elementos, String chave) {
        for (int i = 0; i < elementos.size(); i++) {
            if (chave.equals(elementos.get(i).chavePapelSemantico)) {
                return i;
            }
        }
        return -1;
    }

    private static Map<String, Object> valoresAtuais(List<ElementoVergnaud> elementos, LeitorValores leitor) {
        Map<String, Object> atuais = new LinkedHashMap<String, Object>();
        for (ElementoVergnaud elemento : elementos) {
            Integer valor = leitor.valorDe(elemento);
            if (valor != null && elemento.chavePapelSemantico != null) {
                atuais.put(elemento.chavePapelSemantico, valor);
            }
        }
        return atuais;
    }

    private static Integer inteiro(Object valor) {
        return valor instanceof Number ? Integer.valueOf(((Number) valor).intValue()) : null;
    }
}
