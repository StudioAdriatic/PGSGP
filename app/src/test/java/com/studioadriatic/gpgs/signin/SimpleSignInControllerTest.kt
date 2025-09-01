package com.studioadriatic.gpgs.signin

import android.app.Activity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Use Android API level 28 for testing
class SimpleSignInControllerTest {

    @Mock
    private lateinit var signInListener: SignInListener

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `SignInController can be instantiated`() {
        // Create a real Activity using Robolectric
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        
        // Act & Assert - should not throw exception
        val signInController = SignInController(activity, signInListener)
        assert(signInController != null)
        
        // Test that isSignedIn returns false initially (before authentication)
        assert(signInController.isSignedIn() == false)
    }

    @Test
    fun `UserProfile data class works correctly`() {
        // Test the UserProfile data class
        val userProfile = UserProfile(
            displayName = "Test User",
            email = "test@example.com",
            token = "test_token",
            id = "test_id"
        )

        assert(userProfile.displayName == "Test User")
        assert(userProfile.email == "test@example.com")
        assert(userProfile.token == "test_token")
        assert(userProfile.id == "test_id")
    }

    @Test
    fun `SignInController has expected methods`() {
        // Create a real Activity using Robolectric
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        
        // Test that SignInController has the expected public methods
        val signInController = SignInController(activity, signInListener)
        
        // Verify methods exist (will throw if they don't)
        val isSignedInMethod = signInController.javaClass.getMethod("isSignedIn")
        val signInMethod = signInController.javaClass.getMethod("signIn")
        val signOutMethod = signInController.javaClass.getMethod("signOut")
        val checkAuthMethod = signInController.javaClass.getMethod("checkAuthenticationStatus")
        
        assert(isSignedInMethod != null)
        assert(signInMethod != null)
        assert(signOutMethod != null)
        assert(checkAuthMethod != null)
    }
}
