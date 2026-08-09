# Fase 1.1 - estabilização do build e dos testes

Data: 2026-08-08  
Escopo: infraestrutura de build e testes, sem alteração do comportamento da aplicação.

## Resultado

- `ant clean jar`: aprovado no Windows.
- JAR gerado em `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`.
- 446 fontes Java de produção compilados.
- 51 testes Java compilados em conjunto.
- 47 testes headless executados e aprovados.
- 4 testes gráficos compilados e separados da execução headless.
- 0 testes obsoletos.
- 0 testes reprovados na suíte headless.
- Verificador global: aprovado integralmente.
- Três verificadores Python: aprovados.

## Build portátil

O alvo `compile` importado de `nbproject/build-impl.xml` lia as propriedades
do projeto, mas não fornecia os JARs de `lib` ao `<javac>`. O alvo `run`
também dependia de uma string de classpath com separador específico de sistema
operacional.

Como `nbproject/build-impl.xml` é gerado e ignorado pelo Git, `build.xml`
passou a sobrescrever os alvos `compile` e `run` e a declarar o classpath por
componentes Ant:

```xml
<classpath>
    <fileset dir="lib" includes="*.jar"/>
</classpath>
```

No alvo `run`, `build/classes` é acrescentado com `<pathelement location>`.
Essa configuração não depende de `:` ou `;` e funciona em Windows e Linux.

## Testes alinhados ao comportamento consolidado

### Nova situação após a conclusão

O antigo teste esperava um campo removido chamado `botaoSortear` e exigia
preservação da mesma categoria. O comportamento atual, documentado em
`Main.java`, usa `itemNovaSituacao`, que sorteia também a categoria.

O teste agora valida que a opção Sim:

- aciona exatamente uma vez o item consolidado de nova situação;
- seleciona uma categoria válida;
- mantém coerência entre a situação carregada e a categoria sorteada.

O teste foi renomeado para `TesteConclusaoAcionaNovaSituacao` e seu script
para `testar_conclusao_nova_situacao.sh`.

### Inicialização sem categoria

Os dois botões atuais de sorteio por grupo (`Medidas` e `Relações`) ficam
disponíveis desde o início por decisão já registrada no código. O teste foi
atualizado para verificar esses dois controles, inclusive após troca de idioma
e carregamento de categoria.

### Destaque de conclusão

O teste agora confirma a política vigente: azul de sucesso é aplicado aos
elementos e itens numéricos corretos, não aos conectores estruturais. Nenhuma
cor ou regra visual da aplicação foi alterada.

### Selo de conclusão

O teste passou a usar `mostrarAoLadoDireitoDoDiagrama` e verifica a posição à
direita, acompanhando a API e a decisão visual atuais.

### Aba Construir

O teste passou a esperar o rótulo curto atual da aba, `Construir`, mantendo o
título interno `Construa a situação-problema`.

## Separação headless/gráfica

O verificador Windows compila todos os 51 testes. Estes quatro requerem um
ambiente com display e não são executados como headless:

- `TesteAbaMontagem`;
- `TesteBloqueioDinamicoIdiomaCuradoria`;
- `TesteConclusaoAcionaNovaSituacao`;
- `TesteInicializacaoSemCategoria`.

Os scripts Unix correspondentes continuam podendo executá-los com `xvfb-run`.

## Verificações executadas

```text
ant clean jar
python scripts/verificar_regressao_gerard.py
python scripts/verificar_linha_base_windows.py
python scripts/testar_vinculos_traducoes.py
python scripts/testar_curadoria_sem_ids.py
python scripts/testar_idiomas_configuraveis.py
```

Resultados da suíte Windows:

```text
RESULTADO_COMPILACAO=OK
RESULTADO_COMPILACAO_TESTES=OK
TESTES_APROVADOS=47
TESTES_GRAFICOS_NAO_EXECUTADOS=4
TESTES_OBSOLETOS=0
TESTES_REPROVADOS=0
```

## Observação remanescente

O JDK atual é 22.0.1 e o projeto usa `source=1.8`/`target=1.8`. A compilação
passa, mas emite quatro avisos recomendando `--release 8`. Essa modernização
de configuração pode ser feita separadamente, após confirmar qual versão
mínima de Java deve executar o Gérard.
