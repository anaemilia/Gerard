# Relatório de atualização dos PDFs - C121

Data: 16/07/2026

## Documentos atualizados

1. **Co-design humano-IA no Gérard**
   - corpus ampliado até C121;
   - 112 ciclos de decisão consistentes;
   - 39 ciclos estruturais/de generalização, 26 de consistência teórica/metodológica e 47 de refinamento visual/operacional;
   - inclusão dos ciclos C109 a C121;
   - inclusão do drag-and-drop com física de massa, mola, amortecimento e momento;
   - registro da soltura exata antes da validação e da preservação do estado semântico compartilhado;
   - registro das regras dos controles quantitativos consolidadas na C120.

2. **Análise da Complexidade do Sistema Gérard**
   - análise ampliada até C121;
   - formalização do controlador massa-mola-amortecedor;
   - custo constante O(1) por quadro de animação e O(f) para f quadros de uma interação;
   - separação entre trajetória visual e coordenada semântica final;
   - avaliação dos riscos de atraso excessivo, oscilação, perda de alvo e eventos residuais;
   - preservação dos limites curados, do piso zero, da relação aditiva e da imutabilidade dos dados de curadoria.

## Verificação dos PDFs

- artigo compilado em LaTeX com 49 páginas;
- análise de complexidade compilada em LaTeX com 13 páginas;
- ambos os arquivos foram renderizados em PNG para inspeção visual;
- não foram observados textos cortados, sobreposições, glifos quebrados ou páginas ausentes;
- a compilação do artigo mantém avisos não fatais de referências e ajuste de caixas já presentes na cadeia documental;
- a análise de complexidade apresenta apenas avisos tipográficos não fatais.

## Relação com a implementação

A documentação descreve a versão C121 entregue no mesmo pacote. A animação altera somente a posição visual intermediária durante o arraste. Na soltura, o elemento é colocado na coordenada exata do mouse antes da detecção do alvo, da validação e da publicação no estado compartilhado. Portanto, a física não modifica os valores curados nem as regras matemáticas consolidadas na C120.
