# Instruções para o Claude Code neste repositório (Gérard)

Estas regras existem porque já aconteceu de conteúdo de interface ser inventado
(um painel de mensagem em App.tsx com 11+ strings em português hardcoded, sem
vir de mensagens_pt.properties nem corresponder a nenhum mecanismo real do
desktop). O objetivo aqui é impedir isso na origem, não só detectar depois.

## Regra 1 — nunca inventar conteúdo de interface

Todo texto, mensagem, painel, tip ou tela apresentado ao usuário final tem que
vir de uma fonte real e verificável — nunca ser composto livremente.

Fontes válidas, nesta ordem de busca:
1. `mensagens_pt.properties` (ou arquivo de i18n equivalente) — se o texto já
   existe lá, use-o literalmente.
2. O código do Gérard desktop (Swing) já existente, demonstrando o mecanismo
   real (uma tela, um diálogo, uma mensagem de erro/sucesso já implementada).
3. O log curado (dados curados), quando o conteúdo vem de lá.

Se você não encontrar o texto/mecanismo em NENHUMA dessas fontes:
- NÃO invente uma versão plausível.
- Declare explicitamente: "não encontrei fonte real para esta
  mensagem/tela/mecanismo" e pare.
- Apresente a situação ao usuário e deixe a decisão com ela: criar a entrada
  em mensagens_pt.properties primeiro, apontar onde no desktop isso deveria
  existir, ou descartar a funcionalidade.

Isso vale para qualquer string voltada ao usuário — mensagens de erro/sucesso,
tips, rótulos, textos de ajuda, conteúdo de painéis — não só "telas" no
sentido visual.

## Regra 2 — critério de correção: ação concluída via protocolo de mouse

Não é a inspeção de código, de payload JSON ou de dado curado que decide se
algo funciona: é a ação do usuário concluída de verdade na interface, através
de um dos 6 protocolos de mouse de Shneiderman (apontar-e-clicar,
arrastar-e-soltar, etc.). Antes de declarar qualquer coisa "corrigida",
"funcionando" ou "validada", produza evidência concreta de que a interação
real foi até o fim: uma captura de tela do resultado, uma gravação, ou pelo
menos o log da sequência real de chamadas HTTP disparada pela interação (não
uma chamada isolada simulando o payload). Se não conseguir gerar essa
evidência sozinho no ambiente atual, diga isso explicitamente em vez de
inferir sucesso a partir do código.

## Regra 3 — ordem de investigação e implementação ao encontrar uma situação sem cobertura

Antes de concluir "falta curadoria de dados" para qualquer situação-problema,
siga esta ordem — sem pular etapa:

1. Identificar todas as classes de domínio relacionadas à categoria em
   questão e verificar quais dependem só de dado básico vs. quais exigem dado
   adicional (XML de narrativa rica, arquivo externo, etc.).
2. Se existir uma classe candidata que resolva o caso com dado básico,
   confirme explicitamente que ela está num estado válido de desacoplamento:
   é regra/modelo puro, sem dependência de interface (Swing, HTTP, parsing de
   apresentação), respeitando a arquitetura já decidida (regras
   matemáticas/semânticas em modelos/serviços/políticas; comportamento de
   interface em controladores/componentes). Documente essa verificação — não
   assuma.
   - Se a classe NÃO estiver nesse estado válido, o problema real é esse
     acoplamento, e ele precisa ser corrigido antes de qualquer rota — isso
     não é "falta de curadoria" nem é "só falta ligar a rota", é dívida
     arquitetural a resolver primeiro.
3. Confirmado o desacoplamento, o objeto de domínio deve ser ENRIQUECIDO —
   levado ao nível semântico necessário para cobrir o caso de verdade — e
   qualquer funcionalidade equivalente presente em versões antigas/simples
   que se sobreponha a ela deve ser DESATIVADA nesse mesmo passo. Nunca deixe
   o caminho simples antigo e o objeto novo enriquecido ativos em paralelo —
   isso gera duas fontes de verdade divergentes.
4. Só depois do objeto enriquecido e das versões antigas desativadas é que a
   criação/ajuste da rota web que conecta essa classe é a ação correta.
5. Só depois de esgotar os passos 1 a 4 — ou seja, confirmar que NENHUM
   caminho de domínio já implementado (mesmo depois de enriquecido) cobre o
   caso com os dados hoje disponíveis — é válido dizer "falta curadoria".
   Nesse caso, aponte exatamente qual dado curado está faltando e para qual
   situação.
