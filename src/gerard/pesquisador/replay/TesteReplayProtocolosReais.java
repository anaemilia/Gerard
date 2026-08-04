package gerard.pesquisador.replay;

import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.ConectorVereditoModelador;
import gerard.agente.modelador.InferenciaRegrasModelador;
import gerard.agente.modelador.OuvinteCasoAgenteModelador;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.agente.monitor.AgenteMonitor;
import gerard.agente.monitor.OuvinteVeredictoAgenteMonitor;
import gerard.agente.zdp.AgenteZDP;
import gerard.agente.zdp.CamadaEstrategiaZDP;
import gerard.agente.zdp.OuvinteEstrategiaAgenteZDP;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Ambiente de simulação REAL para os três agentes da Ajuda Adaptativa
 * (Monitor/ZDP/Modelador) — "real" porque a maior parte dos dados que os
 * agentes recebem não são sintéticos nem sorteados: vêm de 16 sessões de
 * pesquisa reais (mestrado + doutorado, ver
 * dados/protocolos_reais_replay.tsv e RepositorioProtocolosReaisReplay),
 * pessoas de verdade resolvendo situações-problema do Gérard. Uma segunda
 * fonte, claramente separada e rotulada (ver CatalogoEpisodiosSinteticos,
 * ids "sint_*"), amplifica artificialmente os MESMOS tipos de erro reais
 * pra dar volume suficiente à ação 2 do Modelador (PART+Apriori) — as 16
 * sessões reais sozinhas não têm repetição suficiente por (categoria,
 * papel-alvo) pra essa ação induzir regra condicional nenhuma.
 *
 * Ao contrário de TesteMonkeySemiGuiado (que dirige a TelaGerard via
 * java.awt.Robot com ações aleatórias — útil para stress-test de UI, mas
 * sem nenhum padrão de erro real), esta classe chama diretamente a mesma
 * cadeia de produção que Main.avaliarQuestionamentoPosicionamento aciona a
 * cada arraste do usuário: AgenteMonitor.avaliarPosicionamento →
 * AgenteZDP.decidirEstrategia → ConectorVereditoModelador.registrarVeredito.
 * Sem Swing, sem Robot, sem tela — só os três agentes de produção reais,
 * recebendo os passos na ordem em que aconteceram (reais) ou foram
 * construídos (sintéticos).
 *
 * Os dados nunca tocam ~/Gerard/perfis_usuario.tsv nem
 * ~/Gerard/diagnosticos_tarefa.tsv (os arquivos reais do app): usa o
 * construtor de RepositorioModeloUsuario que aponta para arquivos isolados
 * em ~/Gerard/replay_mestrado, para não misturar os perfis históricos
 * ("hist_aldenira" etc.) nem os sintéticos ("sint_*") com o uso real do app
 * nesta máquina.
 */
