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

    /**
     * Aceita tanto figuras da cena portátil quanto elementos já materializados
     * por um adaptador, sem exigir que o adaptador reconstrua flags paralelas.
     */
    public static boolean existeAlgumComLupa(
            List<? extends ElementoComLupa> elementos) {
        if (elementos == null) {
            return false;
        }
        for (ElementoComLupa elemento : elementos) {
            if (elemento != null && elemento.isExibirLupa()) {
                return true;
            }
        }
        return false;
    }
}
