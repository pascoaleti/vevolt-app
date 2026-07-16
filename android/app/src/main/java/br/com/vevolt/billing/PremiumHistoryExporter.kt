package br.com.vevolt.billing

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import br.com.vevolt.R
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Activity.sharePremiumChargingHistory(sessions: List<ChargingSession>) {
    runCatching {
        val exportDirectory = File(cacheDir, EXPORT_DIRECTORY).apply { mkdirs() }
        val cutoff = System.currentTimeMillis() - EXPORT_RETENTION_MILLIS
        exportDirectory.listFiles()?.filter { it.lastModified() < cutoff }?.forEach { it.delete() }
        val fileName = "vevolt-recargas-${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.csv"
        val exportFile = File(exportDirectory, fileName)
        exportFile.writeText(buildChargingHistoryCsv(sessions), Charsets.UTF_8)
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", exportFile)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newRawUri(fileName, uri)
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.export_history_chooser)))
    }.onFailure {
        Toast.makeText(this, R.string.export_failed, Toast.LENGTH_LONG).show()
    }
}

internal fun buildChargingHistoryCsv(sessions: List<ChargingSession>): String = buildString {
    append('\uFEFF')
    appendLine("started_at,ended_at,charger,connector,energy_kwh,amount")
    sessions
        .asSequence()
        .filter { it.status == ChargingSessionStatus.FINISHED }
        .sortedByDescending { it.startedAtMillis }
        .forEach { session ->
            append(
                listOf(
                    Instant.ofEpochMilli(session.startedAtMillis).toString(),
                    session.endedAtMillis?.let { Instant.ofEpochMilli(it).toString() }.orEmpty(),
                    session.chargerName,
                    session.connector.name,
                    String.format(Locale.ROOT, "%.3f", session.energyKwh),
                    String.format(Locale.ROOT, "%.2f", session.amount)
                ).joinToString(",", transform = ::safeCsvCell)
            )
            appendLine()
        }
}

private fun safeCsvCell(rawValue: String): String {
    val value = if (rawValue.firstOrNull()?.let { it in FORMULA_PREFIXES } == true) "'$rawValue" else rawValue
    val escaped = value.replace("\"", "\"\"")
    return if (escaped.any { it == ',' || it == '\"' || it == '\n' || it == '\r' }) {
        "\"$escaped\""
    } else {
        escaped
    }
}

private val FORMULA_PREFIXES = setOf('=', '+', '-', '@', '\t')
private const val EXPORT_DIRECTORY = "exports"
private const val EXPORT_RETENTION_MILLIS = 7L * 24L * 60L * 60L * 1_000L
