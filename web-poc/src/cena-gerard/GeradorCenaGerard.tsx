import type { CenaDiagrama } from "../contratos";
import { ConectorCenaGerard } from "./ConectorCenaGerard";
import { FiguraCenaGerard } from "./FiguraCenaGerard";

/** Materializa em SVG a cena completamente especificada pela API do Gérard. */
export function GeradorCenaGerard({ cena }: { cena: CenaDiagrama }) {
  const v = cena.viewport;
  return <svg className="portable-scene" viewBox={`${v.x} ${v.y} ${v.largura} ${v.altura}`}
      preserveAspectRatio="xMidYMid meet" role="img" aria-label={cena.descricao || cena.titulo}>
    <defs><marker id="seta-cena-gerard" viewBox="0 0 10 10" refX="9" refY="5"
      markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 0 L 10 5 L 0 10 z" />
    </marker></defs>
    {cena.conectores.map((conector, indice) =>
      <ConectorCenaGerard key={`conector-${indice}`} conector={conector} indice={indice} />)}
    {cena.figuras.map((figura) => <FiguraCenaGerard key={figura.id} figura={figura} />)}
  </svg>;
}
