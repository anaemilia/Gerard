# Tipos exemplificativos e antipadrões

Leia esta referência ao nomear tipos, revisar cobertura ou auditar produtores
de eventos. A lista de tipos é aberta e não autoriza criar eventos sem fato
semântico correspondente.

## Exemplos de tipos

- papel selecionado;
- valor proposto, aceito ou rejeitado;
- papel posicionado;
- representação alterada;
- relação estrutural verificada;
- cálculo sugerido pelo sistema;
- feedback apresentado;
- ajuda adaptativa decidida;
- explicação solicitada;
- verbalização registrada;
- tentativa iniciada, concluída ou abandonada.

## Antipadrões

- Logar apenas `mouseClicked(x,y)`.
- Omitir a origem da ação.
- Registrar cálculo automático como ação do estudante.
- Vincular explicação apenas à situação, ignorando a tentativa.
- Inserir inferência cognitiva no evento factual.
- Declarar invariante operatório a partir de evento isolado.
- Fazer o objeto executar I/O diretamente ou atribuir ao persistidor a
  propriedade semântica do registro.
- Emitir uma ação duplicada para cada objeto participante da mesma ação.
