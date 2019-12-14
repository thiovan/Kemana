@file:Suppress("UNCHECKED_CAST")

package com.utsman.kemana.driver

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.base.*
import com.utsman.kemana.base.view.BottomSheetUnDrag
import com.utsman.kemana.driver.fragment.MainFragment
import com.utsman.kemana.driver.fragment.bottom_sheet.MainBottomSheet
import com.utsman.kemana.driver.impl.view_state.IActiveState
import com.utsman.kemana.driver.services.LocationServices
import com.utsman.kemana.driver.subscriber.ObjectOrderSubs
import com.utsman.kemana.driver.subscriber.ReadyOrderSubs
import com.utsman.kemana.remote.driver.*
import com.utsman.kemana.remote.toJSONObject
import com.utsman.kemana.remote.toPassenger
import com.utsman.kemana.remote.toPlace
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.bottom_dialog_receiving_order.view.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.json.JSONObject

class MainActivity : RxAppCompatActivity() {

    private var driver: Driver? = null
    private lateinit var locationServices: Intent
    private lateinit var mainFragment: MainFragment

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_main)

        driver = getBundleFrom("driver")
        locationServices = Intent(this, LocationServices::class.java)
        mainFragment = MainFragment(driver)
        //mainFragment = MainFragment.withDriver(driver)

        setupPermission {
            startService(locationServices)

            composite.delay(900) {
                driver?.let {
                    Notify.send(it)
                }
            }
        }

        replaceFragment(mainFragment, R.id.main_frame)
    }



    private fun setupPermission(ready: () -> Unit) {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    ready.invoke()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    loge("permission denied")
                }

            })
            .check()
    }

    override fun onDestroy() {
        super.onDestroy()

        Handler().postDelayed({
            stopService(locationServices)
        }, 800)
    }
}