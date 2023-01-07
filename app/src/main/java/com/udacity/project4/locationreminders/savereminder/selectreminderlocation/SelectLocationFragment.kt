package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import com.udacity.project4.R
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.project4.base.BaseFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment() , OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var activityResultLauncherPermissions: ActivityResultLauncher<Array<String>>
    private var isUserLocationObtained = false
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
//        TODO: put a marker to location that the user selected

        activityResultLauncherPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                if (result.all { result -> result.value }) {
                    //granted
                    getUserLocation()
                } else {
                    //not granted
                    Snackbar.make(
                        requireView(),
                        R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Displays App settings screen.
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                }
            }
        onLocationSelected()

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (!isUserLocationObtained) {
            val mLocationRequest: LocationRequest = LocationRequest.create()
            mLocationRequest.interval = 2000
            mLocationRequest.fastestInterval = 2000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            val mLocationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (location != null && !isUserLocationObtained) {
                            val userLocation = LatLng(location.latitude, location.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 30f))
                            marker = map.addMarker(
                                MarkerOptions().position(userLocation).title("Current Location")
                            )
                            marker?.showInfoWindow()
                            isUserLocationObtained = true
                        }
                    }
                }
            }
            LocationServices.getFusedLocationProviderClient(requireActivity())
                .requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    requireActivity().mainLooper
                )
        }

    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //set map style
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
        )

        //setup the POI click when user click on map to add marker
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title("Location")
            )
            marker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng))
        }

        //setup the long click when user click on map to add marker
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Location")
                    .snippet(snippet)
            )
            marker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f))
        }
    }

    /** Handle Foreground Location permissions **/

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission() {
        if (checkDeviceGPS()) {
            checkPermissionsThenGetUserLocation()
        }
    }

    /**
     * Check if device enable gps or not
     */
    private fun checkDeviceGPS(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MaterialAlertDialogBuilder(requireActivity())
                .setMessage("Please enable gps to could get your location")
                .setPositiveButton(
                    "Enable GPS"
                ) { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    requireActivity().startActivity(intent)
                }
                .setCancelable(false)
                .show()
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissionsThenGetUserLocation() {
            if (foregroundLocationPermissionApproved()) {
                getUserLocation()
            } else {
                if (!foregroundLocationPermissionApproved()) {
                    requestForegroundLocationPermissions()
                }
                if (foregroundLocationPermissionApproved()) {
                    getUserLocation()
                }
            }

    }

    /** Handle Background Location permissions to work on android 10 and more **/
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestForegroundLocationPermissions() {
        when {
            foregroundLocationPermissionApproved() -> {
                return
            }
            requireActivity().shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Snackbar.make(
                    requireView(),
                    R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.settings) {
                        // Displays App settings screen.
                        requireActivity().startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
            else -> {
                activityResultLauncherPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }


    private fun foregroundLocationPermissionApproved(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}
