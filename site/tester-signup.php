<?php

declare(strict_types=1);

header_remove('X-Powered-By');
header('Content-Type: application/json; charset=UTF-8');
header('Cache-Control: no-store, no-cache, must-revalidate, max-age=0');
header('X-Robots-Tag: noindex, nofollow');
header('X-Content-Type-Options: nosniff');
header('Referrer-Policy: no-referrer');

function respond(int $status, string $message, array $extra = []): never
{
    http_response_code($status);
    echo json_encode(
        array_merge(['message' => $message], $extra),
        JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE
    );
    exit;
}

function encoded_subject(string $subject): string
{
    if (function_exists('mb_encode_mimeheader')) {
        return mb_encode_mimeheader($subject, 'UTF-8', 'B', "\r\n");
    }

    return '=?UTF-8?B?' . base64_encode($subject) . '?=';
}

function send_text_email(
    string $to,
    string $subject,
    string $body,
    string $from,
    ?string $replyTo = null
): bool {
    $headers = [
        'MIME-Version: 1.0',
        'Content-Type: text/plain; charset=UTF-8',
        'Content-Transfer-Encoding: 8bit',
        'From: VeVolt <' . $from . '>',
        'X-Mailer: VeVolt Tester Signup',
    ];

    if ($replyTo !== null) {
        $headers[] = 'Reply-To: ' . $replyTo;
    }

    return mail(
        $to,
        encoded_subject($subject),
        wordwrap($body, 76),
        implode("\r\n", $headers),
        '-f' . $from
    );
}

