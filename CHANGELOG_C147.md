# C147 - modelo semântico explícito

## Decisão arquitetural

O comportamento numérico foi retirado das categorias e representações e colocado em objetos do domínio:

- `NumeroNatural` para N0;
- `NumeroInteiro` para Z;
- `ValorDesconhecido` com preservação do universo esperado.

Os papéis quantitativos declaram o universo aceito. As categorias passam a agregar papéis, restrições e relações por esquemas simples ou pelo padrão Composite.

## Novos módulos

```text
gerard.semantica
├── numero
├── papel
├── contexto
├── entidade
├── pista
├── elemento
├── categoria
└── situacao
```

## Compatibilidade

As classes anteriores de política, catálogo e perfis foram preservadas como fachadas que delegam ao novo modelo. Nenhuma regra visual foi adicionada à `Main`.

## Documentação

Foram atualizados:

- `ARTIGO_CODESIGN_HUMANO_IA_GERARD.pdf`;
- `ANALISE_COMPLEXIDADE_GERARD.pdf`;
- fontes LaTeX correspondentes;
- matriz de regressão.
