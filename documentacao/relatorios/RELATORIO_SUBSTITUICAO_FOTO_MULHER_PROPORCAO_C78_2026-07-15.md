# Relatório de alteração — substituição da foto com a mulher no submenu “Em Recife”

**Data:** 2026-07-15  
**Ciclo:** C78  
**Tipo de intervenção:** atualização visual localizada.

## Síntese
A foto da esquerda no submenu **Em Recife** foi substituída pela nova versão melhorada da foto com a mulher, preservando o arranjo com duas imagens lado a lado.

## Alterações implementadas
- substituição do arquivo `src/gerard/imagens/em_recife_rostos.png` pela foto nova;
- manutenção do mesmo layout com duas imagens;
- preservação da mesma lógica de redimensionamento automático;
- preservação da proporção compatível com a foto dos dois homens exibida ao lado.

## Resultado esperado
Ao abrir **Sobre → Gérard Vergnaud → Em Recife**, a imagem da esquerda passa a mostrar a nova foto com a mulher, dimensionada de forma proporcional ao quadro da foto dos dois homens.

## Verificação realizada
- compilação Ant/NetBeans: **aprovada**.
