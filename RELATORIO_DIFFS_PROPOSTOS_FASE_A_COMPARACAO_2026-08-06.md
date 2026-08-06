# Diffs propostos — Fase A: contrato rico para Composição + Comparação nova (pacote piloto)

Data: 2026-08-06. **Propostos, nenhum aplicado ainda** — aguardando aprovação e resposta às duas perguntas abertas no final.

Escopo: `TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md`, Fase A (já autorizada) — só `src/gerard/dominio/campoaditivo/` e dois harnesses em `src/`. Zero mudança em `Main.java` ou qualquer caminho de produção.

---

## 1. `RelacaoEstruturalComposicao.java` — adicionar contrato rico

```diff
--- a/src/gerard/dominio/campoaditivo/RelacaoEstruturalComposicao.java
+++ b/src/gerard/dominio/campoaditivo/RelacaoEstruturalComposicao.java
@@ -1,5 +1,7 @@
 package gerard.dominio.campoaditivo;
 
+import gerard.semantica.numero.NumeroInteiro;
+
 /**
  * Relação estrutural formal do esquema Composição de Medidas:
  * Todo = Parte1 + Parte2.
@@ -47,6 +49,58 @@ public final class RelacaoEstruturalComposicao {
         return (t == p1 + p2) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
     }
 
+    /**
+     * Calcula o valor do único papel incógnito entre os três, sem
+     * modificá-lo. Se não houver exatamente uma incógnita, o resultado é
+     * NAO_RESOLVIVEL_NESTE_ESTADO — não uma exceção: em uma atividade
+     * pedagógica interativa, zero, duas ou três incógnitas são estados
+     * legítimos (representação ainda incompleta, ou já totalmente
+     * preenchida), não violações de contrato de programação. Exceção só é
+     * lançada para argumento nulo, que é, de fato, um erro do chamador.
+     */
+    public ResultadoCalculo calcularValorAusente(PapelQuantitativo parte1, PapelQuantitativo parte2,
+                                                  PapelQuantitativo todo) {
+        exigirNaoNulo(parte1, "parte1");
+        exigirNaoNulo(parte2, "parte2");
+        exigirNaoNulo(todo, "todo");
+
+        int incognitas = 0;
+        if (parte1.ehIncognita()) incognitas++;
+        if (parte2.ehIncognita()) incognitas++;
+        if (todo.ehIncognita()) incognitas++;
+
+        if (incognitas != 1) {
+            return new ResultadoCalculo(null, null, descreverRelacao(),
+                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
+                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
+                            + "é preciso exatamente 1 para calcular um valor ausente único.",
+                    OrigemAcao.ORIGEM_SISTEMA);
+        }
+
+        if (todo.ehIncognita()) {
+            int calculado = parte1.valorAtual().valorOuNull() + parte2.valorAtual().valorOuNull();
+            return new ResultadoCalculo(todo, new NumeroInteiro(calculado), descreverRelacao(),
+                    EstadoConsistencia.CONSISTENTE, "Todo = Parte1 + Parte2",
+                    OrigemAcao.ORIGEM_SISTEMA);
+        }
+        if (parte1.ehIncognita()) {
+            int calculado = todo.valorAtual().valorOuNull() - parte2.valorAtual().valorOuNull();
+            return new ResultadoCalculo(parte1, new NumeroInteiro(calculado), descreverRelacao(),
+                    EstadoConsistencia.CONSISTENTE, "Parte1 = Todo - Parte2",
+                    OrigemAcao.ORIGEM_SISTEMA);
+        }
+        int calculado = todo.valorAtual().valorOuNull() - parte1.valorAtual().valorOuNull();
+        return new ResultadoCalculo(parte2, new NumeroInteiro(calculado), descreverRelacao(),
+                EstadoConsistencia.CONSISTENTE, "Parte2 = Todo - Parte1",
+                OrigemAcao.ORIGEM_SISTEMA);
+    }
+
+    /**
+     * Aplica um ResultadoCalculo previamente obtido ao papel que ele
+     * calculou — o passo explícito que uma camada externa decide dar. Só
+     * então o papel gera seu evento semântico, com a origem que o
+     * resultado já carregava (tipicamente ORIGEM_SISTEMA).
+     *
+     * @throws IllegalStateException se o resultado não tiver valor calculável — violação de contrato do chamador
+     */
+    public java.util.Optional<DiagnosticoErroPapel> aplicar(ResultadoCalculo resultado, ContextoAcao contexto) {
+        if (resultado == null || !resultado.temValorCalculavel()) {
+            throw new IllegalStateException(
+                    "Não há valor calculado para aplicar (estado: "
+                            + (resultado == null ? "resultado nulo" : resultado.getEstadoConsistencia()) + ")");
+        }
+        return resultado.getPapelCalculado().posicionar(resultado.getValorCalculado(), resultado.getOrigem(), contexto);
+    }
+
     private static void exigirNaoNulo(PapelQuantitativo papel, String nomeParametro) {
         if (papel == null) {
             throw new IllegalArgumentException(nomeParametro + " não pode ser nulo — violação de contrato de programação");
```

