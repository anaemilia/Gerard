import { useState } from "react";
import { api } from "./api";
import type { ItemAjudaContextual } from "./contratos";

/**
 * Menu "E agora?" (mostrarMenuAjudaContextual/criarOpcaoAjudaContextual,
 * Main.java): cabeçalho + 3 opções (Tenho uma dúvida / Quero continuar /
 * Qual é o próximo passo?). A estrutura (cabeçalho, rótulos) já vem no
 * estado; a mensagem só é resolvida no servidor quando a opção é clicada —
 * mesmo comportamento do CardLayout "escolha"→"resposta" do desktop, que
 * também só materializa o texto (e só então grava o log granular) no clique.
 * Abre no mouseover (mouseEntered do botão, Main.java:2650-2654) — igual a
 * todos os "?" do desktop, nunca só no clique; onFocus cobre o mesmo gesto
 * por teclado. Fecha só no mouseleave do grupo inteiro (nunca no blur do
 * "?" isolado) — blur dispara ao mover o foco para os próprios botões de
 * opção do menu, o que fecharia o menu antes do clique registrar.
 */
/** Ícone "info" (círculo + haste + ponto) — substitui o "?" nos três botões
 * de ajuda contextual (TEXTO/VERGNAUD/COMPLEMENTAR) e no de dica de próximo
 * passo, mesmo símbolo nas duas plataformas (ver criarIconeInterrogacaoContextual,
 * Main.java). */
export function IconeAjudaContextual() {
  return <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
    <circle cx="12" cy="12" r="10" />
    <path d="M12 16v-4" />
    <path d="M12 8h.01" />
  </svg>;
}

export function MenuAjudaContextual({ item }: { item: ItemAjudaContextual | undefined }) {
  const [aberto, setAberto] = useState(false);
  const [mensagem, setMensagem] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  if (!item) return null;

  function abrir() {
    setAberto(true);
    setMensagem(null);
    setErro(null);
  }

  function fechar() {
    setAberto(false);
  }

  async function escolher(intencao: ItemAjudaContextual["opcoes"][number]["intencao"]) {
    setCarregando(true);
    setErro(null);
    try {
      const resultado = await api.ajudaContextual(item!.area, intencao);
      setMensagem(resultado.mensagem);
    } catch (erroRequisicao) {
      setErro((erroRequisicao as Error).message);
    } finally {
      setCarregando(false);
    }
  }

  return <div className="help-mark-wrap" onMouseEnter={abrir} onMouseLeave={fechar}>
    <button type="button" className="help-mark" aria-expanded={aberto}
      aria-label={item.cabecalho} onFocus={abrir}><IconeAjudaContextual /></button>
    {aberto && <div className="help-menu" role="dialog" aria-label={item.cabecalho}>
      <strong className="help-menu-cabecalho">{item.cabecalho}</strong>
      {mensagem
        ? <p className="help-tip" role="status">{mensagem}</p>
        : <div className="help-menu-opcoes">
            {item.opcoes.map((opcao) => <button key={opcao.intencao} type="button"
              disabled={carregando} onClick={() => void escolher(opcao.intencao)}>
              {opcao.rotulo}
            </button>)}
            {erro && <p className="message message-erro" role="alert">{erro}</p>}
          </div>}
    </div>}
  </div>;
}
