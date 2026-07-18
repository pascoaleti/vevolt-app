import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const PUBLIC = path.join(ROOT, "public");
const errors = [];

async function walk(directory) {
  const entries = await fs.readdir(directory, { withFileTypes: true });
  const nested = await Promise.all(entries.map((entry) => {
    const full = path.join(directory, entry.name);
    return entry.isDirectory() ? walk(full) : [full];
  }));
  return nested.flat();
}

function publicTarget(url) {
  const clean = url.split(/[?#]/, 1)[0];
  if (clean === "/") return path.join(PUBLIC, "index");
  const relative = clean.replace(/^\//, "").replace(/\/$/, "");
  return clean.endsWith("/") ? path.join(PUBLIC, relative, "index") : path.join(PUBLIC, relative);
}

const files = await walk(PUBLIC);
const htmlFiles = [];
for (const file of files) {
  const relative = path.relative(PUBLIC, file).replaceAll("\\", "/");
  if (relative.startsWith("assets/") || /\.(php|txt|xml|json|ico|png|webp|css|js|woff2)$/i.test(relative) || relative === ".htaccess" || relative === "site.webmanifest") continue;
  htmlFiles.push(file);
}

for (const file of htmlFiles) {
  const relative = path.relative(PUBLIC, file).replaceAll("\\", "/");
  const html = await fs.readFile(file, "utf8");
  if (!/^<!doctype html>/i.test(html)) errors.push(`${relative}: missing doctype`);
  if (/Carrega\s*A[iÍ]/i.test(html)) errors.push(`${relative}: obsolete CarregaAI name`);
  if (/Ã.|Â.|â€|ðŸ/.test(html)) errors.push(`${relative}: possible broken UTF-8 text`);
  if (/\son[a-z]+\s*=|<script(?![^>]+(?:src=|type="application\/ld\+json"))/i.test(html)) errors.push(`${relative}: inline executable code`);
  if (relative.includes("blog/") && relative !== "blog/index" && !html.includes('aria-current="page">Blog</a>')) errors.push(`${relative}: Blog is not active`);
  const isLegal = /(^|\/)(termos|politica|terms|privacy|terminos|privacidad)$/.test(relative);
  if (isLegal && !html.includes('content="noindex, nofollow"')) errors.push(`${relative}: legal page is indexable`);
  if (!isLegal && !html.includes('content="index, follow"')) errors.push(`${relative}: indexed page missing robots directive`);

  for (const match of html.matchAll(/(?:href|src)="([^"]+)"/g)) {
    const url = match[1];
    if (!url.startsWith("/") || url.startsWith("//") || url.startsWith("/tester-signup.php")) continue;
    const target = publicTarget(url);
    try {
      await fs.access(target);
    } catch {
      errors.push(`${relative}: missing target ${url}`);
    }
  }
}

if (errors.length) {
  console.error(errors.join("\n"));
  process.exitCode = 1;
} else {
  console.log(`Validated ${htmlFiles.length} HTML pages with no broken internal links or assets.`);
}
