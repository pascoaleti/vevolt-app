#!/usr/bin/env bash

set -euo pipefail

archive="/tmp/vevolt-site-deploy.tar.gz"
target="/home/vevolt/public_html"
stage="/home/vevolt/public_html.deploy"
rollback="/home/vevolt/public_html.rollback"
failed="/home/vevolt/public_html.failed"

test -f "$archive"
test -d "$target"

rm -rf "$stage" "$failed"
mkdir -p "$stage"
tar -xzf "$archive" -C "$stage"

test -f "$stage/index"
test -f "$stage/planos"
test -f "$stage/en/plans"
test -f "$stage/es/planes"
test -f "$stage/condo"
test -f "$stage/faq"
test -f "$stage/en/faq"
test -f "$stage/es/preguntas"
test -f "$stage/exclusao-de-dados"
test -f "$stage/en/data-deletion"
test -f "$stage/es/eliminacion-de-datos"
test -f "$stage/.htaccess"
test ! -e "$stage/seja-testador"
test ! -e "$stage/tester-signup.php"
test ! -e "$stage/tester-config.example.php"

grep -Fq "15 dias grátis no plano mensal" "$stage/planos"
grep -Fq "15-day free trial on the monthly plan" "$stage/en/plans"
grep -Fq "15 días gratis en el plan mensual" "$stage/es/planes"
chown -R vevolt:vevolt "$stage"
find "$stage" -type d -exec chmod 0755 {} +
find "$stage" -type f -exec chmod 0644 {} +
if test -d "$target/.well-known"; then
  cp -a "$target/.well-known" "$stage/.well-known"
fi

rm -rf "$rollback"
mv "$target" "$rollback"
mv "$stage" "$target"

home_check="/tmp/vevolt-home-check.html"
plans_check="/tmp/vevolt-plans-check.html"
plans_en_check="/tmp/vevolt-plans-en-check.html"
plans_es_check="/tmp/vevolt-plans-es-check.html"

if apachectl configtest >/dev/null 2>&1 \
  && curl -kfsS --resolve vevolt.app:443:127.0.0.1 https://vevolt.app/ -o "$home_check" \
  && curl -kfsS --resolve vevolt.app:443:127.0.0.1 https://vevolt.app/planos -o "$plans_check" \
  && curl -kfsS --resolve vevolt.app:443:127.0.0.1 https://vevolt.app/en/plans -o "$plans_en_check" \
  && curl -kfsS --resolve vevolt.app:443:127.0.0.1 https://vevolt.app/es/planes -o "$plans_es_check" \
  && grep -q "VeVolt" "$home_check" \
  && grep -Fq "15 dias grátis no plano mensal" "$plans_check" \
  && grep -Fq "15-day free trial on the monthly plan" "$plans_en_check" \
  && grep -Fq "15 días gratis en el plan mensual" "$plans_es_check"; then
  rm -f "$archive" /tmp/deploy-vevolt-site.sh "$home_check" "$plans_check" "$plans_en_check" "$plans_es_check"
  printf '%s\n' "deploy-ok"
  exit 0
fi

mv "$target" "$failed"
mv "$rollback" "$target"
rm -rf "$failed" "$stage"
rm -f "$archive" /tmp/deploy-vevolt-site.sh "$home_check" "$plans_check" "$plans_en_check" "$plans_es_check"
printf '%s\n' "deploy-rolled-back" >&2
exit 1
