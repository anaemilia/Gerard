import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.ConectorVereditoModelador;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelSuporte;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.RegistroAcaoEscolhaSinalPapelQuantitativo;
import gerard.dominio.campoaditivo.TentativaEscolhaSinalPapelQuantitativo;
import gerard.idioma.IdiomaInterface;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.OpcaoSinalNumeroInteiro;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

/** Verifica a avaliação local da P5.2 sem depender de Swing. */
public final class TesteP5_2EscolhaSinalPapelQuantitativo {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        verificarNumeroInteiroComoProprietarioDoSinal();
        verificarFabricaCuradaESequenciaDeRejeicoes();
        verificarRegistroUnicoNoLogENoModelador();
        System.out.println("TesteP5_2EscolhaSinalPapelQuantitativo: OK ("
                + verificacoes + " verificações)");
    }

    private static void verificarNumeroInteiroComoProprietarioDoSinal() {
        NumeroInteiro positivo = new NumeroInteiro(3);
        NumeroInteiro negativo = new NumeroInteiro(-3);
        NumeroInteiro zero = new NumeroInteiro(0);
        exigir(positivo.correspondeAoSinalRepresentado(
                        OpcaoSinalNumeroInteiro.MAIS),
                "o número positivo deve reconhecer a opção MAIS");
        exigir(negativo.correspondeAoSinalRepresentado(
                        OpcaoSinalNumeroInteiro.MENOS),
                "o número negativo deve reconhecer a opção MENOS");
        exigir(zero.sinalParaRepresentacaoBinaria()
                        == OpcaoSinalNumeroInteiro.MAIS,
                "zero deve preservar a opção MAIS da representação binária atual");
        exigir(!zero.correspondeAoSinalRepresentado(
                        OpcaoSinalNumeroInteiro.MENOS),
                "a opção MENOS não deve representar zero no seletor atual");
    }

    private static void verificarFabricaCuradaESequenciaDeRejeicoes() {
        SituacaoProblemaAditiva situacao = situacao(
                "SIT-P5-2", "3", "-5", "-2");
        Map<String, TentativaEscolhaSinalPapelQuantitativo> tentativas =
                SemanticaCuradaSituacao.criarTentativasEscolhaSinal(
                        situacao, null);
        exigir(tentativas.size() == 3
                        && tentativas.containsKey("papel.relacaoInicial")
                        && tentativas.containsKey("papel.transformacao")
                        && tentativas.containsKey("papel.relacaoFinal"),
                "a curadoria deve criar um proprietário para cada papel inteiro com valor");

        TentativaEscolhaSinalPapelQuantitativo tentativa =
                tentativas.get("papel.transformacao");
        RegistroAcaoEscolhaSinalPapelQuantitativo erro1 =
                tentativa.avaliarEscolha(
                        OpcaoSinalNumeroInteiro.MAIS, contexto("sinal=+"));
        exigir(erro1.foiErrada()
                        && erro1.getDiagnostico()
                                == RegistroAcaoEscolhaSinalPapelQuantitativo
                                        .TipoDiagnostico.SINAL_DIVERGENTE_DO_PAPEL,
                "o papel deve diagnosticar o sinal divergente");
        exigir(erro1.getTarefaInteracao() == TarefaInteracao.SELECIONAR
                        && "+".equals(erro1.getValorPropostoFactual())
                        && "-".equals(erro1.getValorEsperadoFactual()),
                "o registro deve conservar o protocolo e os dois sinais");

        RegistroAcaoEscolhaSinalPapelQuantitativo erro2 =
                tentativa.avaliarEscolha(
                        OpcaoSinalNumeroInteiro.MAIS, contexto("sinal=+"));
        exigir(!erro1.getActionId().equals(erro2.getActionId())
                        && erro1.getRejectionSequenceId().equals(
                                erro2.getRejectionSequenceId())
                        && erro2.getRejeicoesConsecutivas() == 2,
                "cada seleção deve ter ação própria e os erros consecutivos uma sequência comum");

        RegistroAcaoEscolhaSinalPapelQuantitativo acerto =
                tentativa.avaliarEscolha(
                        OpcaoSinalNumeroInteiro.MENOS, contexto("sinal=-"));
        exigir(acerto.foiCorreta()
                        && acerto.getRejectionSequenceId() == null
                        && tentativa.getRejeicoesConsecutivas() == 0,
                "o acerto deve encerrar a sequência de rejeições");

        RegistroAcaoEscolhaSinalPapelQuantitativo novoErro =
                tentativa.avaliarEscolha(
                        OpcaoSinalNumeroInteiro.MAIS, contexto("sinal=+"));
        exigir(!erro1.getRejectionSequenceId().equals(
                        novoErro.getRejectionSequenceId()),
                "um erro posterior ao acerto deve abrir outra sequência");

        for (int i = 0; i < 4; i++) {
            tentativa.avaliarEscolha(
                    OpcaoSinalNumeroInteiro.MAIS, contexto("sinal=+"));
        }
        exigir(tentativa.getRejeicoesConsecutivas() == 5,
                "a escolha de sinal não deve introduzir bloqueio após três erros");

        Map<String, TentativaEscolhaSinalPapelQuantitativo> semCriterio =
                SemanticaCuradaSituacao.criarTentativasEscolhaSinal(
                        situacao("SIT-P5-2-SEM-CRITERIO", "3", "?", "-2"), null);
        exigir(!semCriterio.containsKey("papel.transformacao"),
                "papel desconhecido deve permanecer sem critério normativo de sinal");
    }

    private static void verificarRegistroUnicoNoLogENoModelador()
            throws Exception {
        TentativaEscolhaSinalPapelQuantitativo tentativa =
                SemanticaCuradaSituacao.criarTentativasEscolhaSinal(
                        situacao("SIT-P5-2-LOG", "3", "-5", "-2"), null)
                        .get("papel.transformacao");
        RegistroAcaoEscolhaSinalPapelQuantitativo registro =
                tentativa.avaliarEscolha(
                        OpcaoSinalNumeroInteiro.MAIS, contexto("sinal=+"));

        File raiz = Files.createTempDirectory("gerard-p5-2-").toFile();
        System.setProperty("user.home", raiz.getAbsolutePath());
        LoggerInteracaoGerard logger = LoggerInteracaoGerard.getInstancia();
        logger.definirUsuario("participante-p5-2");
        exigir(logger.registrarAcaoInstrumentalUsuario(registro)
                        && !logger.registrarAcaoInstrumentalUsuario(registro),
                "o log deve persistir uma vez o mesmo action_id");

        RepositorioModeloUsuario modelos = new RepositorioModeloUsuario(
                new File(raiz, "perfis.tsv"),
                new File(raiz, "diagnosticos.tsv"));
        AgenteModelador modelador = new AgenteModelador(modelos);
        ModeloUsuario modelo = modelos.obterOuCriar("participante-p5-2");
        ConectorVereditoModelador conector =
                new ConectorVereditoModelador(modelador);
        conector.registrarAcaoInstrumental(
                "participante-p5-2", registro, NivelSuporte.NENHUM, null);
        conector.registrarAcaoInstrumental(
                "participante-p5-2", registro, NivelSuporte.NENHUM, null);
        exigir(modelo.getDiagnosticos().size() == 1,
                "o Modelador deve receber um único caso para a mesma ação");
        DiagnosticoTarefa caso = modelo.getDiagnosticos().get(0);
        exigir(registro.getActionId().equals(caso.getActionId())
                        && "ERRADA".equals(caso.getAvaliacao())
                        && "SINAL_DIVERGENTE_DO_PAPEL".equals(caso.getTipoErro()),
                "o Modelador deve conservar o resultado produzido pelo papel");
    }

    private static ContextoAcaoInstrumental contexto(String detalhes) {
        return new ContextoAcaoInstrumental(
                "Escolher sinal do número relativo",
                "Selecionar uma opção de sinal",
                "Menu de radio buttons",
                "Representar perda ou ganho com sinal",
                "papel.transformacao.sinal",
                "TESTE_P5_2",
                detalhes,
                "Sinal selecionado",
                Collections.<String>emptyList());
    }

    private static SituacaoProblemaAditiva situacao(
            String id, String relacaoInicial, String transformacao,
            String relacaoFinal) {
        return new SituacaoProblemaAditiva(
                id, true, TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                IdiomaInterface.PORTUGUES,
                "Situação curada para o teste", "", "teste", "",
                relacaoInicial, transformacao, relacaoFinal,
                "", "", "", "", "", "");
    }

    private static void exigir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
