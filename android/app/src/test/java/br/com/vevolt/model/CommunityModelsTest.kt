package br.com.vevolt.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CommunityModelsTest {
    @Test
    fun communityStatusOverridesOnlyConfirmedConditions() {
        assertEquals(
            ChargerStatus.AVAILABLE,
            CommunityStatus.CONFIRMED_WORKING.toChargerStatus(ChargerStatus.UNKNOWN)
        )
        assertEquals(
            ChargerStatus.OFFLINE,
            CommunityStatus.REPORTED_BROKEN.toChargerStatus(ChargerStatus.OPERATIONAL)
        )
        assertEquals(
            ChargerStatus.BUSY,
            CommunityStatus.QUEUE_REPORTED.toChargerStatus(ChargerStatus.OPERATIONAL)
        )
        assertEquals(
            ChargerStatus.OPERATIONAL,
            CommunityStatus.UNCONFIRMED.toChargerStatus(ChargerStatus.OPERATIONAL)
        )
    }
}
