import type { EstadoAtividade, PapelProjetado } from "./contratos";

/**
 * AG_EMCME (ver gerard-scaffolding-interacao seção 3): material concreto que
 * só aparece após a 3ª rejeição consecutiva da incógnita — nunca durante a
 * modelagem normal. Layout genérico (grade quase-quadrada) equivalente ao
 * algoritmo de adicionarQuadradinhosNoCirculo em Main.java, sem copiar
 * coordenadas curadas — cada quadradinho é decorativo, a contagem é que vem
 * do servidor (PapelQuantitativo real, mesmo valor do campo numérico).
 */
function Grupo({ papel, ehAlvo, ocupado, aoAjustar }: {
  papel: PapelProjetado; ehAlvo: boolean; ocupado: boolean;
  aoAjustar?: (delta: 1 | -1) => void;
}) {
  const valor = papel.valor ?? 0;
  return <div className={`quadradinhos-grupo${ehAlvo ? " quadradinhos-grupo-alvo" : ""}`}>
    <strong>{papel.nome}</strong>
    <div className="quadradinhos-grade" aria-hidden="true">
      {Array.from({ length: valor }, (_, indice) => <span key={indice} className="quadradinho" />)}
    </div>
    {ehAlvo && aoAjustar && <div className="quadradinhos-controles">
      <button type="button" onClick={() => aoAjustar(-1)} disabled={ocupado || valor <= 0}
        aria-label={`Remover uma unidade de ${papel.nome}`}>−</button>
      <span className="quadradinhos-contagem">{valor}</span>
      <button type="button" onClick={() => aoAjustar(1)} disabled={ocupado}
        aria-label={`Adicionar uma unidade a ${papel.nome}`}>+</button>
    </div>}
  </div>;
}

export function MaterialConcretoQuadradinhos({ modelagem, ocupado, aoAjustar }: {
  modelagem: EstadoAtividade; ocupado: boolean; aoAjustar: (delta: 1 | -1) => void;
}) {
  const alvo = modelagem.papel_desconhecido_original;
  return <section className="material-concreto" aria-label="Material concreto">
    <p className="material-concreto-aviso">Use os quadradinhos para contar a resposta.</p>
    <div className="material-concreto-grupos">
      <Grupo papel={modelagem.parte1} ehAlvo={modelagem.parte1.id === alvo} ocupado={ocupado} aoAjustar={aoAjustar} />
      <Grupo papel={modelagem.parte2} ehAlvo={modelagem.parte2.id === alvo} ocupado={ocupado} aoAjustar={aoAjustar} />
      <Grupo papel={modelagem.todo} ehAlvo={modelagem.todo.id === alvo} ocupado={ocupado} aoAjustar={aoAjustar} />
    </div>
  </section>;
}
