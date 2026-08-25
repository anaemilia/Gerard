---
name: gerard-ajuda-adaptativa
description: Arquitetura-alvo da Ajuda Adaptativa do Gérard, com o Agente Modelador concentrando a aprendizagem e os proprietários semânticos selecionando ajudas em repertórios locais; Monitor e ZDP pertencem somente à arquitetura anterior. Use ao discutir ou implementar avaliação factual de ações, regras J48/PART e Apriori, carregamento do Modelo do Usuário, escolha de scaffolding ou migração dos agentes legados Monitor/ZDP. Confronte sempre com domínio, localidade, modelo do usuário, scaffolding e registro factual.
---

# Ajuda Adaptativa — Gérard

## Decisão arquitetural vigente — 2026-08-14

A arquitetura-alvo possui somente um agente: o Agente Modelador, porque a ele
pertencem a aprendizagem por J48/PART e Apriori e a publicação de regras. O
Agente Monitor e o Agente ZDP são componentes legados a migrar e retirar.
Zona de Desenvolvimento Proximal continua sendo fundamento
pedagógico, enquanto sua operacionalização fica distribuída pelos
proprietários semânticos e seus repertórios locais.

Fluxo normativo:

1. Objetos ricos da representação possuem e produzem os registros factuais dos
   gestos que os envolvem. Objetos semânticos e relações estruturais possuem e
   produzem os registros das ações constituídas, seus diagnósticos factuais e
   resultados tipados de validação, incluindo C/E quando aplicável.
   Uma ação conserva um único `action_id`; se envolver vários objetos, o menor
   proprietário relacional ou agregado registra a ação uma vez e referencia
   os participantes.
2. A infraestrutura apenas transporta e persiste esses registros, preservando
   seu proprietário, e os disponibiliza ao Modelador.
3. O Agente Modelador transforma os registros em casos, executa J48/PART e
   Apriori e publica regras explicáveis numa versão do Modelo do Usuário.
4. O login carrega uma fotografia versionada do modelo, estável durante a
   sessão.
5. Cada proprietário semântico consulta apenas a projeção imutável que lhe é
   relevante e escolhe uma ajuda do próprio repertório.
6. A interface materializa a decisão; o objeto rico correspondente produz o
   registro da ajuda efetivamente exibida, e a infraestrutura o persiste.

### Fonte da tomada de decisão

Refinamento explícito da usuária em 2026-08-12: a tomada de decisão não é
pautada no perfil isolado. Perfil do aluno e perfil da aprendizagem são
dimensões integrantes do Modelo do Usuário; preferências podem orientar a
materialização da ajuda, mas não substituem as demais dimensões do modelo.

A seleção adaptativa deve resultar da projeção multidimensional pertinente do
Modelo do Usuário, combinada ao diagnóstico factual e às regras publicadas no
escopo do proprietário semântico. Nenhuma preferência isolada decide se,
quando ou qual ajuda pedagógica será aplicada. Um ensaio que altere somente
preferências testa personalização da interface, não a tomada de decisão
adaptativa completa.

Refinamento explícito da usuária em 2026-08-13: a tomada de decisão possui
dois níveis temporais complementares, mas não duas autoridades centrais:

1. **histórico/intersessões** — a fotografia versionada do Modelo do Usuário,
   carregada no login e mantida estável até o logout;
2. **contextual/intrasseção** — os fatos do Modelo da Situação/Solução que
   está sendo construído pelas ações do participante sobre elementos da
   interface, incluindo o estado relevante da tentativa e a sequência de
   situações interativas.

Esses fatos correntes não atualizam clandestinamente a fotografia. Eles são
projeções tipadas e mínimas entregues ao proprietário semântico que possui o
conhecimento e o repertório. O próprio proprietário combina fotografia,
estado atual e diagnóstico factual por seu mecanismo local de decisão. A
interface e serviços genéricos não interpretam essa combinação.

