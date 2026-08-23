import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.ui.vergnaud.PaineisEixosRelacoes;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Harness executável de PaineisEixosRelacoes — item 4 do levantamento de
 * pendências (representação complementar própria de Relações), mesmo
 * padrão sem JUnit dos demais testes deste projeto.
 *
 * Não reimplementa nem retesta a matemática interna de
 * ScaffoldingGraficoInteiros (eixo, escala, ponto de controle) — já é uma
 * classe reaproveitada, própria, com sua própria responsabilidade. Este
 * teste cobre o que é novo aqui: gerir várias instâncias dela ao mesmo
 * tempo (ciclo de vida ativar/desativar, agregação de estado — arrastando,
 * contém ponto de controle, natureza da interação — e isolamento entre
 * painéis). Onde precisa de coordenadas reais de clique, usa os retângulos
 * que a própria instância já calculou (obterAreaVisualPontoControle, etc.)
 * em vez de adivinhar pixels.
 *
 * Atualizado em 2026-08-17: cada painel agora começa escondido (só a lupa
 * aparece) e só fica visível/interativo depois de
 * {@code processarPressionamentoLupa} — decisão da usuária, "os eixos
 * aparecendo logo no início deixou a tela muito poluída". Os testes de
 * arraste/isolamento por isso revelam o painel primeiro (helper
 * {@code revelarPainel}, que espelha a mesma geometria de
 * {@code obterAreaLupa}, privada em PaineisEixosRelacoes — mesmo padrão já
 * usado aqui para o ponto de controle/painel, coordenadas reais, não
 * adivinhadas por fora).
 */
public final class TestePaineisEixosRelacoes {

    private static final int LARGURA_TELA = 1240;
    private static final int ALTURA_TELA = 760;

    public static void main(String[] args) {
        testarCicloDeVidaEIdempotencia();
        testarAgregadosComListaVazia();
        testarArrasteDeUmPainel();
        testarIsolamentoEntreDoisPaineis();
        testarVisibilidadePorLupa();

        System.out.println("Teste aprovado: PaineisEixosRelacoes gere várias instâncias de "
                + "ScaffoldingGraficoInteiros com ciclo de vida, agregação de estado, "
                + "isolamento e visibilidade individual por lupa corretos entre painéis.");
    }

    private static void testarCicloDeVidaEIdempotencia() {
        PaineisEixosRelacoes coordenador = new PaineisEixosRelacoes();
        exigir(!coordenador.estaAtivo(), "Coordenador recém-criado não deveria estar ativo.");
        exigir(coordenador.obterPaineis().isEmpty(), "Sem ativar, não deveria haver painéis.");

        // ativar(null) não deveria lançar exceção nem deixar de marcar ativo.
        coordenador.ativar(null);
        exigir(coordenador.estaAtivo(), "ativar(null) ainda deveria marcar o coordenador como ativo.");
        exigir(coordenador.obterPaineis().isEmpty(), "ativar(null) não deveria criar painéis.");
        coordenador.desativar();

        List<ElementoVergnaud> elementos = criarElementos(3);
        coordenador.ativar(elementos);
        exigir(coordenador.estaAtivo(), "Depois de ativar com 3 elementos, deveria estar ativo.");
        exigir(coordenador.obterPaineis().size() == 3,
                "Deveria haver um painel por elemento (3).");
        for (int i = 0; i < 3; i++) {
            exigir(coordenador.obterPaineis().get(i).elemento == elementos.get(i),
                    "Cada painel deveria referenciar o elemento correspondente, na mesma ordem.");
        }

        // Ativar de novo enquanto já ativo não deveria recriar os painéis
        // (nem duplicá-los) — proteção contra reset de posição/arraste do
        // usuário a cada repaint.
        coordenador.ativar(criarElementos(3));
        exigir(coordenador.obterPaineis().size() == 3,
                "ativar() enquanto já ativo não deveria recriar/duplicar painéis.");
        exigir(coordenador.obterPaineis().get(0).elemento == elementos.get(0),
                "ativar() enquanto já ativo não deveria trocar os elementos já geridos.");

        // desativar() deve esconder (ocultar) os gráficos que estavam
        // visíveis, além de esvaziar a lista.
        PaineisEixosRelacoes.Painel primeiroPainel = coordenador.obterPaineis().get(0);
        primeiroPainel.apresentador.mostrar(new Rectangle(10, 10, 40, 40), "5");
        exigir(primeiroPainel.grafico.isVisivel(),
                "mostrar() deveria deixar o gráfico do painel visível (pré-condição do teste).");
        coordenador.desativar();
        exigir(!coordenador.estaAtivo(), "Depois de desativar, não deveria estar ativo.");
        exigir(coordenador.obterPaineis().isEmpty(), "Depois de desativar, não deveria haver painéis.");
        exigir(!primeiroPainel.grafico.isVisivel(),
                "desativar() deveria esconder (ocultar) os gráficos dos painéis antigos.");
    }

