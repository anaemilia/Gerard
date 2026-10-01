import type { ReactNode } from "react";

/**
 * Algumas mensagens do servidor trazem negrito como <b>…</b> (o desktop as exibe
 * como HTML — ver ScaffoldingQuestionamento). O web converte somente esse marcador
 * em <strong>, sem innerHTML: o texto continua sendo o do servidor, só muda a
 * apresentação (a mensagem nunca é composta aqui).
 */
export function textoComNegrito(mensagem: string): ReactNode[] {
  return mensagem.split(/(<b>.*?<\/b>)/g).filter(Boolean).map((parte, indice) => {
    const negrito = /^<b>(.*)<\/b>$/.exec(parte);
    return negrito ? <strong key={indice}>{negrito[1]}</strong> : parte;
  });
}
