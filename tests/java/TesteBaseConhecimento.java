import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.RegistroAcaoPosicionamentoPapelQuantitativo;
import gerard.semantica.papel.CatalogoPapeisSemanticos;
import gerard.semantica.papel.DescritorPapelQuantitativo;
import java.util.Collections;

/** Harness das regras locais de compatibilidade entre papéis quantitativos. */
public class TesteBaseConhecimento {

    public static void main(String[] args) {
        CatalogoPapeisSemanticos catalogo = new CatalogoPapeisSemanticos();
        DescritorPapelQuantitativo transformacao =
                catalogo.obter("papel.transformacao");
        DescritorPapelQuantitativo transformacao1 =
                catalogo.obter("papel.transformacao1");
        DescritorPapelQuantitativo relacao =
                catalogo.obter("papel.relacao");
        DescritorPapelQuantitativo relacaoFinal =
                catalogo.obter("papel.relacaoFinal");

        checar(transformacao.podeOcupar(transformacao1),
                "papel genérico de transformação aceita membro da família");
        checar(relacao.podeOcupar(relacaoFinal),
                "papel genérico de relação aceita membro da família");
        checar(!transformacao1.podeOcupar(relacaoFinal),
                "papéis de famílias distintas são incompatíveis");

        ContextoAcaoInstrumental contexto = new ContextoAcaoInstrumental(
                "Posicionar", "Arrastar e soltar", "Texto e diagrama",
                "Associar papéis", "papel.transformacao1", "SOLTURA_USUARIO",
                "", "Elemento posicionado", Collections.<String>emptyList());
        RegistroAcaoPosicionamentoPapelQuantitativo registro =
                transformacao.avaliarPosicionamento(
                        transformacao1,
                        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                        contexto);
        checar(registro.getResultado()
                        == ResultadoAvaliacaoAcaoInstrumental.CORRETA,
                "o proprietário produz o veredito factual");
        checar(registro.getTarefaInteracao() == TarefaInteracao.POSICIONAR,
                "o registro preserva o protocolo POSICIONAR");
        checar(registro.getActionId() != null
                        && registro.getActionId().length() > 0,
                "cada ação possui identidade própria");

        System.out.println("TODOS OS TESTES PASSARAM.");
    }

    private static void checar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
