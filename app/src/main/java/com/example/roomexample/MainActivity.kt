package com.example.roomexample

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity() : AppCompatActivity(), Parcelable {

    private var catDb : CatDB? = null
    private var catList = listOf<Cat>()
    lateinit var mAdapter : CatAdapter

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        catDb = CatDB.getInstance(this)
        mAdapter = CatAdapter(this, catList)

        val r = Runnable {
            try {
                catList = catDb?.catDao()?.getAll()!!
                mAdapter = CatAdapter(this, catList)
                mAdapter.notifyDataSetChanged()

                mRecyclerView.adapter = mAdapter
                mRecyclerView.layoutManager = LinearLayoutManager(this)
                mRecyclerView.setHasFixedSize(true)
            } catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()

        mAddBtn.setOnClickListener {
            val i = Intent(this, AddActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onDestroy() {
        CatDB.destroyInstance()
        catDb = null
        super.onDestroy()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
}