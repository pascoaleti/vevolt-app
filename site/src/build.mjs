import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { locales, PLAY_URL } from "./content.mjs";
import { articlesByLocale, localizedSourceLabel } from "./articles-i18n.mjs";

const ROOT = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const SRC = path.join(ROOT, "src");
const PUBLIC = path.join(ROOT, "public");
const BASE = "https://vevolt.app";
const BUILD = "20260718-2";
const UPDATED = "2026-07-18";
const GOOGLE_VERIFICATION = "qdQXCp0VqmTkkN-7x-cuv-L2otJAZixto1u4CroGrIc";

const pageKeys = ["home", "features", "plans", "condo", "community", "faq", "testers"];
const navKeys = ["home", "features", "plans", "condo", "community", "faq", "blog"];
const iconByFeature = ["location", "community", "route", "economy", "market"];
const legalLabels = {
  pt: { privacy: "Privacidade", terms: "Termos de uso" },
  en: { privacy: "Privacy", terms: "Terms of use" },
  es: { privacy: "Privacidad", terms: "Términos de uso" },
};

function esc(value = "") {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function json(value) {
  return JSON.stringify(value).replaceAll("<", "\\u003c");
}

function absolute(route) {
  return `${BASE}${route === "/" ? "/" : route}`;
}

function imageUrl(relative) {
  return `${BASE}/assets/${relative}`;
}

function asset(relative) {
  return `/assets/${relative}`;
}

function localizedRoute(localeKey, pageKey) {
  const locale = locales[localeKey];
  return locale.paths[pageKey] || locale.paths.home;
}

function hreflangs(pageKey, localizedPaths = null) {
  return ["pt", "en", "es"]
    .map((key) => `<link rel="alternate" hreflang="${locales[key].htmlLang}" href="${absolute(localizedPaths?.[key] || localizedRoute(key, pageKey))}">`)
    .concat(`<link rel="alternate" hreflang="x-default" href="${absolute(localizedPaths?.pt || localizedRoute("pt", pageKey))}">`)
    .join("\n    ");
}

function softwareSchema(locale, route) {
  return {
    "@context": "https://schema.org",
    "@type": "SoftwareApplication",
    name: "VeVolt",
    applicationCategory: "NavigationApplication",
    operatingSystem: "Android",
    inLanguage: locale.htmlLang,
    description: locale.home.description,
    url: absolute(route),
    downloadUrl: PLAY_URL,
    image: imageUrl("visuals/pt-hero-1920.webp"),
    author: { "@type": "Organization", name: "Pascoal Eti", url: "https://pascoal.eti.br" },
    offers: [
      { "@type": "Offer", price: "0", priceCurrency: localeKeyFromLocale(locale) === "pt" ? "BRL" : "USD", category: "free" },
    ],
  };
}

function localeKeyFromLocale(locale) {
  return Object.entries(locales).find(([, candidate]) => candidate === locale)?.[0] || "pt";
}

function organizationSchema() {
  return {
    "@context": "https://schema.org",
    "@type": "Organization",
    name: "VeVolt",
    url: BASE,
    logo: imageUrl("icon-512.png"),
    sameAs: [PLAY_URL],
  };
}

function breadcrumbsSchema(items) {
  return {
    "@context": "https://schema.org",
    "@type": "BreadcrumbList",
    itemListElement: items.map((item, index) => ({
      "@type": "ListItem",
      position: index + 1,
      name: item.name,
      item: absolute(item.path),
    })),
  };
}

function faqSchema(items, locale) {
  return {
    "@context": "https://schema.org",
    "@type": "FAQPage",
    inLanguage: locale.htmlLang,
    mainEntity: items.map(([question, answer]) => ({
      "@type": "Question",
      name: question,
      acceptedAnswer: { "@type": "Answer", text: answer },
    })),
  };
}

function languageSwitcher(localeKey, pageKey, compact = false, localizedPaths = null) {
  return `<div class="language-switcher${compact ? " compact" : ""}" aria-label="${esc(locales[localeKey].a11y.language)}">
    ${["pt", "en", "es"].map((key) => {
      const current = key === localeKey ? ' aria-current="true"' : "";
      const route = localizedPaths?.[key] || localizedRoute(key, pageKey);
       return `<a href="${route}" hreflang="${locales[key].htmlLang}" lang="${locales[key].htmlLang}" aria-label="${esc(locales[key].label)}"${current}>${compact ? key.toUpperCase() : locales[key].label}</a>`;
    }).join("")}
  </div>`;
}

function nav(localeKey, pageKey, localizedPaths = null) {
  const locale = locales[localeKey];
  const links = navKeys
    .filter((key) => locale.paths[key])
    .map((key) => {
      const current = key === pageKey || (key === "blog" && pageKey === "article") ? ' aria-current="page"' : "";
      return `<a class="nav-link" href="${locale.paths[key]}"${current}>${esc(locale.nav[key])}</a>`;
    })
    .join("");
  const testerCurrent = pageKey === "testers" ? ' aria-current="page"' : "";
  return `<header class="topbar">
    <nav class="nav" aria-label="${esc(locale.a11y.nav)}">
      <a class="brand" href="${locale.paths.home}" aria-label="${esc(locale.a11y.home)}">
        <img src="/assets/vevolt-icon-64.webp" width="64" height="64" alt="" fetchpriority="high">
        <span class="brand-wordmark"><span class="brand-ve">Ve</span><span class="brand-volt">Volt</span></span>
      </a>
      <div class="nav-panel" id="primary-navigation" data-menu-panel data-open="false">
        <div class="nav-links">${links}</div>
        <a class="nav-cta" href="${locale.paths.testers}"${testerCurrent}>${esc(locale.nav.testers)}</a>
        ${languageSwitcher(localeKey, pageKey, true, localizedPaths)}
      </div>
      <div class="nav-controls">
        <button class="theme-toggle" type="button" data-theme-toggle data-label-light="${esc(locale.a11y.light)}" data-label-dark="${esc(locale.a11y.dark)}" aria-label="${esc(locale.a11y.dark)}" title="${esc(locale.a11y.dark)}"><span aria-hidden="true">&#9788;</span></button>
        <button class="menu-toggle" type="button" data-menu-toggle aria-controls="primary-navigation" aria-expanded="false" aria-label="${esc(locale.a11y.menu)}"><span></span><span></span><span></span></button>
      </div>
    </nav>
  </header>`;
}

function footer(localeKey, pageKey, localizedPaths = null) {
  const locale = locales[localeKey];
  const developedBy = localeKey === "en" ? "Developed by" : localeKey === "es" ? "Desarrollado por" : "Desenvolvido por";
  return `<footer class="footer">
    <div class="footer-inner">
      <div>
        <a class="footer-brand" href="${locale.paths.home}"><img src="/assets/vevolt-icon-64.webp" width="64" height="64" alt=""><span class="brand-wordmark"><span class="brand-ve">Ve</span><span class="brand-volt">Volt</span></span></a>
        <p class="footer-summary">${esc(locale.footer.copy)}</p>
        ${languageSwitcher(localeKey, pageKey, false, localizedPaths)}
      </div>
      <div><h2>${esc(locale.footer.product)}</h2><ul>
        <li><a href="${locale.paths.features}">${esc(locale.nav.features)}</a></li><li><a href="${locale.paths.plans}">${esc(locale.nav.plans)}</a></li><li><a href="${locale.paths.condo}">${esc(locale.nav.condo)}</a></li><li><a href="${locale.paths.community}">${esc(locale.nav.community)}</a></li>
      </ul></div>
      <div><h2>${esc(locale.footer.information)}</h2><ul>
        <li><a href="${locale.paths.faq}">${esc(locale.nav.faq)}</a></li>${locale.paths.blog ? `<li><a href="${locale.paths.blog}">${esc(locale.nav.blog)}</a></li>` : ""}<li><a href="${locale.paths.testers}">${esc(locale.nav.testers)}</a></li><li><a href="${PLAY_URL}" rel="noopener">Google Play</a></li>
      </ul></div>
      <div><h2>${esc(locale.footer.legal)}</h2><ul>
        <li><a href="${locale.paths.privacy}">${esc(legalLabels[localeKey].privacy)}</a></li><li><a href="${locale.paths.terms}">${esc(legalLabels[localeKey].terms)}</a></li><li><a href="mailto:devs@pascoal.eti.br">devs@pascoal.eti.br</a></li>
      </ul></div>
    </div>
    <div class="footer-bottom"><span>&copy; 2026 VeVolt</span><span>${developedBy} <a href="https://pascoal.eti.br/" target="_blank" rel="noopener noreferrer">pascoal.eti.br</a></span></div>
  </footer>`;
}

function shell({ localeKey, pageKey, title, description, route, body, schemas = [], image = "visuals/pt-hero-1920.webp", robots = "index, follow", article = null, localizedPaths = null }) {
  const locale = locales[localeKey];
  const canonical = absolute(route);
  const schemaMarkup = schemas.map((schema) => `<script type="application/ld+json">${json(schema)}</script>`).join("\n    ");
  const articleMeta = article ? `
    <meta property="article:published_time" content="${article.published}">
    <meta property="article:modified_time" content="${article.updated}">
    <meta property="article:author" content="Pascoal Eti">` : "";
  return `<!doctype html>
<html lang="${locale.htmlLang}" data-theme="dark">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover">
    <title>${esc(title)}</title>
    <meta name="description" content="${esc(description)}">
    <meta name="robots" content="${robots}">
    <meta name="google-site-verification" content="${GOOGLE_VERIFICATION}">
    <link rel="canonical" href="${canonical}">
${hreflangs(pageKey, localizedPaths) ? `    ${hreflangs(pageKey, localizedPaths)}` : ""}
    <meta property="og:type" content="${article ? "article" : "website"}">
    <meta property="og:site_name" content="VeVolt">
    <meta property="og:locale" content="${locale.locale}">
    <meta property="og:title" content="${esc(title)}">
    <meta property="og:description" content="${esc(description)}">
    <meta property="og:url" content="${canonical}">
    <meta property="og:image" content="${imageUrl(image)}">
    <meta property="og:image:alt" content="VeVolt - ${esc(locale.slogan)}">
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" content="${esc(title)}">
    <meta name="twitter:description" content="${esc(description)}">
    <meta name="twitter:image" content="${imageUrl(image)}">${articleMeta}
    <meta name="theme-color" content="#07111f" media="(prefers-color-scheme: dark)">
    <meta name="theme-color" content="#f4f8fc" media="(prefers-color-scheme: light)">
    <link rel="manifest" href="/site.webmanifest">
    <link rel="icon" href="/favicon.ico" sizes="any">
    <link rel="icon" type="image/png" sizes="192x192" href="/assets/icon-192.png">
    <link rel="apple-touch-icon" href="/assets/icon-192.png">
    <link rel="preload" href="/assets/fonts/sora-latin-variable.woff2" as="font" type="font/woff2" crossorigin>
    <link rel="preload" href="/assets/fonts/inter-latin-variable.woff2" as="font" type="font/woff2" crossorigin>
    <link rel="stylesheet" href="/assets/site.css?v=${BUILD}">
    <link rel="stylesheet" href="/assets/pages.css?v=${BUILD}">
    <script src="/assets/theme-init.js?v=${BUILD}"></script>
    ${schemaMarkup}
</head>
<body>
    <a class="skip-link" href="#conteudo">${esc(locale.a11y.skip)}</a>
    ${nav(localeKey, pageKey, localizedPaths)}
    <main id="conteudo">${body}</main>
    ${footer(localeKey, pageKey, localizedPaths)}
    <script src="/assets/site.js?v=${BUILD}" defer></script>
</body>
</html>`;
}

function pageHero(localeKey, data) {
  return `<section class="page-hero"><div class="wrap page-hero-inner">
    <p class="eyebrow"><span class="signal"></span>${esc(data.eyebrow)}</p><h1>${esc(data.h1)}</h1><p>${esc(data.copy)}</p>
  </div></section>`;
}

function appPhone(localeKey, screen, alt, options = {}) {
  const loading = options.priority ? "eager" : "lazy";
  const priority = options.priority ? ' fetchpriority="high"' : "";
  const compact = options.compact ? " compact" : "";
  const imageHeight = screen === "condo" ? 1920 : 2400;
  return `<picture class="app-phone${compact}">
    <source type="image/webp" srcset="${asset(`app/${localeKey}-${screen}-360.webp`)} 360w, ${asset(`app/${localeKey}-${screen}-720.webp`)} 720w" sizes="(max-width: 600px) 86vw, 320px">
    <img src="${asset(`app-${localeKey}-${screen}.png`)}" width="1080" height="${imageHeight}" alt="${esc(alt)}" loading="${loading}" decoding="async"${priority}>
  </picture>`;
}

function cta(localeKey, heading, copy) {
  const locale = locales[localeKey];
  return `<section class="cta-band"><div class="wrap cta-inner"><div><h2>${esc(heading)}</h2><p>${esc(copy)}</p></div><div class="actions"><a class="button primary" href="${locale.paths.testers}">${esc(locale.common.testApp)}</a><a class="button secondary" href="${PLAY_URL}" rel="noopener">${esc(locale.common.play)}</a></div></div></section>`;
}

function planCard(localeKey, plan) {
  const locale = locales[localeKey];
  return `<article class="plan-card${plan.featured ? " featured" : ""}">
    <p class="plan-label">${esc(plan.label)}</p><h3>${esc(plan.name)}</h3>
    <p class="plan-price">${esc(plan.price)} <small>${esc(plan.note)}</small></p>
    <ul class="check-list">${plan.features.map((item) => `<li>${esc(item)}</li>`).join("")}</ul>
    <a class="button ${plan.featured ? "primary" : "secondary"}" href="${locale.paths.testers}">${esc(locale.common.testApp)}</a>
  </article>`;
}

async function renderHome(localeKey) {
  const locale = locales[localeKey];
  const source = await fs.readFile(path.join(SRC, "base", `index-${localeKey}.html`), "utf8");
  const main = source.match(/<main id="(?:conteudo|content|contenido)">([\s\S]*?)<\/main>/);
  if (!main) throw new Error(`Conteudo principal ausente em index-${localeKey}.html`);

  const replacements = {
    pt: [
      ["Scanner de QR Code do VeVolt", "Painel VeVolt Condo para gestão de carregadores privados"],
      ["Scanner QR Code no dispositivo.", "Gestão de carregadores, unidades e acesso no condomínio."],
      ["Cadastro de veículo no VeVolt", "Comunidade VeVolt com relatos de motoristas"],
      ["Cadastro completo do veículo.", "Relatos locais e informações compartilhadas pela comunidade."],
    ],
    en: [
      ["VeVolt QR code scanner", "VeVolt Condo private charger management"],
      ["On-device QR code scanner.", "Private charger and condominium management."],
      ["Vehicle setup in VeVolt", "VeVolt driver community"],
      ["Complete vehicle setup.", "Local reports shared by the VeVolt community."],
    ],
    es: [
      ["Escáner de código QR de VeVolt", "Gestión de cargadores privados con VeVolt Condo"],
      ["Escáner de código QR en el dispositivo.", "Gestión de cargadores, unidades y acceso del condominio."],
      ["Registro de vehículo en VeVolt", "Comunidad de conductores VeVolt"],
      ["Registro completo del vehículo.", "Reportes locales compartidos por la comunidad VeVolt."],
    ],
  };

  let body = main[1]
    .replaceAll(`app-${localeKey}-scan`, `app-${localeKey}-condo`)
    .replaceAll(`app-${localeKey}-vehicle`, `app-${localeKey}-community`);
  for (const [from, to] of replacements[localeKey]) body = body.replaceAll(from, to);

  return shell({
    localeKey,
    pageKey: "home",
    title: locale.home.title,
    description: locale.home.description,
    route: locale.paths.home,
    body,
    schemas: [organizationSchema(), softwareSchema(locale, locale.paths.home)],
  });
}

function renderFeatures(localeKey) {
  const locale = locales[localeKey];
  const splits = locale.features.map((feature, index) => `<div class="feature-split${index % 2 ? " reverse" : ""}">
    <div class="feature-copy"><p class="kicker">0${index + 1}</p><h2>${esc(feature.title)}</h2><p>${esc(feature.body)}</p><a class="text-link" href="${locale.paths.testers}">${esc(locale.common.testApp)} &rarr;</a></div>
    ${appPhone(localeKey, feature.screen, feature.title, { priority: index === 0 })}
  </div>`).join("");
  const gallery = locale.screens.map(([screen, title, caption]) => `<figure>${appPhone(localeKey, screen, title, { compact: true })}<figcaption><strong>${esc(title)}</strong>${esc(caption)}</figcaption></figure>`).join("");
  const body = `${pageHero(localeKey, locale.featuresPage)}
  <section class="section"><div class="wrap">${splits}</div></section>
  <section class="section accent-band"><div class="wrap"><div class="section-heading"><div><p class="kicker">VeVolt</p><h2>${esc(locale.featuresPage.galleryTitle)}</h2></div><p>${esc(locale.featuresPage.galleryCopy)}</p></div><div class="screen-gallery">${gallery}</div></div></section>
  ${cta(localeKey, locale.home.h1, locale.home.copy)}`;
  return shell({ localeKey, pageKey: "features", title: locale.featuresPage.title, description: locale.featuresPage.description, route: locale.paths.features, body, schemas: [softwareSchema(locale, locale.paths.features), breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: locale.nav.features, path: locale.paths.features }])] });
}

