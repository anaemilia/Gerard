package gerard.adaptacao.modelousuario;

import gerard.adaptacao.ContextoAdaptativoUsuario;
import gerard.adaptacao.EscopoProprietarioSemantico;
import gerard.adaptacao.RegraAdaptativaPublicada;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelComplexidadeTarefa;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Fotografia multidimensional estável de uma versão do Modelo do Usuário.
 * É criada na fronteira da sessão e nunca acompanha mutações posteriores do
 * repositório ou das regras candidatas.
 */
public final class FotografiaModeloUsuario {

    private final String versaoModelo;
    private final String usuarioId;
    private final ValorProjetado<NiveisTarefasProjetados> niveisTarefas;
    private final ValorProjetado<DominioCategoriasProjetado> dominioCategorias;
    private final ValorProjetado<PerfilAlunoProjetado> perfilAluno;
    private final ValorProjetado<PerfilAprendizagemProjetado> perfilAprendizagem;
    private final ValorProjetado<HistoricoDiagnosticosProjetado> diagnosticosTarefa;
    private final List<RegraAdaptativaPublicada> regrasPublicadas;
    private final int quantidadeRegrasNaoPublicadasIgnoradas;

    public FotografiaModeloUsuario(
            String versaoModelo,
            ModeloUsuario modelo,
            List<RegraAdaptativaPublicada> regrasCandidatas) {
        this(versaoModelo, modelo, regrasCandidatas, false);
    }

    /**
     * Cria a fotografia na fronteira de login e identifica exatamente o
     * conteúdo imutável carregado. O identificador não publica regras: regras
     * não publicadas continuam excluídas pelo mesmo filtro do construtor.
     */
    public static FotografiaModeloUsuario carregarNoLogin(
            ModeloUsuario modelo,
            List<RegraAdaptativaPublicada> regrasCandidatas) {
        return new FotografiaModeloUsuario(null, modelo, regrasCandidatas, true);
    }

    private FotografiaModeloUsuario(
            String versaoInformada,
            ModeloUsuario modelo,
            List<RegraAdaptativaPublicada> regrasCandidatas,
            boolean versionarPeloConteudo) {
        if (modelo == null) {
            throw new IllegalArgumentException("modelo do usuário não pode ser nulo");
        }
        this.usuarioId = textoObrigatorio(modelo.getPerfilAluno().getId(), "id do usuário");
        this.niveisTarefas = fotografarNiveisTarefa(modelo);
        this.dominioCategorias = fotografarDominios(modelo);
        this.perfilAluno = ValorProjetado.presente(
                new PerfilAlunoProjetado(modelo.getPerfilAluno()));
        this.perfilAprendizagem = fotografarPerfilAprendizagem(modelo);
        this.diagnosticosTarefa = modelo.getDiagnosticos().isEmpty()
                ? ValorProjetado.<HistoricoDiagnosticosProjetado>ausente(
                        "nenhum diagnóstico de tarefa registrado")
                : ValorProjetado.presente(
                        new HistoricoDiagnosticosProjetado(modelo.getDiagnosticos()));

        ResultadoRegras resultadoRegras = copiarSomenteRegrasPublicadas(regrasCandidatas);
        this.regrasPublicadas = resultadoRegras.publicadas;
        this.quantidadeRegrasNaoPublicadasIgnoradas = resultadoRegras.ignoradas;
        this.versaoModelo = versionarPeloConteudo
                ? gerarVersaoConteudo()
                : textoObrigatorio(versaoInformada, "versão do modelo");
    }

    public String getVersaoModelo() { return versaoModelo; }
    public String getUsuarioId() { return usuarioId; }
    public ValorProjetado<NiveisTarefasProjetados> getNiveisTarefas() {
        return niveisTarefas;
    }
    public ValorProjetado<DominioCategoriasProjetado> getDominioCategorias() {
        return dominioCategorias;
    }
    public ValorProjetado<PerfilAlunoProjetado> getPerfilAluno() { return perfilAluno; }
    public ValorProjetado<PerfilAprendizagemProjetado> getPerfilAprendizagem() {
        return perfilAprendizagem;
    }
    public ValorProjetado<HistoricoDiagnosticosProjetado> getDiagnosticosTarefa() {
        return diagnosticosTarefa;
    }
    public List<RegraAdaptativaPublicada> getRegrasPublicadas() {
        return regrasPublicadas;
    }
    public int getQuantidadeRegrasNaoPublicadasIgnoradas() {
        return quantidadeRegrasNaoPublicadasIgnoradas;
    }

