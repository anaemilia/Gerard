package gerard.campoaditivo.diagrama.modelo;

import java.util.List;

/**
 * Decide se os painéis de eixo revelados pela lupa devem estar disponíveis
 * para a cena atual: existe pelo menos uma figura marcada para exibir lupa
 * ({@link FiguraDiagrama#isExibirLupa()}). Regra estrutural — não depende de
 * categoria nem de posição na lista; qualquer figura com lupa já habilita a
 * disponibilidade para a cena inteira.
 *
 * Extraída em 2026-09-01 de {@code Main.TelaGerard.devemExibirPaineisEixosRelacoes}
 * (ver {@code LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md}, "Próxima
 * fronteira recomendada"): a mesma regra precisa valer tanto para o
 * adaptador Swing quanto para a API web — hoje
 * {@code ServicoSorteioAtividadeWeb} publica {@code exibir_lupa} por figura,
 * mas não a decisão agregada da cena; sem um ponto único, um futuro
 * consumidor web (React, por exemplo) teria que recalculá-la de forma
 * independente, reintroduzindo a mesma duplicação já eliminada de `Main`
 * para as demais regras aditivas.
 *
 * NÃO decide qual mecanismo de eixo mostrar para um papel específico
 * (painéis por papel das categorias de Relações, ou o eixo único legado das
 * demais categorias) nem unifica os dois — essa permanece uma decisão de
 * design maior, ainda em aberto, que depende de validação por compilação e
 * pela bateria de regressão do projeto (ver mesma seção do levantamento).
 */
public final class DecisaoExibicaoPaineisEixo {

    private DecisaoExibicaoPaineisEixo() {
    }

    /** Variante para quem já tem a lista de figuras portáteis da cena (ex.: API web). */
    public static boolean existeAlgumComLupa(List<FiguraDiagrama> figuras) {
        if (figuras == null) {
            return false;
        }
        for (FiguraDiagrama figura : figuras) {
            if (figura != null && figura.isExibirLupa()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Variante para quem já reduziu cada elemento ao próprio booleano de
     * lupa (ex.: o adaptador Swing, que consulta {@code ElementoVergnaud}
     * em vez de {@link FiguraDiagrama} diretamente).
     */
    public static boolean existeAlgumComLupa(boolean[] flagsExibirLupa) {
        if (flagsExibirLupa == null) {
            return false;
        }
        for (boolean flag : flagsExibirLupa) {
            if (flag) {
                return true;
            }
        }
        return false;
    }
}
