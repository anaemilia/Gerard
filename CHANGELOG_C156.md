# CHANGELOG C156 — sincronização de valores monetários integrais

## Problema corrigido
Valores exibidos com duas casas decimais, como `25,00` e `18,00`, eram aceitos visualmente no diagrama de Vergnaud, mas não entravam no estado semântico compartilhado. O conversor anterior usava apenas `Integer.parseInt`, que rejeitava a vírgula ou o ponto decimal. Como consequência, o processo de transformação não criava os quadradinhos correspondentes.

## Solução
Foi criado `gerard.semantica.numero.ConversorTextoParaInteiroSemantico`.

O conversor:
- reconhece `25,00` e `25.00` como o inteiro `25`;
- reconhece separadores de milhar em português e inglês;
- preserva sinais `+`, `-` e `−`;
- aceita símbolos monetários ao redor do número;
- rejeita valores realmente fracionários, como `25,50`, sem truncamento silencioso.

A `Main.java` apenas delega a conversão para esse componente do domínio numérico.

## Resultado funcional
Após um posicionamento semântico válido no Vergnaud:
- `25,00` no estado inicial cria 25 quadradinhos;
- `-18,00` na transformação cria 18 quadradinhos no funil de retirada;
- o estado compartilhado preserva `25 + (-18) = 7`;
- a representação complementar materializa 7 quadradinhos no estado final.

## Contratos preservados
- primeiro posicionamento no Vergnaud;
- nenhuma antecipação de unidades antes da modelagem;
- transformação em ℤ e estados em ℕ₀;
- sincronização entre texto, Vergnaud, eixo e processo concreto;
- conclusão progressiva da C155;
- dados curados inalterados.
