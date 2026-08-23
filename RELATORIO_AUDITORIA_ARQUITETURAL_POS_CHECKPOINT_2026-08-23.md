# Auditoria arquitetural pós-checkpoint — 2026-08-23

## Escopo e critério

- Branch auditada: `codex/auditoria-arquitetural-pos-505553d`.
- Base: merge `505553d` do PR #24.
- A versão integrada foi analisada como estado correto de partida. Uma
  pendência só foi registrada quando há inconsistência com as definições e
  decisões vigentes da própria arquitetura, e não por diferença em relação a
  uma versão antiga.
- As decisões explícitas mais recentes da pesquisadora prevalecem sobre
  trechos históricos das skills: somente o Agente Modelador permanece na
  arquitetura-alvo; gesto não é ação; cada ação constituída tem um único
  `action_id`; a escolha de ajuda pertence ao proprietário semântico; as seis
  categorias curadas são canônicas.

## Resultado verificável atual

- `python -u scripts\verificar_regressao_gerard.py`: **aprovado**, sem falhas.
- Build Ant: **472 fontes Java compiladas**, JAR produzido; quatro avisos de
  configuração/depreciação já conhecidos, sem erro.
- Linha de base: **83/83 testes aprovados** — 79 testes não gráficos pelo
  verificador e quatro testes Swing executados separadamente contra a mesma
  compilação.
- Ratchet da `Main`: `mousePressed=421`, `mouseDragged=20`,
  `processarMovimentoArraste=69`, `mouseReleased=128`, `mouseClicked=34` e
  `mouseMoved=229`; nenhum método ultrapassou o limite vigente.
- O pacote `gerard.dominio` não importa AWT nem Swing.
- As seis relações estruturais possuem implementação própria no pacote de
  domínio: composição, transformação, comparação, composição de
  transformações, transformação de relação e composição de relações.
- Os números já possuem abstrações semânticas (`NumeroNatural`,
  `NumeroInteiro` e `ValorNumerico`); isso não é pendência desta auditoria.
- O arquivo curado efetivamente carregado em
  `C:\Users\cecomp\Gerard\curadoria\situacoes_vergnaud_curadas.tsv` contém
  210 situações e somente as seis categorias canônicas:

  - `COMPOSICAO_MEDIDAS`: 56;
  - `TRANSFORMACAO_MEDIDAS`: 86;
  - `COMPARACAO_MEDIDAS`: 20;
  - `COMPOSICAO_TRANSFORMACOES`: 32;
  - `TRANSFORMACAO_RELACAO`: 8;
  - `COMPOSICAO_RELACOES`: 8.

Não há pendência de renomear ou criar categoria. Os dois identificadores
históricos aceitos pelo carregador não constituem categorias adicionais.

## Lista priorizada — da mais simples para a mais complexa

### P1 — reconciliar e versionar uma única composição das skills — concluída

**Complexidade:** baixa. **Risco arquitetural:** alto.

**Status em 2026-08-23:** concluída por reconciliação por conhecimento
proprietário. As decisões adaptativas vigentes da cópia de trabalho foram
combinadas com o estado verificado 7.2–7.5 dos handlers da árvore Git; não foi
adotada integralmente nenhuma das duas versões anteriores.

O resultado preserva:

- somente o Agente Modelador na arquitetura-alvo;
- seleção de ajuda nos proprietários semânticos, usando repertório local;
- propriedade dos registros factuais nos objetos ricos ou relações
  correspondentes, com persistência separada;
- um único `action_id` por ação e separação entre gesto e ação;
- `gerard-semantic-model` na versão 3.0;
- histórico e ratchet dos handlers 7.2–7.5;
- nomes canônicos de frontmatter e metadado UTF-8 válido.

`gerard-geracao-texto-diagrama` foi posteriormente revisada como conhecimento
novo, corrigida para exigir rastreabilidade a situações curadas e impedir
promoção automática, e então integrada ao grafo. Ela não valida candidatas nem
substitui a decisão do pesquisador humano.

