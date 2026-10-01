package gerard.infraestrutura.web.scaffolding;

import gerard.Scaffolding.ajudacontextual.ScaffoldingAjudaContextual;
import gerard.aplicacao.portabilidade.PortaAjudaContextual;
import gerard.i18n.ServicoLocalizacao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Único ponto que instancia {@link ScaffoldingAjudaContextual} para a web,
 * recebendo área/intenção por nome (mesma representação já usada na
 * fronteira HTTP) — assim a camada de aplicação (gerard.aplicacao.
 * portabilidade) não precisa importar os enums deste pacote concreto (ver
 * ServicoSorteioAtividadeWeb). Mesmo texto/estrutura que
 * AjudaContextualWeb.projetarArea/nomeArea/mensagem/rotuloOpcao produziam
 * antes de saírem da camada de aplicação. Vive na infraestrutura web, não em
 * gerard.aplicacao nem em gerard.Scaffolding — ver o comentário equivalente
 * em AdaptadorQuestionamentoPosicionamentoWeb.
 */
public final class AdaptadorAjudaContextualWeb implements PortaAjudaContextual {
    private final ScaffoldingAjudaContextual scaffolding = new ScaffoldingAjudaContextual();

    public Map<String, Object> projetarArea(String areaTexto) {
        ScaffoldingAjudaContextual.Area area = ScaffoldingAjudaContextual.Area.valueOf(areaTexto);
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("area", area.name());
        item.put("cabecalho", localizacao.formatar("ui.help.header", nomeArea(areaTexto)));
        List<Object> opcoes = new ArrayList<Object>();
        for (ScaffoldingAjudaContextual.Intencao intencao
                : ScaffoldingAjudaContextual.Intencao.values()) {
            Map<String, Object> opcao = new LinkedHashMap<String, Object>();
            opcao.put("intencao", intencao.name());
            opcao.put("rotulo", localizacao.texto(scaffolding.obterChaveOpcao(intencao)));
            opcoes.add(opcao);
        }
        item.put("opcoes", opcoes);
        return item;
    }

    public String nomeArea(String areaTexto) {
        ScaffoldingAjudaContextual.Area area = ScaffoldingAjudaContextual.Area.valueOf(areaTexto);
        if (area == ScaffoldingAjudaContextual.Area.COMPLEMENTAR) {
            return ServicoLocalizacao.getInstancia().texto("ui.collections.title");
        }
        return ServicoLocalizacao.getInstancia().texto(scaffolding.obterChaveArea(area));
    }

    public String rotuloOpcao(String intencaoTexto) {
        ScaffoldingAjudaContextual.Intencao intencao =
                ScaffoldingAjudaContextual.Intencao.valueOf(intencaoTexto);
        return ServicoLocalizacao.getInstancia().texto(scaffolding.obterChaveOpcao(intencao));
    }

    public String mensagem(String areaTexto, String intencaoTexto) {
        ScaffoldingAjudaContextual.Area area = ScaffoldingAjudaContextual.Area.valueOf(areaTexto);
        ScaffoldingAjudaContextual.Intencao intencao =
                ScaffoldingAjudaContextual.Intencao.valueOf(intencaoTexto);
        return ServicoLocalizacao.getInstancia().texto(scaffolding.obterChaveMensagem(area, intencao));
    }
}
