package gerard.pesquisador.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Uma linha de log da interação no Gerard.
 *
 * O formato segue o modelo analítico usado nos quadros de análise da tarefa:
 * agente da ação, problema, tentativa, tarefa, C/E, instrumento, função do
 * artefato e regras. Foram acrescentados campos de sessão, tempo, categoria e
 * detalhes para permitir análise quantitativa em tempo de uso.
 */
public class EventoLogGerard {

    public static final String[] CABECALHO = new String[]{
            "timestamp",
            "sessao",
            "usuario",
            "agente_da_acao",
            "problema",
            "situacao_versao_id",
            "situacao_grupo_id",
            "idioma_situacao",
            "tentativa",
            "tarefa",
            "ce",
            "instrumento_organizacao",
            "instrumento_artefato",
            "funcao_do_artefato",
            "objeto",
            "regras",
            "categoria",
            "enunciado",
            "origem_evento",
            "detalhes",
            "tipo_acao_interacao",
            "propriedade_acao",
            "mudanca_observavel",
            "tentativa_numero_situacao",
            "invariante_origem",
            "invariante_codigo",
            "invariante_simbolico",
            "invariante_observacao",
            "natureza_acao",
            "efeito_acao"
    };

    private String timestamp;
    private String sessao;
    private String usuario;
    private String agenteDaAcao;
    private String problema;
    private String situacaoVersaoId;
    private String situacaoGrupoId;
    private String idiomaSituacao;
    private String tentativa;
    private String tarefa;
    private String ce;
    private String instrumentoOrganizacao;
    private String instrumentoArtefato;
    private String funcaoDoArtefato;
    private String objeto;
    private String regras;
    private String categoria;
    private String enunciado;
    private String origemEvento;
    private String detalhes;
    private String tipoAcaoInteracao;
    private String propriedadeAcao;
    private String mudancaObservavel;
    private String tentativaNumeroSituacao = "";
    private String invarianteOrigem = "";
    private String invarianteCodigo = "";
    private String invarianteSimbolico = "";
    private String invarianteObservacao = "";
    private String naturezaAcao = "";
    private String efeitoAcao = "";

