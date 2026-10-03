import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.diagrama.elementos.*;
import gerard.ui.ajuda.PainelAjudaNarrativaVisualCategoria;

/** Toda alteração da atividade ocorre por mouse/teclado reais; leituras verificam o resultado. */
public class TesteRobotHistorinhasNovas extends TesteRobotHistorinhasCategoriasRelativas {
    public static void main(String[] args) throws Exception {
        evidencias = new File(args[0]); evidencias.mkdirs();
        new gerard.agente.modelousuario.RepositorioModeloUsuario().cadastrarPerfil(
                "Teste folhas ilustradas", 10, gerard.agente.modelousuario.Genero.OUTRO,
                gerard.agente.modelousuario.MidiaPreferida.VIDEO,
                gerard.agente.modelousuario.NivelEscolaridade.PRIMEIRO_GRAU, null);
        final Main[] janela = new Main[1];
        SwingUtilities.invokeAndWait(() -> {
            janela[0] = new Main(); janela[0].setAlwaysOnTop(true);
            janela[0].setVisible(true); janela[0].toFront();
        });
        Robot r = new Robot(); r.setAutoDelay(20); Thread.sleep(2000); fechar(r);
        Main.TelaGerard t = tela(janela[0]); entrar(r,t);
        String[][] alvos = {
            {"TRANSFORMACAO_MEDIDAS", "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160", "maria_e_as_figurinhas"},
            {"TRANSFORMACAO_MEDIDAS", "PO_TRANSFORMACAO_MEDIDAS_frutas_93128185", "nadia_e_os_morangos"},
            {"COMPARACAO_MEDIDAS", "PO_COMPARACAO_MEDIDAS_bolas_487868670", "paulo_e_jose"},
            {"COMPOSICAO_TRANSFORMACOES", "PO_TRANSFORMACAO_COMPOSTA_DOIS_PASSOS_chocolates_1269597383", "geisa_e_a_caixa_de_chocolates"}
        };
        for (String[] alvo : alvos) {
            TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(alvo[0]);
            JButton sortear = tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                    ? t.botaoFerramentaSortearRelacoes : t.botaoFerramentaSortearMedidas;
            boolean encontrou=false;
            for(int i=0;i<160;i++) {
                clicar(r,sortear); r.waitForIdle(); r.delay(250); fechar(r);
                if(t.situacaoProblemaAtual!=null && alvo[1].equals(t.situacaoProblemaAtual.getId())) { encontrou=true; break; }
            }
            if(!encontrou) throw new AssertionError("não sorteou " + alvo[1]);
            clicar(r,tipo==TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS?t.botaoAtalhoTransformacao
                    :tipo==TipoSituacaoAditiva.COMPARACAO_MEDIDAS?t.botaoAtalhoComparacao:t.botaoAtalhoComposicaoTransformacoes);
            r.waitForIdle(); r.delay(500); fechar(r);
            System.out.println("SITUACAO="+t.situacaoProblemaAtual.getId());
            verificar(alvo[2]+": sem apoio antes das rejeições", !historinhaVisivel(t));
            Point o=t.getLocationOnScreen();
            java.util.List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>(t.elementosTexto);
            for(int passada=0;passada<2;passada++) for(ElementoTextoMovel tx:textos) {
                if(!tx.possuiVinculoSemantico()) continue;
                SemanticaCuradaSituacao.PapelCurado pc=SemanticaCuradaSituacao.buscar(t.situacaoProblemaAtual,null,tx.chavePapelSemantico);
                if(pc==null || pc.isDesconhecido()!=(passada==1)) continue;
                ElementoVergnaud destino=null;
                for(ElementoVergnaud e:t.elementosVergnaud) if(e.chavePapelSemantico.equals(tx.chavePapelSemantico)) destino=e;
                if(destino==null) continue;
                arrastar(r,new Point(o.x+tx.x+tx.largura/2,o.y+tx.y-tx.altura/2),
                        new Point(o.x+destino.x+destino.largura/2,o.y+destino.y+destino.altura/2));
                escolherSinal(r,pc.getValor().startsWith("-"));
            }
            if(t.seletorOperacaoRelacaoAluno.estaAtivo()) clicarOperacao(r,t,t.seletorOperacaoRelacaoAluno,
                    AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(tipo,t.situacaoProblemaAtual,TipoOperacaoSeletor.ENTRE_TRANSFORMACOES));
            if(t.seletorOperacaoEstadoTransformacaoAluno.estaAtivo()) clicarOperacao(r,t,t.seletorOperacaoEstadoTransformacaoAluno,
                    AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(tipo,t.situacaoProblemaAtual,TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO));
            for(int n=1;n<=3;n++) {
                digitarComFoco(r,t,o,Integer.toString(n)); r.delay(700);
                // Uma resposta explícita; Enter repetido no editor reenviaria a mesma proposta.
                for(Window w:Window.getWindows()) if(w instanceof JDialog && w.isShowing() && acharCampo(w)==null) {
                    Component sim=acharBotao(w,gerard.i18n.ServicoLocalizacao.getInstancia().texto("ui.completion.yes"),false);
                    if(sim!=null) { clicar(r,sim); break; }
                }
                r.delay(300); r.keyPress(java.awt.event.KeyEvent.VK_ESCAPE); r.keyRelease(java.awt.event.KeyEvent.VK_ESCAPE);
                r.waitForIdle(); r.delay(700);
                verificar(alvo[2]+": apoio na rejeição "+n,historinhaVisivel(t)==(n==3));
                capturar(r,alvo[2]+"_rejeicao_"+n);
            }
            verificar(alvo[2]+": painel ilustrado",t.painelHistorinhasComplementar instanceof PainelAjudaNarrativaVisualCategoria);
            if(t.painelHistorinhasComplementar instanceof PainelAjudaNarrativaVisualCategoria)
                verificar(alvo[2]+": animação da própria situação",("projecao:"+alvo[2]).equals(
                        ((PainelAjudaNarrativaVisualCategoria)t.painelHistorinhasComplementar).getChaveRepertorio()));
            boolean imagemDisposta=false;
            for(Component c:t.painelHistorinhasComplementar.getComponents())
                if(c instanceof JLabel && ((JLabel)c).getIcon()!=null && c.getWidth()>0 && c.getHeight()>0) imagemDisposta=true;
            verificar(alvo[2]+": imagem com área visível",imagemDisposta);
            r.delay(2400); capturar(r,alvo[2]+"_animacao");
        }
        System.out.println("RESUMO falhas="+falhas);
        janela[0].dispose(); System.exit(falhas==0?0:1);
    }
}