Por decisão da usuária em 2026-08-13, nenhuma política operacional nova pode
ser inventada como valor padrão. Uma decisão deve ser rastreável a uma regra
publicada com proveniência de casos, a uma referência identificável ou a uma
observação registrada do mestrado/doutorado. A observação de tédio nas sessões
reais justifica investigar progressão de dificuldade, mas o sistema não pode
inferir automaticamente o estado mental "tédio". Dificuldade da próxima
situação e intensidade de scaffolding permanecem controles distintos.

O aprendizado é centralizado no Modelador; a aplicação é distribuída por
localidade do conhecimento. Papéis cuidam de regras locais; relações
estruturais, de regras entre papéis; tentativa e situação-problema, de regras
que exigem esses escopos. Nenhum objeto recebe Weka, arquivo de log, Swing,
geometria ou o modelo mutável completo.

O código ainda implementa parcialmente a arquitetura anterior de três
agentes. `AgenteMonitor` e `AgenteZDP` são legados em migração e não definem
a arquitetura-alvo. Não remover ou renomear esses componentes em massa: migrar um fluxo por vez,
preservando logs, observadores e comportamento até a regressão passar.

### Estado de implementação — P2.2B, 2026-08-13

O primeiro proprietário semântico foi implementado no piloto como
`gerard.dominio.campoaditivo.IncognitaQuantitativa`. A designação da incógnita
original é explícita e estável; não é inferida novamente pela simples ausência
de valor do papel.

O proprietário recebe `FatosSelecaoAjudaIncognita`: diagnóstico já produzido
pelo papel ou pela relação estrutural e ordinal de rejeição já produzido pela
sequência da tentativa. Ele não recalcula nenhum dos dois. A seleção consulta
uma projeção que solicita `NIVEL_TAREFAS` e `DIAGNOSTICO_TAREFA`, aplica somente
regras `PUBLICADA` do escopo `PAPEL` e escolhe somente entre `AG_EMLQ`, `AG_EME`
e `AG_EMCME` do seu repertório local.

O vocabulário local de condições é aberto apenas por alteração consciente do
proprietário: `diagnostico_factual`, `ordem_rejeicao`, `categoria`,
`papel_alvo`, `nivel_tarefa` e `suporte_anterior`. Condição desconhecida é erro
de publicação, não um convite para a interface interpretá-la. Se nenhuma regra
se aplica, a ausência de decisão é explícita. Se duas regras se aplicam ao
mesmo tempo, o objeto recusa inventar prioridade: a ambiguidade deve ser
resolvida na publicação pelo Modelador.

Esta fase produz somente `DecisaoAjuda` abstrata. Não foi conectada a
`Main.java`, Swing, login, Monitor ou ZDP; essa integração pertence à P2.3.

### Estado de implementação — P2.3A, 2026-08-13

`gerard.adaptacao.sessao.SessaoAdaptativaUsuario` passou a criar a fotografia
na confirmação do login real, antes de `LoggerInteracaoGerard` trocar o usuário
ativo. A sessão conserva a mesma instância até `encerrarNoLogout`; repetir o
login do mesmo usuário não a refaz, e autenticar outro usuário exige encerrar a
sessão anterior.

`FotografiaModeloUsuario.carregarNoLogin` gera um identificador reproduzível
`conteudo-sha256:` sobre as dimensões e regras efetivamente congeladas. Esse
identificador registra o conteúdo carregado e não se confunde com a versão
editorial de cada regra publicada pelo Modelador.

Desde a P2.4A.1, a produção usa
`RepositorioRegrasAdaptativasPublicadas` como
`FonteRegrasAdaptativasCandidatas`. O catálogo operacional fica em
`~/Gerard/analises/regras_adaptativas_publicadas.jsonl`, separado do TSV de
inferência. A ausência desse arquivo equivale a nenhuma publicação; o login não
fabrica regra. Os TSV de inferência e a base JSON de protocolos humanos
permanecem fontes históricas/experimentais e não são promovidos
automaticamente.

### Estado de implementação — P2.3B, 2026-08-13

