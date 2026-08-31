import { Handle, Position, type Node, type NodeProps } from "@xyflow/react";
import type { PapelProjetado } from "./contratos";

export type PapelNodeType = Node<{ papel: PapelProjetado; incognitaOriginal: boolean }, "papel">;

export function PapelNode({ data }: NodeProps<PapelNodeType>) {
  const { papel, incognitaOriginal } = data;
  return <article className={`papel-node ${incognitaOriginal ? "papel-node-incognita" : ""}`}
    aria-label={`${papel.nome}: ${papel.conhecido ? papel.valor : "desconhecido"}`}>
    <Handle type="target" position={Position.Left} className="papel-handle" />
    <span className="papel-nome">{papel.nome}</span>
    <strong className="papel-valor">{papel.conhecido ? papel.valor : "?"}</strong>
    <Handle type="source" position={Position.Right} className="papel-handle" />
  </article>;
}