    public ProjecaoModeloUsuario projetar(Set<DimensaoModeloUsuario> dimensoes) {
        EnumSet<DimensaoModeloUsuario> solicitadas = copiarDimensoes(dimensoes);
        return new ProjecaoModeloUsuario(
                versaoModelo,
                usuarioId,
                solicitadas,
                selecionar(DimensaoModeloUsuario.NIVEL_TAREFAS, solicitadas, niveisTarefas),
                selecionar(DimensaoModeloUsuario.PARTES_CONHECIMENTO_E_FASES,
                        solicitadas, dominioCategorias),
                selecionar(DimensaoModeloUsuario.PERFIL_ALUNO, solicitadas, perfilAluno),
                selecionar(DimensaoModeloUsuario.PERFIL_APRENDIZAGEM,
                        solicitadas, perfilAprendizagem),
                selecionar(DimensaoModeloUsuario.DIAGNOSTICO_TAREFA,
                        solicitadas, diagnosticosTarefa));
    }

    public ContextoAdaptativoUsuario projetarContextoAdaptativo(
            String proprietarioSemantico,
            EscopoProprietarioSemantico escopo,
            Set<DimensaoModeloUsuario> dimensoes) {
        ProjecaoModeloUsuario projecao = projetar(dimensoes);
        List<RegraAdaptativaPublicada> regrasDoProprietario =
                new ArrayList<RegraAdaptativaPublicada>();
        for (RegraAdaptativaPublicada regra : regrasPublicadas) {
            if (regra.getProprietarioSemantico().equals(proprietarioSemantico)
                    && regra.getEscopo().equals(escopo)) {
                regrasDoProprietario.add(regra);
            }
        }
        return new ContextoAdaptativoUsuario(
                proprietarioSemantico, escopo, projecao, regrasDoProprietario);
    }

    public ProjecaoPreferenciasUsuario projetarPreferencias() {
        return new ProjecaoPreferenciasUsuario(
                versaoModelo, usuarioId, perfilAluno, perfilAprendizagem);
    }

    private static ValorProjetado<NiveisTarefasProjetados> fotografarNiveisTarefa(
            ModeloUsuario modelo) {
        Map<TipoSituacaoAditiva, NivelComplexidadeTarefa> conhecidos =
                new EnumMap<TipoSituacaoAditiva, NivelComplexidadeTarefa>(
                        TipoSituacaoAditiva.class);
        for (TipoSituacaoAditiva categoria : TipoSituacaoAditiva.values()) {
            NivelComplexidadeTarefa nivel = modelo.getNivelTarefa(categoria);
            if (nivel != null) conhecidos.put(categoria, nivel);
        }
        return conhecidos.isEmpty()
                ? ValorProjetado.<NiveisTarefasProjetados>ausente(
                        "nenhum nível de tarefa registrado por categoria")
                : ValorProjetado.presente(new NiveisTarefasProjetados(conhecidos));
    }

    private static ValorProjetado<DominioCategoriasProjetado> fotografarDominios(
            ModeloUsuario modelo) {
        if (modelo.getCategoriaMaiorDominio() == null
                && modelo.getCategoriaMenorDominio() == null) {
            return ValorProjetado.ausente(
                    "categorias de maior e menor domínio não registradas");
        }
        return ValorProjetado.presente(new DominioCategoriasProjetado(
                modelo.getCategoriaMaiorDominio(), modelo.getCategoriaMenorDominio()));
    }

    private static ValorProjetado<PerfilAprendizagemProjetado>
            fotografarPerfilAprendizagem(ModeloUsuario modelo) {
        if (modelo.getPerfilAprendizagem().getMidiaPreferida() == null
                && modelo.getPerfilAprendizagem().getNivelEscolaridade() == null) {
            return ValorProjetado.ausente(
                    "preferências de aprendizagem não registradas");
        }
        return ValorProjetado.presente(
                new PerfilAprendizagemProjetado(modelo.getPerfilAprendizagem()));
    }

    private static ResultadoRegras copiarSomenteRegrasPublicadas(
            List<RegraAdaptativaPublicada> origem) {
        if (origem == null) {
            throw new IllegalArgumentException("regras candidatas não podem ser nulas");
        }
        List<RegraAdaptativaPublicada> publicadas =
                new ArrayList<RegraAdaptativaPublicada>();
        Map<String, Boolean> identidades = new LinkedHashMap<String, Boolean>();
        int ignoradas = 0;
        for (RegraAdaptativaPublicada regra : origem) {
            if (regra == null) {
                throw new IllegalArgumentException("regra candidata não pode ser nula");
            }
            if (!regra.estaPublicada()) {
                ignoradas++;
                continue;
            }
            String identidade = regra.getProprietarioSemantico() + "|"
                    + regra.getEscopo() + "|" + regra.getId() + "@" + regra.getVersao();
            if (identidades.put(identidade, Boolean.TRUE) != null) {
                throw new IllegalArgumentException(
                        "regra publicada duplicada na fotografia: " + identidade);
            }
            publicadas.add(regra);
        }
        return new ResultadoRegras(
                Collections.unmodifiableList(publicadas), ignoradas);
    }

