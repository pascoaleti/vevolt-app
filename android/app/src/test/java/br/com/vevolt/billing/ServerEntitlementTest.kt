package br.com.vevolt.billing

import java.time.Instant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerEntitlementTest {
    private val now = Instant.parse("2026-07-13T12:00:00Z")

    @Test
    fun acceptsFreshMatchingServerEntitlement() {
        val entitlement = ServerEntitlement(
            premiumActive = true,
            productId = "vevolt_premium_monthly",
            state = "SUBSCRIPTION_STATE_ACTIVE",
            expiresAt = now.plusSeconds(86_400),
            verifiedAt = now.minusSeconds(60),
            refreshAfterSeconds = 21_600,
            testPurchase = false
        )
        assertTrue(entitlement.isUsable("vevolt_premium_monthly", now))
    }

    @Test
    fun rejectsWrongProductExpiredAndStaleResponses() {
        val valid = ServerEntitlement(
            premiumActive = true,
            productId = "vevolt_premium_monthly",
            state = "SUBSCRIPTION_STATE_ACTIVE",
            expiresAt = now.plusSeconds(86_400),
            verifiedAt = now.minusSeconds(60),
            refreshAfterSeconds = 3_600,
            testPurchase = true
        )
        assertFalse(valid.isUsable("vevolt_premium_yearly", now))
        assertFalse(valid.copy(expiresAt = now.minusSeconds(1)).isUsable("vevolt_premium_monthly", now))
        assertFalse(
            valid.copy(verifiedAt = now.minusSeconds(4_000)).isUsable("vevolt_premium_monthly", now)
        )
    }
}
