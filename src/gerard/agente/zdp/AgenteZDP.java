package gerard.agente.zdp;

import gerard.agente.conhecimento.Conclusao;
import gerard.agente.conhecimento.LeitorBaseConhecimentoGerard;
import gerard.agente.conhecimento.MotorRegrasConhecimento;
import gerard.agente.conhecimento.RegraConhecimento;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Agente Baseado em Modelo descrito em gerard-ajuda-adaptativa/references/
 * agente-zdp.md: decide a estratégia pedagógica a partir da Ação
 * Instrumental Avaliada (percebida do Agente Monitor).
 *
 * Implementa só a parte diretamente derivável da recorrência de erro/acerto
 * numa mesma tarefa — as regras da Tabela 50 do relatório de pesquisa 2026
 * "primeiro erro", "erro repetido" e "acerto após erro" (camadas N0-N2 e o
 * sinal de retirada progressiva). As camadas N3+ (ajuda representacional,
 * ponte entre representações, antecipação) exigem um catálogo de conteúdo
 * pedagógico e análise de padrões entre problemas que ainda não foram
 * decididos, e ficam de fora de propósito — mesmo critério já usado para
 * adiar a ação 2 do Agente Modelador.
 *
 * Síncrono, chamado diretamente após o Agente Monitor avaliar — não é uma
 * Thread produtor-consumidor (a especificação original descreve os agentes
 * assim, mas o Agente Monitor e a ação 1 do Agente Modelador já foram
 * implementados de forma síncrona; este agente segue o mesmo padrão já em
 * produção, por decisão explícita do usuário em 2026-07-22).
 *
 * Estado interno: só a contagem de erros consecutivos por (usuário, tarefa)
 * — é o "estado atual do mundo" mínimo que o ZDP precisa guardar para
 * distinguir "primeiro erro" de "erro repetido" (ver agente-zdp.md). Não lê
 * nem escreve o Modelo do Usuário; isso fica para quando as camadas N3+
 * forem decididas.
 */
public final class AgenteZDP {
    private final Map<String, Integer> errosConsecutivosPorTarefa = new HashMap<String, Integer>();
    private final List<OuvinteEstrategiaAgenteZDP> ouvintes = new ArrayList<OuvinteEstrategiaAgenteZDP>();

    // Base de conhecimento integrada (ver dados/base_conhecimento_gerard_jsons/
    // base_conhecimento_gerard/regras_pedagogicas.jsonl) — consultada só por
    // consultarConhecimento, abaixo. Estado próprio, separado de
    // errosConsecutivosPorTarefa, para consultarConhecimento poder ser
    // chamado de forma independente de decidirEstrategia, sem acoplar a
    // ordem das duas chamadas (decidirEstrategia continua com as 4 camadas
    // N0-N2 exatamente como estavam).
    private final MotorRegrasConhecimento motorRegrasConhecimento = new MotorRegrasConhecimento();
    private final List<RegraConhecimento> regrasPedagogicas = new LeitorBaseConhecimentoGerard().lerPedagogicas();
    private final Map<String, Integer> errosConsecutivosParaConhecimento = new HashMap<String, Integer>();
    private final Map<String, Integer> errosTotaisPorTarefa = new HashMap<String, Integer>();
    private final Set<String> tarefasComAjuda = new HashSet<String>();
    private final List<OuvinteAuditoriaAgenteZDP> ouvintesAuditoria = new ArrayList<OuvinteAuditoriaAgenteZDP>();

    // Idempotência por gesto (rodada 3, 2026-07-31) — mesmo padrão já usado
    // em AgenteModelador.armazenarCaso: protege errosConsecutivosPorTarefa/
    // errosConsecutivosParaConhecimento/errosTotaisPorTarefa/tarefasComAjuda
    // de mutar mais de uma vez para o MESMO gesto do usuário, achado ao
    // investigar Jamile S9 (um único arraste da interrogação disparava
    // mouseReleased mais de uma vez em Main.java, por mecanismo ainda não
    // identificado — ver RELATORIO_AUDITORIA_MULTIAGENTE — cada disparo
    // chamava decidirEstrategia de novo, com o MESMO idempotencyKey de
    // AgentAuditService; sem isto, só a inserção do caso no Modelador era
    // protegida, não a decisão do ZDP em si).
    private final Set<String> chavesIdempotenciaProcessadas = new HashSet<String>();
    private final Map<String, CamadaEstrategiaZDP> ultimaEstrategiaPorChaveIdempotencia =
            new HashMap<String, CamadaEstrategiaZDP>();

