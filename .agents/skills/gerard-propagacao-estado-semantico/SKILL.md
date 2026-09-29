---
name: gerard-propagacao-estado-semantico
description: Protocolo de difusão e reconciliação de mudanças nos valores dos papéis semânticos do Gérard. Use ao alterar broadcast, listeners, snapshots compartilhados ou propagação exploratória entre reta, texto, Vergnaud e material concreto, especialmente para números relativos e transformações nas versões desktop e web.
---

# Propagação do estado semântico

## Responsabilidade

Esta skill possui o protocolo pelo qual uma mudança no valor de um papel se
torna um estado coerente para todas as representações. Ela não possui as
fórmulas matemáticas, os gestos de entrada, a apresentação visual nem a
persistência.

Leia antes as fontes obrigatórias indicadas em `dependencies.json`. A
consistência geral continua protegida por `gerard-consistencia-estado`; esta
skill localiza somente o protocolo de propagação.

## Contrato conceitual

Uma alteração preserva a identidade do papel. O fato publicado significa:

> O valor do papel identificado foi alterado para um novo valor.

O publicador informa a identidade do papel, o novo valor e a origem factual
quando ela for necessária. Ele não conhece estado final, relação final,
texto, diagramas ou outros consumidores.

O fluxo é:

1. o papel de origem publica a mudança do próprio valor;
2. a relação estrutural pertinente decide se a mudança lhe interessa;
3. a relação reconcilia uma única vez os papéis que coordena;
4. o estado semântico compartilhado publica um snapshot coerente;
5. cada representação interessada lê desse snapshot o papel que materializa.

A representação onde o gesto começou identifica a origem da mudança, mas não
se torna fonte privilegiada nem cria uma direção permanente entre
representações.

## Ouvintes e localidade

O interesse pertence a quem conhece a relação. Um estado final, uma relação
final ou outro papel dependente pode observar a mudança, identificar o papel
de origem e consultar sua relação estrutural. O lançador nunca mantém uma
lista de dependentes.

Representações são ouvintes do snapshot reconciliado, não proprietárias da
regra que o produz. Texto, reta, Vergnaud e material concreto não recalculam
cada qual a sua versão da relação e não mantêm verdades paralelas.

## Limites

- Fórmulas e escolha do papel recalculado pertencem às relações estruturais.
- Mouse, toque e teclado pertencem aos handlers e adaptadores de interação.
- Geometria e aparência pertencem às representações.
- Registro factual e ação instrumental seguem suas skills proprietárias.
- Broadcast interno não constitui, por si só, ação do usuário nem evento
  persistente.
- O cliente remoto não recompõe uma relação que a API e o domínio não tenham
  descrito.

Após a conclusão da modelagem, alterações pela reta são exploratórias: podem
atualizar em memória todas as representações durante e depois do gesto, mas
não chamam a API, não persistem uma nova tentativa e não alimentam o log de
ações. Antes da conclusão, preserve confirmação, avaliação e registro já
definidos pelos protocolos existentes.

## Materialização nas plataformas

Desktop e web podem usar mecanismos técnicos diferentes de observação. Ambos
devem preservar o mesmo protocolo: um único estado reconciliado e múltiplas
projeções sensíveis à mudança. Componentes React renderizam a projeção
recebida; componentes Swing materializam o snapshot compartilhado. Nenhum dos
dois recebe autoridade para inventar relações do domínio.

## Verificação

Ao alterar este protocolo:

- mostre previamente o diff comportamental proposto;
- verifique todas as categorias que possuem número relativo ou transformação;
- durante o arraste e após a soltura, compare reta, papéis dependentes,
  diagrama e texto;
- confirme que a exploração posterior à conclusão não produz requisição nem
  persistência;
- execute o build web e a regressão completa do Gérard;
- preserve Composição de Medidas quando a mudança tratar apenas de valores
  assinados.