    private static EnumSet<DimensaoModeloUsuario> copiarDimensoes(
            Set<DimensaoModeloUsuario> dimensoes) {
        if (dimensoes == null || dimensoes.isEmpty()) {
            throw new IllegalArgumentException("projeção deve solicitar ao menos uma dimensão");
        }
        EnumSet<DimensaoModeloUsuario> copia =
                EnumSet.noneOf(DimensaoModeloUsuario.class);
        for (DimensaoModeloUsuario dimensao : dimensoes) {
            if (dimensao == null) {
                throw new IllegalArgumentException("dimensão solicitada não pode ser nula");
            }
            copia.add(dimensao);
        }
        return copia;
    }

    private static <T> ValorProjetado<T> selecionar(
            DimensaoModeloUsuario dimensao,
            Set<DimensaoModeloUsuario> solicitadas,
            ValorProjetado<T> valor) {
        return solicitadas.contains(dimensao) ? valor : ValorProjetado.<T>naoSolicitado();
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }

    private String gerarVersaoConteudo() {
        StringBuilder conteudo = new StringBuilder();
        adicionar(conteudo, "usuario_id", usuarioId);
        adicionarNiveisTarefas(conteudo);
        adicionarDominios(conteudo);
        adicionarPerfilAluno(conteudo);
        adicionarPerfilAprendizagem(conteudo);
        adicionarDiagnosticos(conteudo);
        adicionarRegrasPublicadas(conteudo);
        adicionar(conteudo, "regras_nao_publicadas_ignoradas",
                Integer.valueOf(quantidadeRegrasNaoPublicadasIgnoradas));
        return "conteudo-sha256:" + sha256(conteudo.toString());
    }

    private void adicionarNiveisTarefas(StringBuilder destino) {
        adicionar(destino, "niveis_tarefas.estado", niveisTarefas.getEstado());
        if (!niveisTarefas.estaPresente()) return;
        NiveisTarefasProjetados niveis = niveisTarefas.getValor().get();
        for (TipoSituacaoAditiva categoria : TipoSituacaoAditiva.values()) {
            adicionar(destino, "nivel_tarefa." + categoria.name(),
                    niveis.obter(categoria).orElse(null));
        }
    }

    private void adicionarDominios(StringBuilder destino) {
        adicionar(destino, "dominio_categorias.estado", dominioCategorias.getEstado());
        if (!dominioCategorias.estaPresente()) return;
        DominioCategoriasProjetado dominios = dominioCategorias.getValor().get();
        adicionar(destino, "categoria_maior_dominio",
                dominios.getMaiorDominio().orElse(null));
        adicionar(destino, "categoria_menor_dominio",
                dominios.getMenorDominio().orElse(null));
    }

    private void adicionarPerfilAluno(StringBuilder destino) {
        adicionar(destino, "perfil_aluno.estado", perfilAluno.getEstado());
        if (!perfilAluno.estaPresente()) return;
        PerfilAlunoProjetado perfil = perfilAluno.getValor().get();
        adicionar(destino, "perfil_aluno.id", perfil.getId());
        adicionar(destino, "perfil_aluno.nome", perfil.getNome().orElse(null));
        adicionar(destino, "perfil_aluno.idade", perfil.getIdade().orElse(null));
        adicionar(destino, "perfil_aluno.sexo", perfil.getSexo().orElse(null));
        adicionar(destino, "perfil_aluno.foto", perfil.getFotoCaminho().orElse(null));
    }

    private void adicionarPerfilAprendizagem(StringBuilder destino) {
        adicionar(destino, "perfil_aprendizagem.estado", perfilAprendizagem.getEstado());
        if (!perfilAprendizagem.estaPresente()) return;
        PerfilAprendizagemProjetado perfil = perfilAprendizagem.getValor().get();
        adicionar(destino, "perfil_aprendizagem.midia",
                perfil.getMidiaPreferida().orElse(null));
        adicionar(destino, "perfil_aprendizagem.escolaridade",
                perfil.getNivelEscolaridade().orElse(null));
    }

