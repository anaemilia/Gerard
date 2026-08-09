package gerard.agente.modelousuario;

import gerard.agente.modelador.AgenteModelador;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Cobre a persistência da dimensão 5 (Diagnóstico da tarefa) em
 * diagnosticos_tarefa.tsv: gravação via AgenteModelador (caso, autorrelato)
 * e via curadoria direta (nivelConceitualCurado), depois releitura por uma
 * segunda instância do repositório apontando para o mesmo arquivo — decisão
 * do usuário em 2026-07-23.
 */
public final class TesteRepositorioModeloUsuarioDiagnosticos {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        File diretorio = new File(System.getProperty("java.io.tmpdir"),
                "gerard_teste_diagnosticos_" + System.currentTimeMillis());
        diretorio.mkdirs();
        File arquivoPerfis = new File(diretorio, "perfis_usuario.tsv");
        File arquivoDiagnosticos = new File(diretorio, "diagnosticos_tarefa.tsv");

        RepositorioModeloUsuario repositorio = new RepositorioModeloUsuario(arquivoPerfis, arquivoDiagnosticos);
        AgenteModelador agenteModelador = new AgenteModelador(repositorio);

        String idUsuario = "professora_teste";
        String tarefa = "COMPOSICAO_MEDIDAS:papel.todo";

        DiagnosticoTarefa diagnostico = new DiagnosticoTarefa(tarefa);
        diagnostico.setSuporte(NivelSuporte.PARCIAL);
        diagnostico.setRegraDeAcao("POSICIONAR");
        agenteModelador.armazenarCaso(idUsuario, diagnostico);

        // Texto com tab e quebra de linha de propósito, para testar o escape.
        String explicacaoComEspeciais = "somei\tas partes\ne encontrei o todo";
        boolean encontrou = agenteModelador.registrarExplicacaoNoUltimoDiagnostico(
                idUsuario, tarefa, "FACIL", explicacaoComEspeciais, "estratégia geral da tentativa",
                "CATALOGO", "COMP_CARDINAL_TODO", "Card(Todo) = Card(Parte₁) + Card(Parte₂)",
                "observação\tcom tab");
        confirmar(encontrou, "deve encontrar o caso recém-armazenado para complementar");

        List<DiagnosticoTarefa> diagnosticosEmMemoria = repositorio.obter(idUsuario).getDiagnosticos();
        confirmar(diagnosticosEmMemoria.size() == 1, "deve haver exatamente um diagnóstico em memória");
        diagnosticosEmMemoria.get(0).setNivelConceitualCurado(NivelConceitualExplicacao.SUBSTANTIVADO);
        repositorio.salvarDiagnosticos();

        confirmar(arquivoDiagnosticos.exists(), "o arquivo de diagnósticos deve existir depois de salvar");

        // Segunda instância, mesmo arquivo: simula reabrir o Gérard.
        RepositorioModeloUsuario repositorioRecarregado =
                new RepositorioModeloUsuario(arquivoPerfis, arquivoDiagnosticos);
        ModeloUsuario modeloRecarregado = repositorioRecarregado.obter(idUsuario);
        confirmar(modeloRecarregado != null, "o usuário deve existir depois de recarregar");
        List<DiagnosticoTarefa> diagnosticosRecarregados = modeloRecarregado.getDiagnosticos();
        confirmar(diagnosticosRecarregados.size() == 1, "deve recarregar exatamente um diagnóstico");

