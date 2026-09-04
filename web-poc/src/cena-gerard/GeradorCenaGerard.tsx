import type { CenaDiagrama, EstadoEscolhaOperacaoRelacoes, EstadoEscolhaOperacaoTransformacoes,
  FiguraCena, InteracaoPermitidaFigura } from "../contratos";
import { ConectorCenaGerard } from "./ConectorCenaGerard";
import { FiguraCenaGerard } from "./FiguraCenaGerard";
import { SeletorOperacaoDiagramaGerard } from "./SeletorOperacaoDiagramaGerard";
import type { PosicaoVisual } from "../estadoRepresentacoes";

/** Materializa em SVG a cena completamente especificada pela API do Gérard. */
export function GeradorCenaGerard({ cena, posicoesEmEdicao = {}, aoEditarValor, figuraDestacadaId, seletorOperacao }: {
  cena: CenaDiagrama;
  posicoesEmEdicao?: Readonly<Record<string, PosicaoVisual>>;
  aoEditarValor?: (figura: FiguraCena, interacao: InteracaoPermitidaFigura) => void;
  figuraDestacadaId?: string | null;
  seletorOperacao?: {
    modelagem: EstadoEscolhaOperacaoTransformacoes | EstadoEscolhaOperacaoRelacoes;
    mensagemErro: string | null; ocupado: boolean;
    aoEscolher: (operacao: "SOMA" | "SUBTRACAO") => void;
  };
}) {
  const v = cena.viewport;
  return <svg className="portable-scene" viewBox={`${v.x} ${v.y} ${v.largura} ${v.altura}`}
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
        aoEditarValor={aoEditarValor} destacada={figura.id === figuraDestacadaId} />;
    })}
    {seletorOperacao && cena.seletor_operacao
      && <SeletorOperacaoDiagramaGerard pontos={cena.seletor_operacao} {...seletorOperacao} />}
  </svg>;
}
