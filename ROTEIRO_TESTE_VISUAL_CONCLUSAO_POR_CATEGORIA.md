# Roteiro de teste visual — conclusão numérica por categoria

Este roteiro complementa os testes automatizados. Ele verifica comportamentos gráficos e sonoros que não podem ser comprovados apenas pela compilação ou por testes estruturais.

## Condições gerais

- Executar no ambiente gráfico usado pelos participantes.
- Iniciar cada cenário com o diagrama restaurado e semanticamente vazio.
- Usar uma situação-problema curada da categoria indicada.
- Confirmar que os dados do enunciado permanecem visíveis após o arraste.
- Registrar evidência por captura de tela e, quando possível, gravação curta do som e do tremor.

## Critérios comuns de aprovação

1. O diagrama não fica azul enquanto houver elemento vazio, interrogação ou posicionamento semanticamente incorreto.
2. O diagrama fica azul somente quando todos os papéis semânticos estiverem preenchidos com números válidos.
3. A regra funciona com preenchimento por mouse e por teclado.
4. Primeiro aparece o selo discreto **“Modelagem concluída”**; o tip não aparece simultaneamente.
5. Após uma pausa curta, o selo desaparece e surge **“Podemos passar para a próxima tarefa?”**, com **Sim** e **Não**.
6. **Não** mantém a situação atual e o destaque azul.
7. **Sim** aciona o botão Sortear e carrega outra situação da mesma categoria.
8. Ao voltar a manipular um item durante a pausa, a sequência é cancelada e o tip não aparece.
9. Posicionamento incorreto produz tip, som sutil e tremor leve; o item permanece manipulável no diagrama.

---

## Composição de medidas

### C139-VIS-COMP-01 — conclusão por arraste

1. Selecione uma situação de composição.
2. Arraste o número de `parte 1` para o papel correspondente.
3. Arraste o número de `parte 2` para o papel correspondente.
4. Confirme que o diagrama ainda não está azul enquanto o `todo` estiver vazio ou com `?`.
5. Arraste o valor numérico do `todo` para o papel correspondente.

**Resultado esperado:** `parte 1`, `parte 2`, `todo`, conectores e itens ficam azuis; aparece o selo discreto e, depois, o tip de decisão, uma única vez.

### C139-VIS-COMP-02 — conclusão por teclado

1. Restaure a atividade.
2. Preencha numericamente `parte 1`, `parte 2` e `todo` por teclado.
3. Pressione Enter após cada entrada.

**Resultado esperado:** o estado azul só aparece após o terceiro valor numérico válido.

### C139-VIS-COMP-03 — erro e recuperação

1. Coloque o número de `parte 1` no papel `todo`.
2. Observe o feedback.
3. Arraste o mesmo item para `parte 1`.
4. Complete os demais papéis corretamente.

**Resultado esperado:** ocorre tip, som e tremor; o item não é removido; após a correção e o preenchimento completo, o diagrama fica azul.

---

## Comparação de medidas

### C139-VIS-COMPAr-01 — conclusão por arraste

1. Selecione uma situação de comparação.
2. Posicione `referido` e `referendo` nos papéis correspondentes.
3. Posicione o `valor relativo` com o sinal correto.
4. Confirme que o diagrama não fica azul enquanto algum papel estiver vazio ou com `?`.

**Resultado esperado:** referido, referendo, valor relativo, conectores e itens ficam azuis somente após os três valores numéricos corretos.

### C139-VIS-COMPAr-02 — valor relativo por teclado/eixo

1. Restaure a atividade.
2. Preencha `referido` e `referendo` por teclado.
3. Defina numericamente o valor relativo por teclado ou pelo eixo, respeitando o sinal.

**Resultado esperado:** o destaque azul aparece somente quando o valor relativo numérico é consistente e todos os papéis estão completos.

### C139-VIS-COMPAr-03 — interrogação e correção

1. Posicione `?` corretamente em um dos papéis.
2. Complete os demais papéis com números.
3. Confirme que não há conclusão.
4. Substitua `?` por número pelo teclado.

**Resultado esperado:** a interrogação impede a conclusão; sua substituição por número ativa o estado azul, o selo e, depois, o tip.

---

## Transformação de medidas

### C139-VIS-TRANS-01 — conclusão por arraste

1. Selecione uma situação de transformação.
2. Posicione `estado inicial`, `transformação` e `estado final` nos papéis corretos.
3. Use sinal positivo ou negativo na transformação conforme o enunciado.

**Resultado esperado:** círculo, estados, seta e itens ficam azuis somente quando os três papéis estiverem numericamente preenchidos e semanticamente corretos.

