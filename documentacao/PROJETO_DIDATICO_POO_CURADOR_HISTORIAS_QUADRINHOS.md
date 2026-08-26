# Mini-projeto de POO: da situação-problema ao roteiro de quadrinhos

## 1. Identificação

- **Nome do projeto:** Curador de Histórias Aditivas
- **Público:** estudantes iniciantes de Programação Orientada a Objetos
- **Organização sugerida:** equipes de três a cinco estudantes
- **Duração sugerida:** seis etapas de uma semana
- **Linguagem sugerida:** Java 8 ou superior
- **Origem do problema:** tela de curadoria de situações-problema do Gérard

## 2. Problema proposto aos estudantes

Uma situação-problema aditiva contém números, objetos contados, participantes,
estados e transformações. Num formulário tradicional, esses elementos podem
ser armazenados apenas como textos e números soltos. Isso dificulta validar a
coerência da situação e reutilizar o mesmo conhecimento em formas diferentes,
como enunciado, diagrama, material concreto ou história em quadrinhos.

A equipe deverá construir um pequeno sistema no qual os elementos da situação
sejam objetos de domínio com responsabilidades próprias. A primeira versão
deverá representar somente uma **composição de transformações**, como:

> Vovó possui 2 rosas brancas e 3 rosas amarelas. Deu 1 rosa branca e 1 rosa
> amarela para sua netinha. Com quantas rosas vovó ficou?

O sistema deverá transformar os dados curados numa sequência narrativa
semântica. Uma camada separada converterá essa sequência num roteiro de
quadrinhos.

## 3. Objetivos de aprendizagem

Ao final do projeto, cada estudante deverá ser capaz de:

1. distinguir classe, objeto, atributo, método e associação;
2. aplicar encapsulamento e proteger invariantes de um objeto;
3. usar composição de objetos antes de recorrer à herança;
4. representar vocabulários fechados com `enum`;
5. separar modelo de domínio, persistência e interface;
6. escrever testes para regras pertencentes aos objetos;
7. explicar por que um objeto rico reúne dados e comportamentos do conhecimento
   que representa;
8. reutilizar o mesmo modelo semântico em uma realização textual e numa
   realização em quadrinhos.

## 4. Escopo da primeira versão

### Incluído

- cadastro explícito de participantes;
- cadastro de objetos contados e de suas variações;
- estado inicial composto por quantidades;
- duas transformações ordenadas;
- estado final curado;
- indicação do papel desconhecido;
- validação estrutural e aritmética sem correção automática;
- geração de uma sequência narrativa semântica;
- geração de um roteiro textual de três a cinco quadros;
- persistência em JSON;
- testes automatizados do domínio.

### Fora do escopo

- implementar as seis categorias do Gérard;
- copiar toda a interface Swing existente;
- gerar imagens por inteligência artificial;
- inferir personagens, objetos ou ações a partir do enunciado;
- corrigir automaticamente um valor informado pelo curador;
- implementar agentes, Modelo do Usuário ou ajuda adaptativa;
- validar conceitos-em-ação ou outras hipóteses sobre o aprendiz.

Esses limites mantêm o projeto viável para iniciantes e impedem que detalhes da
interface escondam o aprendizado de POO.

## 5. O que falta acrescentar à curadoria atual

A tela atual contém a estrutura matemática, mas ainda não explicita todos os
papéis necessários para criar uma narrativa visual. Os campos
`personagem_1`, `personagem_2` e `personagem_3` não são suficientes: no exemplo,
“Rosas brancas” e “Rosas amarelas” aparecem nesses campos, embora sejam objetos
contados, e “netinha” aparece apenas no enunciado.

Não se deve tentar corrigir isso associando informações pela posição dos
campos ou extraindo palavras automaticamente do texto. O curador humano deverá
informar os papéis explicitamente.

### 5.1 Novos grupos de dados

| Grupo | Dados mínimos a acrescentar | Por que são necessários |
|---|---|---|
| Participantes | `id`, nome exibido e papel narrativo | Distinguir Vovó, netinha, ator, possuidor e destinatário |
| Família do objeto | `id` e nome conceitual, como “rosa” | Permitir totalizar objetos da mesma família |
| Variação do objeto contado | família e atributos semânticos, como cor branca ou amarela | Distinguir as quantidades manipuladas em cada transformação |
| Estado inicial detalhado | possuidor, objeto contado e quantidade para cada item | Representar 2 rosas brancas + 3 amarelas, e não apenas o total 5 |
| Transformação narrativa | ordem, ator, ação, destinatário, objeto, quantidade e sentido da mudança | Saber quem fez o quê, com qual objeto e em que ordem |
| Estado final detalhado | possuidor, objeto contado e quantidade de cada item | Produzir a cena final e conferir o total curado |
| Contexto narrativo | local e momento opcionais | Dar unidade visual aos quadros sem inferir cenário pelo enunciado |
| Referência visual | chave abstrata por participante e objeto | Permitir que outra camada escolha desenho, ícone ou animação |
| Realização textual | singular, plural e formas por idioma | Produzir texto correto sem colocar gramática no objeto de domínio |

