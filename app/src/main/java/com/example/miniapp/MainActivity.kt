package com.example.miniapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.miniapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var images = ArrayList<String>()
    private val PICK_IMAGES_CODE = 0

    var dataList : ArrayList<User> = arrayListOf()



    override fun onCreate(savedInstanceState: Bundle?) { // 앱 최초 실행 시 수행
        super.onCreate(savedInstanceState)

        // 권한 물어보기
        checkPermissions()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottom_nav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val add_photo_btn = findViewById<ImageButton>(R.id.btn_add_photo)


        getContact()
        dataList = getStringArrayPref(this,"contact")



        bottom_nav.setOnItemSelectedListener { item ->

            when(item.itemId){
                    R.id.nav_home -> {
                        bottom_nav.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_home)
                        add_photo_btn.visibility = View.INVISIBLE
                        changeFragment(HomeFragment())
                    }

                    R.id.nav_contacts -> {
                        bottom_nav.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_home)
                        add_photo_btn.visibility = View.INVISIBLE
                        changeFragment(ContactsFragment())
                    }

                    R.id.nav_photo -> {
                        bottom_nav.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_home)
                        add_photo_btn.visibility = View.VISIBLE
                        changeFragment(PhotoFragment())
                    }

                    else -> {
                        bottom_nav.itemIconTintList = ContextCompat.getColorStateList(this, R.color.color_home)
                        add_photo_btn.visibility = View.INVISIBLE

                        changeFragment(GameFragment())

                    }
                }
            true
        }
        bottom_nav.selectedItemId = R.id.nav_home


        // pick images clicking this button
        add_photo_btn.setOnClickListener {
            pickImagesIntent()
        }
    }

    private fun checkPermissions(){

        var rejectedPermissiontList = ArrayList<String>()

        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS)

        for(permisson in permissions){
            if(ContextCompat.checkSelfPermission(this, permisson) != PackageManager.PERMISSION_GRANTED){
                rejectedPermissiontList.add(permisson)
            }
        }

        if(rejectedPermissiontList.isNotEmpty()){
            val array = arrayOfNulls<String>(rejectedPermissiontList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissiontList.toArray(array), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            100 -> {
                if(grantResults.isNotEmpty()){
                    for((i, permission) in permissions.withIndex()){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.d("TAG","권한에러")
                        }
                    }
                }
            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_con, fragment).commit() //fl_con의 id를 가지는 Framelayout에 fragment 배치.
        intent.putExtra("DataList", dataList)
    }

    private fun pickImagesIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE)
//        startActivityForResult(intent, PICK_IMAGES_CODE)


    }

    fun getContact(){
        val contactList: MutableList<User> = ArrayList()
        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (contacts!!.moveToNext()){
            val name = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val number = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val obj = User(username = name, phNum = number)

            contactList.add(obj)
        }
        contacts.close()

        setStringArrayPref(this,"contact",contactList)

        Toast.makeText(this, "연락처 정보를 가져왔습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_CODE){
            if(resultCode == Activity.RESULT_OK){
                images.clear()

                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                val photoFragment = PhotoFragment()
                val bundle = Bundle()

                if(data!!.clipData != null){
                    //pick multiple images
                    val count = data.clipData!!.itemCount
                    for(i in 0 until count){
                        val imagesUri = data.clipData!!.getItemAt(i).uri
                        images!!.add(imagesUri.toString())
                    }
                    bundle.putStringArrayList("img", images)

                }
                else{
                    data?.data?.let { uri ->
                        val imageUri : Uri? = data?.data
                        if(imageUri != null){
                            images!!.add(imageUri.toString())
                        }
                    }
                    bundle.putStringArrayList("img", images)

                }

                photoFragment.arguments = bundle
                fragmentTransaction.add(R.id.fl_con, photoFragment).commit()
            }
        }
    }


}