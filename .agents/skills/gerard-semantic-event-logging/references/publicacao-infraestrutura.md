# Publicação e infraestrutura de eventos

Leia esta referência ao implementar ou revisar persistência, indexação,
consulta, exportação, retenção, anonimização ou validação de versão.

O publicador deve ser injetável por interface. Para funcionamento sem
infraestrutura, prefira Null Object, como `PublicadorEventoDominio.NENHUM`, em
vez de dependência `null`.

O publicador recebe um registro já produzido pelo objeto proprietário. Ele não
constitui a ação, não avalia C/E, não reinterpreta o gesto e não passa a ser o
dono do log por gravá-lo.

A infraestrutura pode:

- persistir;
- indexar;
- consultar;
- exportar;
- aplicar retenção e anonimização;
- validar a versão do esquema de evento.

Essas operações não lhe transferem propriedade semântica sobre os fatos.
