package gerard.aplicacao.portabilidade;

import java.util.Map;

/**
 * Capacidade adicional de uma tentativa web (ServicoAtividade ou
 * ServicoAtividadeEscolhaOperacao) cujo esquema tem pelo menos um papel
 * que precisa de representação de sinal
 * (PapelQuantitativo.necessitaRepresentacaoDeSinal(), catalogado por
 * CatalogoNecessidadeRepresentacaoDeSinal — ver comentário de
 * ServicoSorteioAtividade.escolherSinalNumeroRelativo). Categorias sem
 * nenhum papel assim (Composição de Medidas) simplesmente não implementam
 * esta interface — mesmo padrão de capacidade opcional já usado para
 * material concreto (ver instanceof ServicoAtividadeComposicao em
 * ServicoSorteioAtividade.ajustarQuadradinho).
 *
 * Mesmo protocolo do desktop (ScaffoldingNumeroRelativo.mostrarMenuEscolhaSinal
 * / MenuSinalNumeroRelativo, Main.java): ao posicionar (arrastar) um papel
 * conhecido que precisa de sinal, o valor NÃO é aplicado de imediato — a
 * magnitude curada é revelada e o papel fica "aguardando escolha de sinal"
 * até esta ação escolher explicitamente positivo/negativo. A escolha do
 * estudante é sempre aplicada (nunca bloqueada só por divergir do sinal
 * curado) — divergência gera apenas um aviso não-bloqueante, mesmo
 * ui.tooltip.relativeSign.confirm que o desktop já usa
 * (informarSuspeitaSinalIncorretoNumeroRelativo).
 */
public interface ServicoAtividadeComSinal {
    Map<String, Object> escolherSinalNumeroRelativo(String papelId, String sinal);
}
