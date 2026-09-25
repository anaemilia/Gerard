import { useEffect, useRef, useState } from "react";
import type { EixoFigura } from "./contratos";

/**
 * Réplica web de PaineisEixosRelacoes/ScaffoldingGraficoInteiros (Main.java):
 * reta dos inteiros com setas, marcas e rótulos -escala..+escala, e — quando
 * o papel tem um valor — um ponto azul que espelha esse valor. Decisão mais
 * recente da usuária em 2026-09-25: depois que o ponto está azul, círculo e
 * reta são bidirecionais; mover o ponto envia uma revisão semântica ao
 * servidor, que devolve texto, diagrama e reta sincronizados. A lupa mostra
 * o painel e o olhinho o esconde, em qualquer fase da atividade.
 */

const LARGURA = 280;
const ALTURA_SEM_VALOR = 78;
const ALTURA_COM_VALOR = 96;
const MARGEM_EIXO = 24;
const ORIGEM_Y_SEM_VALOR = 40;
const ORIGEM_Y_COM_VALOR = 34;

function limitar(valor: number, minimo: number, maximo: number) {
  return Math.min(maximo, Math.max(minimo, valor));
}

export function EixoNumericoFigura({ figuraId, papelNome, eixo, editavel, ocupado,
  aoAlterarValor, aoFechar }: {
  figuraId: string;
  papelNome: string;
  eixo: EixoFigura;
  editavel: boolean;
  ocupado: boolean;
  aoAlterarValor: (valor: number) => void;
  aoFechar: () => void;
}) {
  const [posicao, setPosicao] = useState<{ left: number; top: number } | null>(null);
  const [valorArrastando, setValorArrastando] = useState<number | null>(null);
  const svgRef = useRef<SVGSVGElement | null>(null);
  const arrastandoValorRef = useRef(false);
  const painelRef = useRef<HTMLDivElement | null>(null);
  const posicaoRef = useRef<{ left: number; top: number } | null>(null);
  const posicaoManualRef = useRef(false);
  const arrastePainelRef = useRef<{ deslocamentoX: number; deslocamentoY: number } | null>(null);

  function guardarPosicao(left: number, top: number) {
    const proxima = { left, top };
    posicaoRef.current = proxima;
    setPosicao((atual) => atual && atual.left === left && atual.top === top ? atual : proxima);
  }

  function limitarPosicaoNaViewport(left: number, top: number) {
    const painel = painelRef.current?.getBoundingClientRect();
    const larguraPainel = painel?.width ?? 0;
    const alturaPainel = painel?.height ?? 0;
    const margem = 8;
    return {
      left: Math.max(margem, Math.min(left, window.innerWidth - margem - larguraPainel)),
      top: Math.max(margem, Math.min(top, window.innerHeight - margem - alturaPainel)),
    };
  }

  useEffect(() => {
    const elemento = Array.from(document.querySelectorAll<HTMLElement>("[data-figura-id]"))
      .find((item) => item.dataset.figuraId === figuraId);
    if (!elemento) {
      setPosicao(null);
      return;
    }
    const ancoraElemento = elemento;

    const margemViewport = 8;
    const distanciaDaFigura = 10;
    function atualizarPosicao() {
      if (posicaoManualRef.current && posicaoRef.current) {
        const limitada = limitarPosicaoNaViewport(posicaoRef.current.left, posicaoRef.current.top);
        guardarPosicao(limitada.left, limitada.top);
        return;
      }
      const ancora = ancoraElemento.getBoundingClientRect();
      const painel = painelRef.current?.getBoundingClientRect();
      const larguraPainel = painel?.width ?? 0;
      const alturaPainel = painel?.height ?? 0;
      let left = ancora.right + distanciaDaFigura;
      if (larguraPainel > 0 && left + larguraPainel > window.innerWidth - margemViewport) {
        left = ancora.left - distanciaDaFigura - larguraPainel;
      }
      left = Math.max(margemViewport,
        Math.min(left, window.innerWidth - margemViewport - larguraPainel));
      const top = Math.max(margemViewport,
        Math.min(ancora.top, window.innerHeight - margemViewport - alturaPainel));
      guardarPosicao(left, top);
    }

    atualizarPosicao();
    const quadro = window.requestAnimationFrame(atualizarPosicao);
    const observador = new ResizeObserver(atualizarPosicao);
    observador.observe(ancoraElemento);
    if (painelRef.current) observador.observe(painelRef.current);
    window.addEventListener("resize", atualizarPosicao);
    window.addEventListener("scroll", atualizarPosicao, true);
    return () => {
      window.cancelAnimationFrame(quadro);
      observador.disconnect();
      window.removeEventListener("resize", atualizarPosicao);
      window.removeEventListener("scroll", atualizarPosicao, true);
    };
  }, [figuraId, eixo.valor]);

  useEffect(() => {
    function moverPainel(evento: PointerEvent) {
      const arraste = arrastePainelRef.current;
      if (!arraste) return;
      const limitada = limitarPosicaoNaViewport(
        evento.clientX - arraste.deslocamentoX,
        evento.clientY - arraste.deslocamentoY);
      guardarPosicao(limitada.left, limitada.top);
    }
    function finalizarArrastePainel() {
      arrastePainelRef.current = null;
    }
    window.addEventListener("pointermove", moverPainel);
    window.addEventListener("pointerup", finalizarArrastePainel);
    window.addEventListener("pointercancel", finalizarArrastePainel);
    return () => {
      window.removeEventListener("pointermove", moverPainel);
      window.removeEventListener("pointerup", finalizarArrastePainel);
      window.removeEventListener("pointercancel", finalizarArrastePainel);
    };
  }, []);

  function iniciarArrastePainel(evento: React.PointerEvent<HTMLDivElement>) {
    if (evento.button !== 0 || !posicaoRef.current
        || (evento.target as Element).closest("button")) return;
    evento.preventDefault();
    posicaoManualRef.current = true;
    arrastePainelRef.current = {
      deslocamentoX: evento.clientX - posicaoRef.current.left,
      deslocamentoY: evento.clientY - posicaoRef.current.top,
    };
  }

  const escalaEfetiva = eixo.escala;
  const valorMostrado = valorArrastando ?? eixo.valor;
  const temValor = valorMostrado !== null;
  const altura = temValor ? ALTURA_COM_VALOR : ALTURA_SEM_VALOR;
  const origemY = temValor ? ORIGEM_Y_COM_VALOR : ORIGEM_Y_SEM_VALOR;
  const origemX = LARGURA / 2;
  const xEsquerda = MARGEM_EIXO;
  const xDireita = LARGURA - MARGEM_EIXO;
  const espacamento = Math.max(1, Math.floor(Math.min(origemX - xEsquerda, xDireita - origemX) / escalaEfetiva));

  function xDoValor(valor: number) {
    const limitado = limitar(valor, -escalaEfetiva, escalaEfetiva);
    return origemX + limitado * espacamento;
  }

  function valorDoClienteX(clienteX: number) {
    if (!svgRef.current) return eixo.valor ?? 0;
    const caixa = svgRef.current.getBoundingClientRect();
    const x = ((clienteX - caixa.left) / caixa.width) * LARGURA;
    return limitar(Math.round((limitar(x, xEsquerda, xDireita) - origemX) / espacamento),
      -escalaEfetiva, escalaEfetiva);
  }

  useEffect(() => {
    function mover(evento: MouseEvent) {
      if (arrastandoValorRef.current) setValorArrastando(valorDoClienteX(evento.clientX));
    }
    function soltar(evento: MouseEvent) {
      if (!arrastandoValorRef.current) return;
      arrastandoValorRef.current = false;
      const valor = valorDoClienteX(evento.clientX);
      setValorArrastando(null);
      aoAlterarValor(valor);
    }
    window.addEventListener("mousemove", mover);
    window.addEventListener("mouseup", soltar);
    return () => { window.removeEventListener("mousemove", mover); window.removeEventListener("mouseup", soltar); };
  });

  function iniciarAlteracao(evento: React.MouseEvent) {
    if (!editavel || ocupado) return;
    evento.preventDefault();
    arrastandoValorRef.current = true;
    setValorArrastando(valorDoClienteX(evento.clientX));
  }

  if (!posicao) return null;

  // Espaçamento pequeno demais (escala cresceu muito no arraste) deixa os
  // números se sobrepondo -- pula rótulos (nunca as marcas) a cada N, só
  // legibilidade, mesma reta e mesmos valores por trás.
  const passoRotulo = espacamento >= 14 ? 1 : espacamento >= 7 ? 2 : espacamento >= 4 ? 5 : 10;
  const marcas = [];
  for (let i = -escalaEfetiva; i <= escalaEfetiva; i++) {
    const px = xDoValor(i);
    marcas.push(<g key={i} className="eixo-marca">
      <line x1={px} y1={origemY - 4} x2={px} y2={origemY + 4} />
      {i % passoRotulo === 0 && <text x={px} y={origemY + 18} textAnchor="middle">{i}</text>}
    </g>);
  }

  return <div ref={painelRef} className="eixo-numerico-tip" style={posicao} role="group"
      aria-label={eixo.titulo + " — " + papelNome}>
    <div className="eixo-numerico-cabecalho" onPointerDown={iniciarArrastePainel}>
      <p className="eixo-numerico-titulo">{eixo.titulo}</p>
      <button type="button" className="eixo-numerico-fechar" onClick={aoFechar}
          disabled={ocupado} aria-label={eixo.rotulo_ocultar} title={eixo.rotulo_ocultar}>
        <svg viewBox="0 0 20 14" aria-hidden="true" focusable="false">
          <path d="M1 7 C4 1 16 1 19 7 C16 13 4 13 1 7 Z" />
          <circle cx="10" cy="7" r="2" />
        </svg>
      </button>
    </div>
    <svg ref={svgRef} viewBox={`0 0 ${LARGURA} ${altura}`} width={LARGURA} height={altura}
        className="eixo-numerico-svg">
      <line className="eixo-linha" x1={xEsquerda} y1={origemY} x2={xDireita} y2={origemY} />
      <path className="eixo-seta" d={`M ${xDireita} ${origemY} l -7 -4 M ${xDireita} ${origemY} l -7 4`} />
      <path className="eixo-seta" d={`M ${xEsquerda} ${origemY} l 7 -4 M ${xEsquerda} ${origemY} l 7 4`} />
      {marcas}
      <text x={xEsquerda} y={origemY - 14} className="eixo-rotulo-lado">{eixo.rotulo_negativos}</text>
      <text x={xDireita} y={origemY - 14} textAnchor="end" className="eixo-rotulo-lado">{eixo.rotulo_positivos}</text>
      <text x={xDireita - 4} y={origemY - 24} textAnchor="end" className="eixo-rotulo-eixo">{eixo.rotulo_eixo}</text>
      {editavel && <rect x={xEsquerda} y={origemY - 12} width={xDireita - xEsquerda} height={24}
        className="eixo-area-clicavel" onMouseDown={iniciarAlteracao} />}
      {temValor && <g className={`eixo-ponto-controle${editavel ? " eixo-ponto-controle-interativo" : ""}`}
          transform={`translate(${xDoValor(valorMostrado!)} ${origemY})`}
          onMouseDown={editavel ? iniciarAlteracao : undefined}>
        <circle r={7} />
        <circle r={3} className="eixo-ponto-controle-miolo" />
        <text y={20} textAnchor="middle" className="eixo-valor-escolhido">{valorMostrado}</text>
      </g>}
    </svg>
  </div>;
}
