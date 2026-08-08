package gerard.Scaffolding.automatizacao;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AG_AE (item 7 do levantamento de 2026-08-07) — política do quarto tipo
 * de scaffolding da skill gerard-scaffolding-interacao ("automatização de
 * passos"), escopo definido pela usuária: dica de posicionamento de frases
 * sob demanda, um papel-dado por vez.
 *
 * Responsabilidade única desta classe: correlação ação:evento
 * (gerard-semantic-model/REFERENCE.md §4.8, cardinalidade adotada —
 * Alternativa B, 1:N). Pedir a dica do mesmo papel mais de uma vez,
 * enquanto ele continuar não resolvido, é a mesma ação pedagógica — cada
 * exibição repetida é um evento correlacionado por action_id, não um
 * evento avulso; a ação fecha quando o papel é resolvido (ou quando o
 * diagrama muda de situação-problema).
 *
 * "Decidir quando/como uma dica deve continuar valendo" é política
 * pedagógica (gerard-knowledge-locality-principle, "localidade
 * pedagógica") — por isso vive aqui, fora de Main.java/TelaGerard, junto
 * das demais classes Scaffolding* do projeto (ScaffoldingQuestionamento,
 * ScaffoldingAjudaContextual etc.), não como um Map solto no controlador.
 */
public final class ScaffoldingAutomatizacaoPassos {
    private final Map<String, String> acoesAbertasPorPapel = new HashMap<String, String>();

    /**
     * @return o action_id já aberto para este papel, se a ação de "pedir
     *         dica para ele" ainda estiver em curso; caso contrário, abre
     *         uma ação nova (novo UUID) e a devolve. null se papel for
     *         null.
     */
    public String obterOuIniciarAcao(String papel) {
        if (papel == null) {
            return null;
        }
        String existente = acoesAbertasPorPapel.get(papel);
        if (existente != null) {
            return existente;
        }
        String novo = UUID.randomUUID().toString();
        acoesAbertasPorPapel.put(papel, novo);
        return novo;
    }

    /** Encerra a ação aberta para o papel indicado, se houver. */
    public void encerrarAcao(String papel) {
        if (papel != null) {
            acoesAbertasPorPapel.remove(papel);
        }
    }

    /** Verdadeiro quando há uma ação de dica aberta para este papel. */
    public boolean temAcaoAberta(String papel) {
        return papel != null && acoesAbertasPorPapel.containsKey(papel);
    }

    /**
     * Fecha todas as ações abertas — chamado sempre que o diagrama muda de
     * situação-problema, para que nenhum action_id sobreviva à troca (ver
     * Main.limparEstadoDicaPosicionamento).
     */
    public void limpar() {
        acoesAbertasPorPapel.clear();
    }
}