`ProprietarioRepertorioAjuda` passou a declarar o menor conjunto de dimensões
do Modelo do Usuário de que necessita. Para `IncognitaQuantitativa`, esse
conjunto é exatamente `NIVEL_TAREFAS` e `DIAGNOSTICO_TAREFA`. A sessão usa a
declaração do proprietário para projetar a fotografia ativa; não escolhe
dimensões, não interpreta condições e não decide ajuda.

`ProjetorContextoAdaptativoIncognita` é uma fronteira de aplicação. Ele obtém a
designação original por `ResolvedorIncognitaCurada`, obtém o papel no modelo
semântico canônico e solicita o contexto à sessão. Divergência entre
`termo_desconhecido` e o símbolo `?` produz
`DESIGNACAO_INCONSISTENTE`; nenhum lado é eleito silenciosamente. A ausência de
situação, designação ou fotografia também permanece explícita.

`Main.java` somente atualiza e conserva o resultado ao carregar uma situação ou
confirmar o login. P2.3B não invoca `selecionarAjuda`, não altera mensagens,
representações ou registros, e não retira a autoridade do fluxo legado. A
materialização e o registro da decisão continuam reservados à P2.3C.

### Estado de implementação — P2.3C, 2026-08-13

`ExecutorAjudaIncognita` liga o contexto da P2.3B à seleção já pertencente a
`IncognitaQuantitativa`. Ele não conhece regra, código de ajuda, Swing nem
formato de log. Uma decisão é registrada antes da apresentação, inclusive
quando o resultado explícito é `SEM_REGRA_APLICAVEL`.

Quando existe regra publicada aplicável, `MaterializadorDecisaoAjudaSwing`
traduz apenas a sintaxe visual do apoio recebido. A representação confirma cada
efeito efetivamente materializado; somente então
`RegistradorEventosAjudaLogGerard` registra `FEEDBACK_EXIBIDO`. `AG_EMCME`
produz duas confirmações factuais: affordance manipulativa do material concreto
ativa e mensagem visual exibida. A confirmação não atribui à representação a
regra que tornou o material elegível.

Os registros carregam `action_id`, `rejection_sequence_id`, versão da fotografia
do Modelo do Usuário, regra e versão, proprietário semântico, diagnóstico
factual e ajuda. A decisão usa origem `INFERENCIA_COMPUTACIONAL`; a
materialização, origem `SISTEMA`. Nenhum evento afirma percepção, compreensão
ou conceito-em-ação do participante.

A fonte de produção lê o catálogo explicitamente publicado pelo Modelador.
Sem uma publicação real para o usuário, a decisão local
`SEM_REGRA_APLICAVEL` é registrada e o fluxo visual legado é usado como
fallback de compatibilidade; as fixtures publicadas dos harnesses demonstram
`AG_EMLQ` → `AG_EME` → `AG_EMCME` sem criar uma segunda autoridade para a
mesma rejeição. O diagnóstico genérico
`VALOR_INCORRETO` ainda é um adaptador transitório do booleano já calculado pelo
legado; deve ser substituído pelo diagnóstico factual rico do proprietário
quando essa fronteira for migrada.

`AG_AE` tornou-se adaptativo na P2.4A (2026-08-13). O
`PapelQuantitativoPosicionavel` possui o repertório local, consulta somente sua
projeção multidimensional da fotografia e pode selecionar `AG_AE` quando uma
regra publicada pelo Modelador se aplica ao diagnóstico factual de divergência
entre o papel esperado e o papel de destino. O
`MaterializadorAtracaoMagneticaAdaptativa` apenas ativa a affordance para a
chave daquele papel; não consulta regras nem decide ajuda. `Main` consulta esse
estado antes de chamar a atração e a centralização já existentes. Sem regra
publicada a atração permanece desligada; a correção do posicionamento e a
restauração/troca da atividade encerram a ativação. A geometria não foi
alterada. O botão legado “Ver dica” não fez parte do escopo restrito da P2.4A
e sua classificação continua pendente.

### Estado de implementação — P2.4A.1, 2026-08-13