    private static void testarAgregadosComListaVazia() {
        PaineisEixosRelacoes coordenador = new PaineisEixosRelacoes();
        // Sem nenhum painel ativo, todo agregado deveria ser seguro (sem
        // exceção) e devolver o valor neutro.
        exigir(!coordenador.estaArrastando(), "Sem painéis, estaArrastando() deveria ser falso.");
        exigir(coordenador.encontrarArrastando() == null,
                "Sem painéis, não deveria haver painel arrastando.");
        exigir(coordenador.encontrarComAlteracaoPorInteracao() == null,
                "Sem painéis, não deveria haver alteração pendente.");
        exigir(!coordenador.contemPontoControle(100, 100),
                "Sem painéis, contemPontoControle() deveria ser falso.");
        exigir(!coordenador.contemAlgumPainel(100, 100),
                "Sem painéis, contemAlgumPainel() deveria ser falso.");
        exigir(!coordenador.contemBotaoEsconder(100, 100),
                "Sem painéis, contemBotaoEsconder() deveria ser falso.");
        exigir("".equals(coordenador.obterDicaBotaoEsconder()),
                "Sem painéis, a dica do botão de esconder deveria ser vazia, não nula/exceção.");
        exigir("".equals(coordenador.obterDicaPontoControle()),
                "Sem painéis, a dica do ponto de controle deveria ser vazia, não nula/exceção.");
        exigir(!coordenador.processarPressionamento(100, 100, LARGURA_TELA, ALTURA_TELA, null),
                "Sem painéis, processarPressionamento() deveria devolver falso.");

        // Nenhuma destas chamadas deveria lançar exceção com a lista vazia.
        coordenador.arrastarPara(100, 100, LARGURA_TELA, ALTURA_TELA);
        coordenador.finalizarArraste();
        coordenador.atualizarFocoBotaoEsconder(100, 100);
        coordenador.limparFocoBotaoEsconder();
        coordenador.desenhar(null, LARGURA_TELA, ALTURA_TELA, null);
        coordenador.desenharPontosControleEmPrimeiroPlano(null);
    }

    private static void testarArrasteDeUmPainel() {
        PaineisEixosRelacoes coordenador = new PaineisEixosRelacoes();
        List<ElementoVergnaud> elementos = criarElementos(1);
        coordenador.ativar(elementos);
        PaineisEixosRelacoes.Painel painel = coordenador.obterPaineis().get(0);

        painel.apresentador.registrarEscolha(
                new Rectangle(elementos.get(0).x, elementos.get(0).y, 60, 40), "5", "+");
        exigir(painel.grafico.isVisivel(), "registrarEscolha() deveria deixar o painel visível.");
        exigir(!painel.estaRevelado(),
                "Painel recém-criado não deveria estar revelado ainda (só a lupa aparece).");
        revelarPainel(coordenador, painel);
        exigir(painel.estaRevelado(), "Depois de clicar na lupa, o painel deveria estar revelado.");

        Rectangle pontoControle = painel.grafico.obterAreaVisualPontoControle();
        int cx = pontoControle.x + pontoControle.width / 2;
        int cy = pontoControle.y + pontoControle.height / 2;

        exigir(coordenador.contemPontoControle(cx, cy),
                "O coordenador deveria reconhecer o ponto de controle do único painel ativo.");

        boolean consumiu = coordenador.processarPressionamento(
                cx, cy, LARGURA_TELA, ALTURA_TELA, null);
        exigir(consumiu, "Pressionar sobre o ponto de controle deveria ser consumido pelo coordenador.");
        exigir(coordenador.estaArrastando(), "Depois do pressionamento, deveria estar arrastando.");
        exigir(coordenador.encontrarArrastando() == painel,
                "O painel arrastando deveria ser o único painel ativo.");

        // Arrastar para a borda esquerda do painel garante uma mudança de
        // valor inequívoca (o valor inicial, 5, com escala mínima 5,
        // começa bem perto da borda direita — um deslocamento pequeno e
        // arbitrário poderia cair na mesma marca inteira por causa do
        // arredondamento; a borda oposta nunca cai na mesma marca).
        Rectangle areaPainel = painel.grafico.obterAreaVisualPainel();
        int novoX = areaPainel.x + 12;
        coordenador.arrastarPara(novoX, cy, LARGURA_TELA, ALTURA_TELA);
        exigir(coordenador.encontrarComAlteracaoPorInteracao() == painel,
                "Depois de arrastar para a borda oposta do eixo, o painel deveria reportar "
                        + "alteração pendente.");

        coordenador.finalizarArraste();
        exigir(!coordenador.estaArrastando(),
                "finalizarArraste() deveria encerrar o arraste em todos os painéis.");
    }

