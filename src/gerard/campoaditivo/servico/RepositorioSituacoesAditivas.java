package gerard.campoaditivo.servico;

import gerard.interpretacao.simbolo.SimboloDesconhecido;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.idioma.CadastroIdiomasSituacao;
import gerard.idioma.IdiomaSituacao;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RepositorioSituacoesAditivas {
    private static final String ARQUIVO_SITUACOES = "/gerard/campoaditivo/dados/situacoes_vergnaud.tsv";
    public static final String CABECALHO_CURADORIA = "# id\tsituacao_grupo_id\ttipo_versao\tversao_origem_id\tvalidada\tidioma\ttipo\tcontexto\tenunciado\tfonte\tsubtipo\testado_inicial\ttransformacao\tsinal_transformacao\testado_final\tquantidade_1\tquantidade_2\tresultado\treferido\treferendo\tvalor_relativo\tsinal_valor_relativo\ttermo_desconhecido\trepresentacao_visual\tobservacoes\tpersonagem_1\tpersonagem_2\tpersonagem_3\tfragmento_texto_1\tfragmento_texto_2\tfragmento_texto_3\tfragmento_texto_4\tfragmento_texto_5\tfragmento_texto_6";

    private final Map<IdiomaInterface, Map<TipoSituacaoAditiva, List<SituacaoProblemaAditiva>>> situacoes;
    private final Random random;
    private final List<SituacaoProblemaAditiva> todasSituacoes;

    public RepositorioSituacoesAditivas() {
        situacoes = new EnumMap<IdiomaInterface, Map<TipoSituacaoAditiva, List<SituacaoProblemaAditiva>>>(IdiomaInterface.class);
        random = new Random();
        todasSituacoes = new ArrayList<SituacaoProblemaAditiva>();
        carregar();
    }

    public final void recarregar() {
        situacoes.clear();
        todasSituacoes.clear();
        carregar();
    }

    private void carregar() {
        boolean carregouArquivo = carregarArquivoSituacoes();

        if (!carregouArquivo) {
            carregarFallbackMinimo();
        }
    }

    private boolean carregarArquivoSituacoes() {
        InputStream input = abrirArquivoSituacoes();

        if (input == null) {
            return false;
        }

        int carregadas = 0;
        Set<String> idsGerados = new LinkedHashSet<String>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String linha;
            int indice = 1;

            while ((linha = br.readLine()) != null) {
                String linhaOriginal = linha;
                linha = linha.trim();

                if (linha.length() == 0 || linha.startsWith("#")) {
                    continue;
                }

                SituacaoProblemaAditiva situacao = parseLinhaSituacao(linhaOriginal, indice, idsGerados);
                indice++;
                if (situacao == null || situacao.getEnunciado().trim().length() == 0) {
                    continue;
                }
                adicionar(situacao);
                carregadas++;
            }
        } catch (IOException ex) {
            return false;
        }

        if (carregadas > 0) {
            migrarVinculosLegadosSeNecessario();
        }
        return carregadas > 0;
    }

    private void migrarVinculosLegadosSeNecessario() {
        boolean possuiVinculoExplicito = false;
        for (SituacaoProblemaAditiva s : todasSituacoes) {
            if ((s.getSituacaoGrupoId() != null && !s.getSituacaoGrupoId().equals(s.getId()))
                    || (s.getVersaoOrigemId() != null && s.getVersaoOrigemId().trim().length() > 0)) {
                possuiVinculoExplicito = true;
                break;
            }
        }
        if (possuiVinculoExplicito) {
            return;
        }

        List<SituacaoProblemaAditiva> pt = filtrarPorIdioma(IdiomaInterface.PORTUGUES);
        List<SituacaoProblemaAditiva> en = filtrarPorIdioma(IdiomaInterface.INGLES);
        List<SituacaoProblemaAditiva> fr = filtrarPorIdioma(IdiomaInterface.FRANCES);
        if (pt.isEmpty() || (en.isEmpty() && fr.isEmpty())) {
            return;
        }

        int deslocamentoEn = Math.max(0, en.size() - pt.size());
        int deslocamentoFr = Math.max(0, fr.size() - pt.size());
        java.util.Map<String, SituacaoProblemaAditiva> substituicoes = new java.util.HashMap<String, SituacaoProblemaAditiva>();

        int extras = Math.max(deslocamentoEn, deslocamentoFr);
        for (int i = 0; i < extras; i++) {
            String grupo = "SP_LEGADO_EXTRA_" + String.format(java.util.Locale.ROOT, "%04d", i + 1);
            if (i < deslocamentoEn) {
                SituacaoProblemaAditiva original = en.get(i);
                substituicoes.put(original.getId(), copiarComVinculo(original, grupo, "original", ""));
            }
            if (i < deslocamentoFr) {
                SituacaoProblemaAditiva traducao = fr.get(i);
                String origem = i < deslocamentoEn ? en.get(i).getId() : "";
                substituicoes.put(traducao.getId(), copiarComVinculo(traducao, grupo, origem.length() == 0 ? "original" : "traducao", origem));
            }
        }

        for (int i = 0; i < pt.size(); i++) {
            String grupo = "SP_LEGADO_" + String.format(java.util.Locale.ROOT, "%04d", i + 1);
            SituacaoProblemaAditiva original = pt.get(i);
            substituicoes.put(original.getId(), copiarComVinculo(original, grupo, "original", ""));
            int indiceEn = i + deslocamentoEn;
            if (indiceEn < en.size() && en.get(indiceEn).getTipo() == original.getTipo()) {
                SituacaoProblemaAditiva traducao = en.get(indiceEn);
                substituicoes.put(traducao.getId(), copiarComVinculo(traducao, grupo, "traducao", original.getId()));
            }
            int indiceFr = i + deslocamentoFr;
            if (indiceFr < fr.size() && fr.get(indiceFr).getTipo() == original.getTipo()) {
                SituacaoProblemaAditiva traducao = fr.get(indiceFr);
                substituicoes.put(traducao.getId(), copiarComVinculo(traducao, grupo, "traducao", original.getId()));
            }
        }

        if (!substituicoes.isEmpty()) {
            List<SituacaoProblemaAditiva> antigas = new ArrayList<SituacaoProblemaAditiva>(todasSituacoes);
            situacoes.clear();
            todasSituacoes.clear();
            for (SituacaoProblemaAditiva antiga : antigas) {
                SituacaoProblemaAditiva vinculada = substituicoes.get(antiga.getId());
                adicionar(vinculada == null ? antiga : vinculada);
            }
        }
    }

    private List<SituacaoProblemaAditiva> filtrarPorIdioma(IdiomaInterface idioma) {
        List<SituacaoProblemaAditiva> resultado = new ArrayList<SituacaoProblemaAditiva>();
        for (SituacaoProblemaAditiva s : todasSituacoes) {
            if (s.getIdioma() == idioma) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    private SituacaoProblemaAditiva copiarComVinculo(SituacaoProblemaAditiva s, String grupo, String tipoVersao, String origem) {
        return new SituacaoProblemaAditiva(s.getId(), grupo, tipoVersao, origem, s.isValidada(), s.getTipo(), s.getCodigoIdioma(),
                s.getEnunciado(), s.getContexto(), s.getFonte(), s.getSubtipo(), s.getEstadoInicial(), s.getTransformacao(),
                s.getSinalTransformacao(), s.getEstadoFinal(), s.getQuantidade1(), s.getQuantidade2(), s.getResultado(),
                s.getReferido(), s.getReferendo(), s.getValorRelativo(), s.getSinalValorRelativo(), s.getTermoDesconhecido(),
                s.getRepresentacaoVisual(), s.getObservacoes(), s.getPersonagem1(), s.getPersonagem2(), s.getPersonagem3(),
                s.getFragmentoTexto1(), s.getFragmentoTexto2(), s.getFragmentoTexto3(),
                s.getFragmentoTexto4(), s.getFragmentoTexto5(), s.getFragmentoTexto6());
    }

    private SituacaoProblemaAditiva parseLinhaSituacao(String linha, int indice, Set<String> idsGerados) {
        String[] partes = linha.split("\\t", -1);
        try {
            if (partes.length >= 25) {
                String id = valor(partes, 0);
                String situacaoGrupoId = valor(partes, 1);
                String tipoVersao = valor(partes, 2);
                String versaoOrigemId = valor(partes, 3);
                boolean validada = Boolean.parseBoolean(valor(partes, 4));
                String codigoIdioma = converterCodigoIdiomaLegado(valor(partes, 5));
                if (!CadastroIdiomasSituacao.ehIdiomaPermitido(codigoIdioma)) return null;
                IdiomaInterface idioma = IdiomaSituacao.paraIdiomaInterface(codigoIdioma);
                TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(valor(partes, 6));
                String contexto = valor(partes, 7);
                String enunciado = valor(partes, 8);
                String fonte = valor(partes, 9);
                String subtipo = valor(partes, 10);
                String estadoInicial = valor(partes, 11);
                String transformacao = valor(partes, 12);
                String sinalTransformacao = valor(partes, 13);
                String estadoFinal = valor(partes, 14);
                String quantidade1 = valor(partes, 15);
                String quantidade2 = valor(partes, 16);
                String resultado = valor(partes, 17);
                String referido = valor(partes, 18);
                String referendo = valor(partes, 19);
                String valorRelativo = valor(partes, 20);
                String sinalValorRelativo = valor(partes, 21);
                String termoDesconhecido = valor(partes, 22);
                String representacaoVisual = valor(partes, 23);
                String observacoes = valor(partes, 24);
                String personagem1 = partes.length > 25 ? valor(partes, 25) : "";
                String personagem2 = partes.length > 26 ? valor(partes, 26) : "";
                String personagem3 = partes.length > 27 ? valor(partes, 27) : "";
                String fragmentoTexto1 = partes.length > 28 ? valor(partes, 28) : "";
                String fragmentoTexto2 = partes.length > 29 ? valor(partes, 29) : "";
                String fragmentoTexto3 = partes.length > 30 ? valor(partes, 30) : "";
                String fragmentoTexto4 = partes.length > 31 ? valor(partes, 31) : "";
                String fragmentoTexto5 = partes.length > 32 ? valor(partes, 32) : "";
                String fragmentoTexto6 = partes.length > 33 ? valor(partes, 33) : "";
                id = garantirId(id, idioma == null ? IdiomaInterface.PORTUGUES : idioma, tipo, contexto, enunciado, indice, idsGerados);
                if (situacaoGrupoId.length() == 0) situacaoGrupoId = id;
                if (tipoVersao.length() == 0) tipoVersao = "original";
                return new SituacaoProblemaAditiva(id, situacaoGrupoId, tipoVersao, versaoOrigemId, validada, tipo, codigoIdioma, enunciado, contexto, fonte, subtipo,
                        estadoInicial, transformacao, sinalTransformacao, estadoFinal, quantidade1, quantidade2, resultado,
                        referido, referendo, valorRelativo, sinalValorRelativo, termoDesconhecido, representacaoVisual, observacoes,
                        personagem1, personagem2, personagem3,
                        fragmentoTexto1, fragmentoTexto2, fragmentoTexto3, fragmentoTexto4, fragmentoTexto5, fragmentoTexto6);
            }

            if (partes.length >= 22) {
                String id = valor(partes, 0);
                boolean validada = Boolean.parseBoolean(valor(partes, 1));
                IdiomaInterface idioma = IdiomaInterface.valueOf(valor(partes, 2));
                TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(valor(partes, 3));
                String contexto = valor(partes, 4);
                String enunciado = valor(partes, 5);
                String fonte = valor(partes, 6);
                String subtipo = valor(partes, 7);
                String estadoInicial = valor(partes, 8);
                String transformacao = valor(partes, 9);
                String sinalTransformacao = valor(partes, 10);
                String estadoFinal = valor(partes, 11);
                String quantidade1 = valor(partes, 12);
                String quantidade2 = valor(partes, 13);
                String resultado = valor(partes, 14);
                String referido = valor(partes, 15);
                String referendo = valor(partes, 16);
                String valorRelativo = valor(partes, 17);
                String sinalValorRelativo = valor(partes, 18);
                String termoDesconhecido = valor(partes, 19);
                String representacaoVisual = valor(partes, 20);
                String observacoes = valor(partes, 21);
                id = garantirId(id, idioma, tipo, contexto, enunciado, indice, idsGerados);
                return new SituacaoProblemaAditiva(id, validada, tipo, idioma, enunciado, contexto, fonte, subtipo,
                        estadoInicial, transformacao, sinalTransformacao, estadoFinal, quantidade1, quantidade2, resultado,
                        referido, referendo, valorRelativo, sinalValorRelativo, termoDesconhecido, representacaoVisual, observacoes);
            }

            if (partes.length >= 20) {
                String id = valor(partes, 0);
                boolean validada = Boolean.parseBoolean(valor(partes, 1));
                IdiomaInterface idioma = IdiomaInterface.valueOf(valor(partes, 2));
                TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(valor(partes, 3));
                String contexto = valor(partes, 4);
                String enunciado = valor(partes, 5);
                String fonte = valor(partes, 6);
                String subtipo = valor(partes, 7);
                String estadoInicial = valor(partes, 8);
                String transformacao = valor(partes, 9);
                String estadoFinal = valor(partes, 10);
                String quantidade1 = valor(partes, 11);
                String quantidade2 = valor(partes, 12);
                String resultado = valor(partes, 13);
                String referido = valor(partes, 14);
                String referendo = valor(partes, 15);
                String valorRelativo = valor(partes, 16);
                String termoDesconhecido = valor(partes, 17);
                String representacaoVisual = valor(partes, 18);
                String observacoes = valor(partes, 19);
                id = garantirId(id, idioma, tipo, contexto, enunciado, indice, idsGerados);
                return new SituacaoProblemaAditiva(id, validada, tipo, idioma, enunciado, contexto, fonte, subtipo,
                        estadoInicial, transformacao, estadoFinal, quantidade1, quantidade2, resultado,
                        referido, referendo, valorRelativo, termoDesconhecido, representacaoVisual, observacoes);
            }

            if (partes.length >= 17) {
                String id = valor(partes, 0);
                boolean validada = Boolean.parseBoolean(valor(partes, 1));
                IdiomaInterface idioma = IdiomaInterface.valueOf(valor(partes, 2));
                TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(valor(partes, 3));
                String contexto = valor(partes, 4);
                String enunciado = valor(partes, 5);
                String fonte = valor(partes, 6);
                String subtipo = valor(partes, 7);
                String estadoInicial = valor(partes, 8);
                String transformacao = valor(partes, 9);
                String estadoFinal = valor(partes, 10);
                String quantidade1 = valor(partes, 11);
                String quantidade2 = valor(partes, 12);
                String resultado = valor(partes, 13);
                String termoDesconhecido = valor(partes, 14);
                String representacaoVisual = valor(partes, 15);
                String observacoes = valor(partes, 16);
                id = garantirId(id, idioma, tipo, contexto, enunciado, indice, idsGerados);
                return new SituacaoProblemaAditiva(id, validada, tipo, idioma, enunciado, contexto, fonte, subtipo,
                        estadoInicial, transformacao, estadoFinal, quantidade1, quantidade2, resultado,
                        termoDesconhecido, representacaoVisual, observacoes);
            }

            if (partes.length >= 4) {
                IdiomaInterface idioma = IdiomaInterface.valueOf(valor(partes, 0));
                TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(valor(partes, 1));
                String contexto = valor(partes, 2);
                String enunciado = valor(partes, 3);
                String fonte = partes.length > 4 ? valor(partes, 4) : "";
                String id = garantirId("", idioma, tipo, contexto, enunciado, indice, idsGerados);
                return new SituacaoProblemaAditiva(id, false, tipo, idioma, enunciado, contexto, fonte, "",
                        "", "", "", "", "", "", "", representacaoPadrao(tipo), "");
            }
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return null;
    }

    private static String converterCodigoIdiomaLegado(String valor) {
        String v = valor == null ? "" : valor.trim();
        if ("PORTUGUES".equalsIgnoreCase(v)) return "pt-BR";
        if ("INGLES".equalsIgnoreCase(v)) return "en";
        if ("FRANCES".equalsIgnoreCase(v)) return "fr";
        return IdiomaSituacao.normalizarCodigo(v);
    }

    private String valor(String[] partes, int indice) {
        return indice >= 0 && indice < partes.length && partes[indice] != null ? partes[indice].trim() : "";
    }

    private String garantirId(String id, IdiomaInterface idioma, TipoSituacaoAditiva tipo, String contexto, String enunciado, int indice, Set<String> usados) {
        String base = id == null || id.trim().length() == 0
                ? idioma.name().substring(0, 2) + "_" + tipo.name() + "_" + normalizarChave(contexto) + "_" + Math.abs(enunciado == null ? indice : enunciado.hashCode())
                : id.trim();
        base = base.replaceAll("[^A-Za-z0-9_\\-]", "_");
        if (base.length() == 0) {
            base = "situacao_" + indice;
        }
        String candidato = base;
        int sufixo = 2;
        while (usados.contains(candidato)) {
            candidato = base + "_" + sufixo;
            sufixo++;
        }
        usados.add(candidato);
        return candidato;
    }

    private String representacaoPadrao(TipoSituacaoAditiva tipo) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return "DIAGRAMA_COMPOSICAO_COLECOES";
        }
        return tipo == null ? "" : tipo.name();
    }

    private InputStream abrirArquivoSituacoes() {
        File arquivoCurado = obterArquivoCuradoriaUsuario();
        if (arquivoCurado.exists() && arquivoCurado.isFile()) {
            try {
                return new FileInputStream(arquivoCurado);
            } catch (IOException ex) {
                // Se o arquivo curado falhar, tenta o arquivo empacotado.
            }
        }

        InputStream input = RepositorioSituacoesAditivas.class.getResourceAsStream(ARQUIVO_SITUACOES);
        if (input != null) {
            return input;
        }

        String[] caminhos = new String[] {
            "dados/situacoes_vergnaud.tsv",
            "../dados/situacoes_vergnaud.tsv",
            "src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv"
        };

        for (String caminho : caminhos) {
            File arquivo = new File(caminho);
            if (arquivo.exists() && arquivo.isFile()) {
                try {
                    return new FileInputStream(arquivo);
                } catch (IOException ex) {
                    // Tenta o próximo caminho.
                }
            }
        }

        return null;
    }

    public static File obterDiretorioCuradoriaUsuario() {
        File base = new File(System.getProperty("user.home"), "Gerard");
        return new File(base, "curadoria");
    }

    public static File obterArquivoCuradoriaUsuario() {
        return new File(obterDiretorioCuradoriaUsuario(), "situacoes_vergnaud_curadas.tsv");
    }

    public static File obterArquivoAuditoriaUsuario() {
        return new File(obterDiretorioCuradoriaUsuario(), "auditoria_curadoria_situacoes.tsv");
    }

    private Map<TipoSituacaoAditiva, List<SituacaoProblemaAditiva>> mapa(IdiomaInterface idioma) {
        Map<TipoSituacaoAditiva, List<SituacaoProblemaAditiva>> mapa = situacoes.get(idioma);
        if (mapa == null) {
            mapa = new EnumMap<TipoSituacaoAditiva, List<SituacaoProblemaAditiva>>(TipoSituacaoAditiva.class);
            situacoes.put(idioma, mapa);
        }
        return mapa;
    }

    private List<SituacaoProblemaAditiva> lista(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        Map<TipoSituacaoAditiva, List<SituacaoProblemaAditiva>> mapa = mapa(idioma);
        List<SituacaoProblemaAditiva> lista = mapa.get(tipo);
        if (lista == null) {
            lista = new ArrayList<SituacaoProblemaAditiva>();
            mapa.put(tipo, lista);
        }
        return lista;
    }

    private void adicionar(IdiomaInterface idioma, TipoSituacaoAditiva tipo, String enunciado) {
        adicionar(new SituacaoProblemaAditiva(tipo, idioma, enunciado));
    }

    private void adicionar(SituacaoProblemaAditiva situacao) {
        if (situacao == null || situacao.getTipo() == null) return;
        if (situacao.getIdioma() != null) lista(situacao.getIdioma(), situacao.getTipo()).add(situacao);
        todasSituacoes.add(situacao);
    }

    private void carregarFallbackMinimo() {
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                "Lucas tem 12 figurinhas de Pokémon e 25 de Digimon. Quantas figurinhas ele tem ao todo?");
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                "Ricardo saiu para jogar bola de gude. Ao sair de casa ele possuía 2 bolas. Ao voltar, ele tinha 6 bolas. O que aconteceu no jogo?");
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS,
                "Vovó possui 2 rosas brancas e 3 amarelas. Deu 1 rosa branca e 1 rosa amarela para sua netinha. Com quantas rosas Vovó ficou?");
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                "Glaudenice tem R$ 14,00 a menos que Nádia. Nádia tem R$ 27,00. Quantos reais Glaudenice tem?");
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                "Em um jogo, Paulo ganhou 8 pontos na primeira rodada e perdeu 3 pontos na segunda. Qual foi a transformação total em sua pontuação?");
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS,
                "Geisa tinha uma caixa com 25 chocolates. Comeu 2 ontem e 5 hoje. Quantos chocolates ainda tem?");
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                "Ana tem 5 figurinhas a mais que Bia. Depois Ana ganhou mais 4 figurinhas. Quantas figurinhas a mais Ana passou a ter que Bia?");
        adicionar(IdiomaInterface.PORTUGUES, TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                "Carlos tem 7 reais a mais que João. João tem 2 reais a menos que Pedro. Qual é a relação entre a quantia de Carlos e a de Pedro?");
    }

    public SituacaoProblemaAditiva obter(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        List<SituacaoProblemaAditiva> candidatas = obterListaValidada(idioma, tipo);

        if (candidatas.isEmpty() && idioma != IdiomaInterface.PORTUGUES) {
            candidatas = obterListaValidada(IdiomaInterface.PORTUGUES, tipo);
        }

        if (candidatas.isEmpty()) {
            return new SituacaoProblemaAditiva(tipo, idioma, "");
        }

        int indice = random.nextInt(candidatas.size());
        return candidatas.get(indice);
    }


    public SituacaoProblemaAditiva obterCorrespondente(
            SituacaoProblemaAditiva referencia,
            IdiomaInterface idiomaDestino,
            TipoSituacaoAditiva tipoAtual,
            int[] valoresAtuais) {

        if (idiomaDestino == null) {
            idiomaDestino = IdiomaInterface.PORTUGUES;
        }

        TipoSituacaoAditiva tipoBusca = tipoAtual;
        if (referencia != null && referencia.getTipo() != null) {
            tipoBusca = referencia.getTipo();
        }

        if (tipoBusca == null) {
            tipoBusca = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
        }

        List<SituacaoProblemaAditiva> candidatas = obterListaValidada(idiomaDestino, tipoBusca);
        if (candidatas.isEmpty()) {
            return null;
        }

        if (referencia != null && referencia.getIdioma() == idiomaDestino) {
            return referencia;
        }

        if (referencia != null && referencia.getSituacaoGrupoId() != null
                && referencia.getSituacaoGrupoId().trim().length() > 0) {
            for (SituacaoProblemaAditiva candidata : candidatas) {
                if (referencia.getSituacaoGrupoId().equals(candidata.getSituacaoGrupoId())) {
                    return candidata;
                }
            }
        }

        String contextoReferencia = referencia == null ? "" : normalizarChave(referencia.getContexto());
        int[] valoresReferencia = referencia == null ? new int[0] : extrairValoresCurados(referencia);
        if (valoresReferencia.length == 0 && referencia != null) {
            valoresReferencia = extrairValoresCanonicos(referencia.getEnunciado());
        }
        int[] valoresPreferenciais = (valoresAtuais != null && valoresAtuais.length > 0) ? valoresAtuais : valoresReferencia;

        SituacaoProblemaAditiva melhorPorContextoEValores = null;
        SituacaoProblemaAditiva melhorPorValores = null;
        SituacaoProblemaAditiva melhorPorContexto = null;

        for (SituacaoProblemaAditiva candidata : candidatas) {
            String contextoCandidata = normalizarChave(candidata.getContexto());
            boolean mesmoContexto = contextoReferencia.length() > 0 && contextoReferencia.equals(contextoCandidata);
            int[] valoresCandidata = extrairValoresCurados(candidata);
            if (valoresCandidata.length == 0) {
                valoresCandidata = extrairValoresCanonicos(candidata.getEnunciado());
            }
            boolean mesmosValores = mesmaSequenciaNumerica(valoresPreferenciais, valoresCandidata);

            if (mesmoContexto && mesmosValores) {
                melhorPorContextoEValores = candidata;
                break;
            }
            if (melhorPorValores == null && mesmosValores) {
                melhorPorValores = candidata;
            }
            if (melhorPorContexto == null && mesmoContexto) {
                melhorPorContexto = candidata;
            }
        }

        if (melhorPorContextoEValores != null) {
            return melhorPorContextoEValores;
        }
        if (melhorPorValores != null) {
            return melhorPorValores;
        }
        if (melhorPorContexto != null) {
            return melhorPorContexto;
        }

        return candidatas.get(0);
    }

    private String normalizarChave(String texto) {
        if (texto == null) {
            return "";
        }
        String normalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(java.util.Locale.ROOT)
                .trim();
        normalizado = normalizado.replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_").replaceAll("^_|_$", "");
        return normalizado;
    }

    private boolean mesmaSequenciaNumerica(int[] a, int[] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i]) != Math.abs(b[i])) {
                return false;
            }
        }
        return true;
    }


    private int[] extrairValoresCurados(SituacaoProblemaAditiva situacao) {
        List<Integer> valores = new ArrayList<Integer>();
        if (situacao == null) {
            return new int[0];
        }
        adicionarValorCurado(valores, situacao.getEstadoInicial());
        adicionarValorCurado(valores, aplicarSinalCurado(situacao.getTransformacao(), situacao.getSinalTransformacao()));
        adicionarValorCurado(valores, situacao.getEstadoFinal());
        adicionarValorCurado(valores, situacao.getQuantidade1());
        adicionarValorCurado(valores, situacao.getQuantidade2());
        adicionarValorCurado(valores, situacao.getResultado());
        adicionarValorCurado(valores, situacao.getReferido());
        adicionarValorCurado(valores, situacao.getReferendo());
        adicionarValorCurado(valores, aplicarSinalCurado(situacao.getValorRelativo(), situacao.getSinalValorRelativo()));
        int[] resultado = new int[valores.size()];
        for (int i = 0; i < valores.size(); i++) {
            resultado[i] = valores.get(i).intValue();
        }
        return resultado;
    }

    private String aplicarSinalCurado(String valor, String sinal) {
        String v = valor == null ? "" : valor.trim();
        if (v.length() == 0 || v.startsWith("+") || v.startsWith("-") || SimboloDesconhecido.eh(v)) {
            return v;
        }
        String s = sinal == null ? "" : java.text.Normalizer.normalize(sinal, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(java.util.Locale.ROOT).trim();
        if ("negativo".equals(s) || "-1".equals(s) || "-".equals(s)) {
            return "-" + v;
        }
        if ("positivo".equals(s) || "+1".equals(s) || "+".equals(s)) {
            return "+" + v;
        }
        return v;
    }

    private void adicionarValorCurado(List<Integer> valores, String texto) {
        Integer valor = converterTokenParaInteiro(texto);
        if (valor != null) {
            valores.add(valor);
        }
    }

    private int[] extrairValoresCanonicos(String texto) {
        List<Integer> valores = new ArrayList<Integer>();
        if (texto == null) {
            return new int[0];
        }

        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("(?i)(?:R\\$\\s*)?\\d+(?:[.,]\\d+)?|\\b(?:um|uma|dois|duas|tres|três|quatro|cinco|seis|sete|oito|nove|dez|onze|doze|treze|quatorze|catorze|quinze|dezesseis|dezessete|dezoito|dezenove|vinte|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|un|une|deux|trois|quatre|cinq|six|sept|huit|neuf|dix|onze|douze|treize|quatorze|quinze|seize)\\b")
                .matcher(texto);

        while (matcher.find()) {
            String token = matcher.group();
            Integer valor = converterTokenParaInteiro(token);
            if (valor != null) {
                valores.add(valor);
            }
        }

        int[] resultado = new int[valores.size()];
        for (int i = 0; i < valores.size(); i++) {
            resultado[i] = valores.get(i);
        }
        return resultado;
    }

    private Integer converterTokenParaInteiro(String token) {
        if (token == null) {
            return null;
        }
        String t = token.replace("R$", "").replace("r$", "").trim().toLowerCase(java.util.Locale.ROOT);
        t = java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        t = t.replace(',', '.');
        try {
            if (t.matches("[+-]?\\d+(?:\\.\\d+)?")) {
                return (int) Math.round(Double.parseDouble(t));
            }
        } catch (NumberFormatException ex) {
            return null;
        }

        Map<String, Integer> mapa = new java.util.HashMap<String, Integer>();
        mapa.put("um", 1); mapa.put("uma", 1); mapa.put("dois", 2); mapa.put("duas", 2); mapa.put("tres", 3);
        mapa.put("quatro", 4); mapa.put("cinco", 5); mapa.put("seis", 6); mapa.put("sete", 7); mapa.put("oito", 8); mapa.put("nove", 9);
        mapa.put("dez", 10); mapa.put("onze", 11); mapa.put("doze", 12); mapa.put("treze", 13); mapa.put("quatorze", 14); mapa.put("catorze", 14);
        mapa.put("quinze", 15); mapa.put("dezesseis", 16); mapa.put("dezessete", 17); mapa.put("dezoito", 18); mapa.put("dezenove", 19); mapa.put("vinte", 20);
        mapa.put("one", 1); mapa.put("two", 2); mapa.put("three", 3); mapa.put("four", 4); mapa.put("five", 5); mapa.put("six", 6); mapa.put("seven", 7);
        mapa.put("eight", 8); mapa.put("nine", 9); mapa.put("ten", 10); mapa.put("eleven", 11); mapa.put("twelve", 12); mapa.put("thirteen", 13);
        mapa.put("fourteen", 14); mapa.put("fifteen", 15); mapa.put("sixteen", 16); mapa.put("seventeen", 17); mapa.put("eighteen", 18); mapa.put("nineteen", 19); mapa.put("twenty", 20);
        mapa.put("un", 1); mapa.put("une", 1); mapa.put("deux", 2); mapa.put("trois", 3); mapa.put("quatre", 4); mapa.put("cinq", 5); mapa.put("sept", 7);
        mapa.put("huit", 8); mapa.put("neuf", 9); mapa.put("dix", 10); mapa.put("onze", 11); mapa.put("douze", 12); mapa.put("treize", 13);
        mapa.put("seize", 16);
        return mapa.get(t);
    }

    public SituacaoProblemaAditiva obterPrimeira(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        List<SituacaoProblemaAditiva> candidatas = obterListaValidada(idioma, tipo);
        if (candidatas.isEmpty()) {
            candidatas = obterListaValidada(IdiomaInterface.PORTUGUES, tipo);
        }
        return candidatas.isEmpty() ? new SituacaoProblemaAditiva(tipo, idioma, "") : candidatas.get(0);
    }

    public int contar(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        return obterListaValidada(idioma, tipo).size();
    }

    public int contarTodas(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        return obterLista(idioma, tipo).size();
    }

    public int contarValidadas() {
        int total = 0;
        for (SituacaoProblemaAditiva s : todasSituacoes) {
            if (ehSituacaoExibivel(s)) {
                total++;
            }
        }
        return total;
    }

    public List<SituacaoProblemaAditiva> listarTodas() {
        return new ArrayList<SituacaoProblemaAditiva>(todasSituacoes);
    }

    public List<SituacaoProblemaAditiva> listarValidadas() {
        List<SituacaoProblemaAditiva> resultado = new ArrayList<SituacaoProblemaAditiva>();
        for (SituacaoProblemaAditiva s : todasSituacoes) {
            if (ehSituacaoExibivel(s)) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    private List<SituacaoProblemaAditiva> obterListaValidada(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        List<SituacaoProblemaAditiva> base = obterLista(idioma, tipo);
        List<SituacaoProblemaAditiva> validadas = new ArrayList<SituacaoProblemaAditiva>();
        for (SituacaoProblemaAditiva s : base) {
            if (ehSituacaoExibivel(s)) {
                validadas.add(s);
            }
        }
        return validadas;
    }

    public boolean possuiSituacoesValidadas() {
        return contarValidadas() > 0;
    }

    private boolean ehSituacaoExibivel(SituacaoProblemaAditiva situacao) {
        return situacao != null
                && situacao.isValidada()
                && situacao.getEnunciado() != null
                && situacao.getEnunciado().trim().length() > 0;
    }

    private List<SituacaoProblemaAditiva> obterLista(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        Map<TipoSituacaoAditiva, List<SituacaoProblemaAditiva>> mapa = situacoes.get(idioma);
        if (mapa == null) {
            return new ArrayList<SituacaoProblemaAditiva>();
        }
        List<SituacaoProblemaAditiva> lista = mapa.get(tipo);
        return lista == null ? new ArrayList<SituacaoProblemaAditiva>() : lista;
    }

    public static void salvarCuradoria(List<SituacaoProblemaAditiva> situacoes) throws IOException {
        ValidadorVinculosTraducoes.validarOuFalhar(situacoes);
        File diretorio = obterDiretorioCuradoriaUsuario();
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
        File destino = obterArquivoCuradoriaUsuario();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            bw.write(CABECALHO_CURADORIA);
            bw.newLine();
            if (situacoes != null) {
                for (SituacaoProblemaAditiva s : situacoes) {
                    bw.write(formatarLinhaCuradoria(s));
                    bw.newLine();
                }
            }
        }
    }

    public static String formatarLinhaCuradoria(SituacaoProblemaAditiva s) {
        return campo(s.getId()) + "\t" + campo(s.getSituacaoGrupoId()) + "\t" + campo(s.getTipoVersao()) + "\t" + campo(s.getVersaoOrigemId())
                + "\t" + s.isValidada() + "\t" + campo(s.getCodigoIdioma())
                + "\t" + campo(s.getTipo() == null ? "" : s.getTipo().name())
                + "\t" + campo(s.getContexto())
                + "\t" + campo(s.getEnunciado())
                + "\t" + campo(s.getFonte())
                + "\t" + campo(s.getSubtipo())
                + "\t" + campo(s.getEstadoInicial())
                + "\t" + campo(s.getTransformacao())
                + "\t" + campo(s.getSinalTransformacao())
                + "\t" + campo(s.getEstadoFinal())
                + "\t" + campo(s.getQuantidade1())
                + "\t" + campo(s.getQuantidade2())
                + "\t" + campo(s.getResultado())
                + "\t" + campo(s.getReferido())
                + "\t" + campo(s.getReferendo())
                + "\t" + campo(s.getValorRelativo())
                + "\t" + campo(s.getSinalValorRelativo())
                + "\t" + campo(s.getTermoDesconhecido())
                + "\t" + campo(s.getRepresentacaoVisual())
                + "\t" + campo(s.getObservacoes())
                + "\t" + campo(s.getPersonagem1())
                + "\t" + campo(s.getPersonagem2())
                + "\t" + campo(s.getPersonagem3())
                + "\t" + campo(s.getFragmentoTexto1())
                + "\t" + campo(s.getFragmentoTexto2())
                + "\t" + campo(s.getFragmentoTexto3())
                + "\t" + campo(s.getFragmentoTexto4())
                + "\t" + campo(s.getFragmentoTexto5())
                + "\t" + campo(s.getFragmentoTexto6());
    }

    private static String campo(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ').trim();
    }
}