`RepositorioRegrasAdaptativasPublicadas` é simultaneamente a porta de escrita
editorial do Modelador e a fonte de leitura da sessão. Cada linha JSONL exige
esquema versionado, usuário destinatário, identidade e versão da regra,
algoritmo `PART`, `J48`, `J48.PART` ou `APRIORI`, data, proveniência, proprietário
semântico, escopo, condições, código de ajuda, métricas disponíveis e estado
explícito `PUBLICADA`. Registro ausente devolve lista vazia; registro malformado
falha fechado e não desliga silenciosamente uma ajuda esperada.

`AgenteModelador.publicarRegrasAdaptativas` substitui atomicamente o conjunto
publicado de um usuário. Essa operação recebe regras já explicitadas: não
analisa a saída textual do Weka, não escolhe ajuda e não cria condições. A
mineração automática continua gravando somente `regras_inferidas.tsv` com
estado experimental. Publicações novas tornam-se elegíveis apenas no próximo
login, porque a fotografia ativa permanece imutável.

As descrições da sociedade de três agentes e do Agente ZDP mantidas abaixo
são histórico da tese, do relatório e da implementação anterior. Elas não
prevalecem sobre esta seção.

### Estado de implementação — P2.4A.2, 2026-08-13

`DecisaoAjuda` conserva a versão da fotografia, a identidade da regra, o
algoritmo de origem e a proveniência dos casos da regra publicada. O evento de
decisão e a confirmação de materialização transportam esses dados sem mudar
qual ajuda é selecionada. Os objetos
`FatosSelecaoAjudaIncognita` e `FatosSelecaoAjudaPosicionamento` já são as
projeções locais do contexto intrasseção; não criar um contexto global que
duplique o conhecimento de todos os proprietários.

### Estado de implementação — P2.5A, 2026-08-15

O primeiro fluxo completo migrado é o protocolo `TEXTO` aplicado ao valor da
incógnita. `IncognitaQuantitativa` recebe o valor proposto, o valor esperado
produzido pelo Modelo da Situação/Solução e o contexto instrumental, interpreta
a correspondência e produz um `RegistroAcaoInstrumental` com um único
`action_id`, C/E quando aplicável, diagnóstico factual e referências a todos
os participantes semânticos. Participantes não criam registros adicionais.

`Main.java` fornece fatos de contexto e solicita a avaliação, mas não compara
os números. Nesse fluxo ela não chama `AgenteMonitor` nem `AgenteZDP`. O mesmo
registro é persistido idempotentemente pelo logger e convertido em um único
caso por `ConectorVereditoModelador`, sem recalcular C/E. O caso conserva
`action_id`, avaliação, tipo de erro e participantes; essas colunas foram
acrescentadas ao final do TSV, com leitura retrocompatível, e a avaliação
passou a integrar o conjunto de atributos disponível ao PART e ao Apriori.

A fotografia do Modelo do Usuário continua necessária para selecionar ajuda,
mas sua ausência não apaga a `IncognitaQuantitativa` já resolvida: a avaliação
factual da ação pertence ao domínio e não depende de haver regra adaptativa
publicada. O adaptador transitório `DiagnosticoCompatibilidadeIncognita` foi
retirado porque o diagnóstico agora nasce no proprietário semântico.

Esta é uma migração por fluxo. Seleção de categoria, posicionamento, sinal e
outros protocolos ainda podem atravessar Monitor/ZDP no legado e devem ser
migrados separadamente, sem remoção em massa.

### Estado de integração — P4.1, 2026-08-25

O fluxo `TEXTO` da incógnita está conectado à interface de produção. A
`IncognitaQuantitativa` reutiliza o mesmo `PapelQuantitativo` que já possui a
identidade da ação e a sequência de rejeições; a projeção adaptativa não cria
outra contagem. A ausência de fotografia ou de regra publicada impede somente
a seleção adaptativa, não a avaliação factual da ação.

Nesse ramo, `Main.confirmarValorIncognitaTexto` entrega os valores e o contexto
instrumental ao proprietário. Não chama Monitor, ZDP nem o conector legado de
veredito. O `RegistroAcaoInstrumental` retornado é entregue uma vez ao
`LoggerInteracaoGerard` e uma vez ao `ConectorVereditoModelador`; os demais
papéis da situação permanecem referências no mesmo `action_id`.

