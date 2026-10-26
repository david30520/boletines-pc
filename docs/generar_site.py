#!/usr/bin/env python3
"""Genera la web de la documentación a partir de los .md de docs/.

Por cada .md de docs/ crea un .html equivalente en docs/site/, copia las
imágenes y genera un index.html que enlaza a todas las páginas.

Uso (desde la raíz del repositorio):
    pip install -r docs/requirements.txt
    python docs/generar_site.py
"""

import html
import re
import shutil
from pathlib import Path

from markdown_it import MarkdownIt

DOCS = Path(__file__).resolve().parent
SITE = DOCS / "site"
EXTENSIONES_IMAGEN = {".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp"}

# CommonMark (como GitHub) con tablas activadas
md = MarkdownIt("commonmark").enable("table")

PLANTILLA = """<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{titulo}</title>
<style>{css}</style>
</head>
<body>
<main>
{navegacion}
{contenido}
</main>
</body>
</html>
"""

CSS = """
:root { color-scheme: light dark; }
body { margin: 0; font-family: system-ui, sans-serif; line-height: 1.6; }
main { max-width: 900px; margin: 0 auto; padding: 1.5rem 1rem 3rem; }
a { color: #0969da; }
img { max-width: 100%; height: auto; border: 1px solid #8884; }
table { border-collapse: collapse; display: block; overflow-x: auto; }
th, td { border: 1px solid #8886; padding: 0.4rem 0.6rem; text-align: left; }
code { background: #8882; padding: 0.1rem 0.3rem; border-radius: 4px; }
pre { background: #8882; padding: 0.8rem; overflow-x: auto; border-radius: 6px; }
pre code { background: none; padding: 0; }
.volver { font-size: 0.9rem; }
"""


def titulo_de(texto: str, por_defecto: str) -> str:
    """Devuelve el primer encabezado de nivel 1 del md o, si no hay, el nombre del archivo."""
    m = re.search(r"^#\s+(.+?)\s*$", texto, re.MULTILINE)
    return m.group(1) if m else por_defecto


def orden_natural(ruta: Path):
    """Ordena boletin2 antes que boletin10."""
    return [int(t) if t.isdigit() else t.lower() for t in re.split(r"(\d+)", ruta.stem)]


def pagina(titulo: str, contenido: str, navegacion: str = "") -> str:
    return PLANTILLA.format(
        titulo=html.escape(titulo), css=CSS, navegacion=navegacion, contenido=contenido
    )


def main() -> None:
    if SITE.exists():
        shutil.rmtree(SITE)
    SITE.mkdir()

    paginas = []
    for fichero in sorted(DOCS.glob("*.md"), key=orden_natural):
        texto = fichero.read_text(encoding="utf-8")
        titulo = titulo_de(texto, fichero.stem)
        volver = '<p class="volver"><a href="index.html">← Volver al índice</a></p>'
        (SITE / f"{fichero.stem}.html").write_text(
            pagina(titulo, md.render(texto), volver), encoding="utf-8"
        )
        paginas.append((fichero.stem, titulo))

    for fichero in DOCS.iterdir():
        if fichero.is_file() and fichero.suffix.lower() in EXTENSIONES_IMAGEN:
            shutil.copy2(fichero, SITE / fichero.name)

    enlaces = "\n".join(
        f'<li><a href="{html.escape(nombre)}.html">{html.escape(titulo)}</a> '
        f"<small>({html.escape(nombre)}.md)</small></li>"
        for nombre, titulo in paginas
    )
    indice = f"<h1>Documentación de la API de tareas</h1>\n<ul>\n{enlaces}\n</ul>"
    (SITE / "index.html").write_text(pagina("Documentación", indice), encoding="utf-8")

    print(f"Generadas {len(paginas)} páginas e index.html en {SITE}")


if __name__ == "__main__":
    main()
