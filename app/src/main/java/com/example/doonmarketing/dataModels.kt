package com.example.doonmarketing


data class VisitModel (
    var uid: String="",
    var place:String="",
    var time:String="",
    var lng:Double=0.0,
    var lat:Double=0.0,
    var remarks:String=""
)
data class UserModel(
    var uid: String="",
    var email:String="",
    var name:String="",
    var mobile:String=""
)