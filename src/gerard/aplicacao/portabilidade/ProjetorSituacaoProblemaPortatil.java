package gerard.aplicacao.portabilidade;

import gerard.dominio.campoaditivo.DescritorRepresentacaoPapel;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.situacao.CriterioOperacaoModelagem;
import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.EstadoNarrativo;
import gerard.dominio.campoaditivo.situacao.EventoNarrativoCurado;
import gerard.dominio.campoaditivo.situacao.InventarioNarrativo;
import gerard.dominio.campoaditivo.situacao.MarcadorTemporal;
import gerard.dominio.campoaditivo.situacao.ObjetoContado;
import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.RelacaoEstruturalVinculada;
import gerard.dominio.campoaditivo.situacao.ResultadoValidacaoSituacao;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Produz uma fotografia de transporte independente de Swing, AWT e da
 * tecnologia que realizará a representação.
 *
 * A projeção não calcula relações, não corrige a curadoria e não decide
 * layout. Ela apenas transporta conhecimento que os proprietários semânticos
 * já validaram. O campo {@code schema} versiona explicitamente o contrato.
 */
public final class ProjetorSituacaoProblemaPortatil {
    public static final String SCHEMA = "gerard.situacao-problema.portatil.v1";

    public Map<String, Object> projetar(SituacaoProblema situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("situação-problema é obrigatória");
        }
        ResultadoValidacaoSituacao validacao = situacao.validar();
        if (!validacao.ehValida()) {
            throw new IllegalStateException(
                    "situação inconsistente não pode ser projetada: "
                            + validacao.getDiagnosticos());
        }