### 5.2 Estrutura recomendada em vez de muitos campos planos

O formulário poderá apresentar quatro subpainéis repetíveis:

1. **Participantes**;
2. **Objetos contados**;
3. **Itens do estado inicial e final**;
4. **Transformações em ordem temporal**.

Internamente, esses dados deverão ser listas de objetos referenciados por
identidade. O enunciado continua sendo dado curado, mas não é usado para
adivinhar os objetos ausentes.

### 5.3 Preenchimento explícito do exemplo

| Elemento | Valor curado |
|---|---|
| Participante principal | Vovó |
| Destinatária | Netinha |
| Família de objeto | Rosa |
| Objeto contado 1 | Rosa, cor branca |
| Objeto contado 2 | Rosa, cor amarela |
| Estado inicial | Vovó: 2 rosas brancas e 3 rosas amarelas |
| Transformação 1 | Vovó dá 1 rosa branca à netinha |
| Transformação 2 | Vovó dá 1 rosa amarela à netinha |
| Estado final | Vovó: 1 rosa branca e 2 rosas amarelas; total 3 |
| Papel desconhecido | Total do estado final |

## 6. Modelo de objetos ricos

```text
SituacaoProblema
├── participantes: ParticipanteNarrativo
├── estadoInicial: EstadoDePosse
│   └── inventario: Inventario
│       └── itens: Quantidade
│           ├── numero: Numero
│           └── objeto: ObjetoContado
├── transformacoes: TransformacaoNarrativa
└── estadoFinalCurado: EstadoDePosse

SituacaoProblema ── produz ──► SequenciaNarrativa
SequenciaNarrativa ── realizada por ──► GeradorRoteiroQuadrinhos
GeradorRoteiroQuadrinhos ── produz ──► RoteiroQuadrinhos ──► Quadro
```

### 6.1 `Numero`

É um objeto-valor. Conhece operações numéricas e não conhece interface,
personagens ou imagens.

```java
public final class Numero {
    private final int valor;

    public Numero(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public Numero somar(Numero outro) {
        return new Numero(valor + outro.valor);
    }

    public Numero subtrair(Numero outro) {
        return new Numero(valor - outro.valor);
    }

    public boolean ehNegativo() {
        return valor < 0;
    }
}
```

### 6.2 `ObjetoContado`

É um objeto de domínio verdadeiro, não uma `String`. Conhece sua identidade e
os atributos que distinguem aquilo que está sendo contado.

```java
public final class ObjetoContado {
    private final String id;
    private final String familia;
    private final Map<String, String> atributosSemanticos;

    public boolean mesmaIdentidadeQue(ObjetoContado outro) {
        return outro != null && id.equals(outro.id);
    }

    public boolean pertenceAMesmaFamilia(ObjetoContado outro) {
        return outro != null && familia.equals(outro.familia);
    }

    public Quantidade quantificar(Numero numero) {
        return new Quantidade(numero, this);
    }
}
```

No exemplo, `rosa_branca` e `rosa_amarela` são objetos contados distintos, mas
pertencem à mesma família `rosa`. Isso permite representar cada transformação
e também calcular o total de rosas.

O objeto não guarda palavras traduzidas, caminhos de imagem, pixels ou
componentes Swing. Esses conhecimentos pertencem às realizações textual e
visual.

### 6.3 `Quantidade`

Reúne um número e o objeto contado. Impede operações entre objetos
incompatíveis.

```java
public final class Quantidade {
    private final Numero numero;
    private final ObjetoContado objeto;

    public Quantidade somar(Quantidade outra) {
        exigirMesmoObjeto(outra);
        return new Quantidade(numero.somar(outra.numero), objeto);
    }

    public Quantidade subtrair(Quantidade outra) {
        exigirMesmoObjeto(outra);
        Numero resultado = numero.subtrair(outra.numero);
        if (resultado.ehNegativo()) {
            throw new IllegalArgumentException("quantidade final negativa");
        }
        return new Quantidade(resultado, objeto);
    }
}
```

### 6.4 `Inventario`

Mantém as quantidades de um participante. Sabe adicionar, retirar e totalizar
objetos de uma mesma família.

