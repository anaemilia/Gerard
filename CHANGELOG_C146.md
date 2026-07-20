# C146 — Perfis de categoria e unidades concretas sincronizadas

## Arquitetura

Foram criados perfis de interação para classificar as categorias em:

- `SEM_RELACAO_ASSINADA`;
- `COM_RELACAO_ASSINADA`.

Os perfis centralizam papéis assinados, índices assinados do estado compartilhado e capacidades como eixo dos inteiros e cruzamento do zero.

## Tábua de transformação

A criação de quadradinhos passa a usar diretamente o snapshot semântico. Portanto, números posicionados pelo mouse ou digitados no diagrama de Vergnaud geram imediatamente a quantidade concreta correspondente nas zonas Antes, Mudança e Depois.
