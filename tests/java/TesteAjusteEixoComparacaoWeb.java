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

/** Regressão: reta -> Valor relativo -> relação -> cena e texto. */
@SuppressWarnings("unchecked")
public final class TesteAjusteEixoComparacaoWeb {
    public static void main(String[] args) {
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(29), IdiomaInterface.PORTUGUES);
        Map<String, Object> estado = acertarComparacaoMedidas(servico);
        Map<String, Object> modelagem = modelagem(estado);
        String desconhecido = String.valueOf(modelagem.get("papel_desconhecido_original"));

        for (String papel : new String[] {
                "papel.referido", "papel.diferenca", "papel.referendo"}) {
            if (papel.equals(desconhecido)) continue;
            estado = estado(servico.posicionarValorConhecido(papel, papel));
            if (papel.equals(modelagem(estado).get("papel_aguardando_sinal"))) {
                String sinal = "papel.referido".equals(desconhecido) ? "-" : "+";
                estado = estado(servico.escolherSinalNumeroRelativo(papel, sinal));
            }
        }

        estado = estado(servico.engatarIncognita(desconhecido, desconhecido));
        Integer referidoAtual = valorOuNull(estado, "papel.referido");
        Integer relativoAtual = valorOuNull(estado, "papel.diferenca");
        Integer referendoAtual = valorOuNull(estado, "papel.referendo");
        int valorDesconhecido = "papel.referido".equals(desconhecido)
                ? referendoAtual.intValue() - relativoAtual.intValue()
                : "papel.diferenca".equals(desconhecido)
                        ? referendoAtual.intValue() - referidoAtual.intValue()
                        : referidoAtual.intValue() + relativoAtual.intValue();
        Map<String, Object> confirmado = servico.proporValor(desconhecido, valorDesconhecido);
        exigir(Boolean.TRUE.equals(confirmado.get("aceita")),
                "pré-condição: comparação deveria ser confirmada");
        estado = estado(confirmado);

        Map<String, Object> cenaConcluida = (Map<String, Object>) estado.get("cena");
        Map<String, Object> relacaoExploratoria =
                (Map<String, Object>) cenaConcluida.get("relacao_exploratoria");
        Map<String, Object> projecoesPorPapel =
                (Map<String, Object>) relacaoExploratoria.get("projecoes");
        Map<String, Object> projecoesValorRelativo =
                (Map<String, Object>) projecoesPorPapel.get("papel.diferenca");
        exigir(projecoesValorRelativo != null && !projecoesValorRelativo.isEmpty(),
                "snapshot concluído deve publicar projeções para broadcast local da reta");

