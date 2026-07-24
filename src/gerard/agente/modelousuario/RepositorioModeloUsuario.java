package gerard.agente.modelousuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositório compartilhado de Modelo do Usuário mencionado em
 * gerard-ajuda-adaptativa/SKILL.md ("Repositórios de dados compartilhados"):
 * escrito pelo Agente Modelador, consultado pelo Agente ZDP.
 *
 * Guarda em memória, por id de usuário (mesmo identificador do campo
 * "usuario" do log — ver gerard-log-acao-instrumental/SKILL.md).
 *
 * Persistência em disco adicionada em 2026-07-22 (decisão do usuário, junto
 * com a tela de cadastro — ver gerard.ui.usuario.DialogoUsuario): as
 * dimensões 3 e 4 (Perfil do aluno + Perfil da aprendizagem — os dados que a
 * tela de cadastro efetivamente coleta) são persistidas, num TSV em
 * ~/Gerard/perfis_usuario.tsv, seguindo a mesma convenção de local de
 * arquivo já usada por LoggerInteracaoGerard (~/Gerard/logs).
 *
 * A dimensão 5 (Diagnóstico da tarefa — os "casos" que o Agente Modelador
 * acumula durante o uso) também passou a persistir, em
 * ~/Gerard/diagnosticos_tarefa.tsv, por decisão do usuário em 2026-07-23 —
 * arquivo próprio porque é relação um-para-muitos (vários casos por
 * usuário), não cabe no formato uma-linha-por-usuário de
 * perfis_usuario.tsv. Ver salvarDiagnosticos()/carregarDiagnosticosDoArquivo().
 *
 * "fotoCaminho" (2026-07-22, ver PerfilAluno) não é copiada pra dentro do
 * TSV — só o caminho do arquivo é armazenado. A foto em si é copiada para
 * ~/Gerard/fotos/&lt;id&gt;.&lt;extensão&gt; na hora do cadastro, preservando o
 * arquivo original escolhido pelo usuário (que pode estar em qualquer
 * lugar do disco, inclusive removível). Compatível com arquivos
 * perfis_usuario.tsv de antes dessa mudança: linhas com 6 colunas (sem
 * foto) continuam carregando normalmente, só sem fotoCaminho.
 */
public class RepositorioModeloUsuario {
    private static final String CABECALHO = "id\tnome\tidade\tsexo\tmidiaPreferida\tnivelEscolaridade\tfotoCaminho";

    // Diagnósticos (dimensão 5) persistidos em arquivo próprio, separado dos
    // perfis: é relação um-para-muitos (vários casos por usuário), não cabe
    // no formato uma-linha-por-usuário de perfis_usuario.tsv. Decisão do
    // usuário em 2026-07-23 — até então essa dimensão só existia em memória
    // (ver histórico deste arquivo).
    private static final String CABECALHO_DIAGNOSTICOS = "usuario_id\ttarefa\tregra_de_acao\tsuporte\t"
            + "internalizado\tprobabilidade_saber_conteudo\tdificuldade_autorrelatada\texplicacao_elemento\t"
            + "explicacao_geral\tnivel_conceitual_estimado\tnivel_conceitual_curado\t"
            + "invariante_origem\tinvariante_codigo\tinvariante_simbolico\tinvariante_observacao";

    private final Map<String, ModeloUsuario> modelosPorId = new LinkedHashMap<String, ModeloUsuario>();
    private final File arquivoPerfis;
    private final File arquivoDiagnosticos;

    public RepositorioModeloUsuario() {
        this(new File(new File(System.getProperty("user.home"), "Gerard"), "perfis_usuario.tsv"),
                new File(new File(System.getProperty("user.home"), "Gerard"), "diagnosticos_tarefa.tsv"));
    }

    /** Construtor usado por testes: permite apontar para arquivos isolados em vez de ~/Gerard. */
    public RepositorioModeloUsuario(File arquivoPerfis, File arquivoDiagnosticos) {
        this.arquivoPerfis = arquivoPerfis;
        this.arquivoDiagnosticos = arquivoDiagnosticos;
        carregarDoArquivo();
        carregarDiagnosticosDoArquivo();
    }

    public ModeloUsuario obterOuCriar(String idUsuario) {
        ModeloUsuario modelo = modelosPorId.get(idUsuario);
        if (modelo == null) {
            modelo = new ModeloUsuario(idUsuario);
            modelosPorId.put(idUsuario, modelo);
        }
        return modelo;
    }

    public ModeloUsuario obter(String idUsuario) {
        return modelosPorId.get(idUsuario);
    }

