import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.ConectorVereditoModelador;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelSuporte;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.RegistroAcaoClassificacaoCategoria;
import gerard.dominio.campoaditivo.TentativaClassificacaoCategoriaAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.pesquisador.log.LoggerInteracaoGerard;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

/** Verifica a primeira família da P5 sem Monitor nem ZDP. */
public final class TesteP5_1ClassificacaoCategoria {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        SituacaoProblemaAditiva situacao = situacao(
                "SIT-P5-1", TipoSituacaoAditiva.TRANSFORMACAO_RELACAO);
        TentativaClassificacaoCategoriaAditiva tentativa =
                new TentativaClassificacaoCategoriaAditiva(situacao);

        RegistroAcaoClassificacaoCategoria escolha1 = tentativa.avaliarEscolha(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                contexto("categoria_escolhida=COMPOSICAO_MEDIDAS"));
        exigir(escolha1.getTarefaInteracao() == TarefaInteracao.SELECIONAR
                        && escolha1.foiErrada()
                        && escolha1.getDiagnostico()
                                == RegistroAcaoClassificacaoCategoria.TipoDiagnostico
                                        .CATEGORIA_DIVERGENTE,
                "a tentativa deve avaliar e diagnosticar a escolha divergente");
        exigir(escolha1.getDesfecho()
                        == RegistroAcaoClassificacaoCategoria.Desfecho
                                .QUESTIONAR_CATEGORIA_ESCOLHIDA,
                "a primeira rejeição deve preservar o questionamento existente");
        exigir(escolha1.getRejeicoesConsecutivas() == 1
                        && escolha1.getRejectionSequenceId() != null,
                "a primeira rejeição deve abrir uma sequência própria");

        RegistroAcaoClassificacaoCategoria confirmacaoCorreta =
                tentativa.avaliarConfirmacaoCategoriaDivergente(
                        false, contexto("concordou=false"));
        exigir(confirmacaoCorreta.foiCorreta()
                        && confirmacaoCorreta.getRejectionSequenceId() == null,
                "discordar da categoria errada é ação correta e não é rejeição");
        exigir(tentativa.getRejeicoesConsecutivas() == 1,
                "reconhecer o erro não deve apagar a escolha ainda não resolvida");
        exigir(!escolha1.getActionId().equals(confirmacaoCorreta.getActionId()),
                "a escolha e sua confirmação são ações distintas");

        RegistroAcaoClassificacaoCategoria escolha2 = tentativa.avaliarEscolha(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                contexto("categoria_escolhida=COMPARACAO_MEDIDAS"));
        exigir(escolha2.getRejeicoesConsecutivas() == 2
                        && escolha1.getRejectionSequenceId().equals(
                                escolha2.getRejectionSequenceId()),
                "rejeições consecutivas devem compartilhar somente a sequência");

        RegistroAcaoClassificacaoCategoria confirmacaoErrada =
                tentativa.avaliarConfirmacaoCategoriaDivergente(
                        true, contexto("concordou=true"));
        exigir(confirmacaoErrada.foiErrada()
                        && confirmacaoErrada.atingiuLimite()
                        && confirmacaoErrada.getRejeicoesConsecutivas() == 3,
                "concordar com a categoria divergente deve constituir a terceira rejeição");
        exigir(escolha1.getRejectionSequenceId().equals(
                        confirmacaoErrada.getRejectionSequenceId())
                        && tentativa.estaEncerrada(),
                "o terceiro erro deve encerrar a tentativa e solicitar reexplicação");

        File raiz = Files.createTempDirectory("gerard-p5-1-").toFile();
        System.setProperty("user.home", raiz.getAbsolutePath());
        LoggerInteracaoGerard logger = LoggerInteracaoGerard.getInstancia();
        logger.definirUsuario("participante-p5-1");
        exigir(logger.registrarAcaoInstrumentalUsuario(escolha1)
                        && !logger.registrarAcaoInstrumentalUsuario(escolha1),
                "a infraestrutura deve persistir uma vez o mesmo action_id");

        RepositorioModeloUsuario modelos = new RepositorioModeloUsuario(
                new File(raiz, "perfis.tsv"), new File(raiz, "diagnosticos.tsv"));
        AgenteModelador modelador = new AgenteModelador(modelos);
        ModeloUsuario modelo = modelos.obterOuCriar("participante-p5-1");
        ConectorVereditoModelador conector =
                new ConectorVereditoModelador(modelador);
        conector.registrarAcaoInstrumental(
                "participante-p5-1", escolha1, NivelSuporte.NENHUM, null);
        conector.registrarAcaoInstrumental(
                "participante-p5-1", escolha1, NivelSuporte.NENHUM, null);
        exigir(modelo.getDiagnosticos().size() == 1,
                "o Modelador deve receber um único caso para a mesma ação");
        DiagnosticoTarefa caso = modelo.getDiagnosticos().get(0);
        exigir(escolha1.getActionId().equals(caso.getActionId())
                        && "ERRADA".equals(caso.getAvaliacao())
                        && "CATEGORIA_DIVERGENTE".equals(caso.getTipoErro()),
                "o caso deve conservar o resultado factual do proprietário");

        TentativaClassificacaoCategoriaAditiva acertoDireto =
                new TentativaClassificacaoCategoriaAditiva(situacao);
        RegistroAcaoClassificacaoCategoria escolhaCorreta =
                acertoDireto.avaliarEscolha(
                        TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                        contexto("categoria_escolhida=TRANSFORMACAO_RELACAO"));
        exigir(escolhaCorreta.foiCorreta()
                        && escolhaCorreta.getDesfecho()
                                == RegistroAcaoClassificacaoCategoria.Desfecho
                                        .ACEITAR_CATEGORIA
                        && escolhaCorreta.getRejectionSequenceId() == null,
                "a situação deve aceitar sua categoria curada sem abrir rejeição");
        exigir(TentativaClassificacaoCategoriaAditiva.CHAVE_PROPRIETARIO
                        .equals(escolhaCorreta.getProprietarioSemantico())
                        && escolhaCorreta.getParticipantesSemanticos().size() >= 4,
                "a tentativa deve possuir o registro e referenciar os participantes");

        System.out.println("TesteP5_1ClassificacaoCategoria: OK ("
                + verificacoes + " verificações)");
    }

    private static ContextoAcaoInstrumental contexto(String detalhes) {
        return new ContextoAcaoInstrumental(
                "Classificar situação-problema",
                "Selecionar opção",
                "Controle de categoria",
                "Classificar a estrutura da situação",
                TentativaClassificacaoCategoriaAditiva.ALVO_ESCOLHA,
                "TESTE_P5_1",
                detalhes,
                "Escolha registrada",
                Collections.<String>emptyList());
    }

    private static SituacaoProblemaAditiva situacao(
            String id, TipoSituacaoAditiva tipo) {
        return new SituacaoProblemaAditiva(
                id, true, tipo, IdiomaInterface.PORTUGUES,
                "Situação curada para o teste", "", "teste", "",
                "", "", "", "", "", "", "", "", "");
    }

    private static void exigir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) { throw new AssertionError(mensagem); }
    }
}
