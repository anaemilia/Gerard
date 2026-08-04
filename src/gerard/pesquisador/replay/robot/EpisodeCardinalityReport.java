package gerard.pesquisador.replay.robot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Uma linha de {@code cardinalidade_episodios.tsv} (rodada 3, 2026-07-31) —
 * junta as contagens do lado Robot (gestos físicos, falhas de pickup/drop,
 * avaliações não despachadas — vindas de {@link RobotGestureTrace}) com as
 * contagens do lado da auditoria (ações canônicas, avaliações reativas,
 * decisões reais do ZDP/Modelador, casos, duplicados, divergências — já
 * calculadas por {@code AgentAuditService}). Nenhum cálculo de decisão
 * pedagógica acontece aqui — só soma o que os dois lados já produziram.
 */
public final class EpisodeCardinalityReport {
    private final String episodeId;
    private final int gestosFisicos;
    private final int acoesPedagogicasCanonicas;
    private final int subeventosTecnicos;
    private final int avaliacoesCanonicas;
    private final int avaliacoesReativas;
    private final int falhasPickup;
    private final int falhasDrop;
    private final int avaliacoesNaoDisparadas;
    private final int decisoesZdp;
    private final int atualizacoesModelador;
    private final int casosInseridos;
    private final int duplicadosBloqueados;
    private final int divergencias;

    public EpisodeCardinalityReport(String episodeId, int gestosFisicos, int acoesPedagogicasCanonicas,
            int subeventosTecnicos, int avaliacoesCanonicas, int avaliacoesReativas, int falhasPickup,
            int falhasDrop, int avaliacoesNaoDisparadas, int decisoesZdp, int atualizacoesModelador,
            int casosInseridos, int duplicadosBloqueados, int divergencias) {
        this.episodeId = episodeId;
        this.gestosFisicos = gestosFisicos;
        this.acoesPedagogicasCanonicas = acoesPedagogicasCanonicas;
        this.subeventosTecnicos = subeventosTecnicos;
        this.avaliacoesCanonicas = avaliacoesCanonicas;
        this.avaliacoesReativas = avaliacoesReativas;
        this.falhasPickup = falhasPickup;
        this.falhasDrop = falhasDrop;
        this.avaliacoesNaoDisparadas = avaliacoesNaoDisparadas;
        this.decisoesZdp = decisoesZdp;
        this.atualizacoesModelador = atualizacoesModelador;
        this.casosInseridos = casosInseridos;
        this.duplicadosBloqueados = duplicadosBloqueados;
        this.divergencias = divergencias;
    }

    /** ações pedagógicas canônicas == decisões ZDP == atualizações Modelador == casos inseridos. */
    public boolean cardinalidadeConsistente() {
        return acoesPedagogicasCanonicas == decisoesZdp
                && decisoesZdp == atualizacoesModelador
                && atualizacoesModelador == casosInseridos;
    }

    public static void escreverCabecalho(File arquivo) throws IOException {
        if (arquivo.exists()) {
            return;
        }
        PrintWriter escritor = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
        try {
            escritor.println("episode_id\tgestos_fisicos\tacoes_pedagogicas_canonicas\tsubeventos_tecnicos\t"
                    + "avaliacoes_canonicas\tavaliacoes_reativas\tfalhas_pickup\tfalhas_drop\t"
                    + "avaliacoes_nao_disparadas\tdecisoes_zdp\tatualizacoes_modelador\tcasos_inseridos\t"
                    + "duplicados_bloqueados\tdivergencias\tcardinalidade_consistente");
        } finally {
            escritor.close();
        }
    }

    public void escreverLinha(File arquivo) throws IOException {
        PrintWriter escritor = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
        try {
            escritor.println(episodeId + "\t" + gestosFisicos + "\t" + acoesPedagogicasCanonicas + "\t"
                    + subeventosTecnicos + "\t" + avaliacoesCanonicas + "\t" + avaliacoesReativas + "\t"
                    + falhasPickup + "\t" + falhasDrop + "\t" + avaliacoesNaoDisparadas + "\t" + decisoesZdp
                    + "\t" + atualizacoesModelador + "\t" + casosInseridos + "\t" + duplicadosBloqueados + "\t"
                    + divergencias + "\t" + cardinalidadeConsistente());
        } finally {
            escritor.close();
        }
    }

    public String getEpisodeId() { return episodeId; }
    public int getGestosFisicos() { return gestosFisicos; }
    public int getAcoesPedagogicasCanonicas() { return acoesPedagogicasCanonicas; }
    public int getFalhasPickup() { return falhasPickup; }
    public int getFalhasDrop() { return falhasDrop; }
    public int getAvaliacoesNaoDisparadas() { return avaliacoesNaoDisparadas; }
}
