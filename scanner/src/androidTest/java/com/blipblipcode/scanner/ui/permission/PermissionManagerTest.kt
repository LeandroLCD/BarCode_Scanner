package com.blipblipcode.scanner.ui.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import com.google.accompanist.permissions.PermissionState as AccompanistPermissionState
import com.google.accompanist.permissions.PermissionStatus
import org.mockito.ArgumentMatchers.anyString
import org.junit.Assert.*
import org.mockito.Mockito.mock

@OptIn(ExperimentalPermissionsApi::class)
@RunWith(AndroidJUnit4::class)
class PermissionManagerTest {

    @Mock
    private lateinit var mockActivity: Activity

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun builder_addsSinglePermission_correctly() {
        /*GIVEN*/
        val cameraPermission = "android.permission.CAMERA"
        val builder = PermissionManager.Builder().addPermission(cameraPermission)

        // Mock initial activity state for the build method
        `when`(mockActivity.checkSelfPermission(cameraPermission)).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(cameraPermission)).thenReturn(true)

        /*WHEN*/
        val permissionManager = builder.build(mockActivity)
        permissionManager.build(mockActivity) // Re-initialize as per original test logic

        /*THEN*/
        val states = permissionManager.getPermissionStates()
        assertEquals(1, states.size)
        assertEquals(cameraPermission, states[0].perm)
    }

    @Test
    fun builder_addsMultiplePermissions_correctly() {
        /*GIVEN*/
        val permission1 = "android.permission.CAMERA"
        val permission2 = "android.permission.RECORD_AUDIO"
        val builder = PermissionManager.Builder().addAllPermissions(permission1, permission2)

        // Mock initial activity state for the build method
        `when`(mockActivity.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(anyString())).thenReturn(true)

        /*WHEN*/
        val permissionManager = builder.build(mockActivity)
        permissionManager.build(mockActivity) // Re-initialize as per original test logic

        /*THEN*/
        val states = permissionManager.getPermissionStates()
        assertEquals(2, states.size)
        assertTrue(states.any { it.perm == permission1 })
        assertTrue(states.any { it.perm == permission2 })
    }

    @Test
    fun build_initializesPermissions_granted() {
        /*GIVEN*/
        val cameraPermission = "android.permission.CAMERA"
        val builder = PermissionManager.Builder().addPermission(cameraPermission)

        // Mock activity to return granted state
        `when`(mockActivity.checkSelfPermission(cameraPermission)).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(cameraPermission)).thenReturn(false)

        /*WHEN*/
        val permissionManager = builder.build(mockActivity)
        permissionManager.build(mockActivity) // Re-trigger initialization

        /*THEN*/
        val state = permissionManager.getPermissionState(cameraPermission)
        assertNotNull(state)
        assertTrue(state!!.isGranted)
        assertFalse(state.shouldShowRequestPermissionRationale)
        assertFalse(state.isPermanentlyDenied)
    }

    @Test
    fun build_initializesPermissions_denied() {
        /*GIVEN*/
        val cameraPermission = "android.permission.CAMERA"
        val builder = PermissionManager.Builder().addPermission(cameraPermission)

        // Mock activity to return denied state with rationale
        `when`(mockActivity.checkSelfPermission(cameraPermission)).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(cameraPermission)).thenReturn(true)

        /*WHEN*/
        val permissionManager = builder.build(mockActivity)

        /*THEN*/
        val state = permissionManager.getPermissionState(cameraPermission)
        assertNotNull(state)
        assertFalse(state!!.isGranted)
        assertTrue(state.shouldShowRequestPermissionRationale)
        assertFalse(state.isPermanentlyDenied)
    }

    @Test
    fun build_initializesPermissions_permanentlyDenied() {
        /*GIVEN*/
        val cameraPermission = "android.permission.CAMERA"
        val builder = PermissionManager.Builder().addPermission(cameraPermission)

        // Mock activity to return permanently denied state
        `when`(mockActivity.checkSelfPermission(cameraPermission)).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(cameraPermission)).thenReturn(false) // No rationale means permanently denied

        /*WHEN*/
        val permissionManager = builder.build(mockActivity)

        /*THEN*/
        val state = permissionManager.getPermissionState(cameraPermission)
        assertNotNull(state)
        assertFalse(state!!.isGranted)
        assertFalse(state.shouldShowRequestPermissionRationale)
        assertTrue(state.isPermanentlyDenied)
    }

    @Test
    fun updatePermissionStates_refreshesCorrectly() {
        /*GIVEN*/
        val cameraPermission = "android.permission.CAMERA"
        val builder = PermissionManager.Builder().addPermission(cameraPermission)

        // Initial state: Denied, rationale needed
        `when`(mockActivity.checkSelfPermission(cameraPermission)).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(cameraPermission)).thenReturn(true)
        val permissionManager = builder.build(mockActivity)

        assertFalse(permissionManager.getPermissionState(cameraPermission)!!.isGranted) // Verify initial state

        /*WHEN*/
        // Change mock behavior to granted
        `when`(mockActivity.checkSelfPermission(cameraPermission)).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(cameraPermission)).thenReturn(false)
        permissionManager.updatePermissionStates()

        /*THEN*/
        val state = permissionManager.getPermissionState(cameraPermission)
        assertNotNull(state)
        assertTrue(state!!.isGranted)
        assertFalse(state.shouldShowRequestPermissionRationale)
        assertFalse(state.isPermanentlyDenied)
    }

    @Test
    fun getPermissionStates_returnsAllPermissions() {
        /*GIVEN*/
        val permission1 = "android.permission.CAMERA"
        val permission2 = "android.permission.RECORD_AUDIO"
        val builder = PermissionManager.Builder().addAllPermissions(permission1, permission2)

        `when`(mockActivity.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(anyString())).thenReturn(true)
        val permissionManager = builder.build(mockActivity)

        /*WHEN*/
        val states = permissionManager.getPermissionStates()

        /*THEN*/
        assertEquals(2, states.size)
        assertTrue(states.any { it.perm == permission1 })
        assertTrue(states.any { it.perm == permission2 })
    }

    @Test
    fun getPermissionState_returnsSpecificPermission() {
        /*GIVEN*/
        val permission1 = "android.permission.CAMERA"
        val permission2 = "android.permission.RECORD_AUDIO"
        val builder = PermissionManager.Builder().addAllPermissions(permission1, permission2)

        `when`(mockActivity.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(anyString())).thenReturn(true)
        val permissionManager = builder.build(mockActivity)

        /*WHEN*/
        val state = permissionManager.getPermissionState(permission1)
        val nonExistentState = permissionManager.getPermissionState("NON_EXISTENT_PERMISSION")

        /*THEN*/
        assertNotNull(state)
        assertEquals(permission1, state!!.perm)
        assertNull(nonExistentState)
    }

    @Test
    fun rememberMultiplePermissionsState_callbackUpdatesInternalStates() {
        /*GIVEN*/
        val cameraPermission = "android.permission.CAMERA"
        val recordAudioPermission = "android.permission.RECORD_AUDIO"
        val builder = PermissionManager.Builder().addAllPermissions(cameraPermission, recordAudioPermission)

        // Initial setup for the permission manager (denied states)
        `when`(mockActivity.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(anyString())).thenReturn(true)
        val permissionManager = builder.build(mockActivity)

        // Verify initial states are denied
        assertFalse(permissionManager.getPermissionState(cameraPermission)!!.isGranted)
        assertFalse(permissionManager.getPermissionState(recordAudioPermission)!!.isGranted)

        // Mocks for AccompanistPermissionState as if returned from the Composable
        val mockCameraPermissionState = mock(AccompanistPermissionState::class.java)
        `when`(mockCameraPermissionState.permission).thenReturn(cameraPermission)
        `when`(mockCameraPermissionState.status).thenReturn(PermissionStatus.Granted)

        val mockRecordAudioPermissionState = mock(AccompanistPermissionState::class.java)
        `when`(mockRecordAudioPermissionState.permission).thenReturn(recordAudioPermission)
        `when`(mockRecordAudioPermissionState.status).thenReturn(PermissionStatus.Denied(false)) // Denied and no rationale

        val mockMapPerm = mapOf(
            cameraPermission to mockCameraPermissionState,
            recordAudioPermission to mockRecordAudioPermissionState
        )

        // Configure mockActivity to reflect the *intended end state* for updatePermissionStates()
        // Camera will be granted, Audio will be permanently denied.
        `when`(mockActivity.checkSelfPermission(cameraPermission)).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(cameraPermission)).thenReturn(false)

        `when`(mockActivity.checkSelfPermission(recordAudioPermission)).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockActivity.shouldShowRequestPermissionRationale(recordAudioPermission)).thenReturn(false)

        /*WHEN*/
        // The actual call to the lambda that `rememberMultiplePermissionsState` would invoke
        val callbackLambda: (Map<String, AccompanistPermissionState>) -> Unit = { _ ->
            permissionManager.updatePermissionStates()
        }
        callbackLambda.invoke(mockMapPerm)

        /*THEN*/
        val cameraState = permissionManager.getPermissionState(cameraPermission)
        assertNotNull(cameraState)
        assertTrue(cameraState!!.isGranted)
        assertFalse(cameraState.shouldShowRequestPermissionRationale)
        assertFalse(cameraState.isPermanentlyDenied)

        val audioState = permissionManager.getPermissionState(recordAudioPermission)
        assertNotNull(audioState)
        assertFalse(audioState!!.isGranted)
        assertFalse(audioState.shouldShowRequestPermissionRationale) // No rationale
        assertTrue(audioState.isPermanentlyDenied)
    }
}
