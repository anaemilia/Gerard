import { Background, MarkerType, ReactFlow, type Edge } from "@xyflow/react";
import "@xyflow/react/dist/style.css";
import type { EstadoAtividade } from "./contratos";
import { PapelNode, type PapelNodeType } from "./PapelNode";

const nodeTypes = { papel: PapelNode };

export function Diagrama({ estado }: { estado: EstadoAtividade }) {
  const nodes: PapelNodeType[] = [
    { id: estado.parte1.id, type: "papel", position: { x: 60, y: 70 }, draggable: false,
      data: { papel: estado.parte1, incognitaOriginal: false }, ariaLabel: "Parte 1" },
    { id: estado.parte2.id, type: "papel", position: { x: 60, y: 280 }, draggable: false,
      data: { papel: estado.parte2, incognitaOriginal: false }, ariaLabel: "Parte 2" },
    { id: estado.todo.id, type: "papel", position: { x: 520, y: 175 }, draggable: false,
      data: { papel: estado.todo, incognitaOriginal: true }, ariaLabel: "Todo" }
  ];
  const edges: Edge[] = [
    { id: "parte1-todo", source: estado.parte1.id, target: estado.todo.id,
      markerEnd: { type: MarkerType.ArrowClosed }, ariaLabel: "Parte 1 compõe o Todo" },
    { id: "parte2-todo", source: estado.parte2.id, target: estado.todo.id,
      markerEnd: { type: MarkerType.ArrowClosed }, ariaLabel: "Parte 2 compõe o Todo" }
  ];
  return <div className="diagram" aria-label="Diagrama de composição de medidas">
    <ReactFlow nodes={nodes} edges={edges} nodeTypes={nodeTypes} fitView minZoom={0.65} maxZoom={1.5}
      nodesConnectable={false} elementsSelectable={false} panOnDrag={false} zoomOnDoubleClick={false}
      deleteKeyCode={null}>
      <Background color="#d6d0c5" gap={28} size={1} />
    </ReactFlow>
  </div>;
}
