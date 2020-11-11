package com.animall.assignemnt.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.animall.assignemnt.model.SMS
import com.animall.assignemnt.utils.Utility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SmsViewModel : ViewModel() {

    private var smsList: MutableLiveData<ArrayList<SMS>> = MutableLiveData()
    private var loading: MutableLiveData<Boolean> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun getSmsList(): LiveData<ArrayList<SMS>> {
        return smsList
    }

    fun getSms(context: Context, offset: Int, timeStamp: Long) {
        loading.value = true
        compositeDisposable.addAll(Observable.fromCallable {
            return@fromCallable Utility.getSms(context, offset, timeStamp)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe( {
                smsList.value = it
                loading.value = false
            }, {
                it.printStackTrace()
            }))

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}