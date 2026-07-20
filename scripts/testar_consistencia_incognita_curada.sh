#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$ROOT/build/test-consistencia-incognita-curada"
rm -rf "$TMP"
mkdir -p "$TMP"
cat > "$TMP/TesteConsistenciaIncognitaCurada.java" <<'JAVA'
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.ResultadoInterpretacao;

public class TesteConsistenciaIncognitaCurada {
    private static int verificacoes = 0;
    private static void ok(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static SituacaoProblemaAditiva s(TipoSituacaoAditiva tipo,
            String ei, String tr, String ef, String q1, String q2,
            String res, String referido, String referendo, String relativo,
            String termo) {
        return new SituacaoProblemaAditiva(
                "T-" + tipo.name(), true, tipo, IdiomaInterface.PORTUGUES,
                "Situação curada de teste?", "Teste", "teste", "",
                ei, tr, ef, q1, q2, res, referido, referendo, relativo,
                termo, "", "");
    }

    private static void verificar(TipoSituacaoAditiva tipo,
            SituacaoProblemaAditiva situacao, String chave, String termo) {
        ResolvedorIncognitaCurada.Resultado r =
                new ResolvedorIncognitaCurada().resolver(situacao);
        ok(chave.equals(r.getChaveEfetiva()),
                tipo + " deve resolver " + chave + ", obtido " + r.getChaveEfetiva());
        ok(termo.equals(r.getTermoCuradoriaEfetivo()),
                tipo + " deve harmonizar o termo " + termo);
        ok(r.possuiUmaUnicaInterrogacao(),
                tipo + " deve encontrar uma única interrogação");
    }

    public static void main(String[] args) {
        SituacaoProblemaAditiva leandro = s(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                "10", "?", "6", "", "", "", "", "", "", "");
        verificar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                leandro, "papel.transformacao", "transformação");

        ResultadoInterpretacao interpretacao =
                new ConstrutorResultadoCurado().construir(leandro,
                "Leandro tinha 10 balas. Restaram 6. Quantas comeu?");
        boolean transformacaoDesconhecida = false;
        for (PapelElementoInterpretado p : interpretacao.getPapeis()) {
            if ("papel.transformacao".equals(p.getChavePapel())) {
                transformacaoDesconhecida = !p.isConhecido() && "?".equals(p.getElemento());
            }
        }
        ok(transformacaoDesconhecida,
                "O ? curado na transformação não pode ser tratado como valor conhecido");
        ok(SemanticaCuradaSituacao.buscar(leandro, null,
                "papel.transformacao").isDesconhecido(),
                "A semântica curada deve marcar a transformação como desconhecida");

        verificar(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                s(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                        "", "", "", "2", "3", "?", "", "", "", ""),
                "papel.todo", "todo");
        verificar(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS,
                s(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS,
                        "", "?", "4", "2", "3", "5", "", "", "", ""),
                "papel.transformacao", "transformação");
        verificar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                s(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                        "", "", "", "", "", "", "6", "14", "?", ""),
                "papel.diferenca", "valor_relativo");
        verificar(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                s(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                        "", "", "", "-2", "+5", "?", "", "", "", ""),
                "papel.transformacaoFinal", "transformacao_resultante");
        verificar(TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS,
                s(TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS,
                        "20", "", "", "-2", "-5", "?", "", "", "", ""),
                "papel.estadoFinal", "estado_final");
        verificar(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                s(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                        "4", "?", "7", "", "", "", "", "", "", ""),
                "papel.transformacao", "transformação");
        verificar(TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                s(TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                        "", "", "", "+2", "-3", "?", "", "", "", ""),
                "papel.relacaoFinal", "relacao_resultante");

        ResolvedorIncognitaCurada.Resultado conflito =
                new ResolvedorIncognitaCurada().resolver(
                s(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        "10", "?", "6", "", "", "", "", "", "",
                        "estado_final"));
        ok(conflito.possuiConflito(),
                "Termo explícito divergente do campo com ? deve ser sinalizado");
        ok("papel.transformacao".equals(conflito.getChaveEfetiva()),
                "O ? efetivamente curado deve permanecer protegido no modelo");

        ResolvedorIncognitaCurada.Resultado multiplas =
                new ResolvedorIncognitaCurada().resolver(
                s(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                        "", "", "", "?", "?", "5", "", "", "", ""));
        ok(multiplas.possuiMultiplasInterrogacoes(),
                "Mais de um campo com ? deve ser rejeitado pela curadoria");

        SituacaoProblemaAditiva explicita = s(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                "10", "4", "6", "", "", "", "", "", "",
                "transformação");
        ResultadoInterpretacao ie = new ConstrutorResultadoCurado().construir(explicita);
        boolean explicitamenteDesconhecida = false;
        for (PapelElementoInterpretado p : ie.getPapeis()) {
            if ("papel.transformacao".equals(p.getChavePapel())) {
                explicitamenteDesconhecida = !p.isConhecido() && "?".equals(p.getElemento());
            }
        }
        ok(explicitamenteDesconhecida,
                "termo_desconhecido explícito deve ocultar valor numérico legado");

        System.out.println("OK: " + verificacoes
                + " verificações de consistência entre curadoria e semântica.");
    }
}
JAVA
javac -encoding UTF-8 -cp "$ROOT/build/classes" -d "$TMP" "$TMP/TesteConsistenciaIncognitaCurada.java"
java -Dfile.encoding=UTF-8 -Djava.awt.headless=true -cp "$ROOT/build/classes:$TMP" TesteConsistenciaIncognitaCurada