        Map<String, Object> raiz = mapa();
        raiz.put("schema", SCHEMA);
        raiz.put("situacao_id", situacao.getId());
        raiz.put("status_curadoria", situacao.getStatusCuradoria().name());
        raiz.put("referencias_curadas",
                new ArrayList<String>(situacao.getReferenciasCuradas()));
        raiz.put("categoria", situacao.getEstrutura().getCategoria().name());
        raiz.put("papel_desconhecido_original",
                situacao.getEstrutura().getChavePapelDesconhecidoOriginal());
        raiz.put("papeis", projetarPapeis(situacao));
        raiz.put("relacoes", projetarRelacoes(situacao));
        raiz.put("criterios_operacao", projetarCriterios(situacao));
        raiz.put("correspondencias", projetarCorrespondencias(situacao));
        raiz.put("narrativa", projetarNarrativa(situacao));
        return congelarMapa(raiz);
    }

    private List<Object> projetarPapeis(SituacaoProblema situacao) {
        List<Object> papeis = lista();
        String incognita =
                situacao.getEstrutura().getChavePapelDesconhecidoOriginal();
        for (PapelQuantitativo papel
                : situacao.getEstrutura().getPapeis().values()) {
            Map<String, Object> item = mapa();
            item.put("id", papel.getChave());
            item.put("nome_conceitual", papel.getNomeConceitual());
            item.put("dominio_numerico", papel.getDominio().name());
            item.put("conhecido", Boolean.valueOf(papel.estaPreenchido()));
            item.put("valor", papel.estaPreenchido()
                    ? papel.valorAtual().valorOuNull() : null);
            item.put("incognita_original",
                    Boolean.valueOf(papel.getChave().equals(incognita)));
            item.put("requer_representacao_de_sinal",
                    Boolean.valueOf(papel.necessitaRepresentacaoDeSinal()));
            item.put("representacao_abstrata",
                    projetarDescritor(papel.descritorRepresentacao()));
            papeis.add(item);
        }
        return papeis;
    }

    private static Map<String, Object> projetarDescritor(
            DescritorRepresentacaoPapel descritor) {
        Map<String, Object> item = mapa();
        item.put("forma", descritor.getForma().name());
        item.put("simbolo", descritor.getSimbolo());
        item.put("chave_rotulo", descritor.getChaveRotulo());
        item.put("chave_explicacao_conceitual",
                descritor.getChaveExplicacaoConceitual());
        return item;
    }

    private static List<Object> projetarRelacoes(SituacaoProblema situacao) {
        List<Object> relacoes = lista();
        for (RelacaoEstruturalVinculada relacao
                : situacao.getEstrutura().getRelacoes()) {
            Map<String, Object> item = mapa();
            item.put("id", relacao.getChave());
            item.put("descricao", relacao.descreverRelacao());
            List<Object> papeis = lista();
            for (PapelQuantitativo papel : relacao.getPapeis()) {
                papeis.add(papel.getChave());
            }
            item.put("papeis", papeis);
            relacoes.add(item);
        }
        return relacoes;
    }

    private static List<Object> projetarCriterios(SituacaoProblema situacao) {
        List<Object> criterios = lista();
        for (CriterioOperacaoModelagem criterio
                : situacao.getEstrutura().getCriteriosOperacao()) {
            Map<String, Object> item = mapa();
            item.put("id", criterio.getChave());
            item.put("operacao_esperada",
                    criterio.getOperacaoEsperada().name());
            item.put("papeis", new ArrayList<String>(
                    criterio.getChavesPapeisEnvolvidos()));
            criterios.add(item);
        }
        return criterios;
    }

    private static List<Object> projetarCorrespondencias(
            SituacaoProblema situacao) {
        List<Object> correspondencias = lista();
        for (CorrespondenciaPapelNarrativa correspondencia
                : situacao.getCorrespondencias()) {
            ReferenciaValorNarrativo referencia =
                    correspondencia.getReferencia();
            Map<String, Object> item = mapa();
            item.put("papel_id", correspondencia.getChavePapel());
            item.put("tipo_referencia", referencia.getTipo().name());
            item.put("participante_id", id(referencia.getParticipante()));
            item.put("participante_comparado_id",
                    id(referencia.getParticipanteComparado()));
            item.put("familia_id", referencia.getFamilia().getId());
            item.put("objeto_id", referencia.getObjeto() == null
                    ? null : referencia.getObjeto().getId());
            item.put("evento_id", vazioComoNulo(referencia.getChaveEvento()));
            correspondencias.add(item);
        }
        return correspondencias;
    }

    private static Map<String, Object> projetarNarrativa(
            SituacaoProblema situacao) {
        Map<String, Object> narrativa = mapa();
        narrativa.put("contexto", situacao.getNarrativa().getContexto());
        narrativa.put("estado_inicial",
                projetarEstado(situacao.getNarrativa().getEstadoInicial()));
        List<Object> eventos = lista();
        for (EventoNarrativoCurado evento
                : situacao.getNarrativa().getEventos()) {
            eventos.add(projetarEvento(evento));
        }
        narrativa.put("eventos", eventos);
        narrativa.put("estado_final_declarado", projetarEstado(
                situacao.getNarrativa().getEstadoFinalDeclarado()));
        return narrativa;
    }

    private static Map<String, Object> projetarEstado(EstadoNarrativo estado) {
        Map<String, Object> resultado = mapa();
        resultado.put("marcador", projetarMarcador(estado.getMarcador()));
        List<Object> inventarios = lista();
        for (Map.Entry<ParticipanteNarrativo, InventarioNarrativo> entrada
                : estado.getInventarios().entrySet()) {
            Map<String, Object> inventario = mapa();
            inventario.put("participante_id", entrada.getKey().getId());
            inventario.put("participante_nome",
                    entrada.getKey().getNomeExibicao());
            List<Object> itens = lista();
            for (Map.Entry<ObjetoContado, NumeroNatural> item
                    : entrada.getValue().getQuantidades().entrySet()) {
                Map<String, Object> quantidade = projetarObjeto(item.getKey());
                quantidade.put("quantidade", Integer.valueOf(
                        item.getValue().intValue()));
                itens.add(quantidade);
            }
            inventario.put("itens", itens);
            inventarios.add(inventario);
        }
        resultado.put("inventarios", inventarios);
        return resultado;
    }

    private static Map<String, Object> projetarEvento(
            EventoNarrativoCurado evento) {
        Map<String, Object> item = mapa();
        item.put("tipo", evento.getTipo().name());
        item.put("marcador", projetarMarcador(evento.getMarcador()));
        item.put("origem_id", id(evento.getOrigem()));
        item.put("destino_id", id(evento.getDestino()));
        item.put("objeto", projetarObjeto(evento.getObjeto()));
        item.put("quantidade", Integer.valueOf(
                evento.getQuantidade().intValue()));
        return item;
    }

    private static Map<String, Object> projetarMarcador(
            MarcadorTemporal marcador) {
        Map<String, Object> item = mapa();
        item.put("ordem", Integer.valueOf(marcador.getOrdem()));
        item.put("chave", marcador.getChave());
        return item;
    }

    private static Map<String, Object> projetarObjeto(ObjetoContado objeto) {
        Map<String, Object> item = mapa();
        item.put("id", objeto.getId());
        item.put("familia_id", objeto.getFamilia().getId());
        item.put("familia_nome", objeto.getFamilia().getNomeConceitual());
        item.put("caracteristicas",
                new LinkedHashMap<String, String>(objeto.getCaracteristicas()));
        item.put("chave_visual_abstrata", objeto.getChaveVisualAbstrata());
        return item;
    }

    private static String id(ParticipanteNarrativo participante) {
        return participante == null ? null : participante.getId();
    }

    private static String vazioComoNulo(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor;
    }

    private static Map<String, Object> mapa() {
        return new LinkedHashMap<String, Object>();
    }

    private static List<Object> lista() {
        return new ArrayList<Object>();
    }

    @SuppressWarnings("unchecked")
    private static Object congelar(Object valor) {
        if (valor instanceof Map) {
            return congelarMapa((Map<String, Object>) valor);
        }
        if (valor instanceof List) {
            List<Object> copia = lista();
            for (Object item : (List<Object>) valor) {
                copia.add(congelar(item));
            }
            return Collections.unmodifiableList(copia);
        }
        return valor;
    }

    private static Map<String, Object> congelarMapa(
            Map<String, Object> origem) {
        Map<String, Object> copia = mapa();
        for (Map.Entry<String, Object> entrada : origem.entrySet()) {
            copia.put(entrada.getKey(), congelar(entrada.getValue()));
        }
        return Collections.unmodifiableMap(copia);
    }
}
