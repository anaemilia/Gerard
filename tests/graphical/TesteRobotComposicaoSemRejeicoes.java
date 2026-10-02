import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.diagrama.elementos.*;

/** Rodada limpa por mouse real; reflexão apenas para observar o gatilho. */
public class TesteRobotComposicaoSemRejeicoes extends TesteRobotHistorinhasCategoriasRelativas {
    public static void main(String[] args) throws Exception {
        evidencias=new java.io.File(args[0]);evidencias.mkdirs();
        new gerard.agente.modelousuario.RepositorioModeloUsuario().cadastrarPerfil(
            "Teste composição",10,gerard.agente.modelousuario.Genero.OUTRO,
            gerard.agente.modelousuario.MidiaPreferida.VIDEO,
            gerard.agente.modelousuario.NivelEscolaridade.PRIMEIRO_GRAU,null);
        final Main[] j=new Main[1];
        SwingUtilities.invokeAndWait(()->{j[0]=new Main();j[0].setAlwaysOnTop(true);j[0].setVisible(true);});
        Robot r=new Robot();r.setAutoDelay(20);Thread.sleep(1500);fechar(r);
        Main.TelaGerard t=tela(j[0]);entrar(r,t);
        for(int n=0;n<100;n++){
            clicar(r,t.botaoFerramentaSortearMedidas);fechar(r);
            if(t.situacaoProblemaAtual.getTipo()==TipoSituacaoAditiva.COMPOSICAO_MEDIDAS)break;
        }
        clicar(r,t.botaoAtalhoComposicao);fechar(r);
        verificar("categoria composição",t.tipoSituacaoSelecionada==TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        System.out.println("SITUACAO="+t.situacaoProblemaAtual.getId());
        Point o=t.getLocationOnScreen();int esperado=0;
        for(ElementoTextoMovel tx:new ArrayList<ElementoTextoMovel>(t.elementosTexto)){
            if(!tx.possuiVinculoSemantico())continue;
            SemanticaCuradaSituacao.PapelCurado pc=SemanticaCuradaSituacao.buscar(t.situacaoProblemaAtual,null,tx.chavePapelSemantico);
            if(pc.isDesconhecido())esperado=pc.getValorInteiro();
            for(ElementoVergnaud alvo:t.elementosVergnaud)if(alvo.chavePapelSemantico.equals(tx.chavePapelSemantico)){
                arrastar(r,new Point(o.x+tx.x+tx.largura/2,o.y+tx.y-tx.altura/2),new Point(o.x+alvo.x+alvo.largura/2,o.y+alvo.y+alvo.altura/2));break;
            }
        }
        digitarComFoco(r,t,o,Integer.toString(esperado));Thread.sleep(1500);fechar(r);
        verificar("conclusão real sem erros",atingida(t));
        java.lang.reflect.Field f=Main.TelaGerard.class.getDeclaredField("tentativasIncognitaAtual");f.setAccessible(true);
        gerard.dominio.campoaditivo.PapelQuantitativo p=(gerard.dominio.campoaditivo.PapelQuantitativo)f.get(t);
        verificar("zero rejeições",p==null||p.getTentativasRejeitadasConsecutivas()==0);
        java.lang.reflect.Method m=Main.TelaGerard.class.getDeclaredMethod("deveExibirDiagramaComplementar");m.setAccessible(true);
        verificar("material concreto não liberado",!((Boolean)m.invoke(t)));
        capturar(r,"composicao_concluida_sem_rejeicoes");
        System.out.println("RESUMO falhas="+falhas);j[0].dispose();System.exit(falhas==0?0:1);
    }
}