Quando há rejeição e fotografia ativa, `ExecutorAjudaIncognita` solicita a
seleção local e a interface Swing apenas materializa a `DecisaoAjuda`. Sem regra
publicada aplicável, o resultado explícito é `SEM_REGRA_APLICAVEL` e o apoio
visual anterior permanece como fallback de compatibilidade. Uma publicação do
Modelador feita durante a sessão só se torna elegível depois de logout e novo
login.

O harness `TesteP4_1FluxoTextoIncognita` verifica ação única, múltiplos
participantes por referência, diagnóstico local, sequência, idempotência do log
e do caso, rastreabilidade da decisão e estabilidade da fotografia. O verificador
`scripts/verificar_regressao_gerard.py` protege deterministamente a ausência de
Monitor/ZDP no ramo `TEXTO` e a independência do proprietário em relação a
Swing/AWT.

### Estado de integração — P5.1, 2026-08-25

A família de seleção/confirmação de categoria foi retirada do Monitor e do
ZDP. A categoria curada continua pertencendo à `SituacaoProblemaAditiva`; o
menor agregado que conhece essa situação, a escolha do participante e o curso
da interação é `TentativaClassificacaoCategoriaAditiva`. Ele produz os
registros factuais da escolha e da resposta ao questionamento, com um
`action_id` novo para cada ato.

Clique em categoria divergente e concordância com essa categoria são
rejeições distintas da mesma sequência. Discordar da categoria divergente é
uma ação correta e não recebe `rejection_sequence_id`, mas não zera a
sequência: a categoria da situação ainda precisa ser acertada. O terceiro erro
encerra a tentativa e solicita a reexplicação já existente, decisão sustentada
pelas observações de campo e já confirmada pela usuária; não é uma nova regra
inventada durante a migração.

`Main.java` somente fornece contexto instrumental, persiste o mesmo registro,
encaminha-o ao Modelador e materializa o desfecho. A escolha inicial registra
suporte anterior `NENHUM`; a resposta ao questionamento registra suporte
factual `PARCIAL`. A materialização narrativa posterior continua sendo um
fluxo de compatibilidade já existente e não autoriza ampliar o repertório de
seis códigos nem fabricar uma regra adaptativa publicada.

O harness `TesteP5_1ClassificacaoCategoria` protege a separação entre ações,
a sequência compartilhada somente por rejeições, a idempotência do logger e
do Modelador e a aceitação da categoria curada. Em 2026-08-25, a linha de base
compilou 520 fontes e 88 harnesses, executou 84 sem falhas e manteve quatro
testes gráficos compilados para execução em ambiente com display.

## Histórico da proposta teórica e da implementação anterior

O conteúdo histórico abaixo vem do material de pesquisa/tese do usuário e de
auditorias posteriores. Ele documenta a origem e o legado, não substitui a
decisão vigente no topo.

**Checado em 2026-07-20**: busquei no código por `AgenteMonitor`, `AgenteZDP`, `AgenteModelador`, `ModeloDoUsuario`/`ModeloUsuario` e qualquer arquitetura de threads produtor-consumidor equivalente — nada encontrado em `src/`. Confirma que nada desta arquitetura existe implementado, nem parcialmente, no código atual do Gérard.

**Atualizado em 2026-07-20**: os três arquivos de `references/` (`agente-monitor.md`, `agente-zdp.md`, `agente-modelador.md`) estão completos. Duas skills companheiras também foram instaladas: `gerard-log-acao-instrumental` (esquema factual do Quadro 4.55; desde a decisão de 2026-08-14, o registro concreto pertence ao objeto rico proprietário da ação) e `gerard-modelo-usuario` (esquema das dimensões do Modelo do Usuário, Quadro 5.60; na arquitetura-alvo, o Modelador publica e os proprietários semânticos consultam projeções). Ver a seção "Relação com outras skills" abaixo.

