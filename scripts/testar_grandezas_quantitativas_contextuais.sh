#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$ROOT/build/test-grandezas-contextuais"
rm -rf "$TMP"
mkdir -p "$TMP"
cat > "$TMP/TesteGrandezasContextuais.java" <<'JAVA'
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.transformacao.processo.PlanoUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.SincronizadorUnidadesProcessoTransformacao;
import gerard.idioma.IdiomaInterface;
import gerard.semantica.papel.CatalogoPapeisSemanticos;
import gerard.semantica.quantidade.*;
import java.math.BigDecimal;

public class TesteGrandezasContextuais {
    private static int verificacoes = 0;
    private static void ok(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static SituacaoProblemaAditiva dinheiro(String enunciado) {
        return new SituacaoProblemaAditiva(
            "M1", true, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
            IdiomaInterface.PORTUGUES, enunciado, "Dinheiro", "teste",
            "", "25,00", "18,00", "7,00", "", "", "",
            "estado_final", "PROCESSO_TRANSFORMACAO", "");
    }

    public static void main(String[] args) {
        ResolvedorPerfilQuantidadeSituacao resolvedor =
                new ResolvedorPerfilQuantidadeSituacao();
        SituacaoProblemaAditiva monetaria = dinheiro(
                "Maria tinha R$ 25,00 e gastou R$ 18,00. Quanto restou?");
        PerfilQuantidadeSituacao pm = resolvedor.resolver(monetaria);
        ok(pm.getTipo() == TipoGrandezaQuantitativa.MONETARIA,
                "R$ deve selecionar grandeza monetária");
        ok("BRL".equals(pm.getUnidade().getCodigo()),
                "A moeda deve ser BRL");

        SituacaoProblemaAditiva contagem = new SituacaoProblemaAditiva(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                IdiomaInterface.PORTUGUES,
                "Paulo tem 6 bolas e 8 bolas. Quantas tem ao todo?",
                "Bolas", "teste");
        PerfilQuantidadeSituacao pc = resolvedor.resolver(contagem);
        ok(pc.getTipo() == TipoGrandezaQuantitativa.CONTAGEM,
                "Bolas devem selecionar contagem");

        SituacaoProblemaAditiva explicita = new SituacaoProblemaAditiva(
            "C1", true, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
            IdiomaInterface.PORTUGUES, "Há 2 moedas e 3 moedas.",
            "Moedas", "teste", "", "", "", "", "2", "3", "5",
            "todo", "GRANDEZA=CONTAGEM", "");
        ok(resolvedor.resolver(explicita).getTipo()
                == TipoGrandezaQuantitativa.CONTAGEM,
                "Metadado explícito deve prevalecer sobre pistas linguísticas");

        ConversorTextoParaQuantidadeSemantica conversor =
                new ConversorTextoParaQuantidadeSemantica();
        CatalogoPapeisSemanticos papeis = new CatalogoPapeisSemanticos();
        ok(conversor.converter("6", pc,
                papeis.obter("papel.estadoInicial")) != null,
                "Contagem inteira deve ser válida");
        ok(conversor.converter("6,5", pc,
                papeis.obter("papel.estadoInicial")) == null,
                "Contagem fracionária deve ser rejeitada");
        ok(conversor.converter("25,50", pm,
                papeis.obter("papel.estadoInicial")) != null,
                "Dinheiro deve preservar centavos");
        ok(conversor.converter("-18,25", pm,
                papeis.obter("papel.transformacao")) != null,
                "Transformação monetária pode ser negativa");
        ok(conversor.converter("-18,25", pm,
                papeis.obter("papel.estadoFinal")) == null,
                "Estado monetário continua não negativo");

        ServicoQuantidadeContextual servico =
                new ServicoQuantidadeContextual();
        ok(Integer.valueOf(25).equals(servico.converterParaInteiroLegado(
                "25,00", monetaria)),
                "Ponte legada deve aceitar dinheiro integral com ,00");
        ok(servico.converterParaInteiroLegado("25,50", monetaria) == null,
                "Ponte inteira não pode truncar centavos");
        ok("7".equals(servico.formatarInteiroLegado(
                7, monetaria, false)),
                "Dinheiro não acrescenta ,00 quando o valor legado é inteiro");

        EstadoSemanticoCompartilhado estado =
                new EstadoSemanticoCompartilhado();
        estado.atualizar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {25, -18, null},
                new boolean[] {true, true, false}, 1,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);
        PlanoUnidadesProcessoTransformacao plano =
                new SincronizadorUnidadesProcessoTransformacao()
                .criarPlano(estado.snapshot(), monetaria);
        ok(plano.getQuantidade(0) == 25, "Antes deve ter 25 unidades");
        ok(plano.getQuantidade(1) == 18, "Funil deve ter 18 unidades");
        ok(plano.getQuantidade(2) == 7, "Depois deve ter 7 unidades");
        ok(plano.getTipoGrandeza() == TipoGrandezaQuantitativa.MONETARIA,
                "Plano deve preservar o tipo monetário");

        EstadoSemanticoCompartilhado estadoGrande =
                new EstadoSemanticoCompartilhado();
        estadoGrande.atualizar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {250, -180, 70},
                new boolean[] {true, true, true}, 1,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);
        PlanoUnidadesProcessoTransformacao agrupado =
                new SincronizadorUnidadesProcessoTransformacao()
                .criarPlano(estadoGrande.snapshot(), monetaria);
        ok(agrupado.getQuantidade(0) == 25
                && agrupado.getQuantidade(1) == 18
                && agrupado.getQuantidade(2) == 7,
                "Escala monetária deve agrupar por divisor comum exato");
        ok(agrupado.possuiEscalaAgrupada(),
                "Escala agrupada deve publicar legenda");
        ok(agrupado.converterUnidadesParaValor(26) == 260,
                "Interação concreta deve respeitar a escala monetária");
        ok("250".equals(agrupado.getValorFormatado(0))
                && "-180".equals(agrupado.getValorFormatado(1)),
                "Processo monetário não acrescenta ,00 quando o valor legado é inteiro");

        System.out.println("OK: " + verificacoes
                + " verificações de grandezas quantitativas contextuais.");
    }
}
JAVA
javac -encoding UTF-8 -cp "$ROOT/build/classes" -d "$TMP" "$TMP/TesteGrandezasContextuais.java"
java -Dfile.encoding=UTF-8 -cp "$ROOT/build/classes:$TMP" TesteGrandezasContextuais
