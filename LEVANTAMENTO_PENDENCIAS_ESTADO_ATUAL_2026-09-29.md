# Estado atual e pendências do Gérard — 2026-09-29

Este levantamento substitui, para consulta operacional, as listas vigentes
embutidas em `LEVANTAMENTO_PENDENCIAS_2026-09-28.md`. O arquivo anterior
permanece como histórico. Nenhum item abaixo autoriza automaticamente uma
alteração arquitetural, decisão pedagógica, exclusão ou versionamento.

## Estado verificado nesta auditoria

- Branch informada pelo Git: `web/deploy-render`.
- Árvore de trabalho: 79 entradas no `status`, sendo 13 arquivos modificados
  e 66 caminhos não rastreados. Há código, relatórios, evidências Robot e
  muitos artefatos de pesquisa misturados; não usar `git add -A`.
- `Claude outputs/sync_web.tgz` não existe mais.
- Grafo de skills válido: 22 nós e 90 relações.
- Build atual aprovado: 622 fontes Java compiladas e JAR gerado.
- O verificador estrutural geral termina com duas falhas. As duas expectativas
  exigem que `suspenderConclusaoDuranteManipulacao` reinicie o controlador e
  que `reiniciarConclusaoModelagem` não cuide do estado visual exploratório.
  Isso conflita com a decisão mais recente: depois do azul, manipulações são
  exploratórias e não desfazem a conclusão da tentativa. Tratar como
  **verificador desatualizado a reconciliar**, não como autorização para
  remover `destaqueConclusaoSuspensoPorExploracao` ou mudar `Main`.
- O verificador de localidade atual passa com a linha de base vazia, mas não
  detecta dependências da aplicação para adaptadores concretos de
  infraestrutura, descritas abaixo.
- A execução fresca de `verificar_linha_base_windows.py` confirmou compilação
  das 622 fontes e dos 151 testes, porém não produziu resumo final confiável
  depois de entrar nos testes gráficos. Não contabilizar esta tentativa como
  151/151. Os relatórios das Fases 7.12/7.13 registram uma execução anterior
  151/151.

## Encerradas com evidência existente

1. **Fases 7.11, 7.12 e 7.13 de `mousePressed`.** Marcador do enunciado,
   pressionamento da barra de Comparação e apresentação da lupa de Relações
   foram retirados do corpo central conforme seus escopos autorizados. O
   ratchet atual de `mousePressed` é 174 linhas. Há testes dirigidos e
   evidências Robot reais no Windows; os relatórios 7.12 e 7.13 registram zero
   falhas nas execuções válidas.
2. **Restauração e persistência da tentativa web.** A restauração captura as
   sequências abertas antes de recriar os papéis e a modelagem web persiste
   pelas ações constituídas pela tentativa.
3. **Incógnita inteira na web.** O fluxo com magnitude, sinal incorreto,
   confirmação, sinal correto e conclusão azul possui evidência Playwright
   anterior 20/20.
4. **Auditoria literal de `BarraCategorias.tsx`.** As duas divergências
   comprovadas foram corrigidas. Os sete textos sem fonte permanecem como
   decisão da pesquisadora, não como correção automática.
5. **Limpeza autorizada.** O arquivo temporário `sync_web.tgz` e o diretório
   da execução Robot concorrente inválida foram removidos; a evidência válida
   dos controles de unidades foi preservada.

## Pendências de implementação e validação

### Prioridade alta — arquitetura web recém-alterada

1. **Concluir a inversão de dependência das novas portas web.** As portas
   `PortaAjudaContextual` e `PortaQuestionamentoPosicionamento` e seus
   adaptadores concretos existem, mas a camada de aplicação ainda conhece e
   instancia infraestrutura:
   - `AvaliadorOrigemDestinoWeb` importa e cria
     `AdaptadorQuestionamentoPosicionamentoWeb`;
   - `ServicoSorteioAtividadeWeb` importa e cria
     `AdaptadorAjudaContextualWeb`.

   A raiz de composição deve fornecer as implementações das portas; aplicação
   não deve importar o adaptador concreto. Antes de alterar, aplicar a spec de
   localidade e registrar proprietário, entrada/saída, porta e raiz de
   composição. Esta pendência exige autorização própria.

2. **Ampliar o verificador de localidade.** Hoje ele aprova a situação acima
   e a linha de base foi zerada. Acrescentar uma regra que rejeite dependência
   `gerard.aplicacao.* -> gerard.infraestrutura.*`, sem mascarar a dívida em
   outra lista. Deve ser feito junto ou após a correção do item 1.

3. **Revalidar a interação web após a refatoração das portas.** Executar o
   Playwright aplicável aos fluxos de ajuda contextual e posicionamento
   incorreto. A própria lista anterior registra que essa evidência real ainda
   está pendente.

### Prioridade alta — verificadores e linha de base

4. **Reconciliar as duas verificações de conclusão com o modo exploratório.**
   Atualizar a expectativa do verificador conforme a decisão de 2026-09-29,
   protegendo simultaneamente:
   - conclusão sem reinício durante manipulação exploratória;
   - retirada temporária do destaque azul;
   - reinício efetivo ao sortear outra situação.

   Não alterar `Main` apenas para satisfazer os textos antigos do script.

