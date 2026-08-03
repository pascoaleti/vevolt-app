import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const PUBLIC = path.join(ROOT, "public");
const errors = [];
const requiredDeletionPages = {
  "exclusao-de-dados": ["VeVolt", "Pascoal Eti", "vevolt@vevolt.app", "90 dias", "30 dias", "2 anos"],
  "en/data-deletion": ["VeVolt", "Pascoal Eti", "vevolt@vevolt.app", "90 days", "30 days", "2 years"],
  "es/eliminacion-de-datos": ["VeVolt", "Pascoal Eti", "vevolt@vevolt.app", "90 días", "30 días", "2 años"],
};
const requiredPlanPages = {
  planos: {
    trial: "15 dias grátis no plano mensal. Depois, o preço local exibido pelo Google Play. Cancele quando quiser.",
    annual: "O plano anual continua disponível, sem um segundo período grátis.",
    separate: "Premium e Condo são assinaturas separadas",
  },
  "en/plans": {
    trial: "15-day free trial on the monthly plan. Then the local price shown by Google Play. Cancel anytime.",
    annual: "The annual plan remains available without a second free trial.",
    separate: "Premium and Condo are separate subscriptions",
  },
  "es/planes": {
    trial: "15 días gratis en el plan mensual. Después, el precio local mostrado por Google Play. Cancela cuando quieras.",
    annual: "El plan anual sigue disponible, sin una segunda prueba gratuita.",
    separate: "Premium y Condo son suscripciones separadas",
  },
};
const requiredFaqPages = {
  faq: ["novos clientes elegíveis", "forma de pagamento", "renova automaticamente", "Play Store", "174 países e regiões", "177 países e regiões"],
  "en/faq": ["eligible new customers", "payment method", "renews automatically", "Play Store", "174 commercial countries and regions", "177 countries and regions"],
  "es/preguntas": ["nuevos clientes elegibles", "método de pago", "se renueva automáticamente", "Play Store", "174 países y regiones", "177 países y regiones"],
};

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
  if (/teste fechado|closed test|prueba cerrada|seja testador|become a tester|ser probador/i.test(html)) errors.push(`${relative}: obsolete closed-test wording`);
  if (/lançamento em análise|launch under review|lanzamiento en revisión|não está publicamente disponível|not publicly available|no estará disponible públicamente/i.test(html)) errors.push(`${relative}: obsolete Google Play review wording`);
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

for (const staleRoute of ["seja-testador", "en/testers", "es/probadores"]) {
  try {
    await fs.access(path.join(PUBLIC, ...staleRoute.split("/")));
    errors.push(`${staleRoute}: obsolete launch page still exists`);
  } catch {
    // Expected: the obsolete launch page was removed.
  }
}

for (const staleFile of ["tester-signup.php", "tester-config.example.php"]) {
  try {
    await fs.access(path.join(PUBLIC, staleFile));
    errors.push(`${staleFile}: obsolete tester endpoint still exists`);
  } catch {
    // Expected: obsolete tester endpoints were removed.
  }
}

for (const [relative, required] of Object.entries(requiredPlanPages)) {
  const html = await fs.readFile(path.join(PUBLIC, ...relative.split("/")), "utf8");
  const trialOccurrences = html.split(required.trial).length - 1;
  if (trialOccurrences < 2) errors.push(`${relative}: trial offer must appear for both Premium and Condo`);
  if (!html.includes(required.annual)) errors.push(`${relative}: annual-plan trial clarification is missing`);
  if (!html.includes(required.separate)) errors.push(`${relative}: separate subscriptions clarification is missing`);
  const playLinks = html.match(/href="https:\/\/play\.google\.com\/store\/apps\/details\?id=br\.com\.vevolt"/g) ?? [];
  if (playLinks.length < 2) errors.push(`${relative}: Google Play CTA is not present near both plans`);
}

for (const [relative, requiredTexts] of Object.entries(requiredFaqPages)) {
  const html = await fs.readFile(path.join(PUBLIC, ...relative.split("/")), "utf8");
  for (const requiredText of requiredTexts) {
    if (!html.includes(requiredText)) errors.push(`${relative}: missing FAQ text "${requiredText}"`);
  }
}

for (const [relative, requiredTexts] of Object.entries(requiredDeletionPages)) {
  const file = path.join(PUBLIC, ...relative.split("/"));
  let html;
  try {
    html = await fs.readFile(file, "utf8");
  } catch {
    errors.push(`${relative}: required Google Play data-deletion page is missing`);
    continue;
  }
  for (const requiredText of requiredTexts) {
    if (!html.includes(requiredText)) errors.push(`${relative}: missing required text "${requiredText}"`);
  }
}

for (const localeKey of ["pt", "en", "es"]) {
  try {
    await fs.access(path.join(PUBLIC, "assets", `google-play-badge-${localeKey}.png`));
  } catch {
    errors.push(`assets/google-play-badge-${localeKey}.png: localized Google Play badge is missing`);
  }
}

if (errors.length) {
  console.error(errors.join("\n"));
  process.exitCode = 1;
} else {
  console.log(`Validated ${htmlFiles.length} HTML pages with no broken internal links or assets.`);
}