    private static void testarIsolamentoEntreDoisPaineis() {
        PaineisEixosRelacoes coordenador = new PaineisEixosRelacoes();
        List<ElementoVergnaud> elementos = criarElementos(2);
        coordenador.ativar(elementos);
        PaineisEixosRelacoes.Painel painelA = coordenador.obterPaineis().get(0);
        PaineisEixosRelacoes.Painel painelB = coordenador.obterPaineis().get(1);

        painelA.apresentador.registrarEscolha(new Rectangle(0, 0, 60, 40), "3", "+");
        painelB.apresentador.registrarEscolha(new Rectangle(400, 0, 60, 40), "9", "-");
        // Painéis flutuantes de larguras/posições padrão iguais colidiriam
        // no mesmo lugar (ambos calculados a partir do mesmo topo padrão);
        // afasta o segundo manualmente para o teste poder mirar cada um
        // sem ambiguidade — mesma operação que Main.java já faz na
        // ativação real (definirPosicaoInicial).
        painelB.grafico.definirPosicaoInicial(700, 400);
        revelarPainel(coordenador, painelA);
        revelarPainel(coordenador, painelB);

        Rectangle pontoA = painelA.grafico.obterAreaVisualPontoControle();
        Rectangle pontoB = painelB.grafico.obterAreaVisualPontoControle();
        int cxA = pontoA.x + pontoA.width / 2;
        int cyA = pontoA.y + pontoA.height / 2;
        int cxB = pontoB.x + pontoB.width / 2;
        int cyB = pontoB.y + pontoB.height / 2;

        exigir(coordenador.processarPressionamento(cxA, cyA, LARGURA_TELA, ALTURA_TELA, null),
                "Pressionar no ponto de controle de A deveria ser consumido.");
        exigir(coordenador.encontrarArrastando() == painelA,
                "Deveria ser o painel A arrastando, não B.");
        exigir(!painelB.grafico.estaArrastando(),
                "B não deveria ser afetado pelo arraste iniciado em A (isolamento).");
        coordenador.finalizarArraste();
        exigir(!coordenador.estaArrastando(), "Depois de finalizar, nenhum painel deveria estar arrastando.");

        exigir(coordenador.processarPressionamento(cxB, cyB, LARGURA_TELA, ALTURA_TELA, null),
                "Pressionar no ponto de controle de B deveria ser consumido.");
        exigir(coordenador.encontrarArrastando() == painelB,
                "Depois de pressionar em B, deveria ser B arrastando, não A.");
        exigir(!painelA.grafico.estaArrastando(),
                "A não deveria ser afetado pelo arraste iniciado em B (isolamento).");
        coordenador.finalizarArraste();
    }

