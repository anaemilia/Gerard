import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.pesquisador.replay.robot.ComponenteLocalizado;

/**
 * Localiza um componente da tela por papel semântico, SEMPRE relido do
 * estado atual (nunca de uma coordenada guardada de uma chamada anterior) —
 * ver pacote de correção rodada 3 (2026-07-31), "GestureCoordinateResolver
 * ... nunca reutilize coordenadas obtidas antes de feedback, tremor ou
 * animação".
 *
 * Correção do bug raiz encontrado nesta rodada: um numeral que já foi
 * arrastado para o diagrama (mesmo com veredito ERRADO) continua com
 * {@code chavePapel} preenchido em {@code itensArrastaveis}, mas
 * {@code PoliticaUnicidadeElementoMatematicoTexto.jaEstaNoDiagrama} passa a
 * bloquear um novo pickup a partir do marcador de texto original
 * ({@code Main.mousePressed}, ramo do {@code MarcadorTexto}) — silenciosamente,
 * sem passar pelo {@code encontrarItemArrastavel} que trataria o item onde
 * ele REALMENTE está agora. {@code localizarOrigem} por isso procura
 * PRIMEIRO em {@code itensArrastaveis} (posição atual, se já estiver no
 * diagrama) e só cai para {@code elementosTexto} (posição original no
 * enunciado) se o item ainda não tiver sido posicionado — replicando a
 * prioridade real que {@code Main.mousePressed} usa
 * (marcador de texto bloqueado quando já posicionado; item do diagrama
 * sempre pegável de novo via {@code encontrarItemArrastavel}).
 *
 * Fica no pacote default (como {@code TesteMonkeyGuiadoPorCasosReais}) só
 * por causa da visibilidade de pacote dos campos de {@code Main.TelaGerard}
 * — não é solução colocada na Main, é infraestrutura de teste que precisa
 * ler o mesmo estado que o Robot está manipulando.
 */
final class SemanticComponentLocator {

    private SemanticComponentLocator() {
    }

    static ComponenteLocalizado localizarOrigem(Main.TelaGerard tela, String chavePapelSemantico) {
        if (chavePapelSemantico == null) {
            return null;
        }
        for (ItemTextoArrastavel item : tela.itensArrastaveis) {
            if (item != null && chavePapelSemantico.equals(item.chavePapel) && item.estaNoDiagrama()) {
                return new ComponenteLocalizado(item.x, item.y, item.largura, item.altura,
                        ComponenteLocalizado.ORIGEM_ITEM_ARRASTAVEL_DIAGRAMA, chavePapelSemantico);
            }
        }
        for (ElementoTextoMovel texto : tela.elementosTexto) {
            if (texto.possuiVinculoSemantico() && chavePapelSemantico.equals(texto.chavePapelSemantico)) {
                return new ComponenteLocalizado(texto.x, texto.y, texto.largura, texto.altura,
                        ComponenteLocalizado.ORIGEM_TEXTO_ENUNCIADO, chavePapelSemantico);
            }
        }
        return null;
    }

    static ComponenteLocalizado localizarDestino(Main.TelaGerard tela, String chavePapelAlvo) {
        int indiceAlvo = tela.scaffoldingQuestionamento.obterIndiceElementoPorPapel(
                chavePapelAlvo, tela.tipoSituacaoSelecionada, false,
                tela.quantidadePassosTransformacaoComposta);
        if (indiceAlvo < 0 || indiceAlvo >= tela.elementosVergnaud.size()) {
            return null;
        }
        ElementoVergnaud elemento = tela.elementosVergnaud.get(indiceAlvo);
        return new ComponenteLocalizado(elemento.x, elemento.y, elemento.largura, elemento.altura,
                "elemento_vergnaud_diagrama", chavePapelAlvo);
    }

    /**
     * Localiza a interrogação ("?") a caminho de um papel-alvo — mesma
     * prioridade de {@link #localizarOrigem}: se já estiver no diagrama
     * (posicionada de uma tentativa anterior), usa a posição atual; senão,
     * procura no pool de texto do enunciado.
     */
    static ComponenteLocalizado localizarInterrogacao(Main.TelaGerard tela, String chavePapelAlvo) {
        return localizarOrigem(tela, chavePapelAlvo);
    }

    static String valorDoComponente(Main.TelaGerard tela, ComponenteLocalizado localizado) {
        if (localizado == null) {
            return null;
        }
        if (ComponenteLocalizado.ORIGEM_ITEM_ARRASTAVEL_DIAGRAMA.equals(localizado.getOrigem())) {
            for (ItemTextoArrastavel item : tela.itensArrastaveis) {
                if (item != null && item.x == localizado.getX() && item.y == localizado.getY()
                        && localizado.getComponentId().equals(item.chavePapel)) {
                    return item.valor;
                }
            }
        } else {
            for (ElementoTextoMovel texto : tela.elementosTexto) {
                if (texto.possuiVinculoSemantico()
                        && localizado.getComponentId().equals(texto.chavePapelSemantico)) {
                    return texto.valor;
                }
            }
        }
        return null;
    }
}
