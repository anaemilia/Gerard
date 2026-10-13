package gerard.aplicacao.portabilidade;

import java.util.List;
import java.util.Map;

/**
 * O que acontece, depois de julgado correto, com um número solto sobre uma figura. Cada constante sabe aplicar o
 * seu efeito na atividade — quem solta não ramifica por tipo de figura. Os nomes são os ids das ações publicadas em
 * {@code acoes_disponiveis} (AcoesDisponiveisAtividadeWeb), que é o que a atividade oferece naquele momento.
 */
public enum EfeitoSoltura {
    POSICIONAR_CONHECIDO {
        @Override
        Map<String, Object> aplicar(ServicoAtividadeWebModelagemPapeis atividade, String papelId, String origemPapelId) {
            return atividade.posicionarValorConhecido(papelId, origemPapelId);
        }
    },
    ENGATAR_INCOGNITA {
        @Override
        Map<String, Object> aplicar(ServicoAtividadeWebModelagemPapeis atividade, String papelId, String origemPapelId) {
            return atividade.engatarIncognita(papelId, origemPapelId);
        }
    },
    /** Posicionamento correto sem efeito oferecido agora: nada muda. */
    NENHUM {
        @Override
        Map<String, Object> aplicar(ServicoAtividadeWebModelagemPapeis atividade, String papelId, String origemPapelId) {
            return atividade.resultadoAceito();
        }
    };

    abstract Map<String, Object> aplicar(ServicoAtividadeWebModelagemPapeis atividade, String papelId,
            String origemPapelId);

    /** O efeito que a lista de ações disponíveis oferece para o papel; {@link #NENHUM} se nenhum. */
    @SuppressWarnings("unchecked")
    static EfeitoSoltura oferecidoPara(List<Object> acoesDisponiveis, String papelId) {
        EfeitoSoltura oferecido = NENHUM;
        for (Object item : acoesDisponiveis) {
            Map<String, Object> acao = (Map<String, Object>) item;
            Object corpo = acao.get("corpo");
            if (!(corpo instanceof Map) || !papelId.equals(((Map<String, Object>) corpo).get("papel_id"))) {
                continue;
            }
            for (EfeitoSoltura efeito : values()) {
                if (efeito != NENHUM && efeito.name().equals(acao.get("id"))) {
                    oferecido = efeito;
                }
            }
        }
        return oferecido;
    }
}
