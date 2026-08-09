# Linha de base da Fase 1 - build e testes do Gerard

Data: 2026-08-08  
Ambiente: Windows, JDK 22.0.1, fontes configurados para Java 8  
Escopo: diagnóstico e infraestrutura de verificação, sem alteração do comportamento da aplicação.

## Resultado executivo

- Os 446 arquivos Java de produção compilam quando os dois JARs de `lib` são fornecidos com o separador de classpath da plataforma.
- O build oficial `ant clean jar` não é reproduzível no Windows atual.
- O verificador global reproduz 5 falhas: 2 de build e 3 verificações textuais obsoletas.
- Dos 51 testes Java autocontidos, 44 passaram, 3 estão obsoletos e não compilam, 2 exigem ambiente gráfico e 2 possuem expectativas incompatíveis com decisões atuais do código.
- Os 3 verificadores Python executados passaram.
- Nenhuma regressão funcional nova foi confirmada nesta fase.

## Estado inicial do repositório

- Branch observada: `migracao-nomenclatura-relacao-estrutural`.
- Alteração preexistente não relacionada: diretório `.agents/` não rastreado.
- `src/Main.java`: 14.633 linhas e 156 imports (levantamento anterior desta tarefa).
- Dependências locais presentes:
  - `lib/weka-stable-3.8.6.jar`;
  - `lib/bounce-0.18.jar`.

## Build oficial Ant

Foi usado Apache Ant 1.10.17 portátil, pois o Ant não estava instalado no Windows.

Comando:

```text
ant clean jar
```

Resultado: falha durante `javac`, com 26 erros derivados da ausência das classes Weka no classpath. O JAR final não foi gerado.

Causa confirmada: `nbproject/project.properties` define:

```text
javac.classpath=lib/weka-stable-3.8.6.jar:lib/bounce-0.18.jar
```

O caractere `:` funciona como separador no Linux, mas não no Windows, que utiliza `;`. Isso explica por que `RESULTADO_BUILD_C166.txt` registra sucesso em Linux enquanto o mesmo build falha no Windows.

Também foram emitidos quatro avisos porque o build combina JDK 22 com `-source 8 -target 8`, sem bootstrap classpath de Java 8.

## Compilação independente da configuração Ant

Foi criado `scripts/verificar_linha_base_windows.py`, que usa `os.pathsep`, copia as dependências para a área temporária e compila em `tmp/linha-base-windows`.

Resultado:

```text
FONTES=446
DEPENDENCIAS=2
RESULTADO_COMPILACAO=OK
```

Conclusão: o código-fonte é compilável; a falha pertence à configuração portátil do build.

## Suíte Java autocontida

Resumo:

```text
TESTES_JAVA=51
TESTES_APROVADOS=44
TESTES_OBSOLETOS=3
TESTES_REPROVADOS=4
```

### Testes obsoletos que não compilam

1. `TesteConclusaoAcionaSortearMesmaCategoria`: acessa o campo removido/renomeado `TelaGerard.botaoSortear`.
2. `TesteInicializacaoSemCategoria`: acessa o mesmo campo antigo `TelaGerard.botaoSortear`.
3. `TestePosicionamentoSeloConclusao`: chama a assinatura removida `mostrarAbaixoDoDiagrama(Rectangle, Rectangle, int, int)`.

Classificação: infraestrutura de teste obsoleta, não falha funcional demonstrada.

### Testes incompatíveis com execução headless

1. `TesteAbaMontagem`: instancia `JFrame`/`Main` e falha sem dispositivo gráfico.
2. `TesteBloqueioDinamicoIdiomaCuradoria`: inicializa interface Swing e falha no ambiente headless.

Classificação: requisito de ambiente gráfico não declarado pela suíte.

### Expectativas antigas que ainda compilam

1. `TesteConclusaoModelagem`: exige destaque azul em elementos, conectores e itens. A implementação atual documenta que somente elementos com valor matemático recebem o destaque; conectores ficam excluídos deliberadamente.
2. `TesteTooltipCategoriaConstrucao`: espera `ui.tab.assembly=Construir situação-problema`, mas o catálogo atual define `ui.tab.assembly=Construir` e mantém `montagem.title=Construa a situação-problema`.

Classificação: testes de regressão desalinhados com decisões atuais. Antes de alterar produção, essas expectativas devem ser validadas com a especificação funcional vigente.

## Verificadores Python

Executados com sucesso:

- `scripts/testar_vinculos_traducoes.py`: 210 versões, 72 grupos conceituais e vínculos coerentes;
- `scripts/testar_curadoria_sem_ids.py`: identificadores técnicos ocultos e preservados internamente;
- `scripts/testar_idiomas_configuraveis.py`: quatro idiomas e restrições linguísticas consistentes.

## Verificador global

`scripts/verificar_regressao_gerard.py` foi ajustado para localizar `ant` ou `ant.bat` e reportar a ausência da ferramenta sem encerrar com traceback.

Resultado reproduzido: 5 falhas.

1. `ant clean jar`: infraestrutura de build não portátil.
2. `JAR gerado`: consequência direta da falha anterior.
3. `escolha Sim aciona o botão Sortear consolidado`: verificação textual obsoleta.
4. `botão Sortear preserva o fluxo consolidado`: verificação textual obsoleta.
5. `nomenclatura portuguesa usa Construir e Construa`: literal esperado desatualizado.

Todas as demais verificações estruturais do script passaram.

## Como reproduzir

Compilação e testes Java independentes do Ant no Windows:

```text
python scripts/verificar_linha_base_windows.py
```

Verificadores Python:

```text
python scripts/testar_vinculos_traducoes.py
python scripts/testar_curadoria_sem_ids.py
python scripts/testar_idiomas_configuraveis.py
```

Verificador global, após disponibilizar Ant no `PATH`:

```text
python scripts/verificar_regressao_gerard.py
```

## Próximas ações recomendadas

1. Tornar o classpath do Ant independente do sistema operacional.
2. Definir uma versão de JDK suportada ou migrar a compilação para `--release 8`.
3. Atualizar ou remover os três testes que não compilam.
4. Separar testes headless de testes que exigem interface gráfica.
5. Validar as duas expectativas funcionais divergentes antes de alterar código de produção.
6. Substituir progressivamente verificações textuais por testes comportamentais.