## 2. `RelacaoEstruturalComparacao.java` — arquivo novo

```java
package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural formal do esquema Comparação de Medidas:
 * Referendo = Referido + ValorRelativo.
 *
 * DISTINÇÃO CONCEITUAL OBRIGATÓRIA (ver relatório técnico do baseline v2,
 * seção "Situações, Invariantes Operatórios e Representações" — tripé
 * C=(S,I,R) de Vergnaud): esta classe NÃO representa um invariante
 * operatório. Um invariante operatório é uma proposição sobre o mundo
 * mobilizada pelo sujeito durante sua atividade — pode emergir
 * implicitamente por meio de ações, ser inferido a partir de uma sequência
 * de ações, ou ser verbalizado pelo estudante em qualquer momento da
 * tentativa. Não pertence a Referido, não pertence a Referendo, não
 * pertence a nenhum objeto gráfico específico, e não deve ser modelado
 * como uma regra formal possuída por um elemento do diagrama.
 *
 * O que esta classe modela é a organização matemática formal que relaciona
 * os papéis Referido/ValorRelativo/Referendo dentro de UMA representação —
 * pertence ao R do tripé (representações), nunca ao I (invariantes
 * operatórios). O invariante operatório em si não é e não pode ser
 * modelado por nenhuma classe deste pacote: ele é mobilizado pelo
 * estudante, não pelo sistema.
 *
 * Objeto coordenador de escopo fechado (só os três papéis deste esquema):
 * a regra cruza três conceitos, então não pertence a nenhum
 * PapelQuantitativo isolado — a mesma regra de revisão da skill
 * KnowledgeLocalityPrinciple que já justificava RelacaoEstruturalComposicao
 * e RelacaoEstruturalTransformacao continua valendo.
 *
 * calcularValorAusente NUNCA modifica o papel calculado — só devolve um
 * ResultadoCalculo com origem ORIGEM_SISTEMA. Aplicar esse valor ao papel
 * (e só então gerar o evento correspondente) é uma decisão explícita de
 * outra camada, tomada chamando aplicar(...) — nunca automática.
 */
public final class RelacaoEstruturalComparacao {

    private RelacaoEstruturalComparacao() { }

    public static RelacaoEstruturalComparacao comparacaoDeMedidas() {
        return new RelacaoEstruturalComparacao();
    }

    public String descreverRelacao() { return "Referendo = Referido + ValorRelativo"; }

    public EstadoConsistencia verificarConsistencia(PapelQuantitativo referido, PapelQuantitativo valorRelativo,
                                                      PapelQuantitativo referendo) {
        exigirNaoNulo(referido, "referido");
        exigirNaoNulo(valorRelativo, "valorRelativo");
        exigirNaoNulo(referendo, "referendo");
        if (!referido.estaPreenchido() || !valorRelativo.estaPreenchido() || !referendo.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int rd = referido.valorAtual().valorOuNull();
        int vr = valorRelativo.valorAtual().valorOuNull();
        int rn = referendo.valorAtual().valorOuNull();
        return (rn == rd + vr) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
    }

    /**
     * Calcula o valor do único papel incógnito entre os três, sem
     * modificá-lo. Se não houver exatamente uma incógnita, o resultado é
     * NAO_RESOLVIVEL_NESTE_ESTADO — não uma exceção: em uma atividade
     * pedagógica interativa, zero, duas ou três incógnitas são estados
     * legítimos (representação ainda incompleta, ou já totalmente
     * preenchida), não violações de contrato de programação. Exceção só é
     * lançada para argumento nulo, que é, de fato, um erro do chamador.
     */
    public ResultadoCalculo calcularValorAusente(PapelQuantitativo referido, PapelQuantitativo valorRelativo,
                                                  PapelQuantitativo referendo) {
        exigirNaoNulo(referido, "referido");
        exigirNaoNulo(valorRelativo, "valorRelativo");
        exigirNaoNulo(referendo, "referendo");

        int incognitas = 0;
        if (referido.ehIncognita()) incognitas++;
        if (valorRelativo.ehIncognita()) incognitas++;
        if (referendo.ehIncognita()) incognitas++;

        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (referendo.ehIncognita()) {
            int calculado = referido.valorAtual().valorOuNull() + valorRelativo.valorAtual().valorOuNull();
            return new ResultadoCalculo(referendo, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Referendo = Referido + ValorRelativo",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        if (referido.ehIncognita()) {
            int calculado = referendo.valorAtual().valorOuNull() - valorRelativo.valorAtual().valorOuNull();
            return new ResultadoCalculo(referido, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Referido = Referendo - ValorRelativo",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        int calculado = referendo.valorAtual().valorOuNull() - referido.valorAtual().valorOuNull();
        return new ResultadoCalculo(valorRelativo, new NumeroInteiro(calculado), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE, "ValorRelativo = Referendo - Referido",
                OrigemAcao.ORIGEM_SISTEMA);
    }

    /**
     * Aplica um ResultadoCalculo previamente obtido ao papel que ele
     * calculou — o passo explícito que uma camada externa decide dar. Só
     * então o papel gera seu evento semântico, com a origem que o
     * resultado já carregava (tipicamente ORIGEM_SISTEMA).
     *
     * @throws IllegalStateException se o resultado não tiver valor calculável — violação de contrato do chamador
     */
    public java.util.Optional<DiagnosticoErroPapel> aplicar(ResultadoCalculo resultado, ContextoAcao contexto) {
        if (resultado == null || !resultado.temValorCalculavel()) {
            throw new IllegalStateException(
                    "Não há valor calculado para aplicar (estado: "
                            + (resultado == null ? "resultado nulo" : resultado.getEstadoConsistencia()) + ")");
        }
        return resultado.getPapelCalculado().posicionar(resultado.getValorCalculado(), resultado.getOrigem(), contexto);
    }

    private static void exigirNaoNulo(PapelQuantitativo papel, String nomeParametro) {
        if (papel == null) {
            throw new IllegalArgumentException(nomeParametro + " não pode ser nulo — violação de contrato de programação");
        }
    }
}
```