### C139-VIS-TRANS-02 — conclusão por teclado

1. Restaure a atividade.
2. Digite valores numéricos nos três elementos do diagrama.
3. Confirme cada entrada com Enter.

**Resultado esperado:** o destaque azul aparece após o último valor numérico válido, sem exigir arraste.

### C139-VIS-TRANS-03 — papel incorreto permanece manipulável

1. Coloque o valor do `estado final` no círculo da `transformação`.
2. Aguarde o feedback completo.
3. Arraste o mesmo item para o `estado final`.
4. Complete os demais papéis.

**Resultado esperado:** tip, som e tremor são exibidos; o item incorreto permanece no diagrama e pode ser reposicionado; a conclusão ocorre apenas depois da correção.

---

---

## C155 — sequência progressiva nas oito categorias

Execute o ciclo completo em cada categoria, preenchendo todos os papéis numéricos corretos:

| Caso | Categoria | Papéis mínimos a conferir |
|---|---|---|
| C155-VIS-CM | Composição de medidas | Parte 1, Parte 2 e Todo |
| C155-VIS-TM | Transformação de medidas | Estado inicial, Transformação e Estado final |
| C155-VIS-CMT | Composição seguida de transformação | Partes, Todo, Transformação e Estado final |
| C155-VIS-COP | Comparação de medidas | Referido, Valor relativo e Referendo |
| C155-VIS-CT | Composição de transformações | Transformação 1, Transformação 2 e Transformação final |
| C155-VIS-TCP | Transformação composta em dois passos | Estado inicial, Transformações 1 e 2, Estado intermediário e Estado final |
| C155-VIS-TR | Transformação de uma relação | Relação inicial, Transformação e Relação final |
| C155-VIS-CR | Composição de relações | Relação 1, Relação 2 e Relação final |

Para cada caso, confirmar:

1. O último valor correto ativa o destaque azul.
2. Aparece primeiro apenas o selo **“Modelagem concluída”**.
3. O selo não cobre o diagrama e fica abaixo de sua área visual real.
4. Após aproximadamente 1,15 segundo, o selo desaparece antes do tip.
5. O tip surge no mesmo setor abaixo do diagrama, sem salto abrupto sobre as figuras.
6. Alterar qualquer valor durante a espera cancela o tip pendente.
7. Reestabelecer a conclusão inicia uma nova sequência completa.
8. A sequência não se repete continuamente durante `repaint`.

## Continuidade da tarefa

### C139-VIS-CONT-01 — opção Não

1. Conclua qualquer uma das três categorias.
2. Marque **Não** no tip.

**Resultado esperado:** o tip fecha; a situação atual e o diagrama azul permanecem inalterados.

### C139-VIS-CONT-02 — opção Sim

1. Conclua qualquer uma das três categorias.
2. Marque **Sim** no tip.

**Resultado esperado:** o sistema executa o fluxo do botão Sortear, limpa a modelagem anterior e apresenta outra situação da mesma categoria.

## Registro da execução

| Caso | Categoria | Ambiente | Resultado | Evidência | Observações |
|---|---|---|---|---|---|
| C139-VIS-COMP-01 | Composição |  |  |  |  |
| C139-VIS-COMP-02 | Composição |  |  |  |  |
| C139-VIS-COMP-03 | Composição |  |  |  |  |
| C139-VIS-COMPAr-01 | Comparação |  |  |  |  |
| C139-VIS-COMPAr-02 | Comparação |  |  |  |  |
| C139-VIS-COMPAr-03 | Comparação |  |  |  |  |
| C139-VIS-TRANS-01 | Transformação |  |  |  |  |
| C139-VIS-TRANS-02 | Transformação |  |  |  |  |
| C139-VIS-TRANS-03 | Transformação |  |  |  |  |
| C139-VIS-CONT-01 | Continuidade |  |  |  |  |
| C139-VIS-CONT-02 | Continuidade |  |  |  |  |

| C155-VIS-CM | Composição de medidas |  |  |  |  |
| C155-VIS-TM | Transformação de medidas |  |  |  |  |
| C155-VIS-CMT | Composição seguida de transformação |  |  |  |  |
| C155-VIS-COP | Comparação de medidas |  |  |  |  |
| C155-VIS-CT | Composição de transformações |  |  |  |  |
| C155-VIS-TCP | Transformação composta em dois passos |  |  |  |  |
| C155-VIS-TR | Transformação de uma relação |  |  |  |  |
| C155-VIS-CR | Composição de relações |  |  |  |  |
