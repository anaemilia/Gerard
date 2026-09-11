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

/**
 * Protocolo portátil REVELAR_EIXO/OCULTAR_EIXO (ver
 * LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md, "próxima fronteira
 * recomendada") — cobre o mesmo estado de domínio que o desktop já usa
 * (gerard.interacao.eixo.ControleVisibilidadeEixoPapel), agora publicado e
 * acionável pela API web via ServicoSorteioAtividadeWeb.revelarEixo/
 * ocultarEixo.
 */
@SuppressWarnings("unchecked")
public final class TesteRevelarOcultarEixoWeb {

    public static void main(String[] args) {
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(11), IdiomaInterface.PORTUGUES);

        Map<String, Object> estado = acertarUmaCategoriaDeMedidas(servico);
        Map<String, Object> cena = (Map<String, Object>) estado.get("cena");
        List<Object> figuras = (List<Object>) cena.get("figuras");

        String papelComLupa = null;
        String papelSemLupa = null;
        for (Object objeto : figuras) {
            Map<String, Object> figura = (Map<String, Object>) objeto;
            boolean exibirLupa = Boolean.TRUE.equals(figura.get("exibir_lupa"));
            String chave = String.valueOf(figura.get("chave_papel_semantico"));
            if (exibirLupa && papelComLupa == null) papelComLupa = chave;
            if (!exibirLupa && papelSemLupa == null) papelSemLupa = chave;
            exigir(Boolean.FALSE.equals(figura.get("lupa_habilitada")),
                    "nenhum papel começa com o eixo revelado: " + chave);
        }
        exigir(papelComLupa != null, "categoria de medidas escolhida deveria ter ao menos um papel com lupa");
        exigir(papelSemLupa != null, "categoria de medidas escolhida deveria ter ao menos um papel sem lupa");

        // --- papel sem lupa: revelar rejeita ---
        boolean lancouParaSemLupa = false;
        try {
            servico.revelarEixo(papelSemLupa);
        } catch (IllegalArgumentException esperada) {
            lancouParaSemLupa = true;
        }
        exigir(lancouParaSemLupa, "revelar eixo de papel sem lupa deveria lançar IllegalArgumentException.");

        // --- papel com lupa: revelar aceita, muda o estado publicado ---
        Map<String, Object> revelado = servico.revelarEixo(papelComLupa);
        exigir(Boolean.TRUE.equals(revelado.get("aceita")), "revelar eixo de papel com lupa deveria ser aceito.");
        exigir(lupaHabilitada((Map<String, Object>) revelado.get("estado"), papelComLupa),
                "lupa_habilitada deveria ser true após revelar.");
        exigir(temInteracao((Map<String, Object>) revelado.get("estado"), papelComLupa, "OCULTAR_EIXO"),
                "figura revelada deveria oferecer OCULTAR_EIXO.");
        exigir(!temInteracao((Map<String, Object>) revelado.get("estado"), papelComLupa, "REVELAR_EIXO"),
                "figura revelada não deveria mais oferecer REVELAR_EIXO.");

        // --- revelar de novo: rejeita (já revelado) ---
        Map<String, Object> revelarDeNovo = servico.revelarEixo(papelComLupa);
        exigir(Boolean.FALSE.equals(revelarDeNovo.get("aceita")),
                "revelar um papel já revelado deveria ser rejeitado (aceita=false), não lançar exceção.");

        // --- ocultar: aceita, volta ao estado fechado ---
        Map<String, Object> ocultado = servico.ocultarEixo(papelComLupa);
        exigir(Boolean.TRUE.equals(ocultado.get("aceita")), "ocultar eixo revelado deveria ser aceito.");
        exigir(!lupaHabilitada((Map<String, Object>) ocultado.get("estado"), papelComLupa),
                "lupa_habilitada deveria voltar a false após ocultar.");
        exigir(temInteracao((Map<String, Object>) ocultado.get("estado"), papelComLupa, "REVELAR_EIXO"),
                "figura fechada deveria voltar a oferecer REVELAR_EIXO.");

