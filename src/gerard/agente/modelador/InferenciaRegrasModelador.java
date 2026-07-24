package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.NivelSuporte;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import weka.associations.Apriori;
import weka.classifiers.rules.PART;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Ação 2 do Agente Modelador (agente-modelador.md): infere regras a partir
 * dos casos acumulados no Modelo do Usuário, via PART (Weka —
 * "J48.PART" na especificação, que é o próprio nome do algoritmo: PART
 * cresce árvores C4.5/J48 parciais e extrai a melhor folha como regra a
 * cada iteração) e Apriori.
 *
 * "Combinados via AND" (texto da especificação) não é implementado aqui:
 * não há, no material de pesquisa, uma definição operacional de como
 * combinar uma regra de classificação do PART com uma regra de associação
 * do Apriori — os dois conjuntos de regras são retornados lado a lado, sem
 * tentar uma junção programática que seria uma invenção não sustentada pela
 * especificação. Reportar ao usuário antes de assumir qualquer combinação
 * automática das duas saídas.
 *
 * Atributos usados: "tarefa" (categoria:papel-alvo), "regraDeAcao" (Tarefa
 * de Interação de Shneiderman — hoje POSICIONAR ou SELECIONAR, ver
 * ConectorVereditoModelador) e "suporte" (NivelSuporte, classe fixa do
 * PART). "internalizado" e "probabilidadeSaberConteudo" não entram ainda
 * porque nenhum outro agente os preenche hoje — treinar sobre uma coluna
 * constante não produziria regra nenhuma.
 *
 * ⚠️ Correção parcial, atualizada em 2026-07-23 (fonte: trecho original da
 * tese colado pelo usuário em 2026-07-22, ver "Correção: o que o Apriori
 * deve associar" em agente-modelador.md): o desenho pedido é associação
 * entre "regra de ação" e "invariante operatório" (teorema-em-ato
 * verdadeiro/falso), livre de classe fixa ("não apenas o atributo classe").
 * "regraDeAcao" já entrava desde 2026-07-22; "invarianteCodigo" passou a
 * entrar em 2026-07-23 — não é calculado por nenhum agente (isso continua
 * de fora de propósito), mas o pesquisador atribui um por ação, via
 * catálogo fechado ou forma simbólica nova (ver TelaArtefatoExplicativo,
 * DiagnosticoTarefa.getInvarianteCodigo). O Apriori abaixo agora minera
 * tarefa/regraDeAcao/suporte/invarianteCodigo — mais próximo do desenho
 * pedido (associação regra-de-ação×invariante), mas ainda depende de o
 * pesquisador ter preenchido o invariante; casos sem isso entram com o
 * valor "SEM_INVARIANTE".
 *
 * ⚠️ Ainda não existe corpus real: o projeto está em desenvolvimento, então
 * o Modelo do Usuário (ModeloUsuario.getDiagnosticos()) só acumula os casos
 * de quem de fato usar o Gérard rodando — hoje isso é pouco ou nada. Por
 * isso os dois limiares mínimos abaixo (numeroMinimoInstanciasPart e
 * numeroMinimoInstanciasApriori) existem: sem eles, PART/Apriori rodam
 * sobre poucos casos e devolvem uma "regra" trivial (ex.: uma folha só,
 * "tudo é NENHUM") que parece aprendida mas não significa nada. O teste de
 * desempenho feito em 2026-07-22 usou dados sintéticos só para validar que
 * a dependência do Weka funciona e escala — não é, e não deve ser lido
 * como, validação da qualidade de nenhuma regra.
 */
public final class InferenciaRegrasModelador {

    public static final class Resultado {
        public final String regrasPart;
        public final String regrasApriori;
        public final int quantidadeInstancias;
        public final long duracaoConstrucaoDatasetMs;
        public final long duracaoPartMs;
        public final long duracaoAprioriMs;

        Resultado(String regrasPart, String regrasApriori, int quantidadeInstancias,
                  long duracaoConstrucaoDatasetMs, long duracaoPartMs, long duracaoAprioriMs) {
            this.regrasPart = regrasPart;
            this.regrasApriori = regrasApriori;
            this.quantidadeInstancias = quantidadeInstancias;
            this.duracaoConstrucaoDatasetMs = duracaoConstrucaoDatasetMs;
            this.duracaoPartMs = duracaoPartMs;
            this.duracaoAprioriMs = duracaoAprioriMs;
        }

        public long duracaoTotalMs() {
            return duracaoConstrucaoDatasetMs + duracaoPartMs + duracaoAprioriMs;
        }
    }

    /**
     * @param numeroMinimoInstanciasPart abaixo desse limiar, PART é pulado
     *        (regrasPart explica o motivo) em vez de treinar sobre um
     *        corpus pequeno demais para produzir uma regra que signifique
     *        algo além de "a classe mais comum nesses poucos casos".
     * @param numeroMinimoInstanciasApriori Apriori do Weka lança exceção
     *        quando não há instâncias suficientes para o suporte mínimo
     *        padrão; abaixo desse limiar, Apriori é pulado (regrasApriori
     *        explica o motivo) em vez de propagar a exceção.
     */
    public Resultado inferir(List<DiagnosticoTarefa> diagnosticos, int numeroMinimoInstanciasPart,
                              int numeroMinimoInstanciasApriori) throws Exception {
        long t0 = System.nanoTime();
        Instances dados = construirDataset(diagnosticos);
        long t1 = System.nanoTime();

        String regrasPart;
        long t2;
        if (dados.numInstances() < numeroMinimoInstanciasPart) {
            regrasPart = "Sem corpus suficiente para treinar PART com significado (mínimo "
                    + numeroMinimoInstanciasPart + ", há " + dados.numInstances() + "). Projeto ainda em "
                    + "desenvolvimento, sem base de dados real acumulada.";
            t2 = System.nanoTime();
        } else {
            dados.setClassIndex(dados.attribute("suporte").index());
            PART part = new PART();
            part.buildClassifier(dados);
            regrasPart = part.toString();
            t2 = System.nanoTime();
        }

        String regrasApriori;
        long t3;
        if (dados.numInstances() < numeroMinimoInstanciasApriori) {
            regrasApriori = "Sem casos suficientes para Apriori (mínimo " + numeroMinimoInstanciasApriori
                    + ", há " + dados.numInstances() + ").";
            t3 = System.nanoTime();
        } else {
            Apriori apriori = new Apriori();
            apriori.buildAssociations(dados);
            regrasApriori = apriori.toString();
            t3 = System.nanoTime();
        }

        return new Resultado(
                regrasPart,
                regrasApriori,
                dados.numInstances(),
                (t1 - t0) / 1_000_000L,
                (t2 - t1) / 1_000_000L,
                (t3 - t2) / 1_000_000L
        );
    }

    private static final String SEM_INVARIANTE = "SEM_INVARIANTE";

    private Instances construirDataset(List<DiagnosticoTarefa> diagnosticos) {
        Set<String> tarefasDistintas = new LinkedHashSet<String>();
        Set<String> regrasDeAcaoDistintas = new LinkedHashSet<String>();
        Set<String> invariantesDistintos = new LinkedHashSet<String>();
        invariantesDistintos.add(SEM_INVARIANTE);
        for (DiagnosticoTarefa d : diagnosticos) {
            tarefasDistintas.add(d.getTarefa() == null ? "" : d.getTarefa());
            regrasDeAcaoDistintas.add(d.getRegraDeAcao() == null ? "" : d.getRegraDeAcao());
            invariantesDistintos.add(valorInvarianteOu(d, SEM_INVARIANTE));
        }

        ArrayList<String> valoresTarefa = new ArrayList<String>(tarefasDistintas);
        ArrayList<String> valoresRegraDeAcao = new ArrayList<String>(regrasDeAcaoDistintas);
        ArrayList<String> valoresInvariante = new ArrayList<String>(invariantesDistintos);
        ArrayList<String> valoresSuporte = new ArrayList<String>();
        for (NivelSuporte nivel : NivelSuporte.values()) {
            valoresSuporte.add(nivel.name());
        }

        Attribute atributoTarefa = new Attribute("tarefa", valoresTarefa);
        Attribute atributoRegraDeAcao = new Attribute("regraDeAcao", valoresRegraDeAcao);
        Attribute atributoInvariante = new Attribute("invarianteCodigo", valoresInvariante);
        Attribute atributoSuporte = new Attribute("suporte", valoresSuporte);

        ArrayList<Attribute> atributos = new ArrayList<Attribute>();
        atributos.add(atributoTarefa);
        atributos.add(atributoRegraDeAcao);
        atributos.add(atributoInvariante);
        atributos.add(atributoSuporte);

        Instances dados = new Instances("diagnosticosModeloUsuario", atributos, diagnosticos.size());
        for (DiagnosticoTarefa d : diagnosticos) {
            Instance instancia = new DenseInstance(atributos.size());
            instancia.setDataset(dados);
            instancia.setValue(atributoTarefa, d.getTarefa() == null ? "" : d.getTarefa());
            instancia.setValue(atributoRegraDeAcao, d.getRegraDeAcao() == null ? "" : d.getRegraDeAcao());
            instancia.setValue(atributoInvariante, valorInvarianteOu(d, SEM_INVARIANTE));
            NivelSuporte suporte = d.getSuporte();
            instancia.setValue(atributoSuporte, suporte == null ? NivelSuporte.NENHUM.name() : suporte.name());
            dados.add(instancia);
        }
        return dados;
    }

    private String valorInvarianteOu(DiagnosticoTarefa d, String semInvariante) {
        String codigo = d.getInvarianteCodigo();
        return codigo == null || codigo.trim().length() == 0 ? semInvariante : codigo;
    }
}
