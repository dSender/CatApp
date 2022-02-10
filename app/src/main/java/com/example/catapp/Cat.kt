package com.example.catapp

import io.realm.RealmObject

open class Cat: RealmObject() {
    lateinit var text: String
    lateinit var img: String
}