function renderPlans(localeKey) {
  const locale = locales[localeKey];
  const comparison = locale.plans.map((plan) => `<article class="comparison-plan"><p class="plan-label">${esc(plan.label)}</p><h3>${esc(plan.name)}</h3><ul class="check-list">${plan.features.map((feature) => `<li>${esc(feature)}</li>`).join("")}</ul></article>`).join("");
  const body = `${pageHero(localeKey, locale.plansPage)}
  <section class="section"><div class="wrap">
    <div class="plans-showcase"><div class="plans-showcase-copy"><p class="kicker">VeVolt Premium</p><h2>${esc(locale.plansPage.compareTitle)}</h2><p>${esc(locale.plansPage.billing)}</p></div>${appPhone(localeKey, "profile", locale.plansPage.compareTitle, { priority: true })}</div>
    <div class="plan-grid">${locale.plans.map((plan) => planCard(localeKey, plan)).join("")}</div>
    <p class="notice"><strong>Google Play:</strong> ${esc(locale.plansPage.billing)}</p>
  </div></section>
  <section class="section accent-band"><div class="wrap"><div class="section-heading"><div><p class="kicker">VeVolt</p><h2>${esc(locale.plansPage.compareTitle)}</h2></div></div><div class="comparison-grid">${comparison}</div></div></section>
  ${cta(localeKey, locale.testerPage.formTitle, locale.testerPage.copy)}`;
  return shell({ localeKey, pageKey: "plans", title: locale.plansPage.title, description: locale.plansPage.description, route: locale.paths.plans, body, schemas: [softwareSchema(locale, locale.paths.plans), breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: locale.nav.plans, path: locale.paths.plans }])] });
}

