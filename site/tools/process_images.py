from pathlib import Path
import shutil
from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "src" / "graphics"
OUTPUT = ROOT / "public" / "assets"

LOCALES = {
    "pt-BR": "pt",
    "en-US": "en",
    "es-419": "es",
}


def save_webp(source: Path, destination: Path, width: int, quality: int = 84) -> None:
    destination.parent.mkdir(parents=True, exist_ok=True)
    with Image.open(source) as image:
        image = image.convert("RGB")
        if image.width > width:
            height = round(image.height * width / image.width)
            image = image.resize((width, height), Image.Resampling.LANCZOS)
        image.save(destination, "WEBP", quality=quality, method=6)


def save_png(source: Path, destination: Path, size: int) -> None:
    destination.parent.mkdir(parents=True, exist_ok=True)
    with Image.open(source) as image:
        image = image.convert("RGBA").resize((size, size), Image.Resampling.LANCZOS)
        image.save(destination, "PNG", optimize=True)


def main() -> None:
    icon = SOURCE / "common" / "icon-512.png"
    save_png(icon, OUTPUT / "icon-512.png", 512)
    save_png(icon, OUTPUT / "icon-192.png", 192)
    save_png(icon, OUTPUT / "apple-touch-icon.png", 180)
    save_png(icon, OUTPUT / "favicon-32.png", 32)
    save_webp(icon, OUTPUT / "vevolt-icon-64.webp", 64, 90)
    save_webp(icon, OUTPUT / "icon-96.webp", 96, 90)

    with Image.open(icon) as image:
        image.convert("RGBA").resize((48, 48), Image.Resampling.LANCZOS).save(
            ROOT / "public" / "favicon.ico",
            format="ICO",
            sizes=[(16, 16), (32, 32), (48, 48)],
        )

    for source_locale, output_locale in LOCALES.items():
        capture_dir = SOURCE / "source-captures" / source_locale
        for source in capture_dir.glob("*.png"):
            stem = source.stem.split("-", 1)[-1]
            shutil.copy2(
                source,
                OUTPUT / f"app-{output_locale}-{stem}.png",
            )
            for width in (360, 720):
                save_webp(
                    source,
                    OUTPUT / "app" / f"{output_locale}-{stem}-{width}.webp",
                    width,
                    84,
                )
                save_webp(
                    source,
                    OUTPUT / f"app-{output_locale}-{stem}-{width}.webp",
                    width,
                    84,
                )

        phone_dir = SOURCE / "playstore" / source_locale / "phone"
        for source in phone_dir.glob("*.png"):
            stem = source.stem.replace("01-", "").replace("02-", "").replace(
                "03-", ""
            ).replace("04-", "").replace("05-", "").replace(
                "06-", ""
            ).replace("07-", "").replace("09-", "")
            save_webp(
                source,
                OUTPUT / "screens" / f"{output_locale}-{stem}-360.webp",
                360,
                82,
            )
            save_webp(
                source,
                OUTPUT / "screens" / f"{output_locale}-{stem}-720.webp",
                720,
                84,
            )

        website_dir = SOURCE / "website" / source_locale
        for source in website_dir.glob("*.png"):
            stem = source.stem.replace("-1920x1080", "").replace(
                "-1600x900", ""
            ).replace("-1200x630", "")
            with Image.open(source) as image:
                source_width = image.width
            widths = (960, 1600) if source_width >= 1600 else (960, 1200)
            for width in widths:
                save_webp(
                    source,
                    OUTPUT / "visuals" / f"{output_locale}-{stem}-{width}.webp",
                    width,
                    83,
                )


if __name__ == "__main__":
    main()
