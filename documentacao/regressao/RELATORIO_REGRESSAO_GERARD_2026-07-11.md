# Relatório de regressão — Gerard

Data: 2026-07-11

## Alterações realizadas nesta versão

1. Inclusão do arquivo `CHECKLIST_REGRESSAO_GERARD.md` no projeto.
2. Inclusão dos scripts:
   - `scripts/verificar_regressao_gerard.py`;
   - `scripts/verificar_regressao_gerard.sh`.
3. Correção de três chaves de internacionalização que existiam em português, mas não existiam em inglês e francês:
   - `pesq.d3.flow.effectiveness.guide`;
   - `pesq.d3.flow.effectiveness.percent`;
   - `pesq.d3.flow.effectiveness.total`.

## Arquivos modificados ou adicionados

- `src/gerard/i18n/ServicoLocalizacao.java` — adicionadas traduções inglesas e francesas para chaves da matriz de efetividade D3.
- `CHECKLIST_REGRESSAO_GERARD.md` — novo checklist fixo de regressão.
- `RELATORIO_REGRESSAO_GERARD_2026-07-11.md` — este relatório.
- `scripts/verificar_regressao_gerard.py` — verificação automatizada.
- `scripts/verificar_regressao_gerard.sh` — atalho para execução do verificador.

## Teste automatizado executado

Comando executado:

```bash
scripts/verificar_regressao_gerard.sh
```

Resultado:

- Compilação: `BUILD SUCCESSFUL`.
- JAR gerado: `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`.
- Chaves de internacionalização: mapas `pt`, `en` e `fr` com 546 chaves cada, sem chaves faltantes.
- Chaves críticas de interface, eixo, Venn e D3: verificadas.
- Sincronização entre representações: referências estáticas preservadas.
- Textos do eixo x: continuam usando `ServicoLocalizacao`.
- Botões de restauração: continuam usando `ui.button.restoreElements` e `ui.button.restoreDiagram`.
- Logs: limpeza automática permanece desativada por padrão e protegida por propriedade explícita com backup.
- D3: termos estruturais encontrados.

## Aviso remanescente

O script informa que as quantidades de situações-problema por idioma não são idênticas:

- Português: 66;
- Inglês: 72;
- Francês: 72.

Isso não impede a compilação nem a execução. A troca de idioma usa busca por contexto e valores quando há correspondência. Mesmo assim, se a exigência futura for paridade total entre idiomas, o arquivo `situacoes_vergnaud.tsv` deve ser revisado para garantir a mesma quantidade de registros por idioma e por tipo de situação.

## Teste manual recomendado antes de uso em sala

O teste automatizado não valida interação visual real de mouse/GUI. Antes de usar a versão, recomenda-se abrir o JAR e testar manualmente:

1. arrastar elementos para o diagrama de Vergnaud;
2. mover o ponto azul do eixo;
3. conferir o Diagrama de Venn;
4. trocar idioma sem perder o estado;
5. restaurar itens;
6. restaurar diagrama;
7. abrir a visão do pesquisador;
8. conferir se as visualizações D3 carregam.
