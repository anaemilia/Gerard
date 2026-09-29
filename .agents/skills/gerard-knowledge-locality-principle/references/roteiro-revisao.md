# Roteiro de revisão da localidade

Leia esta referência antes de adicionar uma regra ou auditar a localização de
conhecimento já existente.

## Perguntas obrigatórias

1. A regra depende de um único objeto?
2. Coordena vários objetos de escopo fechado?
3. É uma política pedagógica reutilizável?
4. É responsabilidade de infraestrutura?
5. É uma interpretação sobre a atividade do usuário?
6. Existe fonte normativa para o nome e o significado usados?
7. A regra aprendida foi publicada e versionada pelo Modelador?
8. A decisão pode ser reconstruída com a versão e a regra consultadas?
9. Os fatos correntes pertencem ao menor proprietário semântico capaz de
   interpretá-los, sem duplicar um Modelo da Situação/Solução global?

## Exemplo: composição

Para `Todo = Parte1 + Parte2`:

- os domínios numéricos pertencem aos papéis;
- a equação pertence à `RelacaoEstruturalComposicao`;
- a relação possui seu repertório e seleciona ajuda usando o diagnóstico local
  e a projeção imutável do Modelo do Usuário;
- o Modelador aprende e publica as regras que alimentam essa projeção;
- a relação produz o registro da ação, e uma porta de infraestrutura apenas o
  persiste como evento semântico;
- uma hipótese de teorema-em-ação pertence ao modelo analítico e exige
  evidências.
