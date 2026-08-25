import gerard.adaptacao.ContextoAdaptativoUsuario;
import gerard.adaptacao.DecisaoAjuda;
import gerard.adaptacao.EstadoPublicacaoRegra;
import gerard.adaptacao.RegraAdaptativaPublicada;
import gerard.adaptacao.sessao.SessaoAdaptativaUsuario;
import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.ConectorVereditoModelador;
import gerard.agente.modelador.RepositorioRegrasAdaptativasPublicadas;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelComplexidadeTarefa;
import gerard.agente.modelousuario.NivelSuporte;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.FatosSelecaoAjudaIncognita;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.TipoErroPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import gerard.semantica.numero.NumeroInteiro;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Verifica o primeiro fluxo distribuído completo da ação TEXTO. */
public final class TesteP4_1FluxoTextoIncognita {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        File raiz = Files.createTempDirectory("gerard-p4-1-").toFile();
        System.setProperty("user.home", raiz.getAbsolutePath());
        RepositorioModeloUsuario modelos = new RepositorioModeloUsuario(
                new File(raiz, "perfis.tsv"),
                new File(raiz, "diagnosticos.tsv"));
        RepositorioRegrasAdaptativasPublicadas regrasPublicadas =
                new RepositorioRegrasAdaptativasPublicadas(
                        new File(raiz, "regras.jsonl"));
        AgenteModelador modelador = new AgenteModelador(
                modelos, regrasPublicadas);

        ModeloUsuario modelo = modelos.obterOuCriar("participante-p4-1");
        modelo.getPerfilAluno().setNome("Participante P4.1");
        modelo.setNivelTarefa(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                NivelComplexidadeTarefa.INTERMEDIARIO);
        DiagnosticoTarefa anterior = new DiagnosticoTarefa(
                "COMPOSICAO_MEDIDAS:papel.todo");
        anterior.setRegraDeAcao("POSICIONAR");
        anterior.setSuporte(NivelSuporte.NENHUM);
        modelo.adicionarDiagnostico(anterior);

        RegraAdaptativaPublicada regraInicial = regra(
                "REGRA-P4-1-INICIAL", "AG_EMLQ",
                NivelComplexidadeTarefa.INTERMEDIARIO);
        modelador.publicarRegrasAdaptativas(
                "participante-p4-1", Collections.singletonList(regraInicial));

        SessaoAdaptativaUsuario sessao = new SessaoAdaptativaUsuario(
                modelos, regrasPublicadas);
        String versaoLogin = sessao.iniciarNoLogin(
                "participante-p4-1").getVersaoModelo();

        PapelQuantitativo fluxo = PapelQuantitativo.todo(
                PublicadorEventoDominio.NENHUM);
        IncognitaQuantitativa incognita = new IncognitaQuantitativa(
                "papel.todo", TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, fluxo);
        ContextoAdaptativoUsuario contexto = sessao
                .projetarContextoPara(incognita).get();
        ContextoAcaoInstrumental instrumento = new ContextoAcaoInstrumental(
                "Substituir incógnita por número",
                "Caixa de texto editável",
                "Item arrastável no diagrama",
                "Informar valor numérico para o papel designado como incógnita",
                "papel.todo",
                "EDICAO_ITEM",
                "valor=3",
                "Valor numérico informado para a incógnita",
                Arrays.asList("papel.parte1", "papel.parte2", "papel.todo"));