## 3. `FabricaPapeisComparacaoMedidas.java` — arquivo novo (decidido)

**Decisão final**: Referido e Referendo com a mesma forma (`FIGURA_RETANGULAR_ARREDONDADA`), preservando a simetria entre os dois papéis — resolve a discordância sinalizada, mantendo o princípio de que papéis do mesmo tipo (medidas, `NATURAIS`, análogos a EstadoInicial/EstadoFinal em Transformação) compartilham forma, só com `FIGURA_RETANGULAR_ARREDONDADA` em vez de `FIGURA_RETANGULAR`.

```java
package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos três papéis do esquema Comparação de Medidas
 * (Referido, ValorRelativo, Referendo).
 *
 * Não altera PapelQuantitativo além do que já foi corrigido em conjunto
 * (baseline v2) — esta fábrica só usa o construtor público, que já era
 * genérico o suficiente para qualquer esquema do campo aditivo.
 */
public final class FabricaPapeisComparacaoMedidas {

    private FabricaPapeisComparacaoMedidas() { }

    public static PapelQuantitativo referido(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.referido", "Referido", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "rotulo.papel.referido"),
                publicador);
    }

    public static PapelQuantitativo valorRelativo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.valorRelativo", "Valor Relativo", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "rotulo.papel.valorRelativo"),
                publicador);
    }

    public static PapelQuantitativo referendo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.referendo", "Referendo", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "rotulo.papel.referendo"),
                publicador);
    }
}
```

