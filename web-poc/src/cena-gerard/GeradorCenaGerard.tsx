import type { CenaDiagrama, EstadoEscolhaOperacaoRelacoes, EstadoEscolhaOperacaoTransformacoes,
  FiguraCena, InteracaoPermitidaFigura } from "../contratos";
import { ConectorCenaGerard } from "./ConectorCenaGerard";
import { FiguraCenaGerard } from "./FiguraCenaGerard";
import { SeletorOperacaoDiagramaGerard } from "./SeletorOperacaoDiagramaGerard";
import type { PosicaoVisual } from "../estadoRepresentacoes";

/** Materializa em SVG a cena completamente especificada pela API do Gérard. */
export function GeradorCenaGerard({ cena, posicoesEmEdicao = {}, aoEditarValor, figuraDestacadaId,
    seletorOperacao, ocupado, aoAjustarQuadradinho, textoAdicionarQuadradinho, textoRemoverQuadradinho,
    aoAlternarEixo }: {
  cena: CenaDiagrama;
  posicoesEmEdicao?: Readonly<Record<string, PosicaoVisual>>;
  aoEditarValor?: (figura: FiguraCena, interacao: InteracaoPermitidaFigura) => void;
  figuraDestacadaId?: string | null;
  seletorOperacao?: {
    modelagem: EstadoEscolhaOperacaoTransformacoes | EstadoEscolhaOperacaoRelacoes;
    mensagemErro: string | null; ocupado: boolean;
    aoEscolher: (operacao: "SOMA" | "SUBTRACAO") => void;
  };
  ocupado?: boolean;
  aoAjustarQuadradinho?: (papelId: string, delta: 1 | -1) => void;
  textoAdicionarQuadradinho?: string;
  textoRemoverQuadradinho?: string;
  aoAlternarEixo?: (figura: FiguraCena) => void;
}) {
  const v = cena.viewport;
  // width/height explícitos = o tamanho real que o gerador de cena desenhou
  // (mesma AreaDiagrama usada no servidor, ver ServicoSorteioAtividadeWeb) —
  // sem isso, .portable-scene (width/height:100%) estica o SVG para
  // preencher o container, ampliando as figuras além do tamanho que o
  // desktop desenha (1 unidade da cena = 1px), em todas as categorias.
  // max-width:100%;height:auto (CSS) deixa encolher em telas estreitas,
  // nunca crescer além do tamanho real.
  return <svg className="portable-scene" viewBox={`${v.x} ${v.y} ${v.largura} ${v.altura}`}
      width={v.largura} height={v.altura}
      preserveAspectRatio="xMidYMid meet" role="img" aria-label={cena.descricao || cena.titulo}>
    <defs><marker id="seta-cena-gerard" viewBox="0 0 10 10" refX="9" refY="5"
      markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 0 L 10 5 L 0 10 z" />
    </marker></defs>
    {cena.conectores.map((conector, indice) =>
      <ConectorCenaGerard key={`conector-${indice}`} conector={conector} indice={indice} />)}
    {cena.figuras.map((figura) => {
      const posicao = posicoesEmEdicao[figura.id];
      const figuraProjetada = posicao ? { ...figura, ...posicao } : figura;
      return <FiguraCenaGerard key={figura.id} figura={figuraProjetada}
        aoEditarValor={aoEditarValor} destacada={figura.id === figuraDestacadaId}
        ocupado={ocupado} aoAjustarQuadradinho={aoAjustarQuadradinho}
        textoAdicionarQuadradinho={textoAdicionarQuadradinho}
        textoRemoverQuadradinho={textoRemoverQuadradinho}
        aoAlternarEixo={aoAlternarEixo} />;
    })}
    {seletorOperacao && cena.seletor_operacao
      && <SeletorOperacaoDiagramaGerard pontos={cena.seletor_operacao} {...seletorOperacao} />}
  </svg>;
}
