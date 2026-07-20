# Relatório C120 — controles de quantidades positivas e sincronização entre representações

Data: 16/07/2026

## Objetivo

Aplicar uma regra única aos controles `+` e `−` das representações quantitativas do Gérard:

1. enquanto o diagrama de Vergnaud estiver semanticamente vazio, os dois controles permanecem inabilitados;
2. após existir ao menos um elemento preenchido no diagrama, inclusive por preenchimento automático ou textual, os controles tornam-se elegíveis;
3. `+` acrescenta unidades somente até o limite definido pelos valores curados da situação;
4. `−` retira unidades até zero, sem permitir quantidades negativas;
5. toda alteração modifica apenas o estado atual da tentativa, preservando integralmente os dados curados.

## Implementação

Foi substituída a condição dependente de posicionamento manual por uma condição baseada no conteúdo semântico efetivo do diagrama de Vergnaud. Assim, o desbloqueio não depende da origem do preenchimento: arraste, entrada textual e preenchimento automático produzem o mesmo estado funcional.

Cada controle agora consulta separadamente se a operação pretendida é válida. Antes de alterar a tela, o sistema simula o estado semântico compartilhado da categoria e verifica:

- limite superior correspondente aos valores curados;
- limite inferior igual a zero nas representações de quantidades positivas;
- consistência da relação aditiva da categoria;
- validade das quantidades derivadas que também aparecem visualmente.

A alteração aprovada é propagada pelo estado compartilhado para:

- texto da situação-problema;
- elementos do diagrama de Vergnaud;
- quadradinhos do diagrama de composição de coleções;
- quadradinhos do gráfico de barras, quando a categoria utiliza barras.

Os objetos de curadoria são usados apenas como referência de limites e relações. Seus valores não são sobrescritos pelos controles da atividade.

## Comportamento esperado

- Diagrama de Vergnaud vazio: `+` e `−` inabilitados.
- Quantidade igual a zero: `−` inabilitado e `+` disponível quando houver margem até o valor curado.
- Quantidade abaixo do limite curado: `+` habilitado.
- Quantidade no limite curado: `+` inabilitado.
- Operação que quebraria a relação semântica da categoria: controle inabilitado.
- Preenchimento automático válido: controles atualizados sem exigir novo posicionamento com o mouse.

## Internacionalização

Foram ajustadas as mensagens dos controles em português, inglês e francês, incluindo a informação de que uma quantidade positiva não pode ser reduzida abaixo de zero.

## Verificação de regressão

Foram executados testes de compilação e de comportamento cobrindo:

- bloqueio inicial de `+` e `−`;
- desbloqueio após preenchimento manual, textual ou automático;
- incremento até o limite curado;
- decremento até zero;
- impedimento de valores negativos;
- sincronização em composição de medidas, transformação de medidas e comparação de medidas;
- preservação dos dados curados;
- sincronização entre texto, Vergnaud e representações por unidades;
- contratos dos controles do diagrama complementar;
- regressão geral do projeto.

Resultado: compilação concluída e testes executados sem falhas.