## 4. `src/TestePilotoComparacaoMedidas.java` — arquivo novo (ponto em aberto)

**Omissão deliberada, aguardando confirmação**: não inclui checagem de contagem *exata* de eventos (o "teste 16" de Transformação) — só "existe ao menos um aceito"/"existe ao menos um rejeitado", para não arriscar contar errado à mão. Referido=6, ValorRelativo=8, Referendo=14; rejeição via `3 + (-10) = -7`.

```java
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComparacao;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Harness executável do piloto Comparação de Medidas — mesmo padrão de
 * TestePilotoPapelQuantitativo (Composição) e TestePilotoTransformacaoMedidas
 * (Transformação): calcularValorAusente() NUNCA modifica o papel calculado —
 * devolve um ResultadoCalculo; aplicar(...) é o passo explícito, separado,
 * que decide posicionar o valor. Todo valor aplicado por cálculo do sistema
 * carrega origem_da_acao = ORIGEM_SISTEMA no evento, nunca ORIGEM_USUARIO.
 * Zero, duas ou três incógnitas não lançam exceção — resultam em
 * NAO_RESOLVIVEL_NESTE_ESTADO, um estado explícito, não um erro.
 *
 * Não toca em Main.java nem em nenhum caminho de produção.
 */
public class TestePilotoComparacaoMedidas {

    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<>();
        PublicadorEventoDominio publicador = eventos::add;
        RelacaoEstruturalComparacao relacao = RelacaoEstruturalComparacao.comparacaoDeMedidas();
        ContextoAcao contexto = new ContextoAcao("sessao-teste-3", "usuario-local-1", "tentativa-3",
                "situacao-comparacao-6-8-14", "diagrama-vergnaud-3");
        System.out.println("relação estrutural: " + relacao.descreverRelacao());

        System.out.println();
        System.out.println("=== Testes: papéis começam como incógnita ===");
        checar("Referido começa como incógnita",
                String.valueOf(FabricaPapeisComparacaoMedidas.referido(publicador).ehIncognita()), "true");
        checar("ValorRelativo começa como incógnita",
                String.valueOf(FabricaPapeisComparacaoMedidas.valorRelativo(publicador).ehIncognita()), "true");
        checar("Referendo começa como incógnita",
                String.valueOf(FabricaPapeisComparacaoMedidas.referendo(publicador).ehIncognita()), "true");

        System.out.println();
        System.out.println("=== ValorRelativo aceita positiva, negativa e nula (domínio INTEIROS) ===");
        PapelQuantitativo v1 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        checar("valor relativo positivo (+5) é aceito", String.valueOf(v1.posicionar(new NumeroInteiro(5)).isPresent()), "false");
        PapelQuantitativo v2 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        checar("valor relativo negativo (-3) é aceito", String.valueOf(v2.posicionar(new NumeroInteiro(-3)).isPresent()), "false");
        PapelQuantitativo v3 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        checar("valor relativo nulo (0) é aceito", String.valueOf(v3.posicionar(new NumeroInteiro(0)).isPresent()), "false");

        System.out.println();
        System.out.println("=== relação estrutural CONSISTENTE (6 + 8 = 14) ===");
        PapelQuantitativo rd = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd.posicionar(new NumeroNatural(6));
        vr.posicionar(new NumeroInteiro(8));
        rn.posicionar(new NumeroNatural(14));
        checar("relação estrutural satisfeita", relacao.verificarConsistencia(rd, vr, rn).name(), "CONSISTENTE");

        System.out.println();
        System.out.println("=== Representação estruturalmente inconsistente (6 + 8 != 999) ===");
        PapelQuantitativo rdInc = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vrInc = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rnInc = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rdInc.posicionar(new NumeroNatural(6));
        vrInc.posicionar(new NumeroInteiro(8));
        rnInc.posicionar(new NumeroNatural(999));
        checar("representação estruturalmente inconsistente detectada",
                relacao.verificarConsistencia(rdInc, vrInc, rnInc).name(), "REPRESENTACAO_INCONSISTENTE");

        System.out.println();
        System.out.println("=== calcularValorAusente com Referendo desconhecido (6 + 8 = ?) ===");
        PapelQuantitativo rd2 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr2 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn2 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd2.posicionar(new NumeroNatural(6));
        vr2.posicionar(new NumeroInteiro(8));
        ResultadoCalculo resReferendo = relacao.calcularValorAusente(rd2, vr2, rn2);
        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(rn2.ehIncognita()), "true");
        checar("resultado do cálculo é CONSISTENTE", resReferendo.getEstadoConsistencia().name(), "CONSISTENTE");
        checar("origem do resultado calculado é ORIGEM_SISTEMA", resReferendo.getOrigem().name(), "ORIGEM_SISTEMA");
        Optional<DiagnosticoErroPapel> aplicReferendo = relacao.aplicar(resReferendo, contexto);
        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicReferendo.isPresent()), "false");
        checar("Referendo resolvido corretamente (14) após aplicar", String.valueOf(rn2.valorAtual().valorOuNull()), "14");

        System.out.println();
        System.out.println("=== calcularValorAusente com Referido desconhecido (? + 8 = 14) ===");
        PapelQuantitativo rd3 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr3 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn3 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        vr3.posicionar(new NumeroInteiro(8));
        rn3.posicionar(new NumeroNatural(14));
        ResultadoCalculo resReferido = relacao.calcularValorAusente(rd3, vr3, rn3);
        relacao.aplicar(resReferido, contexto);
        checar("Referido resolvido corretamente (6)", String.valueOf(rd3.valorAtual().valorOuNull()), "6");

        System.out.println();
        System.out.println("=== calcularValorAusente com ValorRelativo desconhecido (6 + ? = 14) ===");
        PapelQuantitativo rd4 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr4 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn4 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd4.posicionar(new NumeroNatural(6));
        rn4.posicionar(new NumeroNatural(14));
        ResultadoCalculo resValorRelativo = relacao.calcularValorAusente(rd4, vr4, rn4);
        relacao.aplicar(resValorRelativo, contexto);
        checar("ValorRelativo resolvido corretamente (8)", String.valueOf(vr4.valorAtual().valorOuNull()), "8");

        System.out.println();
        System.out.println("=== rejeição de Referido negativo (ação do usuário) ===");
        PapelQuantitativo rd5 = FabricaPapeisComparacaoMedidas.referido(publicador);
        Optional<DiagnosticoErroPapel> resRejReferido = rd5.posicionar(new NumeroInteiro(-2));
        checar("Referido (NATURAIS) rejeita valor negativo", String.valueOf(resRejReferido.isPresent()), "true");
        checar("Referido permanece incógnita após rejeição", String.valueOf(rd5.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== rejeição de Referendo negativo (posicionamento direto) ===");
        PapelQuantitativo rn5 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        Optional<DiagnosticoErroPapel> resRejReferendo = rn5.posicionar(new NumeroInteiro(-2));
        checar("Referendo (NATURAIS) rejeita valor negativo", String.valueOf(resRejReferendo.isPresent()), "true");

        System.out.println();
        System.out.println("=== rejeição de cálculo do SISTEMA que produziria Referendo negativo (3 + (-10) = -7) ===");
        PapelQuantitativo rd6 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr6 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn6 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd6.posicionar(new NumeroNatural(3));
        vr6.posicionar(new NumeroInteiro(-10));
        ResultadoCalculo resRej = relacao.calcularValorAusente(rd6, vr6, rn6);
        checar("cálculo em si é CONSISTENTE (o valor -7 foi calculável, mesmo que inválido para o papel)",
                resRej.getEstadoConsistencia().name(), "CONSISTENTE");
        Optional<DiagnosticoErroPapel> aplicRej = relacao.aplicar(resRej, contexto);
        checar("aplicar o cálculo que produz Referendo negativo é rejeitado pelo próprio papel",
                String.valueOf(aplicRej.isPresent()), "true");
        checar("Referendo permanece incógnita (o sistema não força um valor inválido)",
                String.valueOf(rn6.ehIncognita()), "true");

        System.out.println();
        System.out.println("=== Origem da rejeição acima é do SISTEMA, nunca do usuário (integridade do dado de pesquisa) ===");
        EventoDominio ultimoEvento = eventos.get(eventos.size() - 1);
        checar("evento da rejeição tem origem_da_acao = ORIGEM_SISTEMA",
                String.valueOf(ultimoEvento.paraMapa().get("origem_da_acao")), "ORIGEM_SISTEMA");
        checar("evento da rejeição tem resultado = REJEITADO",
                String.valueOf(ultimoEvento.paraMapa().get("resultado")), "REJEITADO");
        checar("evento carrega id_situacao_problema do contexto",
                String.valueOf(ultimoEvento.paraMapa().get("id_situacao_problema")), "situacao-comparacao-6-8-14");

        System.out.println();
        System.out.println("=== diagnóstico pedagógico da rejeição do sistema ===");
        DiagnosticoErroPapel diagnostico = aplicRej.get();
        checar("diagnóstico presente", String.valueOf(diagnostico != null), "true");
        checar("chave de mensagem", diagnostico.getChaveMensagem(), "erro.papel.valorForaDoDominio");
        checar("chave de feedback pedagógico", diagnostico.getChaveFeedbackPedagogico(),
                "feedback.papel.valorForaDoDominio");
        checar("chave de sugestão de correção", diagnostico.getChaveSugestaoCorrecao(),
                "correcao.papel.valorForaDoDominio");

        System.out.println();
        System.out.println("=== Estados incompletos/não resolvíveis não lançam exceção ===");
        PapelQuantitativo rd7 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr7 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn7 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd7.posicionar(new NumeroNatural(1));
        vr7.posicionar(new NumeroInteiro(1));
        rn7.posicionar(new NumeroNatural(2));
        ResultadoCalculo resZeroIncognitas = relacao.calcularValorAusente(rd7, vr7, rn7);
        checar("zero incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO (não é exceção)",
                resZeroIncognitas.getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("resultado sem valor calculável não tem valor calculável", String.valueOf(resZeroIncognitas.temValorCalculavel()), "false");

        boolean lancouExcecaoAoAplicarSemValor;
        try {
            relacao.aplicar(resZeroIncognitas, contexto);
            lancouExcecaoAoAplicarSemValor = false;
        } catch (IllegalStateException esperada) {
            lancouExcecaoAoAplicarSemValor = true;
        }
        checar("aplicar(...) sem valor calculável lança IllegalStateException (violação de contrato do chamador, não erro pedagógico)",
                String.valueOf(lancouExcecaoAoAplicarSemValor), "true");

        PapelQuantitativo rd8 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr8 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn8 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        rd8.posicionar(new NumeroNatural(5));
        checar("duas incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(rd8, vr8, rn8).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");

        PapelQuantitativo rd9 = FabricaPapeisComparacaoMedidas.referido(publicador);
        PapelQuantitativo vr9 = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        PapelQuantitativo rn9 = FabricaPapeisComparacaoMedidas.referendo(publicador);
        checar("três incógnitas -> NAO_RESOLVIVEL_NESTE_ESTADO",
                relacao.calcularValorAusente(rd9, vr9, rn9).getEstadoConsistencia().name(), "NAO_RESOLVIVEL_NESTE_ESTADO");
        checar("representação totalmente vazia -> REPRESENTACAO_INCOMPLETA na verificação",
                relacao.verificarConsistencia(rd9, vr9, rn9).name(), "REPRESENTACAO_INCOMPLETA");

        System.out.println();
        System.out.println("=== Eventos semânticos ===");
        long aceitos = eventos.stream().filter(e -> "ACEITO".equals(e.paraMapa().get("resultado"))).count();
        long rejeitados = eventos.stream().filter(e -> "REJEITADO".equals(e.paraMapa().get("resultado"))).count();
        System.out.println("total de eventos: " + eventos.size() + " (aceitos=" + aceitos + ", rejeitados=" + rejeitados + ")");
        checar("existe ao menos um evento de valor aceito", String.valueOf(aceitos > 0), "true");
        checar("existe ao menos um evento de valor rejeitado", String.valueOf(rejeitados > 0), "true");

        System.out.println();
        System.out.println("=== Null Object do publicador ===");
        PapelQuantitativo semPublicador = FabricaPapeisComparacaoMedidas.valorRelativo(null);
        semPublicador.posicionar(new NumeroInteiro(-4));
        checar("ValorRelativo com publicador nulo (Null Object) ainda funciona",
                String.valueOf(semPublicador.valorAtual().valorOuNull()), "-4");
        PapelQuantitativo comNenhum = FabricaPapeisComparacaoMedidas.valorRelativo(PublicadorEventoDominio.NENHUM);
        comNenhum.posicionar(new NumeroInteiro(-7));
        checar("ValorRelativo com PublicadorEventoDominio.NENHUM explícito ainda funciona",
                String.valueOf(comNenhum.valorAtual().valorOuNull()), "-7");

        System.out.println();
        System.out.println("=== Serialização por paraMapa() ===");
        System.out.println("vr2.paraMapa() = " + vr2.paraMapa());
        checar("mapa serializado traz domínio INTEIROS", String.valueOf(vr2.paraMapa().get("dominio")), "INTEIROS");
        checar("mapa serializado traz o valor correto (8)", String.valueOf(vr2.paraMapa().get("valor_atual")), "8");

        System.out.println();
        System.out.println("TODOS OS TESTES DO PILOTO DE COMPARAÇÃO DE MEDIDAS PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
```