As duas pastas contêm os mesmos 29 arquivos, com igualdade SHA-256. O grafo
possui 19 nós e 56 relações, passa na validação estrutural e em cinco testes de
percurso, ordenação e detecção de ciclo. As referências de Monitor e ZDP
permanecem somente como histórico explicitamente supersedido.

**Aceite atendido:** uma única composição versionada; grafo válido; nenhuma
descrição normativa atribui decisão ao Monitor/ZDP; o histórico 7.2–7.5 dos
handlers permanece documentado.

### P2 — preservar e tornar reproduzível o arquivo curado ativo

**Complexidade:** baixa a média. **Risco semântico:** crítico.

Existem três fontes físicas com esquemas diferentes:

1. o arquivo ativo do usuário, com personagens, seis fragmentos e
   `operacao_relacao`;
2. `src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv`, com personagens e
   fragmentos, mas sem `operacao_relacao` no cabeçalho empacotado;
3. `dados/situacoes_vergnaud.tsv`, que termina em `observacoes`.

O repositório carrega primeiro o arquivo do usuário, o que mascara a
divergência nas máquinas onde ele existe. Uma instalação nova pode cair no
recurso empacotado incompleto. A correção deve começar por uma cópia de
segurança byte a byte do arquivo ativo e nunca por inferência, normalização ou
recálculo dos campos curados. Depois, uma única fonte versionada deve alimentar
o build, e um verificador deve exigir o mesmo esquema, as 210 linhas e somente
as seis categorias canônicas.

**Aceite:** backup com hash; recurso versionado idêntico à curadoria aceita;
nenhuma segunda cópia divergente; teste de round-trip sem mudar os valores
informados pelo pesquisador, inclusive `relacao_final`.

### P3 — corrigir a identidade entre gesto, ação e sequência de rejeições

**Complexidade:** média. **Risco semântico:** alto.

A implementação versionada ainda conserva a regra supersedida segundo a qual
várias tentativas rejeitadas compartilham o mesmo `action_id`:

- `PapelQuantitativo.registrarTentativa` abre o identificador na primeira
  rejeição e o reutiliza nas seguintes;
- `IdentificacaoEvento` documenta `gesture_id/action_id` como identificadores
  do mesmo gesto físico;
- não existe `rejection_sequence_id` no fluxo de produção.

A definição vigente é:

- o gesto possui `gesture_id`, mesmo quando termina fora de um elemento;
- só um comando semanticamente constituído gera ação;
- cada ação começa e termina no próprio protocolo e recebe novo `action_id`;
- três ações rejeitadas podem ser correlacionadas por
  `rejection_sequence_id`, sem se tornarem uma única ação.

Migrar primeiro um protocolo, com compatibilidade de leitura dos logs antigos,
e acrescentar campos ao final dos formatos persistidos.

**Aceite:** três rejeições consecutivas produzem três `action_id` e um único
`rejection_sequence_id`; gesto sem alvo não produz ação nem C/E; eventos
derivados de uma ação compartilham somente o `action_id` daquela ação.

### P4 — integrar o primeiro fluxo de decisão adaptativa distribuída

**Complexidade:** média a alta. **Risco arquitetural:** alto.

A branch integrada não contém `gerard.adaptacao`,
`IncognitaQuantitativa`, `PapelQuantitativoPosicionavel`,
`SessaoAdaptativaUsuario`, `RegraAdaptativaPublicada` nem
`RegistroAcaoInstrumental`. Esses componentes existem somente na outra pasta
de trabalho, ainda não consolidada e com muitas alterações não commitadas.

Não copiar essa árvore em massa. Usá-la somente como material de migração e
integrar primeiro o fluxo já delimitado pelas decisões vigentes: protocolo
`TEXTO` da incógnita. O proprietário semântico avalia e registra uma única
ação; o Modelador recebe o caso; a fotografia do Modelo do Usuário é carregada
no login; a interface apenas materializa a ajuda selecionada.

**Aceite:** o fluxo `TEXTO` da incógnita não chama Monitor/ZDP, não compara
números na `Main`, produz um único registro por ação e conserva a decisão
adaptativa rastreável à regra publicada e à versão da fotografia.