    public void adicionarOuvinte(OuvinteEstrategiaAgenteZDP ouvinte) {
        if (ouvinte != null) {
            ouvintes.add(ouvinte);
        }
    }

    public void removerOuvinte(OuvinteEstrategiaAgenteZDP ouvinte) {
        ouvintes.remove(ouvinte);
    }

    /** Canal adicional pro log estruturado de auditoria — ver OuvinteAuditoriaAgenteZDP. */
    public void adicionarOuvinteAuditoria(OuvinteAuditoriaAgenteZDP ouvinte) {
        if (ouvinte != null) {
            ouvintesAuditoria.add(ouvinte);
        }
    }

    public void removerOuvinteAuditoria(OuvinteAuditoriaAgenteZDP ouvinte) {
        ouvintesAuditoria.remove(ouvinte);
    }

    /**
     * Decide a estratégia pedagógica para a ação já avaliada pelo Agente
     * Monitor (certo/errado) numa dada tarefa (categoria + papel-alvo).
     */
    public CamadaEstrategiaZDP decidirEstrategia(String idUsuario, TipoSituacaoAditiva categoria,
                                                  String chavePapelAlvo, boolean correto) {
        return decidirEstrategia(idUsuario, categoria, chavePapelAlvo, correto, null);
    }

    /**
     * Sobrecarga com idempotência por gesto (rodada 3, 2026-07-31): quando
     * {@code idempotencyKey} já foi processada, devolve a MESMA estratégia
     * decidida da primeira vez, sem mutar
     * errosConsecutivosPorTarefa/errosConsecutivosParaConhecimento/
     * errosTotaisPorTarefa/tarefasComAjuda de novo — protege o estado
     * pedagógico real de gestos duplicados (mesmo gesture_id) do jeito que
     * {@code AgenteModelador.armazenarCaso} já protege a base de casos.
     */
    public CamadaEstrategiaZDP decidirEstrategia(String idUsuario, TipoSituacaoAditiva categoria,
                                                  String chavePapelAlvo, boolean correto, String idempotencyKey) {
        if (idempotencyKey != null && chavesIdempotenciaProcessadas.contains(idempotencyKey)) {
            CamadaEstrategiaZDP anterior = ultimaEstrategiaPorChaveIdempotencia.get(idempotencyKey);
            CamadaEstrategiaZDP resultado = anterior == null ? CamadaEstrategiaZDP.CONDUCAO_MINIMA : anterior;
            // Ainda notifica o canal de auditoria (com o cálculo PURO, sem
            // mutar estado — mesma função que avaliarSemAlterarEstado usa
            // pra reavaliação reativa) pra este subevento duplicado não
            // ficar com agents.ZDP vazio no log — só não mexe de novo em
            // errosConsecutivosPorTarefa/errosConsecutivosParaConhecimento/
            // errosTotaisPorTarefa/tarefasComAjuda.
            if (!ouvintesAuditoria.isEmpty()) {
                ZdpAuditData hipotetico = avaliarSemAlterarEstado(idUsuario, categoria, chavePapelAlvo, correto);
                for (OuvinteAuditoriaAgenteZDP ouvinte : ouvintesAuditoria) {
                    ouvinte.aoDecidir(hipotetico);
                }
            }
            return resultado;
        }

        long inicio = System.currentTimeMillis();
        String chave = chaveTarefa(idUsuario, categoria, chavePapelAlvo);
        Integer errosAnteriores = errosConsecutivosPorTarefa.get(chave);
        int errosAtuais = errosAnteriores == null ? 0 : errosAnteriores.intValue();

        CamadaEstrategiaZDP estrategia = calcularCamada(errosAtuais, correto);
        if (!correto) {
            errosConsecutivosPorTarefa.put(chave, Integer.valueOf(errosAtuais + 1));
        } else {
            errosConsecutivosPorTarefa.remove(chave);
        }
        if (idempotencyKey != null) {
            chavesIdempotenciaProcessadas.add(idempotencyKey);
            ultimaEstrategiaPorChaveIdempotencia.put(idempotencyKey, estrategia);
        }
        for (OuvinteEstrategiaAgenteZDP ouvinte : ouvintes) {
            ouvinte.aoDecidir(idUsuario, categoria, chavePapelAlvo, correto, estrategia);
        }

        if (!ouvintesAuditoria.isEmpty()) {
            notificarAuditoria(idUsuario, categoria, chavePapelAlvo, correto, estrategia,
                    System.currentTimeMillis() - inicio);
        }
        return estrategia;
    }

