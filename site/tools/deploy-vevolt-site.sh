#!/usr/bin/env bash

set -euo pipefail

archive="/tmp/vevolt-site-deploy.tar.gz"
target="/home/vevolt/public_html"
stage="/home/vevolt/public_html.deploy"
previous="/home/vevolt/public_html.previous"
failed="/home/vevolt/public_html.failed"

test -f "$archive"
test -d "$target"

rm -rf "$stage" "$previous" "$failed"
mkdir -p "$stage"
tar -xzf "$archive" -C "$stage"

test -f "$stage/index"
test -f "$stage/recursos"
test -f "$stage/planos"
test -f "$stage/condo"
test -f "$stage/comunidade"
test -f "$stage/blog/index"
test -f "$stage/tester-signup.php"
test -f "$stage/.htaccess"
test -f "/home/vevolt/.tester-config.php"

php -l "$stage/tester-signup.php" >/dev/null
chown -R vevolt:vevolt "$stage"
find "$stage" -type d -exec chmod 0755 {} +
find "$stage" -type f -exec chmod 0644 {} +

mv "$target" "$previous"
mv "$stage" "$target"

home_check="/tmp/vevolt-home-check.html"
resources_check="/tmp/vevolt-resources-check.html"

if apachectl configtest >/dev/null 2>&1 \
  && curl -kfsS --resolve vevolt.app:443:127.0.0.1 https://vevolt.app/ -o "$home_check" \
  && curl -kfsS --resolve vevolt.app:443:127.0.0.1 https://vevolt.app/recursos -o "$resources_check" \
  && grep -q "VeVolt" "$home_check" \
  && grep -q "Recursos" "$resources_check"; then
  rm -rf "$previous"
  rm -f "$archive" /tmp/deploy-vevolt-site.sh "$home_check" "$resources_check"
  printf '%s\n' "deploy-ok"
  exit 0
fi

mv "$target" "$failed"
mv "$previous" "$target"
rm -rf "$failed" "$stage"
rm -f "$home_check" "$resources_check"
printf '%s\n' "deploy-rolled-back" >&2
exit 1
