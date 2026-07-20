# Relatório técnico — C150

## Objetivo

Corrigir a representação concreta da categoria **Transformação de medidas** para que o funil já esteja presente antes da definição da transformação e eliminar o questionamento semântico que permanecia visível depois de o estado final ser corretamente posicionado.

## Alterações realizadas

### 1. Funil estrutural permanente

Quando a transformação está desconhecida ou vale zero, o processo agora apresenta:

- estado inicial vazio ou conforme a modelagem;
- canal horizontal de passagem;
- funil superior em estado neutro;
- estado final vazio ou conforme a modelagem;
- nenhum quadradinho na transformação enquanto o valor não for conhecido.

O funil é uma estrutura da atividade e não um valor antecipado.

### 2. Regra do primeiro posicionamento preservada

A estrutura pode ser vista desde o início, mas as operações semânticas continuam bloqueadas enquanto o diagrama de Vergnaud estiver vazio. Os controles aparecem desabilitados até o primeiro posicionamento válido no diagrama formal.

Após o primeiro posicionamento:

- `+` incrementa a transformação inteira;
- `−` decrementa a transformação inteira;
- valor positivo ativa inserção pelo funil superior;
- valor negativo ativa retirada pelo funil inferior;
- os quadradinhos representam `abs(transformação)`;
- o sinal permanece no valor semântico compartilhado.

### 3. Controles ancorados ao funil

Foi criada a classe:

`gerard.campoaditivo.transformacao.processo.ControleSinalProcessoTransformacao`

Ela centraliza:

- geometria dos controles `+` e `−`;
- posicionamento ao lado do funil ativo;
- desenho dos estados habilitado, focado e bloqueado;
- detecção de clique.

A `Main.java` apenas coordena o controle especializado com as operações já consolidadas.

### 4. Correção do questionamento do estado final

O tip de incompatibilidade podia permanecer na tela depois de um item inicialmente incorreto ser movido para o papel correto. A liberação do questionamento agora ocorre quando o resultado da nova avaliação é correto ou não aplicável.

Foi ampliado o contrato de `ScaffoldingQuestionamento` com a regra:

`deveLimparQuestionamentoPersistente(resultado)`

O teste específico confirma que:

- `papel.estadoFinal` é compatível com `papel.estadoFinal`;
- o valor `6` do problema de Leandro é aceito como estado final;
- o tip anterior é removido após a correção;
- uma incompatibilidade ainda ativa continua produzindo feedback.

## Funcionalidades preservadas

- primeiro posicionamento obrigatório no Vergnaud;
- bloqueio das representações semânticas antes desse posicionamento;
- estados inicial e final não negativos;
- transformação no universo dos inteiros;
- sincronização entre texto, Vergnaud, eixo e processo concreto;
- arraste físico e proxy textual;
- item semanticamente incorreto permanece manipulável;
- som sutil, tremor leve e tip contextual;
- conclusão da modelagem;
- curadoria e aba Construir;
- dados curados sem alteração.

## Validações executadas

- compilação completa via Ant: 246 fontes Java;
- teste do processo de transformação: 32 verificações;
- teste dos elementos matemáticos imersos no texto;
- teste de bloqueio dos controles antes do primeiro posicionamento;
- teste de bloqueio das representações antes do Vergnaud;
- teste do estado semântico compartilhado;
- teste de arraste físico;
- teste de conclusão da modelagem;
- regressão estrutural completa;
- inspeção visual das prévias desconhecida, neutra, positiva e negativa;
- comparação SHA-256 dos dados curados C149/C150: arquivos idênticos.