function renderCondo(localeKey) {
  const locale = locales[localeKey];
  const plan = locale.plans.find((item) => item.key === "condo");
  const transparency = localeKey === "pt" ? "Transparência" : localeKey === "es" ? "Transparencia" : "Transparency";
  const body = `${pageHero(localeKey, locale.condoPage)}
  <section class="section"><div class="wrap"><div class="feature-split"><div class="feature-copy"><p class="kicker">VeVolt Condo</p><h2>${esc(locale.condoPage.adminTitle)}</h2><p>${esc(locale.condoPage.adminCopy)}</p><ul class="check-list">${plan.features.map((item) => `<li>${esc(item)}</li>`).join("")}</ul></div>${appPhone(localeKey, "condo", "VeVolt Condo", { priority: true })}</div><p class="notice"><strong>${transparency}:</strong> ${esc(locale.condoPage.disclaimer)}</p></div></section>
  ${cta(localeKey, locale.condoPage.h1, locale.condoPage.copy)}`;
  return shell({ localeKey, pageKey: "condo", title: locale.condoPage.title, description: locale.condoPage.description, route: locale.paths.condo, body, schemas: [softwareSchema(locale, locale.paths.condo), breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: locale.nav.condo, path: locale.paths.condo }])] });
}

function renderCommunity(localeKey) {
  const locale = locales[localeKey];
  const transparency = localeKey === "pt" ? "Transparência" : localeKey === "es" ? "Transparencia" : "Transparency";
  const body = `${pageHero(localeKey, locale.communityPage)}
  <section class="section"><div class="wrap"><div class="feature-split reverse">${appPhone(localeKey, "community", locale.nav.community, { priority: true })}<div class="feature-copy"><p class="kicker">${esc(locale.nav.community)}</p><h2>${esc(locale.communityPage.trustTitle)}</h2><p>${esc(locale.communityPage.trustCopy)}</p><p class="notice"><strong>${transparency}:</strong> ${esc(locale.communityPage.transparency)}</p></div></div></div></section>
  ${cta(localeKey, locale.communityPage.h1, locale.communityPage.copy)}`;
  return shell({ localeKey, pageKey: "community", title: locale.communityPage.title, description: locale.communityPage.description, route: locale.paths.community, body, schemas: [softwareSchema(locale, locale.paths.community), breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: locale.nav.community, path: locale.paths.community }])] });
}