    /**
     * Monta e emite ZdpAuditData pra decisão que decidirEstrategia acabou de
     * tomar — chama consultarConhecimento (R-PED-001/002) pra ter regras de
     * verdade no log, o que também é a única chamada real deste método no
     * fluxo de produção hoje (antes desta auditoria, nada chamava
     * consultarConhecimento fora de teste pontual — os contadores
     * errosConsecutivosParaConhecimento/errosTotaisPorTarefa/tarefasComAjuda
     * passam a ficar corretos em uso real por causa disso, não só em teste).
     */
    private void notificarAuditoria(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo,
            boolean correto, CamadaEstrategiaZDP estrategia, long processingTimeMs) {
        String chave = chaveTarefa(idUsuario, categoria, chavePapelAlvo);
        int consecutivosAntes = valorOuZero(errosConsecutivosParaConhecimento.get(chave));
        int totalAntes = valorOuZero(errosTotaisPorTarefa.get(chave));
        boolean ajudaPreviaAntes = tarefasComAjuda.contains(chave);

        ResultadoConsultaConhecimento resultado = consultarConhecimento(idUsuario, categoria, chavePapelAlvo, correto);

        int consecutivosDepois = valorOuZero(errosConsecutivosParaConhecimento.get(chave));
        int totalDepois = valorOuZero(errosTotaisPorTarefa.get(chave));
        boolean ajudaPreviaDepois = tarefasComAjuda.contains(chave);

        List<gerard.agente.conhecimento.RuleActivationAudit> regrasAtivadas =
                new ArrayList<gerard.agente.conhecimento.RuleActivationAudit>();
        if (resultado.getRuleId() != null) {
            for (RegraConhecimento regra : regrasPedagogicas) {
                if (regra.getRuleId().equals(resultado.getRuleId())) {
                    regrasAtivadas.add(gerard.agente.conhecimento.RuleActivationAudit.deRegraQueBateu(
                            regra, true, null));
                    break;
                }
            }
        }

        ZdpAuditData dados = new ZdpAuditData(
                "MONITOR_ACAO_AVALIADA", correto ? "C" : "E",
                consecutivosAntes, consecutivosDepois, totalAntes, totalDepois, ajudaPreviaAntes, ajudaPreviaDepois,
                resultado.getIntervencaoSugerida(),
                resultado.temIntervencao() ? 1 : 0,
                false,
                estrategia == CamadaEstrategiaZDP.AJUDA_ESPECIFICA,
                true,
                resultado.temIntervencao()
                        ? resultado.getExplicacao()
                        : "Nenhuma regra pedagógica por contagem de erro (R-PED-001/002) bateu para este estado "
                                + "(erros_consecutivos=" + consecutivosDepois + ", historico_erros=" + totalDepois
                                + ", ajuda_previa=" + ajudaPreviaDepois + ") — camada decidida só pela lógica "
                                + "N0-N2 já existente (ver CamadaEstrategiaZDP).",
                regrasAtivadas,
                String.valueOf(estrategia),
                processingTimeMs, null);
        for (OuvinteAuditoriaAgenteZDP ouvinte : ouvintesAuditoria) {
            ouvinte.aoDecidir(dados);
        }
    }

