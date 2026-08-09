package gerard.campoaditivo.sincronizacao.representacoes;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

public final class TesteCoordenadorSincronizacaoRepresentacoes {

    public static void main(String[] args) {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[] {2, 3, 5},
                new boolean[] {true, true, true},
                0,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);

        final CoordenadorSincronizacaoRepresentacoes coordenador =
                new CoordenadorSincronizacaoRepresentacoes();
        final StringBuilder ordem = new StringBuilder();
        DestinoSincronizacaoRepresentacoes destino = new DestinoSincronizacaoRepresentacoes() {
            @Override
            public void aplicarNoVergnaud(EstadoSemanticoCompartilhado.Snapshot estado) {
                ordem.append("vergnaud>");
            }

            @Override
            public void aplicarNoTexto(EstadoSemanticoCompartilhado.Snapshot estado) {
                ordem.append("texto>");
                coordenador.sincronizar(estado, true, this);
            }

            @Override
            public void reconstruirRepresentacaoComplementar() {
                ordem.append("complementar>");
            }

            @Override
            public void aplicarNosEixos(EstadoSemanticoCompartilhado.Snapshot estado) {
                ordem.append("eixos");
            }
        };

        coordenador.sincronizar(snapshot, true, destino);
        exigir("vergnaud>texto>complementar>eixos".equals(ordem.toString()),
                "Ordem alterada ou reentrada não bloqueada: " + ordem);
        exigir(!coordenador.estaSincronizando(),
                "Coordenador permaneceu marcado como sincronizando.");

        ordem.setLength(0);
        coordenador.sincronizar(snapshot, false, destino);
        exigir("vergnaud>texto>eixos".equals(ordem.toString()),
                "Representação complementar não deveria ser reconstruída: " + ordem);

        System.out.println("Teste aprovado: coordenação preserva ordem e bloqueia reentrada.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
