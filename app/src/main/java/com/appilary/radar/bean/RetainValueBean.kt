package com.appilary.radar.bean

data class RetainValueBean(
    var valaueMap: HashMap<String, String?>? = null,
    var multiValaueMap: HashMap<String, List<String>?>? = null,
    var otherValMap : HashMap<String, String?>? = null
) //QuesId_PageId, Ans / List<Ans>