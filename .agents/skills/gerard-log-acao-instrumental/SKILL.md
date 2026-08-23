---
name: gerard-log-acao-instrumental
description: Esquema e formato do log de ação instrumental do Gérard — o que precisa ser capturado a cada interação semanticamente constituída (Quadro 4.55). Use ao criar, revisar ou estender log de ação/erro ou dados que alimentarão o Modelador. Esta skill possui o esquema factual; o objeto semanticamente rico ou a relação estrutural proprietária da regra possui e produz o registro e sua validação.
---

# Log de Ação Instrumental — Gérard

## Status

O esquema abaixo vem do material de pesquisa (Quadro 4.55, "Análise da tarefa"). É uma referência de estrutura teórica — não presumir que os logs atuais do Gérard já seguem esse formato inteiro sem verificar. Antes de estender/criar um log, comparar com o que já existe e reportar o que falta, em vez de reescrever o que já funciona.

## Comparação com o log real (checado em 2026-07-20)

`gerard/pesquisador/log/EventoLogGerard.java` já é uma implementação real e ativa de log, e a comparação com o Quadro 4.55 é majoritariamente positiva:

- **"Tarefa de Interação" já usa os termos de Shneiderman no código real**, não é só teoria: `registrarAcaoGranular("SELECIONAR", ...)`, `"ORIENTACAO"`, `"CAMINHO"`, `"POSICIONAR"`, `"TEXTO"`, `"QUANTIFICAR"` aparecem literalmente como primeiro argumento em dezenas de pontos de `Main.java` (ex.: linhas 9178, 9196, 9202, 9207, 10297, 10388, 10496). Essa parte do esquema teórico já é comportamento real, não é lacuna.
- **O log real tem mais campos que o Quadro 4.55, não menos**: além de usuário/problema/tentativa/tarefa/C-E/instrumento-organização/instrumento-artefato/função-do-artefato/regras (que já existem, com nomes próximos: `usuario`, `problema`, `tentativa`, `tarefa`, `ce`, `instrumento_organizacao`, `instrumento_artefato`, `funcao_do_artefato`, `regras`), `EventoLogGerard` também registra `sessao`, `situacao_versao_id`, `situacao_grupo_id`, `idioma_situacao`, `categoria`, `enunciado`, `origem_evento`, `detalhes`, `propriedade_acao`, `mudanca_observavel`, `tentativa_numero_situacao`, `natureza_acao`, `efeito_acao`.
- **Diferença real de estrutura**: o Quadro 4.55 tem um único campo "Função → Invariantes"; o log real tem **quatro** campos de invariante (`invariante_origem`, `invariante_codigo`, `invariante_simbolico`, `invariante_observacao`) — uma decomposição mais granular do mesmo conceito, não uma lacuna a preencher.
- **Não verificado**: se o campo `usuario` do log real corresponde a um identificador numérico como o "04" do exemplo do material, ou a outra forma de identificação — não confirmei o formato exato usado em runtime.

Ao estender o log, siga o padrão de "acrescentar campos ao final preservando leitura de logs antigos" já usado em `EventoLogGerard.deTsv()` (comentário: "Os quatro campos de invariante foram acrescentados ao final para preservar a leitura dos logs produzidos pelas versões anteriores") — é a convenção já estabelecida no código real, não uma sugestão nova.

## Propriedade do registro

Decisão da usuária em 2026-08-14: o log da ação instrumental pertence ao
Objeto Semanticamente Rico ou à relação estrutural que possui o conhecimento
necessário para constituir e avaliar a ação. Esse proprietário produz o
registro factual, inclusive C/E quando aplicável, sem delegar a avaliação a um
Monitor ou a um serviço central.

Uma ação instrumental produz exatamente um registro e um `action_id`, ainda
que envolva vários objetos semânticos. Nesse caso, o proprietário é o menor
objeto rico relacional ou agregado de escopo fechado capaz de possuir o
conhecimento da ação completa. Os objetos envolvidos são referências de
participação no mesmo registro; não originam cópias da ação.

Persistência não equivale a propriedade semântica. Uma porta injetável pode
transportar e gravar o registro produzido pelo objeto, mas não o interpreta,
não recalcula C/E e não passa a possuir o conhecimento registrado. O mesmo
registro pode servir a auditoria, testes, análise qualitativa e aprendizagem
do Modelador.

### Implementação piloto — P2.5A, 2026-08-15

`RegistroAcaoInstrumental` é o valor factual comum da primeira migração. Para
o protocolo `TEXTO` da incógnita, ele é produzido por
`IncognitaQuantitativa` e contém identidade, protocolo, proprietário, alvo,
categoria, resultado tipado, diagnóstico, valores, regra semântica, contexto
instrumental e participantes. `LoggerInteracaoGerard` grava no máximo uma
linha por `action_id`; reenvios do mesmo registro são idempotentes e eventos
correlatos não são reinterpretados como novas ações.

O caso entregue ao Modelador preserva `action_id`, avaliação, tipo de erro e
participantes em colunas acrescentadas ao final de `diagnosticos_tarefa.tsv`.
Arquivos antigos com 11 ou 15 colunas continuam válidos. Este estado vale
somente para `TEXTO` da incógnita; os demais protocolos ainda não devem ser
descritos como migrados.

## Esquema de captura (Quadro 4.55)

Cada ação instrumental registrada deve poder responder:

| Campo | O que captura | Exemplo do material |
|---|---|---|
| Usuário | identificação de quem realizou a ação | 04 |
| Problema | qual problema/situação está em execução | 05 |
| Tentativa | número da tentativa | 01 |
| Tarefa | descrição do que estava sendo feito | "Identificar o cardinal do referente" |
| Tarefa de Interação | qual protocolo de mouse foi usado (Shneiderman, 1998: Selecionar, Posicionar, Orientar, Quantificar, Caminho, Texto) | "Selecionar" |
| C/E | se a tentativa foi certa ou errada | C |
| Instrumento → Organização | o que o usuário fez, na prática | "Seleção do número que corresponde ao cardinal do referente" |
| Instrumento → Artefato | qual elemento de UI foi usado | "Número 7 do enunciado" |
| Função → Representação | o que aquele artefato representa no domínio | "Representação do cardinal do referente" |
| Função → Invariantes | o que permanece verdadeiro | "O número representa o referente da medida" |
| Função → Regras | a regra que rege se a ação é válida | "O número que representa o cardinal do referente pode ser arrastado do enunciado para a legenda" |

## Regras de uso

1. Todo novo tipo de interação (novo protocolo de mouse, novo tipo de tela) deve ser capaz de preencher todos os campos acima antes de ser considerado "logado corretamente".
2. O campo "Tarefa de Interação" só aceita um dos seis valores de Shneiderman (Selecionar, Posicionar, Orientar, Quantificar, Caminho, Texto) — não é texto livre. (Confirmado: é exatamente assim que o código real já usa esse campo.)
3. Não inventar valores para os campos "Invariantes" e "Regras". Relações
   estruturais e regras computacionais vêm do domínio; invariantes operatórios
   mobilizados são atribuição exclusiva do pesquisador humano.
4. Ao encontrar um log existente que não segue esse esquema, reportar a lacuna ao usuário antes de alterar — não presumir que o esquema antigo estava errado.
5. O objeto proprietário deve produzir o registro da ação como valor factual
   tipado. Escrita em TSV, arquivo ou banco permanece numa porta de
   infraestrutura, sem retirar do objeto a propriedade do log.
6. Nunca gerar um registro de ação por objeto participante. Preservar um único
   `action_id` e representar os participantes como referências no registro do
   proprietário relacional ou agregado.
