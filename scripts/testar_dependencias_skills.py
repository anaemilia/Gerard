#!/usr/bin/env python3
"""Testes do registro e do percurso das skills do Gerard."""

from __future__ import annotations

import copy
import sys
import unittest
from pathlib import Path


RAIZ_PROJETO = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(RAIZ_PROJETO / "scripts"))

import verificar_dependencias_skills as grafo  # noqa: E402


class TesteDependenciasSkills(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.registro = grafo.carregar(grafo.REGISTRO_PADRAO)

    def test_registro_real_e_valido(self) -> None:
        self.assertEqual([], grafo.validar(self.registro))

    def test_percurso_de_handler_descobre_fontes_protetoras(self) -> None:
        _, pais, _, condicionais = grafo.percorrer(
            self.registro, ["gerard-handlers-de-interacao"], False
        )
        self.assertIn("gerard-semantic-model", pais)
        self.assertIn("gerard-domain-model-first", pais)
        self.assertIn("gerard-consistencia-estado", pais)
        self.assertIn("gerard-knowledge-locality-principle", pais)
        self.assertIn("gerard-posicionamento-relativo", pais)
        self.assertNotIn("gerard-scaffolding-interacao", pais)
        self.assertIn(
            (
                "gerard-handlers-de-interacao",
                "related",
                "gerard-scaffolding-interacao",
            ),
            condicionais,
        )

    def test_auditoria_exploratoria_expande_relacionadas(self) -> None:
        _, pais, _, condicionais = grafo.percorrer(
            self.registro,
            ["gerard-handlers-de-interacao"],
            True,
        )
        self.assertIn("gerard-scaffolding-interacao", pais)
        self.assertEqual([], condicionais)

    def test_ordem_coloca_dependencias_antes_do_handler(self) -> None:
        _, pais, _, _ = grafo.percorrer(
            self.registro, ["gerard-handlers-de-interacao"], False
        )
        ordem = grafo.ordem_leitura(self.registro, set(pais))
        self.assertLess(
            ordem.index("gerard-semantic-model"),
            ordem.index("gerard-domain-model-first"),
        )
        self.assertLess(
            ordem.index("gerard-domain-model-first"),
            ordem.index("gerard-handlers-de-interacao"),
        )
        self.assertLess(
            ordem.index("gerard-posicionamento-relativo"),
            ordem.index("gerard-handlers-de-interacao"),
        )

    def test_ciclo_requires_e_rejeitado(self) -> None:
        registro = copy.deepcopy(self.registro)
        registro["edges"].append(
            {
                "from": "gerard-semantic-model",
                "relation": "requires",
                "to": "gerard-domain-model-first",
                "reason": "ciclo artificial para o teste",
            }
        )
        erros = grafo.validar(registro)
        self.assertTrue(any("ciclo em requires/constrains" in erro for erro in erros))


if __name__ == "__main__":
    unittest.main(verbosity=2)