        // --- ocultar sem estar revelado: rejeita ---
        Map<String, Object> ocultarDeNovo = servico.ocultarEixo(papelComLupa);
        exigir(Boolean.FALSE.equals(ocultarDeNovo.get("aceita")),
                "ocultar um papel já fechado deveria ser rejeitado (aceita=false).");

        // --- novo sorteio zera o estado (mesmo papel, se aparecer de novo,
        // começa fechado) ---
        servico.revelarEixo(papelComLupa);
        Map<String, Object> novoEstado = acertarUmaCategoriaDeMedidas(servico);
        Map<String, Object> novaCena = (Map<String, Object>) novoEstado.get("cena");
        for (Object objeto : (List<Object>) novaCena.get("figuras")) {
            Map<String, Object> figura = (Map<String, Object>) objeto;
            exigir(Boolean.FALSE.equals(figura.get("lupa_habilitada")),
                    "novo sorteio deveria começar com todos os eixos fechados, mesmo reaproveitando chaves de papel.");
        }

        System.out.println("APROVADO: protocolo REVELAR_EIXO/OCULTAR_EIXO aceita/rejeita "
                + "corretamente por papel, publica lupa_habilitada e interacoes_permitidas reais, "
                + "e reinicia a cada sorteio.");
    }

    @SuppressWarnings("unchecked")
    private static boolean lupaHabilitada(Map<String, Object> estado, String papel) {
        for (Object objeto : (List<Object>) ((Map<String, Object>) estado.get("cena")).get("figuras")) {
            Map<String, Object> figura = (Map<String, Object>) objeto;
            if (papel.equals(figura.get("chave_papel_semantico"))) {
                return Boolean.TRUE.equals(figura.get("lupa_habilitada"));
            }
        }
        throw new AssertionError("papel não encontrado na cena: " + papel);
    }

    @SuppressWarnings("unchecked")
    private static boolean temInteracao(Map<String, Object> estado, String papel, String tipo) {
        for (Object objeto : (List<Object>) ((Map<String, Object>) estado.get("cena")).get("figuras")) {
            Map<String, Object> figura = (Map<String, Object>) objeto;
            if (!papel.equals(figura.get("chave_papel_semantico"))) continue;
            for (Object item : (List<Object>) figura.get("interacoes_permitidas")) {
                if (tipo.equals(((Map<String, Object>) item).get("tipo"))) return true;
            }
        }
        return false;
    }

    /**
     * COMPOSICAO_MEDIDAS (parte1/parte2/todo) nunca tem papel com lupa — só
     * medida() é usado lá, nunca transformacao()/relacao(). Redesenha até
     * cair em TRANSFORMACAO_MEDIDAS ou COMPARACAO_MEDIDAS, que sempre têm
     * pelo menos um papel de número relativo/transformação (com lupa).
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> acertarUmaCategoriaDeMedidas(
            ServicoSorteioAtividadeWeb servico) {
        String[] candidatas = {"TRANSFORMACAO_MEDIDAS", "COMPARACAO_MEDIDAS",
                "COMPOSICAO_MEDIDAS"};
        for (int tentativa = 0; tentativa < 30; tentativa++) {
            servico.sortearMedidas();
            for (String candidata : candidatas) {
                Map<String, Object> resultado = servico.escolherCategoria(candidata);
                if (Boolean.TRUE.equals(resultado.get("correta"))) {
                    Map<String, Object> estado = (Map<String, Object>) resultado.get("estado");
                    if (!"COMPOSICAO_MEDIDAS".equals(estado.get("categoria"))) {
                        return estado;
                    }
                    break;
                }
                Map<String, Object> reconheceu = servico.confirmarCategoria(false);
                exigir(Boolean.TRUE.equals(reconheceu.get("correta")),
                        "discordar da categoria divergente é ação correta");
            }
        }
        throw new AssertionError(
                "não caiu em TRANSFORMACAO_MEDIDAS/COMPARACAO_MEDIDAS em 30 sorteios");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