    private void adicionarDiagnosticos(StringBuilder destino) {
        adicionar(destino, "diagnosticos.estado", diagnosticosTarefa.getEstado());
        if (!diagnosticosTarefa.estaPresente()) return;
        List<DiagnosticoTarefaProjetado> diagnosticos =
                diagnosticosTarefa.getValor().get().getDiagnosticos();
        adicionar(destino, "diagnosticos.quantidade", Integer.valueOf(diagnosticos.size()));
        for (int i = 0; i < diagnosticos.size(); i++) {
            DiagnosticoTarefaProjetado diagnostico = diagnosticos.get(i);
            String prefixo = "diagnostico." + i + ".";
            adicionarValorProjetado(destino, prefixo + "tarefa", diagnostico.getTarefa());
            adicionarValorProjetado(destino, prefixo + "regra_de_acao",
                    diagnostico.getRegraDeAcao());
            adicionarValorProjetado(destino, prefixo + "suporte", diagnostico.getSuporte());
            adicionarValorProjetado(destino, prefixo + "internalizado",
                    diagnostico.getInternalizado());
            adicionarValorProjetado(destino, prefixo + "probabilidade",
                    diagnostico.getProbabilidadeSaberConteudo());
        }
    }

    private void adicionarRegrasPublicadas(StringBuilder destino) {
        List<RegraAdaptativaPublicada> ordenadas =
                new ArrayList<RegraAdaptativaPublicada>(regrasPublicadas);
        Collections.sort(ordenadas, new Comparator<RegraAdaptativaPublicada>() {
            public int compare(RegraAdaptativaPublicada a, RegraAdaptativaPublicada b) {
                return identidadeRegra(a).compareTo(identidadeRegra(b));
            }
        });
        adicionar(destino, "regras_publicadas.quantidade", Integer.valueOf(ordenadas.size()));
        for (int i = 0; i < ordenadas.size(); i++) {
            RegraAdaptativaPublicada regra = ordenadas.get(i);
            String prefixo = "regra." + i + ".";
            adicionar(destino, prefixo + "id", regra.getId());
            adicionar(destino, prefixo + "versao", regra.getVersao());
            adicionar(destino, prefixo + "algoritmo", regra.getAlgoritmoOrigem());
            adicionar(destino, prefixo + "publicada_em", regra.getPublicadaEm());
            adicionar(destino, prefixo + "proveniencia", regra.getProvenienciaCasos());
            adicionar(destino, prefixo + "proprietario", regra.getProprietarioSemantico());
            adicionar(destino, prefixo + "escopo", regra.getEscopo());
            for (Map.Entry<String, String> condicao
                    : new TreeMap<String, String>(regra.getCondicoes()).entrySet()) {
                adicionar(destino, prefixo + "condicao." + condicao.getKey(),
                        condicao.getValue());
            }
            adicionar(destino, prefixo + "ajuda", regra.getCodigoAjudaRecomendada());
            adicionar(destino, prefixo + "suporte", regra.getSuporte());
            adicionar(destino, prefixo + "confianca", regra.getConfianca());
            adicionar(destino, prefixo + "lift", regra.getLift());
            adicionar(destino, prefixo + "estado", regra.getEstado());
        }
    }

    private static <T> void adicionarValorProjetado(
            StringBuilder destino,
            String chave,
            ValorProjetado<T> valor) {
        adicionar(destino, chave + ".estado", valor.getEstado());
        adicionar(destino, chave + ".valor", valor.getValor().orElse(null));
    }

    private static String identidadeRegra(RegraAdaptativaPublicada regra) {
        return regra.getProprietarioSemantico() + "|" + regra.getEscopo()
                + "|" + regra.getId() + "@" + regra.getVersao();
    }

    private static void adicionar(StringBuilder destino, String chave, Object valor) {
        String texto = valor == null ? "<ausente>" : String.valueOf(valor);
        destino.append(chave.length()).append(':').append(chave)
                .append('=').append(texto.length()).append(':').append(texto).append(';');
    }

    private static String sha256(String conteudo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(conteudo.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder(bytes.length * 2);
            for (byte atual : bytes) {
                hexadecimal.append(String.format("%02x", Integer.valueOf(atual & 0xff)));
            }
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException impossivelNaJre) {
            throw new IllegalStateException("SHA-256 indisponível na JRE", impossivelNaJre);
        }
    }

    private static final class ResultadoRegras {
        private final List<RegraAdaptativaPublicada> publicadas;
        private final int ignoradas;

        private ResultadoRegras(List<RegraAdaptativaPublicada> publicadas, int ignoradas) {
            this.publicadas = publicadas;
            this.ignoradas = ignoradas;
        }
    }
}
