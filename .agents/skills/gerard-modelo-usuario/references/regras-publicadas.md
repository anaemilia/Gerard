# Regras adaptativas publicadas

Leia esta referência ao criar, revisar, publicar, rejeitar ou retirar uma regra
adaptativa do Modelo do Usuário.

Cada regra disponibilizada para aplicação deve possuir, no mínimo:

- identificador e versão;
- algoritmo de origem (`PART`, `J48` ou `APRIORI`);
- data de publicação e proveniência dos casos;
- condições expressas em atributos do modelo e fatos permitidos;
- proprietário semântico e escopo, por exemplo `PAPEL`, `RELACAO`,
  `TENTATIVA` ou `SITUACAO`, em vocabulário aberto a outros objetos
  semanticamente definidos;
- apoio recomendado dentre o repertório do proprietário;
- suporte, confiança e lift quando o algoritmo os fornecer;
- estado de publicação, permitindo rejeição ou retirada.

Regra minerada não é invariante operatório nem conclusão sobre
conceito-em-ação. Atribuições analíticas continuam sendo do pesquisador.
