.PHONY: all clean update validar-regressao
all: update validar-regressao
	lualatex -interaction=nonstopmode -halt-on-error main.tex
	lualatex -interaction=nonstopmode -halt-on-error main.tex
update:
	python3 scripts/gerar_linha_tempo.py
validar-regressao:
	python3 scripts/validar_regressao_artigo.py
clean:
	rm -f *.aux *.log *.out *.toc *.lof *.lot *.bbl *.blg