5. **Executar novamente a linha de base Windows até obter resumo final.** A
   tentativa desta auditoria compilou fontes e testes, mas não forneceu o
   resultado agregado após iniciar os testes gráficos. Rodar os testes
   gráficos estritamente em série e registrar processos/timestamps da nova
   execução.

### Implementações já conhecidas

6. **Migrar o log granular legado** “Tentar adicionar unidade antes da
   modelagem” para produção factual no proprietário semanticamente autorizado,
   mantendo persistência como infraestrutura.
7. **Investigar `bolas_509261012` em Composição de Transformações:** três
   valores errados sem diálogo ou escalada. Primeiro determinar se o fluxo
   está corretamente aguardando operação/sinal; não presumir defeito.
8. **Corrigir, se reproduzido e autorizado, o layout 1600×1000 de Composição
   de Transformações:** seletor invadindo o enunciado e estado inicial
   cortado. Aplicar geometria relativa; não introduzir pixels coincidentes.

## Decisões exclusivas da pesquisadora

9. Definir fonte e redação dos sete textos do upload de curadoria listados em
   `RELATORIO_AUDITORIA_REGRA_1_BARRA_CATEGORIAS_2026-09-29.md`.
10. Decidir quando o seletor de operação das Relações deve aparecer em relação
    à confirmação da categoria.
11. Decidir se o diagnóstico “operação invertida” deve ser localizado em
    `IncognitaQuantitativa` nas duas plataformas ou se o resultado continua
    sendo `VALOR_INCORRETO`.

## Dados, documentação e baixa prioridade

12. **Cobertura curada parcial das operações:** 1/4 em Transformação de
    Relação, 1/2 em Composição de Relações e 1/5 em Composição de
    Transformações. Ampliar depende de curadoria, não de inferência do código.
13. **Contexto após F5/recarregamento:** permanece em baixa prioridade por
    decisão da pesquisadora; `GET /api/situacao` ainda sorteia nova situação.
14. **Artigo autoral:** `documentacao/artigo_codesign/main.tex`, linha 498,
    ainda afirma que a Fase 7.8 não recebeu validação Robot. O fato está
    desatualizado, mas o texto não deve ser alterado sem autorização.
15. **Organizar a árvore por frente antes de commits.** Separar, no mínimo:
    interação desktop e Robots; portas/localidade web; documentação; dados e
    artefatos de pesquisa. Revisar por hunk arquivos compartilhados. Não há
    autorização atual para commit, push, exclusão ou inclusão em massa.
16. **Historinha ilustrada com texto traduzido dentro da animação — RESOLVIDO
    no mecanismo em 2026-10-04; resta a curadoria dos trechos.**
    Regra vigente: o idioma da interface não altera a regra "número relativo ou
    transformação + 3 rejeições consecutivas → historinha"; ele só determina o texto
    passado como argumento. A ilustração é a da versão original (as traduções herdam).
    Como funciona:
    - o GIF não grava texto; o método polimórfico `argumentosHistorinha(situacao,
      idioma)` do renderizador da categoria entrega os trechos curados da situação
      (`fragmento_texto_1..6`, no idioma dela);
    - `DuracaoAnimacaoHistorinha` lê a duração real do laço no próprio GIF (16,5 s;
      19,5 s em `02_manuel_figurinhas`) e `ArgumentosHistorinhaCena` divide esse tempo em
      partes iguais entre os trechos (decisão da pesquisadora, 2026-10-04); a projeção
      da API leva `duracao_s` e `legendas` (texto, início, fim);
    - Swing (`RotuloAnimacaoComLegendas`, laço contado desde a primeira pintura do GIF)
      e React (`HistorinhaPassiva`) pintam a legenda ativa DENTRO da animação.
    Verificação por ação real: web no site publicado (R8: legenda dentro da imagem, todas
    passam no laço) e desktop (macaco com desdobramentos por clique: 66 de 66 situações,
    309 cliques, 0 falhas; legendas lidas em captura de tela); en/fr com trechos
    sintéticos em cópia de teste; `verificar_regressao_gerard.py` aprovado (623
    checagens). Evidências em `documentacao/relatorios/evidencias/legenda_na_animacao_20261004/`
    e `.../regressao_20261004/`.
    **Pendente (depende da pesquisadora):** sem trecho curado não há legenda (Regra 1 do
    CLAUDE.md). Hoje só 7 das 22 historinhas têm trechos (paulo_e_jose, maria_joao_carla,
    vovo_rosas, andrea_boneca, lucas_e_as_figurinhas_do_tio, julia_maria_bonecas,
    ana_bia_figurinhas). Faltam os trechos das outras 15 originais e das 44 traduções
    (en/fr), na tela de curadoria; a lista está em
    `documentacao/relatorios/historinhas_e_trechos_curados_2026-10-04.tsv`. O site
    publicado só mostra os trechos que estão na curadoria canônica do repositório
    (3 das 7 na verificação de 2026-10-04); os demais exigem "Atualizar curadoria" no site (token).

## Artefatos não rastreados — decisão de versionamento pendente

Os 66 caminhos não rastreados incluem GIFs/storyboards, PDFs, TSV/JSONL de
mineração, arquivos de regras, datasets, propriedades, imagens, arquivos ZIP
e diretórios de evidências Robot. A auditoria não encontrou fonte que autorize
classificá-los coletivamente como temporários, produto ou dados publicáveis.
Inventariar origem, licença, reprodutibilidade e destino de cada grupo antes de
adicionar ou remover qualquer um deles.
