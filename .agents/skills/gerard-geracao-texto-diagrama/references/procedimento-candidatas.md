# Procedimento para gerar candidatas

Leia esta referência depois de confirmar a fonte curada, a categoria e as
fronteiras de autoridade no arquivo principal.

## Procedimento

1. Identifique os valores e a relação numérica fornecidos sem lhes atribuir
   uma categoria por conta própria.
2. Selecione padrões curados compatíveis com categoria, subtipo, papel
   desconhecido, idioma e contexto pedidos.
3. Reutilize fragmentos curados quando existirem; caso contrário, siga o
   fallback permitido por `gerard-construcao-texto`.
4. Realize os mesmos papéis semânticos em sintaxe textual própria, preservando
   sua relação e identidade. Leia personagens dos campos nomeados; nunca os
   associe por posição no diagrama.
5. Verifique a candidata contra o padrão curado e marque qualquer ausência de
   lastro. Sem correspondência clara, informe a lacuna em vez de improvisar.

## Ficha para revisão humana

Para cada candidata, apresente pelo menos:

```text
status: CANDIDATA_NAO_CURADA
categoria:
subtipo:
papel_desconhecido:
valores_por_papel:
personagens_por_campo:
idioma:
enunciado:
referencias_curadas:
observacoes_para_revisao:
```

A ficha serve à revisão humana; não é contrato de transporte entre cliente e
servidor nem linha pronta para o catálogo curado.
