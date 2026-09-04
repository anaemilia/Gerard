import type { CentroSeletorOperacao, EscolhaOperacao,
  EstadoEscolhaOperacaoRelacoes, EstadoEscolhaOperacaoTransformacoes } from "../contratos";

/**
 * Réplica web de gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno: dois botões
 * circulares pequenos (raio 9) desenhados sobre o próprio diagrama — nunca
 * um painel/diálogo separado com frase revelando os valores. A explicação só
 * aparece abaixo dos botões, e só quando a escolha está errada.
 *
 * O CENTRO de cada seletor vem pronto em cena.seletor_operacao — calculado
 * pelo gerador de cena (PosicaoSeletorOperacaoDiagrama, Java) a partir das
 * figuras/conectores já presentes, a mesma fonte que também dimensiona o
 * viewport do SVG. Este componente só sabe desenhar (raio, espaçamento entre
 * os dois botões, rótulos) — não recalcula onde o seletor deveria ficar.
 */
const RAIO_BOTAO = 9;
const ESPACAMENTO_BOTOES = 70;
const LARGURA_EXPLICACAO = 240;

function GrupoBotoes({ centro, escolha, correta, ocupado, onEscolher }: {
  centro: CentroSeletorOperacao; escolha: EscolhaOperacao; correta: boolean | null;
  ocupado: boolean; onEscolher?: (operacao: "SOMA" | "SUBTRACAO") => void;
}) {
  const clicavel = Boolean(onEscolher) && !ocupado;
  return <g className="op-seletor">
    <BotaoOperacao cx={centro.cx - ESPACAMENTO_BOTOES / 2} cy={centro.cy} sinal="+" nome="Soma"
      selecionado={escolha === "SOMA"} marcadoErrado={escolha === "SOMA" && correta === false}
      clicavel={clicavel} onClick={() => onEscolher?.("SOMA")} />
    <BotaoOperacao cx={centro.cx + ESPACAMENTO_BOTOES / 2} cy={centro.cy} sinal="−" nome="Subtração"
      selecionado={escolha === "SUBTRACAO"} marcadoErrado={escolha === "SUBTRACAO" && correta === false}
      clicavel={clicavel} onClick={() => onEscolher?.("SUBTRACAO")} />
  </g>;
}

function BotaoOperacao({ cx, cy, sinal, nome, selecionado, marcadoErrado, clicavel, onClick }: {
  cx: number; cy: number; sinal: string; nome: string;
  selecionado: boolean; marcadoErrado: boolean; clicavel: boolean; onClick: () => void;
}) {
  const classe = "op-botao" + (marcadoErrado ? " op-botao-erro" : selecionado ? " op-botao-selecionado" : "");
  return <g className={classe} onClick={clicavel ? onClick : undefined}
      style={{ cursor: clicavel ? "pointer" : "default" }}
      role={clicavel ? "button" : undefined} aria-label={nome}>
    <circle className="op-anel" cx={cx} cy={cy} r={RAIO_BOTAO} />
    {selecionado && <circle className="op-miolo" cx={cx} cy={cy} r={RAIO_BOTAO - 4} />}
    <text x={cx} y={cy + RAIO_BOTAO + 14}>{sinal}</text>
    <text x={cx} y={cy + RAIO_BOTAO + 28}>{nome}</text>
  </g>;
}

function Explicacao({ centro, texto }: { centro: CentroSeletorOperacao; texto: string }) {
  const x = centro.cx - LARGURA_EXPLICACAO / 2;
  const y = centro.cy + RAIO_BOTAO + 36;
  return <foreignObject x={x} y={y} width={LARGURA_EXPLICACAO} height={90} className="op-explicacao">
    <div>{texto}</div>
  </foreignObject>;
}

export function SeletorOperacaoDiagramaGerard({ pontos, modelagem, mensagemErro, ocupado, aoEscolher }: {
  pontos: Readonly<{ entre_transformacoes?: CentroSeletorOperacao;
    entre_estado_transformacao?: CentroSeletorOperacao; relacao?: CentroSeletorOperacao }>;
  modelagem: EstadoEscolhaOperacaoTransformacoes | EstadoEscolhaOperacaoRelacoes;
  mensagemErro: string | null; ocupado: boolean;
  aoEscolher: (operacao: "SOMA" | "SUBTRACAO") => void;
}) {
  if (modelagem.categoria === "COMPOSICAO_RELACOES") {
    const centro = pontos.relacao;
    if (!centro) return null;
    const marcouErrado = modelagem.escolha_operacao !== null && modelagem.correta === false;
    return <>
      <GrupoBotoes centro={centro} escolha={modelagem.escolha_operacao} correta={modelagem.correta}
        ocupado={ocupado} onEscolher={modelagem.concluida ? undefined : aoEscolher} />
      {marcouErrado && mensagemErro && <Explicacao centro={centro} texto={mensagemErro} />}
    </>;
  }

  const centro1 = pontos.entre_transformacoes;
  const centro2 = modelagem.segunda_etapa_habilitada ? pontos.entre_estado_transformacao : undefined;
  const primeiraErrada = modelagem.escolha_entre_transformacoes !== null
    && modelagem.correta_entre_transformacoes === false;
  const segundaErrada = modelagem.escolha_entre_estado_transformacao !== null
    && modelagem.correta_entre_estado_transformacao === false;
  return <>
    {centro1 && <GrupoBotoes centro={centro1} escolha={modelagem.escolha_entre_transformacoes}
      correta={modelagem.correta_entre_transformacoes} ocupado={ocupado}
      onEscolher={modelagem.segunda_etapa_habilitada ? undefined : aoEscolher} />}
    {centro1 && primeiraErrada && mensagemErro && <Explicacao centro={centro1} texto={mensagemErro} />}
    {centro2 && <GrupoBotoes centro={centro2} escolha={modelagem.escolha_entre_estado_transformacao}
      correta={modelagem.correta_entre_estado_transformacao} ocupado={ocupado}
      onEscolher={modelagem.concluida ? undefined : aoEscolher} />}
    {centro2 && segundaErrada && mensagemErro && <Explicacao centro={centro2} texto={mensagemErro} />}
  </>;
}