**Atualizado em 2026-07-22**: `agente-zdp.md` e `agente-modelador.md` incorporaram material do relatório de pesquisa "Análise de situações interativas no Gerard..." (Queiroz, 2026, Univasf), que a lacuna nº4 abaixo já apontava como necessário. Esse relatório dá camadas progressivas de ajuda (N0–N7), regras condição→ação e uma escala de indícios de reorganização pós-ajuda — a lacuna nº4 fica **parcialmente** endereçada (ver detalhe na nota atualizada abaixo).

**Atualizado em 2026-07-25**: `AgenteZDP` (`src/gerard/agente/zdp/AgenteZDP.java`) já foi implementado — síncrono, chamado direto após o Agente Monitor avaliar (mesmo padrão já em produção do Monitor e da ação 1 do Modelador), decide `CamadaEstrategiaZDP` (CONDUCAO_MINIMA/QUESTIONAMENTO_LEVE/AJUDA_ESPECIFICA/RETIRADA_PROGRESSIVA — camadas N0-N2, ver `agente-zdp.md`). Os três agentes (Monitor, ZDP, Modelador) agora têm interface de observador própria (`OuvinteVeredictoAgenteMonitor`, `OuvinteEstrategiaAgenteZDP`, `OuvinteCasoAgenteModelador`), consumida por `FaixaLateralAtividadeAgentes` (`src/gerard/pesquisador/FaixaLateralAtividadeAgentes.java`) — uma faixa recolhida por padrão, embutida na tela principal (`TelaGerard`), que a pesquisadora expande (com a mesma senha do botão "Visão de pesquisador") pra ver um feed ao vivo da atividade dos três agentes enquanto observa o estudante ao lado. `IndicadorAgenteMonitor` (o LED) continua sendo o único sinal que o estudante vê — a faixa é só para quem autentica como pesquisador.

**Atualizado em 2026-07-26**: `agente-zdp.md` ganhou uma nova seção, "Catálogo de mensagens de ajuda por tipo de erro", trazida de um documento de 2011 (`ajudas-modificadoAnaEmilai.doc`, período de TCC — bem anterior à arquitetura de três agentes) a pedido do usuário. É uma tabela condição×estratégia×mensagem-padrão×mensagem-personalizada que serve de material histórico para o repositório de **Conteúdo Pedagógico** consultado pelo Agente ZDP — não é conteúdo pronto para produção (tem texto claramente de rascunho, ver a seção pra cautelas específicas). Um segundo complemento na mesma seção (tabela colada diretamente pela usuária, confirmada por ela como sendo também de 2011) detalha as mensagens de categorização por subtipo — mas inclui 3 subcategorias do **campo multiplicativo** (Multiplicação, Divisão por partes, Divisão por cotas) que não existem em nenhum lugar do código atual (só campo aditivo) — divergência sinalizada na seção, não resolvida.

**Atualizado em 2026-07-27**: `agente-zdp.md` ganhou a seção "Comunicabilidade (De Souza, 1999) como princípio de posicionamento das mensagens" — traz o Método de Avaliação de Comunicabilidade (Engenharia Semiótica) discutido com a usuária, uma tabela de resultado de mestrado que ela compartilhou (autoria/ano exatos ainda a confirmar) e o princípio que ela articulou: artefatos de ajuda devem ficar no local específico da tela correlato ao pensamento/dúvida do usuário, não centralizados. A tabela dá lastro empírico direto a duas decisões já tomadas no código (botão "Qual o próximo passo?" desabilitado, botão "Mais Dica" deixado de fora) e revela um dado negativo não documentado antes: mesmo os 3 botões "?" contextuais já implementados e situados não bastaram para resolver a dúvida dos usuários no teste. Fica em aberto onde reposicionar "Qual o próximo passo?" para um correlato mais específico, e como diferenciar tipos de dúvida sem usar rótulos de texto (ver seção para as duas saídas propostas, nenhuma decidida).

## Regras obrigatórias