public final class TesteReplayProtocolosReais {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        File diretorio = new File(new File(System.getProperty("user.home"), "Gerard"), "replay_mestrado");
        diretorio.mkdirs();
        File arquivoPerfis = new File(diretorio, "perfis_historicos.tsv");
        File arquivoDiagnosticos = new File(diretorio, "diagnosticos_historicos.tsv");
        File arquivoLogAgentes = new File(diretorio,
                "atividade_agentes_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".tsv");

        // Cada execução é um replay determinístico do catálogo inteiro, do
        // zero — sem isto, rodar o harness de novo apenas empilharia mais
        // uma cópia dos mesmos diagnósticos em cima dos da execução
        // anterior (RepositorioModeloUsuario carrega o que já existir no
        // arquivo), inflando quantidadeInstancias e distorcendo a
        // inferência de regras a cada nova rodada.
        arquivoPerfis.delete();
        arquivoDiagnosticos.delete();

        ScaffoldingQuestionamento scaffoldingQuestionamento = new ScaffoldingQuestionamento();
        AgenteMonitor agenteMonitor = new AgenteMonitor(scaffoldingQuestionamento);
        AgenteZDP agenteZDP = new AgenteZDP();
        RepositorioModeloUsuario repositorioModeloUsuario =
                new RepositorioModeloUsuario(arquivoPerfis, arquivoDiagnosticos);
        AgenteModelador agenteModelador = new AgenteModelador(repositorioModeloUsuario);
        ConectorVereditoModelador conectorVereditoModelador = new ConectorVereditoModelador(agenteModelador);

        PrintWriter logAgentes = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoLogAgentes), "UTF-8")), true);
        logAgentes.println("seq\ttMs\tagente\tdetalhe");
        GravadorAtividadeAgentes gravador = new GravadorAtividadeAgentes(logAgentes, System.currentTimeMillis());
        agenteMonitor.adicionarOuvinte(gravador);
        agenteZDP.adicionarOuvinte(gravador);
        agenteModelador.adicionarOuvinte(gravador);

        List<EpisodioReplayHumano> episodiosReais = new RepositorioProtocolosReaisReplay().obterTodos();
        List<EpisodioReplayHumano> episodiosSinteticos = CatalogoEpisodiosSinteticos.obterTodos();
        Set<String> usuarios = new LinkedHashSet<String>();

        System.out.println("Arquivos isolados: " + arquivoPerfis.getAbsolutePath() + " / " + arquivoDiagnosticos.getAbsolutePath());
        System.out.println("Log de atividade dos agentes: " + arquivoLogAgentes.getAbsolutePath());
        System.out.println();

        System.out.println("############################################################");
        System.out.println("### REAIS (" + episodiosReais.size() + " episodios, 16 sessoes, gente de verdade) ###");
        System.out.println("############################################################");
        System.out.println();
        int divergencias = processarEpisodios(episodiosReais, agenteMonitor, agenteZDP, conectorVereditoModelador, usuarios);

        System.out.println("############################################################");
        System.out.println("### SINTETICOS (" + episodiosSinteticos.size() + " episodios, ids \"sint_*\" ###");
        System.out.println("### amplificacao artificial de erros reais - NAO e replay ###");
        System.out.println("### de gente real. So para testar a inferencia de regras. ###");
        System.out.println("############################################################");
        System.out.println();
        processarEpisodios(episodiosSinteticos, agenteMonitor, agenteZDP, conectorVereditoModelador, usuarios);

        agenteMonitor.removerOuvinte(gravador);
        agenteZDP.removerOuvinte(gravador);
        agenteModelador.removerOuvinte(gravador);
        logAgentes.close();

        System.out.println("Replay concluido: " + (episodiosReais.size() + episodiosSinteticos.size()) + " episodios ("
                + episodiosReais.size() + " reais + " + episodiosSinteticos.size() + " sinteticos), "
                + gravador.totalEventos() + " eventos de agentes, " + divergencias
                + " divergencia(s) entre veredito humano original e Agente Monitor atual (só conta nos reais).");
        System.out.println();

        System.out.println("Inferencia de regras (Agente Modelador, acao 2) por participante/persona:");
        for (String idUsuario : usuarios) {
            InferenciaRegrasModelador.Resultado resultado = agenteModelador.inferirRegras(idUsuario, 5, 5);
            System.out.println("--- " + idUsuario + " (" + resultado.quantidadeInstancias + " casos) ---");
            System.out.println(resultado.regrasPart);
            System.out.println(resultado.regrasApriori);
        }
    }

    /**
     * Papel "sintético" usado para a tarefa de escolha de categoria — mesma
     * convenção de Main.TelaGerard.CHAVE_PAPEL_CATEGORIA (constante privada
     * lá, duplicada aqui como literal porque não é acessível de fora).
     */
    private static final String CHAVE_PAPEL_CATEGORIA = "papel.categoria";

    /**
     * Toca uma lista de episódios (reais ou sintéticos, mesmo formato) pela
     * cadeia real de agentes — as três ações avaliáveis conhecidas até
     * 2026-07-30: escolha de categoria (categorizacao), posicionamento no
     * diagrama (passos) e preenchimento de incógnita por digitação (textos).
     * Devolve a contagem de divergências entre o veredito do Agente Monitor
     * atual e o veredito original registrado no protocolo humano (só
     * calculada para posicionamento — categoria e texto replicam o veredito
     * já registrado, sem re-derivação independente no harness).
     */
    private static int processarEpisodios(List<EpisodioReplayHumano> episodios, AgenteMonitor agenteMonitor,
            AgenteZDP agenteZDP, ConectorVereditoModelador conectorVereditoModelador, Set<String> usuarios) {
        int divergencias = 0;
        for (EpisodioReplayHumano episodio : episodios) {
            usuarios.add(episodio.idUsuarioReal);
            System.out.println("=== " + episodio.rotulo + " (" + episodio.idUsuarioReal + ") ===");

            for (PassoCategoriaReplayHumano passoCategoria : episodio.categorizacao) {
                boolean correto = agenteMonitor.avaliarCategoria(passoCategoria.categoriaEscolhida, episodio.categoria);
                CamadaEstrategiaZDP estrategia = agenteZDP.decidirEstrategia(
                        episodio.idUsuarioReal, episodio.categoria, CHAVE_PAPEL_CATEGORIA, correto);
                conectorVereditoModelador.registrarVeredito(
                        episodio.idUsuarioReal, episodio.categoria, CHAVE_PAPEL_CATEGORIA, estrategia, "SELECIONAR");
                String marcador = correto ? "OK" : "ERRO";
                System.out.println("  [CATEGORIA " + marcador + ", zdp=" + estrategia + "] " + passoCategoria.descricao);
                if (correto != passoCategoria.correta) {
                    divergencias++;
                    System.out.println("    >> DIVERGE do veredito humano/original ("
                            + (passoCategoria.correta ? "C" : "E") + " no protocolo)");
                }
            }

            for (PassoReplayHumano passo : episodio.passos) {
                String papelDoElementoNoDiagrama = ServicoLocalizacao.getInstancia().texto(passo.chavePapelAlvo);
                ResultadoQuestionamento resultado = agenteMonitor.avaliarPosicionamento(
                        passo.chavePapelNumeral, passo.chavePapelAlvo, papelDoElementoNoDiagrama,
                        ServicoLocalizacao.getInstancia().descricaoTipo(episodio.categoria), episodio.categoria);

                if (resultado == null || !resultado.isAplicavel()) {
                    System.out.println("  [nao aplicavel] " + passo.descricao);
                    continue;
                }

                CamadaEstrategiaZDP estrategia = agenteZDP.decidirEstrategia(
                        episodio.idUsuarioReal, episodio.categoria, passo.chavePapelAlvo, resultado.isCorreto());
                conectorVereditoModelador.registrarVeredito(
                        episodio.idUsuarioReal, episodio.categoria, passo.chavePapelAlvo, estrategia, "POSICIONAR");

                String marcador = resultado.isCorreto() ? "OK" : "ERRO";
                System.out.println("  [" + marcador + ", zdp=" + estrategia + "] " + passo.descricao);

                if (resultado.isCorreto() != passo.corretoNoProtocoloOriginal) {
                    divergencias++;
                    System.out.println("    >> DIVERGE do veredito humano/original ("
                            + (passo.corretoNoProtocoloOriginal ? "C" : "E") + " no protocolo)");
                }
            }

            for (PassoTextoReplayHumano passoTexto : episodio.textos) {
                boolean correto = agenteMonitor.avaliarValorIncognita(passoTexto.correto);
                CamadaEstrategiaZDP estrategia = agenteZDP.decidirEstrategia(
                        episodio.idUsuarioReal, episodio.categoria, passoTexto.chavePapelAlvo, correto);
                conectorVereditoModelador.registrarVeredito(
                        episodio.idUsuarioReal, episodio.categoria, passoTexto.chavePapelAlvo, estrategia, "TEXTO");
                String marcador = correto ? "OK" : "ERRO";
                System.out.println("  [TEXTO " + marcador + ", zdp=" + estrategia + "] " + passoTexto.descricao);
            }
            System.out.println();
        }
        return divergencias;
    }

    /**
     * Grava, em ordem cronológica, cada evento percebido pelos três agentes
     * — mesmo formato TSV usado por TesteMonkeySemiGuiado, para os dois
     * logs poderem ser comparados/analisados juntos (ex.: distribuição de
     * camadas de estratégia do ZDP em dado real vs. em ruído aleatório).
     */
    private static final class GravadorAtividadeAgentes
            implements OuvinteVeredictoAgenteMonitor, OuvinteEstrategiaAgenteZDP, OuvinteCasoAgenteModelador {
        private final PrintWriter destino;
        private final long inicioMs;
        private int sequencia;

        GravadorAtividadeAgentes(PrintWriter destino, long inicioMs) {
            this.destino = destino;
            this.inicioMs = inicioMs;
        }

        int totalEventos() {
            return sequencia;
        }

        @Override
        public void aoAvaliar(boolean correto) {
            registrar("Monitor", correto ? "CORRETO" : "ERRADO");
        }

        @Override
        public void aoDecidir(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo,
                               boolean correto, CamadaEstrategiaZDP estrategia) {
            registrar("ZDP", String.valueOf(estrategia));
        }

        @Override
        public void aoArmazenar(String idUsuario, DiagnosticoTarefa diagnostico) {
            registrar("Modelador", String.valueOf(diagnostico.getSuporte()));
        }

        private void registrar(String agente, String detalhe) {
            sequencia++;
            destino.println(sequencia + "\t" + (System.currentTimeMillis() - inicioMs) + "\t" + agente + "\t" + detalhe);
        }
    }
}