Responsabilidades:

- localizar uma quantidade por identidade do objeto;
- aplicar entrada ou saída sem produzir quantidade negativa;
- totalizar todas as variações da família “rosa”;
- devolver uma nova fotografia do inventário após cada transformação.

O `Inventario` resolve um conhecimento que o campo simples
`estado_inicial = 5` não preserva: o total é formado por duas rosas brancas e
três amarelas.

### 6.5 `ParticipanteNarrativo`

Representa uma pessoa ou entidade que participa da história. Possui identidade
e nome curado. Os papéis de ator, possuidor e destinatário pertencem à relação
em que participa; não devem ser fixados para sempre dentro da pessoa.

### 6.6 `EstadoDePosse`

Associa um participante ao seu inventário em um momento da história. Conhece o
estado, mas não decide como ele será desenhado.

### 6.7 `TransformacaoNarrativa`

É responsável pela mudança de um estado para outro.

```java
public final class TransformacaoNarrativa {
    private final int ordem;
    private final ParticipanteNarrativo ator;
    private final ParticipanteNarrativo destinatario;
    private final AcaoNarrativa acao;
    private final Quantidade quantidade;
    private final SentidoMudanca sentido;

    public EstadoDePosse aplicarEm(EstadoDePosse estado) {
        validarParticipantes(estado);
        return sentido == SentidoMudanca.DIMINUIR
                ? estado.retirar(quantidade)
                : estado.adicionar(quantidade);
    }
}
```

`AcaoNarrativa` poderá começar com um vocabulário fechado pequeno:
`DAR`, `RECEBER`, `GANHAR` e `PERDER`. O efeito matemático não deve ser
deduzido apenas do verbo: `SentidoMudanca` é preenchido explicitamente pelo
curador.

### 6.8 `SituacaoProblema`

É o agregado que coordena a história completa. Deve:

- possuir o enunciado curado;
- manter participantes, estado inicial, transformações e estado final;
- aplicar as transformações na ordem declarada;
- comparar o estado resultante com o estado final curado;
- produzir um `ResultadoValidacao`, sem sobrescrever a decisão humana;
- produzir uma `SequenciaNarrativa` quando a estrutura estiver completa.

Ela não deve desenhar quadros nem acessar arquivos diretamente.

### 6.9 `ResultadoValidacao`

Em vez de devolver somente `true` ou `false`, informa problemas estruturados:

- participante inexistente;
- objeto contado inexistente;
- transformação sem ator ou objeto;
- ordem temporal repetida;
- tentativa de retirar quantidade indisponível;
- estado calculado divergente do estado final curado;
- papel desconhecido não informado.

Uma divergência curatorial é apresentada ao humano; o sistema não corrige o
campo automaticamente.

### 6.10 `SequenciaNarrativa`

É uma projeção semântica independente de mídia:

```text
ESTADO: Vovó possui 2 rosas brancas e 3 rosas amarelas.
EVENTO: Vovó dá 1 rosa branca à netinha.
EVENTO: Vovó dá 1 rosa amarela à netinha.
ESTADO: Vovó possui 1 rosa branca e 2 rosas amarelas.
PERGUNTA: Qual é o total do estado final?
```

Ela contém referências aos objetos, participantes e números. As frases acima
são apenas uma realização textual para leitura.

### 6.11 Objetos da representação em quadrinhos

`RoteiroQuadrinhos`, `Quadro`, `Balao` e `DescritorCena` são objetos ricos da
representação, não do núcleo matemático. Eles conhecem a sintaxe de quadrinhos:

- divisão da sequência em quadros;
- participante que aparece em cada quadro;
- objeto e quantidade visíveis;
- ação representada;
- texto de legenda ou balão;
- chave abstrata do cenário e dos recursos visuais.

Um `GeradorRoteiroQuadrinhos` recebe a `SequenciaNarrativa` e produz, por
exemplo:

1. quadro inicial com Vovó, duas rosas brancas e três amarelas;
2. quadro em que Vovó entrega uma rosa branca à netinha;
3. quadro em que entrega uma rosa amarela;
4. quadro final com uma rosa branca, duas amarelas e a pergunta.

O gerador não valida novamente a matemática e não altera a curadoria.

## 7. Realizações textual e visual

Para preservar a localidade do conhecimento:

| Conhecimento | Proprietário |
|---|---|
| Valor e operações numéricas | `Numero` |
| Identidade do que é contado | `ObjetoContado` |
| Número associado ao objeto | `Quantidade` |
| Conteúdo possuído por participante | `Inventario` e `EstadoDePosse` |
| Mudança entre estados | `TransformacaoNarrativa` |
| Ordem e coerência da história | `SituacaoProblema` |
| Sequência de fatos | `SequenciaNarrativa` |
| Singular, plural e idioma | `RealizacaoTextualObjeto` |
| Divisão em quadros e balões | `GeradorRoteiroQuadrinhos` |
| Imagem, fonte, cor de interface e pixels | renderizador da plataforma |
| Confirmação de que a situação está curada | pesquisador humano |

## 8. Exemplo de persistência JSON

```json
{
  "id": "exemplo-rosas-01",
  "categoria": "COMPOSICAO_TRANSFORMACOES",
  "participantes": [
    {"id": "vovo", "nome": "Vovó"},
    {"id": "netinha", "nome": "Netinha"}
  ],
  "objetos_contados": [
    {"id": "rosa_branca", "familia": "rosa", "atributos": {"cor": "branca"}},
    {"id": "rosa_amarela", "familia": "rosa", "atributos": {"cor": "amarela"}}
  ],
  "estado_inicial": {
    "possuidor": "vovo",
    "itens": [
      {"objeto": "rosa_branca", "quantidade": 2},
      {"objeto": "rosa_amarela", "quantidade": 3}
    ]
  },
  "transformacoes": [
    {
      "ordem": 1,
      "ator": "vovo",
      "acao": "DAR",
      "destinatario": "netinha",
      "objeto": "rosa_branca",
      "quantidade": 1,
      "sentido_no_estado_principal": "DIMINUIR"
    },
    {
      "ordem": 2,
      "ator": "vovo",
      "acao": "DAR",
      "destinatario": "netinha",
      "objeto": "rosa_amarela",
      "quantidade": 1,
      "sentido_no_estado_principal": "DIMINUIR"
    }
  ],
  "estado_final_curado": {
    "possuidor": "vovo",
    "itens": [
      {"objeto": "rosa_branca", "quantidade": 1},
      {"objeto": "rosa_amarela", "quantidade": 2}
    ]
  },
  "papel_desconhecido": "total_estado_final",
  "status": "CANDIDATA_NAO_CURADA"
}
```

O estado `CANDIDATA_NAO_CURADA` é obrigatório até que o professor ou
pesquisador revise a estrutura e a narrativa.

## 9. Histórias de usuário

### Estudante de POO

- Como estudante, quero modelar um número e um objeto contado separadamente
  para compreender composição de objetos.
- Como estudante, quero que a quantidade rejeite operações entre objetos
  incompatíveis para compreender invariantes.
- Como estudante, quero aplicar transformações a um estado para observar o
  comportamento distribuído entre objetos ricos.
- Como estudante, quero gerar o mesmo conteúdo como texto e roteiro para
  compreender a separação entre domínio e representação.

### Professor

- Como professor, quero um problema pequeno e testável para avaliar
  encapsulamento e responsabilidade das classes.
- Como professor, quero identificar quando uma equipe colocou regras do
  domínio na interface ou no arquivo JSON.
- Como professor, quero comparar diferentes modelos de classes produzidos
  pelas equipes e discutir suas consequências.

### Curador humano

- Como curador, quero informar explicitamente participantes, objetos e ações
  para que o sistema não invente relações narrativas.
- Como curador, quero receber diagnósticos de divergência sem que meus dados
  sejam sobrescritos automaticamente.

## 10. Etapas de desenvolvimento

### Etapa 1 — vocabulário e cartões CRC

- identificar objetos, responsabilidades e colaboradores;
- produzir um diagrama inicial;
- distinguir dados da situação e dados da interface;
- escrever o exemplo das rosas sem código.

**Entrega:** cartões CRC e diagrama comentado.

### Etapa 2 — valores e objetos contados

- implementar `Numero`, `ObjetoContado` e `Quantidade`;
- testar igualdade, soma, subtração e incompatibilidade de objetos;
- evitar setters que permitam estado inválido.

**Entrega:** código e testes do núcleo quantitativo.

### Etapa 3 — estados e transformações

- implementar `Inventario`, `ParticipanteNarrativo`, `EstadoDePosse` e
  `TransformacaoNarrativa`;
- aplicar as duas transformações do exemplo;
- preservar fotografias anteriores dos estados.

**Entrega:** sequência de estados executada pelo console.

### Etapa 4 — situação e validação

- implementar `SituacaoProblema` e `ResultadoValidacao`;
- comparar resultado calculado e estado final curado;
- apresentar divergências sem correção automática.

**Entrega:** relatório de validação estruturado.

### Etapa 5 — persistência

- implementar `RepositorioSituacoes`;
- salvar e carregar JSON;
- impedir que o repositório contenha regras matemáticas.