function renderFaq(localeKey) {
  const locale = locales[localeKey];
  const items = locale.faq.map(([question, answer]) => `<details class="faq-item"><summary>${esc(question)}</summary><p>${esc(answer)}</p></details>`).join("");
  const body = `${pageHero(localeKey, locale.faqPage)}<section class="section"><div class="wrap faq-list">${items}</div></section>${cta(localeKey, locale.testerPage.formTitle, locale.testerPage.copy)}`;
  return shell({ localeKey, pageKey: "faq", title: locale.faqPage.title, description: locale.faqPage.description, route: locale.paths.faq, body, schemas: [faqSchema(locale.faq, locale), breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: locale.nav.faq, path: locale.paths.faq }])] });
}

function renderTester(localeKey) {
  const locale = locales[localeKey];
  const body = `${pageHero(localeKey, locale.testerPage)}
  <section class="section"><div class="wrap tester-panel"><div><p class="kicker">Google Play</p><h2>${esc(locale.testerPage.stepsTitle)}</h2><ol class="plain-list">${locale.testerPage.steps.map((step) => `<li>${esc(step)}</li>`).join("")}</ol><a class="text-link" href="${PLAY_URL}" rel="noopener">${esc(locale.common.play)} &rarr;</a></div>
  <form class="tester-form" action="/tester-signup.php" method="post" data-tester-form><h2>${esc(locale.testerPage.formTitle)}</h2><div class="field"><label for="name-${localeKey}">${esc(locale.testerPage.name)}</label><input id="name-${localeKey}" name="name" autocomplete="name" required maxlength="120"></div><div class="field"><label for="email-${localeKey}">${esc(locale.testerPage.email)}</label><input id="email-${localeKey}" name="email" type="email" autocomplete="email" required maxlength="190"></div><label><input type="checkbox" name="consent" value="1" required> ${esc(locale.testerPage.consent)}</label><input type="hidden" name="language" value="${esc(locale.htmlLang)}"><input type="text" name="website" tabindex="-1" autocomplete="off" aria-hidden="true" class="honeypot"><button class="button primary" type="submit">${esc(locale.testerPage.submit)}</button><p class="form-status" data-form-status aria-live="polite"></p></form></div></section>`;
  return shell({ localeKey, pageKey: "testers", title: locale.testerPage.title, description: locale.testerPage.description, route: locale.paths.testers, body, schemas: [softwareSchema(locale, locale.paths.testers), breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: locale.nav.testers, path: locale.paths.testers }])] });
}

