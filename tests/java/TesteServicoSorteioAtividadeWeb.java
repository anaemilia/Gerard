import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividadeWeb;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TesteServicoSorteioAtividadeWeb {
    public static void main(String[] args) {
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(11), IdiomaInterface.PORTUGUES);
        Map<String, Object> medidas = servico.sortearMedidas();
        verificar(medidas, "MEDIDAS");
        exigir(!medidas.containsKey("categoria") && !medidas.containsKey("cena"),
                "resposta não revela a classificação antes da escolha");
        Map<String, Object> acerto = acertarUmaCategoriaDeMedidas(servico);
        exigir(Boolean.TRUE.equals(acerto.get("correta")), "categoria curada aceita");
        Map<String, Object> estadoAceito = (Map<String, Object>) acerto.get("estado");
        exigir(estadoAceito.get("categoria") != null && estadoAceito.get("cena") instanceof Map,
                "categoria e cena são publicadas somente depois do acerto");
        Map<String, Object> cena = (Map<String, Object>) estadoAceito.get("cena");
        List<Object> figuras = (List<Object>) cena.get("figuras");
        String papelEditavel = papelAlvoDaAcao(
                (List<Object>) estadoAceito.get("acoes_disponiveis"));
        exigir(figuras != null && !figuras.isEmpty(), "cena contém figuras");
        for (Object objeto : figuras) {
            Map<String, Object> figura = (Map<String, Object>) objeto;
            String papel = String.valueOf(figura.get("chave_papel_semantico"));
            exigir(papel.startsWith("papel."),
                    "cada figura da API transporta identidade semântica explícita");
            exigir(figura.containsKey("subtitulo"),
                    "a API projeta o participante curado da figura, ainda que vazio");
            List<Object> interacoes = (List<Object>) figura.get("interacoes_permitidas");
            exigir(interacoes != null,
                    "cada figura declara capacidades, ainda que vazias");
            exigir(interacoes.isEmpty() == !papel.equals(papelEditavel),
                    "somente o papel anunciado pela ação pode ser editado");
        }
        exigir(!estadoAceito.containsKey("curadoria"),
                "API não expõe a resposta armazenada na curadoria");
        verificar(servico.sortearRelacoes(), "RELACOES");
        System.out.println("APROVADO: sorteios web retornam atividade curada completa.");
    }

    @SuppressWarnings("unchecked")
    private static String papelAlvoDaAcao(List<Object> acoes) {
        if (acoes == null) return null;
        for (Object item : acoes) {
            Map<String, Object> acao = (Map<String, Object>) item;
            if ("PROPOR_VALOR_PAPEL".equals(acao.get("id"))) {
                return String.valueOf(((Map<String, Object>) acao.get("corpo"))
                        .get("papel_id"));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> acertarUmaCategoriaDeMedidas(
            ServicoSorteioAtividadeWeb servico) {
        String[] candidatas = {"COMPOSICAO_MEDIDAS", "TRANSFORMACAO_MEDIDAS",
                "COMPARACAO_MEDIDAS"};
        for (String candidata : candidatas) {
            Map<String, Object> resultado = servico.escolherCategoria(candidata);
            if (Boolean.TRUE.equals(resultado.get("correta"))) return resultado;
            exigir("AGUARDANDO_CONFIRMACAO_CATEGORIA".equals(
                    ((Map<String, Object>) resultado.get("estado")).get("modo")),
                    "escolha divergente aguarda confirmação");
            Map<String, Object> reconheceu = servico.confirmarCategoria(false);
            exigir(Boolean.TRUE.equals(reconheceu.get("correta")),
                    "discordar da categoria divergente é ação correta");
        }
        throw new AssertionError("nenhuma categoria de medidas foi aceita");
    }

    @SuppressWarnings("unchecked")
    private static void verificar(Map<String, Object> estado, String grupo) {
        exigir(ServicoSorteioAtividadeWeb.SCHEMA.equals(estado.get("schema")), "schema");
        exigir(grupo.equals(estado.get("grupo_sorteio")), "grupo");
        exigir(Boolean.TRUE.equals(estado.get("situacao_validada")), "validação curada");
        exigir(estado.get("situacao_id") != null, "identidade da situação");
        exigir(estado.get("enunciado") != null, "enunciado");
        exigir(estado.get("categoria_selecionada") == null,
                "nenhuma categoria começa marcada");
        List<Object> acoes = (List<Object>) estado.get("acoes_disponiveis");
        exigir(acoes != null && acoes.size() == 8,
                "dois sorteios e seis escolhas publicados como controles");
        exigir(possuiAcao(acoes, "SORTEAR_MEDIDAS", "/api/sorteios/medidas"),
                "controle de sorteio de medidas");
        exigir(possuiAcao(acoes, "SORTEAR_RELACOES", "/api/sorteios/relacoes"),
                "controle de sorteio de relações");
        exigir(contarAcoes(acoes, "ESCOLHER_CATEGORIA") == 6,
                "seis categorias começam disponíveis e desmarcadas");
    }

    @SuppressWarnings("unchecked")
    private static int contarAcoes(List<Object> acoes, String id) {
        int total = 0;
        for (Object item : acoes) {
            if (id.equals(((Map<String, Object>) item).get("id"))) total++;
        }
        return total;
    }

    @SuppressWarnings("unchecked")
    private static boolean possuiAcao(List<Object> acoes, String id, String href) {
        for (Object item : acoes) {
            Map<String, Object> acao = (Map<String, Object>) item;
            if (id.equals(acao.get("id")) && href.equals(acao.get("href"))
                    && "POST".equals(acao.get("metodo"))) return true;
        }
        return false;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
