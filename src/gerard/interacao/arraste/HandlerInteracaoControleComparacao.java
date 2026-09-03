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
 */
public final class HandlerInteracaoControleComparacao {

    private boolean ativo;

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