        DiagnosticoTarefa recarregado = diagnosticosRecarregados.get(0);
        confirmar(tarefa.equals(recarregado.getTarefa()), "tarefa deve bater depois de recarregar");
        confirmar(NivelSuporte.PARCIAL == recarregado.getSuporte(), "suporte deve bater depois de recarregar");
        confirmar("POSICIONAR".equals(recarregado.getRegraDeAcao()), "regraDeAcao deve bater depois de recarregar");
        confirmar("FACIL".equals(recarregado.getDificuldadeAutorrelatada()),
                "dificuldadeAutorrelatada deve bater depois de recarregar");
        confirmar(explicacaoComEspeciais.equals(recarregado.getExplicacaoElemento()),
                "explicacaoElemento com tab/quebra de linha deve sobreviver ao escape/desescape");
        confirmar("estratégia geral da tentativa".equals(recarregado.getExplicacaoGeral()),
                "explicacaoGeral deve bater depois de recarregar");
        confirmar(NivelConceitualExplicacao.AUSENTE != recarregado.getNivelConceitualEstimado(),
                "nivelConceitualEstimado deve ter sido calculado e persistido");
        confirmar(NivelConceitualExplicacao.SUBSTANTIVADO == recarregado.getNivelConceitualCurado(),
                "nivelConceitualCurado deve bater depois de recarregar");
        confirmar("CATALOGO".equals(recarregado.getInvarianteOrigem()),
                "invarianteOrigem deve bater depois de recarregar");
        confirmar("COMP_CARDINAL_TODO".equals(recarregado.getInvarianteCodigo()),
                "invarianteCodigo deve bater depois de recarregar");
        confirmar("Card(Todo) = Card(Parte₁) + Card(Parte₂)".equals(recarregado.getInvarianteSimbolico()),
                "invarianteSimbolico deve bater depois de recarregar");
        confirmar("observação\tcom tab".equals(recarregado.getInvarianteObservacao()),
                "invarianteObservacao com tab deve sobreviver ao escape/desescape");

        testarCompatibilidadeComFormatoAntigoSemInvariante(diretorio);

        System.out.println("Teste de persistência de diagnósticos aprovado: " + verificacoes + " verificações.");
    }

    /**
     * Regressão do bug encontrado pelo usuário em 2026-07-23: um arquivo
     * gravado antes das 4 colunas de invariante (11 colunas) precisa
     * continuar carregando — sem isso, toda explicação salva antes da
     * mudança de esquema era descartada como "corrompida" ao abrir o app.
     */
    private static void testarCompatibilidadeComFormatoAntigoSemInvariante(File diretorio) throws Exception {
        File arquivoPerfisAntigo = new File(diretorio, "perfis_usuario_antigo.tsv");
        File arquivoDiagnosticosAntigo = new File(diretorio, "diagnosticos_tarefa_antigo.tsv");

        BufferedWriter escritor = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(arquivoDiagnosticosAntigo), "UTF-8"));
        escritor.write("usuario_id\ttarefa\tregra_de_acao\tsuporte\tinternalizado\t"
                + "probabilidade_saber_conteudo\tdificuldade_autorrelatada\texplicacao_elemento\t"
                + "explicacao_geral\tnivel_conceitual_estimado\tnivel_conceitual_curado");
        escritor.newLine();
        escritor.write("usuario_local\tCOMPOSICAO_MEDIDAS:papel.todo\tPOSICIONAR\tNENHUM\tfalse\t0.0\t"
                + "FACIL\tIdentiquei o valor do todo\tIdentifiquei os elementos da situação-problema\tADJETIVO\t");
        escritor.newLine();
        escritor.close();

        RepositorioModeloUsuario repositorioAntigo =
                new RepositorioModeloUsuario(arquivoPerfisAntigo, arquivoDiagnosticosAntigo);
        ModeloUsuario modeloAntigo = repositorioAntigo.obter("usuario_local");
        confirmar(modeloAntigo != null, "usuario_local do formato antigo deve carregar");
        List<DiagnosticoTarefa> diagnosticosAntigos = modeloAntigo.getDiagnosticos();
        confirmar(diagnosticosAntigos.size() == 1, "a linha de 11 colunas não deve ser descartada como corrompida");
        DiagnosticoTarefa diagnosticoAntigo = diagnosticosAntigos.get(0);
        confirmar("Identiquei o valor do todo".equals(diagnosticoAntigo.getExplicacaoElemento()),
                "explicacaoElemento do formato antigo deve carregar");
        confirmar(NivelConceitualExplicacao.ADJETIVO == diagnosticoAntigo.getNivelConceitualEstimado(),
                "nivelConceitualEstimado do formato antigo deve carregar");
        confirmar(diagnosticoAntigo.getInvarianteCodigo() == null,
                "invarianteCodigo deve ficar nulo para uma linha sem essas colunas, não quebrar o parse");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
