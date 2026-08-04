import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.agente.monitor.AgenteMonitor;
import gerard.agente.zdp.AgenteZDP;
import gerard.agente.zdp.ResultadoConsultaConhecimento;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

import java.util.List;

/**
 * Teste pontual da integração da base de conhecimento
 * (base_conhecimento_gerard_jsons) aos agentes reais — descartável, mesmo
 * espírito de TesteMonkeyGuiadoPorCasosReais: não é um framework de testes,
 * só um harness executável que falha alto (AssertionError) se a integração
 * quebrar.
 */
public class TesteBaseConhecimento {

    public static void main(String[] args) {
        System.out.println("=== AgenteMonitor + regras de dominio ===");
        AgenteMonitor monitor = new AgenteMonitor(new ScaffoldingQuestionamento());

        List<String> papeisComposicao = monitor.papeisValidosPara(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        String invarianteComposicao = monitor.invarianteAditivoPara(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        System.out.println("papeis composicao: " + papeisComposicao);
        System.out.println("invariante composicao: " + invarianteComposicao);
        checar("papeis composicao", papeisComposicao.toString(), "[parte_1, parte_2, todo]");
        checar("invariante composicao", invarianteComposicao, "parte_1 + parte_2 = todo");

        List<String> papeisTransformacao = monitor.papeisValidosPara(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        String invarianteTransformacao = monitor.invarianteAditivoPara(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        System.out.println("papeis transformacao: " + papeisTransformacao);
        System.out.println("invariante transformacao: " + invarianteTransformacao);
        checar("papeis transformacao", papeisTransformacao.toString(), "[estado_inicial, transformacao, estado_final]");
        checar("invariante transformacao", invarianteTransformacao, "estado_inicial + transformacao = estado_final");

        List<String> papeisComparacao = monitor.papeisValidosPara(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        String invarianteComparacao = monitor.invarianteAditivoPara(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        System.out.println("papeis comparacao: " + papeisComparacao);
        System.out.println("invariante comparacao: " + invarianteComparacao);
        checar("papeis comparacao", papeisComparacao.toString(), "[referido, referendo, valor_relativo]");
        checar("invariante comparacao", invarianteComparacao, "referendo + valor_relativo = referido");

        List<String> papeisComposto =
                monitor.papeisValidosPara(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS);
        System.out.println("papeis categoria composta (sem regra de dominio): " + papeisComposto);
        checar("categoria composta devolve lista vazia", papeisComposto.toString(), "[]");

        System.out.println();
        System.out.println("=== AgenteMonitor.avaliarSinalNumeroRelativo ===");
        final boolean[] ultimoVeredito = new boolean[1];
        monitor.adicionarOuvinte(new gerard.agente.monitor.OuvinteVeredictoAgenteMonitor() {
            public void aoAvaliar(boolean correto) {
                ultimoVeredito[0] = correto;
            }
        });
        boolean retorno = monitor.avaliarSinalNumeroRelativo(false);
        System.out.println("avaliarSinalNumeroRelativo(false) -> retorno=" + retorno
                + " notificou=" + ultimoVeredito[0]);
        checar("avaliarSinalNumeroRelativo retorna o mesmo booleano", String.valueOf(retorno), "false");
        checar("avaliarSinalNumeroRelativo notifica ouvintes", String.valueOf(ultimoVeredito[0]), "false");

        System.out.println();
        System.out.println("=== AgenteZDP + R-PED-001/002 ===");
        AgenteZDP zdp = new AgenteZDP();
        String idUsuario = "teste_conhecimento";
        TipoSituacaoAditiva categoria = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;

        // Cenario R-PED-001: 2 erros consecutivos na mesma tarefa, sem ajuda ainda.
        String papelA = "papel.estadoFinal";
        ResultadoConsultaConhecimento r1 = zdp.consultarConhecimento(idUsuario, categoria, papelA, false);
        System.out.println("1o erro: ruleId=" + r1.getRuleId() + " intervencao=" + r1.getIntervencaoSugerida());
        checar("1o erro consecutivo nao deve intervir ainda", String.valueOf(r1.temIntervencao()), "false");

        ResultadoConsultaConhecimento r2 = zdp.consultarConhecimento(idUsuario, categoria, papelA, false);
        System.out.println("2o erro: ruleId=" + r2.getRuleId() + " intervencao=" + r2.getIntervencaoSugerida());
        checar("2o erro consecutivo dispara R-PED-001", r2.getRuleId(), "R-PED-001");
        checar("2o erro consecutivo sugere ajuda_conceitual", r2.getIntervencaoSugerida(), "ajuda_conceitual");

        // Cenario R-PED-002: erro, acerto, erro, acerto, erro na mesma tarefa
        // (historico chega a 3 sem nunca acumular 2 erros CONSECUTIVOS, entao
        // R-PED-001 nao dispara primeiro e nao marca ajuda_previa=true antes).
        String papelB = "papel.estadoInicial";
        zdp.consultarConhecimento(idUsuario, categoria, papelB, false); // historico=1
        zdp.consultarConhecimento(idUsuario, categoria, papelB, true);  // consecutivos volta a 0
        zdp.consultarConhecimento(idUsuario, categoria, papelB, false); // historico=2
        zdp.consultarConhecimento(idUsuario, categoria, papelB, true);  // consecutivos volta a 0
        ResultadoConsultaConhecimento r5 = zdp.consultarConhecimento(idUsuario, categoria, papelB, false); // historico=3
        System.out.println("5a acao (historico=3, sem 2 consecutivos): ruleId=" + r5.getRuleId()
                + " intervencao=" + r5.getIntervencaoSugerida() + " risco=" + r5.getRiscoNovoErro());
        checar("historico=3 dispara R-PED-002", r5.getRuleId(), "R-PED-002");
        checar("R-PED-002 sugere mudar modalidade de ajuda", r5.getIntervencaoSugerida(), "mudar_modalidade_de_ajuda");
        checar("R-PED-002 marca risco alto", r5.getRiscoNovoErro(), "alto");

        System.out.println();
        System.out.println("TODOS OS TESTES PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
    }
}
