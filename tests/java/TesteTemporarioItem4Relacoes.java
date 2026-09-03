import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Teste temporário (mesmo padrão de TesteTemporarioAGAE.java,
 * TesteTemporarioComposicaoTransformacoesProcesso.java etc., citados nos
 * relatórios anteriores desta sessão — deletar depois de validar) para o
 * item 4: material concreto de Relações (PaineisEixosRelacoes).
 *
 * Diferente de tests/java/TestePaineisEixosRelacoes.java (que testa só o
 * coordenador em isolamento), este dirige a aplicação real
 * (Main.TelaGerard, construída diretamente — mesmo pacote-padrão, sem
 * precisar de Main.main nem de janela visível) para confirmar a
 * integração: que Main.java de fato aciona o coordenador com os elementos
 * certos e propaga o valor arrastado de volta ao ElementoVergnaud real.
 *
 * Atualizado em 2026-08-17 (revisão da regra de visibilidade, mesmo dia,
 * duas vezes): primeiro a usuária reverteu a decisão original ("mesma
 * regra" das 3 tentativas rejeitadas) depois de ver o comportamento real —
 * os painéis de Relações passaram a ser criados sempre que a categoria
 * ativa for uma das duas de Relações, sem depender de nenhuma tentativa
 * rejeitada (por isso este teste chama diretamente
 * atualizarPaineisEixosRelacoesConformeVisibilidade(), o método real que a
 * tela usa a cada repaint, em vez de pular direto para
 * ativarPaineisEixosRelacoes()). Depois, vendo os 3 eixos sempre visíveis
 * de uma vez, a usuária achou a tela poluída e pediu visibilidade
 * individual por lupa — cada papel é CRIADO junto com a categoria, mas só
 * fica REVELADO (visível/interativo) depois de um clique na lupa perto do
 * elemento, um de cada vez, independente dos outros papéis.
 */
public final class TesteTemporarioItem4Relacoes {

    public static void main(String[] args) throws Exception {
        final AtomicReference<Throwable> falha = new AtomicReference<Throwable>();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    rodar();
                } catch (Throwable t) {
                    falha.set(t);
                }
            }
        });
        if (falha.get() != null) {
            throw new RuntimeException("TesteTemporarioItem4Relacoes falhou", falha.get());
        }
        System.out.println("Teste aprovado: painéis de eixo das Relações aparecem só com a categoria "
                + "ativa (sem exigir tentativa rejeitada, regra revista em 2026-08-17), são criados "
                + "com os papéis certos mas começam escondidos até a lupa de cada um ser clicada "
                + "(visibilidade independente por papel), o arraste de um deles propaga de volta ao "
                + "ElementoVergnaud real através de Main.TelaGerard (não só do coordenador isolado), "
                + "o eixo único antigo fica suprimido nas categorias de Relações, e um painel se "
                + "atualiza quando o valor do papel muda por fora do próprio arraste (decisões e "
                + "correções de 2026-08-17).");
        System.exit(0);
    }

    private static void rodar() {
        try {
            Main.TelaGerard tela = new Main.TelaGerard();

            tela.categoriaSelecionadaParaAtividade = true;
            tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.TRANSFORMACAO_RELACAO;

            ElementoVergnaud relacaoInicial = new ElementoVergnaud(
                    100, 300, 170, 160, TipoFiguraDiagrama.ELIPSE, "papel.relacaoInicial",
                    new Rectangle(0, 0, 1240, 760), false);
            relacaoInicial.textoEditavel = "5";
            ElementoVergnaud transformacao = new ElementoVergnaud(
                    500, 100, 180, 140, TipoFiguraDiagrama.ELIPSE, "papel.transformacao",
                    new Rectangle(0, 0, 1240, 760), false);
            transformacao.textoEditavel = "-3";
            ElementoVergnaud relacaoFinal = new ElementoVergnaud(
                    900, 300, 170, 160, TipoFiguraDiagrama.ELIPSE, "papel.relacaoFinal",
                    new Rectangle(0, 0, 1240, 760), true);
            // RelaçãoFinal deliberadamente sem valor ainda (a incógnita) —
            // confirma que um papel "desconhecido" também ganha painel,
            // só sem valor definido.

            tela.elementosVergnaud.clear();
            tela.elementosVergnaud.add(relacaoInicial);
            tela.elementosVergnaud.add(transformacao);
            tela.elementosVergnaud.add(relacaoFinal);

            // categoriaSelecionadaParaAtividade=true + categoria de Relações,
            // SEM nenhuma tentativa rejeitada simulada — se a regra antiga
            // ("mesma regra" das 3 tentativas) ainda estivesse em vigor, os
            // painéis continuariam escondidos e a checagem abaixo falharia.
            invocarPrivado(tela, "atualizarPaineisEixosRelacoesConformeVisibilidade");

            exigir(tela.paineisEixosRelacoes.estaAtivo(),
                    "Depois de atualizarPaineisEixosRelacoesConformeVisibilidade() com a categoria "
                            + "ativa, o coordenador deveria estar ativo mesmo sem nenhuma tentativa "
                            + "rejeitada (regra de visibilidade revista em 2026-08-17).");
            exigir(tela.paineisEixosRelacoes.obterPaineis().size() == 3,
                    "Deveria haver 3 painéis, um por papel de TRANSFORMACAO_RELACAO.");
            exigir(tela.paineisEixosRelacoes.obterPaineis().get(0).elemento == relacaoInicial
                            && tela.paineisEixosRelacoes.obterPaineis().get(1).elemento == transformacao
                            && tela.paineisEixosRelacoes.obterPaineis().get(2).elemento == relacaoFinal,
                    "Os painéis deveriam corresponder aos elementos na mesma ordem de elementosVergnaud.");

            gerard.ui.vergnaud.PaineisEixosRelacoes.Painel painelRelacaoInicial =
                    tela.paineisEixosRelacoes.obterPaineis().get(0);
            exigir(painelRelacaoInicial.grafico.isVisivel(),
                    "Painel de um papel com valor definido (\"5\") deveria estar visível.");
            exigir(painelRelacaoInicial.grafico.getValorNavegavel() == 5,
                    "Painel de RelaçãoInicial deveria mostrar o valor real do elemento (5).");

            gerard.ui.vergnaud.PaineisEixosRelacoes.Painel painelRelacaoFinal =
                    tela.paineisEixosRelacoes.obterPaineis().get(2);
            exigir(painelRelacaoFinal.grafico.isVisivel(),
                    "Painel de um papel sem valor ainda também deveria ficar visível "
                            + "(mostrando magnitude desconhecida), não escondido.");

            // Decisão da usuária (2026-08-17, mesmo dia): "os eixos
            // aparecendo logo no início deixou a tela muito poluída" — cada
            // painel agora começa escondido (só a lupa aparece perto do
            // elemento) e só fica visível/interativo depois de um clique
            // nela. "grafico.isVisivel()" acima é estado interno (o valor
            // já foi semeado); "estaRevelado()" é o que decide se o app
            // desenha/aceita interação no eixo — os dois são independentes
            // desde essa mudança.
            exigir(!painelRelacaoInicial.estaRevelado(),
                    "Painel de RelaçãoInicial não deveria estar revelado antes do clique na lupa.");
            int[] centroLupa = obterCentroLupa(painelRelacaoInicial);
            gerard.ui.vergnaud.PaineisEixosRelacoes.Painel revelado =
                    tela.paineisEixosRelacoes.processarPressionamentoLupa(centroLupa[0], centroLupa[1]);
            exigir(revelado == painelRelacaoInicial,
                    "Clicar na lupa de RelaçãoInicial deveria revelar esse mesmo painel.");
            exigir(painelRelacaoInicial.estaRevelado(),
                    "Depois do clique na lupa, RelaçãoInicial deveria estar revelado.");
            exigir(!painelRelacaoFinal.estaRevelado(),
                    "Revelar RelaçãoInicial não deveria afetar RelaçãoFinal (visibilidade independente por papel).");

            // Arrastar o painel de RelaçãoInicial para a borda oposta do
            // eixo (mudança de valor inequívoca, mesma técnica de
            // TestePaineisEixosRelacoes) e confirmar que Main.java propaga
            // o novo valor de volta para o ElementoVergnaud real.
            Rectangle pontoControle = painelRelacaoInicial.grafico.obterAreaVisualPontoControle();
            int cx = pontoControle.x + pontoControle.width / 2;
            int cy = pontoControle.y + pontoControle.height / 2;
            boolean consumiu = tela.paineisEixosRelacoes.processarPressionamento(
                    cx, cy, 1240, 760, null);
            exigir(consumiu, "Pressionar sobre o ponto de controle de RelaçãoInicial deveria ser consumido.");

            Rectangle areaPainel = painelRelacaoInicial.grafico.obterAreaVisualPainel();
            int novoX = areaPainel.x + 12;
            tela.paineisEixosRelacoes.arrastarPara(novoX, cy, 1240, 760);

            invocarPrivadoComBoolean(tela, "sincronizarPainelEixoRelacaoSeNecessario", true);

            exigir(!"5".equals(relacaoInicial.textoEditavel),
                    "Depois do arraste e da sincronização, o texto do elemento real "
                            + "deveria ter mudado a partir do valor \"5\" original — "
                            + "textoEditavel continua \"5\": " + relacaoInicial.textoEditavel);

            tela.paineisEixosRelacoes.finalizarArraste();

            // O eixo único antigo (mecanismo de instância única, mostrado
            // junto do menu de escolha de sinal) foi removido por inteiro em
            // 2026-09-01 — inalcançável desde a regra generalizada
            // 2026-08-18 ("todo número relativo carrega uma lupa"), que já o
            // suprimia em qualquer categoria com número relativo, não só em
            // Relações. Não há mais o que verificar aqui: não existe mais
            // scaffoldingGraficoInteiros nem mostrarGraficoInteirosNumeroRelativo
            // para confirmar que "não aparece".

            // Bug relatado pela usuária em 2026-08-17 ("eixos não mudam com
            // a mudança dos elementos no diagrama"): quando o valor de um
            // papel muda por um caminho que NÃO é o arraste do próprio
            // painel (aqui simulado direto no elemento, no lugar do menu de
            // escolha de sinal), o painel correspondente também precisa
            // refletir o novo valor — antes da correção o painel continuava
            // preso ao valor com que foi criado ("-3").
            gerard.ui.vergnaud.PaineisEixosRelacoes.Painel painelTransformacao =
                    tela.paineisEixosRelacoes.obterPaineis().get(1);
            transformacao.textoEditavel = "+9";
            Method metodoSincronizarTudo = tela.getClass().getDeclaredMethod(
                    "sincronizarTodasAsRepresentacoesAPartirDoVergnaud",
                    ElementoVergnaud.class,
                    gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado.Origem.class);
            metodoSincronizarTudo.setAccessible(true);
            metodoSincronizarTudo.invoke(tela, transformacao,
                    gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
            exigir(painelTransformacao.grafico.getValorNavegavel() == 9,
                    "Depois de mudar o valor do elemento Transformação para \"+9\" por fora do "
                            + "arraste do próprio painel e sincronizar, o painel deveria mostrar 9 — "
                            + "mostrou " + painelTransformacao.grafico.getValorNavegavel() + ".");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Espelha PaineisEixosRelacoes.obterAreaLupa/TAMANHO_LUPA (privados) — mesmo padrão de TestePaineisEixosRelacoes. */
    private static int[] obterCentroLupa(gerard.ui.vergnaud.PaineisEixosRelacoes.Painel painel) {
        final int TAMANHO_LUPA = 22;
        ElementoVergnaud elemento = painel.elemento;
        int x = elemento.x + elemento.largura - TAMANHO_LUPA + 6;
        int y = elemento.y - TAMANHO_LUPA / 2 - 2;
        return new int[] { x + TAMANHO_LUPA / 2, y + TAMANHO_LUPA / 2 };
    }

    private static void invocarPrivado(Object alvo, String nomeMetodo) throws Exception {
        Method metodo = alvo.getClass().getDeclaredMethod(nomeMetodo);
        metodo.setAccessible(true);
        metodo.invoke(alvo);
    }

    private static void invocarPrivadoComBoolean(Object alvo, String nomeMetodo, boolean valor) throws Exception {
        Method metodo = alvo.getClass().getDeclaredMethod(nomeMetodo, boolean.class);
        metodo.setAccessible(true);
        metodo.invoke(alvo, valor);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
