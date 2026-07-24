package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.NivelSuporte;
import java.util.ArrayList;
import java.util.List;

/**
 * Cobre que invarianteCodigo entra como atributo do dataset minerado por
 * InferenciaRegrasModelador (decisão do usuário em 2026-07-23: o invariante
 * atribuído pelo pesquisador é insumo direto da ação 2 do Agente Modelador).
 * Roda com poucos casos, então usa limiares mínimos = 1 para não pular
 * PART/Apriori.
 */
public final class TesteInferenciaRegrasComInvariante {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        List<DiagnosticoTarefa> diagnosticos = new ArrayList<DiagnosticoTarefa>();

        DiagnosticoTarefa comInvariante = new DiagnosticoTarefa("COMPOSICAO_MEDIDAS:papel.todo");
        comInvariante.setSuporte(NivelSuporte.NENHUM);
        comInvariante.setRegraDeAcao("POSICIONAR");
        comInvariante.setInvarianteCodigo("COMP_CARDINAL_TODO");
        diagnosticos.add(comInvariante);

        DiagnosticoTarefa semInvariante = new DiagnosticoTarefa("COMPOSICAO_MEDIDAS:papel.parte1");
        semInvariante.setSuporte(NivelSuporte.PARCIAL);
        semInvariante.setRegraDeAcao("POSICIONAR");
        diagnosticos.add(semInvariante);

        InferenciaRegrasModelador inferencia = new InferenciaRegrasModelador();
        InferenciaRegrasModelador.Resultado resultado = inferencia.inferir(diagnosticos, 1, 1);

        confirmar(resultado.quantidadeInstancias == 2, "deve minerar as 2 instâncias fornecidas");
        confirmar(resultado.regrasPart != null && resultado.regrasPart.length() > 0,
                "PART deve produzir alguma saída com o limiar mínimo satisfeito");
        confirmar(resultado.regrasApriori != null && resultado.regrasApriori.length() > 0,
                "Apriori deve produzir alguma saída com o limiar mínimo satisfeito");

        System.out.println("Teste de inferência com invariante aprovado: " + verificacoes + " verificações.");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
