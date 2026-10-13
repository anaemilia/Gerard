package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.conclusao.EscolhaConclusaoModelagem;
import gerard.i18n.ServicoLocalizacao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Projeta, para o cliente web, o fluxo de decisão depois que a modelagem ficou azul: os textos reais do desktop
 * (mensagens_pt.properties: ui.completion.*), a pausa entre o azul e o selo, e as ações HTTP que o selo/tip publicam.
 * Não decide nada: quem decide o que cada escolha faz é {@link EscolhaConclusaoModelagem}; o cliente só realiza.
 */
public final class ProjetorConclusao {
    public static final String ROTA_ESCOLHA = "/api/acoes/conclusao";

    private ProjetorConclusao() {
    }

    public static Map<String, Object> projetar(ServicoLocalizacao localizacao) {
        Map<String, Object> textos = new LinkedHashMap<String, Object>();
        textos.put("selo", localizacao.texto("ui.completion.completed"));
        textos.put("pergunta", localizacao.texto("ui.completion.congratulations"));
        textos.put("sim", localizacao.texto("ui.completion.yes"));
        textos.put("nao", localizacao.texto("ui.completion.no"));
        textos.put("proxima_tarefa", localizacao.texto("ui.completion.next"));
        List<Object> acoes = new ArrayList<Object>();
        for (EscolhaConclusaoModelagem escolha : EscolhaConclusaoModelagem.values()) {
            Map<String, Object> corpo = new LinkedHashMap<String, Object>();
            corpo.put("escolha", escolha.name());
            Map<String, Object> acao = new LinkedHashMap<String, Object>();
            acao.put("tipo", "CONCLUSAO_" + escolha.name());
            acao.put("metodo", "POST");
            acao.put("href", ROTA_ESCOLHA);
            acao.put("corpo", corpo);
            acao.put("inicia_nova_situacao", Boolean.valueOf(escolha.iniciaNovaSituacao()));
            acoes.add(acao);
        }
        Map<String, Object> conclusao = new LinkedHashMap<String, Object>();
        conclusao.put("atraso_decisao_ms", Integer.valueOf(EscolhaConclusaoModelagem.ATRASO_DECISAO_MS));
        conclusao.put("textos", textos);
        conclusao.put("acoes", acoes);
        return conclusao;
    }
}
