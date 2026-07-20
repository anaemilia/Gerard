package gerard.pesquisador.log;

import gerard.i18n.ServicoLocalizacao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Registro local das ações do usuário e da interface.
 *
 * Cada execução do Gerard cria um arquivo TSV em Gerard/logs. A tela
 * "Visão de pesquisador" lê esses arquivos e monta as visualizações em tempo
 * de uso, sem usar dados fixos do estudo original.
 */
public class LoggerInteracaoGerard {

    private static final LoggerInteracaoGerard INSTANCIA = new LoggerInteracaoGerard();

    private final String sessao;
    private static final String MARCADOR_REGERACAO_LOGS = "logs_regerados_quadros_fieis_agente_cs_v7.flag";
    private static final boolean PERMITIR_LIMPEZA_AUTOMATICA_LOGS = Boolean.getBoolean("gerard.logs.permitirLimpezaAutomatica");

    private final File diretorioLogs;
    private final File arquivoSessao;

    private String t(String chave) {
        return ServicoLocalizacao.getInstancia().texto(chave);
    }

    private String f(String chave, Object... argumentos) {
        return ServicoLocalizacao.getInstancia().formatar(chave, argumentos);
    }

    private String usuario = "usuario_local";
    private String problemaAtual = "P1";
    private String situacaoVersaoIdAtual = "";
    private String situacaoGrupoIdAtual = "";
    private String idiomaSituacaoAtual = "";
    private String categoriaAtual = "";
    private String enunciadoAtual = "";
    private int contadorProblemas = 0;
    private int contadorTentativas = 0;
    private String tentativaAtualId = "T0000";
    private int tentativaAtualNumeroSituacao = 0;
    private String invarianteOrigemAtual = "";
    private String invarianteCodigoAtual = "";
    private String invarianteSimbolicoAtual = "";
    private String invarianteObservacaoAtual = "";

    private LoggerInteracaoGerard() {
        sessao = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        diretorioLogs = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        if (!diretorioLogs.exists()) {
            diretorioLogs.mkdirs();
        }
        apagarLogsAntigosUmaVezParaRegerarDosQuadrosAnexados();
        copiarLogInicialDosQuadrosSeNecessario();
        normalizarRotuloErroPropositalNosLogs();
        normalizarAgentesECeNosLogs();
        arquivoSessao = new File(diretorioLogs, "gerard_interacao_" + sessao + ".tsv");
        inicializarArquivo();
    }

    public static LoggerInteracaoGerard getInstancia() {
        return INSTANCIA;
    }