function renderBlog(localeKey) {
  const locale = locales[localeKey];
  const articles = articlesByLocale[localeKey];
  const cards = articles.map((article) => `<article class="article-card"><img src="${asset(article.image)}" width="720" height="405" alt="${esc(article.imageAlt)}" loading="lazy"><div class="article-card-body"><p class="article-meta">${esc(locale.common.updated)} 18/07/2026 · ${article.readTime} ${esc(locale.common.minutes)}</p><h2>${esc(article.title)}</h2><p>${esc(article.description)}</p><a class="text-link" href="${locale.paths.blog}${article.slug}">${esc(locale.common.readArticle)} →</a></div></article>`).join("");
  const body = `${pageHero(localeKey, locale.blogPage)}<section class="section"><div class="wrap blog-grid">${cards}</div></section>${cta(localeKey, locale.home.h1, locale.home.copy)}`;
  const schema = {
    "@context": "https://schema.org", "@type": "Blog", name: "Blog VeVolt", description: locale.blogPage.description, url: absolute(locale.paths.blog), inLanguage: locale.htmlLang,
    blogPost: articles.map((article) => ({ "@type": "BlogPosting", headline: article.title, url: absolute(`${locale.paths.blog}${article.slug}`) })),
  };
  return shell({ localeKey, pageKey: "blog", title: locale.blogPage.title, description: locale.blogPage.description, route: locale.paths.blog, body, schemas: [schema, breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: "Blog", path: locale.paths.blog }])], image: articles[0].image });
}

