package de.lulebe.vakation.ui.intro

import agency.tango.materialintroscreen.SlideFragment
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.lulebe.vakation.R
import de.lulebe.vakation.data.Permissions


class IntroSlidePermissions: SlideFragment() {

    private val neededPermissions = Permissions.needed
    private val possiblePermissions: Array<String>? = null
    private val PERMISSIONS_REQUEST_CODE = 12642

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.intro, container, false)
        view.findViewById<ImageView>(R.id.image).setImageResource(R.drawable.intro3)
        view.findViewById<TextView>(R.id.title).setText(R.string.intro_slide3_title)
        view.findViewById<TextView>(R.id.description).setText(R.string.intro_slide3_description)
        return view
    }

    override fun backgroundColor() = R.color.introBG3
    override fun buttonsColor() = R.color.colorAccent
    override fun canMoveFurther() = true

    override fun hasNeededPermissionsToGrant() = hasPermissionsToGrant(neededPermissions)

    override fun hasAnyPermissionsToGrant(): Boolean {
        var hasPermissionToGrant = hasPermissionsToGrant(neededPermissions)
        if (!hasPermissionToGrant) {
            hasPermissionToGrant = hasPermissionsToGrant(possiblePermissions)
        }
        return hasPermissionToGrant
    }

    private fun hasPermissionsToGrant(permissions: Array<String>?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (SlideFragment.isNotNullOrEmpty(permission)) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun askForPermissions() {
        val notGrantedPermissions = ArrayList<String>()
        if (neededPermissions != null) {
            for (permission in neededPermissions) {
                if (SlideFragment.isNotNullOrEmpty(permission)) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        notGrantedPermissions.add(permission)
                    }
                }
            }
        }
        if (possiblePermissions != null) {
            for (permission in possiblePermissions) {
                if (SlideFragment.isNotNullOrEmpty(permission)) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        notGrantedPermissions.add(permission)
                    }
                }
            }
        }
        val permissionsToGrant = removeEmptyAndNullStrings(notGrantedPermissions)
        ActivityCompat.requestPermissions(activity, permissionsToGrant, PERMISSIONS_REQUEST_CODE)
    }

    private fun removeEmptyAndNullStrings(permissions: ArrayList<String>): Array<String> {
        val list = ArrayList(permissions)
        list.filter { SlideFragment.isNotNullOrEmpty(it) }
        return list.toTypedArray()
    }
}