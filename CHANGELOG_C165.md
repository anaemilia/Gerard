# C165 — escolha obrigatória do sinal na curadoria

- A curadoria de `transformacao` e `valor_relativo` passa a exigir uma escolha explícita entre positivo, negativo e neutro.
- O campo numérico recebe somente a magnitude; os sinais `+` e `-` são escolhidos no seletor ao lado.
- A opção inicial “Selecione o sinal…” não pode ser salva.
- `?` continua permitido, mas não dispensa a escolha do sinal semântico.
- Zero exige a opção neutra; a opção neutra não pode ser usada com magnitude não nula.
- Valores legados com sinal explícito recuperam automaticamente a opção correspondente.
- Traduções herdam a escolha da versão original e mantêm os controles semânticos bloqueados.
- A regra, o componente, o controlador e a validação foram isolados em `gerard.campoaditivo.curadoria.sinal`.
- `Main.java` permaneceu byte a byte idêntico à versão-base enviada.