    private int valorOuZero(Integer valor) {
        return valor == null ? 0 : valor.intValue();
    }

    /**
     * Lógica pura das camadas N0-N2 (Tabela 50) — extraída pra ser
     * reaproveitada tanto por decidirEstrategia (real, muta estado) quanto
     * por avaliarSemAlterarEstado (dry-run, não muta). Único lugar que
     * decide a camada; os dois métodos só diferem em SE gravam o resultado.
     */
    private CamadaEstrategiaZDP calcularCamada(int errosAtuaisAntes, boolean correto) {
        if (!correto) {
            return errosAtuaisAntes == 0 ? CamadaEstrategiaZDP.QUESTIONAMENTO_LEVE
                    : CamadaEstrategiaZDP.AJUDA_ESPECIFICA;
        }
        return errosAtuaisAntes > 0 ? CamadaEstrategiaZDP.RETIRADA_PROGRESSIVA
                : CamadaEstrategiaZDP.CONDUCAO_MINIMA;
    }

    /**
     * Avalia R-PED-001/002 pra um estado hipotético de contadores (sem ler
     * nem escrever nenhum campo da classe) — lógica pura reaproveitada por
     * consultarConhecimento (real) e avaliarSemAlterarEstado (dry-run).
     */
    private ResultadoConsultaConhecimento avaliarRegrasPedagogicasPuro(int consecutivos, int total,
            boolean ajudaPrevia) {
        Map<String, Object> fatos = new LinkedHashMap<String, Object>();
        fatos.put("erros_consecutivos", Integer.valueOf(consecutivos));
        fatos.put("historico_erros", Integer.valueOf(total));
        fatos.put("ajuda_previa", Boolean.valueOf(ajudaPrevia));

        List<RegraConhecimento> regrasQueBatem = motorRegrasConhecimento.regrasQueBatem(fatos, regrasPedagogicas);
        if (regrasQueBatem.isEmpty()) {
            return ResultadoConsultaConhecimento.semIntervencao();
        }
        RegraConhecimento vencedora = regrasQueBatem.get(0);
        String intervencao = null;
        String risco = null;
        for (Conclusao conclusao : vencedora.getConclusoes()) {
            if ("intervencao".equals(conclusao.getCampo())) {
                intervencao = String.valueOf(conclusao.getValor());
            } else if ("risco_novo_erro".equals(conclusao.getCampo())) {
                risco = String.valueOf(conclusao.getValor());
            }
        }
        return new ResultadoConsultaConhecimento(vencedora.getRuleId(), intervencao, risco, vencedora.getExplicacao());
    }