        int referido = valor(estado, "papel.referido");
        int relativoConfirmado = valor(estado, "papel.diferenca");
        int referendoConfirmado = valor(estado, "papel.referendo");
        int relativoExplorado = relativoConfirmado;
        for (String valorPublicado : projecoesValorRelativo.keySet()) {
            int candidato = Integer.parseInt(valorPublicado);
            if (candidato != relativoConfirmado) {
                relativoExplorado = candidato;
                break;
            }
        }
        exigir(relativoExplorado != relativoConfirmado,
                "reta deve publicar ao menos uma mudança exploratória válida");
        Map<String, Object> projecaoPublicada = (Map<String, Object>)
                projecoesValorRelativo.get(String.valueOf(relativoExplorado));
        exigir(projecaoPublicada != null,
                "posição arrastável deve possuir projeção calculada pelo domínio");
        exigir(Integer.valueOf(relativoExplorado).equals(
                        projecaoPublicada.get("papel.diferenca")),
                "broadcast publicado deve preservar o papel de origem");
        exigir(Integer.valueOf(referido + relativoExplorado).equals(
                        projecaoPublicada.get("papel.referendo")),
                "projeção publicada deve conter a reação relacional sem fórmula no cliente");
        servico.revelarEixo("papel.diferenca");
        // Decisão da usuária (2026-09-29): mexer no eixo é exploratório e não
        // persiste. A relação do domínio projeta o dependente; nada é gravado.
        Map<String, Object> projecao = servico.projetarAlteracaoEixo(
                "papel.diferenca", relativoExplorado);
        Map<String, Object> valores = (Map<String, Object>) projecao.get("valores");
        exigir(Boolean.TRUE.equals(projecao.get("aceita")), "projeção deveria ser aceita");
        exigir(Integer.valueOf(relativoExplorado).equals(valores.get("papel.diferenca")),
                "projeção deveria conter o Valor relativo explorado");
        exigir(Integer.valueOf(referido + relativoExplorado).equals(valores.get("papel.referendo")),
                "relação do domínio deveria projetar o Referendo");
        exigir(!valores.containsKey("papel.referido"),
                "Referido independente não deveria ser alterado");
        estado = estado(servico.ocultarEixo("papel.diferenca"));
        exigir(valor(estado, "papel.diferenca") == relativoConfirmado
                        && valor(estado, "papel.referendo") == referendoConfirmado,
                "exploração pelo eixo não pode gravar na tentativa");
        Map<String, Object> negativa = servico.projetarAlteracaoEixo(
                "papel.diferenca", -(referido + 1));
        exigir(Boolean.FALSE.equals(negativa.get("aceita")),
                "o domínio deveria recusar projeção que deixa o Referendo negativo");
        System.out.println("APROVADO: reta projeta Valor relativo e Referendo pelo domínio, sem persistir.");
    }

    private static Map<String, Object> acertarComparacaoMedidas(
            ServicoSorteioAtividadeWeb servico) {
        for (int tentativa = 0; tentativa < 40; tentativa++) {
            servico.sortearMedidas();
            Map<String, Object> resultado = servico.escolherCategoria("COMPARACAO_MEDIDAS");
            if (Boolean.TRUE.equals(resultado.get("correta"))) return estado(resultado);
            servico.confirmarCategoria(false);
        }
        throw new AssertionError("não caiu em Comparação de Medidas");
    }

    private static Map<String, Object> estado(Map<String, Object> resultado) {
        return (Map<String, Object>) resultado.get("estado");
    }

    private static Map<String, Object> modelagem(Map<String, Object> estado) {
        return (Map<String, Object>) estado.get("modelagem");
    }

    private static Integer valorOuNull(Map<String, Object> estado, String papelId) {
        for (Object objeto : (List<Object>) modelagem(estado).get("papeis")) {
            Map<String, Object> papel = (Map<String, Object>) objeto;
            if (papelId.equals(papel.get("id"))) return (Integer) papel.get("valor");
        }
        throw new AssertionError("papel ausente: " + papelId);
    }

    private static int valor(Map<String, Object> estado, String papelId) {
        Integer encontrado = valorOuNull(estado, papelId);
        if (encontrado == null) throw new AssertionError("papel sem valor: " + papelId);
        return encontrado.intValue();
    }

    private static Map<String, Object> figura(Map<String, Object> estado, String papelId) {
        Map<String, Object> cena = (Map<String, Object>) estado.get("cena");
        for (Object objeto : (List<Object>) cena.get("figuras")) {
            Map<String, Object> figura = (Map<String, Object>) objeto;
            if (papelId.equals(figura.get("chave_papel_semantico"))) return figura;
        }
        throw new AssertionError("figura ausente: " + papelId);
    }

    private static String texto(Map<String, Object> estado, String papelId) {
        Map<String, Object> cena = (Map<String, Object>) estado.get("cena");
        for (Object objeto : (List<Object>) cena.get("elementos_texto")) {
            Map<String, Object> elemento = (Map<String, Object>) objeto;
            if (papelId.equals(elemento.get("papel_id"))) return String.valueOf(elemento.get("valor"));
        }
        throw new AssertionError("elemento textual ausente: " + papelId);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
