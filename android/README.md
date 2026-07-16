# VeVolt para Android

Aplicativo Android nativo do VeVolt, pacote `br.com.vevolt`, desenvolvido em Kotlin com Jetpack Compose.

## Requisitos

- JDK 17
- Android SDK 36
- Android Studio compatível com AGP 8.13.2
- Kotlin 2.3.20

## Configuração local

1. Copie `local.properties.example` para `local.properties`.
2. Informe o caminho do Android SDK.
3. Configure sua própria chave do Open Charge Map.
4. Mantenha `PREMIUM_SALES_ENABLED=false` em builds locais sem backend de entitlement.

A assinatura de produção e as credenciais reais não são versionadas.

## Verificação

No Windows:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

Em Linux ou macOS:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

## Arquitetura

- UI: Jetpack Compose e Material 3
- Mapas: MapLibre e OpenFreeMap
- Recarga: Open Charge Map
- QR Code: CameraX e ML Kit
- Tarefas locais: WorkManager
- Compras: Google Play Billing
- Persistência: DataStore e dados locais
- Backend: API HTTPS do VeVolt, configurada fora do código-fonte

## Segurança

Não adicione ao Git:

- `local.properties`;
- `local-signing/`;
- keystores ou certificados;
- tokens, senhas ou chaves privadas;
- APKs e AABs.
