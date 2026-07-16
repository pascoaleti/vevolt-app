package br.com.vevolt.data.external

import br.com.vevolt.model.MarketplaceCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenStreetMapMarketplaceRepositoryTest {
    @Test
    fun mapsSupportedBusinessCategories() {
        assertEquals(MarketplaceCategory.DEALER, marketplaceCategory("car"))
        assertEquals(MarketplaceCategory.REPAIR, marketplaceCategory("car_repair"))
        assertNull(marketplaceCategory("car_parts"))
    }

    @Test
    fun matchesConfiguredVehicleBrandIgnoringCaseAndAccents() {
        assertTrue(matchesVehicleBrand("BYD Itaim", null, "byd"))
        assertTrue(matchesVehicleBrand("Concessionaria", "Citroen", "Citroën"))
        assertFalse(matchesVehicleBrand("Volvo Vocal", "Volvo", "BYD"))
        assertFalse(matchesVehicleBrand("Auto Center", null, ""))
    }

    @Test
    fun normalizesOnlyUsableWebsites() {
        assertEquals("https://vevolt.app", normalizeMarketplaceWebsite("vevolt.app"))
        assertEquals("https://example.com/store", normalizeMarketplaceWebsite("https://example.com/store"))
        assertNull(normalizeMarketplaceWebsite("not a website"))
        assertNull(normalizeMarketplaceWebsite("null"))
    }

    @Test
    fun normalizesWhatsAppOnlyWhenAUsableNumberIsDeclared() {
        assertEquals("5511999999999", normalizeMarketplaceWhatsApp("+55 11 99999-9999", null, "BR"))
        assertEquals("5511999999999", normalizeMarketplaceWhatsApp("yes", "11 99999-9999", "BR"))
        assertNull(normalizeMarketplaceWhatsApp("yes", null, "BR"))
    }

    @Test
    fun acceptsSecurePhotosAndWikimediaFiles() {
        assertEquals(
            "https://example.com/store.jpg",
            normalizeMarketplacePhoto("https://example.com/store.jpg", null)
        )
        assertEquals(
            "https://commons.wikimedia.org/wiki/Special:Redirect/file/VeVolt%20Store.jpg",
            normalizeMarketplacePhoto(null, "File:VeVolt Store.jpg")
        )
        assertNull(normalizeMarketplacePhoto("http://example.com/store.jpg", "Category:Stores"))
    }

    @Test
    fun calculatesRealisticDistance() {
        val distance = haversineDistanceKm(-23.5505, -46.6333, -23.5614, -46.6559)
        assertEquals(2.6, distance, 0.3)
    }

    @Test
    fun acceptsOnlyStructuredElectricVehicleEvidence() {
        assertTrue(declaresElectricVehicle("electric", "", ""))
        assertTrue(declaresElectricVehicle("", "yes", ""))
        assertFalse(declaresElectricVehicle("", "", ""))
    }
}
