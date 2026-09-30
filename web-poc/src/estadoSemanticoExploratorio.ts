import type { RelacaoExploratoria } from "./contratos";

export type MudancaPapel = Readonly<{
  papelId: string;
  novoValor: number;
  origem: "GESTO_RETA" | "RECONCILIACAO_RELACAO";
}>;
export type SnapshotExploratorio = Readonly<Record<string, number>>;

type OuvinteMudanca = (mudanca: MudancaPapel) => void;

/** Canal neutro: transmite a todos os inscritos e não conhece dependentes. */
class CanalBroadcastMudancaPapel {
  private readonly ouvintes = new Set<OuvinteMudanca>();
  private readonly ouvintesFimDifusao = new Set<() => void>();
  private profundidadeDifusao = 0;

  observar(ouvinte: OuvinteMudanca) {
    this.ouvintes.add(ouvinte);
    return () => { this.ouvintes.delete(ouvinte); };
  }

  observarFimDifusao(ouvinte: () => void) {
    this.ouvintesFimDifusao.add(ouvinte);
    return () => { this.ouvintesFimDifusao.delete(ouvinte); };
  }

  publicar(mudanca: MudancaPapel) {
    this.profundidadeDifusao++;
    try {
      this.ouvintes.forEach((ouvinte) => ouvinte(mudanca));
    } finally {
      this.profundidadeDifusao--;
      if (this.profundidadeDifusao === 0) {
        this.ouvintesFimDifusao.forEach((ouvinte) => ouvinte());
      }
    }
  }
}

/** O papel conhece somente a própria identidade, valor e canal de broadcast. */
class PapelSemanticoExploratorio {
  constructor(readonly id: string, private valor: number,
      private readonly canal: CanalBroadcastMudancaPapel) { }

  alterarPelaReta(novoValor: number) {
    return this.alterar(novoValor, "GESTO_RETA");
  }

  reconciliar(novoValor: number) {
    this.alterar(novoValor, "RECONCILIACAO_RELACAO");
  }

  private alterar(novoValor: number, origem: MudancaPapel["origem"]) {
    if (!Number.isInteger(novoValor) || novoValor === this.valor) return false;
    this.valor = novoValor;
    this.canal.publicar({ papelId: this.id, novoValor, origem });
    return true;
  }
}

/**
 * A relação é apenas mais uma interessada no broadcast. Ela reage aos três
 * papéis que conhece e não conhece nem enumera representações consumidoras.
 */
class OuvinteRelacaoExploratoria {
  constructor(relacao: RelacaoExploratoria,
      papeis: ReadonlyMap<string, PapelSemanticoExploratorio>,
      canal: CanalBroadcastMudancaPapel) {
    canal.observar((mudanca) => {
      if (mudanca.origem !== "GESTO_RETA") return;
      const projecao = relacao.projecoes[mudanca.papelId]?.[String(mudanca.novoValor)];
      if (!projecao) return;
      Object.entries(projecao).forEach(([papelId, valor]) => {
        if (papelId !== mudanca.papelId) papeis.get(papelId)?.reconciliar(valor);
      });
    });
  }
}

/** Uma projeção interessada; outros consumidores podem assinar o mesmo canal. */
class ProjecaoEstadoExploratorio {
  private readonly valores: Record<string, number>;
  private ultimaMudanca: MudancaPapel | null = null;
  private readonly ouvintes = new Set<(
    mudanca: MudancaPapel, snapshot: SnapshotExploratorio) => void>();

  constructor(valoresIniciais: SnapshotExploratorio, canal: CanalBroadcastMudancaPapel) {
    this.valores = { ...valoresIniciais };
    canal.observar((mudanca) => {
      this.valores[mudanca.papelId] = mudanca.novoValor;
      if (mudanca.origem === "GESTO_RETA") this.ultimaMudanca = mudanca;
    });
    canal.observarFimDifusao(() => {
      if (!this.ultimaMudanca) return;
      const mudanca = this.ultimaMudanca;
      this.ultimaMudanca = null;
      const snapshot = { ...this.valores };
      this.ouvintes.forEach((ouvinte) => ouvinte(mudanca, snapshot));
    });
  }

  observar(ouvinte: (mudanca: MudancaPapel, snapshot: SnapshotExploratorio) => void) {
    this.ouvintes.add(ouvinte);
    return () => { this.ouvintes.delete(ouvinte); };
  }
}

/** Monta os participantes do protocolo; não roteia destinatários. */
export class EstadoSemanticoExploratorio {
  private readonly papeis = new Map<string, PapelSemanticoExploratorio>();
  private readonly projecao: ProjecaoEstadoExploratorio;

  constructor(relacao: RelacaoExploratoria, valoresIniciais: SnapshotExploratorio) {
    const canal = new CanalBroadcastMudancaPapel();
    relacao.papeis.forEach((papelId) => {
      const valor = valoresIniciais[papelId];
      if (Number.isInteger(valor)) {
        this.papeis.set(papelId, new PapelSemanticoExploratorio(papelId, valor, canal));
      }
    });
    new OuvinteRelacaoExploratoria(relacao, this.papeis, canal);
    this.projecao = new ProjecaoEstadoExploratorio(valoresIniciais, canal);
  }

  observarSnapshot(ouvinte: Parameters<ProjecaoEstadoExploratorio["observar"]>[0]) {
    return this.projecao.observar(ouvinte);
  }

  alterarPapel(papelId: string, novoValor: number) {
    return this.papeis.get(papelId)?.alterarPelaReta(novoValor) ?? false;
  }
}
