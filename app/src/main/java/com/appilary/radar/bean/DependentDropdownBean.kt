package com.appilary.radar.bean

import com.appilary.radar.api.res.DependentDropDownData

class DependentDropdownBean (
    val time: Long = System.currentTimeMillis(),
    var dependentMap : HashMap<String, DependentDropDownData> ?= null
)