function articleSection(section) {
  const paragraphs = (section.paragraphs || []).map((item) => `<p>${esc(item)}</p>`).join("");
  const bullets = section.bullets ? `<ul>${section.bullets.map((item) => `<li>${esc(item)}</li>`).join("")}</ul>` : "";
  return `<section><h2>${esc(section.heading)}</h2>${paragraphs}${bullets}</section>`;
}

function renderArticle(localeKey, article, index) {
  const locale = locales[localeKey];
  const articles = articlesByLocale[localeKey];
  const route = `${locale.paths.blog}${article.slug}`;
  const localizedPaths = Object.fromEntries(["pt", "en", "es"].map((key) => [key, `${locales[key].paths.blog}${articlesByLocale[key][index].slug}`]));
  const related = [articles[(index + 1) % articles.length], articles[(index + 4) % articles.length]];
  const faq = article.faqs.map(([question, answer]) => `<details class="faq-item"><summary>${esc(question)}</summary><p>${esc(answer)}</p></details>`).join("");
  const sources = article.sources.map((source) => `<li><a href="${esc(source.url)}" rel="noopener noreferrer">${esc(localizedSourceLabel(localeKey, source))}</a></li>`).join("");
  const labels = {
    pt: { breadcrumb: "Navegação estrutural", guide: "Guia VeVolt", faq: "Perguntas frequentes", aside: "Use o VeVolt na sua rotina", asideCopy: "Encontre pontos, confira relatos, planeje alternativas e registre sua experiência em um só app.", continue: "Continue aprendendo" },
    en: { breadcrumb: "Breadcrumb", guide: "VeVolt guide", faq: "Frequently asked questions", aside: "Use VeVolt in your routine", asideCopy: "Find chargers, check reports, plan alternatives and record your experience in one app.", continue: "Keep learning" },
    es: { breadcrumb: "Ruta de navegación", guide: "Guía VeVolt", faq: "Preguntas frecuentes", aside: "Usa VeVolt en tu rutina", asideCopy: "Encuentra cargadores, consulta reportes, planifica alternativas y registra tu experiencia en una sola app.", continue: "Sigue aprendiendo" },
  }[localeKey];
  const body = `<article class="section"><div class="wrap">
    <nav class="breadcrumbs" aria-label="${esc(labels.breadcrumb)}"><a href="${locale.paths.home}">VeVolt</a><span>/</span><a href="${locale.paths.blog}">Blog</a></nav>
    <div class="article-intro"><div class="article-hero"><img src="${asset(article.image)}" width="1280" height="720" alt="${esc(article.imageAlt)}" fetchpriority="high"></div><header class="article-header"><p class="eyebrow"><span class="signal"></span>${esc(labels.guide)}</p><h1>${esc(article.title)}</h1><p class="lede">${esc(article.lead)}</p><p class="article-meta">${esc(locale.common.updated)} 18/07/2026 · ${article.readTime} ${esc(locale.common.minutes)}</p></header></div>
    <div class="article-layout"><div class="article-body">${article.sections.map(articleSection).join("")}<section><h2>${esc(labels.faq)}</h2><div class="faq-list">${faq}</div></section></div>
    <aside class="article-aside"><h2>${esc(labels.aside)}</h2><p>${esc(labels.asideCopy)}</p><a class="button primary" href="${locale.paths.testers}">${esc(locale.common.testApp)}</a><h2>${esc(locale.common.sources)}</h2><ul class="source-list">${sources}</ul></aside></div>
    <section class="section compact"><div class="section-heading"><div><p class="kicker">${esc(locale.common.related)}</p><h2>${esc(labels.continue)}</h2></div></div><div class="grid two">${related.map((item) => `<article class="article-card"><div class="article-card-body"><h3>${esc(item.title)}</h3><p>${esc(item.description)}</p><a class="text-link" href="${locale.paths.blog}${item.slug}">${esc(locale.common.readArticle)} →</a></div></article>`).join("")}</div></section>
  </div></article>`;
  const blogPosting = {
    "@context": "https://schema.org", "@type": "BlogPosting", headline: article.title, description: article.description, image: imageUrl(article.image),
    datePublished: UPDATED, dateModified: UPDATED, inLanguage: locale.htmlLang, mainEntityOfPage: absolute(route),
    author: { "@type": "Organization", name: "Pascoal Eti", url: "https://pascoal.eti.br" },
    publisher: { "@type": "Organization", name: "VeVolt", logo: { "@type": "ImageObject", url: imageUrl("icon-512.png") } },
  };
  return shell({ localeKey, pageKey: "article", title: `${article.title} | VeVolt`, description: article.description, route, body, schemas: [blogPosting, faqSchema(article.faqs, locale), breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: "Blog", path: locale.paths.blog }, { name: article.title, path: route }])], image: article.image, article: { published: UPDATED, updated: UPDATED }, localizedPaths });
}

