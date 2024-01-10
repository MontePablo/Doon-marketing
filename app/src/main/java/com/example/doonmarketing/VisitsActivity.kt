package com.example.doonmarketing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doonmarketing.CommonDialogs.convertMillisToTimeString
import com.example.doonmarketing.databinding.ActivityDialogAddVisitBinding
import com.example.doonmarketing.databinding.ActivityVisitsBinding
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.FirebaseDao.logout
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Locale

class VisitsActivity : AppCompatActivity(),VisitFunctionListener {
    lateinit var mBinding:ActivityVisitsBinding
     lateinit var mDialogBinding:ActivityDialogAddVisitBinding
     lateinit var mDialog:AlertDialog
     lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     var id=""
     var selectedAddressLine=""
     var selectedLng=0.0
     var selectedLat=0.0
    lateinit var adapter: VisitAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivityVisitsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        id=intent.getStringExtra("id")?:MyPreferences.fetchUser()!!.uid

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        val query: Query = FirebaseDao.visitCollection.whereEqualTo("uid",id)
        val options: FirestoreRecyclerOptions<VisitModel> = FirestoreRecyclerOptions.Builder<VisitModel>().setQuery(query, VisitModel::class.java).build()
        adapter= VisitAdapter(options,this)

        mBinding.apply {
            recyclerView.adapter=adapter
            recyclerView.layoutManager=LinearLayoutManager(this@VisitsActivity)
            addBtn.setOnClickListener {
                mDialogBinding=ActivityDialogAddVisitBinding.inflate(layoutInflater)
                mDialog=CommonDialogs.dialogBuild(this@VisitsActivity, mDialogBinding!!)
                mDialogBinding.apply {
                    backBtn.setOnClickListener { mDialog.dismiss() }
                    currentLocation()
                }
            }
            logoutBtn.setOnClickListener { logout(this@VisitsActivity);finishAffinity() }
        }
    }




    private fun currentLocation() {
        if(checkLocationPermission()){
            if(isLocationEnabled()){
                getLocationFromFusionProvider()
            }else{
                Toast.makeText(this,"Turn on Location first", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else{askLocationPermission()}
    }

    @SuppressLint("MissingPermission")
    private fun getLocationFromFusionProvider() {
        fusedLocationProviderClient.locationAvailability.addOnCompleteListener(this){
            Log.d("TAGG","locationAvaility: "+it.result.isLocationAvailable.toString())
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
            val location=it.result
            if(location==null){
                Log.d("TAGG","null location received")
                mDialogBinding.failureMarker.visibility=View.VISIBLE
                mDialogBinding.sucessMarker.visibility=View.GONE
            } else
                updateUI(location)
        }.addOnFailureListener {
            Log.d("TAGG","exeption on fusedLocationProvider: ${it.localizedMessage}")
        }
    }
    private fun isLocationEnabled():Boolean{
        val locationManger: LocationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManger.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    private fun checkLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
    }
    private fun askLocationPermission(){
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),123)
    }
    override
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d("TAGG","Granted")
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                currentLocation()
            }else{
                Log.d("TAGG","Denied")
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateUI(location: Location){
        val addressList= getAddressFromLongLat(location)?: mutableListOf()
        val addressLineList=addressList.map { it.getAddressLine(0)}
        mDialogBinding?.apply {
            sucessMarker.visibility=View.VISIBLE
            mDialogBinding.failureMarker.visibility=View.INVISIBLE

            placeSpinner.apply {
                visibility=View.VISIBLE
                adapter=ArrayAdapter(this@VisitsActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,addressLineList)
                onItemSelectedListener= object:AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedAddressLine=addressLineList[p2]
                        selectedLng=addressList[p2].longitude
                        selectedLat=addressList[p2].latitude
                        saveBtn.visibility=View.VISIBLE
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Toast.makeText(this@VisitsActivity,"please chooose one!",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            saveBtn.setOnClickListener {
                val visit=VisitModel().apply {
                    uid=MyPreferences.fetchUser()!!.uid
                    lat=selectedLat
                    lng=selectedLng
                    place=selectedAddressLine
                    remarks=mDialogBinding.remarks.text.toString()
                    time=convertMillisToTimeString(System.currentTimeMillis())
                }
                uploadVisit(visit)
            }
        }
//        Log.d("TAGG",""+location.longitude+" "+location.latitude)
//        Log.d("TAGG","${addressList[0]?.getAddressLine(0)} @@ ${addressList[0]?.getAddressLine(1)}")
    }
    private fun getAddressFromLongLat(location:Location): MutableList<Address>? {
        CoroutineScope(Dispatchers.IO).run {
            var geocoder= Geocoder(applicationContext, Locale.getDefault())
            var addressList = geocoder.getFromLocation(location.latitude,location.longitude,20)
            return addressList }
    }

    fun uploadVisit(visit:VisitModel){
        FirebaseDao.addVisit(visit)
            .addOnSuccessListener { Log.d("TAGG","gigUpload success"); Toast.makeText(this,"success",Toast.LENGTH_SHORT).show()
                mDialog.dismiss()
            }
            .addOnFailureListener { Log.d("TAGG","gigUpload failed:${it.localizedMessage}");Toast.makeText(this,"failed! retry later",Toast.LENGTH_SHORT).show()}
    }
    override fun delete(user: VisitModel) {
        TODO("Not yet implemented")
    }

    override fun edit(user: VisitModel) {
        TODO("Not yet implemented")
    }

    override fun gotoMap(model: VisitModel) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MAP_URL+model.lng+","+model.lat)))
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}
interface VisitFunctionListener{
    fun delete(model:VisitModel)
    fun edit(model:VisitModel)
    fun gotoMap(model:VisitModel)

}