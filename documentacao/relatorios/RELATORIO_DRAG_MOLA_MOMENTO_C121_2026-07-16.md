# Relatório C121 — drag-and-drop com física de mola e momento

Data: 16/07/2026

## Objetivo

Aplicar ao arraste do Gérard um comportamento visual de massa-mola-amortecedor: o elemento não copia instantaneamente cada coordenada do cursor, mas persegue o ponteiro com pequeno atraso elástico, massa e inércia. A alteração deve permanecer estritamente visual e não pode modificar a posição semântica final, a curadoria ou o estado compartilhado.

## Implementação

O controlador introduzido na C116 foi refinado para explicitar massa, força elástica, amortecimento, velocidade e limite de atraso. A tela principal passa a usar `ControladorArrasteMolaMomento`, enquanto o contrato `ControladorArrasteElastico` permanece estável.

O ciclo visual é executado em aproximadamente 60 quadros por segundo:

1. o cursor define a posição-alvo;
2. a diferença entre alvo e posição visual produz uma força de mola;
3. a força, dividida pela massa, produz aceleração;
4. a velocidade acumulada conserva um pequeno momento e recebe amortecimento;
5. a distância visual ao cursor é limitada a 26 pixels;
6. na soltura, o elemento é levado à coordenada exata do mouse antes de qualquer validação ou sincronização.

A limitação de velocidade evita saltos quando o cursor muda bruscamente de direção. A conclusão exata na soltura impede que o efeito visual altere zonas de colisão, escolha do papel semântico ou resultado da atividade.

## Elementos abrangidos

- números, interrogação e palavras móveis do enunciado;
- itens posicionados no diagrama;
- quadradinhos das representações complementares;
- elementos e conectores de Vergnaud;
- ponto de controle da comparação;
- ponto e painel do eixo dos inteiros.

## Preservação da C120

A física do arraste não participa do cálculo dos controles quantitativos. Permanecem preservadas as seguintes regras:

- com o diagrama de Vergnaud semanticamente vazio, `+` e `−` ficam inabilitados;
- após preenchimento por arraste, digitação ou preenchimento automático, os controles são recalculados;
- cada `+` funciona independentemente e acrescenta uma unidade até o limite curado;
- cada `−` remove uma unidade até zero, sem quantidade negativa;
- operações incompatíveis com a relação aditiva são bloqueadas;
- texto, Vergnaud, coleções e barras continuam projetando o mesmo estado semântico;
- os dados curados continuam somente como referência e não são modificados.

## Arquitetura

- `ControladorArrasteElastico`: contrato do ciclo de arraste;
- `ControladorArrasteElasticoAbstrato`: template do modelo massa-mola-amortecedor, temporização, conclusão e cancelamento;
- `ControladorArrasteMolaMomento`: parâmetros concretos de rigidez, amortecimento, massa, velocidade máxima e atraso visual;
- `OuvinteArrasteElastico`: atualização polimórfica da posição visual sem conhecer o elemento manipulado.

## Verificação

Foram aprovados:

- compilação Ant/NetBeans em Java 8;
- teste unitário de perseguição, atraso máximo, momento, reversão e soltura exata;
- teste Swing integrado de pickup, movimento e liberação;
- bloqueio inicial dos controles `+` e `−`;
- incremento até o limite curado e decremento até zero;
- sincronização por categoria entre texto, Vergnaud e unidades;
- mapeamento visual-semântico da comparação;
- preservação da interrogação no enunciado;
- adição, remoção, limite e contratos polimórficos das unidades;
- affordance de pickup, cursores e renderização 2,5D;
- comparação dos arquivos de dados do projeto com a C120, sem alterações.

## Resultado

O Gérard passa a apresentar um arraste com resposta elástica e inercial discreta, mantendo precisão na soltura e todas as regras semânticas consolidadas na C120.
