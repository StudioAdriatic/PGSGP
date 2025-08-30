package com.studioadriatic.gpgs.model

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ModelClassesTest {

    @Test
    fun `AchievementInfo data class works correctly`() {
        // Test the AchievementInfo data class
        val achievementInfo = AchievementInfo(
            id = "test_id",
            name = "Test Achievement",
            description = "Test Description",
            state = 1,
            type = 0,
            currentSteps = 5,
            totalSteps = 10,
            xp = 100L
        )

        assert(achievementInfo.id == "test_id")
        assert(achievementInfo.name == "Test Achievement")
        assert(achievementInfo.description == "Test Description")
        assert(achievementInfo.state == 1)
        assert(achievementInfo.type == 0)
        assert(achievementInfo.currentSteps == 5)
        assert(achievementInfo.totalSteps == 10)
        assert(achievementInfo.xp == 100L)
    }

    @Test
    fun `PlayerInfo data class works correctly`() {
        // Test the PlayerInfo data class
        val playerInfo = PlayerInfo(
            playerId = "player123",
            displayName = "Test Player",
            name = "Test Name",
            iconImageUrl = "https://example.com/icon.jpg",
            hiResImageUrl = "https://example.com/hires.jpg",
            title = "Test Title",
            bannerImageLandscapeUrl = "https://example.com/banner_landscape.jpg",
            bannerImagePortraitUrl = "https://example.com/banner_portrait.jpg",
            levelInfo = null
        )

        assert(playerInfo.playerId == "player123")
        assert(playerInfo.displayName == "Test Player")
        assert(playerInfo.name == "Test Name")
        assert(playerInfo.iconImageUrl == "https://example.com/icon.jpg")
        assert(playerInfo.hiResImageUrl == "https://example.com/hires.jpg")
        assert(playerInfo.title == "Test Title")
    }

    @Test
    fun `PlayerStats data class works correctly`() {
        // Test the PlayerStats data class
        val playerStats = PlayerStats(
            avgSessionLength = 300.5,
            daysSinceLastPlayed = 2,
            numberOfPurchases = 5,
            numberOfSessions = 10,
            sessionPercentile = 0.75,
            spendPercentile = 0.85
        )

        assert(playerStats.avgSessionLength == 300.5)
        assert(playerStats.daysSinceLastPlayed == 2)
        assert(playerStats.numberOfPurchases == 5)
        assert(playerStats.numberOfSessions == 10)
        assert(playerStats.sessionPercentile == 0.75)
        assert(playerStats.spendPercentile == 0.85)
    }
}