    /** Perfis cadastrados, na ordem em que foram carregados/criados — para a tela de seleção de usuário. */
    public List<ModeloUsuario> listarPerfisCadastrados() {
        List<ModeloUsuario> resultado = new ArrayList<ModeloUsuario>();
        for (ModeloUsuario modelo : modelosPorId.values()) {
            if (modelo.getPerfilAluno().getNome() != null) {
                resultado.add(modelo);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    /**
     * Todos os modelos rastreados, cadastrados ou não — ao contrário de
     * listarPerfisCadastrados(), inclui também "usuario_local" (o id padrão
     * de LoggerInteracaoGerard antes de qualquer cadastro via
     * DialogoUsuario). Diagnósticos/explicações são gravados sob
     * loggerInteracaoGerard.getUsuarioAtual() independente de cadastro, e a
     * curadoria de nível conceitual (Visão de Pesquisador) precisa
     * enxergá-los mesmo sem nome — decisão do usuário em 2026-07-23.
     */
    public List<ModeloUsuario> listarTodosOsModelos() {
        return Collections.unmodifiableList(new ArrayList<ModeloUsuario>(modelosPorId.values()));
    }

    /**
     * Cadastra um novo perfil (dimensões 3 e 4) e persiste imediatamente.
     * @return o id gerado, único entre os perfis já cadastrados.
     */
    public synchronized String cadastrarPerfil(String nome, Integer idade, Genero sexo,
                                                MidiaPreferida midiaPreferida, NivelEscolaridade nivelEscolaridade,
                                                File fotoOrigem) {
        String id = gerarIdUnico(nome);
        ModeloUsuario modelo = obterOuCriar(id);
        modelo.getPerfilAluno().setNome(sanitizar(nome));
        modelo.getPerfilAluno().setIdade(idade);
        modelo.getPerfilAluno().setSexo(sexo);
        modelo.getPerfilAprendizagem().setMidiaPreferida(midiaPreferida);
        modelo.getPerfilAprendizagem().setNivelEscolaridade(nivelEscolaridade);
        if (fotoOrigem != null && fotoOrigem.isFile()) {
            modelo.getPerfilAluno().setFotoCaminho(copiarFoto(id, fotoOrigem));
        }
        salvarNoArquivo();
        return id;
    }

    /** Copia a foto escolhida para ~/Gerard/fotos/&lt;id&gt;.&lt;extensão&gt; e devolve o caminho absoluto, ou null se a cópia falhar. */
    private String copiarFoto(String id, File origem) {
        try {
            File pastaFotos = new File(arquivoPerfis.getParentFile(), "fotos");
            if (!pastaFotos.exists()) {
                pastaFotos.mkdirs();
            }
            String extensao = obterExtensao(origem.getName());
            File destino = new File(pastaFotos, id + (extensao.isEmpty() ? "" : "." + extensao));
            Files.copy(origem.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destino.getAbsolutePath();
        } catch (IOException ex) {
            return null;
        }
    }

    private String obterExtensao(String nomeArquivo) {
        int ponto = nomeArquivo.lastIndexOf('.');
        return ponto < 0 ? "" : nomeArquivo.substring(ponto + 1).toLowerCase();
    }

    private String gerarIdUnico(String nome) {
        String base = nome == null ? "" : nome.trim().toLowerCase()
                .replaceAll("[^a-z0-9áàâãéêíóôõúüç ]", "")
                .replaceAll("\\s+", "_");
        if (base.length() == 0) {
            base = "usuario";
        }
        String candidato = base;
        int sufixo = 1;
        while (modelosPorId.containsKey(candidato)) {
            sufixo++;
            candidato = base + "_" + sufixo;
        }
        return candidato;
    }

    private String sanitizar(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.replace("\r", " ").replace("\n", " ").replace("\t", " ").trim();
    }

    private void carregarDoArquivo() {
        if (!arquivoPerfis.exists()) {
            return;
        }
        BufferedReader leitor = null;
        try {
            leitor = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoPerfis), "UTF-8"));
            String linha = leitor.readLine();
            if (linha == null) {
                return;
            }
            while ((linha = leitor.readLine()) != null) {
                if (linha.trim().length() == 0) {
                    continue;
                }
                String[] campos = linha.split("\t", -1);
                if (campos.length < 6) {
                    continue;
                }
                ModeloUsuario modelo = obterOuCriar(campos[0]);
                modelo.getPerfilAluno().setNome(campos[1].length() == 0 ? null : campos[1]);
                modelo.getPerfilAluno().setIdade(campos[2].length() == 0 ? null : Integer.valueOf(campos[2]));
                modelo.getPerfilAluno().setSexo(campos[3].length() == 0 ? null : Genero.valueOf(campos[3]));
                modelo.getPerfilAprendizagem().setMidiaPreferida(
                        campos[4].length() == 0 ? null : MidiaPreferida.valueOf(campos[4]));
                modelo.getPerfilAprendizagem().setNivelEscolaridade(
                        campos[5].length() == 0 ? null : NivelEscolaridade.valueOf(campos[5]));
                if (campos.length > 6 && campos[6].length() > 0) {
                    modelo.getPerfilAluno().setFotoCaminho(campos[6]);
                }
            }
        } catch (IOException | IllegalArgumentException ex) {
            // Perfis corrompidos/ilegíveis não devem impedir o app de abrir;
            // segue com o repositório vazio (equivalente a nenhum cadastro).
        } finally {
            fechar(leitor);
        }
    }

    private synchronized void salvarNoArquivo() {
        File pai = arquivoPerfis.getParentFile();
        if (pai != null && !pai.exists()) {
            pai.mkdirs();
        }
        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoPerfis), "UTF-8"));
            escritor.write(CABECALHO);
            escritor.newLine();
            for (ModeloUsuario modelo : listarPerfisCadastrados()) {
                PerfilAluno aluno = modelo.getPerfilAluno();
                PerfilAprendizagem aprendizagem = modelo.getPerfilAprendizagem();
                escritor.write(aluno.getId());
                escritor.write("\t");
                escritor.write(aluno.getNome() == null ? "" : aluno.getNome());
                escritor.write("\t");
                escritor.write(aluno.getIdade() == null ? "" : String.valueOf(aluno.getIdade()));
                escritor.write("\t");
                escritor.write(aluno.getSexo() == null ? "" : aluno.getSexo().name());
                escritor.write("\t");
                escritor.write(aprendizagem.getMidiaPreferida() == null ? "" : aprendizagem.getMidiaPreferida().name());
                escritor.write("\t");
                escritor.write(aprendizagem.getNivelEscolaridade() == null ? "" : aprendizagem.getNivelEscolaridade().name());
                escritor.write("\t");
                escritor.write(aluno.getFotoCaminho() == null ? "" : aluno.getFotoCaminho());
                escritor.newLine();
            }
        } catch (IOException ex) {
            // Falha ao persistir não deve derrubar a UI; o cadastro segue
            // válido em memória pelo resto da sessão.
        } finally {
            fechar(escritor);
        }
    }

    private void carregarDiagnosticosDoArquivo() {
        if (!arquivoDiagnosticos.exists()) {
            return;
        }
        BufferedReader leitor = null;
        try {
            leitor = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoDiagnosticos), "UTF-8"));
            String linha = leitor.readLine();
            if (linha == null) {
                return;
            }
            while ((linha = leitor.readLine()) != null) {
                if (linha.trim().length() == 0) {
                    continue;
                }
                // 11 colunas = formato de antes de 2026-07-23 (sem as 4 colunas
                // de invariante) — continua carregando normalmente, só sem esses
                // campos, igual ao mesmo tipo de compatibilidade já feita para
                // perfis_usuario.tsv (ver carregarDoArquivo()). Sem isso, todas
                // as linhas gravadas antes da mudança de esquema eram
                // descartadas como "corrompidas" ao carregar — bug encontrado
                // pelo usuário em 2026-07-23 (explicação salva, mas a curadoria
                // aparecia vazia).
                String[] campos = linha.split("\t", -1);
                if (campos.length < 11) {
                    continue;
                }
                String usuarioId = campos[0];
                DiagnosticoTarefa diagnostico = new DiagnosticoTarefa(campos[1]);
                diagnostico.setRegraDeAcao(campos[2].length() == 0 ? null : campos[2]);
                diagnostico.setSuporte(campos[3].length() == 0 ? null : NivelSuporte.valueOf(campos[3]));
                diagnostico.setInternalizado("true".equals(campos[4]));
                diagnostico.setProbabilidadeSaberConteudo(
                        campos[5].length() == 0 ? 0.0 : Double.parseDouble(campos[5]));
                diagnostico.setDificuldadeAutorrelatada(campos[6].length() == 0 ? null : desescapar(campos[6]));
                diagnostico.setExplicacaoElemento(campos[7].length() == 0 ? null : desescapar(campos[7]));
                diagnostico.setExplicacaoGeral(campos[8].length() == 0 ? null : desescapar(campos[8]));
                diagnostico.setNivelConceitualEstimado(
                        campos[9].length() == 0 ? null : NivelConceitualExplicacao.valueOf(campos[9]));
                diagnostico.setNivelConceitualCurado(
                        campos[10].length() == 0 ? null : NivelConceitualExplicacao.valueOf(campos[10]));
                if (campos.length >= 15) {
                    diagnostico.setInvarianteOrigem(campos[11].length() == 0 ? null : desescapar(campos[11]));
                    diagnostico.setInvarianteCodigo(campos[12].length() == 0 ? null : desescapar(campos[12]));
                    diagnostico.setInvarianteSimbolico(campos[13].length() == 0 ? null : desescapar(campos[13]));
                    diagnostico.setInvarianteObservacao(campos[14].length() == 0 ? null : desescapar(campos[14]));
                }
                obterOuCriar(usuarioId).adicionarDiagnostico(diagnostico);
            }
        } catch (IOException | IllegalArgumentException ex) {
            // Diagnósticos corrompidos/ilegíveis não devem impedir o app de
            // abrir; segue com o que já tiver sido lido até aqui.
        } finally {
            fechar(leitor);
        }
    }

    /**
     * Regrava o arquivo inteiro de diagnósticos a partir do estado atual em
     * memória — mesmo padrão de salvarNoArquivo() para perfis. Público (ao
     * contrário de salvarNoArquivo()) porque quem muda um diagnóstico é
     * AgenteModelador ou a curadoria da Visão de Pesquisador, não este
     * repositório sozinho; chamar depois de qualquer alteração num
     * DiagnosticoTarefa (novo caso, autorrelato, curadoria de nível).
     */
    public synchronized void salvarDiagnosticos() {
        File pai = arquivoDiagnosticos.getParentFile();
        if (pai != null && !pai.exists()) {
            pai.mkdirs();
        }
        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoDiagnosticos), "UTF-8"));
            escritor.write(CABECALHO_DIAGNOSTICOS);
            escritor.newLine();
            for (ModeloUsuario modelo : modelosPorId.values()) {
                String usuarioId = modelo.getPerfilAluno().getId();
                for (DiagnosticoTarefa diagnostico : modelo.getDiagnosticos()) {
                    escritor.write(usuarioId);
                    escritor.write("\t");
                    escritor.write(diagnostico.getTarefa() == null ? "" : diagnostico.getTarefa());
                    escritor.write("\t");
                    escritor.write(diagnostico.getRegraDeAcao() == null ? "" : diagnostico.getRegraDeAcao());
                    escritor.write("\t");
                    escritor.write(diagnostico.getSuporte() == null ? "" : diagnostico.getSuporte().name());
                    escritor.write("\t");
                    escritor.write(String.valueOf(diagnostico.isInternalizado()));
                    escritor.write("\t");
                    escritor.write(String.valueOf(diagnostico.getProbabilidadeSaberConteudo()));
                    escritor.write("\t");
                    escritor.write(escapar(diagnostico.getDificuldadeAutorrelatada()));
                    escritor.write("\t");
                    escritor.write(escapar(diagnostico.getExplicacaoElemento()));
                    escritor.write("\t");
                    escritor.write(escapar(diagnostico.getExplicacaoGeral()));
                    escritor.write("\t");
                    escritor.write(diagnostico.getNivelConceitualEstimado() == null
                            ? "" : diagnostico.getNivelConceitualEstimado().name());
                    escritor.write("\t");
                    escritor.write(diagnostico.getNivelConceitualCurado() == null
                            ? "" : diagnostico.getNivelConceitualCurado().name());
                    escritor.write("\t");
                    escritor.write(escapar(diagnostico.getInvarianteOrigem()));
                    escritor.write("\t");
                    escritor.write(escapar(diagnostico.getInvarianteCodigo()));
                    escritor.write("\t");
                    escritor.write(escapar(diagnostico.getInvarianteSimbolico()));
                    escritor.write("\t");
                    escritor.write(escapar(diagnostico.getInvarianteObservacao()));
                    escritor.newLine();
                }
            }
        } catch (IOException ex) {
            // Falha ao persistir não deve derrubar a UI; os diagnósticos
            // seguem válidos em memória pelo resto da sessão.
        } finally {
            fechar(escritor);
        }
    }

    /** Campos de texto livre (explicações) podem conter tabs/quebras de linha — precisam de escape no TSV. */
    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
    }

    private String desescapar(String valor) {
        if (valor == null || valor.length() == 0) {
            return valor;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char atual = valor.charAt(i);
            if (atual == '\\' && i + 1 < valor.length()) {
                char proximo = valor.charAt(i + 1);
                if (proximo == 't') { sb.append('\t'); i++; continue; }
                if (proximo == 'r') { sb.append('\r'); i++; continue; }
                if (proximo == 'n') { sb.append('\n'); i++; continue; }
                if (proximo == '\\') { sb.append('\\'); i++; continue; }
            }
            sb.append(atual);
        }
        return sb.toString();
    }

    private void fechar(java.io.Closeable recurso) {
        if (recurso != null) {
            try {
                recurso.close();
            } catch (IOException ignorada) {
                // nada a fazer
            }
        }
    }
}
