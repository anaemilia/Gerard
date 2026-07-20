# Relatório técnico — C132

## Objetivo
Restaurar o tratamento consolidado de posicionamento semanticamente incorreto para números, valores assinados e interrogações arrastados do texto por meio de proxy.

## Regressão identificada
Nas versões em que o arraste textual foi generalizado por proxy, o elemento temporário era descartado imediatamente quando o papel semântico estava incorreto. O tip permanecia, mas o descarte antecipado impedia que o proxy recebesse o som sutil e o tremor leve já utilizados nos demais itens do diagrama.

## Comportamento restaurado
Quando um elemento matemático imerso no texto é solto sobre um papel incorreto:

1. o posicionamento é avaliado antes de qualquer alteração do modelo;
2. o tip contextual de pergunta ou orientação é exibido junto ao proxy;
3. o som sutil é emitido;
4. o proxy executa o tremor leve;
5. sua posição horizontal é restaurada;
6. somente então o proxy é descartado;
7. o elemento original permanece no enunciado;
8. o estado semântico e as demais representações não são alterados.

Solturas fora do diagrama continuam sendo descartadas imediatamente, pois não representam associação a um papel semântico incorreto.

## Organização arquitetural
A correção não introduziu estado novo solto na `Main`.

### Nova classe
- `gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackProxyPosicionamento`

Ela coordena:
- a permanência temporária do proxy;
- o feedback multissensorial;
- o descarte após a conclusão da animação.

### Classes ajustadas
- `SessaoArrasteTextoParaDiagrama`: passou a distinguir proxy em arraste de proxy em feedback incorreto;
- `ScaffoldingFeedbackMultissensorialErro`: ganhou uma sobrecarga com ação de conclusão, preservando integralmente a assinatura anterior;
- `Main`: apenas conecta avaliação, tip, renderização em primeiro plano e controlador especializado.

## Compatibilidade preservada
- números naturais, valores positivos/negativos, decimais e interrogação continuam usando o mesmo fluxo de proxy;
- verificação semântica cobre todas as categorias aditivas;
- arraste com física de mola permanece ativo;
- eixo flutuante continua sendo componente de tela, separado da navegação semântica;
- controles `+` e `−`, limites curados e piso zero permanecem preservados;
- sincronização entre texto, Vergnaud e representações complementares permanece preservada;
- verificação passo a passo azul/vermelho da aba Construir, introduzida na C131, permanece ativa;
- dados curados não foram modificados.

## Validação executada
- compilação de 181 arquivos Java: aprovada;
- teste específico do ciclo som–tremor–descarte posterior: aprovado;
- feedback multissensorial em todas as categorias: aprovado;
- proxy textual semântico: aprovado;
- elementos matemáticos imersos no texto: aprovado;
- aba Construir e verificação passo a passo: aprovada;
- arraste físico: aprovado;
- controles de quantidades: aprovados;
- estado semântico compartilhado: aprovado;
- quantidades não negativas: aprovado;
- classificação das interações do eixo: aprovada;
- regressão estrutural completa: aprovada.

## Dados curados
A comparação entre as pastas `dados` da C131 e da C132 não encontrou alterações.

## Observação
A validação automatizada confirma o ciclo funcional. A inspeção visual final deve conferir a intensidade do som, a amplitude do tremor e a ancoragem do tip no ambiente gráfico real.