    private static void testarVisibilidadePorLupa() {
        PaineisEixosRelacoes coordenador = new PaineisEixosRelacoes();
        List<ElementoVergnaud> elementos = criarElementos(2);
        coordenador.ativar(elementos);
        PaineisEixosRelacoes.Painel painelA = coordenador.obterPaineis().get(0);
        PaineisEixosRelacoes.Painel painelB = coordenador.obterPaineis().get(1);
        painelA.apresentador.registrarEscolha(new Rectangle(elementos.get(0).x, elementos.get(0).y, 60, 40), "4", "+");
        painelB.apresentador.registrarEscolha(new Rectangle(elementos.get(1).x, elementos.get(1).y, 60, 40), "2", "+");
        painelB.grafico.definirPosicaoInicial(700, 400);

        exigir(!painelA.estaRevelado() && !painelB.estaRevelado(),
                "Os dois painéis deveriam começar escondidos (só a lupa aparece).");

        // Mesmo com um valor/posição já semeados, nenhuma interação de eixo
        // deveria alcançar um painel ainda não revelado — a lupa é o único
        // jeito de chegar até ele.
        Rectangle pontoControleA = painelA.grafico.obterAreaVisualPontoControle();
        exigir(!coordenador.contemPontoControle(
                        pontoControleA.x + pontoControleA.width / 2, pontoControleA.y + pontoControleA.height / 2),
                "Antes de revelar, o ponto de controle de A não deveria ser reconhecido pelo coordenador.");
        exigir(coordenador.contemLupa(obterCentroLupa(painelA)[0], obterCentroLupa(painelA)[1]),
                "Antes de revelar, a lupa de A deveria responder no ponto onde ela é desenhada.");

        // Revelar A não deveria afetar B (visibilidade independente, "uma
        // por vez", decisão da usuária).
        revelarPainel(coordenador, painelA);
        exigir(painelA.estaRevelado(), "Depois da lupa, A deveria estar revelado.");
        exigir(!painelB.estaRevelado(), "Revelar A não deveria revelar B (independentes).");
        exigir(!coordenador.contemLupa(obterCentroLupa(painelA)[0], obterCentroLupa(painelA)[1]),
                "A lupa de um painel já revelado não deveria mais responder a cliques (o eixo tomou o lugar dela).");
        exigir(coordenador.contemLupa(obterCentroLupa(painelB)[0], obterCentroLupa(painelB)[1]),
                "A lupa de B, ainda não revelado, continua ativa.");

        // Agora que A está revelado, a interação de eixo passa a alcançá-lo.
        exigir(coordenador.contemPontoControle(
                        pontoControleA.x + pontoControleA.width / 2, pontoControleA.y + pontoControleA.height / 2),
                "Depois de revelar, o ponto de controle de A deveria ser reconhecido pelo coordenador.");

        revelarPainel(coordenador, painelB);
        exigir(painelA.estaRevelado() && painelB.estaRevelado(),
                "Depois de revelar os dois, ambos deveriam estar revelados ao mesmo tempo (nada impede múltiplos abertos).");
    }

    /** Espelha PaineisEixosRelacoes.obterAreaLupa (privada) para clicar na lupa certa a partir do teste. */
    private static int[] obterCentroLupa(PaineisEixosRelacoes.Painel painel) {
        ElementoVergnaud elemento = painel.elemento;
        int x = elemento.x + elemento.largura - TAMANHO_LUPA + 6;
        int y = elemento.y - TAMANHO_LUPA / 2 - 2;
        return new int[] { x + TAMANHO_LUPA / 2, y + TAMANHO_LUPA / 2 };
    }

    private static void revelarPainel(PaineisEixosRelacoes coordenador, PaineisEixosRelacoes.Painel painel) {
        int[] centro = obterCentroLupa(painel);
        PaineisEixosRelacoes.Painel revelado = coordenador.processarPressionamentoLupa(centro[0], centro[1]);
        exigir(revelado == painel,
                "processarPressionamentoLupa nas coordenadas da lupa de \"" + painel.elemento.rotulo
                        + "\" deveria revelar esse mesmo painel — revelou "
                        + (revelado == null ? "nenhum" : revelado.elemento.rotulo) + ".");
    }

    /** Espelha PaineisEixosRelacoes.TAMANHO_LUPA (privada, 22px). */
    private static final int TAMANHO_LUPA = 22;

    private static List<ElementoVergnaud> criarElementos(int quantidade) {
        List<ElementoVergnaud> elementos = new ArrayList<ElementoVergnaud>();
        for (int i = 0; i < quantidade; i++) {
            elementos.add(new ElementoVergnaud(
                    10 + i * 200, 100, 150, 100,
                    TipoFiguraDiagrama.ELIPSE, "papel" + i,
                    new Rectangle(0, 0, 1240, 760), false));
        }
        return elementos;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