    public EventoLogGerard() {
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static EventoLogGerard criar(String sessao,
                                        String usuario,
                                        String agenteDaAcao,
                                        String problema,
                                        String tentativa,
                                        String tarefa,
                                        String ce,
                                        String instrumentoOrganizacao,
                                        String instrumentoArtefato,
                                        String funcaoDoArtefato,
                                        String objeto,
                                        String regras,
                                        String categoria,
                                        String enunciado,
                                        String origemEvento,
                                        String detalhes) {
        return criar(sessao, usuario, agenteDaAcao, problema, tentativa, tarefa, ce,
                instrumentoOrganizacao, instrumentoArtefato, funcaoDoArtefato, objeto,
                regras, categoria, enunciado, origemEvento, detalhes, "", "", "", "", "", "");
    }

    public static EventoLogGerard criar(String sessao,
                                        String usuario,
                                        String agenteDaAcao,
                                        String problema,
                                        String tentativa,
                                        String tarefa,
                                        String ce,
                                        String instrumentoOrganizacao,
                                        String instrumentoArtefato,
                                        String funcaoDoArtefato,
                                        String objeto,
                                        String regras,
                                        String categoria,
                                        String enunciado,
                                        String origemEvento,
                                        String detalhes,
                                        String situacaoVersaoId,
                                        String situacaoGrupoId,
                                        String idiomaSituacao,
                                        String tipoAcaoInteracao,
                                        String propriedadeAcao,
                                        String mudancaObservavel) {
        EventoLogGerard evento = new EventoLogGerard();
        evento.sessao = valor(sessao);
        evento.usuario = valor(usuario);
        evento.agenteDaAcao = normalizarAgente(agenteDaAcao);
        evento.problema = valor(problema);
        evento.situacaoVersaoId = valor(situacaoVersaoId);
        evento.situacaoGrupoId = valor(situacaoGrupoId);
        evento.idiomaSituacao = valor(idiomaSituacao);
        evento.tentativa = valor(tentativa);
        evento.tarefa = valor(tarefa);
        evento.ce = "C".equals(evento.agenteDaAcao) ? "" : normalizarCe(ce);
        evento.instrumentoOrganizacao = valor(instrumentoOrganizacao);
        evento.instrumentoArtefato = valor(instrumentoArtefato);
        evento.funcaoDoArtefato = valor(funcaoDoArtefato);
        evento.objeto = valor(objeto);
        evento.regras = valor(regras);
        evento.categoria = valor(categoria);
        evento.enunciado = valor(enunciado);
        evento.origemEvento = valor(origemEvento);
        evento.detalhes = valor(detalhes);
        evento.tipoAcaoInteracao = valor(tipoAcaoInteracao);
        evento.propriedadeAcao = valor(propriedadeAcao);
        evento.mudancaObservavel = valor(mudancaObservavel);
        return evento;
    }

    public String toTsv() {
        String[] campos = new String[]{
                timestamp, sessao, usuario, agenteDaAcao, problema, situacaoVersaoId, situacaoGrupoId, idiomaSituacao, tentativa,
                tarefa, ce, instrumentoOrganizacao, instrumentoArtefato,
                funcaoDoArtefato, objeto, regras, categoria, enunciado,
                origemEvento, detalhes, tipoAcaoInteracao, propriedadeAcao, mudancaObservavel, tentativaNumeroSituacao,
                invarianteOrigem, invarianteCodigo, invarianteSimbolico, invarianteObservacao, naturezaAcao, efeitoAcao
        };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append('\t');
            }
            sb.append(escapar(campos[i]));
        }
        return sb.toString();
    }

    public static String cabecalhoTsv() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CABECALHO.length; i++) {
            if (i > 0) {
                sb.append('\t');
            }
            sb.append(CABECALHO[i]);
        }
        return sb.toString();
    }

    public static EventoLogGerard deTsv(String linha) {
        if (linha == null || linha.trim().length() == 0) return null;
        String[] campos = linha.split("\\t", -1);
        if (campos.length < 17 || "timestamp".equalsIgnoreCase(campos[0])) return null;
        EventoLogGerard evento = new EventoLogGerard();
        boolean novoFormato = campos.length >= 23;
        int deslocamento = novoFormato ? 3 : 0;
        evento.timestamp = campo(campos, 0);
        evento.sessao = campo(campos, 1);
        evento.usuario = campo(campos, 2);
        evento.agenteDaAcao = normalizarAgente(campo(campos, 3));
        evento.problema = campo(campos, 4);
        evento.situacaoVersaoId = novoFormato ? campo(campos, 5) : "";
        evento.situacaoGrupoId = novoFormato ? campo(campos, 6) : "";
        evento.idiomaSituacao = novoFormato ? campo(campos, 7) : "";
        evento.tentativa = campo(campos, 5 + deslocamento);
        evento.tarefa = campo(campos, 6 + deslocamento);
        evento.ce = "C".equals(evento.agenteDaAcao) ? "" : normalizarCe(campo(campos, 7 + deslocamento));
        evento.instrumentoOrganizacao = campo(campos, 8 + deslocamento);
        evento.instrumentoArtefato = campo(campos, 9 + deslocamento);
        evento.funcaoDoArtefato = campo(campos, 10 + deslocamento);
        evento.objeto = campo(campos, 11 + deslocamento);
        evento.regras = campo(campos, 12 + deslocamento);
        evento.categoria = campo(campos, 13 + deslocamento);
        evento.enunciado = campo(campos, 14 + deslocamento);
        evento.origemEvento = campo(campos, 15 + deslocamento);
        evento.detalhes = campo(campos, 16 + deslocamento);
        evento.tipoAcaoInteracao = campo(campos, 17 + deslocamento);
        evento.propriedadeAcao = campo(campos, 18 + deslocamento);
        evento.mudancaObservavel = campo(campos, 19 + deslocamento);
        evento.tentativaNumeroSituacao = campo(campos, 20 + deslocamento);
        // Os quatro campos de invariante foram acrescentados ao final para
        // preservar a leitura dos logs produzidos pelas versões anteriores.
        evento.invarianteOrigem = campo(campos, 24);
        evento.invarianteCodigo = campo(campos, 25);
        evento.invarianteSimbolico = campo(campos, 26);
        evento.invarianteObservacao = campo(campos, 27);
        evento.naturezaAcao = campo(campos, 28);
        evento.efeitoAcao = campo(campos, 29);
        return evento;
    }

    private static String campo(String[] campos, int indice) {
        return indice >= 0 && indice < campos.length ? campos[indice] : "";
    }


    public static String normalizarAgente(String texto) {
        String v = valor(texto).trim();
        if (v.length() == 0) {
            return "";
        }
        String upper = removerAcentos(v).toUpperCase();
        if ("C".equals(upper) || upper.indexOf("COMPUTADOR") >= 0 || upper.indexOf("INTERFACE") >= 0) {
            return "C";
        }
        if ("S".equals(upper) || upper.indexOf("SUJEITO") >= 0 || upper.indexOf("USUARIO") >= 0 || upper.indexOf("USUARIA") >= 0) {
            return "S";
        }
        return v;
    }

    public static String normalizarCe(String texto) {
        String v = valor(texto).trim();
        if (v.length() == 0 || "-".equals(v)) {
            return v;
        }
        String upper = removerAcentos(v).toUpperCase();
        if ("C".equals(upper) || "CORRETO".equals(upper) || "ACERTO".equals(upper)) {
            return "C";
        }
        if ("E".equals(upper) || "ERRO".equals(upper) || "ERRADO".equals(upper)) {
            return "E";
        }
        return v;
    }

    private static String removerAcentos(String texto) {
        if (texto == null) {
            return "";
        }
        String n = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "");
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\r", " ").replace("\n", " ").replace("\t", " ").trim();
    }

    private static String valor(String texto) {
        return texto == null ? "" : texto;
    }

    public String getTimestamp() { return timestamp; }
    public String getSessao() { return sessao; }
    public String getUsuario() { return usuario; }
    public String getAgenteDaAcao() { return agenteDaAcao; }
    public String getProblema() { return problema; }
    public String getSituacaoVersaoId() { return situacaoVersaoId; }
    public String getSituacaoGrupoId() { return situacaoGrupoId; }
    public String getIdiomaSituacao() { return idiomaSituacao; }
    public String getTentativa() { return tentativa; }
    public String getTarefa() { return tarefa; }
    public String getCe() { return ce; }
    public String getInstrumentoOrganizacao() { return instrumentoOrganizacao; }
    public String getInstrumentoArtefato() { return instrumentoArtefato; }
    public String getFuncaoDoArtefato() { return funcaoDoArtefato; }
    public String getObjeto() { return objeto; }
    public String getRegras() { return regras; }
    public String getCategoria() { return categoria; }
    public String getEnunciado() { return enunciado; }
    public String getOrigemEvento() { return origemEvento; }
    public String getDetalhes() { return detalhes; }
    public String getTipoAcaoInteracao() { return tipoAcaoInteracao; }
    public String getPropriedadeAcao() { return propriedadeAcao; }
    public String getMudancaObservavel() { return mudancaObservavel; }
    public String getTentativaNumeroSituacao() { return tentativaNumeroSituacao; }
    public void setTentativaNumeroSituacao(String valor) { tentativaNumeroSituacao = valor(valor); }
    public String getInvarianteOrigem() { return invarianteOrigem; }
    public String getInvarianteCodigo() { return invarianteCodigo; }
    public String getInvarianteSimbolico() { return invarianteSimbolico; }
    public String getInvarianteObservacao() { return invarianteObservacao; }
    public void setInvarianteOrigem(String valor) { invarianteOrigem = valor(valor); }
    public void setInvarianteCodigo(String valor) { invarianteCodigo = valor(valor); }
    public void setInvarianteSimbolico(String valor) { invarianteSimbolico = valor(valor); }
    public void setInvarianteObservacao(String valor) { invarianteObservacao = valor(valor); }
    public String getNaturezaAcao() { return naturezaAcao; }
    public String getEfeitoAcao() { return efeitoAcao; }
    public void setNaturezaAcao(String valor) { naturezaAcao = valor(valor); }
    public void setEfeitoAcao(String valor) { efeitoAcao = valor(valor); }
}
