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
 * Cobre o segundo gate de Transformação de Relação (auditoria de
 * acoplamento Main/web, 2026-09-19) passando pela camada de fato exposta ao
 * HTTP -- {@link ServicoSorteioAtividadeWeb}, não
 * {@code ServicoAtividadeWebTransformacaoRelacao} direto.
 *
 * Essa distinção importa de verdade: um bug real só apareceu aqui.
 * {@link ServicoSorteioAtividadeWeb#possuiAtividadeEscolhaOperacaoAtiva()}
 * checava só o slot antigo (atividadeEscolhaOperacao), nunca
 * atividadeModelagem -- e {@code ServidorPrototipoWeb.escolherOperacao}
 * consulta exatamente esse método antes de despachar a requisição HTTP.
 * O teste direto na classe interna (TesteServicoAtividadeWebTransformacaoRelacao)
 * não tinha como pegar isso, porque chama escolherOperacao() sem passar por
 * este portão. Encontrado só testando o clique real via HTTP no navegador.
 */
public final class TesteServicoSorteioAtividadeWebEscolhaOperacaoTransformacaoRelacao {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(0), IdiomaInterface.PORTUGUES);

        Map<String, Object> estado = localizarBonecas(servico);
        exigir(estado != null, "não sorteou a situação 'bonecas' em tentativas razoáveis");

        exigir(!servico.possuiAtividadeEscolhaOperacaoAtiva(),
                "não deveria ter escolha de operação ativa antes da classificação");

        Map<String, Object> classificacao = servico.escolherCategoria("TRANSFORMACAO_RELACAO");
        exigir(Boolean.TRUE.equals(classificacao.get("correta")),
                "TRANSFORMACAO_RELACAO deveria ser aceita para 'bonecas'");
        estado = (Map<String, Object>) classificacao.get("estado");

        Map<String, Object> modelagem = (Map<String, Object>) estado.get("modelagem");
        String alvo = String.valueOf(modelagem.get("papel_desconhecido_original"));
        int somaEstrutural = 0;
        for (Object item : (List<Object>) modelagem.get("papeis")) {
            Map<String, Object> papel = (Map<String, Object>) item;
            String id = String.valueOf(papel.get("id"));
            if (id.equals(alvo)) continue;
            Map<String, Object> posicionado = servico.posicionarValorConhecido(id, id);
            Map<String, Object> estadoPos = (Map<String, Object>) posicionado.get("estado");
            Map<String, Object> modelagemPos = (Map<String, Object>) estadoPos.get("modelagem");
            if (id.equals(modelagemPos.get("papel_aguardando_sinal"))) {
                Map<String, Object> comSinal = servico.escolherSinalNumeroRelativo(id, "+");
                modelagemPos = (Map<String, Object>)
                        ((Map<String, Object>) comSinal.get("estado")).get("modelagem");
            }
            for (Object outro : (List<Object>) modelagemPos.get("papeis")) {
                Map<String, Object> candidato = (Map<String, Object>) outro;
                if (id.equals(candidato.get("id"))) {
                    somaEstrutural += ((Number) candidato.get("valor")).intValue();
                }
            }
        }

        // O bug real: esta checagem (e a requisição HTTP correspondente)
        // falhava com "a situação atual não possui escolha de operação
        // implementada" antes da correção.
        exigir(servico.possuiAtividadeEscolhaOperacaoAtiva(),
                "deveria ter escolha de operação ativa depois dos dois papéis posicionados "
                        + "('bonecas' tem operacao_relacao curado)");

        Map<String, Object> operacao = servico.escolherOperacao(null, "SUBTRACAO");
        exigir(Boolean.TRUE.equals(operacao.get("aceita")),
                "SUBTRACAO deveria ser aceita para 'bonecas' (curado é subtracao)");

        servico.proporValor(alvo, somaEstrutural);

        System.out.println("APROVADO: escolha de operação de Transformação de Relação "
                + "funciona pela camada ServicoSorteioAtividadeWeb (mesmo portão do HTTP).");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> localizarBonecas(ServicoSorteioAtividadeWeb servico) {
        for (int tentativa = 0; tentativa < 60; tentativa++) {
            Map<String, Object> estado = servico.sortearRelacoes();
            String enunciado = String.valueOf(estado.get("enunciado"));
            if (enunciado.contains("bonecas")) {
                return estado;
            }
        }
        return null;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