async function renderLegal(localeKey, pageKey, sourceName, title, description) {
  const locale = locales[localeKey];
  const source = await fs.readFile(path.join(SRC, "legal", sourceName), "utf8");
  const articleMatch = source.match(/<article class="legal-content">([\s\S]*?)<\/article>/i);
  if (!articleMatch) throw new Error(`Legal content not found in ${sourceName}`);
  const headingMatch = source.match(/<main[\s\S]*?<h1>([\s\S]*?)<\/h1>/i);
  const leadMatch = source.match(/<main[\s\S]*?<p class="lead">([\s\S]*?)<\/p>/i);
  const heading = headingMatch ? headingMatch[1].replace(/<[^>]+>/g, "").trim() : title;
  const lead = leadMatch ? leadMatch[1].replace(/<[^>]+>/g, "").replace(/\s+/g, " ").trim() : description;
  const body = `<section class="page-hero"><div class="wrap"><p class="eyebrow"><span class="signal"></span>${esc(legalLabels[localeKey][pageKey])}</p><h1>${esc(heading)}</h1><p>${esc(lead)}</p></div></section><section class="section"><div class="wrap legal">${articleMatch[1]}</div></section>`;
  return shell({ localeKey, pageKey, title, description, route: locale.paths[pageKey], body, schemas: [breadcrumbsSchema([{ name: "VeVolt", path: locale.paths.home }, { name: legalLabels[localeKey][pageKey], path: locale.paths[pageKey] }])], robots: "noindex, nofollow", image: "visuals/pt-hero-1920.webp" });
}

async function writeRoute(file, content) {
  const destination = path.join(PUBLIC, file);
  await fs.mkdir(path.dirname(destination), { recursive: true });
  await fs.writeFile(destination, content, "utf8");
}

async function copyAssets() {
  await fs.mkdir(path.join(PUBLIC, "assets"), { recursive: true });
  await fs.cp(path.join(SRC, "base", "localized-assets"), path.join(PUBLIC, "assets"), { recursive: true });
  await Promise.all([
    fs.copyFile(path.join(SRC, "site.css"), path.join(PUBLIC, "assets", "site.css")),
    fs.copyFile(path.join(SRC, "pages.css"), path.join(PUBLIC, "assets", "pages.css")),
    fs.copyFile(path.join(SRC, "site.js"), path.join(PUBLIC, "assets", "site.js")),
    fs.copyFile(path.join(SRC, "theme-init.js"), path.join(PUBLIC, "assets", "theme-init.js")),
  ]);
}

function sitemap(routes) {
  return `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">${routes.map((route) => `
  <url><loc>${absolute(route)}</loc><lastmod>${UPDATED}</lastmod><changefreq>${route.startsWith("/blog/") ? "monthly" : "weekly"}</changefreq><priority>${route === "/" ? "1.0" : "0.8"}</priority></url>`).join("")}
</urlset>`;
}

