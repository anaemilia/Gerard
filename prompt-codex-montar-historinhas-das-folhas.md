# Prompt para o Codex — montar, registrar e testar as historinhas a partir das folhas

Contexto: o Gérard mostra uma **historinha** após 3 rejeições consecutivas, onde há número relativo
ou transformação. A decisão é do backend (`PoliticaApoioVisual` → `DecisorAjudaVisual`); a cena
carrega uma lista de `ApoioVisual` tipados; Swing e React só renderizam a primeira entrada pelo
`tipo`. Se a situação tem **animação ilustrada** (`idSituacaoCurada` registrado), ela é o apoio;
senão, o apoio é a historinha de **texto** da própria situação. Hoje há 9 situações com animação e
28 só com texto. Sua tarefa é produzir as animações que faltam (13 em português) a partir das
**folhas ilustradas já geradas**.

Leia antes: `CLAUDE.md`, `AGENTS.md`, a skill `gerard-tela-via-gerador-de-cena`, a referência
`gerard-scaffolding-interacao/references/material-concreto.md` e
`documentacao/producao_historinhas/` (levantamento e rascunhos).

## Entradas (já prontas)

- **Folhas:** `documentacao/producao_historinhas/folhas/NN_slug_folha.png` (13 arquivos, gerados
  na etapa anterior; cada um deve ter 4 quadros empilhados, sem texto). A folha da Andréa,
  `andrea_boneca_folha_gamma_2026-10-02.jpg`, **já está pronta e registrada — não refaça**.
- **Rascunhos:** `documentacao/producao_historinhas/rascunhos/historias_pendentes_rascunho.json`
  (id da situação, título, 4 legendas, equação, conclusão, título final). Derivados só do
  enunciado e dos números curados. A usuária pode ter editado; **use o arquivo como está**.
- **Montador:** `scripts/producao_historinhas/montar_historinha.py <especificacao.json>`; exemplo de
  especificação: `documentacao/producao_historinhas/especificacoes/andrea_boneca.json`.

## Passos

1. **Conferir cada folha**: exatamente 4 quadros, nenhum texto/letra/número, personagens
   consistentes. Liste as reprovadas e **não monte** as reprovadas; peça nova geração à usuária.
2. **Criar uma especificação por folha aprovada** em
   `documentacao/producao_historinhas/especificacoes/NN_slug.json`, copiando os campos do rascunho.
   Para folhas de 4 quadros empilhados use `"grade": [1, 4]` e `"paineis": [0, 1, 2, 3]`; ajuste
   `"focos"` (0.0–1.0, posição horizontal do recorte 16:9 em cada quadro) olhando a folha para
   enquadrar os personagens. `"saida"` aponta para
   `src/gerard/recursos/ajuda/<categoria>/NN_slug_historinha` com a pasta da categoria
   (`transformacao_medidas`, `comparacao_medidas`, `composicao_transformacoes`, …) e `NN` seguindo a
   numeração já existente na pasta.
3. **Montar** cada GIF com o montador (saída 640×360, ~9 MB, 8 quadros/s) e conferir os
   `*_storyboard.png` gerados: legenda legível, equação correta, sem corte de rosto.
4. **Registrar** cada animação em `TipoSituacaoAditiva`
   (`HistorinhaAjudaVisual(identificador, idSituacaoCurada, referencia)`), no repertório da
   categoria da situação. Categorias hoje sem repertório (ex.: `COMPARACAO_MEDIDAS`) passam a usar
   `RepertorioAjudaVisual.criar(...)`. **Não crie regra por categoria no `Main` nem no React**: a
   decisão e a escolha já estão no backend; só o repertório cresce.
5. **Testar**:
   - `TesteAjudaVisualPorCategoriaWeb` (deve contar mais situações "ilustradas" e menos "de texto"),
     `TesteLimiteHistorinhasOperacao`, `TesteCenaSubtitulos`, `TesteAreaDiagramaPortatil`.
   - **Mouse real no desktop** (`tests/graphical/TesteRobotHistorinhasCategoriasRelativas.java` ou
     equivalente): em pelo menos 4 das situações novas (uma por categoria), levar a incógnita a 3
     rejeições e confirmar que o painel direito mostra a **animação ilustrada da própria
     situação**, não o cartão de texto. Guarde capturas.
   - **Web** (`tests/web/e2e_historinhas_categorias_relativas.mjs`): o item de `ajuda_visual` da
     situação tem `tipo = HISTORINHA_ILUSTRADA` e `referencia` correta; o GIF carrega.
6. **Não alterar a curadoria.** Se achar inconsistência nos dados curados, **apenas relate**. Já
   conhecida: em `PO_TRANSFORMACAO_COMPOSTA_DOIS_PASSOS_chocolates_1269597383` (Geisa) os campos
   `estado_final=16` e `resultado=3` não conferem com o enunciado (25 − 2 − 5 = 18); o rascunho usa 18.
7. **Commit local** (um por categoria, mensagem em português seguindo o padrão do repositório,
   incluindo o trailer de coautoria do ambiente). **Não faça push** e não mexa em `C:\gd` nem no Render.

## Restrições

- **Não use ferramentas pagas ou que gastem créditos** (Gamma, APIs de imagem). Se faltar folha,
  pare e avise.
- Não invente legendas, equações ou texto fora do rascunho. Se uma legenda parecer errada, relate;
  não corrija sozinho.
- Siga a skill `gerard-tela-via-gerador-de-cena`: nenhum cliente (Swing/React) escolhe entre
  apoios; nenhuma decisão no `Main`.
- Respeite Regra 2 do `CLAUDE.md`: "funcionando" só com a ação concluída por mouse real e evidência.

## Entrega

Relatório curto: folhas aprovadas/reprovadas; GIFs gerados (caminho e tamanho); situações
registradas; testes executados e resultado; capturas do mouse real; inconsistências de dados
encontradas; commits criados (hashes) e o que ficou pendente.
