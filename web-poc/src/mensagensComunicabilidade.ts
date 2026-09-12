export type AreaComunicabilidade = "TEXTO" | "VERGNAUD" | "COMPLEMENTAR";
export type IntencaoComunicabilidade = "DUVIDA" | "CONTINUAR" | "PROXIMO_PASSO";

const mensagens: Record<AreaComunicabilidade, Record<IntencaoComunicabilidade, string>> = {
  TEXTO: {
    DUVIDA: "Observe os números, os personagens e o que a pergunta solicita. Passe o mouse sobre os elementos destacados no texto para consultar seus papéis semânticos.",
    CONTINUAR: "Continue identificando e arrastando para o diagrama os números ou a interrogação que ainda não foram posicionados.",
    PROXIMO_PASSO: "Procure no enunciado o valor que corresponde ao papel semântico ainda vazio e arraste-o para o diagrama de Vergnaud."
  },
  VERGNAUD: {
    DUVIDA: "Observe o papel semântico exibido abaixo de cada elemento e compare-o com os valores do enunciado.",
    CONTINUAR: "Complete os elementos vazios com números e a interrogação do enunciado.",
    PROXIMO_PASSO: "Identifique um papel vazio e arraste até ele o valor correspondente."
  },
  COMPLEMENTAR: {
    DUVIDA: "Observe como esta representação mostra a mesma relação presente no diagrama de Vergnaud.",
    CONTINUAR: "Manipule os elementos desta representação e observe como as outras representações são atualizadas.",
    PROXIMO_PASSO: "Ajuste esta representação para que seus valores e relações correspondam ao que já foi construído no diagrama de Vergnaud."
  }
};

export function mensagemComunicabilidade(area: AreaComunicabilidade, intencao: IntencaoComunicabilidade) {
  return mensagens[area][intencao];
}

export function interpretarMensagem(texto: string, areaAtual: AreaComunicabilidade) {
  const normalizado = texto.normalize("NFD").replace(/\p{Diacritic}/gu, "").toLowerCase();
  let area = areaAtual;
  if (/complement|venn|quadrad|grafico|eixo/.test(normalizado)) area = "COMPLEMENTAR";
  else if (/diagrama|vergnaud|arrast|modelo|sinal|menos|digitar|valor/.test(normalizado)) area = "VERGNAUD";
  else if (/texto|enunciado|leitura|reler|personagem/.test(normalizado)) area = "TEXTO";
  let intencao: IntencaoComunicabilidade = "DUVIDA";
  if (/proximo|passo|agora/.test(normalizado)) intencao = "PROXIMO_PASSO";
  else if (/continu|voltar|retomar/.test(normalizado)) intencao = "CONTINUAR";
  return { area, intencao, resposta: mensagemComunicabilidade(area, intencao) };
}