function htaccess() {
  return `Options -Indexes -MultiViews
DirectoryIndex index

<FilesMatch "^[^.]+$">
  ForceType text/html
</FilesMatch>

<IfModule mod_headers.c>
  Header always set X-Content-Type-Options "nosniff"
  Header always set X-Frame-Options "DENY"
  Header always set Referrer-Policy "strict-origin-when-cross-origin"
  Header always set Permissions-Policy "camera=(), microphone=(), geolocation=()"
  Header always set Content-Security-Policy "default-src 'self'; base-uri 'self'; connect-src 'self'; font-src 'self'; form-action 'self'; frame-ancestors 'none'; img-src 'self' data:; object-src 'none'; script-src 'self'; style-src 'self'; upgrade-insecure-requests"
  <FilesMatch "^(index|recursos|planos|condo|comunidade|faq|blog|seja-testador|features|plans|community|testers|funciones|planes|comunidad|preguntas|probadores|terms|privacy|terminos|privacidad|termos|politica)$">
    Header set Cache-Control "public, max-age=300, must-revalidate, no-transform"
  </FilesMatch>
  <FilesMatch "\\.(css|js|woff2|webp|png|ico)$">
    Header set Cache-Control "public, max-age=31536000, immutable"
  </FilesMatch>
  <FilesMatch "^(termos|politica|terms|privacy|terminos|privacidad)$">
    Header set X-Robots-Tag "noindex, nofollow"
  </FilesMatch>
</IfModule>

<IfModule mod_rewrite.c>
  RewriteEngine On
  RewriteCond %{HTTPS} !=on
  RewriteRule ^ https://%{HTTP_HOST}%{REQUEST_URI} [R=301,L]
  RewriteRule ^tester-signup/?$ tester-signup.php [L]
</IfModule>
`;
}

async function build() {
  await fs.mkdir(PUBLIC, { recursive: true });
  const staleBlog = path.join(PUBLIC, "blog");
  try {
    if ((await fs.lstat(staleBlog)).isFile()) await fs.rm(staleBlog);
  } catch (error) {
    if (error.code !== "ENOENT") throw error;
  }
  await copyAssets();
  const publicRoutes = [];
  const renderers = { home: renderHome, features: renderFeatures, plans: renderPlans, condo: renderCondo, community: renderCommunity, faq: renderFaq, testers: renderTester };
  for (const localeKey of ["pt", "en", "es"]) {
    const locale = locales[localeKey];
    for (const pageKey of pageKeys) {
      await writeRoute(locale.files[pageKey], await renderers[pageKey](localeKey));
      publicRoutes.push(locale.paths[pageKey]);
    }
  }
  for (const localeKey of ["pt", "en", "es"]) {
    const locale = locales[localeKey];
    await writeRoute(locale.files.blog, renderBlog(localeKey));
    publicRoutes.push(locale.paths.blog);
    for (const [index, article] of articlesByLocale[localeKey].entries()) {
      const route = `${locale.paths.blog}${article.slug}`;
      await writeRoute(route.replace(/^\//, ""), renderArticle(localeKey, article, index));
      publicRoutes.push(route);
    }
  }
  const legalPages = [
    ["pt", "terms", "termos", "Termos de Uso - VeVolt", "Regras para usar o aplicativo VeVolt."],
    ["pt", "privacy", "politica", "Política de Privacidade - VeVolt", "Como o VeVolt trata dados e permissões."],
    ["en", "terms", "terms-en", "Terms of Use - VeVolt", "Rules for using the VeVolt application."],
    ["en", "privacy", "privacy-en", "Privacy Policy - VeVolt", "How VeVolt handles data and permissions."],
    ["es", "terms", "terminos-es", "Términos de Uso - VeVolt", "Reglas para usar la aplicación VeVolt."],
    ["es", "privacy", "privacidad-es", "Política de Privacidad - VeVolt", "Cómo VeVolt trata los datos y permisos."],
  ];
  for (const [localeKey, pageKey, sourceName, title, description] of legalPages) {
    await writeRoute(locales[localeKey].files[pageKey], await renderLegal(localeKey, pageKey, sourceName, title, description));
  }
  await fs.writeFile(path.join(PUBLIC, "robots.txt"), `User-agent: *\nAllow: /\nDisallow: /termos\nDisallow: /politica\nDisallow: /en/terms\nDisallow: /en/privacy\nDisallow: /es/terminos\nDisallow: /es/privacidad\nSitemap: ${BASE}/sitemap.xml\n`, "utf8");
  await fs.writeFile(path.join(PUBLIC, "sitemap.xml"), sitemap([...new Set(publicRoutes)]), "utf8");
  await fs.writeFile(path.join(PUBLIC, "site.webmanifest"), JSON.stringify({ name: "VeVolt", short_name: "VeVolt", description: locales.pt.home.description, lang: "pt-BR", start_url: "/", display: "standalone", background_color: "#07111f", theme_color: "#0066ff", icons: [{ src: "/assets/icon-192.png", sizes: "192x192", type: "image/png" }, { src: "/assets/icon-512.png", sizes: "512x512", type: "image/png" }] }, null, 2), "utf8");
  await fs.writeFile(path.join(PUBLIC, ".htaccess"), htaccess(), "utf8");
  console.log(`Built ${publicRoutes.length} indexed routes and ${legalPages.length} legal pages.`);
}

await build();
