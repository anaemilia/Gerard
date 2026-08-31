# Gérard — prova web funcional

Prova funcional de uma ação completa. O navegador carrega o estado de uma
tentativa, envia uma proposta para o papel Todo e renderiza o novo estado
devolvido pelo domínio Java.

## Fronteiras protegidas

- o domínio Java continua sendo a fonte semântica;
- o JSON é transporte, não uma nova fonte de regras;
- o JavaScript não calcula nem valida a relação aditiva;
- `RelacaoEstruturalComposicao` diagnostica a proposta;
- `PapelQuantitativo` registra a tentativa e aceita o valor;
- a geometria pertence ao renderizador web e não copia pixels do Swing;
- identidades de papéis e correspondências são nominais;
- o status editorial da amostra permanece visível.

`situacao-exemplo.json` é um exemplo técnico marcado como
`CANDIDATA_NAO_CURADA`. Ele não entra no catálogo de situações curadas.

## Executar

Compile o projeto e execute `gerard.infraestrutura.web.ServidorPrototipoWeb`.
Depois, abra `http://localhost:8080`. O servidor Python estático anterior não
serve os endpoints `/api` e deve ser encerrado antes.

O teste `TesteContratoWebSituacaoProblema` confirma que a amostra corresponde
à projeção produzida pelo código Java e que o contrato usa as identidades
semânticas da situação.
