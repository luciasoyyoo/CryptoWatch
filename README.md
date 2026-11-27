# CryptoWatch — Patrón Observador en Java

Aplicación de escritorio en Java/Swing para monitorizar criptomonedas mediante CoinGecko.

## Estructura

- `core`: interfaces e implementación genérica del patrón Observer.
- `model`: datos y eventos del dominio.
- `data`: acceso a CoinGecko, fuente de demostración y `PriceFeed` (Subject).
- `alerts`: observador que genera alertas y actúa a su vez como Subject.
- `ui`: observadores concretos y componentes Swing.
- `test`: comprobaciones sencillas del parser y del patrón.

## Patrón Observador

`PriceFeed` es el **Subject**. Los observadores se registran con `attach()` y reciben `MarketEvent` mediante `update()`.

Observadores principales:
- `CoinCard`: actualiza una tarjeta de cada criptomoneda.
- `DetailPanel`: actualiza el gráfico histórico.
- `StatusBar`: muestra estado de conexión.
- `LogPanel`: registra actividad.
- `RefreshButton`: refleja las actualizaciones.
- `AlertService`: detecta cambios bruscos y publica `Alert`.

`AlertService` demuestra una segunda relación Observer: es Observer de `PriceFeed` y Subject de las alertas.

## CoinGecko

Se usa `GET /api/v3/coins/markets`. Para el plan Demo se recomienda configurar la variable de entorno:

Linux/macOS:
```bash
export COINGECKO_API_KEY="TU_CLAVE"
mvn exec:java -Dexec.mainClass=cryptowatch.Main
```

Windows PowerShell:
```powershell
$env:COINGECKO_API_KEY="TU_CLAVE"
mvn exec:java -Dexec.mainClass=cryptowatch.Main
```

La aplicación también acepta `-Dcoingecko.api.key=...`.

Para probar sin red:
```bash
mvn exec:java -Dexec.mainClass=cryptowatch.Main -Dexec.args="--demo"
```

## Compilación

```bash
mvn clean package
```

## Tests sencillos

```bash
mvn exec:java -Dexec.mainClass=cryptowatch.Checks
```

Requiere Java 17 o superior.
