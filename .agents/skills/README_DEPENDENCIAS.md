# Grafo de dependencias das skills do Gerard

O arquivo `dependencies.json` e a fonte executavel das relacoes entre as
skills. Ele nao substitui nenhum `SKILL.md` e nao pode receber regras de
dominio, representacao, interacao, pedagogia, registro ou analise.

## Modelo

- `requires`: a fonte de destino precisa ser lida antes da origem.
- `constrains`: a fonte de destino protege ou limita a decisao da origem.
- `related`: a consulta depende do escopo concreto da tarefa.
- `conflicts`: existe uma tensao que precisa de reconciliacao explicita.

A direcao de uma aresta e sempre `origem -> fonte a consultar`.

O grafo global registra dependencias compartilhadas. A arvore de percurso
e gerada para uma tarefa concreta, com uma ou mais skills como sementes. A
busca em largura descobre as fontes afetadas; a ordem de leitura coloca
`requires` e `constrains` antes das fontes dependentes.

## Comandos

Validar cobertura, caminhos, relacoes, duplicacoes e ciclos:

```powershell
python scripts\verificar_dependencias_skills.py --validar
```

Gerar uma arvore para uma tarefa de extracao de handler:

```powershell
python scripts\verificar_dependencias_skills.py --percorrer gerard-handlers-de-interacao
```

O percurso normal segue `requires` e `constrains`. As relações `related` e
`conflicts` aparecem como candidatas condicionais, para que o escopo concreto
decida se devem ser incluídas. Para uma auditoria exploratória que expanda
também essas relações:

```powershell
python scripts\verificar_dependencias_skills.py --percorrer gerard-handlers-de-interacao --incluir-relacionadas
```

Gerar o grafo global em Mermaid:

```powershell
python scripts\verificar_dependencias_skills.py --mermaid
```

Executar os testes do mecanismo:

```powershell
python scripts\testar_dependencias_skills.py
```

## Regra de manutencao

Toda pasta imediata de `.agents/skills` que contenha `SKILL.md` precisa ter
exatamente um no `kind=skill`. Adicionar uma aresta nao transfere a autoria
do conhecimento: a justificativa descreve somente por que a consulta e
necessaria; a regra completa continua no arquivo de destino.