### P5 — retirar Monitor e ZDP incrementalmente dos demais protocolos

**Complexidade:** alta. **Risco de comportamento:** alto.

Os agentes legados ainda são instanciados em `Main.java` e participam de
cinco famílias de fluxo:

- seleção/confirmação de categoria;
- preenchimento da incógnita;
- posicionamento;
- sinal do número relativo;
- observação e painéis de auditoria/replay.

As chamadas reais permanecem, entre outros pontos, nas regiões de
`Main.java` próximas a 3710, 3813, 6430, 12428, 13405 e 13502. Migrar uma
família por vez para seu menor proprietário semântico ou relacional. Os
painéis, ouvintes e classes legados só devem ser removidos depois que não
houver consumidor e que replay, logs e regressão continuem aprovados.

**Aceite:** somente `AgenteModelador` permanece como agente operacional;
nenhum objeto ou interface central substitui os proprietários semânticos na
validação ou na seleção de ajuda.

### P6 — completar logs de gestos e handlers por protocolo

**Complexidade:** alta. **Risco de interação:** alto.

A separação factual gesto/ação está validada para
`ItemTextoArrastavel`. Os demais protocolos ainda usam partes do rastreador e
do log granular legados. A extração de handlers avançou até item textual,
elemento textual, conectores de Vergnaud e quadradinho de Venn; ainda restam o
eixo de inteiros e os despachos particulares presentes em `mousePressed`,
`mouseReleased` e `mouseMoved`.

Seguir o ratchet existente, um protocolo por alteração, com teste Robot. Não
usar a redução do total de linhas de `Main.java` como critério; o critério é
ela atuar como compositora e roteadora, sem mecânica particular.

**Aceite:** handler portátil sem Swing/AWT; geometria fornecida pela
representação; objeto representacional produz o gesto; proprietário semântico
produz a ação; limites do ratchet diminuídos na mesma alteração.

### P7 — retirar da `Main` a materialização particular de ajuda narrativa

**Complexidade:** média a alta. **Risco funcional:** médio.

`TipoSituacaoAditiva` já possui o repertório local das narrativas e
`PainelAjudaNarrativaVisualCategoria` já traduz GIF/storyboard para Swing.
Entretanto, `Main.java` ainda consulta diretamente `MidiaPreferida`, escolhe o
formato e monta parte do fluxo de exibição. A direção correta é manter o
conteúdo na categoria, a escolha pedagógica no proprietário semântico e a
materialização num apresentador/adaptador Swing, deixando a `Main` apenas
solicitar e posicionar o componente pela geometria real.

Preferência de mídia continua sendo somente forma de apresentação; não pode
decidir sozinha se haverá ajuda ou qual será sua função pedagógica.

**Aceite:** `Main` não interpreta perfil nem código de ajuda; recebe uma
decisão abstrata e encaminha ao apresentador; comportamento visual, GIFs,
storyboards e registros permanecem iguais.

## Ordem recomendada de execução

Executar P1 e P2 antes de novas mudanças de comportamento. Elas estabilizam
as duas fontes de verdade que hoje estão divididas: arquitetura e curadoria.
Depois executar P3 e P4 como uma migração pequena e testável do fluxo `TEXTO`
da incógnita. P5–P7 devem permanecer incrementais, sempre com regressão,
testes gráficos e Robot quando houver protocolo de mouse.

## Itens que não são pendências

- Não criar novas categorias nem renomear as seis canônicas.
- Não retirar a validação da escolha soma/subtração do seletor do aluno; ela
  valida a operação escolhida, não o valor resultante.
- Não recalcular `relacao_final` de `TRANSFORMACAO_RELACAO` na curadoria.
- Não reimplementar as relações estruturais já existentes.
- Não substituir `NumeroNatural`/`NumeroInteiro` por primitivos espalhados.
- Não restaurar o arraste dos elementos semânticos de Vergnaud.
- Não tornar a atração magnética permanentemente disponível.
- Não inferir conceitos-em-ação, teoremas-em-ação, tédio ou compreensão do
  participante; esses campos permanecem sob responsabilidade humana.