function record_event(string $queuePath, array $event): bool
{
    $handle = fopen($queuePath, 'ab');
    if ($handle === false) {
        return false;
    }

    $written = false;
    if (flock($handle, LOCK_EX)) {
        $line = json_encode($event, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
        $written = $line !== false && fwrite($handle, $line . PHP_EOL) !== false;
        fflush($handle);
        flock($handle, LOCK_UN);
    }

    fclose($handle);
    @chmod($queuePath, 0600);
    return $written;
}

if (($_SERVER['REQUEST_METHOD'] ?? '') !== 'POST') {
    header('Allow: POST');
    respond(405, 'Método não permitido.');
}

$origin = $_SERVER['HTTP_ORIGIN'] ?? '';
$allowedOrigins = ['https://vevolt.app', 'https://www.vevolt.app'];
if ($origin !== '' && !in_array($origin, $allowedOrigins, true)) {
    respond(403, 'Origem não autorizada.');
}

$configPath = dirname(__DIR__) . '/.tester-config.php';
if (!is_file($configPath)) {
    respond(503, 'O cadastro está temporariamente indisponível.');
}

$config = require $configPath;
$language = (string) ($_POST['language'] ?? 'pt-BR');
if (!in_array($language, ['pt-BR', 'en', 'es'], true)) {
    $language = 'pt-BR';
}

$copy = [
    'pt-BR' => [
        'invalid' => 'Informe um e-mail válido da sua conta Google Play.',
        'consent' => 'É necessário autorizar o uso do e-mail para participar do teste.',
        'limited' => 'Muitas tentativas foram feitas. Aguarde um pouco e tente novamente.',
        'saved' => 'Solicitação recebida. Enviamos o link da Google Play para o seu e-mail; aguarde a confirmação de entrada no grupo.',
        'saved_no_mail' => 'Solicitação registrada, mas o envio automático falhou. A equipe recebeu seu endereço e entrará em contato.',
        'subject' => 'Seu acesso de teste ao VeVolt',
        'body' => "Olá!\n\nRecebemos seu pedido para testar o VeVolt.\n\nLink oficial na Google Play:\n%s\n\nO teste é fechado. Se a Play Store ainda não permitir a instalação, aguarde: seu e-mail precisa ser adicionado ao grupo de testadores. Assim que isso acontecer, você receberá um aviso e poderá usar o mesmo link acima.\n\nObrigado por ajudar a testar o VeVolt.\n\nEquipe VeVolt",
    ],
    'en' => [
        'invalid' => 'Enter a valid email from your Google Play account.',
        'consent' => 'You must authorize email use to join the test.',
        'limited' => 'Too many attempts were made. Please wait and try again.',
        'saved' => 'Request received. We sent the Google Play link to your email; wait for confirmation that you were added to the group.',
        'saved_no_mail' => 'Your request was saved, but the automatic email failed. The team received your address and will contact you.',
        'subject' => 'Your VeVolt testing access',
        'body' => "Hello!\n\nWe received your request to test VeVolt.\n\nOfficial Google Play link:\n%s\n\nThis is a closed test. If Google Play does not allow installation yet, please wait: your email must be added to the testing group. Once approved, you will be notified and can use the same link above.\n\nThank you for helping us test VeVolt.\n\nVeVolt team",
    ],
    'es' => [
        'invalid' => 'Introduce un correo válido de tu cuenta de Google Play.',
        'consent' => 'Debes autorizar el uso del correo para participar en la prueba.',
        'limited' => 'Se realizaron demasiados intentos. Espera un poco e inténtalo de nuevo.',
        'saved' => 'Solicitud recibida. Enviamos el enlace de Google Play a tu correo; espera la confirmación de entrada en el grupo.',
        'saved_no_mail' => 'La solicitud fue guardada, pero falló el correo automático. El equipo recibió tu dirección y se pondrá en contacto.',
        'subject' => 'Tu acceso de prueba a VeVolt',
        'body' => "¡Hola!\n\nRecibimos tu solicitud para probar VeVolt.\n\nEnlace oficial en Google Play:\n%s\n\nLa prueba es cerrada. Si Google Play todavía no permite la instalación, espera: tu correo debe añadirse al grupo de probadores. Cuando se apruebe, recibirás un aviso y podrás usar el mismo enlace.\n\nGracias por ayudar a probar VeVolt.\n\nEquipo VeVolt",
    ],
][$language];

if (trim((string) ($_POST['company'] ?? '')) !== '') {
    respond(200, $copy['saved']);
}

if (($_POST['consent'] ?? '') !== '1') {
    respond(422, $copy['consent']);
}

$email = strtolower(trim((string) ($_POST['email'] ?? '')));
if (strlen($email) > 254 || filter_var($email, FILTER_VALIDATE_EMAIL) === false) {
    respond(422, $copy['invalid']);
}

$dataDir = (string) ($config['data_dir'] ?? '');
if ($dataDir === '' || (!is_dir($dataDir) && !mkdir($dataDir, 0700, true))) {
    respond(503, $copy['saved_no_mail']);
}
@chmod($dataDir, 0700);

$ip = (string) ($_SERVER['REMOTE_ADDR'] ?? 'unknown');
$ipHash = hash_hmac('sha256', $ip, (string) $config['rate_limit_secret']);
$ratePath = $dataDir . '/rate-' . $ipHash . '.json';
$rateHandle = fopen($ratePath, 'c+');
if ($rateHandle === false || !flock($rateHandle, LOCK_EX)) {
    respond(503, $copy['saved_no_mail']);
}

$rawRateData = stream_get_contents($rateHandle);
$attempts = json_decode($rawRateData ?: '[]', true);
if (!is_array($attempts)) {
    $attempts = [];
}
$now = time();
$attempts = array_values(array_filter(
    $attempts,
    static fn ($timestamp): bool => is_int($timestamp) && $timestamp > $now - 3600
));

if (count($attempts) >= 5) {
    flock($rateHandle, LOCK_UN);
    fclose($rateHandle);
    respond(429, $copy['limited']);
}

$attempts[] = $now;
rewind($rateHandle);
ftruncate($rateHandle, 0);
fwrite($rateHandle, json_encode($attempts));
fflush($rateHandle);
flock($rateHandle, LOCK_UN);
fclose($rateHandle);
@chmod($ratePath, 0600);

$queuePath = $dataDir . '/pending.ndjson';
$signupRecorded = record_event($queuePath, [
    'event' => 'signup',
    'submitted_at' => gmdate('c'),
    'email' => $email,
    'language' => $language,
    'status' => 'pending',
]);

if (!$signupRecorded) {
    respond(503, $copy['saved_no_mail']);
}

$downloadUrl = (string) $config['download_url'];
$fromEmail = (string) $config['from_email'];
$adminEmail = (string) $config['admin_email'];
$userBody = sprintf($copy['body'], $downloadUrl);
$userMailSent = send_text_email($email, $copy['subject'], $userBody, $fromEmail, $adminEmail);

$adminSubject = '[VeVolt] Novo pedido de testador';
$adminBody = "Novo pedido de acesso ao teste fechado do VeVolt.\n\n"
    . "E-mail: {$email}\n"
    . "Idioma: {$language}\n"
    . 'Data UTC: ' . gmdate('c') . "\n\n"
    . "Adicione este endereço ao grupo de testadores da Google Play e depois avise o candidato.\n\n"
    . "Link enviado ao candidato:\n{$downloadUrl}\n";
$adminMailSent = send_text_email($adminEmail, $adminSubject, $adminBody, $fromEmail, $email);

record_event($queuePath, [
    'event' => 'mail_delivery',
    'recorded_at' => gmdate('c'),
    'email' => $email,
    'user_mail_sent' => $userMailSent,
    'admin_mail_sent' => $adminMailSent,
]);

respond(200, $userMailSent ? $copy['saved'] : $copy['saved_no_mail']);
