import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.portabilidade.PortaRegistroAtividade;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividade;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;

import java.util.Map;
import java.util.Random;

/** Protege a fronteira de registro factual extraída do serviço web. */
public final class TestePortaRegistroAtividadeWeb {

    public static void main(String[] args) {
        PortaFalsa porta = new PortaFalsa();
        ServicoSorteioAtividade servico = novoServico(porta);

        servico.sortearMedidas();
        aceitarCategoriaCurada(servico);
        exigir(porta.situacoes == 1 && porta.situacao != null,
                "categoria aceita deve publicar uma única situação");
        exigir(porta.categoriaFallback != null
                        && porta.categoriaFallback.length() > 0
                        && porta.enunciado != null
                        && porta.enunciado.length() > 0,
                "porta deve receber o contexto factual já constituído");

        Map<String, Object> ajuda = servico.ajudaContextual("TEXTO", "DUVIDA");
        exigir("gerard.atividade-web.resultado-ajuda-contextual.v1".equals(
                        ajuda.get("schema")),
                "registro não pode alterar a resposta da API");
        exigir(porta.acoes == 1,
                "ajuda apresentada deve produzir um único registro granular");
        exigir("SELECIONAR".equals(porta.tipoAcao)
                        && "Solicitar ajuda contextual".equals(porta.tarefa)
                        && "MENU_E_AGORA".equals(porta.instrumentoArtefato)
                        && "OBJ_INTERACAO".equals(porta.objeto)
                        && "ACAO_GRANULAR_SELECIONAR".equals(porta.origemEvento)
                        && "area=TEXTO; intencao=DUVIDA".equals(porta.detalhes),
                "adaptador deve transportar os nove campos sem reinterpretá-los");

        ServicoSorteioAtividade semRegistro = novoServico(null);
        semRegistro.ajudaContextual("VERGNAUD", "CONTINUAR");

        System.out.println("APROVADO: porta web preserva situação e ação factual sem possuir suas regras.");
    }

    private static ServicoSorteioAtividade novoServico(
            PortaRegistroAtividade porta) {
        return new ServicoSorteioAtividade(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(
                        new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(),
                        new ConstrutorResultadoCurado()),
                new Random(11), IdiomaInterface.PORTUGUES, porta);
    }

    @SuppressWarnings("unchecked")
    private static void aceitarCategoriaCurada(
            ServicoSorteioAtividade servico) {
        String[] candidatas = {"COMPOSICAO_MEDIDAS", "TRANSFORMACAO_MEDIDAS",
                "COMPARACAO_MEDIDAS"};
        for (String candidata : candidatas) {
            Map<String, Object> resultado = servico.escolherCategoria(candidata);
            if (Boolean.TRUE.equals(resultado.get("correta"))) {
                return;
            }
            Map<String, Object> confirmacao = servico.confirmarCategoria(false);
            exigir(Boolean.TRUE.equals(confirmacao.get("correta")),
                    "discordância factual deve manter o fluxo de classificação");
        }
        throw new AssertionError("nenhuma categoria curada foi aceita");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static final class PortaFalsa implements PortaRegistroAtividade {
        private int situacoes;
        private SituacaoProblemaAditiva situacao;
        private String categoriaFallback;
        private String enunciado;
        private int acoes;
        private String tipoAcao;
        private String tarefa;
        private String instrumentoArtefato;
        private String objeto;
        private String origemEvento;
        private String detalhes;

        @Override
        public void registrarNovaSituacao(SituacaoProblemaAditiva situacao,
                String categoriaFallback, String enunciado) {
            situacoes++;
            this.situacao = situacao;
            this.categoriaFallback = categoriaFallback;
            this.enunciado = enunciado;
        }

        @Override
        public void registrarAcaoGranularUsuario(String tipoAcao, String tarefa,
                String instrumentoOrganizacao, String instrumentoArtefato,
                String funcaoArtefato, String objeto, String origemEvento,
                String detalhes, String mudancaObservavel) {
            acoes++;
            this.tipoAcao = tipoAcao;
            this.tarefa = tarefa;
            this.instrumentoArtefato = instrumentoArtefato;
            this.objeto = objeto;
            this.origemEvento = origemEvento;
            this.detalhes = detalhes;
        }

        @Override
        public void persistir(RegistroFactualAcaoInstrumental registro) {
        }

        @Override
        public void registrarRestauracaoModelagem(RegistroAcaoRestauracaoModelagem registro) {
        }
    }
}
