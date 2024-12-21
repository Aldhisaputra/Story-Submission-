package com.bangkit.submissionintermediet.view.map

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.submissionintermediet.R
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.ViewModelFactory
import com.bangkit.submissionintermediet.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> { ViewModelFactory.getInstance(this) }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) getMyLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {
            uiSettings.run {
                isZoomControlsEnabled = true
                isIndoorLevelPickerEnabled = true
                isCompassEnabled = true
                isMapToolbarEnabled = true
            }
        }
        getMyLocation()
        setMapStyle()
        addManyMarkers()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun addManyMarkers() {
        viewModel.getAllStoryWithLocation.observe(this) { result ->
            if (result is Results.Success) {
                result.data.forEach { story ->
                    map.addMarker(MarkerOptions().position(LatLng(story.lat ?: 0.0, story.lon ?: 0.0)).title(story.name).snippet(story.description))
                }
                result.data.randomOrNull()?.let {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.lat ?: 0.0, it.lon ?: 0.0), 1f))
                }
            } else if (result is Results.Error) {
                Log.e(TAG, "Error: ${result.error}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?) = menuInflater.inflate(R.menu.map_options, menu).run { true }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_type -> { map.mapType = GoogleMap.MAP_TYPE_NORMAL; true }
        R.id.satellite_type -> { map.mapType = GoogleMap.MAP_TYPE_SATELLITE; true }
        R.id.terrain_type -> { map.mapType = GoogleMap.MAP_TYPE_TERRAIN; true }
        R.id.hybrid_type -> { map.mapType = GoogleMap.MAP_TYPE_HYBRID; true }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