1. Nunca presumir que algo aqui já existe no código sem verificar.
2. Nunca reescrever código existente para "bater" com esta especificação sem confirmação explícita do usuário.
3. Ao encontrar divergência entre esta skill e o código real, reportar ao usuário — não corrigir silenciosamente em nenhuma direção.
4. É material de referência para design e implementação futura sob orientação direta do usuário — não uma ordem de serviço.
5. Não manter duas autoridades para a mesma decisão: na arquitetura-alvo,
   `Main.java`, `AgenteMonitor` e `AgenteZDP` não validam em nome dos
   proprietários semânticos nem escolhem o scaffolding local.
6. Registrar na decisão adaptativa a versão do modelo, a regra aplicada, o
   proprietário semântico, o diagnóstico factual e o apoio escolhido.
7. Não transformar regras mineradas em invariantes operatórios ou afirmações
   automáticas sobre conceitos-em-ação; essa interpretação pertence ao
   pesquisador humano.
8. Não reduzir o Modelo do Usuário ao perfil nem usar uma preferência isolada
   como autoridade para a tomada de decisão adaptativa.

## Motivação (RFAs)

- RFA 1.1 — deve haver um agente responsável por observar o usuário.
- RFA 1.2 — o agente deve conhecer a forma correta de resolução do problema.
- RFA 1.3 — o agente deve deliberar sobre a melhor forma de ajuda.
- RFA 1.4 — o agente deve conhecer o que o usuário sabe e não sabe.
- RFA 1.5 — o agente deve possuir um conjunto de conteúdos pedagógicos.
- RFA 1.6 — o agente deve tratar situações em que o próximo estado do ambiente não é totalmente determinado pelo estado atual e pela ação do agente.

## Arquitetura histórica supersedida: sociedade de três agentes

O quadro abaixo descreve a proposta original e o legado ainda existente no
código. Não orientar novas implementações por ele.

| Agente | Papel | Arquitetura | Detalhes |
|---|---|---|---|
| Agente Monitor | Calculava/avaliava C/E; alvo: observar e registrar | Reativo simples | `references/agente-monitor.md` |
| Agente ZDP | Concentrava a estratégia; alvo: remover incrementalmente | Baseado em modelo | `references/agente-zdp.md` |
| Agente Modelador | Mantinha o modelo; alvo: aprender e publicar versões | Reativo simples | `references/agente-modelador.md` |

Ao auditar ou migrar código legado, leia o arquivo histórico do agente
envolvido para reconstruir suas percepções, ações e granularidade. Não use
essas referências para atribuir novas decisões ao Monitor ou ao ZDP.

## Comunicação histórica entre os agentes

Agentes colaborativos, usando concorrência cooperativa. Cada agente é operacionalizado como uma Thread produtor-consumidor. Sem protocolo de comunicação direta identificado — colaboram via dados compartilhados.

## Repositórios na arquitetura-alvo

- **Modelo do Usuário** — publicado pelo Modelador; projetado como fotografia
  imutável para os proprietários semânticos no login.
- **Modelo de domínio** — fonte das validações locais e relacionais; seus
  objetos e relações possuem e produzem os registros das ações e seus
  resultados factuais.
- **Objetos ricos da representação** — possuem e produzem os registros dos
  gestos observáveis que os envolvem, sem avaliação semântica.
- **Repertórios pedagógicos locais** — Mensagens, Automatização de passos,
  Mostrar Modelo Completo e outros apoios pertencem aos proprietários
  semânticos correspondentes.

## Propriedades formais da proposta histórica (Russell & Norvig)

- **Estratégico**: Agente ZDP e o próprio usuário podem modificar o ambiente.
- **Completamente observável**: sensores do Monitor têm acesso total ao estado.
- **Episódico**: cada episódio = percepção + ação.
- **Dinâmico**: usuário pode agir enquanto um agente delibera.
- **Discreto**: número finito de estados. Fórmula (não tratar como constante): `nº_operações × (2 × posições_por_operação) × última_ação × presença_de_ajuda`. No exemplo do material: 3×(2×2×2)×1×2 = 48 — muda se mais elementos forem inseridos na tela.

## Questões em aberto (observações do Codex, NÃO do material de pesquisa)

Estas notas são análise feita ao documentar o material — não estão na tese, não têm validação, e não devem ser tratadas como parte da especificação. Servem só para orientar discussão futura com o usuário.

