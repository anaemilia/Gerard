# Relatório técnico — C165

## Objetivo

Na curadoria das situações-problema, tornar obrigatória a escolha do sinal semântico do campo `transformacao` ou do campo `valor_relativo`, conforme a categoria, sem inserir responsabilidades na `Main.java`.

## Comportamento implementado

A interface apresenta um seletor tipado ao lado da magnitude, com as opções:

- `Selecione o sinal…` — estado inicial não persistível;
- `+ Positivo`;
- `− Negativo`;
- `0 Neutro`.

A escolha é exigida nas categorias que usam os metadados dedicados `sinal_transformacao` ou `sinal_valor_relativo`:

- transformação de medidas;
- composição seguida de transformação de medidas;
- transformação de uma relação;
- comparação de medidas.

O usuário digita somente a magnitude ou `?`. A digitação e a colagem de `+` ou `−` no campo de magnitude são bloqueadas. Ao salvar ou criar uma tradução vinculada, a tela interrompe a operação se o sinal obrigatório não tiver sido escolhido e direciona o foco ao seletor.

O valor desconhecido `?` não elimina a obrigação de curar o sinal. O zero exige `0 Neutro`, e `0 Neutro` não pode ser associado a uma magnitude não nula.

Registros legados com `+` ou `−` no próprio valor são abertos com a magnitude separada e o seletor correspondente já marcado. Magnitudes sem sinal e sem metadado não são assumidas automaticamente como positivas.

As versões traduzidas continuam herdando os metadados semânticos da versão original. Campo e seletor permanecem bloqueados durante a edição da tradução.

## Arquitetura

A implementação foi concentrada no pacote:

```text
gerard.campoaditivo.curadoria.sinal
├── ControladorSinaisCuradoria
├── FiltroMagnitudeSemSinal
├── ModoPersistenciaSinalCuradoria
├── OpcaoSinalCuradoria
├── PainelValorComSinalCuradoria
├── PapelSinalCuradoria
├── PoliticaSinalCuradoria
└── ResultadoValidacaoSinalCuradoria
```

`TelaCuradoriaSituacoes` apenas compõe os componentes, solicita a validação e transfere os valores canônicos ao modelo. `Main.java` não foi alterado.

## Internacionalização e acessibilidade

Foram adicionados rótulos, tooltips, mensagens de validação e nomes acessíveis em português, inglês, francês e espanhol. O seletor é alcançável por teclado e pode ser operado com as teclas padrão do Swing.

## Validação realizada

- `ant clean jar`: compilação bem-sucedida de 298 fontes Java;
- teste específico: 28 verificações da política, persistência, filtro, acessibilidade e herança;
- bateria completa `scripts/testar_*.sh`: 53 scripts executados;
- 52 scripts aprovados;
- 1 falha preexistente e não relacionada: `testar_affordance_pickup_ui.sh`, que continua esperando cursor de mão no quadradinho disponível;
- 0 timeouts;
- dados curados da versão-base e da C165: idênticos por SHA-256;
- `Main.java`: idêntica por SHA-256.

A disposição final dos controles deve ser conferida visualmente no Windows de destino, inclusive com escala de tela de 125% e 150%.
