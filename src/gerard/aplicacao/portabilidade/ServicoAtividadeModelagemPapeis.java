package gerard.aplicacao.portabilidade;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gerard.campoaditivo.conclusao.AvaliadorConclusaoModelagem;
import gerard.campoaditivo.conclusao.EstadoPosicionamentoModelagem;
import gerard.dominio.campoaditivo.PapelQuantitativo;

/**
 * Esqueleto comum das atividades de modelagem por papéis: o que acontece com um número solto sobre uma figura é
 * decidido aqui, uma vez, para todas as categorias. Cada categoria só declara o que é dela — quais são os seus
 * papéis ({@link #papelPorChave}) e como revela o valor curado de um papel conhecido ({@link #posicionarConhecido}).
 * O julgamento (posicionamento certo ou incorreto) acontece antes, em ServicoSorteioAtividade.julgarSoltura.
 */
abstract class ServicoAtividadeModelagemPapeis implements ServicoAtividade {
    private static final AvaliadorConclusaoModelagem AVALIADOR_CONCLUSAO = new AvaliadorConclusaoModelagem();

    /** null quando a situação não tem incógnita curada resolvível. */
    protected PapelQuantitativo papelDesconhecido;
    /**
     * Draft do servidor (nunca grava em papelDesconhecido): o "?" foi arrastado até a caixa mas ainda não tem valor
     * digitado/confirmado. Fica aqui, não no cliente — quem decide o que a figura mostra é o gerador de cena.
     */
    protected boolean incognitaEngatada;
    /**
     * Papel conhecido que precisa de representação de sinal (ver ServicoAtividadeComSinal), já revelado mas ainda
     * sem sinal escolhido; nunca aplicado de imediato como os demais. null nas categorias sem sinal.
     */
    protected PapelQuantitativo papelAguardandoSinal;

    /** Os papéis da categoria; lança IllegalStateException para uma chave que não é dela. */
    protected abstract PapelQuantitativo papelPorChave(String chave);

    /** As chaves dos papéis da categoria, na ordem canônica. */
    protected abstract List<String> participantes();

    /**
     * O posicionamento está concluído pela mesma regra do desktop (AvaliadorConclusaoModelagem): todos os papéis no
     * diagrama, cada um no papel que lhe cabe, e a incógnita original preenchida com o valor aceito. Aqui a
     * tentativa só guarda posicionamentos já julgados corretos e o valor da incógnita só entra se aceito.
     */
    protected final boolean posicionamentoConcluido() {
        List<EstadoPosicionamentoModelagem> estados = new ArrayList<EstadoPosicionamentoModelagem>();
        for (String chave : participantes()) {
            PapelQuantitativo papel = papelPorChave(chave);
            boolean preenchido = papel.estaPreenchido();
            boolean incognita = papel == papelDesconhecido;
            String valor = preenchido && papel.valorAtual() != null ? papel.valorAtual().formatar(false) : "";
            estados.add(new EstadoPosicionamentoModelagem(chave, chave, valor, preenchido, incognita,
                    incognita && preenchido, incognita ? Boolean.TRUE : null, null));
        }
        return AVALIADOR_CONCLUSAO.estaConcluida(participantes(), estados);
    }

    /** Revela o valor curado de um papel conhecido da categoria (pode deixá-lo aguardando o sinal). */
    protected abstract void posicionarConhecido(PapelQuantitativo papel);

    /** Gancho para a categoria validar que o papel tem valor curado a posicionar. */
    protected void validarPosicionavel(PapelQuantitativo papel) {
    }

    /** Gancho para a categoria completar o que decorre do posicionamento (derivados por consistência). */
    protected void aposPosicionarConhecido() {
    }

    /** Soltura já julgada correta: aplica o efeito que a atividade oferece agora para esse papel. */
    @SuppressWarnings("unchecked")
    public final synchronized Map<String, Object> soltarNumero(String papelId, String origemPapelId) {
        Object acoes = estadoAtual().get("acoes_disponiveis");
        EfeitoSoltura efeito = acoes instanceof List
                ? EfeitoSoltura.oferecidoPara((List<Object>) acoes, papelId) : EfeitoSoltura.NENHUM;
        return efeito.aplicar(this, papelId, origemPapelId);
    }

    /**
     * Posiciona um papel conhecido (nunca a incógnita) com o valor curado. Idempotente: soltar de novo um papel já
     * posicionado, ou aguardando o sinal, não faz nada.
     */
    @Override
    public final synchronized Map<String, Object> posicionarValorConhecido(String papelId, String origemPapelId) {
        PapelQuantitativo papel = papelPorChave(papelId);
        if (papel == papelDesconhecido) {
            throw new IllegalArgumentException(
                    "papel é a incógnita desta situação, use ENGATAR_INCOGNITA/PROPOR_VALOR_PAPEL: " + papelId);
        }
        validarPosicionavel(papel);
        if (!papel.estaPreenchido() && papel != papelAguardandoSinal) {
            posicionarConhecido(papel);
        }
        aposPosicionarConhecido();
        return resultadoAceito();
    }

    /**
     * Engata o "?" na caixa da incógnita (protocolo mouse-texto, Main.java). Só marca o rascunho; a digitação em si
     * continua indo por PROPOR_VALOR_PAPEL.
     */
    @Override
    public final synchronized Map<String, Object> engatarIncognita(String papelId, String origemPapelId) {
        if (papelDesconhecido == null || !papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException("papel não é a incógnita desta situação: " + papelId);
        }
        if (!papelDesconhecido.estaPreenchido()) {
            incognitaEngatada = true;
        }
        return resultadoAceito();
    }

    final Map<String, Object> resultadoAceito() {
        Map<String, Object> resultado = new LinkedHashMap<String, Object>();
        resultado.put("schema", ServicoAtividadeComposicao.SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("chave_mensagem", null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }
}
