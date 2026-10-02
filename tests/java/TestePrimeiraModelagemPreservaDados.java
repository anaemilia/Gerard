import gerard.aplicacao.ResolvedorValorEsperadoIncognita;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.conclusao.*;
import java.util.Arrays;

public class TestePrimeiraModelagemPreservaDados {
    public static void main(String[] args) {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot s = estado.atualizar(
            TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
            new Integer[]{11,null,23}, new boolean[]{true,false,true},
            0, EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO, 1, false);
        Integer esperado = new ResolvedorValorEsperadoIncognita().resolver(
            null,null,"papel.diferenca","papel.diferenca",s,1);
        exigir(Integer.valueOf(12).equals(esperado), "23 e 11 exigem 12, mesmo com incógnita protegida");
        exigir(Integer.valueOf(11).equals(estado.snapshot().getValor(0))
            && Integer.valueOf(23).equals(estado.snapshot().getValor(2))
            && !estado.snapshot().isConhecido(1), "avaliação não modifica os dados nem preenche a incógnita");
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        exigir(avaliador.avaliar(Arrays.asList("papel.referido","papel.diferenca","papel.referendo"),
            Arrays.asList(item("papel.referido","23",false,true),
                item("papel.diferenca","12",true,true),item("papel.referendo","11",false,false)))
            == FaseConclusaoModelagem.INCOMPLETA, "dados alterados não liberam conclusão original");
        exigir(avaliador.avaliar(Arrays.asList("papel.referido","papel.diferenca","papel.referendo"),
            Arrays.asList(item("papel.referido","13",false,false),
                item("papel.diferenca","2",true,false),item("papel.referendo","11",false,false)))
            == FaseConclusaoModelagem.CONCLUIDA, "dados originais corretos liberam conclusão");
        System.out.println("APROVADO: primeira modelagem preserva dados e conclusão original.");
    }
    static EstadoPosicionamentoModelagem item(String papel,String valor,boolean incognita,boolean modificado){
        return new EstadoPosicionamentoModelagem(papel,papel,valor,true,incognita,incognita,true,modificado);
    }
    static void exigir(boolean ok,String msg){if(!ok)throw new AssertionError(msg);}
}