1. **Sincronização entre threads**: os agentes são threads produtor-consumidor cooperativas sem protocolo de comunicação direto, mas leem/escrevem nos mesmos repositórios compartilhados (Modelo do Usuário, Ontologia, Conteúdo Pedagógico). Sem alguma forma de sincronização, há risco de condição de corrida — especialmente porque o ambiente é classificado como Dinâmico.
2. **Validade temporal da avaliação do Monitor**: o Monitor ignora deliberadamente o que muda depois da ação avaliada. Não está claro se há garantia de que a ação avaliada ainda é válida no momento em que a avaliação termina (ex.: usuário desfaz a ação durante a avaliação).
3. **Cardinalidade de estados é por tela, não do sistema**: o cálculo de 48 estados (Fig. 5.81) foi feito para uma configuração específica de tela. Com a intenção de 3 apps/telas separados (composição, transformação, comparação), cada um provavelmente tem cardinalidade própria — não existe um número único do sistema.
4. **Lacuna na operacionalização da ZDP — parcialmente endereçada em 2026-07-22, com mecanismo proposto em 2026-07-25**: o material original admite que falta um mecanismo verificável (com dados quantitativos) para confirmar que a ajuda oferecida está de fato na Zona de Desenvolvimento Proximal. O relatório de pesquisa 2026 (ver `agente-zdp.md`) dá camadas de ajuda concretas (N0–N7) e regras condição→ação derivadas de 556 ações observadas — mas o próprio relatório reconhece, na sua seção de limitações, que a validação foi formativa (protótipo em papel, intervenção humana simulando o sistema), não operacional. Em 2026-07-25, discussão com o usuário (motivada por checar a decisão contra o diagrama de Vergnaud de situações/schemes/significante — Figura 2, setas 2/2bis) produziu uma proposta concreta ainda não implementada: regras mineradas pelo Apriori (ver `agente-modelador.md`) carregariam uma força ajustável, fortalecida ou enfraquecida a cada nova evidência de mudança na corretude das ações do usuário, usando a escala de "indícios de reorganização após ajuda" (Tabelas 51/52) como sinal — ver seção "Mecanismo proposto" em `agente-zdp.md` para o desenho completo e as questões técnicas ainda em aberto. A pergunta original ("como confirmar quantitativamente que a ajuda está na ZDP") continua sem implementação, mas agora tem um desenho concreto de mecanismo, não só o princípio abstrato.
5. **Classificação "Multiagente" do ambiente nunca foi confirmada no material** — pode estar implícita, ou pode ser uma lacuna na fundamentação teórica (3 agentes + usuário sugerem que deveria estar presente ao lado de Estratégico/Observável/Episódico/Dinâmico/Discreto).

## Relação com outras skills do Gérard

- `gerard-consistencia-estado` — protege a propagação de estado já
  implementada; ajuda adaptativa não pode transformar sincronização em
  automatização pedagógica acidental.
- `gerard-scaffolding-interacao` — possui as definições e o vocabulário dos
  apoios oferecidos. Esta skill descreve como regras publicadas e contexto são
  entregues ao proprietário semântico, que seleciona dentro do repertório
  local; são conhecimentos complementares e não centralizados.
- `gerard-log-acao-instrumental` — dona do esquema factual (Quadro 4.55) disponibilizado ao Modelador depois que os proprietários semânticos produzem seus resultados e a infraestrutura os correlaciona e persiste. O consumo desse log pelo Agente Monitor pertence ao fluxo legado. Confirmado contra o código real: `EventoLogGerard.java` já usa os seis termos de Shneiderman ("SELECIONAR", "ORIENTACAO", "CAMINHO", "POSICIONAR", "TEXTO", "QUANTIFICAR") como valores do campo Tarefa de Interação, e tem mais campos que o quadro teórico, não menos.
- `gerard-modelo-usuario` — possui as dimensões, regras versionadas e projeções
  de leitura do modelo que o Modelador publica e os proprietários semânticos
  consultam. A implementação atual é parcial e deve ser confrontada com a
  arquitetura-alvo.
