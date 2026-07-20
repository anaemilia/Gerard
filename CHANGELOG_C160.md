# CHANGELOG C160

## Aviso para termo desconhecido vazio

A curadoria passou a exibir um aviso visual não modal imediatamente abaixo do campo `termo_desconhecido`.

Regras:

- campo vazio: borda âmbar e aviso para selecionar o papel da interrogação;
- campo originalmente vazio, mas harmonizado automaticamente a partir de um único `?`: aviso para revisar a seleção automática;
- seleção explícita do curador: o aviso desaparece;
- tradução com semântica herdada: o aviso fica oculto;
- nenhuma inferência nova foi criada e nenhuma regra de salvamento foi alterada.

A implementação está em `gerard.campoaditivo.curadoria.AvisoTermoDesconhecidoVazio`, fora da `Main.java`.