    private void apagarLogsAntigosUmaVezParaRegerarDosQuadrosAnexados() {
        File marcador = new File(diretorioLogs, MARCADOR_REGERACAO_LOGS);
        if (marcador.exists()) {
            return;
        }

        if (!PERMITIR_LIMPEZA_AUTOMATICA_LOGS) {
            gravarMarcadorRegeracao(marcador,
                    "Limpeza automática de logs desativada nesta versão. "
                    + "Arquivos .tsv e .tmp existentes em ~/Gerard/logs foram preservados. "
                    + "Para executar limpeza excepcional e explícita, iniciar a aplicação com "
                    + "-Dgerard.logs.permitirLimpezaAutomatica=true; nesse caso, um backup será criado antes da remoção.");
            return;
        }

        File backup = criarBackupLogsExistentes();
        File[] arquivos = diretorioLogs.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo == null || !arquivo.isFile()) {
                    continue;
                }
                String nome = arquivo.getName().toLowerCase();
                if (nome.endsWith(".tsv") || nome.endsWith(".tmp")) {
                    if (!arquivo.delete()) {
                        System.err.println("Não foi possível apagar log antigo: " + arquivo.getAbsolutePath());
                    }
                }
            }
        }

        String caminhoBackup = backup == null ? "nenhum arquivo anterior localizado" : backup.getAbsolutePath();
        gravarMarcadorRegeracao(marcador,
                "Limpeza explícita de logs executada com backup prévio. "
                + "Backup: " + caminhoBackup + ". "
                + "Nova semente fiel aos quadros anexados: coluna agente_da_acao preserva apenas C para computador/interface e S para sujeito/usuario; C/E mantido apenas nas acoes do sujeito.");
    }

    private File criarBackupLogsExistentes() {
        File[] arquivos = diretorioLogs.listFiles();
        if (arquivos == null) {
            return null;
        }
        List<File> arquivosParaBackup = new ArrayList<File>();
        for (File arquivo : arquivos) {
            if (arquivo == null || !arquivo.isFile()) {
                continue;
            }
            String nome = arquivo.getName().toLowerCase();
            if (nome.endsWith(".tsv") || nome.endsWith(".tmp")) {
                arquivosParaBackup.add(arquivo);
            }
        }
        if (arquivosParaBackup.isEmpty()) {
            return null;
        }
        File pastaBackup = new File(diretorioLogs, "backup_logs_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        if (!pastaBackup.exists()) {
            pastaBackup.mkdirs();
        }
        for (File origem : arquivosParaBackup) {
            copiarArquivo(origem, new File(pastaBackup, origem.getName()));
        }
        return pastaBackup;
    }

    private void copiarArquivo(File origem, File destino) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(origem);
            out = new FileOutputStream(destino);
            byte[] buffer = new byte[8192];
            int lidos;
            while ((lidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, lidos);
            }
        } catch (Exception ex) {
            System.err.println("Erro ao copiar log para backup: " + ex.getMessage());
        } finally {
            try { if (in != null) in.close(); } catch (Exception ignored) { }
            try { if (out != null) out.close(); } catch (Exception ignored) { }
        }
    }

    private void gravarMarcadorRegeracao(File marcador, String mensagem) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(marcador), "UTF-8"));
            writer.write(mensagem);
            writer.newLine();
        } catch (Exception ex) {
            System.err.println("Erro ao gravar marcador de regeneração do log: " + ex.getMessage());
        } finally {
            try { if (writer != null) writer.close(); } catch (Exception ignored) { }
        }
    }


    private void copiarLogInicialDosQuadrosSeNecessario() {
        File arquivoSeed = new File(diretorioLogs, "gerard_quadros_analise_tarefa_seed.tsv");
        if (arquivoSeed.exists() && arquivoSeed.length() > 0) {
            return;
        }
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = LoggerInteracaoGerard.class.getResourceAsStream("/gerard/pesquisador/log/dados/quadros_analise_tarefa_seed.tsv");
            if (in == null) {
                return;
            }
            out = new FileOutputStream(arquivoSeed);
            byte[] buffer = new byte[8192];
            int lidos;
            while ((lidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, lidos);
            }
        } catch (Exception ex) {
            System.err.println(f("pesq.log.error.seed", ex.getMessage()));
        } finally {
            try { if (in != null) in.close(); } catch (Exception ignored) { }
            try { if (out != null) out.close(); } catch (Exception ignored) { }
        }
    }

    private boolean terminaComQuebraLinha(File arquivo) {
        if (arquivo == null || !arquivo.exists() || arquivo.length() == 0) {
            return true;
        }
        java.io.RandomAccessFile raf = null;
        try {
            raf = new java.io.RandomAccessFile(arquivo, "r");
            raf.seek(arquivo.length() - 1);
            int ultimo = raf.read();
            return ultimo == '\n' || ultimo == '\r';
        } catch (Exception ex) {
            return true;
        } finally {
            try { if (raf != null) raf.close(); } catch (Exception ignored) { }
        }
    }

    private boolean arquivoContemMarcador(File arquivo, String marcador) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.indexOf(marcador) >= 0) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.err.println("Erro ao verificar log local: " + ex.getMessage());
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) { }
        }
        return false;
    }

    private void normalizarRotuloErroPropositalNosLogs() {
        File[] arquivos = diretorioLogs.listFiles();
        if (arquivos == null) {
            return;
        }
        for (File arquivo : arquivos) {
            if (arquivo != null && arquivo.isFile() && arquivo.getName().toLowerCase().endsWith(".tsv")) {
                normalizarArquivoLog(arquivo);
            }
        }
    }


    private void normalizarAgentesECeNosLogs() {
        File[] arquivos = diretorioLogs.listFiles();
        if (arquivos == null) {
            return;
        }
        for (File arquivo : arquivos) {
            if (arquivo != null && arquivo.isFile() && arquivo.getName().toLowerCase().endsWith(".tsv")) {
                normalizarAgentesECeArquivoLog(arquivo);
            }
        }
    }

    private void normalizarAgentesECeArquivoLog(File arquivo) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File temporario = new File(arquivo.getParentFile(), arquivo.getName() + ".agentes.tmp");
        boolean alterado = false;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temporario), "UTF-8"));
            String linha;
            while ((linha = reader.readLine()) != null) {
                String ajustada = normalizarLinhaLogAgentesECe(linha);
                if (!linha.equals(ajustada)) {
                    alterado = true;
                }
                writer.write(ajustada);
                writer.newLine();
            }
        } catch (Exception ex) {
            System.err.println("Erro ao normalizar agente/C-E do log: " + ex.getMessage());
            alterado = false;
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) { }
            try { if (writer != null) writer.close(); } catch (Exception ignored) { }
        }
        if (alterado) {
            if (!arquivo.delete() || !temporario.renameTo(arquivo)) {
                System.err.println("Erro ao substituir log com agentes normalizados: " + arquivo.getAbsolutePath());
            }
        } else {
            if (temporario.exists()) {
                temporario.delete();
            }
        }
    }

    private String normalizarLinhaLogAgentesECe(String linha) {
        if (linha == null || linha.trim().length() == 0 || linha.startsWith("timestamp\t")) {
            return linha;
        }
        String[] campos = linha.split("\\t", -1);
        if (campos.length < 17) {
            return linha;
        }
        campos[3] = EventoLogGerard.normalizarAgente(campos[3]);
        int indiceCe = campos.length >= 23 ? 10 : 7;
        campos[indiceCe] = "C".equals(campos[3]) ? "" : EventoLogGerard.normalizarCe(campos[indiceCe]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append('\t');
            }
            sb.append(campos[i]);
        }
        return sb.toString();
    }

    private void normalizarArquivoLog(File arquivo) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File temporario = new File(arquivo.getParentFile(), arquivo.getName() + ".tmp");
        boolean alterado = false;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temporario), "UTF-8"));
            String linha;
            while ((linha = reader.readLine()) != null) {
                String ajustada = linha.replace("Erro proposital", "erro");
                if (!linha.equals(ajustada)) {
                    alterado = true;
                }
                writer.write(ajustada);
                writer.newLine();
            }
        } catch (Exception ex) {
            System.err.println("Erro ao normalizar log: " + ex.getMessage());
            alterado = false;
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) { }
            try { if (writer != null) writer.close(); } catch (Exception ignored) { }
        }
        if (alterado) {
            if (!arquivo.delete() || !temporario.renameTo(arquivo)) {
                System.err.println("Erro ao substituir log normalizado: " + arquivo.getAbsolutePath());
            }
        } else {
            if (temporario.exists()) {
                temporario.delete();
            }
        }
    }

    private void inicializarArquivo() {
        try {
            if (!arquivoSessao.exists() || arquivoSessao.length() == 0) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoSessao, true), "UTF-8"));
                writer.write(EventoLogGerard.cabecalhoTsv());
                writer.newLine();
                writer.close();
            }
        } catch (Exception ex) {
            System.err.println(f("pesq.log.error.init", ex.getMessage()));
        }
    }


    public synchronized String getTentativaAtualId() { return tentativaAtualId; }
    public synchronized int getTentativaAtualNumeroSituacao() { return tentativaAtualNumeroSituacao; }
    public synchronized String getUsuarioAtual() { return usuario; }
    public synchronized String getProblemaAtual() { return problemaAtual; }
    public String getSessaoId() { return sessao; }

    /**
     * Fotografia do contexto lógico de uma tentativa. Permite que uma aba com
     * tarefa própria use o logger sem sobrescrever definitivamente a tentativa
     * que permanece ativa em outra aba.
     */
    public static final class ContextoInteracao {
        private final String problemaAtual;
        private final String situacaoVersaoIdAtual;
        private final String situacaoGrupoIdAtual;
        private final String idiomaSituacaoAtual;
        private final String categoriaAtual;
        private final String enunciadoAtual;
        private final String tentativaAtualId;
        private final int tentativaAtualNumeroSituacao;
        private final String invarianteOrigemAtual;
        private final String invarianteCodigoAtual;
        private final String invarianteSimbolicoAtual;
        private final String invarianteObservacaoAtual;

        private ContextoInteracao(LoggerInteracaoGerard logger) {
            this.problemaAtual = logger.problemaAtual;
            this.situacaoVersaoIdAtual = logger.situacaoVersaoIdAtual;
            this.situacaoGrupoIdAtual = logger.situacaoGrupoIdAtual;
            this.idiomaSituacaoAtual = logger.idiomaSituacaoAtual;
            this.categoriaAtual = logger.categoriaAtual;
            this.enunciadoAtual = logger.enunciadoAtual;
            this.tentativaAtualId = logger.tentativaAtualId;
            this.tentativaAtualNumeroSituacao = logger.tentativaAtualNumeroSituacao;
            this.invarianteOrigemAtual = logger.invarianteOrigemAtual;
            this.invarianteCodigoAtual = logger.invarianteCodigoAtual;
            this.invarianteSimbolicoAtual = logger.invarianteSimbolicoAtual;
            this.invarianteObservacaoAtual = logger.invarianteObservacaoAtual;
        }
    }

    public synchronized ContextoInteracao capturarContextoAtual() {
        return new ContextoInteracao(this);
    }

    public synchronized void restaurarContexto(ContextoInteracao contexto) {
        if (contexto == null) {
            return;
        }
        problemaAtual = contexto.problemaAtual;
        situacaoVersaoIdAtual = contexto.situacaoVersaoIdAtual;
        situacaoGrupoIdAtual = contexto.situacaoGrupoIdAtual;
        idiomaSituacaoAtual = contexto.idiomaSituacaoAtual;
        categoriaAtual = contexto.categoriaAtual;
        enunciadoAtual = contexto.enunciadoAtual;
        tentativaAtualId = contexto.tentativaAtualId;
        tentativaAtualNumeroSituacao = contexto.tentativaAtualNumeroSituacao;
        invarianteOrigemAtual = contexto.invarianteOrigemAtual;
        invarianteCodigoAtual = contexto.invarianteCodigoAtual;
        invarianteSimbolicoAtual = contexto.invarianteSimbolicoAtual;
        invarianteObservacaoAtual = contexto.invarianteObservacaoAtual;
    }


    public synchronized void definirUsuario(String usuario) {
        if (usuario != null && usuario.trim().length() > 0) {
            this.usuario = usuario.trim();
        }
    }

    public synchronized void novoProblema(String categoria, String enunciado) {
        novoProblema(categoria, enunciado, "", "", "");
    }

    public synchronized void novoProblema(String categoria, String enunciado,
                                           String situacaoVersaoId, String situacaoGrupoId,
                                           String idiomaSituacao) {

        contadorProblemas++;
        contadorTentativas++;
        tentativaAtualId = "T" + String.format("%04d", contadorTentativas) + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        problemaAtual = "P" + contadorProblemas;
        categoriaAtual = categoria != null ? categoria : "";
        enunciadoAtual = enunciado != null ? enunciado : "";
        situacaoVersaoIdAtual = situacaoVersaoId != null ? situacaoVersaoId : "";
        situacaoGrupoIdAtual = situacaoGrupoId != null ? situacaoGrupoId : "";
        idiomaSituacaoAtual = idiomaSituacao != null ? idiomaSituacao : "";
        tentativaAtualNumeroSituacao = calcularProximoNumeroTentativaSituacao();
        invarianteOrigemAtual = "";
        invarianteCodigoAtual = "";
        invarianteSimbolicoAtual = "";
        invarianteObservacaoAtual = "";
        registrarComputador(
                t("pesq.log.task.presentQuestion"),
                t("pesq.log.instrument.taskStatement"),
                t("pesq.log.artifact.initialScreen"),
                t("pesq.log.function.startProblem"),
                t("pesq.log.rule.readAndSolve"),
                "PROBLEMA",
                f("pesq.log.detail.category", categoriaAtual)
        );
    }

    private int calcularProximoNumeroTentativaSituacao() {
        String chaveGrupo = situacaoGrupoIdAtual == null ? "" : situacaoGrupoIdAtual.trim();
        String chaveVersao = situacaoVersaoIdAtual == null ? "" : situacaoVersaoIdAtual.trim();
        String chaveEnunciado = enunciadoAtual == null ? "" : enunciadoAtual.trim();
        Set<String> tentativas = new HashSet<String>();
        File[] arquivos = diretorioLogs.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (!arquivo.isFile() || !arquivo.getName().startsWith("gerard_interacao_") || !arquivo.getName().endsWith(".tsv")) {
                    continue;
                }
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        EventoLogGerard evento = EventoLogGerard.deTsv(linha);
                        if (evento == null || !usuario.equals(evento.getUsuario())) continue;
                        boolean mesmaSituacao;
                        if (chaveGrupo.length() > 0) {
                            mesmaSituacao = chaveGrupo.equals(evento.getSituacaoGrupoId());
                        } else if (chaveVersao.length() > 0) {
                            mesmaSituacao = chaveVersao.equals(evento.getSituacaoVersaoId());
                        } else {
                            mesmaSituacao = chaveEnunciado.equals(evento.getEnunciado());
                        }
                        if (mesmaSituacao && evento.getTentativa() != null && evento.getTentativa().trim().length() > 0) {
                            tentativas.add(evento.getTentativa().trim());
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao contar tentativas da situação: " + ex.getMessage());
                } finally {
                    if (reader != null) try { reader.close(); } catch (Exception ignore) { }
                }
            }
        }
        return tentativas.size() + 1;
    }

    public synchronized void atualizarContexto(String categoria, String enunciado) {
        categoriaAtual = categoria != null ? categoria : categoriaAtual;
        enunciadoAtual = enunciado != null ? enunciado : enunciadoAtual;
    }

    public synchronized void registrarUsuario(String tarefa,
                                              String ce,
                                              String instrumentoOrganizacao,
                                              String instrumentoArtefato,
                                              String funcaoDoArtefato,
                                              String objeto,
                                              String regras,
                                              String origemEvento,
                                              String detalhes) {
        String tipo = inferirTipoAcao(tarefa, origemEvento, detalhes);
        registrar("S", tarefa, ce, instrumentoOrganizacao, instrumentoArtefato, funcaoDoArtefato, objeto, regras, origemEvento, detalhes, tipo, descreverPropriedade(tipo), "");
    }

    public synchronized void registrarAcaoGranularUsuario(String tipoAcao,
                                                           String tarefa,
                                                           String instrumentoOrganizacao,
                                                           String instrumentoArtefato,
                                                           String funcaoDoArtefato,
                                                           String objeto,
                                                           String origemEvento,
                                                           String detalhes,
                                                           String mudancaObservavel) {
        String tipo = normalizarTipoAcao(tipoAcao);
        registrar("S", tarefa, "-", instrumentoOrganizacao, instrumentoArtefato,
                funcaoDoArtefato, objeto,
                "Registro granular de técnica de interação; não corresponde, isoladamente, a acerto ou erro matemático.",
                origemEvento, detalhes, tipo, descreverPropriedade(tipo), mudancaObservavel);
    }

    public synchronized void registrarComputador(String tarefa,
                                                 String instrumentoOrganizacao,
                                                 String instrumentoArtefato,
                                                 String funcaoDoArtefato,
                                                 String regras,
                                                 String origemEvento,
                                                 String detalhes) {
        registrar("C", tarefa, "", instrumentoOrganizacao, instrumentoArtefato, funcaoDoArtefato, "", regras, origemEvento, detalhes, "", "", "");
    }

    public synchronized void registrar(String agente,
                                       String tarefa,
                                       String ce,
                                       String instrumentoOrganizacao,
                                       String instrumentoArtefato,
                                       String funcaoDoArtefato,
                                       String objeto,
                                       String regras,
                                       String origemEvento,
                                       String detalhes) {
        registrar(agente, tarefa, ce, instrumentoOrganizacao, instrumentoArtefato, funcaoDoArtefato, objeto, regras, origemEvento, detalhes, "", "", "");
    }

    public synchronized void registrar(String agente,
                                       String tarefa,
                                       String ce,
                                       String instrumentoOrganizacao,
                                       String instrumentoArtefato,
                                       String funcaoDoArtefato,
                                       String objeto,
                                       String regras,
                                       String origemEvento,
                                       String detalhes,
                                       String tipoAcaoInteracao,
                                       String propriedadeAcao,
                                       String mudancaObservavel) {
        EventoLogGerard evento = EventoLogGerard.criar(
                sessao,
                usuario,
                agente,
                problemaAtual,
                tentativaAtualId,
                tarefa,
                ce,
                instrumentoOrganizacao,
                instrumentoArtefato,
                funcaoDoArtefato,
                objeto,
                regras,
                categoriaAtual,
                enunciadoAtual,
                origemEvento,
                detalhes,
                situacaoVersaoIdAtual,
                situacaoGrupoIdAtual,
                idiomaSituacaoAtual,
                tipoAcaoInteracao,
                propriedadeAcao,
                mudancaObservavel
        );
        evento.setTentativaNumeroSituacao(String.valueOf(tentativaAtualNumeroSituacao));
        evento.setInvarianteOrigem(invarianteOrigemAtual);
        evento.setInvarianteCodigo(invarianteCodigoAtual);
        evento.setInvarianteSimbolico(invarianteSimbolicoAtual);
        evento.setInvarianteObservacao(invarianteObservacaoAtual);
        evento.setNaturezaAcao(classificarNaturezaAcao(tarefa, origemEvento, tipoAcaoInteracao, instrumentoArtefato));
        evento.setEfeitoAcao(classificarEfeitoAcao(tarefa, origemEvento, tipoAcaoInteracao, mudancaObservavel));
        gravar(evento);
    }

    /**
     * Classifica a natureza da ação sem confundir a tarefa matemática com a
     * navegabilidade da interface. A categoria continua sendo fonte da verdade
     * apenas para os elementos da tarefa matemática.
     */
    private String classificarNaturezaAcao(String tarefa, String origemEvento,
                                           String tipoAcaoInteracao, String instrumentoArtefato) {
        String contexto = normalizarClassificacao(tarefa + " " + origemEvento + " " + instrumentoArtefato);
        if (contemAlgum(contexto,
                "IDIOMA", "CURADORIA", "ANALISE", "EXPLICACOES", "REPRESENTACAO",
                "ESTILO", "RESTAURAR", "REDIMENSION", "SORTEAR", "CATEGORIA",
                "MENU", "JANELA", "TELA")) {
            return "NAVEGABILIDADE";
        }
        String protocolo = normalizarClassificacao(tipoAcaoInteracao);
        if (contemAlgum(protocolo,
                "SELECIONAR", "CAMINHO", "ORIENTACAO", "POSICIONAR",
                "QUANTIFICAR", "TEXTO", "REMOVER", "ALTERAR_SINAL")) {
            return "MODELAGEM";
        }
        return "NAVEGABILIDADE";
    }

    /**
     * Registra a consequência observável separadamente da natureza da ação.
     * Assim, RESTAURAR é navegabilidade, mas seu efeito é reiniciar a modelagem.
     */
    private String classificarEfeitoAcao(String tarefa, String origemEvento,
                                         String tipoAcaoInteracao, String mudancaObservavel) {
        String contexto = normalizarClassificacao(tarefa + " " + origemEvento + " "
                + tipoAcaoInteracao + " " + mudancaObservavel);
        if (contemAlgum(contexto, "RESTAURAR", "NOVO_PROBLEMA", "SORTEAR", "TROCAR_CATEGORIA")) {
            return "REINICIA_MODELAGEM";
        }
        if (contemAlgum(contexto,
                "TROCAR_IDIOMA", "ALTERAR IDIOMA", "REPRESENTACAO", "ESTILO",
                "REDIMENSION", "ABRIR", "FECHAR", "EXIBIR", "OCULTAR")) {
            return "ALTERA_VISUALIZACAO";
        }
        if (contemAlgum(contexto,
                "POSICIONAR", "QUANTIFICAR", "MODIFICAR TEXTO", "SUBSTITUIR TEXTO",
                "REMOVER", "ALTERAR_SINAL", "SINAL", "REPOSICIONAMENTO")) {
            return "ALTERA_MODELAGEM";
        }
        return "NENHUM";
    }

    private String normalizarClassificacao(String valor) {
        String texto = valor == null ? "" : valor;
        texto = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return texto.toUpperCase(java.util.Locale.ROOT);
    }

    private boolean contemAlgum(String texto, String... termos) {
        if (texto == null) return false;
        for (String termo : termos) {
            if (texto.contains(termo)) return true;
        }
        return false;
    }



    /**
     * Associa o invariante selecionado a toda ação da tentativa atual.
     * As ações anteriores são regravadas com os mesmos dados originais e com
     * os quatro campos de invariante repetidos; as ações posteriores recebem
     * os mesmos campos no momento em que forem registradas.
     */
    public synchronized void associarInvarianteATentativaAtual(String origem,
                                                                String codigo,
                                                                String simbolico,
                                                                String observacao) {
        invarianteOrigemAtual = origem == null ? "" : origem;
        invarianteCodigoAtual = codigo == null ? "" : codigo;
        invarianteSimbolicoAtual = simbolico == null ? "" : simbolico;
        invarianteObservacaoAtual = observacao == null ? "" : observacao;

        File temporario = new File(arquivoSessao.getParentFile(), arquivoSessao.getName() + ".invariante.tmp");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoSessao), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temporario), "UTF-8"));
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.startsWith("timestamp\t")) {
                    writer.write(EventoLogGerard.cabecalhoTsv());
                } else {
                    EventoLogGerard evento = EventoLogGerard.deTsv(linha);
                    if (evento != null && tentativaAtualId.equals(evento.getTentativa())) {
                        evento.setInvarianteOrigem(invarianteOrigemAtual);
                        evento.setInvarianteCodigo(invarianteCodigoAtual);
                        evento.setInvarianteSimbolico(invarianteSimbolicoAtual);
                        evento.setInvarianteObservacao(invarianteObservacaoAtual);
                        writer.write(evento.toTsv());
                    } else {
                        writer.write(linha);
                    }
                }
                writer.newLine();
            }
        } catch (Exception ex) {
            System.err.println("Erro ao associar invariante às ações da tentativa: " + ex.getMessage());
            if (temporario.exists()) temporario.delete();
            return;
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) { }
            try { if (writer != null) writer.close(); } catch (Exception ignored) { }
        }
        if (!arquivoSessao.delete() || !temporario.renameTo(arquivoSessao)) {
            System.err.println("Erro ao substituir log após associação do invariante.");
        }
    }

    /**
     * Registra, no mesmo log de interação da sessão, a explicação matemática
     * declarada para um elemento semântico. A explicação geral e o invariante
     * operatório são deliberadamente repetidos em cada elemento para manter a
     * menor granularidade analítica.
     */
    public synchronized void registrarExplicacaoMatematica(String elemento,
                                                            String papelSemantico,
                                                            String explicacaoElemento,
                                                            String dificuldade,
                                                            String explicacaoGeral,
                                                            String invarianteOrigem,
                                                            String invarianteCodigo,
                                                            String invarianteSimbolico,
                                                            String invarianteObservacao,
                                                            String fotografiaModelagem) {
        String detalhes = "papel_semantico=" + campoDetalhe(papelSemantico)
                + ";dificuldade=" + campoDetalhe(dificuldade)
                + ";explicacao_elemento=" + campoDetalhe(explicacaoElemento)
                + ";explicacao_geral=" + campoDetalhe(explicacaoGeral)
                + ";invariante_origem=" + campoDetalhe(invarianteOrigem)
                + ";invariante_codigo=" + campoDetalhe(invarianteCodigo)
                + ";invariante_simbolico=" + campoDetalhe(invarianteSimbolico)
                + ";invariante_observacao=" + campoDetalhe(invarianteObservacao)
                + ";fotografia_modelagem=" + campoDetalhe(fotografiaModelagem);
        registrar("S",
                "TAREFA_MATEMATICA_EXPLICACAO",
                "-",
                "Interpretacao da situacao-problema",
                "Artefato explicativo",
                "Explicitar a decisao de modelagem",
                elemento,
                "Papel semantico definido na curadoria: " + (papelSemantico == null ? "" : papelSemantico),
                "TELA_EXPLICACOES",
                detalhes,
                "TEXTO",
                "Explicacao declarada pelo usuario para o elemento semantico",
                "Registro qualitativo associado a tentativa atual");
    }

    /** Retorna os protocolos automáticos observados na tentativa atual, por objeto. */
    public synchronized java.util.Map<String, String> resumoProtocolosTentativaAtual() {
        java.util.Map<String, java.util.LinkedHashSet<String>> porObjeto =
                new java.util.LinkedHashMap<String, java.util.LinkedHashSet<String>>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoSessao), "UTF-8"));
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.startsWith("timestamp\t")) continue;
                String[] c = linha.split("\t", -1);
                if (c.length < 23 || !tentativaAtualId.equals(c[8])) continue;
                String objeto = resolverElementoSemanticoDoEvento(c[14], c[19]);
                String protocolo = c[20];
                if (objeto == null || objeto.trim().length() == 0 || protocolo == null || protocolo.trim().length() == 0) continue;
                java.util.LinkedHashSet<String> set = porObjeto.get(objeto);
                if (set == null) { set = new java.util.LinkedHashSet<String>(); porObjeto.put(objeto, set); }
                set.add(protocolo);
            }
        } catch (Exception ex) {
            System.err.println("Erro ao resumir protocolos da tentativa: " + ex.getMessage());
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) { }
        }
        java.util.Map<String, String> resultado = new java.util.LinkedHashMap<String, String>();
        for (java.util.Map.Entry<String, java.util.LinkedHashSet<String>> e : porObjeto.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (String v : e.getValue()) { if (sb.length() > 0) sb.append(" -> "); sb.append(v); }
            resultado.put(e.getKey(), sb.toString());
        }
        return resultado;
    }


    /**
     * Resolve o elemento semântico realmente manipulado a partir da linha do
     * log. Os eventos granulares antigos usam OBJ_INTERACAO na coluna objeto,
     * mas preservam o valor em detalhes (valor=..., objeto=... ou texto=...).
     * A tela de explicações precisa agrupar pelos valores semânticos da
     * situação, e não pelo identificador técnico do registrador.
     */
    private String resolverElementoSemanticoDoEvento(String objetoRegistrado, String detalhes) {
        String objeto = objetoRegistrado == null ? "" : objetoRegistrado.trim();
        if (objeto.length() > 0 && !"OBJ_INTERACAO".equalsIgnoreCase(objeto)) {
            return objeto;
        }
        String valor = extrairCampoDetalhe(detalhes, "valor");
        if (valor.length() == 0) valor = extrairCampoDetalhe(detalhes, "objeto");
        if (valor.length() == 0) valor = extrairCampoDetalhe(detalhes, "texto");
        return valor;
    }

    private String extrairCampoDetalhe(String detalhes, String chave) {
        if (detalhes == null || chave == null) return "";
        String prefixo = chave + "=";
        int inicio = detalhes.indexOf(prefixo);
        if (inicio < 0) return "";
        inicio += prefixo.length();
        StringBuilder valor = new StringBuilder();
        boolean escapado = false;
        for (int i = inicio; i < detalhes.length(); i++) {
            char ch = detalhes.charAt(i);
            if (escapado) {
                valor.append(ch);
                escapado = false;
            } else if (ch == '\\') {
                escapado = true;
            } else if (ch == ';') {
                break;
            } else {
                valor.append(ch);
            }
        }
        return valor.toString().trim();
    }

    private String campoDetalhe(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("\r", " ")
                .replace("\n", " ")
                .trim();
    }

    private String inferirTipoAcao(String tarefa, String origemEvento, String detalhes) {
        String texto = ((tarefa == null ? "" : tarefa) + " " +
                (origemEvento == null ? "" : origemEvento) + " " +
                (detalhes == null ? "" : detalhes)).toLowerCase();
        if (texto.contains("selecion") || texto.contains("menu") || texto.contains("escolh")) return "SELECIONAR";
        if (texto.contains("arrast") || texto.contains("solt") || texto.contains("posicion") || texto.contains("associar valor")) return "POSICIONAR";
        if (texto.contains("orient") || texto.contains("direção") || texto.contains("direcao")) return "ORIENTACAO";
        if (texto.contains("caminho") || texto.contains("trajet")) return "CAMINHO";
        if (texto.contains("número") || texto.contains("numero") || texto.contains("valor numérico") || texto.contains("valor numerico") || texto.contains("sinal")) return "QUANTIFICAR";
        if (texto.contains("texto") || texto.contains("editar") || texto.contains("enunciado")) return "TEXTO";
        return "";
    }

    private String normalizarTipoAcao(String tipo) {
        if (tipo == null) return "";
        String v = java.text.Normalizer.normalize(tipo, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase().trim();
        if (v.startsWith("ORIENT")) return "ORIENTACAO";
        if (v.startsWith("SELEC")) return "SELECIONAR";
        if (v.startsWith("POSIC")) return "POSICIONAR";
        if (v.startsWith("CAMIN")) return "CAMINHO";
        if (v.startsWith("QUANT")) return "QUANTIFICAR";
        if (v.startsWith("TEXT")) return "TEXTO";
        return v;
    }

    private String descreverPropriedade(String tipo) {
        if ("SELECIONAR".equals(tipo)) return "Escolha de uma opção entre itens disponíveis.";
        if ("POSICIONAR".equals(tipo)) return "Definição de uma posição em espaço de uma ou mais dimensões.";
        if ("ORIENTACAO".equals(tipo)) return "Escolha ou mudança de direção durante a interação espacial.";
        if ("CAMINHO".equals(tipo)) return "Série contínua e rápida de operações de orientação e posicionamento.";
        if ("QUANTIFICAR".equals(tipo)) return "Especificação ou alteração de um valor numérico.";
        if ("TEXTO".equals(tipo)) return "Inserção, movimentação ou modificação de texto.";
        return "";
    }

    private void gravar(EventoLogGerard evento) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoSessao, true), "UTF-8"));
            writer.write(evento.toTsv());
            writer.newLine();
            writer.close();
        } catch (Exception ex) {
            System.err.println(f("pesq.log.error.write", ex.getMessage()));
        }
    }

    public File getDiretorioLogs() {
        return diretorioLogs;
    }

    public File getArquivoSessao() {
        return arquivoSessao;
    }

    public String getSessao() {
        return sessao;
    }

    public List<EventoLogGerard> carregarEventos() {
        List<EventoLogGerard> eventos = new ArrayList<EventoLogGerard>();
        File[] arquivos = diretorioLogs.listFiles();
        if (arquivos == null) {
            return eventos;
        }
        Arrays.sort(arquivos, new Comparator<File>() {
            public int compare(File a, File b) {
                if (a == null && b == null) return 0;
                if (a == null) return 1;
                if (b == null) return -1;
                return a.getName().compareTo(b.getName());
            }
        });
        for (int i = 0; i < arquivos.length; i++) {
            File arquivo = arquivos[i];
            if (arquivo == null || !arquivo.isFile() || !arquivo.getName().endsWith(".tsv")) {
                continue;
            }
            carregarEventosDoArquivo(arquivo, eventos);
        }
        return eventos;
    }

    private void carregarEventosDoArquivo(File arquivo, List<EventoLogGerard> eventos) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
            String linha;
            while ((linha = reader.readLine()) != null) {
                EventoLogGerard evento = EventoLogGerard.deTsv(linha);
                if (evento != null) {
                    eventos.add(evento);
                }
            }
            reader.close();
        } catch (Exception ex) {
            System.err.println(f("pesq.log.error.read", arquivo.getName(), ex.getMessage()));
        }
    }
}
