package gerard.aplicacao.portabilidade;

import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;

/**
 * Compatibilidade semântica entre o elemento arrastado do enunciado (origem)
 * e o papel da figura-alvo (destino) — mesma avaliação usada pelo desktop em
 * avaliarQuestionamentoPosicionamento/Main.java, via o scaffolding portátil
 * já existente (ScaffoldingQuestionamento, sem Swing). Sem isso, a versão web
 * decidia essa compatibilidade só no cliente (papelId !==
 * figura.chave_papel_semantico em App.tsx) e o servidor aceitava cegamente
 * qualquer papel_id de destino, sem saber o que foi arrastado.
 */
final class AvaliadorOrigemDestinoWeb {
    private static final ScaffoldingQuestionamento SCAFFOLDING = new ScaffoldingQuestionamento();

    private AvaliadorOrigemDestinoWeb() {
    }

    static ResultadoQuestionamento avaliar(
            String origemPapelId, String chavePapelAlvo, TipoSituacaoAditiva categoria) {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        String papelDoElementoNoDiagrama = localizacao.texto(chavePapelAlvo);
        String categoriaEscolhida = localizacao.descricaoTipo(categoria);
        return SCAFFOLDING.avaliarPosicionamento(
                origemPapelId, chavePapelAlvo, papelDoElementoNoDiagrama, categoriaEscolhida);
    }
}