## 5. `src/TestePilotoPapelQuantitativo.java` — adicionar 3 ramos de `calcularValorAusente`

Bloco inserido depois da seção "Null Object" e antes do print final — não mexe na checagem já existente `eventosCapturados.size()==7` (continua correta, checada antes de qualquer evento novo ser gerado). `relacao` e `contexto` já existem como variáveis locais em `main()` (linhas 29 e 45), reaproveitados.

```diff
--- a/src/TestePilotoPapelQuantitativo.java
+++ b/src/TestePilotoPapelQuantitativo.java
@@ -6,6 +6,7 @@ import gerard.dominio.campoaditivo.OrigemAcao;
 import gerard.dominio.campoaditivo.PapelQuantitativo;
 import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
+import gerard.dominio.campoaditivo.ResultadoCalculo;
 import gerard.dominio.campoaditivo.evento.EventoDominio;
 import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
 import gerard.semantica.numero.NumeroInteiro;
@@ -122,6 +123,38 @@ public class TestePilotoPapelQuantitativo {
         checar("papel construído explicitamente com PublicadorEventoDominio.NENHUM funciona",
                 String.valueOf(comNenhum.valorAtual().valorOuNull()), "9");
 
+        System.out.println();
+        System.out.println("=== calcularValorAusente com Todo desconhecido (8 + 6 = ?) ===");
+        PapelQuantitativo parte1B = PapelQuantitativo.parte1(publicador);
+        PapelQuantitativo parte2B = PapelQuantitativo.parte2(publicador);
+        PapelQuantitativo todoB = PapelQuantitativo.todo(publicador);
+        parte1B.posicionar(new NumeroNatural(8));
+        parte2B.posicionar(new NumeroNatural(6));
+        ResultadoCalculo resTodo = relacao.calcularValorAusente(parte1B, parte2B, todoB);
+        checar("cálculo não modifica o papel (ainda incógnita antes de aplicar)", String.valueOf(todoB.ehIncognita()), "true");
+        checar("resultado do cálculo é CONSISTENTE", resTodo.getEstadoConsistencia().name(), "CONSISTENTE");
+        checar("origem do resultado calculado é ORIGEM_SISTEMA", resTodo.getOrigem().name(), "ORIGEM_SISTEMA");
+        Optional<DiagnosticoErroPapel> aplicTodo = relacao.aplicar(resTodo, contexto);
+        checar("aplicar o resultado calculado é aceito", String.valueOf(aplicTodo.isPresent()), "false");
+        checar("Todo resolvido corretamente (14) após aplicar", String.valueOf(todoB.valorAtual().valorOuNull()), "14");
+
+        System.out.println();
+        System.out.println("=== calcularValorAusente com Parte1 desconhecida (? + 6 = 14) ===");
+        PapelQuantitativo parte1C = PapelQuantitativo.parte1(publicador);
+        PapelQuantitativo parte2C = PapelQuantitativo.parte2(publicador);
+        PapelQuantitativo todoC = PapelQuantitativo.todo(publicador);
+        parte2C.posicionar(new NumeroNatural(6));
+        todoC.posicionar(new NumeroNatural(14));
+        ResultadoCalculo resParte1 = relacao.calcularValorAusente(parte1C, parte2C, todoC);
+        relacao.aplicar(resParte1, contexto);
+        checar("Parte1 resolvida corretamente (8)", String.valueOf(parte1C.valorAtual().valorOuNull()), "8");
+
+        System.out.println();
+        System.out.println("=== calcularValorAusente com Parte2 desconhecida (8 + ? = 14) ===");
+        PapelQuantitativo parte1D = PapelQuantitativo.parte1(publicador);
+        PapelQuantitativo parte2D = PapelQuantitativo.parte2(publicador);
+        PapelQuantitativo todoD = PapelQuantitativo.todo(publicador);
+        parte1D.posicionar(new NumeroNatural(8));
+        todoD.posicionar(new NumeroNatural(14));
+        ResultadoCalculo resParte2 = relacao.calcularValorAusente(parte1D, parte2D, todoD);
+        relacao.aplicar(resParte2, contexto);
+        checar("Parte2 resolvida corretamente (6)", String.valueOf(parte2D.valorAtual().valorOuNull()), "6");
+
         System.out.println();
         System.out.println("TODOS OS TESTES DO PILOTO PASSARAM.");
     }
```

---

## Perguntas em aberto antes de aplicar

1. ~~Forma de `Referendo` na fábrica~~ — **decidido**: `FIGURA_RETANGULAR_ARREDONDADA` para Referido e Referendo (ver item 3 acima).
2. Contagem exata de eventos no harness de Comparação (item 4) — deixo só "pelo menos um aceito/rejeitado", ou conto e adiciono o total exato?

## Status

Nenhum arquivo tocado. Aguardando resposta à pergunta 2 e aprovação final item por item.
