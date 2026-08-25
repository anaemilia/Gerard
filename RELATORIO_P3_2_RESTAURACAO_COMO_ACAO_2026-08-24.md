# Relatório P3.2 — restauração como ação própria

**Data:** 2026-08-24

**Escopo:** comandos “Restaurar elementos fora do diagrama” e “Restaurar
diagrama” na branch arquitetural integrada.

## Decisão validada pelas skills

O grafo de consulta foi validado antes da alteração (19 nós e 56 relações).
Foram confrontadas as fontes de domínio, localidade do conhecimento, objetos
orientados ao conhecimento, consistência de estado, handlers, ação
instrumental e eventos semânticos.

A restauração é uma ação instrumental do participante, não uma tentativa de
posicionamento e não uma quarta rejeição. Como coordena a tentativa/modelagem e
pode alcançar vários objetos, pertence ao agregado semântico de menor escopo
que conhece o comando completo. O botão Swing não cria a identidade nem possui
a regra; o logger não avalia nem reinterpreta o registro.

## Implementação

- `TentativaModelagemAditiva` constitui a ação sem dependência de Swing/AWT;
- `TipoRestauracaoModelagem` distingue `ELEMENTOS_FORA_DO_DIAGRAMA` e
  `DIAGRAMA_COMPLETO`;
- `RegistroAcaoRestauracaoModelagem` conserva um `action_id`, o escopo, a
  tentativa, a origem, os papéis participantes e as sequências encerradas;
- cada `PapelQuantitativo` aplica somente sua mudança local de
  contagem/bloqueio;
- `Main.TelaGerard` solicita a ação, entrega o valor produzido ao logger e
  executa a restauração visual já existente;
- a linha da restauração usa protocolo `SELECIONAR`, C/E “-” e
  `rejection_sequence_id` vazio.

## Invariantes preservadas

- um comando gera uma ação, ainda que vários papéis participem;
- comandos Restaurar consecutivos geram `action_id` diferentes;
- sequências encerradas não são fundidas com a ação que as encerra;
- os dois escopos de restauração permanecem distintos;
- nenhuma geometria, sincronização, ajuda ou aparência foi alterada;
- a leitura de logs antigos permanece inalterada, pois não houve nova coluna.

## Verificação

O harness `TestePilotoTentativasRejeitadas` cobre a ação própria, a distinção
dos dois escopos, a restauração de vários papéis sem duplicação e a abertura de
uma sequência nova após a restauração. O verificador determinístico protege a
localidade, a independência de Swing/AWT, o `action_id` exclusivo e a ausência
de associação da restauração a uma sequência rejeitada.

Resultado posterior à integração na árvore ativa:

- 477 fontes Java compilados e JAR gerado;
- 86 harnesses Java compilados;
- 82 harnesses executados e aprovados;
- 4 harnesses gráficos compilados e não executados por exigirem display;
- 0 testes obsoletos e 0 falhas;
- `scripts/verificar_regressao_gerard.py`: **APROVADO**, incluindo as travas
  novas da P3.2;
- grafo de skills novamente validado: 19 nós e 56 relações.
