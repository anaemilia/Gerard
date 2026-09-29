package gerard.interacao.arraste;

/**
 * Fase 7.8 do roteiro de {@code gerard-handlers-de-interacao}: extrai da
 * tela principal o único estado mecânico do gesto de arraste do ponto de
 * controle (ou clique inicial na escala) da barra de Comparação de Medidas.
 *
 * Diferente dos protocolos já extraídos (item textual, elemento textual,
 * elementos/conectores de Vergnaud, quadradinho do Venn), este gesto não
 * acompanha deslocamento relativo nem posição de origem: a cada movimento,
 * quem chama recalcula a proporção e o valor diretamente a partir da
 * posição vertical do ponteiro e da geometria real do eixo
 * ({@code aplicarControleComparacaoPeloMouse}, que já delega a conversão de
 * proporção em valor a {@code RecalculoComparacaoMedidas}/
 * {@code RelacaoEstruturalComparacao} — nenhum desses pertence a este
 * handler). O único fato mecânico que restava solto em {@code Main}, e que
 * este handler agora concentra, é se o gesto está ou não em curso — o mesmo
 * papel de {@code estaAtivo()} nos demais handlers de interação, que
 * {@code TelaGerard} já combina com os outros em
 * {@code existePickupAtivo()}.
 *
 * Deliberadamente FORA do escopo: hit-testing do controle e da escala
 * (depende de {@code Rectangle}/geometria da cena e permanece no
 * adaptador), a conversão de posição em valor (já delegada ao domínio,
 * citada acima), e a confirmação/sincronização semântica ao soltar o mouse
 * (proprietário semântico — permanece em {@code Main}, inclusive a consulta
 * a {@code confirmarValorIncognitaAceito} sobre o papel da diferença).
 *
 * Fase 7.12: também concentra a sequência do pressionamento — fora do
 * controle e da escala o clique não é consumido; dentro, com a manipulação
 * das representações ainda não liberada pela modelagem, o gesto é bloqueado
 * sem iniciar; liberada, o gesto inicia. O hit-test chega pela porta
 * {@link AlvoControleComparacao}; a política de liberação é consultada por
 * quem chama e chega como booleano; bloqueio, fantasma, cursor, valor e log
 * são materializados pelo adaptador da tela.
 */
public final class HandlerInteracaoControleComparacao {

    private boolean ativo;

    /**
     * Sequência do pressionamento: alvo antes da liberação, liberação antes
     * do início (mesma ordem de avaliação anterior em mousePressed).
     */
    public ResultadoPressionamentoControleComparacao pressionar(
            AlvoControleComparacao alvo, int posicaoX, int posicaoY,
            boolean manipulacaoLiberada) {
        if (alvo == null || !alvo.contemControleOuEscala(posicaoX, posicaoY)) {
            return ResultadoPressionamentoControleComparacao.NAO_CONSUMIDO;
        }
        if (!manipulacaoLiberada) {
            return ResultadoPressionamentoControleComparacao.BLOQUEADO;
        }
        iniciar();
        return ResultadoPressionamentoControleComparacao.INICIADO;
    }

    /** Inicia o gesto. */
    public void iniciar() {
        ativo = true;
    }

    /** @return true se o gesto está em curso. */
    public boolean estaAtivo() {
        return ativo;
    }

    /** Encerra o gesto — soltura normal ou reset defensivo de um arraste interrompido. */
    public void concluir() {
        ativo = false;
    }
}
