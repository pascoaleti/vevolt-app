<?php
declare(strict_types=1);

$public = realpath(__DIR__ . '/../public');
$uri = rawurldecode(parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?: '/');
$relative = ltrim($uri, '/');
$candidate = $public . DIRECTORY_SEPARATOR . str_replace('/', DIRECTORY_SEPARATOR, $relative);

if ($uri === '/') {
    $candidate = $public . DIRECTORY_SEPARATOR . 'index';
} elseif (is_dir($candidate)) {
    $candidate .= DIRECTORY_SEPARATOR . 'index';
}

$resolved = realpath($candidate);
if ($resolved === false || !str_starts_with($resolved, $public) || !is_file($resolved)) {
    http_response_code(404);
    header('Content-Type: text/plain; charset=utf-8');
    echo 'Not found';
    return true;
}

if (pathinfo($resolved, PATHINFO_EXTENSION) === 'php') {
    require $resolved;
    return true;
}

$extension = strtolower(pathinfo($resolved, PATHINFO_EXTENSION));
$types = [
    'css' => 'text/css; charset=utf-8',
    'js' => 'text/javascript; charset=utf-8',
    'json' => 'application/json; charset=utf-8',
    'webmanifest' => 'application/manifest+json; charset=utf-8',
    'xml' => 'application/xml; charset=utf-8',
    'txt' => 'text/plain; charset=utf-8',
    'webp' => 'image/webp',
    'png' => 'image/png',
    'ico' => 'image/x-icon',
    'woff2' => 'font/woff2',
];
header('Content-Type: ' . ($types[$extension] ?? 'text/html; charset=utf-8'));
readfile($resolved);
return true;
