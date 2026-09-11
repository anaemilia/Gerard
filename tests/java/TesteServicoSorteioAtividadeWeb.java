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
        List<Object> acoesFigura = (List<Object>) estadoAceito.get("acoes_disponiveis");
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
            exigir(Boolean.FALSE.equals(figura.get("conhecido")) && figura.get("valor") == null,
                    "classificação não posiciona valores automaticamente");
            boolean possuiAcaoParaPapel = acoesFigura.stream().map(a -> (Map<String, Object>) a)
                    .anyMatch(a -> a.get("corpo") instanceof Map
                            && papel.equals(((Map<String, Object>) a.get("corpo")).get("papel_id")));
            // REVELAR_EIXO/OCULTAR_EIXO não vem de acoes_disponiveis (ver
            // comentário mais abaixo) — ignorado aqui para isolar a
            // comparação original (interações originadas das ações globais
            // da atividade) do protocolo independente do eixo.
            long interacoesForaDoEixo = interacoes.stream().map(i -> (Map<String, Object>) i)
                    .filter(i -> !"REVELAR_EIXO".equals(i.get("tipo")) && !"OCULTAR_EIXO".equals(i.get("tipo")))
                    .count();
            exigir((interacoesForaDoEixo == 0) == !possuiAcaoParaPapel,
                    "a figura oferece interação somente quando há ação para seu papel");
            for (Object item : interacoes) {
                Map<String, Object> interacao = (Map<String, Object>) item;
                exigir(!"EDITAR_VALOR".equals(interacao.get("tipo")),
                        "a digitação não é oferecida antes do engate");
                // REVELAR_EIXO/OCULTAR_EIXO (protocolo do eixo dos inteiros,
                // ver ServicoSorteioAtividadeWeb.revelarEixo/ocultarEixo) é
                // decidido localmente por figura (isExibirLupa + estado do
                // mapa de visibilidade), não publicado em acoes_disponiveis
                // por nenhuma atividade — não tem o que casar ali.
                if ("REVELAR_EIXO".equals(interacao.get("tipo"))
                        || "OCULTAR_EIXO".equals(interacao.get("tipo"))) {
                    continue;
                }
                exigir(acoesFigura.stream().map(a -> (Map<String, Object>) a).anyMatch(a ->
                        interacao.get("acao_id").equals(a.get("id"))
                        && a.get("corpo") instanceof Map
                        && papel.equals(((Map<String, Object>) a.get("corpo")).get("papel_id"))),
                        "cada interação referencia ação disponível para o mesmo papel");
            }
        }
        exigir(!estadoAceito.containsKey("curadoria"),
                "API não expõe a resposta armazenada na curadoria");
        verificar(servico.sortearRelacoes(), "RELACOES");
        System.out.println("APROVADO: sorteios web retornam atividade curada completa.");
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
