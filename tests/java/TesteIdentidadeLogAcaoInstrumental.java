import gerard.pesquisador.log.EventoLogGerard;

/** Verifica acréscimo ao fim do TSV e leitura de linhas anteriores à P3.1. */
public final class TesteIdentidadeLogAcaoInstrumental {
    public static void main(String[] args) {
        EventoLogGerard novo = EventoLogGerard.criar(
                "sessao", "usuario", "S", "problema", "tentativa",
                "Substituir incógnita por número", "E", "Caixa de texto",
                "Item arrastável", "Informar valor", "OBJ8", "regra",
                "COMPOSICAO_MEDIDAS", "enunciado", "EDICAO_ITEM", "valor=9");
        novo.setActionId("acao-3");
        novo.setRejectionSequenceId("sequencia-1");

        String cabecalho = EventoLogGerard.cabecalhoTsv();
        exigir(cabecalho.endsWith("action_id\trejection_sequence_id"),
                "Os novos campos devem ser acrescentados ao fim do cabeçalho.");
        EventoLogGerard lido = EventoLogGerard.deTsv(novo.toTsv());
        exigir("acao-3".equals(lido.getActionId())
                        && "sequencia-1".equals(lido.getRejectionSequenceId()),
                "O round-trip deve preservar as duas identidades.");

        String[] camposNovos = novo.toTsv().split("\\t", -1);
        StringBuilder linhaAntiga = new StringBuilder();
        for (int i = 0; i < camposNovos.length - 2; i++) {
            if (i > 0) linhaAntiga.append('\t');
            linhaAntiga.append(camposNovos[i]);
        }
        EventoLogGerard antigo = EventoLogGerard.deTsv(linhaAntiga.toString());
        exigir(antigo != null && "".equals(antigo.getActionId())
                        && "".equals(antigo.getRejectionSequenceId()),
                "Uma linha antiga deve continuar legível com identidades vazias.");

        System.out.println("Teste aprovado: persistência compatível das identidades de ação.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
        System.out.println("OK - " + mensagem);
    }
}
