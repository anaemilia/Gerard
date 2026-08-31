type IconeProps = { tipo: "composicao" | "transformacao" | "comparacao" | "relacoes" | "transformacaoRelacao" };

function IconeCategoria({ tipo }: IconeProps) {
  if (tipo === "composicao") return <svg viewBox="0 0 72 64" aria-hidden="true"><rect x="7" y="8" width="18" height="18"/><rect x="7" y="38" width="18" height="18"/><path d="M30 12c14 7 14 33 0 40"/><rect x="47" y="23" width="18" height="18"/></svg>;
  if (tipo === "transformacao") return <svg viewBox="0 0 72 64" aria-hidden="true"><rect x="5" y="24" width="17" height="17"/><path d="M25 32h30m-9-8 9 8-9 8"/><rect x="55" y="24" width="12" height="17"/><circle cx="36" cy="9" r="8"/></svg>;
  if (tipo === "comparacao") return <svg viewBox="0 0 72 64" aria-hidden="true"><rect x="28" y="4" width="16" height="16"/><path d="M36 20v35m0-30-10 10m10-10 10 10"/><rect x="28" y="44" width="16" height="16"/><circle cx="61" cy="34" r="8"/></svg>;
  if (tipo === "relacoes") return <svg viewBox="0 0 72 64" aria-hidden="true"><circle cx="15" cy="32" r="8"/><circle cx="57" cy="32" r="8"/><path d="M23 32h26m-8-7 8 7-8 7"/></svg>;
  return <svg viewBox="0 0 72 64" aria-hidden="true"><circle cx="15" cy="15" r="8"/><circle cx="15" cy="49" r="8"/><path d="M28 10c15 8 15 36 0 44"/><circle cx="51" cy="32" r="8"/></svg>;
}

function BotaoCategoria({ rotulo, tipo, habilitado, selecionado, aoEscolher }: {
  rotulo: string; tipo: IconeProps["tipo"]; habilitado: boolean; selecionado: boolean; aoEscolher: () => void;
}) {
  return <button className="category-button" type="button"
    aria-label={rotulo} title={rotulo} aria-pressed={selecionado} disabled={!habilitado} onClick={aoEscolher}>
    <IconeCategoria tipo={tipo} />
  </button>;
}

function IconeDado() {
  return <svg viewBox="0 0 48 48" aria-hidden="true">
    <rect x="7" y="7" width="34" height="34" rx="4" />
    <circle cx="16" cy="16" r="2.25" />
    <circle cx="32" cy="16" r="2.25" />
    <circle cx="24" cy="24" r="2.25" />
    <circle cx="16" cy="32" r="2.25" />
    <circle cx="32" cy="32" r="2.25" />
  </svg>;
}

function BotaoSortear({ grupo, habilitado, ocupado, aoSortear }: {
  grupo: "Medidas" | "Relações"; habilitado: boolean; ocupado: boolean; aoSortear: () => void;
}) {
  const rotulo = `Sortear ${grupo}`;
  return <span className="draw-button-tooltip" title={rotulo}>
    <button className="draw-button" type="button" disabled={!habilitado || ocupado} onClick={aoSortear}
      aria-label={rotulo}
      aria-description={`Solicita ao servidor uma situação do grupo ${grupo}`}>
      <IconeDado />
    </button>
  </span>;
}

export function BarraCategorias({ podeSortearMedidas, podeSortearRelacoes, ocupado, aoSortearMedidas, aoSortearRelacoes, categoriasHabilitadas, categoriaSelecionada, aoEscolherCategoria }: {
  podeSortearMedidas: boolean; podeSortearRelacoes: boolean; ocupado: boolean;
  aoSortearMedidas: () => void; aoSortearRelacoes: () => void;
  categoriasHabilitadas: readonly string[]; categoriaSelecionada: string | null;
  aoEscolherCategoria: (categoria: string) => void;
}) {
  return <header className="top-area">
    <div className="utility-row">
      <div className="utility-tools" aria-label="Ferramentas"><button type="button" title="Representações" aria-label="Representações">▣</button><button type="button" title="Ajuda adaptativa" aria-label="Ajuda adaptativa">⚙</button></div>
      <span className="language">Português</span>
    </div>
    <nav className="category-navigation" aria-label="Categorias de situações aditivas">
      <div className="category-group"><strong>Medidas</strong><div className="category-buttons">
        <BotaoCategoria rotulo="Composição de medidas" tipo="composicao" habilitado={categoriasHabilitadas.includes("COMPOSICAO_MEDIDAS") && !ocupado} selecionado={categoriaSelecionada === "COMPOSICAO_MEDIDAS"} aoEscolher={() => aoEscolherCategoria("COMPOSICAO_MEDIDAS")} />
        <BotaoCategoria rotulo="Transformação de medidas" tipo="transformacao" habilitado={categoriasHabilitadas.includes("TRANSFORMACAO_MEDIDAS") && !ocupado} selecionado={categoriaSelecionada === "TRANSFORMACAO_MEDIDAS"} aoEscolher={() => aoEscolherCategoria("TRANSFORMACAO_MEDIDAS")} />
        <BotaoCategoria rotulo="Comparação de medidas" tipo="comparacao" habilitado={categoriasHabilitadas.includes("COMPARACAO_MEDIDAS") && !ocupado} selecionado={categoriaSelecionada === "COMPARACAO_MEDIDAS"} aoEscolher={() => aoEscolherCategoria("COMPARACAO_MEDIDAS")} />
      </div></div>
      <BotaoSortear grupo="Medidas" habilitado={podeSortearMedidas} ocupado={ocupado} aoSortear={aoSortearMedidas} />
      <span className="category-divider" aria-hidden="true" />
      <BotaoSortear grupo="Relações" habilitado={podeSortearRelacoes} ocupado={ocupado} aoSortear={aoSortearRelacoes} />
      <div className="category-group"><strong>Relações</strong><div className="category-buttons">
        <BotaoCategoria rotulo="Composição de transformações" tipo="composicao" habilitado={categoriasHabilitadas.includes("COMPOSICAO_TRANSFORMACOES") && !ocupado} selecionado={categoriaSelecionada === "COMPOSICAO_TRANSFORMACOES"} aoEscolher={() => aoEscolherCategoria("COMPOSICAO_TRANSFORMACOES")} />
        <BotaoCategoria rotulo="Transformação de relação" tipo="relacoes" habilitado={categoriasHabilitadas.includes("TRANSFORMACAO_RELACAO") && !ocupado} selecionado={categoriaSelecionada === "TRANSFORMACAO_RELACAO"} aoEscolher={() => aoEscolherCategoria("TRANSFORMACAO_RELACAO")} />
        <BotaoCategoria rotulo="Composição de relações" tipo="transformacaoRelacao" habilitado={categoriasHabilitadas.includes("COMPOSICAO_RELACOES") && !ocupado} selecionado={categoriaSelecionada === "COMPOSICAO_RELACOES"} aoEscolher={() => aoEscolherCategoria("COMPOSICAO_RELACOES")} />
      </div></div>
    </nav>
  </header>;
}