**Entrega:** arquivo JSON e teste de ida e volta.

### Etapa 6 — roteiro de quadrinhos

- implementar `SequenciaNarrativa`, `RoteiroQuadrinhos` e `Quadro`;
- converter cada estado ou transformação em quadro;
- gerar inicialmente uma saída textual ou HTML simples;
- manter imagens e layout fora do domínio.

**Entrega:** roteiro de três a cinco quadros e apresentação da arquitetura.

## 11. Critérios de aceitação

- [ ] O exemplo das rosas é representado sem usar `String` para quantidades.
- [ ] Rosa branca e rosa amarela são objetos distintos da mesma família.
- [ ] O inventário inicial totaliza cinco rosas.
- [ ] A primeira transformação retira somente uma rosa branca.
- [ ] A segunda transformação retira somente uma rosa amarela.
- [ ] O inventário final totaliza três rosas.
- [ ] Uma retirada impossível produz diagnóstico e não corrompe o estado.
- [ ] Somar rosas e bonecas é rejeitado pelo domínio.
- [ ] Cada transformação referencia ator, destinatário e objeto explicitamente.
- [ ] Nenhuma associação narrativa é inferida pela posição de um campo.
- [ ] O valor final curado não é sobrescrito quando diverge do cálculo.
- [ ] O roteiro é criado a partir da sequência semântica, não do texto bruto.
- [ ] Nenhuma classe de domínio importa Swing, AWT ou biblioteca de JSON.
- [ ] Os testes do domínio executam sem abrir a interface.
- [ ] O JSON carregado produz o mesmo modelo que foi salvo.

## 12. Casos de teste mínimos

1. duas rosas brancas mais três amarelas totalizam cinco rosas;
2. retirar uma rosa branca deixa uma rosa branca;
3. retirar uma amarela depois deixa duas amarelas;
4. o total final é três;
5. retirar três rosas brancas quando existem duas é rejeitado;
6. uma transformação sem ator é inválida;
7. duas transformações com a mesma ordem são inválidas;
8. um objeto referido por uma transformação precisa existir no catálogo;
9. estado final curado divergente gera diagnóstico, não correção;
10. a sequência narrativa mantém a ordem das duas transformações;
11. o roteiro possui quadro inicial, dois quadros de ação e quadro final;
12. trocar o renderizador textual pelo de quadrinhos não altera o domínio.

## 13. Organização da equipe

Os papéis devem rodar a cada etapa:

- **modelador do domínio:** propõe responsabilidades e invariantes;
- **implementador:** escreve as classes da etapa;
- **responsável por testes:** escreve casos antes ou junto do código;
- **revisor de localidade:** procura regras colocadas na classe errada;
- **integrador:** mantém o repositório e a demonstração executável.

O rodízio evita que somente um estudante compreenda o modelo.

## 14. Rubrica de avaliação

| Critério | Pontos |
|---|---:|
| Modelo expressa participantes, objetos, quantidades, estados e transformações | 20 |
| Encapsulamento e proteção de invariantes | 20 |
| Responsabilidades localizadas nos objetos corretos | 20 |
| Testes automatizados e casos de erro | 15 |
| Separação entre domínio, JSON e representação | 15 |
| Clareza da apresentação e justificativa das decisões | 10 |
| **Total** | **100** |

## 15. Perguntas para discussão em sala

1. Por que `ObjetoContado` é melhor do que uma `String`?
2. A cor branca pertence ao domínio ou apenas ao desenho nesse problema?
3. Por que o participante não deve guardar permanentemente o papel de
   destinatário?
4. Quem deve impedir a retirada de uma quantidade inexistente?
5. Por que o repositório JSON não deve recalcular o estado final?
6. Por que `SituacaoProblema` produz uma sequência semântica, mas não desenha
   os quadros?
7. O que precisaria mudar para adicionar outra categoria de Vergnaud?
8. Que conhecimento permaneceria igual numa versão web ou móvel?

## 16. Evoluções posteriores

Somente depois de a primeira versão estar testada:

- adicionar outras categorias aditivas uma por vez;
- criar realizações textuais em diferentes idiomas;
- substituir o roteiro textual por imagens ou animações;
- permitir que o professor crie candidatas de novas histórias;
- rastrear cada candidata às situações curadas que a fundamentaram;
- integrar o roteiro como uma forma de scaffolding, mediante decisão
  pedagógica explícita do proprietário semântico correspondente.

Uma candidata gerada nunca deve ser gravada diretamente como situação
validada. A promoção ao catálogo curado continua sendo responsabilidade do
pesquisador humano.
