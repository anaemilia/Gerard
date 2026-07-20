# Relatório técnico — C133

## Correção

Na C132, o feedback de erro voltou a apresentar tip, som e tremor, mas o proxy textual era descartado ao terminar a animação. Isso contrariava o comportamento consolidado, no qual o item incorretamente posicionado permanecia no diagrama para ser manipulado novamente.

Na C133, um elemento matemático oriundo do texto que seja solto sobre o diagrama permanece como `ItemTextoArrastavel`, mesmo quando o papel semântico escolhido está incorreto.

## Fluxo consolidado

1. O número, valor assinado ou interrogação permanece no enunciado.
2. O proxy é arrastado até o diagrama.
3. Se o papel estiver incorreto, o item é incorporado visualmente ao diagrama, mas não é sincronizado como associação correta.
4. O fluxo consolidado `processarQuestionamentoPosicionamento(...)` apresenta:
   - tip contextual persistente;
   - som sutil;
   - tremor leve.
5. Ao terminar o tremor, o item continua no diagrama.
6. O usuário pode selecioná-lo e arrastá-lo para outro papel.
7. Ao alcançar o papel correto, o questionamento é removido e a sincronização é executada normalmente.

Somente proxies soltos fora da área do diagrama são descartados.

## Alterações arquiteturais

A política foi mantida no módulo de sessão de arraste:

- `SessaoArrasteTextoParaDiagrama.deveManterNoDiagramaAposErro(...)`;
- `SessaoArrasteTextoParaDiagrama.deveDescartarAoSoltar(...)` passou a restringir o descarte à soltura fora do diagrama.

A tela reutiliza o fluxo de feedback já consolidado, em vez de criar uma segunda variante de comportamento.

## Regressão formalizada

Foi criada a matriz:

- `MATRIZ_REGRESSAO_FUNCIONALIDADES_CONSOLIDADAS.md`

O verificador `scripts/verificar_regressao_gerard.py` passou a exigir os seguintes contratos:

- item incorreto permanece no diagrama;
- item continua manipulável após o feedback;
- tip persistente, som e tremor permanecem ativos;
- descarte ocorre somente fora do diagrama;
- texto original permanece no enunciado;
- dados curados não são modificados.

Também foram atualizados:

- `scripts/testes/TesteFeedbackProxyPosicionamento.java`;
- `scripts/testes/TesteElementosMatematicosImersosTexto.java`;
- `scripts/testar_feedback_proxy_posicionamento.sh`.

## Validação automática

Aprovados:

- compilação de 181 arquivos Java;
- teste do feedback e permanência do item incorreto;
- teste dos elementos matemáticos imersos no texto;
- teste do proxy textual;
- teste multissensorial em oito categorias;
- teste do arraste físico;
- teste dos controles `+` e `−`;
- teste do estado semântico compartilhado;
- teste da aba Construir;
- bloqueio semântico antes do primeiro posicionamento;
- classificação das interações do eixo;
- quantidades não negativas;
- sincronização por categoria;
- sincronização dos elementos semânticos do texto;
- regressão estrutural completa.

## Curadoria

Os arquivos da pasta `dados` da C132 e da C133 possuem o mesmo hash agregado:

`017986daa2729a02ccc08f8f758f6723b16b8ed90ebf0ae89edab0e45d53c7bb`

Portanto, os dados curados não foram alterados.

## Verificação visual pendente

A inspeção final no Windows deve confirmar:

- intensidade do som;
- suavidade do tremor;
- permanência do item após a animação;
- possibilidade de arrastá-lo imediatamente para outro papel;
- remoção do tip após o posicionamento correto.