    /**
     * Modo "avaliar sem gravar": monta o ZdpAuditData que decidirEstrategia
     * montaria pra este estado, SEM mutar errosConsecutivosPorTarefa/
     * errosConsecutivosParaConhecimento/errosTotaisPorTarefa/tarefasComAjuda.
     * Usado só pra eventos reativos (reavaliações de consistência que não
     * são o gesto canônico do usuário) — ver Main.java, OrigemAvaliacao. A
     * decisão que aparece aqui é hipotética ("o que teria acontecido"), não
     * uma decisão real do ZDP; nunca é gravada nem afeta decidirEstrategia.
     */
    public ZdpAuditData avaliarSemAlterarEstado(String idUsuario, TipoSituacaoAditiva categoria,
            String chavePapelAlvo, boolean correto) {
        long inicio = System.currentTimeMillis();
        String chave = chaveTarefa(idUsuario, categoria, chavePapelAlvo);

        int errosAtuais = valorOuZero(errosConsecutivosPorTarefa.get(chave));
        CamadaEstrategiaZDP estrategiaHipotetica = calcularCamada(errosAtuais, correto);

        int consecutivosAntes = valorOuZero(errosConsecutivosParaConhecimento.get(chave));
        int totalAntes = valorOuZero(errosTotaisPorTarefa.get(chave));
        boolean ajudaPrevia = tarefasComAjuda.contains(chave);
        int consecutivosHipoteticos = correto ? 0 : consecutivosAntes + 1;
        int totalHipotetico = correto ? totalAntes : totalAntes + 1;

        ResultadoConsultaConhecimento resultado =
                avaliarRegrasPedagogicasPuro(consecutivosHipoteticos, totalHipotetico, ajudaPrevia);

        List<gerard.agente.conhecimento.RuleActivationAudit> regrasAtivadas =
                new ArrayList<gerard.agente.conhecimento.RuleActivationAudit>();
        if (resultado.getRuleId() != null) {
            for (RegraConhecimento regra : regrasPedagogicas) {
                if (regra.getRuleId().equals(resultado.getRuleId())) {
                    regrasAtivadas.add(gerard.agente.conhecimento.RuleActivationAudit.deRegraQueBateu(
                            regra, true, null));
                    break;
                }
            }
        }

        return new ZdpAuditData(
                "REAVALIACAO_REATIVA_NAO_PERSISTIDA", correto ? "C" : "E",
                consecutivosAntes, consecutivosHipoteticos, totalAntes, totalHipotetico, ajudaPrevia, ajudaPrevia,
                resultado.getIntervencaoSugerida(),
                resultado.temIntervencao() ? 1 : 0,
                false,
                estrategiaHipotetica == CamadaEstrategiaZDP.AJUDA_ESPECIFICA,
                true,
                "Avaliação HIPOTÉTICA — evento reativo, não persistido, não afeta o estado real do ZDP. "
                        + (resultado.temIntervencao() ? resultado.getExplicacao()
                                : "Nenhuma regra pedagógica bateria para este estado hipotético."),
                regrasAtivadas,
                estrategiaHipotetica + " (hipotética)",
                System.currentTimeMillis() - inicio, null);
    }

    /**
     * Consulta as regras pedagógicas por contagem de erro da base de
     * conhecimento integrada (R-PED-001: >=2 erros consecutivos sem ajuda
     * ainda → ajuda_conceitual; R-PED-002: 3 a 5 erros totais na tarefa sem
     * ajuda ainda → mudar_modalidade_de_ajuda, risco_novo_erro=alto). As
     * outras 12 regras de regras_pedagogicas.jsonl ficam carregadas mas não
     * batem nunca aqui — dependem de fatos que este método não fornece
     * (estrategia_detectada, houve_feedback, tarefa_concluida etc.), o que é
     * intencional: só fica inerte, não lança erro.
     *
     * Método aditivo: não lê nem escreve o estado de decidirEstrategia, e
     * não influencia a CamadaEstrategiaZDP retornada por ele.
     */
    public ResultadoConsultaConhecimento consultarConhecimento(String idUsuario, TipoSituacaoAditiva categoria,
            String chavePapelAlvo, boolean correto) {
        String chave = chaveTarefa(idUsuario, categoria, chavePapelAlvo);

        Integer consecutivosAnteriores = errosConsecutivosParaConhecimento.get(chave);
        int consecutivosAtuais = consecutivosAnteriores == null ? 0 : consecutivosAnteriores.intValue();
        Integer totalAnterior = errosTotaisPorTarefa.get(chave);
        int totalAtual = totalAnterior == null ? 0 : totalAnterior.intValue();

        if (correto) {
            errosConsecutivosParaConhecimento.remove(chave);
        } else {
            consecutivosAtuais = consecutivosAtuais + 1;
            totalAtual = totalAtual + 1;
            errosConsecutivosParaConhecimento.put(chave, Integer.valueOf(consecutivosAtuais));
            errosTotaisPorTarefa.put(chave, Integer.valueOf(totalAtual));
        }

        ResultadoConsultaConhecimento resultado = avaliarRegrasPedagogicasPuro(
                consecutivosAtuais, totalAtual, tarefasComAjuda.contains(chave));
        if (resultado.temIntervencao()) {
            tarefasComAjuda.add(chave);
        }
        return resultado;
    }

    private String chaveTarefa(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo) {
        return (idUsuario == null ? "" : idUsuario) + "|"
                + (categoria == null ? "" : categoria.name()) + ":"
                + (chavePapelAlvo == null ? "" : chavePapelAlvo);
    }
}
