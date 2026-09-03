package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalDiagnosticavel;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.dominio.campoaditivo.situacao.EstruturaAditiva;
import gerard.dominio.campoaditivo.situacao.RelacaoEstruturalVinculada;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import gerard.semantica.numero.NumeroInteiro;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Tentativa web criada exclusivamente de uma situação rica validada. */
public final class ServicoAtividadeWebTransformacaoRelacaoRica
        implements ServicoAtividadeWeb {
    private final String tentativaId;
    private final SituacaoProblema situacao;
    private PapelQuantitativo[] papeis;
    private PapelQuantitativo papelDesconhecido;
    private RelacaoEstruturalDiagnosticavel relacao;

    public ServicoAtividadeWebTransformacaoRelacaoRica(
            String tentativaId, SituacaoProblema situacao) {
        if (situacao == null || situacao.getEstrutura().getCategoria()
                != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                || !situacao.validar().ehValida()) {
            throw new IllegalArgumentException(
                    "situação rica validada de Transformação de Relação é obrigatória");
        }
        this.tentativaId = tentativaId;
        this.situacao = situacao;
        reiniciar();
    }

    public synchronized Map<String, Object> estadoAtual() {
        Map<String, Object> estado = mapa();
        estado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_ESTADO);
        estado.put("situacao_id", situacao.getId());
        estado.put("tentativa_id", tentativaId);
        estado.put("categoria", TipoSituacaoAditiva.TRANSFORMACAO_RELACAO.name());
        estado.put("relacao", relacao.descreverRelacao());
        estado.put("papel_desconhecido_original", papelDesconhecido.getChave());
        List<Object> projetados = new ArrayList<Object>();
        for (PapelQuantitativo papel : papeis) projetados.add(projetarPapel(papel));
        estado.put("papeis", projetados);
        boolean concluida = papelDesconhecido.estaPreenchido()
                && relacao.verificarConsistencia(papeis[0], papeis[1], papeis[2])
                        == EstadoConsistencia.CONSISTENTE;
        estado.put("concluida", Boolean.valueOf(concluida));
        estado.put("acoes_disponiveis", AcoesDisponiveisAtividadeWeb
                .modelagemPapel(concluida, papelDesconhecido.getChave()));
        return estado;
    }

    public synchronized Map<String, Object> proporValor(String papelId, int valor) {
        if (!papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException("papel não é a incógnita: " + papelId);
        }
        NumeroInteiro proposta = new NumeroInteiro(valor);
        ContextoAcao contexto = new ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        IdentidadeAcaoInstrumentalPapel identidade = papelDesconhecido
                .iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
        Optional<DiagnosticoErroPapel> diagnostico = relacao
                .diagnosticarValorProposto(papeis[0], papeis[1], papeis[2],
                        papelDesconhecido, proposta);
        ResultadoRegistroTentativaPapel registro = papelDesconhecido
                .registrarTentativaComIdentidade(
                        identidade, diagnostico, contexto, proposta);
        if (!diagnostico.isPresent()) {
            papelDesconhecido.posicionar(
                    proposta, OrigemAcao.ORIGEM_USUARIO, contexto);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("action_id", registro.getActionId());
        resultado.put("aceita", Boolean.valueOf(!diagnostico.isPresent()));
        resultado.put("diagnostico", diagnostico.isPresent()
                ? diagnostico.get().getTipo().name() : null);
        resultado.put("chave_mensagem", diagnostico.isPresent()
                ? diagnostico.get().getChaveMensagem() : null);
        resultado.put("rejeicoes_consecutivas",
                Integer.valueOf(registro.getRejeicoesConsecutivas()));
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    public synchronized Map<String, Object> reiniciar() {
        EstruturaAditiva estrutura = situacao.getEstrutura();
        RelacaoEstruturalVinculada vinculada = estrutura.getRelacoes().get(0);
        if (!(vinculada.getRelacao() instanceof RelacaoEstruturalDiagnosticavel)) {
            throw new IllegalStateException("relação rica não diagnostica propostas");
        }
        relacao = (RelacaoEstruturalDiagnosticavel) vinculada.getRelacao();
        List<PapelQuantitativo> originais = vinculada.getPapeis();
        papeis = new PapelQuantitativo[3];
        String chaveIncognita = estrutura.getChavePapelDesconhecidoOriginal();
        for (int i = 0; i < papeis.length; i++) {
            PapelQuantitativo original = originais.get(i);
            PapelQuantitativo tentativa = new PapelQuantitativo(
                    original.getChave(), original.getNomeConceitual(),
                    original.getDominio(), original.descritorRepresentacao(),
                    PublicadorEventoDominio.NENHUM);
            papeis[i] = tentativa;
            if (chaveIncognita.equals(tentativa.getChave())) {
                papelDesconhecido = tentativa;
            } else {
                tentativa.posicionar(new NumeroInteiro(
                                original.valorAtual().valorOuNull().intValue()),
                        OrigemAcao.ORIGEM_SISTEMA, ContextoAcao.NAO_INFORMADO);
            }
        }
        if (papelDesconhecido == null) {
            throw new IllegalStateException("incógnita rica não pertence à relação");
        }
        return estadoAtual();
    }

    private static Map<String, Object> projetarPapel(PapelQuantitativo papel) {
        Map<String, Object> item = mapa();
        item.put("id", papel.getChave());
        item.put("nome", papel.getNomeConceitual());
        item.put("conhecido", Boolean.valueOf(papel.estaPreenchido()));
        item.put("valor", papel.estaPreenchido()
                ? papel.valorAtual().valorOuNull() : null);
        return item;
    }

    private static Map<String, Object> mapa() {
        return new LinkedHashMap<String, Object>();
    }
}
