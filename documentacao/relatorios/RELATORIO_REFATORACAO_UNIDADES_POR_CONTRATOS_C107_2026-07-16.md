# Relatório de alteração - refatoração das unidades visuais por contratos

**Data:** 2026-07-16  
**Ciclo:** C107  
**Tipo de intervenção:** arquitetura, generalização e redução de acoplamento.

## Síntese

A adição e a remoção de quadradinhos deixaram de depender diretamente de verificações concretas sobre `CirculoVenn`. As capacidades passaram a ser expressas por interfaces distintas, com uma classe abstrata para o estado comum e uma implementação polimórfica para as representações editáveis.

## Estrutura criada

- `RepresentacaoComUnidades`;
- `RepresentacaoComUnidadesAdicionaveis`;
- `RepresentacaoComUnidadesRemoviveis`;
- `OperacoesUnidadesVenn`;
- `ResultadoOperacaoUnidade`;
- `RepresentacaoComUnidadesAbstrata`;
- `RepresentacaoVennEditavel`.

## Comportamento preservado

- criação e remoção de quadradinhos;
- limite semântico oriundo da curadoria;
- feedback ao atingir a quantidade textual;
- reorganização do layout;
- sincronização com o estado semântico compartilhado;
- atualização do diagrama de Vergnaud;
- registro das ações no log;
- controles vetoriais `+` e `-`.

## Verificações

- compilação Ant/NetBeans: aprovada;
- teste de adição por contrato: aprovado;
- teste de remoção por contrato: aprovado;
- teste de limite semântico: aprovado;
- teste específico de contratos, herança e polimorfismo: aprovado;
- teste do estado semântico compartilhado: aprovado;
- regressão geral do Gérard: aprovada;
- vínculos: 210 versões e 72 grupos conceituais consistentes.
