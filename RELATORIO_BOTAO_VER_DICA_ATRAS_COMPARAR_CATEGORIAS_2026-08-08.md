# Correção: botão "Ver dica" visível atrás de "Comparar categorias" — 2026-08-08

## Relato

Captura de tela do cabeçalho da aba Diagramar, tela inicial (nenhuma
categoria selecionada): um fragmento de botão aparecia por trás do ícone
"Comparar categorias". A usuária confirmou: "por trás do comparar
categorias".

## Diagnóstico

Em vez de arriscar um chute a partir da imagem, a tela real foi aberta sob
Xvfb (`Main.main`) e o próprio `TelaGerard` foi inspecionado por reflection:
todos os componentes filhos cujo retângulo de bounds intersectava a região do
cabeçalho (0,0)-(220,50) foram listados com classe, bounds, visibilidade e o
nome do campo correspondente (casando por identidade de objeto contra os
campos declarados da classe).

Achado: `botaoVerDicaPosicionamento` — o botão "Ver dica" do AG_AE
(implementado em 2026-08-08, sessão anterior) — estava com
`bounds=(0,0,73,25)` e `visible=true` na tela inicial sem categoria, exatamente
sobre o canto superior esquerdo onde fica "Comparar categorias"
(`bounds=(16,8,34,34)`).

Causa raiz: `criarBotaoVerDicaPosicionamento()` cria o botão e o adiciona ao
painel, mas nunca define sua posição/visibilidade inicial — isso só acontece
dentro de `reposicionarBotaoVerDicaPosicionamento(Rectangle area)`, chamado
apenas durante o desenho do diagrama de Vergnaud (só existe quando há uma
categoria selecionada). Os três botões irmãos de mesmo padrão
(`botaoAjudaTexto`, `botaoAjudaVergnaud`, `botaoAjudaComplementar`) não têm
esse problema porque `ocultarControlesDaAtividadeSemCategoria()` — chamado a
partir de `inicializarTelaSemCategoria()`, executado na construção da tela e
sempre que a categoria é limpa — já os oculta explicitamente. O botão do AG_AE
foi adicionado à tela nesta mesma sessão anterior e ficou de fora dessa lista
por omissão.

## Correção

Uma linha em `ocultarControlesDaAtividadeSemCategoria()`
(`src/Main.java`), no mesmo padrão dos três botões irmãos:

```java
if (botaoAjudaComplementar != null) botaoAjudaComplementar.setVisible(false);
if (botaoVerDicaPosicionamento != null) botaoVerDicaPosicionamento.setVisible(false);
```

Não foi necessário tocar em `reposicionarBotaoVerDicaPosicionamento` (que já
decide corretamente quando reexibir o botão, assim que existe categoria e um
papel-dado pendente) — o problema era só a ausência de estado inicial oculto,
o mesmo mecanismo central já usado por todos os outros botões contextuais
opcionais.

## Verificação

Compilação completa do projeto sem erros.

Diagnóstico visual e estrutural sob Xvfb, antes e depois da correção,
inspecionando a tela real (`Main.main`) via reflection sobre `TelaGerard`:

- **Antes**: `botaoVerDicaPosicionamento` com `visible=true`,
  `bounds=(0,0,73,25)`, sobrepondo `botaoCompararCategorias`.
- **Depois**: `botaoVerDicaPosicionamento` com `visible=false` — mesmo estado
  dos três botões irmãos (`botaoAjudaTexto/Vergnaud/Complementar`, também
  `visible=false` na tela sem categoria). Recorte renderizado do cabeçalho
  confirma visualmente: só "Comparar categorias" e os 3 LEDs de agente
  aparecem, sem fragmento de botão atrás.

A lógica de reexibição do botão quando há categoria selecionada e papel-dado
pendente (`reposicionarBotaoVerDicaPosicionamento`, chamada a cada repaint do
diagrama) não foi alterada — já era coberta pelos testes da implementação
original do AG_AE (19 checks) e da refatoração de localidade (10 checks),
ambos na sessão anterior.

## Arquivo alterado

- `src/Main.java` (1 linha)
