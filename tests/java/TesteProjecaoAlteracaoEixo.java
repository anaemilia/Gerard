import gerard.aplicacao.portabilidade.ProjecaoAlteracaoEixo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.LinkedHashMap;
import java.util.Map;

/** A projeção da reta é a mesma para web e desktop: o domínio do papel mexido decide (zero não é transformação). */
public final class TesteProjecaoAlteracaoEixo {
    public static void main(String[] args) {
        Map<String, Object> atuais = new LinkedHashMap<String, Object>();
        atuais.put("papel.transformacao1", -2);
        atuais.put("papel.transformacao2", -5);
        atuais.put("papel.transformacaoFinal", -7);
        atuais.put("papel.estadoInicial", 35);
        atuais.put("papel.estadoFinal", 28);
        TipoSituacaoAditiva tipo = TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES;

        Map<String, Object> zero = ProjecaoAlteracaoEixo.projetar(tipo, "papel.transformacao1", 0, atuais);
        exigir(Boolean.FALSE.equals(zero.get("aceita")), "zero numa transformação é recusado");

        Map<String, Object> um = ProjecaoAlteracaoEixo.projetar(tipo, "papel.transformacao1", -1, atuais);
        exigir(Boolean.TRUE.equals(um.get("aceita")), "-1 é aceito");
        Map<?, ?> v = (Map<?, ?>) um.get("valores");
        exigir(Integer.valueOf(-6).equals(v.get("papel.transformacaoFinal")), "a resultante acompanha: " + v);
        exigir(Integer.valueOf(29).equals(v.get("papel.estadoFinal")), "o estado final acompanha: " + v);

        Map<String, Object> negativo = ProjecaoAlteracaoEixo.projetar(tipo, "papel.transformacao1", -40, atuais);
        exigir(Boolean.FALSE.equals(negativo.get("aceita")), "um estado final negativo é recusado");
        System.out.println("Teste da projeção da alteração do eixo aprovado.");
    }

    private static void exigir(boolean ok, String msg) {
        if (!ok) throw new AssertionError(msg);
    }
}
