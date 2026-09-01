---
name: gerard-knowledge-locality-principle
description: Localiza cada tipo de conhecimento no elemento arquitetural responsável, distinguindo conhecimento local, relacional, pedagógico e analítico.
---

# Princípio da Localidade do Conhecimento

## Dependência normativa

Leia `gerard-semantic-model/REFERENCE.md` antes de aplicar esta skill.

## Objetivo

Todo conhecimento deve permanecer próximo do elemento arquitetural que possui autoridade legítima sobre ele.

A localidade não significa colocar toda regra em um objeto individual. A localização correta depende da natureza do conhecimento.

## Tipos de localidade

### 1. Localidade no objeto

Use quando a regra depende apenas da identidade, do estado ou das restrições locais de uma entidade ou papel.

Exemplos:

- domínio numérico aceito por um papel;
- presença de valor;
- identidade semântica;
- descritor abstrato de representação.

### 2. Localidade relacional

Use um coordenador de escopo fechado quando a regra envolve vários objetos irmãos de uma mesma representação.

Exemplos:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`;
- compatibilidade entre papéis e unidades.

Esse coordenador não é uma política global e não deve ser chamado de invariante operatório.

### 3. Localidade pedagógica

Separe aprendizado transversal de aplicação local.

Exemplos:

- o Agente Modelador aprende, versiona e publica regras a partir dos casos;
- o Modelo do Usuário armazena o perfil e as regras publicadas;
- o proprietário semântico escolhe, em seu próprio repertório, a ajuda
  aplicável ao diagnóstico factual e à projeção de usuário recebida;
- a interface apresenta a decisão sem reinterpretá-la.

Uma política que cruza vários objetos não pode ser colocada arbitrariamente
em um papel. Localize-a no menor objeto de escopo que possua todos os fatos
necessários: relação estrutural, tentativa ou situação-problema. Use serviço
especializado somente quando a política for genuinamente transversal.

O objeto não conhece o modelo mutável completo, mecanismos de aprendizagem,
persistência ou tecnologia de interface. Ele recebe um contexto adaptativo
imutável e de leitura, limitado ao seu escopo.

O contexto histórico e o contexto corrente também obedecem à localidade. A
fotografia do Modelo do Usuário é projetada por dimensão; o Modelo da
Situação/Solução é projetado em fatos tipados do proprietário. Não criar um
coordenador global que leia toda a sessão e escolha em nome dos objetos.

### 4. Localidade de infraestrutura

Use infraestrutura para persistir, indexar, transportar, renderizar ou exportar.

A API semântica do Gérard pertence a essa fronteira de infraestrutura enquanto
HTTP/JSON, serialização e compatibilidade. Ela publica decisões dos proprietários
semânticos, mas não passa a possuir as regras que transporta. O detalhamento dos
contratos e de sua maturidade pertence a `gerard-api-semantica`.

Os objetos ricos possuem e produzem os registros factuais que pertencem ao
seu conhecimento: o objeto representacional registra o gesto que o envolve; o
objeto semântico ou a relação estrutural registra a ação constituída e seu
resultado. A infraestrutura apenas executa persistência, indexação e
transporte; ela não se torna proprietária desses logs. Objetos não executam
diretamente I/O em arquivo ou banco.

Uma ação que envolve vários objetos continua sendo uma única ação. Seu log
pertence ao menor proprietário relacional ou agregado que possui o conhecimento
do conjunto; cada objeto aparece como participante referenciado, sem duplicar
o registro.

### 5. Localidade epistemológica

Ações e verbalizações pertencem aos registros da atividade. Hipóteses sobre esquemas, teoremas-em-ação e conceitos-em-ação pertencem a um modelo analítico separado.

Nenhum objeto do diagrama é autoridade sobre o conhecimento-em-ação do usuário.

## Regras

- Evite conhecimento duplicado.
- Evite regras específicas em controllers e utilitários genéricos.
- Não coloque relações multiobjeto em um único papel.
- Não coloque política pedagógica transversal dentro de um papel isolado.
- Não concentre num agente central a escolha que pertence ao repertório de um
  proprietário semântico.
- Não transfira para um logger, serviço ou agente a propriedade do registro
  que pertence ao objeto rico; separe produção factual de persistência técnica.
- Não crie uma ação por objeto quando um único ato envolver vários objetos;
  use um proprietário relacional e referências aos participantes.
- Não entregue o Modelo do Usuário mutável completo aos objetos.
- Não trate evento factual como hipótese cognitiva.
- Não trate hipótese analítica como verdade definitiva.
- Preserve a possibilidade de análise inconclusiva.

## Perguntas obrigatórias

Antes de adicionar uma regra, responda:

1. A regra depende de um único objeto?
2. Coordena vários objetos de escopo fechado?
3. É uma política pedagógica reutilizável?
4. É responsabilidade de infraestrutura?
5. É uma interpretação sobre a atividade do usuário?
6. Existe uma fonte normativa que sustenta o nome e o significado usados?
7. A regra aprendida foi publicada e versionada pelo Modelador?
8. A decisão pode ser reconstruída com a versão e a regra consultadas?
9. Os fatos correntes pertencem ao menor proprietário semântico capaz de
   interpretá-los, sem duplicar um Modelo da Situação/Solução global?

## Exemplo

Para `Todo = Parte1 + Parte2`:

- os domínios numéricos pertencem aos papéis;
- a equação pertence à `RelacaoEstruturalComposicao`;
- a relação possui seu repertório de ajudas e seleciona uma pista usando o
  diagnóstico que produziu e a projeção imutável do Modelo do Usuário;
- o Modelador aprende e publica as regras que alimentam essa projeção;
- a relação estrutural possui e produz o registro da ação; uma porta de
  infraestrutura apenas o persiste como evento semântico;
- uma hipótese de teorema-em-ação pertence ao modelo analítico e exige evidências.