        RegistroAcaoInstrumental registro = incognita.avaliarAcaoTexto(
                fluxo.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO),
                new NumeroInteiro(3), new NumeroInteiro(7), instrumento);
        exigir(registro.getTarefaInteracao() == TarefaInteracao.TEXTO,
                "o protocolo deve permanecer TEXTO");
        exigir(registro.foiErrada()
                        && registro.getDiagnostico().isPresent()
                        && registro.getDiagnostico().get().getTipo()
                                == TipoErroPapel.VALOR_INCORRETO,
                "a incógnita deve avaliar e diagnosticar a divergência");
        exigir(registro.getResultadoTentativa().isPresent()
                        && registro.getResultadoTentativa().get()
                                .getRejeicoesConsecutivas() == 1,
                "a mesma decisão deve atualizar a sequência de rejeições");
        exigir(registro.getParticipantesSemanticos().size() == 4,
                "vários objetos devem ser referências da mesma ação");

        LoggerInteracaoGerard logger = LoggerInteracaoGerard.getInstancia();
        logger.definirUsuario("participante-p4-1");
        exigir(logger.registrarAcaoInstrumentalUsuario(registro)
                        && !logger.registrarAcaoInstrumentalUsuario(registro),
                "o log deve persistir uma única vez o mesmo action_id");

        ConectorVereditoModelador conector =
                new ConectorVereditoModelador(modelador);
        conector.registrarAcaoInstrumental(
                "participante-p4-1", registro, null, null);
        conector.registrarAcaoInstrumental(
                "participante-p4-1", registro, null, null);
        exigir(modelo.getDiagnosticos().size() == 2,
                "o Modelador deve receber um único caso além do caso histórico");
        DiagnosticoTarefa caso = modelo.getDiagnosticos().get(1);
        exigir(registro.getActionId().equals(caso.getActionId())
                        && "ERRADA".equals(caso.getAvaliacao())
                        && "VALOR_INCORRETO".equals(caso.getTipoErro()),
                "o caso deve conservar a decisão factual do proprietário");

        DecisaoAjuda decisao = incognita.selecionarAjuda(
                new FatosSelecaoAjudaIncognita(
                        registro.getDiagnostico().get(), 1),
                contexto);
        exigir(decisao.deveAplicarAjuda()
                        && "AG_EMLQ".equals(decisao.getAjuda().getCodigo()),
                "a incógnita deve escolher somente no repertório local");
        exigir("REGRA-P4-1-INICIAL".equals(decisao.getRegraId())
                        && versaoLogin.equals(decisao.getVersaoModelo())
                        && "PART".equals(decisao.getRegraAlgoritmoOrigem()),
                "a decisão deve ser rastreável à regra e à fotografia do login");

        modelo.setNivelTarefa(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                NivelComplexidadeTarefa.AVANCADO);
        modelador.publicarRegrasAdaptativas(
                "participante-p4-1",
                Collections.singletonList(regra(
                        "REGRA-P4-1-POSTERIOR", "AG_EME",
                        NivelComplexidadeTarefa.AVANCADO)));
        exigir(sessao.iniciarNoLogin("participante-p4-1")
                        .getVersaoModelo().equals(versaoLogin)
                        && "REGRA-P4-1-INICIAL".equals(
                                contexto.getRegras().get(0).getId()),
                "mudanças posteriores não podem alterar a fotografia ativa");

        sessao.encerrarNoLogout();
        String versaoNovoLogin = sessao.iniciarNoLogin(
                "participante-p4-1").getVersaoModelo();
        exigir(!versaoNovoLogin.equals(versaoLogin),
                "um novo login deve carregar uma nova versão do modelo");
        exigir("REGRA-P4-1-POSTERIOR".equals(sessao
                        .projetarContextoPara(incognita).get()
                        .getRegras().get(0).getId()),
                "a regra publicada depois do login só deve aparecer na sessão seguinte");

        System.out.println("TesteP4_1FluxoTextoIncognita: OK ("
                + verificacoes + " verificações)");
    }

    private static RegraAdaptativaPublicada regra(
            String id,
            String codigoAjuda,
            NivelComplexidadeTarefa nivel) {
        Map<String, String> condicoes = new LinkedHashMap<String, String>();
        condicoes.put(IncognitaQuantitativa.CONDICAO_DIAGNOSTICO_FACTUAL,
                TipoErroPapel.VALOR_INCORRETO.name());
        condicoes.put(IncognitaQuantitativa.CONDICAO_ORDEM_REJEICAO, "1");
        condicoes.put(IncognitaQuantitativa.CONDICAO_CATEGORIA,
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS.name());
        condicoes.put(IncognitaQuantitativa.CONDICAO_PAPEL_ALVO,
                "papel.todo");
        condicoes.put(IncognitaQuantitativa.CONDICAO_NIVEL_TAREFA,
                nivel.name());
        return new RegraAdaptativaPublicada(
                id, "1", "PART",
                Instant.parse("2026-08-25T12:00:00Z"),
                "casos-humanos-curados-p4-1",
                IncognitaQuantitativa.CHAVE_PROPRIETARIO,
                incognitaEscopo(), condicoes, codigoAjuda,
                Double.valueOf(0.60), Double.valueOf(0.85), null,
                EstadoPublicacaoRegra.PUBLICADA);
    }

    private static gerard.adaptacao.EscopoProprietarioSemantico incognitaEscopo() {
        return gerard.adaptacao.EscopoProprietarioSemantico.PAPEL;
    }

    private static void exigir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
