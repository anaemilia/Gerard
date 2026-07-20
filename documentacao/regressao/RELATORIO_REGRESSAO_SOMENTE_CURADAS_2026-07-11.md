# Relatório de regressão — exibição apenas de situações-problema curadas

## Alteração solicitada
A tela principal do Gerard deve mostrar textos apenas de situações-problema curadas. Situações não validadas continuam disponíveis na aba Curadoria para revisão humana, mas deixam de ser usadas como fonte de enunciados na interação principal.

## Alterações realizadas

1. O repositório de situações passou a separar duas listas lógicas:
   - `listarTodas()`: mantém todas as situações para curadoria humana.
   - `listarValidadas()`: retorna apenas situações com `validada = true` e enunciado não vazio.

2. Os métodos usados pela interface principal agora filtram somente situações curadas:
   - `obter(...)`;
   - `obterPrimeira(...)`;
   - `obterCorrespondente(...)`;
   - `contar(...)`.

3. A interface principal não recorre mais ao fallback de situações não curadas para exibir enunciados.

4. Quando não existe situação curada para a categoria selecionada, a tela mostra uma mensagem localizada informando que nenhuma situação-problema curada está disponível e orienta a validação na aba Curadoria.

5. A troca de idioma não usa tradução não curada. Se não houver versão curada no idioma de destino, o sistema não substitui o enunciado por texto não validado.

6. Foram adicionadas chaves de internacionalização para a ausência de situação curada:
   - português;
   - inglês;
   - francês.

7. O procedimento de regressão passou a testar explicitamente que situações não validadas não aparecem na interface principal.

## Arquivos alterados

- `src/gerard/campoaditivo/servico/RepositorioSituacoesAditivas.java`
- `src/Main.java`
- `src/gerard/i18n/ServicoLocalizacao.java`
- `scripts/verificar_regressao_gerard.py`

## Procedimento de regressão executado

Comando:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- Compilação: `BUILD SUCCESSFUL`.
- Erros críticos: 0.
- Avisos: 1.

Aviso mantido: diferença já conhecida na quantidade de situações-problema por idioma no TSV empacotado.

## Teste novo incluído

O script cria temporariamente um arquivo de curadoria com:

- uma situação em português não validada;
- uma situação em português validada;
- uma tradução em inglês não validada.

O teste confirma que:

- a aba Curadoria continua listando todas as linhas;
- a interface principal só obtém a situação validada;
- a contagem usada pela interface considera apenas situações curadas;
- a troca de idioma não usa tradução não curada.
